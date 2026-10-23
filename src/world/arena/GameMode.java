package world.arena;

/**
 * Rules for an arena run: how many waves it lasts, how long the breaks between waves are,
 * whether difficulty keeps ramping, and how the payout works.
 */
public enum GameMode {

    STANDARD("Standard",
            "Ten waves, miniboss on five, boss on ten. Clearing it unlocks the next arena.",
            10, 1200, 0, 1.0, 0.4),

    ENDLESS("Endless",
            "Waves never stop and enemies keep getting stronger. The run ends when you fall.",
            999, 900, 0, 1.2, 1.0),

    BOSS_RUSH("Boss Rush",
            "Minibosses and bosses back to back, no filler waves. Six fights, short breaks.",
            6, 600, 0, 1.4, 0.5),

    TIME_ATTACK("Time Attack",
            "Four minutes on the clock. Clear as many waves as you can before time runs out.",
            999, 300, 14400, 1.3, 1.0);

    private static final int FRAMES_PER_SECOND = 60;

    private final String displayName;
    private final String description;
    private final int maxWaves;
    private final int gracePeriodFrames;
    private final int timeLimitFrames;
    private final double rewardMultiplier;
    private final double deathPayoutFraction;

    GameMode(String displayName, String description, int maxWaves, int gracePeriodFrames,
             int timeLimitFrames, double rewardMultiplier, double deathPayoutFraction) {
        this.displayName = displayName;
        this.description = description;
        this.maxWaves = maxWaves;
        this.gracePeriodFrames = gracePeriodFrames;
        this.timeLimitFrames = timeLimitFrames;
        this.rewardMultiplier = rewardMultiplier;
        this.deathPayoutFraction = deathPayoutFraction;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public int getMaxWaves() { return maxWaves; }
    public int getGracePeriodFrames() { return gracePeriodFrames; }
    public int getTimeLimitFrames() { return timeLimitFrames; }
    public double getRewardMultiplier() { return rewardMultiplier; }

    /** Share of the run reward paid out when the player dies instead of finishing. */
    public double getDeathPayoutFraction() { return deathPayoutFraction; }

    public boolean hasTimeLimit() { return timeLimitFrames > 0; }

    /** True for modes with no fixed end, where enemy stats keep climbing with the wave number. */
    public boolean rampsWithWaves() { return this == ENDLESS || this == TIME_ATTACK; }

    /** Only the standard run counts as beating an arena for progression. */
    public boolean unlocksNextArena() { return this == STANDARD; }

    public int getTimeLimitSeconds() { return timeLimitFrames / FRAMES_PER_SECOND; }
}
