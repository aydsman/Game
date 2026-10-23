package progression;

import save.SaveData;

/**
 * Represents a single reward from the daily/weekly/seasonal reward system.
 * Each reward is gated behind a {@link QuestObjective} tracked against the save file.
 */
public class Reward {
    
    public enum RewardType {
        DAILY,
        WEEKLY,
        SEASONAL
    }
    
    private String id;
    private String title;
    private String description;
    private int goldAmount;
    private int gemAmount;
    private int xpAmount;
    private RewardType type;
    private boolean claimed;
    private long expiresAt; // Timestamp when reward expires
    private QuestObjective objective;
    private int goal;
    
    public Reward(String id, String title, String description, int gold, int gems, int xp, RewardType type, long expiresAt) {
        this(id, title, description, gold, gems, xp, type, expiresAt, QuestObjective.NONE, 1);
    }

    public Reward(String id, String title, String description, int gold, int gems, int xp, RewardType type,
                  long expiresAt, QuestObjective objective, int goal) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.goldAmount = gold;
        this.gemAmount = gems;
        this.xpAmount = xp;
        this.type = type;
        this.claimed = false;
        this.expiresAt = expiresAt;
        this.objective = (objective == null) ? QuestObjective.NONE : objective;
        this.goal = Math.max(1, goal);
    }
    
    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getGoldAmount() { return goldAmount; }
    public int getGemAmount() { return gemAmount; }
    public int getXpAmount() { return xpAmount; }
    public RewardType getType() { return type; }
    public boolean isClaimed() { return claimed; }
    public long getExpiresAt() { return expiresAt; }
    public QuestObjective getObjective() { return objective; }
    public int getGoal() { return goal; }
    
    // Setters
    public void setClaimed(boolean claimed) { this.claimed = claimed; }

    /** Current progress, capped at the goal for display purposes. */
    public int getProgress(SaveData data) {
        return Math.min(goal, Math.max(0, objective.progress(data)));
    }

    public boolean isComplete(SaveData data) {
        return objective.progress(data) >= goal;
    }

    public boolean isClaimable(SaveData data) {
        return !claimed && !isExpired() && isComplete(data);
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
    
    @Override
    public String toString() {
        return title + " (" + goldAmount + " gold, " + gemAmount + " gems, " + xpAmount + " xp)";
    }
}

