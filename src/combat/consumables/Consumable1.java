package combat.consumables;

public class Consumable1 extends Consumable {
    public Consumable1() {
        this(1);
    }

    public Consumable1(int tier) {
        super(tier);
        name = "Small Health Potion";
        healthRestorePercent = 0.25; // Restores 25% of max HP
    }
}
