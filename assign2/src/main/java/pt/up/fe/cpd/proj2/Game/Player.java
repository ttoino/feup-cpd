package pt.up.fe.cpd.proj2.Game;

import java.util.ArrayList;

public class Player {

    ArrayList<Card> faceUpCards = new ArrayList<>();

    ArrayList<Card> faceDownCards = new ArrayList<>();

    private int points = 0;

    public Player(boolean isDealer, Deck deck){

        drawnUpCard(deck);

        if (!isDealer){
            drawnUpCard(deck);
        }
        else {
            drawCard(deck);
        }
    }


    public void drawCard(Deck deck){
        faceDownCards.add(deck.drawCard());
        points += faceDownCards.get(faceDownCards.size()-1).getValue();
    }

    private void drawnUpCard(Deck deck){
        faceUpCards.add(deck.drawCard());
        points += faceUpCards.get(faceUpCards.size()-1).getValue();
    }
}
