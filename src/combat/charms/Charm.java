package combat.charms;

import combat.Item;

public class Charm extends Item {
    public Charm() {
        super();
        name = "Charm";
    }

    public Charm(int tier) {
        super(tier);
        name = "Charm";
    }
}
