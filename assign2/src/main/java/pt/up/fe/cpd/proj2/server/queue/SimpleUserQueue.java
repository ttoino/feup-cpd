package pt.up.fe.cpd.proj2.server.queue;

import pt.up.fe.cpd.proj2.common.Config;
import pt.up.fe.cpd.proj2.server.User;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;

public class SimpleUserQueue extends AbstractUserQueue {
    private final Condition enoughUsers;
    private final Queue<QueuedUser> queue;

    public SimpleUserQueue() {
        super();
        queue = new LinkedList<>();
        enoughUsers = lock.newCondition();
    }

    @Override
    public void enqueue(User user) {
        lock.lock();
        queue.add(new QueuedUser(user, new Date()));
        if (queue.size() >= Config.maxPlayers())
            enoughUsers.signal();
        lock.unlock();
    }

    @Override
    public void notifyUsers() throws IOException {
        notifyUsers(queue);
    }

    @Override
    public List<User> nextUsers() {
        lock.lock();

        try {
            if (queue.size() < Config.maxPlayers())
                enoughUsers.await();

            var users = new ArrayList<User>();
            for (var i = 0; i < Config.maxPlayers(); i++) {
                var queuedUserInfo = queue.remove();
                users.add(queuedUserInfo.user());
            }

            return users;
        } catch (InterruptedException e) {
            return null;
        } finally {
            lock.unlock();
        }
    }
}
