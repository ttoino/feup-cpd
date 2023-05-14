package pt.up.fe.cpd.proj2.game;

import pt.up.fe.cpd.proj2.server.User;

public class Player {
    private final User user;
    private final Hand hand;
    private int points;

    public Player(User user) {
        this.user = user;
        this.hand = new Hand();
        this.points = 0;
    }

    public User user() {
        return user;
    }

    public Hand hand() {
        return hand;
    }

    public int points() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }
}
