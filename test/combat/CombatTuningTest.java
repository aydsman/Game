package combat;

import java.util.Properties;
import testutil.TestSupport;

public final class CombatTuningTest {

    public static void run(TestSupport t) {
        CombatTuning defaults = CombatTuning.defaults();
        t.check("dodge distance is in range", defaults.getDodgeDistancePx() >= 40);
        t.check("hurt i-frames are non-negative", defaults.getHurtInvulnerabilityMs() >= 0);

        Properties over = new Properties();
        over.setProperty("dodge.distancePx", "99999");
        over.setProperty("hurt.iframeMs", "not-a-number");
        CombatTuning clamped = CombatTuning.fromProperties(over);
        t.checkEquals("out-of-range dodge is clamped to max",
                (long) CombatTuning.Key.DODGE_DISTANCE_PX.max(),
                clamped.getDodgeDistancePx());
        t.checkEquals("unparseable hurt i-frames fall back to default",
                defaults.getHurtInvulnerabilityMs(),
                clamped.getHurtInvulnerabilityMs());
    }
}
