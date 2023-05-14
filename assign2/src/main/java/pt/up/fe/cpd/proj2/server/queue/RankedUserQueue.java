package pt.up.fe.cpd.proj2.server.queue;

import pt.up.fe.cpd.proj2.common.Config;
import pt.up.fe.cpd.proj2.server.User;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;

public class RankedUserQueue extends AbstractUserQueue {
    private final Condition enoughUsers;
    private final NavigableSet<QueuedUser> setByTime;
    private final NavigableSet<QueuedUser> setByElo;

    public RankedUserQueue() {
        super();
        setByTime = new TreeSet<>();
        setByElo = new TreeSet<>((a, b) -> (int) Math.ceil(a.user().info().elo() - b.user().info().elo()));
        enoughUsers = lock.newCondition();
    }

    @Override
    public void enqueue(User user) {
        lock.lock();
        var info = new QueuedUser(user, new Date());
        setByTime.add(info);
        setByElo.add(info);
        if (setByTime.size() >= Config.maxPlayers())
            enoughUsers.signal();
        lock.unlock();
    }

    @Override
    public void notifyUsers() throws IOException {
        notifyUsers(setByTime);
    }

    @Override
    public List<User> nextUsers() {
        lock.lock();

        try {
            if (setByTime.size() < Config.maxPlayers())
                enoughUsers.await();

            for (var pivot : setByTime) {
                var users = tryNextUsers(pivot);
                if (users != null)
                    return users;
            }

            return null;
        } catch (InterruptedException e) {
            return null;
        } finally {
            lock.unlock();
        }
    }

    private List<User> tryNextUsers(QueuedUser pivot) {
        var elo = pivot.user().info().elo();
        var impatience = impatience(pivot);

        var nextUp = setByElo.higher(pivot);
        var nextDown = setByElo.lower(pivot);

        var users = new ArrayList<User>(Config.maxPlayers());
        users.add(pivot.user());

        while ((nextUp != null || nextDown != null) && users.size() < Config.maxPlayers()) {
            var eloUp = nextUp == null ? Double.POSITIVE_INFINITY : nextUp.user().info().elo();
            var eloDown = nextDown == null ? Double.NEGATIVE_INFINITY : nextDown.user().info().elo();

            if (eloUp - elo < elo - eloDown) {
                var impatienceUp = impatience(nextUp);

                if (elo + impatience >= eloUp - impatienceUp) {
                    users.add(nextUp.user());
                    setByTime.remove(nextUp);
                    setByElo.remove(nextUp);
                } else {
                    nextUp = setByElo.higher(nextUp);
                }
            } else {
                var impatienceDown = impatience(nextDown);

                if (elo - impatience <= eloDown + impatienceDown) {
                    users.add(nextDown.user());
                    setByTime.remove(nextDown);
                    setByElo.remove(nextDown);
                } else {
                    nextDown = setByElo.lower(nextDown);
                }
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
