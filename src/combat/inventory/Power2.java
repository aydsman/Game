package combat.inventory;

import combat.Item;

public class Power2 extends Item {

    public Power2() {
        super();
        this.name = "Power2";
    }

    public Power2(int tier) {
        super(tier);
        this.name = "Power2";
    }

    @Override
    public Power2 clone() {
        Power2 cloned = new Power2(this.tier);
        cloned.name = this.name;
        cloned.description = this.description;
        return cloned;
    }
}
