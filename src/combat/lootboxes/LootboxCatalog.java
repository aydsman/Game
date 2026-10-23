package combat.lootboxes;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LootboxCatalog {
    public static final String TEST_CRATE_ID = "test_crate";
    public static final String CLOTHES_CRATE_1_ID = "clothes_crate_1";
    public static final String SHOP_EXCLUSIVE_POWER_CRATE_ID = "shop_exclusive_power_crate";
    public static final String FRUIT_CRATE_ID = "fruit_crate";

    private static final Map<String, LootboxDefinition> DEFINITIONS = new LinkedHashMap<>();

    static {
        // Cash crate range is intentionally within 500..5000.
        register(new LootboxDefinition(TEST_CRATE_ID, "Test Crate", 1000, 0, 0));
        register(new LootboxDefinition(CLOTHES_CRATE_1_ID, "Clothes Crate 1", 0, 25, 900));
        register(new LootboxDefinition(SHOP_EXCLUSIVE_POWER_CRATE_ID, "Power Surge Crate", 0, 40, 1300));
        register(new LootboxDefinition(FRUIT_CRATE_ID, "Fruit Crate", 0, 55, 1600));
    }

    private LootboxCatalog() {}

    private static void register(LootboxDefinition definition) {
        DEFINITIONS.put(definition.getId(), definition);
    }

    public static Map<String, LootboxDefinition> all() {
        return new LinkedHashMap<>(DEFINITIONS);
    }

    public static LootboxDefinition get(String id) {
        return DEFINITIONS.get(id);
    }

    public static LootBox createLootbox(String id) {
        return switch (id) {
            case TEST_CRATE_ID -> new Lootbox1();
            case CLOTHES_CRATE_1_ID -> new ClothesLootbox1();
            case SHOP_EXCLUSIVE_POWER_CRATE_ID -> new LootboxPowerOnly();
            case FRUIT_CRATE_ID -> new FruitLootbox();
            default -> null;
        };
    }
}
