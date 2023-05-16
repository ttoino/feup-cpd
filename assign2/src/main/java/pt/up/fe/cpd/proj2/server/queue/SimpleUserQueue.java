package pt.up.fe.cpd.proj2.server.queue;

import pt.up.fe.cpd.proj2.common.Config;
import pt.up.fe.cpd.proj2.server.User;

import java.util.*;

public class SimpleUserQueue extends AbstractUserQueue {

    public SimpleUserQueue() {
        super();
    }

    @Override
    public void enqueue(User user) {
        lock.lock();
        var info = new QueuedUser(user, new Date());

        var oldInfo = queue.remove(info);

        if (oldInfo != null)
            info = new QueuedUser(user, oldInfo.queueTime());

        queue.put(info, info);
        lock.unlock();
    }

    @Override
    public List<User> nextUsers() {
        try {
            lock.lock();

            if (queue.size() < Config.maxPlayers())
                return null;

            var users = new ArrayList<QueuedUser>();

            for (var pivot : queue.values()) {
                if (!pivot.user().channel().isOpen()) {
                    if (pivot.queueTime().getTime() - new Date().getTime() > Config.maxQueueTime() * 2000L) {
                        queue.remove(pivot);
                    }

                    continue;
                }

                users.add(pivot);

                if (users.size() == Config.maxPlayers()) {
                    users.forEach(queue::remove);
                    return users.stream().map(QueuedUser::user).toList();
                }
            }

            return null;
        } finally {
            lock.unlock();
        }
    }
}
