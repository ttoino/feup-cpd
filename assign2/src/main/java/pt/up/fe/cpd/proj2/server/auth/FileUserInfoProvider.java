package pt.up.fe.cpd.proj2.server.auth;

import pt.up.fe.cpd.proj2.common.Base64;
import pt.up.fe.cpd.proj2.common.Config;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class FileUserInfoProvider implements UserInfoProvider, AutoCloseable {
    private final FileChannel fileChannel;

    private final Set<UserInfo> users;

    public FileUserInfoProvider() throws IOException {
        fileChannel = FileChannel.open(Path.of(Config.authFile()), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
        users = load();
    }

    private Set<UserInfo> load() throws IOException {
        var users = new TreeSet<UserInfo>();

        fileChannel.position(0);
        long id = 0;
        var stream = Channels.newInputStream(fileChannel);

        while (stream.available() > 0) {
            var bUsername = stream.readNBytes(64);
            var bPassword = stream.readNBytes(32);
            var bElo = stream.readNBytes(8);
            var bNl = stream.read();

            assert bNl == '\n';

            var username = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bUsername)).toString().trim();
            var elo = ByteBuffer.wrap(bElo).getDouble();

            users.add(new UserInfo(id++, username, bPassword, elo));
        }

        return users;
    }

    @Override
    public void close() throws Exception {
        save();

        if (fileChannel != null)
            fileChannel.close();
    }

    @Override
    public UserInfo login(String username, String password) {
        var user =  users.stream()
                .filter(u -> u.username().equals(username))
                .findFirst()
                .orElse(null);

        if (user == null) return null;

        if (!Password.verify(password, user.password())) return null;

        return user;
    }

    @Override
    public UserInfo register(String username, String password) {
        if (users.stream().anyMatch(user -> user.username().equals(username)))
            return null;

        if (username.isBlank() || password.isBlank() || StandardCharsets.UTF_8.encode(username).limit() > 64)
            return null;

        var hash = Password.hash(password, Password.generateSalt());

        var id = users.size();
        var user = new UserInfo(id, username, hash, Config.initialElo());
        users.add(user);

        try {
            saveUser(user);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public UserInfo update(UserInfo user) {
        users.remove(user);
        users.add(user);

        try {
            saveUser(user);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return user;
    }

    private void save() throws IOException {
        fileChannel.position(0);

        var buffer = ByteBuffer.allocate(1024);

        for (var user : users) {
            buffer.clear();
            writeUser(user, buffer);
        }
    }

    private void saveUser(UserInfo user) throws IOException {
        fileChannel.position(user.id() * 105);

        var buffer = ByteBuffer.allocate(1024);

        writeUser(user, buffer);
    }

    private void writeUser(UserInfo user, ByteBuffer buffer) throws IOException {
        buffer.put(StandardCharsets.UTF_8.encode(user.username()));
        buffer.put(new byte[64 - user.username().length()]);
        buffer.put(user.password());
        buffer.putDouble(user.elo());
        buffer.put((byte) '\n');
        buffer.flip();

        fileChannel.write(buffer);
    }
}
