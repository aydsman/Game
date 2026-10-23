package combat;

import testutil.TestSupport;

public final class HitstopTest {

    public static void run(TestSupport t) {
        Hitstop hitstop = new Hitstop(100);
        t.check("idle", !hitstop.isFrozen(0));
        hitstop.request(1_000, 40);
        t.check("frozen after request", hitstop.isFrozen(1_020));
        t.check("thawed after request", !hitstop.isFrozen(1_040));

        hitstop.request(2_000, 500);
        t.checkEquals("request is clamped to max", 100, hitstop.getRemainingMs(2_000));

        hitstop.request(2_010, 20);
        t.check("short request does not cut an active freeze", hitstop.isFrozen(2_080));
        t.check("overlap cannot pass now+max of latest request", !hitstop.isFrozen(2_111));

        Hitstop disabled = new Hitstop(0);
        disabled.request(0, 50);
        t.check("max 0 disables hitstop", !disabled.isFrozen(1));

        t.expectThrows("negative max rejected", IllegalArgumentException.class, () -> new Hitstop(-1));
        hitstop.reset();
        t.check("reset clears freeze", !hitstop.isFrozen(10_000));
    }
}
