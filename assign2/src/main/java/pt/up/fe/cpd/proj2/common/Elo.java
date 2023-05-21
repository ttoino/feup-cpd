package pt.up.fe.cpd.proj2.common;

/**
 * Elo rating system.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Elo_rating_system">Elo rating system</a>
 * @see <a href="https://towardsdatascience.com/developing-a-generalized-elo-rating-system-for-multiplayer-games-b9b495e87802">Developing a Generalized Elo Rating System for Multiplayer Games</a>
 */
public final class Elo {
    public static double expectedScore(double a, double b) {
        return 1.0 / (1.0 + Math.pow(10.0, (b - a) / Config.dFactor()));
    }

    public static double updatedRating(double rating, double expectedScore, double actualScore) {
        return rating + Config.kFactor() * (actualScore - expectedScore);
    }

    public static double expectedScore(double rating, double... other) {
        var sum = 0.0;
        for (var otherRating : other)
            sum += expectedScore(rating, otherRating);
        return sum / ((other.length + 1) * other.length / 2.0);
    }

    public static double updatedRating(double rating, double expectedScore, double actualScore, int size) {
        return rating + Config.kFactor() * (actualScore - expectedScore) * (size - 1);
    }

    public static double actualScore(int place, int size) {
        if (size == 1)
            return 1.0;

        var sum = 0.0;

        if (Config.alphaFactor() > 1) {
            for (var i = 1; i <= size; i++)
                sum += Math.pow(Config.alphaFactor(), size - i) - 1;
            return (Math.pow(Config.alphaFactor(), size - place) - 1) / sum;
        } else {
            return (size - place)/(size * (size - 1) / 2.0);
        }
    }

    private Elo() {}
}
