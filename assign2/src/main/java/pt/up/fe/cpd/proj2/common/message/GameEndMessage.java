package pt.up.fe.cpd.proj2.common.message;

import java.nio.ByteBuffer;

public class GameEndMessage extends GameMessage {
    private int[] points;

    public GameEndMessage(int[] points) {
        super();
        this.points = points;
    }

    public GameEndMessage() {
        super();
        this.points = null;
    }

    public int[] points() {
        return points;
    }

    @Override
    public ByteBuffer serialize() {
        var parts = new Object[points.length + 1];
        parts[0] = points.length;
        for (int i = 0; i < points.length; i++)
            parts[i + 1] = String.valueOf(points[i]);
        return super.serialize(parts);
    }

    @Override
    public void deserialize(String[] parts) {
        if (parts.length < 2)
            throw new InvalidMessageException("Invalid number of arguments");

        var nPoints = Integer.parseInt(parts[1]);

        if (parts.length != nPoints + 2)
            throw new InvalidMessageException("Invalid number of arguments");

        points = new int[nPoints];
        for (int i = 0; i < nPoints; i++)
            points[i] = Integer.parseInt(parts[i + 2]);
    }
}
