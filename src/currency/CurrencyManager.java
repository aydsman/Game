package currency;

public class CurrencyManager {
    private Gold gold;
    private Cash cash;
    private Gems gems;

    /** Session admin: spend stipend first; only {@code *Bank} persists to save. */
    private boolean adminWalletSplit;
    private int cashBank;
    private int cashStipend;
    private int gemsBank;
    private int gemsStipend;

    public CurrencyManager() {
        this.gold = new Gold();
        this.cash = new Cash();
        this.gems = new Gems();
    }

    public CurrencyManager(int startingGold, int startingCash, int startingGems) {
        this.gold = new Gold(startingGold);
        this.cash = new Cash(startingCash);
        this.gems = new Gems(startingGems);
    }

    public void resetSessionCurrencies() {
        gold.reset();
    }

    public Gold getGold() {
        return gold;
    }

    public Cash getCash() {
        return cash;
    }

    public Gems getGems() {
        return gems;
    }

    public void addGold(int amount) {
        gold.add(amount);
    }

    public void addCash(int amount) {
        if (amount <= 0) {
            return;
        }
        if (adminWalletSplit) {
            cashBank += amount;
            syncCashAmount();
        } else {
            cash.add(amount);
        }
    }

    public void addGems(int amount) {
        if (amount <= 0) {
            return;
        }
        if (adminWalletSplit) {
            gemsBank += amount;
            syncGemsAmount();
        } else {
            gems.add(amount);
        }
    }

    public boolean spendGold(int amount) {
        return gold.subtract(amount);
    }

    public boolean spendCash(int amount) {
        if (amount <= 0) {
            return true;
        }
        if (!adminWalletSplit) {
            return cash.subtract(amount);
        }
        if (cashBank + cashStipend < amount) {
            return false;
        }
        if (cashStipend >= amount) {
            cashStipend -= amount;
        } else {
            amount -= cashStipend;
            cashStipend = 0;
            cashBank -= amount;
        }
        syncCashAmount();
        return true;
    }

    public boolean spendGems(int amount) {
        if (amount <= 0) {
            return true;
        }
        if (!adminWalletSplit) {
            return gems.subtract(amount);
        }
        if (gemsBank + gemsStipend < amount) {
            return false;
        }
        if (gemsStipend >= amount) {
            gemsStipend -= amount;
        } else {
            amount -= gemsStipend;
            gemsStipend = 0;
            gemsBank -= amount;
        }
        syncGemsAmount();
        return true;
    }

    public int getPersistableCash() {
        return adminWalletSplit ? cashBank : cash.getAmount();
    }

    public int getPersistableGems() {
        return adminWalletSplit ? gemsBank : gems.getAmount();
    }

    /**
     * Inflate cash/gems toward targets; stipend is not written to disk (see {@link #getPersistableCash()}).
     */
    public void beginSessionAdminCurrencies(int targetCash, int targetGems) {
        adminWalletSplit = true;
        cashBank = cash.getAmount();
        cashStipend = Math.max(0, targetCash - cashBank);
        syncCashAmount();
        gemsBank = gems.getAmount();
        gemsStipend = Math.max(0, targetGems - gemsBank);
        syncGemsAmount();
    }

    public void endSessionAdminCurrencies() {
        if (!adminWalletSplit) {
            return;
        }
        cashStipend = 0;
        gemsStipend = 0;
        syncCashAmount();
        syncGemsAmount();
        adminWalletSplit = false;
    }

    public boolean isSessionAdminWalletSplit() {
        return adminWalletSplit;
    }

    private void syncCashAmount() {
        cash.setAmount(cashBank + cashStipend);
    }

    private void syncGemsAmount() {
        gems.setAmount(gemsBank + gemsStipend);
    }

    public String getCurrencyDisplay() {
        return gold.getDisplayString() + " | " + cash.getDisplayString() + " | " + gems.getDisplayString();
    }
}
