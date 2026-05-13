package combat.charms;

public class SpeedCharm2 extends Charm {
    public SpeedCharm2() {
        this(2);
    }

    public SpeedCharm2(int tier) {
        super(tier);
        name = "Speed Charm";
        description = "A charm that increases your movement speed by 10%.";
        iconPath = "assets/items/charms/speed/speed_charm.png";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
        // For now, just ensures tier is set correctly
    }

    @Override
    public double getSpeedBonusFraction() {
        return 0.10 * tierMultiplier();
    }
}

