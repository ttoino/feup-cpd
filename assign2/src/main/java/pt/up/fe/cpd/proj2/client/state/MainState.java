package pt.up.fe.cpd.proj2.client.state;

import pt.up.fe.cpd.proj2.common.Input;
import pt.up.fe.cpd.proj2.common.message.AckMessage;
import pt.up.fe.cpd.proj2.common.message.Message;
import pt.up.fe.cpd.proj2.common.message.NackMessage;

public class MainState extends State {
    @Override
    public Message run() {
        var input =  Input.get("Press enter to enter a queue, or type \"exit\" to exit");

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
