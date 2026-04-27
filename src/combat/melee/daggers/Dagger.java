package combat.melee.daggers;

import combat.Melee;

public class Dagger extends Melee {

    public Dagger() {
        // base dagger stats: very fast attack speed, low damage, short range
        attackSpeed = 2.5; // 2.5 attacks per second
        damage = 12;
        range = 30.0; // 30 pixel range
        knockback = 1.0;
    }

    public Dagger(int tier) {
        super(tier);
        // base dagger stats: very fast attack speed, low damage, short range
        attackSpeed = 2.5; // 2.5 attacks per second
        damage = 12;
        range = 30.0; // 30 pixel range
        knockback = 1.0;
    }
}
