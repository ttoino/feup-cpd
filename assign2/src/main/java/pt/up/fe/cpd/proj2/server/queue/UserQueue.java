package pt.up.fe.cpd.proj2.server.queue;

import pt.up.fe.cpd.proj2.server.auth.UserInfo;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Collection;

public interface UserQueue {
    void enqueue(UserInfo userInfo, SocketChannel channel);

    void notifyUsers() throws IOException;

    Collection<UserInfo> nextUsers();
}
