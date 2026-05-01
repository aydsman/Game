package combat.melee.maces;

import combat.Melee;

public class Mace extends Melee {

    public Mace() {
        // base mace stats: medium attack speed, medium-high damage, medium range, high knockback
        attackSpeed = 1.0; // 1 attack per second
        damage = 120;
        range = 45.0; // 45 pixel range
        knockback = 12.0;
        name = "Mace";
    }

    public Mace(int tier) {
        super(tier);
        // base mace stats: medium attack speed, medium-high damage, medium range, high knockback
        attackSpeed = 1.0; // 1 attack per second
        damage = 120;
        range = 45.0; // 45 pixel range
        knockback = 12.0;
        name = "Mace";

        // Mace: 100 degree arc, medium speed, 0.6s delay, not automatic
        setSwingProperties(100.0, 0.1, 0.6, false);
        range = 70.0; // Increased range

        // Apply tier multipliers after setting base stats
        applyTierMultipliers();
    }
}
