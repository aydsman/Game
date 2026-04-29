package combat;

public class Item implements Cloneable {

    protected String name;
    protected int tier; // 1-5 (I-V)
    protected String rarity; // Tier I, Tier II, Tier III, Tier IV, Tier V
    protected String description;

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

    // Barrel-related methods (default values, can be overridden)
    public int getBarrelLength() { return 20; }
    public int getBarrelHeight() { return 8; }
    public String getBarrelColor() { return "gray"; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }

    // Clone method for creating item copies
    public Item clone() {
        try {
            return (Item) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
