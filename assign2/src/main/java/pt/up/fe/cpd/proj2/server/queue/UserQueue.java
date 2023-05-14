package pt.up.fe.cpd.proj2.server.queue;

import pt.up.fe.cpd.proj2.server.User;

import java.io.IOException;
import java.util.List;

public interface UserQueue {
    void enqueue(User user);

    void notifyUsers() throws IOException;

    List<User> nextUsers();
}
