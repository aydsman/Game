package combat;

public class Item implements Cloneable {

    protected String name;
    protected int tier; // 1-5 (I-V)
    protected String rarity; // Tier I, Tier II, Tier III, Tier IV, Tier V
    protected String description;
    protected int quantity = 1; // Stack quantity
    protected String iconPath; // Path to item icon image

    public Item() {
        this.tier = 1;
        this.rarity = "Tier I";
        this.description = "null";
    }

    public Item(int tier) {
        this();
        this.tier = tier;
        setRarityFromTier();
    }

    private void setRarityFromTier() {
        switch (tier) {
            case 1:
                rarity = "Tier I";
                break;
            case 2:
                rarity = "Tier II";
                break;
            case 3:
                rarity = "Tier III";
                break;
            case 4:
                rarity = "Tier IV";
                break;
            case 5:
                rarity = "Tier V";
                break;
            default:
                rarity = "Tier I";
        }
    }

    // Getters
    public String getName() { return name; }
    public int getTier() { return tier; }
    public String getRarity() { return rarity; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public String getIconPath() { return iconPath; }

    // Barrel-related methods (default values, can be overridden)
    public int getBarrelLength() { return 20; }
    public int getBarrelHeight() { return 8; }
    public String getBarrelColor() { return "gray"; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setTier(int tier) { this.tier = tier; setRarityFromTier(); }
    public void setDescription(String description) { this.description = description; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Clone method for creating item copies
    public Item clone() {
        try {
            Item cloned = (Item) super.clone();
            cloned.quantity = this.quantity;
            return cloned;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
