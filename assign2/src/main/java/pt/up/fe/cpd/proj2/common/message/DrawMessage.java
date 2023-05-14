package pt.up.fe.cpd.proj2.common.message;

import pt.up.fe.cpd.proj2.game.Card;

import java.nio.ByteBuffer;

public class DrawMessage extends GameMessage {
    private int player;
    private Card card;

    public DrawMessage() {
        super();
    }

    public DrawMessage(int player, Card card) {
        this.player = player;
        this.card = card;
    }

    public int player() {
        return player;
    }

    public Card card() {
        return card;
    }

    @Override
    public ByteBuffer serialize() {
        return super.serialize(player, card.symbol().ordinal(), card.suit().ordinal());
    }

    @Override
    public void deserialize(String[] parts) {
        if (parts.length != 4)
            throw new InvalidMessageException("Invalid number of arguments");

        player = Integer.parseInt(parts[1]);
        card = new Card(Card.Symbol.values()[Integer.parseInt(parts[2])], Card.Suit.values()[Integer.parseInt(parts[3])]);
    }
}
