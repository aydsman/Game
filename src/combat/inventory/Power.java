package combat.inventory;

import combat.Item;

public class Power extends Item {

    public Power() {
        super();
        this.name = "Power";
    }

    public Power(int tier) {
        super(tier);
        this.name = "Power";
    }

    @Override
    public Power clone() {
        Power cloned = new Power(this.tier);
        cloned.name = this.name;
        cloned.description = this.description;
        return cloned;
    }
}
