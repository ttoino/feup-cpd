package pt.up.fe.cpd.proj2.server.queue;

import pt.up.fe.cpd.proj2.server.User;

import java.util.List;

public interface UserQueue {
    void enqueue(User user);

    void notifyUsers();

    List<User> nextUsers();
}
