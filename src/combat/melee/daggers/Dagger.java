package combat.melee.daggers;

import combat.Melee;

/**
 * Fast, low-damage, short-reach weapon whose identity is its combo: the dagger is the only weapon
 * that rewards staying on a target, with a third-hit finisher that hits harder and reaches wider.
 */
public class Dagger extends Melee {

    /** Swings needed to reach the finisher. */
    private static final int COMBO_CHAIN_LENGTH = 3;
    private static final double FINISHER_DAMAGE_MULTIPLIER = 1.45;
    private static final double FINISHER_KNOCKBACK_MULTIPLIER = 1.9;
    private static final double FINISHER_ARC_BONUS_DEGREES = 35.0;

    public Dagger() {
        // base dagger stats: very fast attack speed, low damage, short range
        attackSpeed = 2.5; // 2.5 attacks per second
        damage = 50;
        range = 30.0; // 30 pixel range
        knockback = 1.0;
        name = "Dagger";

        applyDaggerCombo();
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

        applyDaggerCombo();

        // Apply tier multipliers after setting base stats
        applyTierMultipliers();
    }

    private void applyDaggerCombo() {
        setComboProperties(COMBO_CHAIN_LENGTH,
                FINISHER_DAMAGE_MULTIPLIER,
                FINISHER_KNOCKBACK_MULTIPLIER,
                FINISHER_ARC_BONUS_DEGREES);
    }
}
