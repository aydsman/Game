package progression;

import testutil.TestSupport;

public final class PerkServiceTest {

    public static void run(TestSupport t) {
        t.checkEquals("level 1 has no perks", 0, PerkService.unlockedForLevel(1).size());
        t.checkEquals("level 4 has no perks", 0, PerkService.unlockedForLevel(4).size());
        t.checkEquals("level 5 unlocks Headhunter", 1, PerkService.unlockedForLevel(5).size());
        t.checkEquals("level 10 unlocks two", 2, PerkService.unlockedForLevel(10).size());
        t.checkEquals("level 15 unlocks all three", 3, PerkService.unlockedForLevel(15).size());

        t.checkEquals("headhunter crit", 0.08, PerkService.critChanceBonus(5), 0.0001);
        t.checkEquals("no reflex yet", 1.0, PerkService.dodgeCooldownMultiplier(5), 0.0001);
        t.checkEquals("iron reflex shortens dodge", 0.85, PerkService.dodgeCooldownMultiplier(10), 0.0001);
        t.checkEquals("lifesteal at 15", 0.04, PerkService.lifestealFraction(15), 0.0001);
        t.checkEquals("no lifesteal before 15", 0.0, PerkService.lifestealFraction(10), 0.0001);

        t.expectThrows("blank perk id rejected", IllegalArgumentException.class,
                () -> new Perk(" ", "X", "", 1, 0, 1.0, 0));
        t.expectThrows("dodge scale above 1 rejected", IllegalArgumentException.class,
                () -> new Perk("x", "X", "", 1, 0, 1.1, 0));
    }
}
