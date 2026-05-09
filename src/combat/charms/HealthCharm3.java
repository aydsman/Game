package combat.charms;

public class HealthCharm3 extends Charm {
    public HealthCharm3() {
        this(1);
    }

    public HealthCharm3(int tier) {
        super(tier);
        name = "Advanced Health Charm";
        description = "An advanced charm that increases your health by 25%.";
        iconPath = "assets/items/charms/health/advanced_health_charm.png";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
        // For now, just ensures tier is set correctly
    }
}

