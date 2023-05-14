package pt.up.fe.cpd.proj2.game;

public enum Move {
    HIT("Hit"),
    STAND("Stand"),
    // TODO
    // DOUBLE("Double"),
    // SPLIT("Split");
    ;

    private final String display;

    Move(String display) {
        this.display = display;
    }

    public String display() {
        return display;
    }
}
