package pt.up.fe.cpd.proj2.Game;

import java.util.ArrayList;

public class Player {

    ArrayList<Card> faceUpCards = new ArrayList<>();

    ArrayList<Card> faceDownCards = new ArrayList<>();

    private int points = 0;

    int hasAce = 0;

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
        updatePoints(faceDownCards.get(faceDownCards.size()-1));

    }

    private void drawnUpCard(Deck deck){
        faceUpCards.add(deck.drawCard());
        updatePoints(faceUpCards.get(faceUpCards.size()-1));
    }


    private void updatePoints(Card card){
        points += card.getValue();
        if (card.getSymbol().equals("Ace")){
            hasAce++;
        }
        if (hasAce > 0 && points > 21){
            points -= 10;
            hasAce --;
        }
    }
}
