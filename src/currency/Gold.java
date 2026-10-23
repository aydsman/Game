package currency;

public class Gold extends Currency {
    public Gold() {
        super("Gold", "G", CurrencyType.SESSION);
    }

    public Gold(int startingAmount) {
        super("Gold", "G", CurrencyType.SESSION);
        this.amount = startingAmount;
    }
}
