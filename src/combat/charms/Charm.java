package combat.charms;

import combat.Item;

public class Charm extends Item {

    public Charm() {
        super();
        this.name = "Charm";
    }

    public Charm(int tier) {
        super(tier);
        this.name = "Charm";
    }
}
