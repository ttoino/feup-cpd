package pt.up.fe.cpd.proj2.client.state;

import pt.up.fe.cpd.proj2.common.Output;
import pt.up.fe.cpd.proj2.common.message.DrawMessage;
import pt.up.fe.cpd.proj2.common.message.Message;
import pt.up.fe.cpd.proj2.common.message.MoveMessage;
import pt.up.fe.cpd.proj2.common.message.RoundStartMessage;
import pt.up.fe.cpd.proj2.game.Card;
import pt.up.fe.cpd.proj2.game.Hand;

import java.util.ArrayList;
import java.util.Arrays;
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
        System.out.println("┌" + "─".repeat(width) + "┐");
        System.out.println("│" + Output.centered("Dealer", width) + "│");
        System.out.println("│" + Output.centered(showHand(gameInfo.dealer.hand()), width) + "│");
        System.out.println("│" + Output.centered(showScore(gameInfo.dealer.hand().score()), width) + "│");

        System.out.println("│" + " ".repeat(width) + "│");

        System.out.println("│" + Output.centered(gameInfo.players.stream().map(p -> Output.centered(p.name, 11)).collect(Collectors.joining("    ")), width) + "│");
        System.out.println("│" + Output.centered(gameInfo.players.stream().map(p -> showHand(p.hand)).collect(Collectors.joining("    ")), width) + "│");
        System.out.println("│" + Output.centered(gameInfo.players.stream().map(p -> showScore(p.hand.score())).collect(Collectors.joining("    ")), width) + "│");

        System.out.println("└" + "─".repeat(width) + "┘");
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
