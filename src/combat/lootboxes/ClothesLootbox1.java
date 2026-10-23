package combat.lootboxes;

public class ClothesLootbox1 extends ClothingLootBox {

    public ClothesLootbox1() {
        super("Clothes Crate 1");

        // Tier probabilities
        setTierProbability(1, 0.50);
        setTierProbability(2, 0.25);
        setTierProbability(3, 0.15);
        setTierProbability(4, 0.08);
        setTierProbability(5, 0.02);

        // Only clothing
        setItemTypeProbability("Clothing", 1.0);

        // Tier 1 — excludes TShirt and Shorts (default unlocked)
        addItem(1, "Clothing", "LongSleeve");
        addItem(1, "Clothing", "TankTop");
        addItem(1, "Clothing", "BasicPants");
        addItem(1, "Clothing", "Headband");
        addItem(1, "Clothing", "Glasses");

        // Tier 2
        addItem(2, "Clothing", "BoxyTee");
        addItem(2, "Clothing", "Hoodie");
        addItem(2, "Clothing", "Jorts");
        addItem(2, "Clothing", "Sweatpants");
        addItem(2, "Clothing", "NinjaHeadband");
        addItem(2, "Clothing", "Shades");

        // Tier 3
        addItem(3, "Clothing", "BaggyJeans");
        addItem(3, "Clothing", "SwimShorts");
        addItem(3, "Clothing", "SuitTop");
        addItem(3, "Clothing", "SuitBottom");
        addItem(3, "Clothing", "Fedora");
        addItem(3, "Clothing", "Aviators");

        // Tier 4
        addItem(4, "Clothing", "Crown");
        addItem(4, "Clothing", "Cape");

        // Tier 5
        addItem(5, "Clothing", "AllSeeingEye");
    }

    /**
     * Define which styles each clothing item can drop from THIS lootbox
     * Only returns styles added via addStyle() - NOT default or unlockable styles
     */
    @Override
    protected java.util.List<String> getAllowedStylesForItem(String itemName, java.util.List<String> allStyles) {
        return switch (itemName) {
            // Tier 1
            case "LongSleeve" -> java.util.List.of("White", "Black");
            case "TankTop" -> java.util.List.of("Black", "White", "Green");
            case "BasicPants" -> java.util.List.of("Gray");
            case "Headband" -> java.util.List.of("White", "Red", "Blue");
            case "Glasses" -> java.util.List.of("Silver");

            // Tier 2
            case "BoxyTee" -> java.util.List.of("Black", "Gray", "Navy");
            case "Hoodie" -> java.util.List.of("Gray", "Black", "White", "Navy");
            case "Jorts" -> java.util.List.of("Light Wash", "Dark");
            case "Sweatpants" -> java.util.List.of("Gray", "Black", "Navy");
            case "NinjaHeadband" -> java.util.List.of("Rain", "Mist", "Sound");
            case "Shades" -> java.util.List.of("Black");

            // Tier 3
            case "BaggyJeans" -> java.util.List.of("Gray", "Dark Wash", "Dark", "Beige");
            case "SwimShorts" -> java.util.List.of("Tropical", "Striped Red", "Palm Trees", "Striped Blue");
            case "SuitTop" -> java.util.List.of("Black", "White");
            case "SuitBottom" -> java.util.List.of("Black", "White");
            case "Fedora" -> java.util.List.of("White");
            case "Aviators" -> java.util.List.of("Silver");

            // Tier 4
            case "Crown" -> java.util.List.of("Gold");
            case "Cape" -> java.util.List.of("Default", "Dark");

            // Tier 5
            case "AllSeeingEye" -> java.util.List.of("Gold");

            // Default: shouldn't reach here for items in this lootbox
            default -> java.util.List.of();
        };
    }
}