package combat.lootboxes;

import combat.powers.fruits.FruitPool;

public class FruitLootbox extends LootBox {
    public FruitLootbox() {
        super("Fruit Crate");

        setTierProbability(2, FruitPool.TIER2_CHANCE);
        setTierProbability(3, FruitPool.TIER3_CHANCE);
        setTierProbability(4, FruitPool.TIER4_CHANCE);
        setTierProbability(5, FruitPool.TIER5_CHANCE);

        setItemTypeProbability("Power", 1.0);

        for (FruitPool.FruitEntry fruit : FruitPool.all()) {
            addItem(fruit.tier(), "Power", fruit.itemId(), 1.0);
        }
    }

    @Override
    public combat.Item open() {
        FruitPool.FruitEntry fruit = FruitPool.roll(random);
        return createItem(fruit.itemId(), fruit.tier());
    }
}
