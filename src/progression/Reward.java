package progression;

/**
 * Represents a single reward from the daily/weekly/seasonal reward system.
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
    
    public Reward(String id, String title, String description, int gold, int gems, int xp, RewardType type, long expiresAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.goldAmount = gold;
        this.gemAmount = gems;
        this.xpAmount = xp;
        this.type = type;
        this.claimed = false;
        this.expiresAt = expiresAt;
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
    
    // Setters
    public void setClaimed(boolean claimed) { this.claimed = claimed; }
    
    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
    
    @Override
    public String toString() {
        return title + " (" + goldAmount + " gold, " + gemAmount + " gems, " + xpAmount + " xp)";
    }
}

