package pt.up.fe.cpd.proj2.common;

public class Config {
    /**
     * The port to listen on/connect to.
     */
    private static int port = 8080;

    /**
     * The host to connect to.
     */
    private static String host = "localhost";

    /**
     * The maximum number of players in a game.
     */
    private static int maxPlayers = 2;

    private static int decks = 2;

    private static int maxPoints = 7;

    /**
     * The maximum number of game threads (and thus games).
     */
    private static int maxGameThreads = 10;

    /**
     * The maximum number of players in the queue.
     */
    private static int maxQueueSize = 100;

    /**
     * The maximum time a player can be in the queue.
     */
    private static int maxQueueTime = 60;

    /**
     * The "K" factor used in the Elo rating system.
     */
    private static double kFactor = 32;

    /**
     * The "D" factor used in the Elo rating system.
     */
    private static double dFactor = 400;

    /**
     * The "alpha" factor used in the Elo rating system.
     */
    private static double alphaFactor = 1;

    /**
     * The initial Elo rating for new players.
     */
    private static double initialElo = 1000;

    /**
     * The maximum elo difference between two players in the same match.
     */
    private static double eloThreshold = 800;

    private static String authFile = "auth.users";

    /**
     * The type of queue to use.
     */
    private static QueueType queueType = QueueType.UNRANKED;

    /**
     * The type of application to run.
     */
    private static AppType appType = AppType.CLIENT;

    /**
     * The type of authentication to use.
     */
    private static AuthType authType = AuthType.FILE;

    /**
     * Whether to run in debug mode.
     */
    private static boolean debug = false;

    /**
     * Parses the command line arguments.
     *
     * @param args the command line arguments
     */
    public static void parse(String[] args) {
        for (String arg : args) {
            if (!arg.startsWith("-"))
                throw new IllegalArgumentException("Invalid argument: " + arg);

            var parsedArg = arg.substring(1).split("=", 2);

            switch (parsedArg[0]) {
                case "port", "p" -> port = Integer.parseInt(parsedArg[1]);
                case "host", "h" -> host = parsedArg[1];
                case "max-players", "mp" -> maxPlayers = Integer.parseInt(parsedArg[1]);
                case "decks", "nd" -> decks = Integer.parseInt(parsedArg[1]);
                case "max-points", "mpt" -> maxPoints = Integer.parseInt(parsedArg[1]);
                case "max-game-threads", "mg" -> maxGameThreads = Integer.parseInt(parsedArg[1]);
                case "max-queue-size", "mq" -> maxQueueSize = Integer.parseInt(parsedArg[1]);
                case "max-queue-time", "mt" -> maxQueueTime = Integer.parseInt(parsedArg[1]);
                case "k-factor", "kf" -> kFactor = Double.parseDouble(parsedArg[1]);
                case "d-factor", "df" -> dFactor = Double.parseDouble(parsedArg[1]);
                case "alpha-factor", "af" -> alphaFactor = Double.parseDouble(parsedArg[1]);
                case "initial-elo", "ie" -> initialElo = Double.parseDouble(parsedArg[1]);
                case "elo-threshold", "et" -> eloThreshold = Double.parseDouble(parsedArg[1]);
                case "auth-file", "fp" -> authFile = parsedArg[1];
                case "ranked", "r" -> queueType = QueueType.RANKED;
                case "unranked", "u" -> queueType = QueueType.UNRANKED;
                case "client", "c" -> appType = AppType.CLIENT;
                case "server", "s" -> appType = AppType.SERVER;
                case "file", "f" -> authType = AuthType.FILE;
                case "debug", "d" -> debug = true;
            }
        }
    }

    /**
     * The port to listen on/connect to.
     */
    public static int port() {
        return port;
    }

    /**
     * The host to connect to.
     */
    public static String host() {
        return host;
    }

    /**
     * The maximum number of players in a game.
     */
    public static int maxPlayers() {
        return maxPlayers;
    }

    public static int decks() {
        return decks;
    }

    public static int maxPoints() {
        return maxPoints;
    }

    /**
     * The maximum number of game threads (and thus games).
     */
    public static int maxGameThreads() {
        return maxGameThreads;
    }

    /**
     * The maximum number of players in the queue.
     */
    public static int maxQueueSize() {
        return maxQueueSize;
    }

    /**
     * The maximum time a player can be in the queue.
     */
    public static int maxQueueTime() {
        return maxQueueTime;
    }

    /**
     * The "K" factor used in the Elo rating system.
     */
    public static double kFactor() {
        return kFactor;
    }

    /**
     * The "D" factor used in the Elo rating system.
     */
    public static double dFactor() {
        return dFactor;
    }

    /**
     * The "alpha" factor used in the Elo rating system.
     */
    public static double alphaFactor() {
        return alphaFactor;
    }

    /**
     * The initial Elo rating for new players.
     */
    public static double initialElo() {
        return initialElo;
    }

    /**
     * The maximum elo difference between two players in the same match.
     */
    public static double eloThreshold() {
        return eloThreshold;
    }

    public static String authFile() {
        return authFile;
    }

    /**
     * The type of queue to use.
     */
    public static QueueType queueType() {
        return queueType;
    }

    /**
     * The type of application to run.
     */
    public static AppType appType() {
        return appType;
    }

    /**
     * The type of authentication to use.
     */
    public static AuthType authType() {
        return authType;
    }

    /**
     * Whether to run in debug mode.
     */
    public static boolean debug() {
        return debug;
    }

    /**
     * The type of application to run.
     *
     * @see pt.up.fe.cpd.proj2.Main
     */
    public enum AppType {
        /**
         * Run the client application.
         *
         * @see pt.up.fe.cpd.proj2.client.Client
         */
        CLIENT,
        /**
         * Run the server application.
         *
         * @see pt.up.fe.cpd.proj2.server.Server
         */
        SERVER
    }

    /**
     * The type of queue to use.
     *
     * @see pt.up.fe.cpd.proj2.server.queue.UserQueue
     */
    public enum QueueType {
        /**
         * Use a ranked queue.
         *
         * @see pt.up.fe.cpd.proj2.server.queue.RankedUserQueue
         */
        RANKED,
        /**
         * Use an unranked queue.
         *
         * @see pt.up.fe.cpd.proj2.server.queue.SimpleUserQueue
         */
        UNRANKED
    }

    /**
     * The type of authentication to use.
     *
     * @see pt.up.fe.cpd.proj2.server.auth.UserInfoProvider
     */
    public enum AuthType {
        /**
         * Use a file to store the user information.
         *
         * @see pt.up.fe.cpd.proj2.server.auth.FileUserInfoProvider
         */
        FILE
    }

    private Config() {}
}
