package pt.up.fe.cpd.proj2.common.message;

public class LoginMessage extends AuthMessage {
    public LoginMessage(String username, String password) {
        super(username, password);
    }

    public LoginMessage() {
        super();
    }
}
