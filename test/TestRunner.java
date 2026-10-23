import testutil.TestSupport;
import combat.CombatTuningTest;
import combat.DamageRollTest;
import combat.DodgeStateTest;
import combat.HitstopTest;
import combat.InvulnerabilityWindowTest;
import combat.MeleeComboTrackerTest;
import combat.MeleeIdentityTest;
import combat.ProjectileFalloffTest;
import combat.WornArmorTest;
import entity.TelegraphedSlamTest;
import progression.PerkServiceTest;

/**
 * Runs the combat unit suite. Compile with the game classes on the classpath, then:
 * {@code java -cp out;out-test TestRunner}
 */
public final class TestRunner {

    public static void main(String[] args) {
        TestSupport t = new TestSupport();
        InvulnerabilityWindowTest.run(t);
        DodgeStateTest.run(t);
        HitstopTest.run(t);
        DamageRollTest.run(t);
        MeleeComboTrackerTest.run(t);
        ProjectileFalloffTest.run(t);
        TelegraphedSlamTest.run(t);
        WornArmorTest.run(t);
        PerkServiceTest.run(t);
        MeleeIdentityTest.run(t);
        CombatTuningTest.run(t);

        System.out.println("Passed: " + t.getPassed());
        System.out.println("Failed: " + t.getFailed());
        if (t.getFailed() > 0) {
            System.exit(1);
        }
    }
}
