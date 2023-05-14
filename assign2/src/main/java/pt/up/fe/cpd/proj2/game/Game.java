package pt.up.fe.cpd.proj2.game;

import pt.up.fe.cpd.proj2.common.Config;
import pt.up.fe.cpd.proj2.common.Sockets;
import pt.up.fe.cpd.proj2.common.message.*;
import pt.up.fe.cpd.proj2.server.User;

import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.List;

public class Game {
    private final Deck deck;
    private final List<Player> players;
    private final Player dealer;

    public Game(List<User> users) {
        deck = new Deck(Config.decks());
        players = users.stream().map(Player::new).toList();
        dealer = new Player(null);
    }

    public void run() {
        broadcast(new GameStartMessage(players.stream().map(p -> p.user().info().username()).toArray(String[]::new)));

        while (players.stream().max(Comparator.comparingInt(Player::points)).get().points() < Config.maxPoints())
            round();
    }

    public void round() {
        broadcast(new RoundStartMessage());

        for (var player : players) {
            player.hand().clear();
        }

        dealer.hand().clear();

        for (int i = 0; i < 2; i++) {
            for (int p = 0; p < players.size(); p++) {
                var card = deck.draw();
                players.get(p).hand().add(card);
                broadcast(new DrawMessage(p, card));
                sleep(500);
            }
        }

        {
            var card = deck.draw();
            dealer.hand().add(card);
            broadcast(new DrawMessage(-1, card));
            sleep(500);
        }

        for (var player : players) {
            if (player.isBlackjack()) {
                player.addPoints(1);
                continue;
            }

            loop: while (!player.isBust()) {
                var move = awaitMove(player);

                switch (move) {
                    case HIT -> hit(player);
                    case STAND -> { break loop; }
                }
            }
        }

        {
            var card = deck.draw();
            dealer.hand().add(card);
            broadcast(new DrawMessage(-1, card));
            sleep(500);
        }

        while (dealer.score() < 17) {
            var card = deck.draw();
            dealer.hand().add(card);
            broadcast(new DrawMessage(-1, card));
            sleep(500);
        }

        var dealerScore = dealer.score();

        for (var player : players) {
            if (player.isBust())
                continue;

            if (dealer.isBust()) {
                player.addPoints(1);
                continue;
            }

            if (player.score() > dealerScore)
                player.addPoints(1);
        }

        sleep(2000);
    }

    public void hit(Player player) {
        var card = deck.draw();
        player.hand().add(card);
        broadcast(new DrawMessage(players.indexOf(player), card));
    }

    private void broadcast(Message message) {
        for (var player : players)
            Sockets.write(player.user().channel(), message);
    }

    private Move awaitMove(Player player) {
        Move move = null;

        try {
            Sockets.write(player.user().channel(), new MoveMessage(Move.HIT));

            var message = Sockets.read(player.user().channel());

            if (message == null) {
                return Move.STAND;
            } if (message instanceof MoveMessage) {
                move = ((MoveMessage) message).move();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return move;
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
