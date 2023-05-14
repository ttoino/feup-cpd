package pt.up.fe.cpd.proj2.common.message;

import java.nio.ByteBuffer;

public class GameStartMessage extends GameMessage {
    private String[] players;

    public GameStartMessage() {
        super();
        this.players = null;
    }

    public GameStartMessage(String[] players) {
        super();
        this.players = players;
    }

    public String[] players() {
        return players;
    }

    @Override
    public ByteBuffer serialize() {
        var parts = new Object[players.length + 1];
        parts[0] = players.length;
        System.arraycopy(players, 0, parts, 1, players.length);
        return super.serialize(parts);
    }

    @Override
    public void deserialize(String[] parts) {
        if (parts.length < 2)
            throw new InvalidMessageException("Invalid number of arguments");

        var nPlayers = Integer.parseInt(parts[1]);

        if (parts.length != nPlayers + 2)
            throw new InvalidMessageException("Invalid number of arguments");

        players = new String[nPlayers];
        System.arraycopy(parts, 2, players, 0, nPlayers);
    }
}
