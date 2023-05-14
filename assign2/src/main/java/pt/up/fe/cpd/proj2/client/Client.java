package pt.up.fe.cpd.proj2.client;

import pt.up.fe.cpd.proj2.client.state.AuthState;
import pt.up.fe.cpd.proj2.client.state.State;
import pt.up.fe.cpd.proj2.common.Config;
import pt.up.fe.cpd.proj2.common.Output;
import pt.up.fe.cpd.proj2.common.Sockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 */
public class Client {
    private final SocketChannel channel;
    private State state;

    public Client() throws IOException {
        channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(Config.host(), Config.port()));

        state = new AuthState();

        Output.debug("Connected to server");
    }

    public void run() throws IOException {
        while (channel.isConnected()) {
            var message = state.run();
            if (message != null)
                Sockets.write(channel, message);

            var response = Sockets.read(channel);
            state = state.handle(response);
        }
    }
}
