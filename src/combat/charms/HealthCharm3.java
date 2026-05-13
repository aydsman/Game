package combat.charms;

public class HealthCharm3 extends Charm {
    public HealthCharm3() {
        this(3);
    }

    public HealthCharm3(int tier) {
        super(tier);
        name = "Advanced Health Charm";
        description = "A basic charm that adds +25% to your health.";
        iconPath = "assets/items/charms/health/advanced_health_charm.png";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
        // For now, just ensures tier is set correctly
    }

    @Override
    public double getMaxHpBonusFraction() {
        return 0.25 * tierMultiplier();
    }
}

