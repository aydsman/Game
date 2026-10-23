package combat.inventory;

import combat.Item;

public class Summon extends Item {

    public Summon() {
        super();
        this.name = "Summon";
    }

    public Summon(int tier) {
        super(tier);
        this.name = "Summon";
    }

    @Override
    public Summon clone() {
        Summon cloned = new Summon(this.tier);
        cloned.name = this.name;
        cloned.description = this.description;
        return cloned;
    }
}
