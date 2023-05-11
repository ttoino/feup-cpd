package pt.up.fe.cpd.proj2.client.state;

import pt.up.fe.cpd.proj2.common.message.Message;

public abstract class State {
    public abstract Message run();

    public abstract State handle(Message message);
}
