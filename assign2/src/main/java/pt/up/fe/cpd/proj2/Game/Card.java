package pt.up.fe.cpd.proj2.Game;

public class Card {

    private final String symbol;
    private final int value;

    private final String suit;

    public Card(String symbol, String suit) throws Exception {
        switch (symbol) {
            case "Ace" -> {
                this.symbol = symbol;
                this.value = 11;
            }
            case "2" -> {
                this.symbol = symbol;
                this.value = 2;
            }
            case "3" -> {
                this.symbol = symbol;
                this.value = 3;
            }
            case "4" -> {
                this.symbol = symbol;
                this.value = 4;
            }
            case "5" -> {
                this.symbol = symbol;
                this.value = 5;
            }
            case "6" -> {
                this.symbol = symbol;
                this.value = 6;
            }
            case "7" -> {
                this.symbol = symbol;
                this.value = 7;
            }
            case "8" -> {
                this.symbol = symbol;
                this.value = 8;
            }
            case "9" -> {
                this.symbol = symbol;
                this.value = 9;
            }
            case "10", "Jack", "Queen", "King" -> {
                this.symbol = symbol;
                this.value = 10;
            }
            default -> throw new Exception("Invalid Card Symbol");
        }

        switch (suit) {
            case "Heart", "Club", "Diamond", "Spade" -> this.suit = suit;
            default -> throw new Exception("Invalid Card Suit");
        }
    }

    public String getSymbol() {
        return symbol;
    }

    public int getValue() {
        return value;
    }

    public String getSuit() {
        return suit;
    }

    @Override
    public String toString() {

        return "This is a " + symbol + " of " + suit;

    }
}