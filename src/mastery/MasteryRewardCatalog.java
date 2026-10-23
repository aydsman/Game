package mastery;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MasteryRewardCatalog {
    private MasteryRewardCatalog() {}

    // Intentionally empty for now. Part 3 will fill concrete rewards.
    private static final Map<Integer, String> WEAPON_REWARDS_BY_LEVEL = new HashMap<>();
    private static final Map<Integer, String> POWER_REWARDS_BY_LEVEL = new HashMap<>();

    public static Map<Integer, String> weaponRewardsByLevel() {
        return Collections.unmodifiableMap(WEAPON_REWARDS_BY_LEVEL);
    }

    public static Map<Integer, String> powerRewardsByLevel() {
        return Collections.unmodifiableMap(POWER_REWARDS_BY_LEVEL);
    }
}
