package combat.lootboxes;

import java.util.List;

public class ClothesLootbox1 extends ClothingLootBox {
    public ClothesLootbox1() {
        super("Clothes Crate 1");
        setTierProbability(1, 0.5);
        setTierProbability(2, 0.3);
        setTierProbability(3, 0.2);
        setItemTypeProbability("Clothing", 1.0);
        addItem(1, "Clothing", "TShirt");
        addItem(1, "Clothing", "Shorts");
        addItem(2, "Clothing", "Hoodie");
        addItem(2, "Clothing", "BasicPants");
    }

    @Override
    protected List<String> getAllowedStylesForItem(String itemName, List<String> allStyles) {
        return allStyles;
    }
}
