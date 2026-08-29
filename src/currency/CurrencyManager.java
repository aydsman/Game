package currency;

public class CurrencyManager {
    private final Currency gold = new Currency(0);
    private final Currency cash = new Currency(0);
    private final Currency gems = new Currency(0);
    private int sessionAdminCashBank = 0;
    private int sessionAdminGemsBank = 0;
    private boolean sessionAdmin = false;

    public CurrencyManager() {}

    public CurrencyManager(int gold, int cash, int gems) {
        this.gold.setAmount(gold);
        this.cash.setAmount(cash);
        this.gems.setAmount(gems);
    }

    public Currency getGold() { return gold; }
    public Currency getCash() { return cash; }
    public Currency getGems() { return gems; }

    public void addGold(int amount) { gold.add(amount); }
    public void addCash(int amount) { cash.add(amount); }
    public void addGems(int amount) { gems.add(amount); }

    public boolean spendCash(int amount) { return cash.spend(amount); }
    public boolean spendGems(int amount) { return gems.spend(amount); }

    public void beginSessionAdminCurrencies(int cashAmount, int gemAmount) {
        sessionAdmin = true;
        sessionAdminCashBank = cash.getAmount();
        sessionAdminGemsBank = gems.getAmount();
        cash.setAmount(cashAmount);
        gems.setAmount(gemAmount);
    }

    public void endSessionAdminCurrencies() {
        if (!sessionAdmin) return;
        cash.setAmount(sessionAdminCashBank);
        gems.setAmount(sessionAdminGemsBank);
        sessionAdmin = false;
    }

    public int getPersistableCash() { return cash.getAmount(); }
    public int getPersistableGems() { return gems.getAmount(); }
}
