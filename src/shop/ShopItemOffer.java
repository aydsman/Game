package shop;

public class ShopItemOffer extends ShopEntry {
    public ShopItemOffer(
            String id,
            String title,
            String description,
            int priceCash,
            int priceGems,
            String iconPath,
            ShopReward reward
    ) {
        super(id, title, description, priceCash, priceGems, iconPath, false);
        if (reward != null) {
            this.rewards.add(reward);
        }
    }
}
