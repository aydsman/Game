package combat.melee.hammers;

import combat.Melee;

public class Hammer extends Melee {

    public Hammer() {
        // base hammer stats: slow attack speed, high damage, medium range, high knockback
        attackSpeed = 0.8; // 0.8 attacks per second
        damage = 40;
        range = 50.0; // 50 pixel range
        knockback = 15.0;
    }

    public Hammer(int tier) {
        super(tier);
        // base hammer stats: slow attack speed, high damage, medium range, high knockback
        attackSpeed = 0.8; // 0.8 attacks per second
        damage = 40;
        range = 50.0; // 50 pixel range
        knockback = 15.0;
    }
}
