package combat;

import testutil.TestSupport;

public final class DamageRollTest {

    public static void run(TestSupport t) {
        DamageRoll missChance = DamageRoll.of(10, 1.0, 0.0, 2.0, 0.0);
        t.check("zero chance never crits", !missChance.isCritical());
        t.checkEquals("base amount", 10, missChance.getAmount());

        DamageRoll crit = DamageRoll.of(10, 1.0, 1.0, 2.0, 0.0);
        t.check("full chance always crits", crit.isCritical());
        t.checkEquals("crit doubles", 20, crit.getAmount());

        DamageRoll zero = DamageRoll.of(0, 1.0, 1.0, 2.0, 0.0);
        t.checkEquals("non-positive base is zero", 0, zero.getAmount());
        t.check("non-positive base is not a crit", !zero.isCritical());

        DamageRoll nan = DamageRoll.of(Double.NaN, 1.0, 1.0, 2.0, 0.0);
        t.checkEquals("NaN base is zero", 0, nan.getAmount());

        DamageRoll tiny = DamageRoll.of(0.1, 1.0, 0.0, 2.0, 0.0);
        t.checkEquals("sub-one hit rounds up to 1", 1, tiny.getAmount());

        t.check("sample at chance is not a crit", !DamageRoll.rollsCritical(0.25, 0.25));
        t.check("sample below chance is a crit", DamageRoll.rollsCritical(0.25, 0.24));
        t.check("NaN sample is not a crit", !DamageRoll.rollsCritical(1.0, Double.NaN));

        DamageRoll scaled = crit.scaled(1.45);
        t.check("scaled keeps crit flag", scaled.isCritical());
        t.checkEquals("scaled amount", 29, scaled.getAmount());
    }
}
