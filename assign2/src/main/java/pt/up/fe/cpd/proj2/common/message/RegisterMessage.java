package pt.up.fe.cpd.proj2.common.message;

public class RegisterMessage extends AuthMessage {
    public RegisterMessage(String username, String password) {
        super(username, password);
    }

    public RegisterMessage() {
        super();
    }
}
