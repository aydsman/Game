package combat.charms;

public class HealthCharm2 extends Charm {
    public HealthCharm2() {
        this(1);
    }

    public HealthCharm2(int tier) {
        super(tier);
        name = "Health Charm";
        description = "A charm that increases your health by 15%.";
        iconPath = "assets/items/charms/health/health_charm.png";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
        // For now, just ensures tier is set correctly
    }
}

