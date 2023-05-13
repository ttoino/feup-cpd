package pt.up.fe.cpd.proj2.common;

import java.io.PrintStream;

public class Output {
    public static void clear(PrintStream out) {
        out.print("\033[H\033[2J");
        out.flush();
    }

    public static void clear() {
        clear(System.out);
    }

    public static void debug(String message) {
        if (Config.debug())
            System.out.println(message);
    }

    private Output() {}
}
