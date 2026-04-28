package combat.melee.maces;

import combat.Melee;

public class Mace extends Melee {

    public Mace() {
        // base mace stats: medium attack speed, medium-high damage, medium range, high knockback
        attackSpeed = 1.0; // 1 attack per second
        damage = 30;
        range = 45.0; // 45 pixel range
        knockback = 12.0;
        name = "Mace";
    }

    public Mace(int tier) {
        super(tier);
        // base mace stats: medium attack speed, medium-high damage, medium range, high knockback
        attackSpeed = 1.0; // 1 attack per second
        damage = 30;
        range = 45.0; // 45 pixel range
        knockback = 12.0;
        name = "Mace";

        // Apply tier multipliers after setting base stats
        applyTierMultipliers();
    }
}
