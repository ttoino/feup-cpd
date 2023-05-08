package pt.up.fe.cpd.proj2.client;

import pt.up.fe.cpd.proj2.common.Config;
import pt.up.fe.cpd.proj2.common.Input;
import pt.up.fe.cpd.proj2.common.Output;
import pt.up.fe.cpd.proj2.common.message.LoginMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client {
    private final SocketChannel channel;

    public Client() throws IOException {
        channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(Config.host(), Config.port()));

        Output.debug("Connected to server");
    }

    public void run() throws IOException {
        while (channel.isConnected()) {
            var username = Input.get("Username: ");
            var password = Input.get("Password: ");
            var message = new LoginMessage(username, password);
            channel.write(message.serialize());
        }
    }
}
