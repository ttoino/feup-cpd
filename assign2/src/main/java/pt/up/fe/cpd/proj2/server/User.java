package pt.up.fe.cpd.proj2.server;

import pt.up.fe.cpd.proj2.server.auth.UserInfo;

import java.nio.channels.SocketChannel;

public record User(UserInfo info, SocketChannel channel) {
}
