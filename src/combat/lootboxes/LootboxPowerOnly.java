package combat.lootboxes;

public class LootboxPowerOnly extends LootBox {
    public LootboxPowerOnly() {
        super("Power Surge Crate");

        setTierProbability(1, 0.55);
        setTierProbability(2, 0.25);
        setTierProbability(3, 0.13);
        setTierProbability(4, 0.06);
        setTierProbability(5, 0.01);

        setItemTypeProbability("Power", 1.0);

        // Lower weights for stronger powers in the same tier.
        addItem("Power", "Earth", 1.0);
        addItem("Power", "Fire", 1.0);
        addItem("Power", "Light", 1.0);
        addItem("Power", "Lightning", 0.9);
        addItem("Power", "Water", 1.0);
        addItem("Power", "Magma", 0.85);
        addItem("Power", "EarthV2", 0.6);
        addItem("Power", "FireV2", 0.6);
        addItem("Power", "LightningV2", 0.55);
        addItem("Power", "MagmaV2", 0.55);
        addItem("Power", "WaterV2", 0.6);
        addItem("Power", "Infinity", 0.2);
        addItem("Power", "KingOfCurses", 0.22);
        addItem("Power", "RinneSharingan", 0.24);
    }
}
