package pt.up.fe.cpd.proj2.common.message;

import java.nio.ByteBuffer;

public class RoundStartMessage extends GameMessage {
    public RoundStartMessage() {
        super();
    }

    @Override
    public ByteBuffer serialize() {
        return super.serialize((Object[]) null);
    }

    @Override
    public void deserialize(String[] parts) {
        if (parts.length != 1)
            throw new IllegalArgumentException("Invalid number of arguments");
    }
}
