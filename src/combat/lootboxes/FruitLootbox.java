package combat.lootboxes;

import combat.powers.fruits.BombFruit;
import combat.powers.fruits.GasFruit;
import combat.powers.fruits.StringFruit;
import combat.powers.fruits.SandFruit;

public class FruitLootbox extends LootBox {
    public FruitLootbox() {
        super("Fruit Crate");
        setTierProbability(2, 0.4);
        setTierProbability(3, 0.35);
        setTierProbability(4, 0.25);
        setItemTypeProbability("Power", 1.0);
        addItem(2, "Power", "BombFruit");
        addItem(2, "Power", "GasFruit");
        addItem(2, "Power", "StringFruit");
        addItem(2, "Power", "SandFruit");
    }
}
