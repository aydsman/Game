package shop;

public class ShopBundle extends ShopEntry {
    public ShopBundle(
            String id,
            String title,
            String description,
            int priceCash,
            int priceGems,
            String iconPath
    ) {
        super(id, title, description, priceCash, priceGems, iconPath, true);
    }

    public ShopBundle add(ShopReward reward) {
        this.rewards.add(reward);
        return this;
    }
}
