package pt.up.fe.cpd.proj2.server;

import pt.up.fe.cpd.proj2.common.Config;
import pt.up.fe.cpd.proj2.common.Elo;
import pt.up.fe.cpd.proj2.common.Output;
import pt.up.fe.cpd.proj2.common.Sockets;
import pt.up.fe.cpd.proj2.common.message.*;
import pt.up.fe.cpd.proj2.game.Game;
import pt.up.fe.cpd.proj2.game.Player;
import pt.up.fe.cpd.proj2.server.auth.FileUserInfoProvider;
import pt.up.fe.cpd.proj2.server.auth.UserInfo;
import pt.up.fe.cpd.proj2.server.auth.UserInfoProvider;
import pt.up.fe.cpd.proj2.server.queue.RankedUserQueue;
import pt.up.fe.cpd.proj2.server.queue.SimpleUserQueue;
import pt.up.fe.cpd.proj2.server.queue.UserQueue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
            case FILE -> new FileUserInfoProvider();
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
                    var serverChannel = key.channel();

                    var channel = ((ServerSocketChannel) serverChannel).accept();
                    channel.configureBlocking(false).register(selector, SelectionKey.OP_READ);
                    Output.debug(channel, "Connected");

                } else if (key.isReadable()) {
                    var channel = (SocketChannel) key.channel();

                    Message message = Sockets.read(channel);

                    while (message != null) {
                        handleMessage(message, channel, key);
                        message = Sockets.read(channel);
                    }

                    if (!channel.isConnected())
                        Output.debug(channel, "Disconnected");
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void runGame(List<User> users) {
        for (var user : users) {
            try {
                user.channel().configureBlocking(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Output.debug("Starting game with " + users.size() + " players: " + users.stream().map(u -> u.info().username()).collect(Collectors.joining(", ")));

        var game = new Game(users);
        var standings = game.run();

        var size = standings.stream().map(Player::points).collect(Collectors.toSet()).size();
        var place = 0;
        var lastPoints = -1;
        users = new ArrayList<>();

        for (var player : standings) {
            if (player.points() != lastPoints)
                place++;

            lastPoints = player.points();

            Output.debug(player.user().channel(), "Finished in " + place + "th place with " + player.points() + " points");

            var score = Elo.actualScore(place, size);
            var expected = Elo.expectedScore(player.user().info().elo(), standings.stream().filter(p -> p.user() != player.user()).mapToDouble(p -> p.user().info().elo()).toArray());
            var newElo = Elo.updatedRating(player.user().info().elo(), expected, score);

            Output.debug(player.user().channel(), "Elo: " + player.user().info().elo() + " -> " + newElo);

            var newUser = new UserInfo(player.user().info().id(), player.user().info().username(), player.user().info().password(), newElo);
            userInfoProvider.update(newUser);

            if (player.user().channel().isOpen())
                users.add(new User(newUser, player.user().channel()));
        }

        Output.debug("Game ended");

        for (var user : users) {
            try {
                user.channel().configureBlocking(false).register(selector, SelectionKey.OP_READ, user.info());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        selector.wakeup();
    }

    private void handleMessage(Message message, SocketChannel channel, SelectionKey key) throws IOException {
        if (message instanceof NackMessage) {
            Output.debug(channel, "Disconnected by request of user");
            channel.close();

        } else if (message instanceof AckMessage) {

            if (key.attachment() == null || !(key.attachment() instanceof UserInfo)) {
                Output.debug(channel, "Tried to enter queue without being authed");
                return;
            }

            Output.debug(channel, "Entered queue");
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
                Output.debug(channel, "Logged in as '" + username + "'");
            } else {
                Sockets.write(channel, new NackMessage());
                Output.debug(channel, "Failed to login as '" + username + "'");
            }

        } else {
            Output.debug(channel, "Sent invalid message");
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
