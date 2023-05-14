package pt.up.fe.cpd.proj2.game;

import java.util.*;

public class Deck {
    private final List<Card> decks = new ArrayList<>();

    public Deck(int nDecks) {
        for (int i = 0; i < nDecks; i++)
            for (var suit : Card.Suit.values())
                for (var symbol : Card.Symbol.values())
                    decks.add(new Card(symbol, suit));

        Collections.shuffle(decks);
    }

    public Card draw() {
        return decks.remove(decks.size() - 1);
    }
}
