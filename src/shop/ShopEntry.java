package shop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShopEntry {
    private final String id;
    private final String title;
    private final String description;
    private final int priceCash;
    private final int priceGems;
    private final String iconPath;
    private final boolean bundle;
    protected final List<ShopReward> rewards = new ArrayList<>();

    protected ShopEntry(String id, String title, String description, int priceCash, int priceGems, String iconPath, boolean bundle) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priceCash = priceCash;
        this.priceGems = priceGems;
        this.iconPath = iconPath;
        this.bundle = bundle;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getPriceCash() { return priceCash; }
    public int getPriceGems() { return priceGems; }
    public String getIconPath() { return iconPath; }
    public boolean isBundle() { return bundle; }
    public List<ShopReward> getRewards() { return Collections.unmodifiableList(rewards); }

    public String getPriceLabel() {
        if (priceCash > 0 && priceGems > 0) {
            return "$" + priceCash + " + ◆" + priceGems;
        }
        if (priceGems > 0) {
            return "◆" + priceGems;
        }
        return "$" + priceCash;
    }
}
