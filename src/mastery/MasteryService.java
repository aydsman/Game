package mastery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import save.SaveData;
import save.SaveManager;

public final class MasteryService {
    private MasteryService() {}

    public static void grantWeaponDamageXP(String weaponName, double damageDealt) {
        if (damageDealt <= 0) {
            return;
        }
        int xp = (int) Math.max(1, Math.floor(damageDealt / 100.0 * MasteryConstants.WEAPON_DAMAGE_XP_PER_100));
        addWeaponMasteryXP(weaponName, xp);
    }

    public static void grantWeaponKillXP(String weaponName, int kills) {
        if (kills <= 0) {
            return;
        }
        addWeaponMasteryXP(weaponName, kills * MasteryConstants.WEAPON_KILL_XP);
    }

    public static void grantPowerDamageXP(String powerName, double damageDealt) {
        if (damageDealt <= 0) {
            return;
        }
        String powerId = MasteryIds.normalizePowerTypeId(powerName);
        if ("None".equals(powerId)) {
            return;
        }
        int xp = (int) Math.max(1, Math.floor(damageDealt / 100.0 * MasteryConstants.POWER_DAMAGE_XP_PER_100));
        addPowerMasteryXP(powerName, xp);
    }

    public static void grantPowerKillXP(String powerName, int kills) {
        if (kills <= 0) {
            return;
        }
        addPowerMasteryXP(powerName, kills * MasteryConstants.POWER_KILL_XP);
    }

    public static void grantPowerUseXP(String powerName, boolean utilityMove) {
        int xp = utilityMove ? MasteryConstants.POWER_UTILITY_USE_XP : MasteryConstants.POWER_USE_XP;
        addPowerMasteryXP(powerName, xp);
    }

    public static int getWeaponMasteryLevel(String weaponName) {
        SaveData data = SaveManager.load();
        String weaponTypeId = MasteryIds.normalizeWeaponTypeId(weaponName);
        int totalXp = data.getWeaponMasteryXpByType().getOrDefault(weaponTypeId, 0);
        return levelFromTotalXp(totalXp);
    }

    public static int getPowerMasteryLevel(String powerName) {
        SaveData data = SaveManager.load();
        String powerId = MasteryIds.normalizePowerTypeId(powerName);
        int totalXp = data.getPowerMasteryXpByType().getOrDefault(powerId, 0);
        return levelFromTotalXp(totalXp);
    }

    public static int getUnlockedPowerCount(String powerName, int maxPowerCountForType) {
        int level = getPowerMasteryLevel(powerName);
        int unlocked = 1;
        if (level >= MasteryConstants.POWER_2_UNLOCK_LEVEL) unlocked = 2;
        if (level >= MasteryConstants.POWER_3_UNLOCK_LEVEL) unlocked = 3;
        if (level >= MasteryConstants.POWER_4_UNLOCK_LEVEL) unlocked = 4;
        if (level >= MasteryConstants.POWER_5_UNLOCK_LEVEL) unlocked = 5;
        return Math.max(1, Math.min(unlocked, Math.max(1, maxPowerCountForType)));
    }

    public static List<MasteryRewardSlot> getWeaponRewardSlots(String weaponName) {
        return buildSlots(
                getWeaponMasteryLevel(weaponName),
                MasteryRewardCatalog.weaponRewardsByLevel(),
                getClaimedWeaponLevels(weaponName));
    }

    public static List<MasteryRewardSlot> getPowerRewardSlots(String powerName) {
        return buildSlots(
                getPowerMasteryLevel(powerName),
                MasteryRewardCatalog.powerRewardsByLevel(),
                getClaimedPowerLevels(powerName));
    }

    public static int getUnclaimedWeaponRewardCount(String weaponName) {
        return countUnclaimed(getWeaponMasteryLevel(weaponName), MasteryRewardCatalog.weaponRewardsByLevel(), getClaimedWeaponLevels(weaponName));
    }

    public static int getUnclaimedPowerRewardCount(String powerName) {
        return countUnclaimed(getPowerMasteryLevel(powerName), MasteryRewardCatalog.powerRewardsByLevel(), getClaimedPowerLevels(powerName));
    }

    public static boolean claimWeaponReward(String weaponName, int level) {
        if (!MasteryRewardCatalog.weaponRewardsByLevel().containsKey(level)) {
            return false;
        }
        SaveData data = SaveManager.load();
        String weaponTypeId = MasteryIds.normalizeWeaponTypeId(weaponName);
        int currentLevel = levelFromTotalXp(data.getWeaponMasteryXpByType().getOrDefault(weaponTypeId, 0));
        if (level > currentLevel) {
            return false;
        }
        Map<String, Set<Integer>> map = data.getWeaponMasteryClaimedLevelsByType();
        Set<Integer> claimed = map.computeIfAbsent(weaponTypeId, ignored -> new HashSet<>());
        if (claimed.contains(level)) {
            return false;
        }
        claimed.add(level);
        SaveManager.save(data);
        return true;
    }

    public static boolean claimPowerReward(String powerName, int level) {
        if (!MasteryRewardCatalog.powerRewardsByLevel().containsKey(level)) {
            return false;
        }
        SaveData data = SaveManager.load();
        String powerId = MasteryIds.normalizePowerTypeId(powerName);
        int currentLevel = levelFromTotalXp(data.getPowerMasteryXpByType().getOrDefault(powerId, 0));
        if (level > currentLevel) {
            return false;
        }
        Map<String, Set<Integer>> map = data.getPowerMasteryClaimedLevelsByType();
        Set<Integer> claimed = map.computeIfAbsent(powerId, ignored -> new HashSet<>());
        if (claimed.contains(level)) {
            return false;
        }
        claimed.add(level);
        SaveManager.save(data);
        return true;
    }

    public static void resetAllWeaponMasteries() {
        SaveData data = SaveManager.load();
        data.getWeaponMasteryXpByType().clear();
        data.getWeaponMasteryClaimedLevelsByType().clear();
        // Medals currently come from completing weapon mastery tracks.
        data.setMasteryMedals(0);
        SaveManager.save(data);
    }

    private static List<MasteryRewardSlot> buildSlots(int currentLevel, Map<Integer, String> rewardsByLevel, Set<Integer> claimedLevels) {
        List<MasteryRewardSlot> slots = new ArrayList<>();
        for (int level = 1; level <= MasteryConstants.MAX_LEVEL; level++) {
            String rewardKey = rewardsByLevel.get(level);
            boolean configured = rewardKey != null && !rewardKey.isBlank();
            boolean reached = level <= currentLevel;
            boolean claimed = reached && claimedLevels.contains(level);
            slots.add(new MasteryRewardSlot(level, rewardKey, configured, claimed));
        }
        return slots;
    }

    private static int countUnclaimed(int currentLevel, Map<Integer, String> rewardsByLevel, Set<Integer> claimedLevels) {
        int count = 0;
        for (Map.Entry<Integer, String> entry : rewardsByLevel.entrySet()) {
            if (entry.getKey() <= currentLevel && !claimedLevels.contains(entry.getKey())) {
                count++;
            }
        }
        return count;
    }

    private static Set<Integer> getClaimedWeaponLevels(String weaponName) {
        SaveData data = SaveManager.load();
        String weaponTypeId = MasteryIds.normalizeWeaponTypeId(weaponName);
        return data.getWeaponMasteryClaimedLevelsByType().getOrDefault(weaponTypeId, Set.of());
    }

    private static Set<Integer> getClaimedPowerLevels(String powerName) {
        SaveData data = SaveManager.load();
        String powerId = MasteryIds.normalizePowerTypeId(powerName);
        return data.getPowerMasteryClaimedLevelsByType().getOrDefault(powerId, Set.of());
    }

    private static void addWeaponMasteryXP(String weaponName, int xp) {
        if (xp <= 0) {
            return;
        }
        SaveData data = SaveManager.load();
        String weaponTypeId = MasteryIds.normalizeWeaponTypeId(weaponName);
        int oldXp = data.getWeaponMasteryXpByType().getOrDefault(weaponTypeId, 0);
        int oldLevel = levelFromTotalXp(oldXp);
        int newXp = oldXp + xp;
        data.getWeaponMasteryXpByType().put(weaponTypeId, newXp);

        int newLevel = levelFromTotalXp(newXp);
        if (oldLevel < MasteryConstants.MASTERY_MEDAL_LEVEL && newLevel >= MasteryConstants.MASTERY_MEDAL_LEVEL) {
            data.addMasteryMedals(1);
        }
        SaveManager.save(data);
    }

    private static void addPowerMasteryXP(String powerName, int xp) {
        if (xp <= 0) {
            return;
        }
        String powerId = MasteryIds.normalizePowerTypeId(powerName);
        if ("None".equals(powerId)) {
            return;
        }
        SaveData data = SaveManager.load();
        int oldXp = data.getPowerMasteryXpByType().getOrDefault(powerId, 0);
        int newXp = oldXp + xp;
        data.getPowerMasteryXpByType().put(powerId, newXp);
        SaveManager.save(data);
    }

    public static int levelFromTotalXp(int totalXp) {
        int xp = Math.max(0, totalXp);
        int level = 1;
        while (level < MasteryConstants.MAX_LEVEL) {
            int xpToNext = xpRequiredForLevel(level);
            if (xp < xpToNext) {
                break;
            }
            xp -= xpToNext;
            level++;
        }
        return level;
    }

    private static int xpRequiredForLevel(int currentLevel) {
        // Smoothly increasing curve intended as a placeholder until Part 3 balancing.
        return 60 + (currentLevel * 22);
    }
}
