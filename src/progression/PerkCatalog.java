package progression;

import java.util.List;

/**
 * The combat perks that unlock automatically with account level.
 *
 * <p>Kept as an explicit catalog so a later perk-choice screen can reuse the same definitions
 * without changing the combat pipeline that reads them.
 */
public final class PerkCatalog {

    public static final Perk HEADHUNTER = new Perk(
            "headhunter",
            "Headhunter",
            "+8% critical hit chance",
            5,
            0.08,
            1.0,
            0.0);

    public static final Perk IRON_REFLEX = new Perk(
            "iron_reflex",
            "Iron Reflex",
            "Dodge cooldown is 15% shorter",
            10,
            0.0,
            0.85,
            0.0);

    public static final Perk BLOODLETTER = new Perk(
            "bloodletter",
            "Bloodletter",
            "Heal for 4% of weapon damage dealt",
            15,
            0.0,
            1.0,
            0.04);

    private static final List<Perk> ALL = List.of(HEADHUNTER, IRON_REFLEX, BLOODLETTER);

    private PerkCatalog() {}

    public static List<Perk> all() {
        return ALL;
    }
}
