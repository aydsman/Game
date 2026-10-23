package combat.inventory;

import combat.Item;

public class Charm3 extends Item {

    public Charm3() {
        super();
        this.name = "Charm3";
    }

    public Charm3(int tier) {
        super(tier);
        this.name = "Charm3";
    }

    @Override
    public Charm3 clone() {
        Charm3 cloned = new Charm3(this.tier);
        cloned.name = this.name;
        cloned.description = this.description;
        return cloned;
    }
}
