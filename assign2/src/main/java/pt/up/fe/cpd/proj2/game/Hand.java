package pt.up.fe.cpd.proj2.game;

import java.util.ArrayList;

public class Hand extends ArrayList<Card> {
    public int score() {
        var score = 0;
        var aces = 0;

        for (var card : this)
            if (card.symbol() == Card.Symbol.ACE)
                aces++;
            else
                score += card.symbol().value();

        for (var i = 0; i < aces; i++)
            if (score + 11 <= 21)
                score += 11;
            else
                score += 1;

        return score;
    }
}
