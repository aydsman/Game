package combat.melee.scythes;

import combat.Melee;

public class Scythe extends Melee {

    public Scythe() {
        // base scythe stats: slow attack speed, high damage, long range
        attackSpeed = 0.6; // 0.6 attacks per second
        damage = 35;
        range = 80.0; // 80 pixel range
        knockback = 8.0;
    }

    public Scythe(int tier) {
        super(tier);
        // base scythe stats: slow attack speed, high damage, long range
        attackSpeed = 0.6; // 0.6 attacks per second
        damage = 35;
        range = 80.0; // 80 pixel range
        knockback = 8.0;
    }
}
