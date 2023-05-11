package pt.up.fe.cpd.proj2;

import pt.up.fe.cpd.proj2.client.Client;
import pt.up.fe.cpd.proj2.common.Config;
import pt.up.fe.cpd.proj2.server.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Config.parse(args);

        switch (Config.appType()) {
            case CLIENT -> new Client().run();
            case SERVER -> {
                try (var server = new Server()) {
                    server.run();
                } catch (Exception e) {
                }
            }
        }
    }
}
