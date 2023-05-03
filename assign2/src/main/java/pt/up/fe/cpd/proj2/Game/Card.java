package pt.up.fe.cpd.proj2.Game;

public class Card {

    private String symbol;
    private int value;

    private String suit;

    public Card(String symbol, String suit) throws Exception {
        switch (symbol) {
            case "Ace":
                this.symbol = symbol;
                this.value = 11;
                break;
            case "2":
                this.symbol = symbol;
                this.value = 2;
                break;
            case "3":
                this.symbol = symbol;
                this.value = 3;
                break;
            case "4":
                this.symbol = symbol;
                this.value = 4;
                break;
            case "5":
                this.symbol = symbol;
                this.value = 5;
                break;
            case "6":
                this.symbol = symbol;
                this.value = 6;
                break;
            case "7":
                this.symbol = symbol;
                this.value = 7;
                break;
            case "8":
                this.symbol = symbol;
                this.value = 8;
                break;
            case "9":
                this.symbol = symbol;
                this.value = 9;
                break;
            case "10":
                this.symbol = symbol;
                this.value = 10;
                break;
            case "Jack":
                this.symbol = symbol;
                this.value = 10;
                break;
            case "Queen":
                this.symbol = symbol;
                this.value = 10;
                break;
            case "King":
                this.symbol = symbol;
                this.value = 10;
                break;
            default:
                throw new Exception("Invalid Card Symbol");
        }

        switch (suit) {
            case "Heart":
                this.suit = suit;
                break;
            case "Club":
                this.suit = suit;
                break;
            case "Diamond":
                this.suit = suit;
                break;
            case "Spade":
                this.suit = suit;
                break;
            default:
                throw new Exception("Invalid Card Suit");
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

    public void changeAceValue(){
        this.value = 1;
    }
}