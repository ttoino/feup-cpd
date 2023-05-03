protected class Card {

    String symbol;
    int value;
    String suit;

    Card(String symbol, String suit){
        switch (symbol) {
            case "Ace":
                this.symbol = symbol;
                this.value = 11;
            case "2":
                this.symbol = symbol;
                this.value = 2;
            case "3":
                this.symbol = symbol;
                this.value = 3;
            case "4":
                this.symbol = symbol;
                this.value = 4;
            case "5":
                this.symbol = symbol;
                this.value = 5;
            case "6":
                this.symbol = symbol;
                this.value = 6;
            case "7":
                this.symbol = symbol;
                this.value = 7;
            case "8":
                this.symbol = symbol;
                this.value = 8;
            case "9":
                this.symbol = symbol;
                this.value = 9;
            case "10":
                this.symbol = symbol;
                this.value = 10;
            case "Jack":
                this.symbol = symbol;
                this.value = 10;
            case "Queen":
                this.symbol = symbol;
                this.value = 10;
            case "King":
                this.symbol = symbol;
                this.value = 10;
            default:
                throw new Exception("Invalid Card Symbol");
        }

        switch (suit) {
            case "Heart":
                this.suit = suit;
            case "Club":
                this.suit = suit;
            case "Diamond":
                this.suit = suit;
            case "Spade":
                this.suit = suit;
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

    public void changeAceValue {
        this.value = 1;
    }
}