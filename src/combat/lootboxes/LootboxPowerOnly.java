package combat.lootboxes;

public class LootboxPowerOnly extends LootBox {
    public LootboxPowerOnly() {
        super("Power Surge Crate");
        setTierProbability(3, 0.3);
        setTierProbability(4, 0.4);
        setTierProbability(5, 0.3);
        setItemTypeProbability("Power", 1.0);
        addItem("Power", "Fire");
        addItem("Power", "Lightning");
        addItem("Power", "Infinity");
        addItem("Power", "KingOfCurses");
    }
}
