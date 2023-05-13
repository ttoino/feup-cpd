package pt.up.fe.cpd.proj2.client.state;

import pt.up.fe.cpd.proj2.common.Input;
import pt.up.fe.cpd.proj2.common.message.AckMessage;
import pt.up.fe.cpd.proj2.common.message.Message;
import pt.up.fe.cpd.proj2.common.message.NackMessage;

/**
 * Represents the initial state in the client state machine, after the user has been authenticated.
 *
 * @see pt.up.fe.cpd.proj2.client.Client
 */
public class MainState implements State {
    @Override
    public Message run() {
        var input =  Input.getFromOptions("Press enter to enter a queue, or type \"exit\" to exit", "", "exit");

        if (input.equals("exit"))
            return new NackMessage();

        return new AckMessage();
    }

    @Override
    public State handle(Message input) {
        if (input instanceof AckMessage)
            return new QueueState();

        else if (input instanceof NackMessage)
            System.out.println("The queue is full!");

        return this;
    }
}
