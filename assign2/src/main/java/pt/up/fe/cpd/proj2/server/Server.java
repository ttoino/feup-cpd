package pt.up.fe.cpd.proj2.server;

import pt.up.fe.cpd.proj2.common.Config;
import pt.up.fe.cpd.proj2.common.Output;
import pt.up.fe.cpd.proj2.common.message.*;
import pt.up.fe.cpd.proj2.server.auth.FileUserInfoProvider;
import pt.up.fe.cpd.proj2.server.auth.UserInfoProvider;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements AutoCloseable {
    private final ExecutorService executor;
    private final Selector selector;
    private final UserInfoProvider userInfoProvider;

    public Server() throws IOException {
        executor = Executors.newFixedThreadPool(Config.maxGameThreads());

        selector = Selector.open();

        ServerSocketChannel.open()
                .bind(new InetSocketAddress(Config.host(), Config.port()))
                .configureBlocking(false)
                .register(selector, SelectionKey.OP_ACCEPT);

        switch (Config.authType()) {
            case FILE -> userInfoProvider = new FileUserInfoProvider("users.txt");
            default -> throw new RuntimeException("Invalid auth type");
        }
    }

    public void run() throws IOException {
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
                    var channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    var bytesRead = channel.read(buffer);

                    if (bytesRead == -1) {
                        Output.debug("Client disconnected");
                        channel.close();
                        continue;
                    }

                    buffer = buffer.slice(0, bytesRead);

                    Output.debug("Received message");

                    var message = Message.deserialize(buffer);
                    handleMessage(message, channel, key);
                }
            }

            keys.clear();
        }
    }

    private void handleMessage(Message message, SocketChannel channel, SelectionKey key) throws IOException {
        if (message instanceof NackMessage) {
            Output.debug("Client disconnected by request of user");
            channel.close();

        } else if (message instanceof AckMessage) {
            Output.debug("Client entered queue");
            // TODO

        } else if (message instanceof AuthMessage authMessage) {
            var username = authMessage.username();
            var password = authMessage.password();

            var user = message instanceof LoginMessage
                    ? userInfoProvider.login(username, password)
                    : userInfoProvider.register(username, password);
            key.attach(user);

            if (user != null) {
                channel.write(new AckMessage().serialize());
                Output.debug("User " + username + " logged in");
            } else {
                channel.write(new NackMessage().serialize());
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

        if (userInfoProvider instanceof AutoCloseable autoCloseable)
            autoCloseable.close();
    }
}
