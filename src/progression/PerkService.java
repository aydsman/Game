package progression;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves which perks are active for a given account level and folds them into the combat numbers
 * the player reads (crit, dodge cooldown, lifesteal).
 */
public final class PerkService {

    private PerkService() {}

    /**
     * Perks whose unlock level is at or below {@code playerLevel}, in catalog order.
     *
     * @param playerLevel persistent account level; values below 1 are treated as 1
     */
    public static List<Perk> unlockedForLevel(int playerLevel) {
        int level = Math.max(1, playerLevel);
        List<Perk> unlocked = new ArrayList<>();
        for (Perk perk : PerkCatalog.all()) {
            if (perk.getUnlockLevel() <= level) {
                unlocked.add(perk);
            }
        }
        return List.copyOf(unlocked);
    }

    public static double critChanceBonus(int playerLevel) {
        double total = 0.0;
        for (Perk perk : unlockedForLevel(playerLevel)) {
            total += perk.getCritChanceBonus();
        }
        return total;
    }

    /**
     * Multiplier applied to dodge cooldown. Multiple reflex perks multiply together; the result is
     * never allowed below 0.5 so a stack cannot erase the commitment a dodge is supposed to have.
     */
    public static double dodgeCooldownMultiplier(int playerLevel) {
        double total = 1.0;
        for (Perk perk : unlockedForLevel(playerLevel)) {
            total *= perk.getDodgeCooldownMultiplier();
        }
        return Math.max(0.5, total);
    }

    public static double lifestealFraction(int playerLevel) {
        double total = 0.0;
        for (Perk perk : unlockedForLevel(playerLevel)) {
            total += perk.getLifestealFraction();
        }
        return Math.min(0.25, total);
    }
}
