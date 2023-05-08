package pt.up.fe.cpd.proj2.common.message;

import java.nio.ByteBuffer;

public class NackMessage extends Message {
    public NackMessage() {
        super();
    }

    @Override
    public void deserialize(String[] parts) {}

    @Override
    public ByteBuffer serialize() {
        return serialize((Object[]) null);
    }
}
