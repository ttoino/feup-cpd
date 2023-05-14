package pt.up.fe.cpd.proj2.client.state;

import pt.up.fe.cpd.proj2.common.Output;
import pt.up.fe.cpd.proj2.common.message.DrawMessage;
import pt.up.fe.cpd.proj2.common.message.Message;
import pt.up.fe.cpd.proj2.common.message.MoveMessage;
import pt.up.fe.cpd.proj2.common.message.RoundStartMessage;
import pt.up.fe.cpd.proj2.game.Card;

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
        Output.clear();
        System.out.println("Dealer: " + displayHand(gameInfo.dealer.hand()));

        for (var player : gameInfo.players) {
            System.out.println(player.name() + ": " + displayHand(player.hand()));
        }

        return null;
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

    protected String displayHand(List<Card> hand) {
        return hand.stream().map(Card::display).collect(Collectors.joining(" "));
    }

    protected record GameInfo(PlayerInfo dealer, List<PlayerInfo> players) {
    }

    protected static final class PlayerInfo {
        private final String name;
        private final List<Card> hand;
        private int points;

        public PlayerInfo(String name) {
            this.name = name;
            this.hand = new ArrayList<>();
            this.points = 0;
        }

        public String name() {
            return name;
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
    }
}
