package combat.consumables;

public class Consumable2 extends Consumable {
    public Consumable2() {
        this(2);
    }

    public Consumable2(int tier) {
        super(tier);
        name = "Medium Health Potion";
        healthRestorePercent = 0.50; // Restores 50% of max HP
    }
}
