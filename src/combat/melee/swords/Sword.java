package combat.melee.swords;

import combat.Melee;

public class Sword extends Melee {

    public Sword() {
        // base sword stats: balanced attack speed, medium damage, medium range
        attackSpeed = 1.2; // 1.2 attacks per second
        damage = 20;
        range = 60.0; // 60 pixel range
        knockback = 3.0;
    }

    public Sword(int tier) {
        super(tier);
        // base sword stats: balanced attack speed, medium damage, medium range
        attackSpeed = 1.2; // 1.2 attacks per second
        damage = 20;
        range = 60.0; // 60 pixel range
        knockback = 3.0;
    }
}
