package combat.charms;

public class Charm1 extends Charm {
    public Charm1() {
        this(1);
    }

    public Charm1(int tier) {
        super(tier);
        name = "Simple Health Charm";
        description = "A basic charm that adds +10% to your health.";
        iconPath = "assets/items/charms/health/simple_health_charm.png";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
        // For now, just ensures tier is set correctly
    }

    @Override
    public double getMaxHpBonusFraction() {
        return 0.10 * tierMultiplier();
    }
}
