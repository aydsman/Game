package progression;

public class XP {
    private int level;
    private int currentXP;
    private int xpToNextLevel;
    private int totalXP;
    private double xpMultiplier;

    public XP(double xpMultiplier) {
        this.level = 1;
        this.currentXP = 0;
        this.xpToNextLevel = 200;
        this.totalXP = 0;
        this.xpMultiplier = xpMultiplier;
    }

    public void addXP(int amount) {
        int actualAmount = (int) (amount * xpMultiplier);
        currentXP += actualAmount;
        totalXP += actualAmount;
        checkLevelUp();
    }

    public void setXpMultiplier(double multiplier) {
        this.xpMultiplier = multiplier;
    }

    public void removeXP(int amount) {
        currentXP = Math.max(0, currentXP - amount);
        totalXP = Math.max(0, totalXP - amount);
    }

    public void resetXP() {
        level = 1;
        currentXP = 0;
        xpToNextLevel = 200;
        totalXP = 0;
    }

    private void checkLevelUp() {
        while (currentXP >= xpToNextLevel) {
            currentXP -= xpToNextLevel;
            level++;
            xpToNextLevel = calculateXPForLevel(level + 1);
        }
    }

    private int calculateXPForLevel(int level) {
        return (int) (200 * Math.pow(1.3, level - 1));
    }

    public int getLevel() {
        return level;
    }

    public int getCurrentXP() {
        return currentXP;
    }

    public int getXpToNextLevel() {
        return xpToNextLevel;
    }

    public int getTotalXP() {
        return totalXP;
    }

    // returns the progress to the next level as a percentage (for xp bar)
    public double getProgressToNextLevel() {
        return (double) currentXP / xpToNextLevel;
    }
}
