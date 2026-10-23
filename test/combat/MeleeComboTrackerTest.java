package combat;

import combat.melee.MeleeComboTracker;
import testutil.TestSupport;

public final class MeleeComboTrackerTest {

    public static void run(TestSupport t) {
        MeleeComboTracker combo = new MeleeComboTracker(3, 500);
        t.checkEquals("first swing is step 1", 1, combo.onSwingStart(0));
        t.check("whiff restarts", combo.onSwingStart(100) == 1);

        combo.onSwingStart(200);
        combo.onHitLanded(210);
        t.checkEquals("connecting swing advances", 2, combo.onSwingStart(300));
        combo.onHitLanded(310);
        t.checkEquals("third swing is the finisher", 3, combo.onSwingStart(400));
        t.check("finisher flag", combo.isFinisher());
        combo.onHitLanded(410);
        t.checkEquals("after finisher the chain wraps", 1, combo.onSwingStart(500));

        combo.onHitLanded(510);
        t.checkEquals("expired window restarts", 1, combo.onSwingStart(510 + 501));

        t.expectThrows("chain 0 rejected", IllegalArgumentException.class,
                () -> new MeleeComboTracker(0, 100));
        t.expectThrows("window 0 rejected", IllegalArgumentException.class,
                () -> new MeleeComboTracker(2, 0));
    }
}
