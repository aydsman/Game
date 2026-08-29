package combat.lootboxes;

public class LootboxDefinition {
    private final String id;
    private final String displayName;
    private final int priceCash;
    private final int priceGems;
    private final int duplicateCash;

    public LootboxDefinition(String id, String displayName, int priceCash, int priceGems, int duplicateCash) {
        this.id = id;
        this.displayName = displayName;
        this.priceCash = priceCash;
        this.priceGems = priceGems;
        this.duplicateCash = duplicateCash;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public int getPriceCash() { return priceCash; }
    public int getPriceGems() { return priceGems; }
    public int duplicateCashCompensation() { return duplicateCash; }
}
