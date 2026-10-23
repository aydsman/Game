package combat.charms;

/**
 * Tier I damage charm (fixed identity; loadout tier still scales via {@link #tierMultiplier()}).
 */
public class DamageCharm1 extends Charm {

    public DamageCharm1() {
        this(1);
    }

    public DamageCharm1(int tier) {
        super(tier);
        name = "Simple Damage Charm";
        description = "Tier I — Increases damage dealt by a percentage of your base damage.";
        iconPath = "assets/items/charms/damage/simple_damage_charm.png";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Reserved for future per-tier tuning
    }

    @Override
    public double getDamageBonusFraction() {
        return 0.08 * tierMultiplier();
    }
}
