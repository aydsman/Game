package combat.charms;

public class SpeedCharm3 extends Charm {
    public SpeedCharm3() {
        this(3);
    }

    public SpeedCharm3(int tier) {
        super(tier);
        name = "Advanced Speed Charm";
        description = "An advanced charm that increases your movement speed by 15%.";
        iconPath = "assets/items/charms/speed/advanced_speed_charm.png";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
        // For now, just ensures tier is set correctly
    }

    @Override
    public double getSpeedBonusFraction() {
        return 0.15 * tierMultiplier();
    }
}

