package entity;

import entity.attack.TelegraphedSlam;
import testutil.TestSupport;

public final class TelegraphedSlamTest {

    public static void run(TestSupport t) {
        TelegraphedSlam slam = new TelegraphedSlam(100, 20, 30, 400, 50, 2.0);
        t.check("idle at start", slam.getPhase(0) == TelegraphedSlam.Phase.IDLE);
        t.check("starts", slam.tryBegin(0, 10, 20));
        t.check("second start blocked", !slam.tryBegin(10, 0, 0));
        t.check("windup", slam.getPhase(50) == TelegraphedSlam.Phase.WINDUP);
        t.check("impact not ready in windup", !slam.consumeImpact(50));
        t.check("active", slam.getPhase(110) == TelegraphedSlam.Phase.ACTIVE);
        t.check("impact fires once", slam.consumeImpact(110));
        t.check("impact does not repeat", !slam.consumeImpact(115));
        t.check("covers the anchor", slam.covers(10, 20));
        t.check("does not cover outside the radius", !slam.covers(10 + 51, 20));
        t.check("recovery", slam.getPhase(130) == TelegraphedSlam.Phase.RECOVERY);
        t.check("idle after recovery", slam.getPhase(160) == TelegraphedSlam.Phase.IDLE);
        t.check("cooldown still running", slam.getCooldownRemainingMs(200) > 0);
        t.check("ready after cooldown", slam.tryBegin(400, 0, 0));

        TelegraphedSlam hitch = new TelegraphedSlam(100, 20, 30, 400, 50, 2.0);
        hitch.tryBegin(0, 0, 0);
        t.check("latched impact still resolves after a skipped active window", hitch.consumeImpact(200));

        t.expectThrows("zero windup rejected", IllegalArgumentException.class,
                () -> new TelegraphedSlam(0, 20, 0, 0, 10, 1.0));
    }
}
