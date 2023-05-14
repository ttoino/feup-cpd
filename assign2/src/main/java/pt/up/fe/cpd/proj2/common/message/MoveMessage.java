package pt.up.fe.cpd.proj2.common.message;

import pt.up.fe.cpd.proj2.game.Move;

import java.nio.ByteBuffer;

public class MoveMessage extends GameMessage {
    private Move move;

    public MoveMessage() {
        super();
    }

    public MoveMessage(Move move) {
        super();
        this.move = move;
    }

    public Move move() {
        return move;
    }

    @Override
    public void deserialize(String[] parts) {
        if (parts.length != 2)
            throw new InvalidMessageException("Invalid number of arguments");

        move = Move.values()[Integer.parseInt(parts[1])];
    }

    @Override
    public ByteBuffer serialize() {
        return super.serialize(move.ordinal());
    }
}
