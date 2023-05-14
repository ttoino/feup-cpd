package pt.up.fe.cpd.proj2.game;

import pt.up.fe.cpd.proj2.server.User;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final User user;
    private final List<Card> hand;
    private int points;

    public Player(User user) {
        this.user = user;
        this.hand = new ArrayList<>();
        this.points = 0;
    }

    public User user() {
        return user;
    }

    public List<Card> hand() {
        return hand;
    }

    public int points() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public int score() {
        int score = 0;
        int aces = 0;

        for (var card : hand) {
            if (card.symbol() == Card.Symbol.ACE) {
                aces++;
                continue;
            }

            score += card.symbol().value();
        }

        for (int i = 0; i < aces; i++) {
            if (score + 11 > 21)
                score += 1;
            else
                score += 11;
        }

        return score;
    }

    public boolean isBust() {
        return score() > 21;
    }

    public boolean isBlackjack() {
        return score() == 21;
    }
}
