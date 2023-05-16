package pt.up.fe.cpd.proj2.server.queue;

import pt.up.fe.cpd.proj2.server.User;

import java.util.Date;

public record QueuedUser(User user, Date queueTime) implements Comparable<QueuedUser> {
    @Override
    public int compareTo(QueuedUser o) {
        if (user.info().id() == o.user().info().id())
            return 0;

        return queueTime.compareTo(o.queueTime);
    }
}
