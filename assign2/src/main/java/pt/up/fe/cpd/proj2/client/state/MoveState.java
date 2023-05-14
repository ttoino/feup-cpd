package pt.up.fe.cpd.proj2.client.state;

import pt.up.fe.cpd.proj2.common.Input;
import pt.up.fe.cpd.proj2.common.message.Message;
import pt.up.fe.cpd.proj2.common.message.MoveMessage;
import pt.up.fe.cpd.proj2.game.Move;

public class MoveState extends GameState {
    public MoveState(GameInfo gameInfo) {
        super(gameInfo);
    }

    @Override
    public Message run() {
        showBoard();

        var input = Input.getFromOptions("\nDo you want to hit or stand? ", "hit", "stand");
        var move = Move.valueOf(input.toUpperCase());

        return new MoveMessage(move);
    }
}
