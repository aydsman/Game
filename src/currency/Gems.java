package currency;

public class Gems extends Currency {
    public Gems() {
        super("Gems", "◆", CurrencyType.PERMANENT);
    }

    public Gems(int startingAmount) {
        super("Gems", "◆", CurrencyType.PERMANENT);
        this.amount = startingAmount;
    }
}
