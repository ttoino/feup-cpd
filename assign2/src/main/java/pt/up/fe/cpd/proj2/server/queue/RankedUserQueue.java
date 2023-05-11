package pt.up.fe.cpd.proj2.server.queue;

import pt.up.fe.cpd.proj2.server.auth.UserInfo;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Collection;

public class RankedUserQueue implements UserQueue {
    @Override
    public void enqueue(UserInfo userInfo, SocketChannel channel) {

    }

    @Override
    public void notifyUsers() throws IOException {

    }

    @Override
    public Collection<UserInfo> nextUsers() {
        return null;
    }
}
