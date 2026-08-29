package combat.charms;

public class DamageCharm2 extends Charm {
    public DamageCharm2() { super(2); name = "DamageCharm2"; }
    @Override public double getDamageBonusFraction() { return 0.15 * tierMultiplier(); }
}
