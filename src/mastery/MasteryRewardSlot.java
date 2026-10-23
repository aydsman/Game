package mastery;

public class MasteryRewardSlot {
    private final int level;
    private final String rewardKey;
    private final boolean configured;
    private final boolean claimed;

    public MasteryRewardSlot(int level, String rewardKey, boolean configured, boolean claimed) {
        this.level = level;
        this.rewardKey = rewardKey;
        this.configured = configured;
        this.claimed = claimed;
    }

    public int getLevel() { return level; }
    public String getRewardKey() { return rewardKey; }
    public boolean isConfigured() { return configured; }
    public boolean isClaimed() { return claimed; }
}
