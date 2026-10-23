package combat.charms;

public class SpeedCharm extends Charm {
    public SpeedCharm() {
        this(1);
    }

    public SpeedCharm(int tier) {
        super(tier);
        name = "Simple Speed Charm";
        description = "A basic charm that increases your movement speed by 5%.";
        iconPath = "assets/items/charms/speed/simple_speed_charm.png";
    }

    @Override
    public double getSpeedBonusFraction() {
        return 0.05 * tierMultiplier();
    }
}
