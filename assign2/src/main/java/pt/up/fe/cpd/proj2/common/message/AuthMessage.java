package pt.up.fe.cpd.proj2.common.message;

import java.nio.ByteBuffer;

public abstract class AuthMessage extends Message {
    private String username;
    private String password;

    public AuthMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AuthMessage() {
        this("", "");
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    @Override
    public ByteBuffer serialize() {
        return super.serialize(username, password);
    }

    @Override
    public void deserialize(String[] parts) {
        if (parts.length != 3)
            throw new InvalidMessageException("Invalid number of arguments");

        username = parts[1];
        password = parts[2];
    }
}
