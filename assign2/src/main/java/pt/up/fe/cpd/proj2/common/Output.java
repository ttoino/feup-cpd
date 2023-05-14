package pt.up.fe.cpd.proj2.common;

import java.io.PrintStream;
import java.util.stream.Collectors;

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

    public static String centered(String s, int width) {
        int len = s.codePointCount(0, s.length());
        if (s.codePointCount(0, s.length()) >= width) {
            s = s.codePoints().limit(width - 1).mapToObj(Character::toString).collect(Collectors.joining()) + "…";
            len = width;
        }
        return " ".repeat((width - len) / 2) + s + " ".repeat((width - len + 1) / 2);
    }

    private Output() {}
}
