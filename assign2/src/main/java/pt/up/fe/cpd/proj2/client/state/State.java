package pt.up.fe.cpd.proj2.client.state;

import pt.up.fe.cpd.proj2.common.message.Message;

public interface State {
    Message run();

    State handle(Message message);
}
