package combat.melee.daggers;

import combat.Melee;

public class Dagger extends Melee {

    public Dagger() {
        // base dagger stats: very fast attack speed, low damage, short range
        attackSpeed = 2.5; // 2.5 attacks per second
        damage = 50;
        range = 30.0; // 30 pixel range
        knockback = 1.0;
        name = "Dagger";
    }

    public Dagger(int tier) {
        super(tier);
        // base dagger stats: very fast attack speed, low damage, short range
        attackSpeed = 2.5; // 2.5 attacks per second
        damage = 50;
        range = 30.0; // 30 pixel range
        knockback = 1.0;
        name = "Dagger";

        // Dagger: 45 degree arc, very fast, 0.3s delay, automatic
        setSwingProperties(45.0, 0.2, 0.3, true);
        range = 45.0; // Increased range

        // Apply tier multipliers after setting base stats
        applyTierMultipliers();
    }
}
