package skilltree;

import entity.Player;
import save.SaveData;
import save.SaveManager;

/**
 * Applies passive bonuses from the skill tree / skill points to the player.
 * Full skill-tree UI can expand on this without changing combat call sites.
 */
public final class SkillEffects {

    private SkillEffects() {}

    public static void applyToPlayer(Player player) {
        if (player == null) {
            return;
        }
        SaveData data = SaveManager.load();
        int skillPoints = data != null ? data.getSkillPoints() : 0;

        player.setXpMultiplier(1.0 + skillPoints * 0.02);
        player.applySkillBonuses(
            skillPoints * 8,
            1.0 + skillPoints * 0.01,
            1.0 + skillPoints * 0.005
        );
    }

    public static double getXpMultiplier() {
        SaveData data = SaveManager.load();
        int skillPoints = data != null ? data.getSkillPoints() : 0;
        return 1.0 + skillPoints * 0.02;
    }

    public static double getDamageMultiplier() {
        SaveData data = SaveManager.load();
        int skillPoints = data != null ? data.getSkillPoints() : 0;
        return 1.0 + skillPoints * 0.01;
    }

    public static double getSpeedMultiplier() {
        SaveData data = SaveManager.load();
        int skillPoints = data != null ? data.getSkillPoints() : 0;
        return 1.0 + skillPoints * 0.005;
    }

    public static int getMaxHpBonus() {
        SaveData data = SaveManager.load();
        int skillPoints = data != null ? data.getSkillPoints() : 0;
        return skillPoints * 8;
    }

    public static int getExtraCharmSlots() {
        SaveData data = SaveManager.load();
        int skillPoints = data != null ? data.getSkillPoints() : 0;
        if (skillPoints >= 20) {
            return 2;
        }
        if (skillPoints >= 10) {
            return 1;
        }
        return 0;
    }

    public static void refreshPlayer(Player player) {
        applyToPlayer(player);
    }
}
