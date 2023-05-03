package pt.up.fe.cpd.proj2.server;

import pt.up.fe.cpd.proj2.common.Config;
import pt.up.fe.cpd.proj2.common.Input;
import pt.up.fe.cpd.proj2.common.Output;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService executor;
    private final Selector selector;

    public Server() throws IOException {
        executor = Executors.newFixedThreadPool(Config.maxGameThreads());

        selector = Selector.open();

        ServerSocketChannel.open()
                .bind(new InetSocketAddress(Config.host(), Config.port()))
                .configureBlocking(false)
                .register(selector, SelectionKey.OP_ACCEPT);
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
                    var channel = key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    var bytesRead = ((SocketChannel) channel).read(buffer);

                    if (bytesRead == -1) {
                        Output.debug("Client disconnected");
                        channel.close();
                        continue;
                    }

                    System.out.println(new String(buffer.array()));
                }
            }

            keys.clear();
        }
    }
}
