package pt.up.fe.cpd.proj2.server;

import pt.up.fe.cpd.proj2.common.Config;
import pt.up.fe.cpd.proj2.common.Output;
import pt.up.fe.cpd.proj2.common.Sockets;
import pt.up.fe.cpd.proj2.common.message.*;
import pt.up.fe.cpd.proj2.game.Game;
import pt.up.fe.cpd.proj2.server.auth.FileUserInfoProvider;
import pt.up.fe.cpd.proj2.server.auth.UserInfo;
import pt.up.fe.cpd.proj2.server.auth.UserInfoProvider;
import pt.up.fe.cpd.proj2.server.queue.RankedUserQueue;
import pt.up.fe.cpd.proj2.server.queue.SimpleUserQueue;
import pt.up.fe.cpd.proj2.server.queue.UserQueue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements AutoCloseable {
    private final ExecutorService executor;
    private final Selector selector;
    private final UserInfoProvider userInfoProvider;
    private final UserQueue userQueue;
    private final Thread userQueueThread;

    public Server() throws IOException {
        executor = Executors.newFixedThreadPool(Config.maxGameThreads());

        selector = Selector.open();

        ServerSocketChannel.open()
                .bind(new InetSocketAddress(Config.host(), Config.port()))
                .configureBlocking(false)
                .register(selector, SelectionKey.OP_ACCEPT);

        userInfoProvider = switch (Config.authType()) {
            case FILE -> new FileUserInfoProvider("users.txt");
        };

        userQueue = switch (Config.queueType()) {
            case UNRANKED -> new SimpleUserQueue();
            case RANKED -> new RankedUserQueue();
        };

        userQueueThread = new Thread(this::runQueue);
    }

    public void run() throws IOException {
        userQueueThread.start();

        while (true) {
            selector.select();
            var keys = selector.selectedKeys();

            for (var key : keys) {
                if (key.isAcceptable()) {
                    Output.debug("Accepting client");
                    var channel = key.channel();

                    ((ServerSocketChannel) channel).accept()
                            .configureBlocking(false)
                            .register(selector, SelectionKey.OP_READ);
                    Output.debug("Client connected");

                } else if (key.isReadable()) {
                    Message message = Sockets.read((SocketChannel) key.channel());

                    while (message != null) {
                        handleMessage(message, (SocketChannel) key.channel(), key);
                        message = Sockets.read((SocketChannel) key.channel());
                    }

                    if (!((SocketChannel) key.channel()).isConnected())
                        Output.debug("Client disconnected");
                }
            }

            keys.clear();
        }
    }

    private void runQueue() {
        while (true) {
            var users = userQueue.nextUsers();
            executor.submit(() -> runGame(users));

            if (users == null) {
                try {
                    Thread.sleep(1000);
                    userQueue.notifyUsers();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
                continue;
            }

            Output.debug(users.toString());
        }
    }

    private void runGame(List<User> users) {
        for (var user : users) {
            try {
                user.channel().configureBlocking(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Output.debug("Starting game with " + users.size() + " players");

        var game = new Game(users);
        game.run();

        Output.debug("Game ended");

        for (var user : users) {
            try {
                user.channel().configureBlocking(false).register(selector, SelectionKey.OP_READ, user.info());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(Message message, SocketChannel channel, SelectionKey key) throws IOException {
        if (message instanceof NackMessage) {
            Output.debug("Client disconnected by request of user");
            channel.close();

        } else if (message instanceof AckMessage) {

            if (key.attachment() == null || !(key.attachment() instanceof UserInfo)) {
                Output.debug("Client not logged in");
                return;
            }

            Output.debug("Client entered queue");
            userQueue.enqueue(new User((UserInfo) key.attachment(), channel));
            Sockets.write(channel, new AckMessage());
            key.cancel();

        } else if (message instanceof AuthMessage authMessage) {
            var username = authMessage.username();
            var password = authMessage.password();

            var user = message instanceof LoginMessage
                    ? userInfoProvider.login(username, password)
                    : userInfoProvider.register(username, password);
            key.attach(user);

            if (user != null) {
                Sockets.write(channel, new AckMessage());
                Output.debug("User " + username + " logged in");
            } else {
                Sockets.write(channel, new NackMessage());
                Output.debug("User " + username + " failed to log in");
            }

        } else {
            Output.debug("Unknown message type");
        }
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
        selector.close();
        userQueueThread.interrupt();

        if (userInfoProvider instanceof AutoCloseable autoCloseable)
            autoCloseable.close();
    }
}
