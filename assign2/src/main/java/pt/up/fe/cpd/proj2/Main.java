package pt.up.fe.cpd.proj2;

import pt.up.fe.cpd.proj2.common.Config;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Config.parse(args);

        switch (Config.appType()) {
//            case CLIENT -> new Client().run();
//            case SERVER -> new Server().run();
        }
    }
}
