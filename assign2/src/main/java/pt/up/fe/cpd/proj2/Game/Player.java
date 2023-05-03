package pt.up.fe.cpd.proj2.Game;

import java.util.ArrayList;

public class Player {

    ArrayList<Card> faceUpCards = new ArrayList<>();

    ArrayList<Card> faceDownCards = new ArrayList<>();

    private int points = 0;

    private int hasAce = 0;

    private final int playerID;


    public Player(boolean isDealer, Deck deck, int playerID){

        drawnUpCard(deck);

        this.playerID = playerID;

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

    public int getPoints() {
        return points;
    }

    public void printPublicCards(){
        for (Card card: faceUpCards) {
            System.out.println(card);
        }
    }

    public void printAllCards(){
        for (Card card: faceUpCards) {
            System.out.println(card);
        }
        for (Card card: faceDownCards) {
            System.out.println(card);
        }
    }

    public int getPlayerID() {
        return playerID;
    }
}
