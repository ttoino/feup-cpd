package pt.up.fe.cpd.proj2.server.queue;

import pt.up.fe.cpd.proj2.common.Config;
import pt.up.fe.cpd.proj2.server.User;

import java.util.*;

public class RankedUserQueue extends AbstractUserQueue {
    private final NavigableMap<QueuedUser, QueuedUser> queueByElo;

    public RankedUserQueue() {
        super();
        queueByElo = new TreeMap<>((a, b) -> {
            if (a.user().info().elo() == b.user().info().elo())
                return Long.compare(a.user().info().id(), b.user().info().id());

            return Double.compare(a.user().info().elo(), b.user().info().elo());
        });
    }

    @Override
    public void enqueue(User user) {
        lock.lock();
        var info = new QueuedUser(user, new Date());

        var oldInfo = queueByElo.remove(info);

        if (oldInfo != null) {
            queue.remove(oldInfo);
            info = new QueuedUser(user, oldInfo.queueTime());
        }

        queue.put(info, info);
        queueByElo.put(info, info);
        lock.unlock();
    }

    @Override
    public List<User> nextUsers() {
        try {
            lock.lock();

            for (var pivot : queue.values()) {
                if (!pivot.user().channel().isOpen()) {
                    if (pivot.queueTime().getTime() - new Date().getTime() > Config.maxQueueTime() * 2000L) {
                        queue.remove(pivot);
                        queueByElo.remove(pivot);
                    }

                    continue;
                }

                var users = tryNextUsers(pivot);
                if (users != null)
                    return users;
            }

            return null;
        } finally {
            lock.unlock();
        }
    }

    private List<User> tryNextUsers(QueuedUser pivot) {
        var elo = pivot.user().info().elo();
        var impatience = impatience(pivot);

        var nextUp = queueByElo.higherKey(pivot);
        var nextDown = queueByElo.lowerKey(pivot);

        var users = new ArrayList<User>(Config.maxPlayers());
        users.add(pivot.user());

        while ((nextUp != null || nextDown != null) && users.size() < Config.maxPlayers()) {
            var eloUp = nextUp == null ? Double.POSITIVE_INFINITY : nextUp.user().info().elo();
            var eloDown = nextDown == null ? Double.NEGATIVE_INFINITY : nextDown.user().info().elo();

            if (eloUp - elo < elo - eloDown) {
                var impatienceUp = impatience(nextUp);

                if (elo + impatience >= eloUp - impatienceUp && nextUp.user().channel().isOpen()) {
                    users.add(nextUp.user());
                    queue.remove(nextUp);
                    queueByElo.remove(nextUp);
                }

                nextUp = queueByElo.higherKey(nextUp);
            } else {
                var impatienceDown = impatience(nextDown);

                if (elo - impatience <= eloDown + impatienceDown && nextDown.user().channel().isOpen()) {
                    users.add(nextDown.user());
                    queue.remove(nextDown);
                    queueByElo.remove(nextDown);
                }

                nextDown = queueByElo.lowerKey(nextDown);
            }
        }

        if (users.size() == Config.maxPlayers())
            return users;
        else
            return null;
    }

    private double impatience(QueuedUser user) {
        var time = (double) Math.min((new Date().getTime() - user.queueTime().getTime()) / 1000, Config.maxQueueTime());
        return Config.eloThreshold() / 2.0 * time / Config.maxQueueTime();
    }
}
