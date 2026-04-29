package combat.consumables;

public class Consumable3 extends Consumable {
    public Consumable3() {
        this(3);
    }

    public Consumable3(int tier) {
        super(tier);
        name = "Large Health Potion";
        healthRestorePercent = 0.75; // Restores 75% of max HP
    }
}
