package combat.charms;

public class HealthCharm2 extends Charm {
    public HealthCharm2() {
        this(2);
    }

    public HealthCharm2(int tier) {
        super(tier);
        name = "Health Charm";
        description = "A basic charm that adds +15% to your health.";
        iconPath = "assets/items/charms/health/health_charm.png";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
        // For now, just ensures tier is set correctly
    }

    @Override
    public double getMaxHpBonusFraction() {
        return 0.15 * tierMultiplier();
    }
}

