package pt.up.fe.cpd.proj2.common;

public class Config {
    private static int port = 8080;

    private static String host = "localhost";

    private static int maxPlayers = 2;

    private static int maxGameThreads = 10;

    private static int maxQueueSize = 100;

    private static int maxQueueTime = 60;

    private static QueueType queueType = QueueType.UNRANKED;

    private static AppType appType = AppType.CLIENT;

    private static boolean debug = false;

    public static void parse(String[] args) {
        for (String arg : args) {
            if (!arg.startsWith("-"))
                throw new IllegalArgumentException("Invalid argument: " + arg);

            var parsedArg = arg.substring(1).split("=", 2);

            switch (parsedArg[0]) {
                case "port", "p" -> port = Integer.parseInt(parsedArg[1]);
                case "host", "h" -> host = parsedArg[1];
                case "max-players", "mp" -> maxPlayers = Integer.parseInt(parsedArg[1]);
                case "max-game-threads", "mg" -> maxGameThreads = Integer.parseInt(parsedArg[1]);
                case "max-queue-size", "mq" -> maxQueueSize = Integer.parseInt(parsedArg[1]);
                case "max-queue-time", "mt" -> maxQueueTime = Integer.parseInt(parsedArg[1]);
                case "ranked", "r" -> queueType = QueueType.RANKED;
                case "unranked", "u" -> queueType = QueueType.UNRANKED;
                case "client", "c" -> appType = AppType.CLIENT;
                case "server", "s" -> appType = AppType.SERVER;
                case "debug", "d" -> debug = true;
            }
        }
    }

    public static int port() {
        return port;
    }

    public static String host() {
        return host;
    }

    public static int maxPlayers() {
        return maxPlayers;
    }

    public static int maxGameThreads() {
        return maxGameThreads;
    }

    public static int maxQueueSize() {
        return maxQueueSize;
    }

    public static int maxQueueTime() {
        return maxQueueTime;
    }

    public static QueueType queueType() {
        return queueType;
    }

    public static AppType appType() {
        return appType;
    }

    public static boolean debug() {
        return debug;
    }

    public enum AppType {
        CLIENT,
        SERVER
    }

    public enum QueueType {
        RANKED,
        UNRANKED
    }
}
