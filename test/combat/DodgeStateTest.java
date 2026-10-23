package combat;

import combat.defense.DodgeState;
import testutil.TestSupport;

public final class DodgeStateTest {

    public static void run(TestSupport t) {
        DodgeState dodge = new DodgeState(100, 100, 80, 400);
        t.check("ready at start", dodge.isReady(0));
        t.check("zero direction rejected", !dodge.tryStart(0, 0, 0));
        t.check("non-finite direction rejected", !dodge.tryStart(0, Double.NaN, 1));
        t.check("starts on a real direction", dodge.tryStart(0, 1, 0));
        t.check("invulnerable during dash", dodge.isInvulnerable(40));
        t.check("dashing during duration", dodge.isDashing(50));
        t.check("not ready while cooling", !dodge.isReady(50));

        int travelled = 0;
        travelled += dodge.sampleDisplacement(25)[0];
        travelled += dodge.sampleDisplacement(50)[0];
        travelled += dodge.sampleDisplacement(75)[0];
        travelled += dodge.sampleDisplacement(100)[0];
        travelled += dodge.sampleDisplacement(150)[0];
        t.check("travelled distance is the configured distance", travelled == 100);
        t.check("no leftover after the dash ends", dodge.sampleDisplacement(200)[0] == 0);
        t.check("ready after cooldown", dodge.isReady(400));

        dodge.setCooldownScale(0.5);
        dodge.tryStart(400, 1, 0);
        t.check("scaled cooldown is shorter", dodge.isReady(400 + 200));

        t.expectThrows("zero distance rejected", IllegalArgumentException.class,
                () -> new DodgeState(0, 100, 10, 10));
        t.expectThrows("zero duration rejected", IllegalArgumentException.class,
                () -> new DodgeState(10, 0, 10, 10));
    }
}
