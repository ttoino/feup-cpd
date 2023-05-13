package pt.up.fe.cpd.proj2.server.queue;

import pt.up.fe.cpd.proj2.common.Config;
import pt.up.fe.cpd.proj2.server.auth.UserInfo;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.locks.Condition;

public class RankedUserQueue extends AbstractUserQueue {
    private final Condition enoughUsers;
    private final NavigableSet<QueuedUserInfo> setByTime;
    private final NavigableSet<QueuedUserInfo> setByElo;

    public RankedUserQueue() {
        super();
        setByTime = new TreeSet<>();
        setByElo = new TreeSet<>((a, b) -> (int) Math.ceil(a.userInfo().elo() - b.userInfo().elo()));
        enoughUsers = lock.newCondition();
    }

    @Override
    public void enqueue(UserInfo userInfo, SocketChannel channel) {
        lock.lock();
        var info = new QueuedUserInfo(userInfo, channel, new Date());
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
    public Collection<QueuedUserInfo> nextUsers() {
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

    private Collection<QueuedUserInfo> tryNextUsers(QueuedUserInfo pivot) {
        var elo = pivot.userInfo().elo();
        var impatience = impatience(pivot);

        var nextUp = setByElo.higher(pivot);
        var nextDown = setByElo.lower(pivot);

        var users = new ArrayList<QueuedUserInfo>(Config.maxPlayers());
        users.add(pivot);

        while ((nextUp != null || nextDown != null) && users.size() < Config.maxPlayers()) {
            var eloUp = nextUp == null ? Double.POSITIVE_INFINITY : nextUp.userInfo().elo();
            var eloDown = nextDown == null ? Double.NEGATIVE_INFINITY : nextDown.userInfo().elo();

            if (eloUp - elo < elo - eloDown) {
                var impatienceUp = impatience(nextUp);

                if (elo + impatience >= eloUp - impatienceUp) {
                    users.add(nextUp);
                } else {
                    nextUp = setByElo.higher(nextUp);
                }
            } else {
                var impatienceDown = impatience(nextDown);

                if (elo - impatience <= eloDown + impatienceDown) {
                    users.add(nextDown);
                } else {
                    nextDown = setByElo.lower(nextDown);
                }
            }
        }

        if (users.size() == Config.maxPlayers()) {
            users.forEach(setByTime::remove);
            users.forEach(setByElo::remove);
            return users;
        } else {
            return null;
        }
    }

    private double impatience(QueuedUserInfo info) {
        var time = (double) Math.min((new Date().getTime() - info.queueTime().getTime()) / 1000, Config.maxQueueTime());
        return Config.eloThreshold() / 2.0 * time / Config.maxQueueTime();
    }
}
