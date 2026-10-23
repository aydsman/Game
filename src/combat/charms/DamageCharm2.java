package combat.charms;

/**
 * Tier II damage charm.
 */
public class DamageCharm2 extends Charm {

    public DamageCharm2() {
        this(2);
    }

    public DamageCharm2(int tier) {
        super(tier);
        name = "Damage Charm";
        description = "Tier II — Increases damage dealt by a percentage of your base damage.";
        iconPath = "assets/items/charms/damage/damage_charm.png";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Reserved for future per-tier tuning
    }

    @Override
    public double getDamageBonusFraction() {
        return 0.14 * tierMultiplier();
    }
}
