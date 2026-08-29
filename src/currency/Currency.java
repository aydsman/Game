package currency;

public class Currency {
    private int amount;

    public Currency(int amount) { this.amount = amount; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public void add(int delta) { amount += delta; }
    public boolean spend(int cost) {
        if (amount < cost) return false;
        amount -= cost;
        return true;
    }
}
