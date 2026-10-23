package combat;

import combat.melee.daggers.Dagger;
import testutil.TestSupport;
import combat.melee.hammers.Hammer;

public final class MeleeIdentityTest {

    public static void run(TestSupport t) {
        CombatTuning tuning = CombatTuning.defaults();
        Dagger dagger = new Dagger(1);
        Hammer hammer = new Hammer(1);

        t.check("dagger is the combo weapon", dagger.hasCombo());
        t.check("hammer is not a combo weapon", !hammer.hasCombo());
        t.checkEquals("hammer stuns", 550, hammer.getOnHitStunMs());
        t.checkEquals("dagger does not stun", 0, dagger.getOnHitStunMs());
        t.check("hammer knockback exceeds dagger",
                hammer.getKnockbackPixels(tuning) > dagger.getKnockbackPixels(tuning));
        t.checkEquals("dagger combo is three hits", 3, dagger.getComboChainLength());
    }
}
