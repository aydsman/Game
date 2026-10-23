package currency;

public abstract class Currency {
    protected int amount;
    protected String name;
    protected String symbol;
    protected CurrencyType type;

    public Currency(String name, String symbol, CurrencyType type) {
        this.name = name;
        this.symbol = symbol;
        this.type = type;
        this.amount = 0;
    }

    public void add(int value) {
        if (value > 0) {
            this.amount += value;
        }
    }

    public boolean subtract(int value) {
        if (value <= 0) {
            return true;
        }
        if (this.amount >= value) {
            this.amount -= value;
            return true;
        }
        return false;
    }

    public boolean canAfford(int value) {
        return this.amount >= value;
    }

    public void reset() {
        if (type == CurrencyType.SESSION) {
            this.amount = 0;
        }
    }

    public void setAmount(int value) {
        this.amount = Math.max(0, value);
    }

    public int getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public CurrencyType getType() {
        return type;
    }

    public String getDisplayString() {
        return symbol + " " + amount;
    }
}
