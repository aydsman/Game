package shop;

public class ShopReward {
    private final ShopRewardType type;
    private final String itemClassName;
    private final int tier;
    private final int quantity;
    private final int amount;
    private final String clothingId;
    private final String styleId;
    private final String lootboxId;

    private ShopReward(
            ShopRewardType type,
            String itemClassName,
            int tier,
            int quantity,
            int amount,
            String clothingId,
            String styleId,
            String lootboxId
    ) {
        this.type = type;
        this.itemClassName = itemClassName;
        this.tier = tier;
        this.quantity = quantity;
        this.amount = amount;
        this.clothingId = clothingId;
        this.styleId = styleId;
        this.lootboxId = lootboxId;
    }

    public static ShopReward item(String itemClassName, int tier, int quantity) {
        return new ShopReward(ShopRewardType.ITEM, itemClassName, tier, quantity, 0, null, null, null);
    }

    public static ShopReward lootboxToken(String lootboxId, int quantity) {
        return new ShopReward(ShopRewardType.LOOTBOX_TOKEN, null, 0, quantity, 0, null, null, lootboxId);
    }

    public static ShopReward unlockClothing(String clothingId) {
        return new ShopReward(ShopRewardType.CLOTHING_UNLOCK, null, 0, 0, 0, clothingId, null, null);
    }

    public static ShopReward unlockStyle(String clothingId, String styleId) {
        return new ShopReward(ShopRewardType.CLOTHING_STYLE_UNLOCK, null, 0, 0, 0, clothingId, styleId, null);
    }

    public static ShopReward cash(int amount) {
        return new ShopReward(ShopRewardType.CASH, null, 0, 0, amount, null, null, null);
    }

    public static ShopReward gems(int amount) {
        return new ShopReward(ShopRewardType.GEMS, null, 0, 0, amount, null, null, null);
    }

    public ShopRewardType getType() { return type; }
    public String getItemClassName() { return itemClassName; }
    public int getTier() { return tier; }
    public int getQuantity() { return quantity; }
    public int getAmount() { return amount; }
    public String getClothingId() { return clothingId; }
    public String getStyleId() { return styleId; }
    public String getLootboxId() { return lootboxId; }
}
