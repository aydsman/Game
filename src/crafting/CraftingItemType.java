package crafting;

public enum CraftingItemType {
    WEAPON("weapon"),
    CHARM("charm"),
    POWER("power"),
    SUMMON("summon"),
    CONSUMABLE("consumable");

    private final String saveKey;

    CraftingItemType(String saveKey) {
        this.saveKey = saveKey;
    }

    public String getSaveKey() {
        return saveKey;
    }
}
