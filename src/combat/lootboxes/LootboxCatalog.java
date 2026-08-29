package combat.lootboxes;

import java.util.HashMap;
import java.util.Map;

public final class LootboxCatalog {
    public static final String TEST_CRATE_ID = "test_crate";
    public static final String CLOTHES_CRATE_1_ID = "clothes_crate_1";
    public static final String SHOP_EXCLUSIVE_POWER_CRATE_ID = "power_surge_crate";
    public static final String FRUIT_CRATE_ID = "fruit_crate";

    private static final Map<String, LootboxDefinition> DEFINITIONS = new HashMap<>();

    static {
        DEFINITIONS.put(TEST_CRATE_ID, new LootboxDefinition(TEST_CRATE_ID, "Test Crate", 100, 0, 25));
        DEFINITIONS.put(CLOTHES_CRATE_1_ID, new LootboxDefinition(CLOTHES_CRATE_1_ID, "Clothes Crate 1", 200, 1, 40));
        DEFINITIONS.put(SHOP_EXCLUSIVE_POWER_CRATE_ID, new LootboxDefinition(SHOP_EXCLUSIVE_POWER_CRATE_ID, "Power Surge Crate", 500, 5, 75));
        DEFINITIONS.put(FRUIT_CRATE_ID, new LootboxDefinition(FRUIT_CRATE_ID, "Fruit Crate", 300, 2, 50));
    }

    public static LootboxDefinition get(String id) { return DEFINITIONS.get(id); }

    public static LootBox createLootbox(String id) {
        if (TEST_CRATE_ID.equals(id)) return new Lootbox1();
        if (CLOTHES_CRATE_1_ID.equals(id)) return new ClothesLootbox1();
        if (SHOP_EXCLUSIVE_POWER_CRATE_ID.equals(id)) return new LootboxPowerOnly();
        if (FRUIT_CRATE_ID.equals(id)) return new FruitLootbox();
        return new Lootbox1();
    }
}
