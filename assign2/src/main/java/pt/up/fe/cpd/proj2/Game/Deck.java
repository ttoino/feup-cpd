package pt.up.fe.cpd.proj2.Game;

import java.util.*;


public class Deck {

    private String[] suits = {"Heart", "Club", "Diamond", "Spade"};

    private String[] symbols = {"Ace", "2", "3", "5", "6", "7", "8",
                        "9", "10", "Jack", "Queen", "King"};

    private ArrayList<Card> cardDecks = new ArrayList<Card>();


    Deck() throws Exception {
        for (String suit: suits) {
            for (String symbol: symbols) {
                cardDecks.add(new Card(symbol, suit));
            }
        }
        Collections.shuffle(cardDecks);
    }


    public Card drawCard(){
        return cardDecks.remove(0);
    }
}



