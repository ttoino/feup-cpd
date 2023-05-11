package pt.up.fe.cpd.proj2.server.queue;

import pt.up.fe.cpd.proj2.common.Config;
import pt.up.fe.cpd.proj2.common.message.QueueStatusMessage;
import pt.up.fe.cpd.proj2.server.auth.UserInfo;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleUserQueue implements UserQueue {
    private final Lock lock;
    private final Condition enoughUsers;
    private final Queue<QueuedUserInfo> queue;

    public SimpleUserQueue() {
        queue = new LinkedList<>();
        lock = new ReentrantLock();
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
        var i = 0;
        var now = new Date();

        lock.lock();
        var s = queue.size();
        for (var queuedUserInfo : queue) {
            var time = (int) (now.getTime() - queuedUserInfo.queueTime().getTime()) / 1000;
            var message = new QueueStatusMessage(i++, s, time);

            queuedUserInfo.channel().write(message.serialize());
        }
        lock.unlock();
    }

    @Override
    public Collection<UserInfo> nextUsers() {
        lock.lock();

        try {
            enoughUsers.await();

            var users = new TreeSet<UserInfo>();
            for (var i = 0; i < Config.maxPlayers(); i++) {
                var queuedUserInfo = queue.remove();
                users.add(queuedUserInfo.userInfo());
            }

            return users;
        } catch (InterruptedException e) {
            return null;
        } finally {
            lock.unlock();
        }
    }
}
