package pt.up.fe.cpd.proj2.client.state;

import pt.up.fe.cpd.proj2.common.message.Message;
import pt.up.fe.cpd.proj2.common.message.QueueStatusMessage;

/**
 * Represents the state in the client state machine where the user is in the queue.
 *
 * @see pt.up.fe.cpd.proj2.client.Client
 */
public class QueueState implements State {
    @Override
    public Message run() {
        return null;
    }

    @Override
    public State handle(Message message) {
        if (message instanceof QueueStatusMessage queueStatusMessage) {
            System.out.println("You are in position " + queueStatusMessage.queuePosition() + " of " + queueStatusMessage.queueSize() + " in the queue");
            System.out.println("You have been waiting for" + queueStatusMessage.queueTime() + " seconds");
        }

        return this;
    }
}
