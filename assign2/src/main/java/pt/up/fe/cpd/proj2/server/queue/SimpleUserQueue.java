package pt.up.fe.cpd.proj2.server.queue;

import pt.up.fe.cpd.proj2.common.Config;
import pt.up.fe.cpd.proj2.server.auth.UserInfo;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.locks.Condition;

public class SimpleUserQueue extends AbstractUserQueue {
    private final Condition enoughUsers;
    private final Queue<QueuedUserInfo> queue;

    public SimpleUserQueue() {
        super();
        queue = new LinkedList<>();
        enoughUsers = lock.newCondition();
    }

    @Override
    public void enqueue(UserInfo userInfo, SocketChannel channel) {
        lock.lock();
        queue.add(new QueuedUserInfo(userInfo, channel, new Date()));
        if (queue.size() >= Config.maxPlayers())
            enoughUsers.signal();
        lock.unlock();
    }

    @Override
    public void notifyUsers() throws IOException {
        notifyUsers(queue);
    }

    @Override
    public Collection<QueuedUserInfo> nextUsers() {
        lock.lock();

        try {
            if (queue.size() < Config.maxPlayers())
                enoughUsers.await();

            var users = new TreeSet<QueuedUserInfo>();
            for (var i = 0; i < Config.maxPlayers(); i++) {
                var queuedUserInfo = queue.remove();
                users.add(queuedUserInfo);
            }

            return users;
        } catch (InterruptedException e) {
            return null;
        } finally {
            lock.unlock();
        }
    }
}
