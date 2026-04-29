package combat.melee.swords;

import combat.Melee;

public class Sword extends Melee {

    public Sword() {
        // base sword stats: balanced attack speed, medium damage, medium range
        attackSpeed = 1.2; // 1.2 attacks per second
        damage = 8;
        range = 60.0; // 60 pixel range
        knockback = 3.0;
        name = "Sword";
    }

    public Sword(int tier) {
        super(tier);
        // base sword stats: balanced attack speed, medium damage, medium range
        attackSpeed = 1.2; // 1.2 attacks per second
        damage = 8;
        range = 60.0; // 60 pixel range
        knockback = 3.0;
        name = "Sword";

        // Sword: 90 degree arc, medium speed, 0.5s delay, not automatic
        setSwingProperties(90.0, 0.12, 0.5, false);
        range = 80.0; // Increased range

        // Apply tier multipliers after setting base stats
        applyTierMultipliers();
    }
}
