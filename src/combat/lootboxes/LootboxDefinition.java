package combat.lootboxes;

public class LootboxDefinition {
    private final String id;
    private final String displayName;
    private final int priceCash;
    private final int priceGems;
    private final int gemDuplicateCompensationCash;

    public LootboxDefinition(String id, String displayName, int priceCash, int priceGems, int gemDuplicateCompensationCash) {
        this.id = id;
        this.displayName = displayName;
        this.priceCash = Math.max(0, priceCash);
        this.priceGems = Math.max(0, priceGems);
        this.gemDuplicateCompensationCash = Math.max(0, gemDuplicateCompensationCash);
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public int getPriceCash() { return priceCash; }
    public int getPriceGems() { return priceGems; }

    public boolean isGemCrate() {
        return priceGems > 0 && priceCash == 0;
    }

    public int duplicateCashCompensation() {
        if (isGemCrate()) {
            return gemDuplicateCompensationCash;
        }
        if (priceCash > 0) {
            return Math.max(1, (int) Math.round(priceCash * 0.30));
        }
        return 0;
    }
}
