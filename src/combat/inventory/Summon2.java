package combat.inventory;

import combat.Item;

public class Summon2 extends Item {

    public Summon2() {
        super();
        this.name = "Summon2";
    }

    public Summon2(int tier) {
        super(tier);
        this.name = "Summon2";
    }

    @Override
    public Summon2 clone() {
        Summon2 cloned = new Summon2(this.tier);
        cloned.name = this.name;
        cloned.description = this.description;
        return cloned;
    }
}
