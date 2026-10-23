package combat;

import java.awt.Color;
import testutil.TestSupport;

public final class ProjectileFalloffTest {

    public static void run(TestSupport t) {
        Projectile pellet = new Projectile(0, 0, Color.WHITE, 10, 0, 100, 3);
        t.checkEquals("no falloff is full damage", 100, pellet.getEffectiveDamage());

        pellet.configureFalloff(50, 150, 0.25);
        t.checkEquals("before start is full", 100, pellet.getEffectiveDamage());

        for (int i = 0; i < 5; i++) {
            pellet.update();
        }
        t.checkEquals("at start is still full", 100, pellet.getEffectiveDamage());

        for (int i = 0; i < 5; i++) {
            pellet.update();
        }
        t.checkEquals("mid falloff is halfway to the floor", 63, pellet.getEffectiveDamage());

        for (int i = 0; i < 10; i++) {
            pellet.update();
        }
        t.checkEquals("past end is the floor", 25, pellet.getEffectiveDamage());

        t.expectThrows("inverted window rejected", IllegalArgumentException.class,
                () -> pellet.configureFalloff(100, 50, 0.2));
        t.expectThrows("fraction above 1 rejected", IllegalArgumentException.class,
                () -> pellet.configureFalloff(0, 10, 1.5));
    }
}
