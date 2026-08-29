package combat.charms;

public class DamageCharm3 extends Charm {
    public DamageCharm3() { super(3); name = "DamageCharm3"; }
    @Override public double getDamageBonusFraction() { return 0.20 * tierMultiplier(); }
}
