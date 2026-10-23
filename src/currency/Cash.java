package currency;

public class Cash extends Currency {
    public Cash() {
        super("Cash", "$", CurrencyType.PERMANENT);
    }

    public Cash(int startingAmount) {
        super("Cash", "$", CurrencyType.PERMANENT);
        this.amount = startingAmount;
    }
}
