package combat.charms;

public class SpeedCharm extends Charm {
    public SpeedCharm() { super(1); name = "SpeedCharm"; description = "Simple speed charm"; }
    @Override public double getSpeedBonusFraction() { return 0.05 * tierMultiplier(); }
}
