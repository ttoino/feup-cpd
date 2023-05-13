package pt.up.fe.cpd.proj2.client.state;

import pt.up.fe.cpd.proj2.common.message.Message;

/**
 * Represents a state in the client state machine.
 *
 * @see pt.up.fe.cpd.proj2.client.Client
 */
public interface State {
    /**
     * Runs the state.
     *
     * @return The message to send to the server
     */
    Message run();

    /**
     * Handles a message received from the server.
     *
     * @param message The message received from the server
     * @return The next state
     */
    State handle(Message message);
}
