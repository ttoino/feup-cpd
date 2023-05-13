package pt.up.fe.cpd.proj2.client;

import pt.up.fe.cpd.proj2.client.state.AuthState;
import pt.up.fe.cpd.proj2.client.state.State;
import pt.up.fe.cpd.proj2.common.Config;
import pt.up.fe.cpd.proj2.common.Output;
import pt.up.fe.cpd.proj2.common.message.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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
                channel.write(message.serialize());

            var buffer = ByteBuffer.allocate(1024);
            var bytesRead = channel.read(buffer);

            if (bytesRead == -1) {
                Output.debug("Disconnected!");
                channel.close();
                continue;
            }

            buffer = buffer.slice(0, bytesRead);
            var response = Message.deserialize(buffer);
            state = state.handle(response);
        }
    }
}
