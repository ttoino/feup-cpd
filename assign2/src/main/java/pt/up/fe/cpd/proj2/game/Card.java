package pt.up.fe.cpd.proj2.game;

public record Card(Symbol symbol, Suit suit) {
    public String display() {
        return Character.toString(0x1F0A1 + suit.ordinal() * 16 + symbol.ordinal() + (symbol.ordinal() > 10 ? 1 : 0));
    }

    public static String backDisplay() {
        return Character.toString(0x1F0A0);
    }

    public enum Suit {
        SPADES("♠"),
        HEARTS("♥"),
        DIAMONDS("♦"),
        CLUBS("♣");

        private final String display;

        Suit(String display) {
            this.display = display;
        }

        public String value() {
            return display;
        }
    }
    
    public enum Symbol {
        ACE("A", 11),
        TWO("2", 2),
        THREE("3", 3),
        FOUR("4", 4),
        FIVE("5", 5),
        SIX("6", 6),
        SEVEN("7", 7),
        EIGHT("8", 8),
        NINE("9", 9),
        TEN("10", 10),
        JACK("J", 10),
        QUEEN("Q", 10),
        KING("K", 10);
        
        private final String display;

        private final int value;
        
        Symbol(String display, int value) {
            this.display = display;
            this.value = value;
        }
        
        public String display() {
            return display;
        }

        public int value() {
            return value;
        }
    }
}