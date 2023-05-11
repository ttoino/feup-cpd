package pt.up.fe.cpd.proj2.common.message;

import java.nio.ByteBuffer;

public class QueueStatusMessage extends Message {
    private int queuePosition;
    private int queueSize;
    private int queueTime;

    public QueueStatusMessage(int queuePosition, int queueSize, int queueTime) {
        this.queuePosition = queuePosition;
        this.queueSize = queueSize;
        this.queueTime = queueTime;
    }

    public QueueStatusMessage() {
        this(0, 0, 0);
    }

    public int queuePosition() {
        return queuePosition;
    }

    public int queueSize() {
        return queueSize;
    }

    public int queueTime() {
        return queueTime;
    }

    @Override
    public ByteBuffer serialize() {
        return super.serialize(queuePosition, queueSize, queueTime);
    }

    @Override
    public void deserialize(String[] parts) {
        if (parts.length != 4)
            throw new InvalidMessageException("Invalid number of arguments");

        queuePosition = Integer.parseInt(parts[1]);
        queueSize = Integer.parseInt(parts[2]);
        queueTime = Integer.parseInt(parts[3]);
    }
}
