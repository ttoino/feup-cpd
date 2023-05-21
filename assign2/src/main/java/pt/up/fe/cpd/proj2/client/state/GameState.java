package pt.up.fe.cpd.proj2.client.state;

import pt.up.fe.cpd.proj2.common.Input;
import pt.up.fe.cpd.proj2.common.Output;
import pt.up.fe.cpd.proj2.common.message.*;
import pt.up.fe.cpd.proj2.game.Card;
import pt.up.fe.cpd.proj2.game.Hand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GameState implements State {
    protected final GameInfo gameInfo;

    public GameState(String[] players) {
        this.gameInfo = new GameInfo(new PlayerInfo("Dealer"), Arrays.stream(players).map(PlayerInfo::new).toList());
    }

    public GameState(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    @Override
    public Message run() {
        showBoard();
        return null;
    }

    protected void showBoard() {
        var width = gameInfo.players.size() * 15;

        Output.clear();
        System.out.println("\u256D" + "\u2500".repeat(width) + "\u256E");
        System.out.println("\u2502" + Output.centered("Dealer", width) + "\u2502");
        System.out.println("\u2502" + Output.centered(showHand(gameInfo.dealer.hand()), width) + "\u2502");
        System.out.println("\u2502" + Output.centered(showScore(gameInfo.dealer.hand().score()), width) + "\u2502");

        System.out.println("\u2502" + " ".repeat(width) + "\u2502");

        System.out.println("\u2502" + Output.centered(gameInfo.players.stream().map(p -> Output.centered(p.name, 11)).collect(Collectors.joining("    ")), width) + "\u2502");
        System.out.println("\u2502" + Output.centered(gameInfo.players.stream().map(p -> showHand(p.hand)).collect(Collectors.joining("    ")), width) + "\u2502");
        System.out.println("\u2502" + Output.centered(gameInfo.players.stream().map(p -> showScore(p.hand.score())).collect(Collectors.joining("    ")), width) + "\u2502");

        System.out.println("\u2570" + "\u2500".repeat(width) + "\u256F");
    }

    protected String showHand(Hand hand) {
        return Output.centered(hand.stream().map(Card::display).collect(Collectors.joining(hand.size() > 6 ? "" : " ")), 11);
    }

    protected String showScore(int score) {
        var s = String.valueOf(score);

        if (score == 21)
            s = "Blackjack";
        else if (score > 21)
            s = "Bust!";

        return Output.centered(s, 11);
    }

    protected void showScoreboard() {
        Output.clear();

        var players = gameInfo.players.stream().sorted(Comparator.comparingInt(PlayerInfo::points).reversed()).toList();

        System.out.println("Scoreboard\n");

        for (var player : players)
            System.out.println(player.name() + ": " + player.points());

        System.out.println();
    }

    @Override
    public State handle(Message message) {
        if (message instanceof DrawMessage drawMessage) {
            if (drawMessage.player() < 0 || drawMessage.player() >= gameInfo.players.size()) {
                gameInfo.dealer.hand().add(drawMessage.card());
            } else {
                gameInfo.players.get(drawMessage.player()).hand().add(drawMessage.card());
            }
        } else if (message instanceof RoundStartMessage) {
            gameInfo.dealer.hand().clear();
            for (var player : gameInfo.players)
                player.hand().clear();
        } else if (message instanceof MoveMessage) {
            return new MoveState(gameInfo);
        } else if (message instanceof GameEndMessage gameEndMessage) {
            for (int i = 0; i < gameEndMessage.points().length; ++i)
                gameInfo.players.get(i).addPoints(gameEndMessage.points()[i]);

            showScoreboard();

            return new MainState();
        }

        return new GameState(gameInfo);
    }

    protected record GameInfo(PlayerInfo dealer, List<PlayerInfo> players) {
    }

    protected static final class PlayerInfo {
        private final String name;
        private final Hand hand;
        private int points;

        public PlayerInfo(String name) {
            this.name = name;
            this.hand = new Hand();
            this.points = 0;
        }

        public String name() {
            return name;
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
}
