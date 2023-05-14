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

        for (var player : players)
            player.hand().clear();
        dealer.hand().clear();

        for (int i = 0; i < 2; i++)
            for (var player : players)
                hit(player);

        hit(dealer);

        for (var player : players) {
            if (player.hand().score() == 21)
                player.addPoints(1);

            loop: while (player.hand().score() < 21) {
                var move = awaitMove(player);

                switch (move) {
                    case HIT -> hit(player);
                    case STAND -> { break loop; }
                }
            }
        }

        hit(dealer);

        while (dealer.hand().score() < 17)
            hit(dealer);

        var dealerScore = dealer.hand().score();

        for (var player : players) {
            if (player.hand().score() > 21)
                continue;

            if (dealerScore > 21 || player.hand().score() > dealerScore)
                player.addPoints(1);
        }

        sleep(2000);
    }

    public void hit(Player player) {
        var card = deck.draw();
        player.hand().add(card);
        broadcast(new DrawMessage(players.indexOf(player), card));
        sleep(500);
    }

    private void broadcast(Message message) {
        for (var player : players)
            Sockets.write(player.user().channel(), message);
    }

    private Move awaitMove(Player player) {
        Sockets.write(player.user().channel(), new MoveMessage(Move.HIT));

        Move move = null;

        while (move == null) {
            var message = Sockets.read(player.user().channel());

            if (message == null)
                move = Move.STAND;
            else if (message instanceof MoveMessage moveMessage)
                move = moveMessage.move();
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
