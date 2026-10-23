package crafting;

public class CraftingOutput {
    private final CraftingItemType itemType;
    private final String itemId;
    private final int tier;
    private final int quantity;

    public CraftingOutput(CraftingItemType itemType, String itemId, int tier, int quantity) {
        this.itemType = itemType;
        this.itemId = itemId;
        this.tier = tier;
        this.quantity = Math.max(1, quantity);
    }

    public CraftingItemType getItemType() {
        return itemType;
    }

    public String getItemId() {
        return itemId;
    }

    public int getTier() {
        return tier;
    }

    public int getQuantity() {
        return quantity;
    }
}
