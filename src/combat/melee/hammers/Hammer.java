package combat.melee.hammers;

import combat.Melee;

public class Hammer extends Melee {

    public Hammer() {
        // base hammer stats: slow attack speed, high damage, medium range, high knockback
        attackSpeed = 0.8; // 0.8 attacks per second
        damage = 180;
        range = 50.0; // 50 pixel range
        knockback = 15.0;
        name = "Hammer";
    }

    public Hammer(int tier) {
        super(tier);
        // base hammer stats: slow attack speed, high damage, medium range, high knockback
        attackSpeed = 0.8; // 0.8 attacks per second
        damage = 180;
        range = 50.0; // 50 pixel range
        knockback = 15.0;
        name = "Hammer";

        // Hammer: 120 degree arc, slow swing, 0.8s delay, not automatic
        setSwingProperties(120.0, 0.06, 0.8, false);
        range = 75.0; // Increased range

        // Apply tier multipliers after setting base stats
        applyTierMultipliers();
    }
}
