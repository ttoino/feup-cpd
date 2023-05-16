package pt.up.fe.cpd.proj2.server.queue;

import pt.up.fe.cpd.proj2.common.Sockets;
import pt.up.fe.cpd.proj2.common.message.QueueStatusMessage;

import java.util.Collection;
import java.util.Date;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractUserQueue implements UserQueue {
    protected final Lock lock;
    protected final NavigableMap<QueuedUser, QueuedUser> queue;

    protected AbstractUserQueue() {
        lock = new ReentrantLock();
        queue = new TreeMap<>();
    }

    @Override
    public void notifyUsers() {
        var i = 0;
        var now = new Date();

        lock.lock();
        var s = queue.size();
        for (var queuedUser : queue.values()) {
            var time = (int) (now.getTime() - queuedUser.queueTime().getTime()) / 1000;
            var message = new QueueStatusMessage(i++, s, time);

            Sockets.write(queuedUser.user().channel(), message);
        }
        lock.unlock();
    }
}
