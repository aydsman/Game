package combat.consumables;

import combat.Item;

public class Consumable extends Item {

    protected double healthRestorePercent = 0.0; // 0.0 to 1.0 representing 0% to 100%

    public Consumable() {
        super();
        name = "Consumable";
    }

    public Consumable(int tier) {
        super(tier);
        name = "Consumable";
    }

    public double getHealthRestorePercent() {
        return healthRestorePercent;
    }

    @Override
    public Consumable clone() {
        Consumable cloned = (Consumable) super.clone();
        cloned.healthRestorePercent = this.healthRestorePercent;
        return cloned;
    }
}
