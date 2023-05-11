package pt.up.fe.cpd.proj2.server.queue;

import pt.up.fe.cpd.proj2.server.auth.UserInfo;

import java.nio.channels.SocketChannel;
import java.util.Date;

public record QueuedUserInfo(UserInfo userInfo, SocketChannel channel, Date queueTime) {
}
