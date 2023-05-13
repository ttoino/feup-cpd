package pt.up.fe.cpd.proj2.server.auth;

import pt.up.fe.cpd.proj2.common.Base64;
import pt.up.fe.cpd.proj2.common.Config;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Scanner;
import java.util.TreeSet;

public class FileUserInfoProvider implements UserInfoProvider, AutoCloseable {
    private final FileChannel fileChannel;

    private final Collection<UserInfo> users;

    public FileUserInfoProvider(String filename) throws IOException {
        fileChannel = FileChannel.open(Path.of(filename), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
        users = load();
    }

    private Collection<UserInfo> load() throws IOException {
        var users = new TreeSet<UserInfo>();

        fileChannel.position(0);
        var stream = Channels.newInputStream(fileChannel);
        var scanner = new Scanner(stream, StandardCharsets.UTF_8);

        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();
            var parts = line.split(":");

            var id = Integer.parseInt(parts[0]);
            var username = Base64.decode(parts[1]);
            var password = Base64.decode(parts[2]);
            var elo = Double.parseDouble(parts[3]);

            users.add(new UserInfo(id, username, password, elo));
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
        return users.stream()
                .filter(user -> user.username().equals(username) && user.password().equals(password))
                .findFirst()
                .orElse(null);
    }

    @Override
    public UserInfo register(String username, String password) {
        if (users.stream().anyMatch(user -> user.username().equals(username)))
            return null;

        var id = users.size();
        var user = new UserInfo(id, username, password, Config.initialElo());
        users.add(user);

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return user;
    }

    public void save() throws IOException {
        fileChannel.position(0);

        var out = Channels.newOutputStream(fileChannel);
        var stream = new PrintStream(out, false, StandardCharsets.UTF_8);

        for (var user : users) {
            stream.println(user.id() + ":" + Base64.encode(user.username()) + ":" + Base64.encode(user.password()) + ":" + user.elo());
        }

        stream.flush();
    }
}
