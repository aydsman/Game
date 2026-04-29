package combat.melee.scythes;

import combat.Melee;

public class Scythe extends Melee {

    public Scythe() {
        // base scythe stats: slow attack speed, high damage, long range
        attackSpeed = 0.6; // 0.6 attacks per second
        damage = 15;
        range = 80.0; // 80 pixel range
        knockback = 8.0;
        name = "Scythe";
    }

    public Scythe(int tier) {
        super(tier);
        // base scythe stats: slow attack speed, high damage, long range
        attackSpeed = 0.6; // 0.6 attacks per second
        damage = 15;
        range = 80.0; // 80 pixel range
        knockback = 8.0;
        name = "Scythe";

        // Scythe: 110 degree arc, medium-slow swing, 0.7s delay, not automatic
        setSwingProperties(110.0, 0.08, 0.7, false);
        range = 100.0; // Increased range

        // Apply tier multipliers after setting base stats
        applyTierMultipliers();
    }
}
