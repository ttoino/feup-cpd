package pt.up.fe.cpd.proj2.Game;

import java.util.ArrayList;
import java.util.Collections;


public class Deck {

    private final String[] suits = {"Heart", "Club", "Diamond", "Spade"};

    private final String[] symbols = {"Ace", "2", "3", "5", "6", "7", "8",
                        "9", "10", "Jack", "Queen", "King"};

    private ArrayList<Card> cardDecks = new ArrayList<>();


    public Deck(int nDecks) throws Exception {
        for (int i = 0; i < nDecks; i++) {
            for (String suit: suits) {
                for (String symbol: symbols) {
                    cardDecks.add(new Card(symbol, suit));
                }
            }
        }

        Collections.shuffle(cardDecks);
    }


    public Card drawCard(){
        return cardDecks.remove(0);
    }
}



