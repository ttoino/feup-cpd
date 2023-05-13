package pt.up.fe.cpd.proj2.server.queue;

import pt.up.fe.cpd.proj2.common.message.QueueStatusMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractUserQueue implements UserQueue {
    protected final Lock lock;

    protected AbstractUserQueue() {
        lock = new ReentrantLock();
    }

    protected void notifyUsers(Collection<QueuedUserInfo> users) throws IOException {
        var i = 0;
        var now = new Date();

        lock.lock();
        var s = users.size();
        for (var queuedUserInfo : users) {
            var time = (int) (now.getTime() - queuedUserInfo.queueTime().getTime()) / 1000;
            var message = new QueueStatusMessage(i++, s, time);

            queuedUserInfo.channel().write(message.serialize());
        }
        lock.unlock();
    }
}
