package progression;

import save.SaveData;

import java.util.List;
import java.util.Map;

/**
 * Trackable condition behind a {@link Reward}. Progress is read from the save file,
 * so quests reflect real play rather than being freely claimable.
 */
public enum QuestObjective {

    /** No condition; claimable immediately (login-style rewards). */
    NONE {
        @Override
        public int progress(SaveData data) {
            return 1;
        }
    },
    REACH_LEVEL {
        @Override
        public int progress(SaveData data) {
            return data.getPlayerLevel();
        }
    },
    TOTAL_KILLS {
        @Override
        public int progress(SaveData data) {
            return data.getTotalKills();
        }
    },
    TOTAL_DAMAGE {
        @Override
        public int progress(SaveData data) {
            return data.getTotalDamageDealt();
        }
    },
    HIGHEST_WAVE {
        @Override
        public int progress(SaveData data) {
            return data.getHighestWave();
        }
    },
    RUNS_PLAYED {
        @Override
        public int progress(SaveData data) {
            return data.getGamesPlayed();
        }
    },
    ARENAS_UNLOCKED {
        @Override
        public int progress(SaveData data) {
            return data.getUnlockedArenas().size();
        }
    },
    RECIPES_DISCOVERED {
        @Override
        public int progress(SaveData data) {
            return data.getDiscoveredCraftingRecipeIds().size();
        }
    },
    MASTERY_MEDALS {
        @Override
        public int progress(SaveData data) {
            return data.getMasteryMedals();
        }
    },
    ITEMS_COLLECTED {
        @Override
        public int progress(SaveData data) {
            return countTiers(data.getUnlockedWeapons())
                    + countTiers(data.getUnlockedCharms())
                    + countTiers(data.getUnlockedPowers())
                    + countTiers(data.getUnlockedSummons())
                    + countTiers(data.getUnlockedConsumables());
        }
    };

    public abstract int progress(SaveData data);

    private static int countTiers(Map<String, List<Integer>> unlocked) {
        if (unlocked == null) {
            return 0;
        }
        int total = 0;
        for (List<Integer> tiers : unlocked.values()) {
            total += (tiers == null ? 0 : tiers.size());
        }
        return total;
    }
}
