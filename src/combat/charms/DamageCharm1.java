package combat.charms;

public class DamageCharm1 extends Charm {
    public DamageCharm1() { super(1); name = "DamageCharm1"; }
    @Override public double getDamageBonusFraction() { return 0.10 * tierMultiplier(); }
}
