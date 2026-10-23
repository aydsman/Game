package combat.inventory;

import combat.Item;

public class Charm2 extends Item {

    public Charm2() {
        super();
        this.name = "Charm2";
    }

    public Charm2(int tier) {
        super(tier);
        this.name = "Charm2";
    }

    @Override
    public Charm2 clone() {
        Charm2 cloned = new Charm2(this.tier);
        cloned.name = this.name;
        cloned.description = this.description;
        return cloned;
    }
}
