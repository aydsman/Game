package combat.melee;

/**
 * Tracks a melee weapon's combo chain: consecutive connecting swings escalate to a finisher, and a
 * whiff or a pause drops the chain back to the first step.
 *
 * <p>The chain advances on <em>swing start</em> so the widened finisher arc is known before collision
 * runs, but it only advances when the previous swing actually landed. That makes the rule the player
 * feels — "keep connecting to reach the finisher" — the same rule the code enforces.
 *
 * <p>Time is passed in by the caller; the tracker never reads the clock.
 */
public final class MeleeComboTracker {

    /** Chain length for a weapon with no combo; every swing is step 1. */
    public static final int NO_COMBO = 1;

    private final int chainLength;
    private final long windowMs;

    private int step = 0;
    private long lastHitMs = Long.MIN_VALUE;
    private boolean lastSwingConnected;

    /**
     * @param chainLength number of swings in a full chain; 1 disables combos
     * @param windowMs    time after a connecting swing during which the chain stays alive
     * @throws IllegalArgumentException if the chain length is below 1 or the window is not positive
     */
    public MeleeComboTracker(int chainLength, long windowMs) {
        if (chainLength < NO_COMBO) {
            throw new IllegalArgumentException("Combo chain length must be >= 1, got " + chainLength);
        }
        if (windowMs <= 0) {
            throw new IllegalArgumentException("Combo window must be > 0, got " + windowMs);
        }
        this.chainLength = chainLength;
        this.windowMs = windowMs;
    }

    /**
     * Advances the chain for a swing that is starting now.
     *
     * @return the 1-based step this swing occupies
     */
    public int onSwingStart(long nowMs) {
        if (isChainAlive(nowMs)) {
            step = step >= chainLength ? 1 : step + 1;
        } else {
            step = 1;
        }
        lastSwingConnected = false;
        return step;
    }

    /** Records that the in-flight swing hit something, keeping the chain alive. */
    public void onHitLanded(long nowMs) {
        lastSwingConnected = true;
        lastHitMs = nowMs;
    }

    /**
     * True when the previous swing connected and the window has not lapsed, meaning the next swing
     * continues the chain instead of restarting it.
     */
    public boolean isChainAlive(long nowMs) {
        return lastSwingConnected
                && lastHitMs != Long.MIN_VALUE
                && nowMs - lastHitMs <= windowMs;
    }

    /** The step of the swing currently in flight, or 0 before the first swing. */
    public int getStep() {
        return step;
    }

    public boolean isFinisherStep(int candidateStep) {
        return chainLength > NO_COMBO && candidateStep >= chainLength;
    }

    /** True when the swing currently in flight is the chain's finisher. */
    public boolean isFinisher() {
        return isFinisherStep(step);
    }

    public int getChainLength() {
        return chainLength;
    }

    public long getWindowMs() {
        return windowMs;
    }

    /** Drops the chain; used when the weapon is swapped or a run restarts. */
    public void reset() {
        step = 0;
        lastHitMs = Long.MIN_VALUE;
        lastSwingConnected = false;
    }
}
