package pt.up.fe.cpd.proj2.client.state;

import pt.up.fe.cpd.proj2.common.Input;
import pt.up.fe.cpd.proj2.common.message.*;

public class AuthState implements State {
    @Override
    public Message run() {
        var action = Input.getFromOptions("Do you want to register or login? ", "register", "login");

        var username = Input.get("Username: ");
        var password = Input.getObfuscated("Password: ");

        if (action.equals("register"))
            return new RegisterMessage(username, password);
        else
            return new LoginMessage(username, password);
    }

    @Override
    public State handle(Message message) {
        if (message instanceof AckMessage) {
            System.out.println("Successfully authenticated!");
            return new MainState();
        }

        if (message instanceof NackMessage)
            System.out.println("Authentication failed!");

        return this;
    }
}
