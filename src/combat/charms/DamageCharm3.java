package combat.charms;

/**
 * Tier III damage charm.
 */
public class DamageCharm3 extends Charm {

    public DamageCharm3() {
        this(3);
    }

    public DamageCharm3(int tier) {
        super(tier);
        name = "Advanced Damage Charm";
        description = "Tier III — Greatly increases damage dealt as a percentage of your base damage.";
        iconPath = "assets/items/charms/damage/advanced_damage_charm.png";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Reserved for future per-tier tuning
    }

    @Override
    public double getDamageBonusFraction() {
        return 0.22 * tierMultiplier();
    }
}
