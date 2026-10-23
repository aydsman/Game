package combat;

/**
 * Very short simulation freeze on impactful hits, so a heavy swing or a crit reads as landing
 * instead of passing through the target.
 *
 * <p>Only entity simulation is expected to pause while {@link #isFrozen(long)} is true — input, UI
 * and camera keep running, which keeps the freeze feeling like weight rather than a dropped frame.
 *
 * <p>Two safety properties matter more than the effect itself, because a stuck freeze would look
 * like a hang:
 * <ul>
 *   <li>a single request is clamped to the configured maximum;</li>
 *   <li>overlapping requests cannot accumulate — the freeze never ends later than
 *       {@code nowMs + maxMs} of the most recent request.</li>
 * </ul>
 */
public final class Hitstop {

    private final long maxMs;

    /** Exclusive end of the current freeze. {@link Long#MIN_VALUE} means "never requested". */
    private long frozenUntilMs = Long.MIN_VALUE;

    public Hitstop(CombatTuning tuning) {
        this(tuning.getHitstopMaxMs());
    }

    /**
     * @param maxMs longest freeze allowed; 0 disables hitstop entirely
     * @throws IllegalArgumentException if negative
     */
    public Hitstop(long maxMs) {
        if (maxMs < 0) {
            throw new IllegalArgumentException("Hitstop maximum must be >= 0, got " + maxMs);
        }
        this.maxMs = maxMs;
    }

    /**
     * Requests a freeze. Extends an active freeze but never shortens it, and never pushes the end
     * past the per-request ceiling.
     *
     * @param durationMs requested length; values <= 0 are ignored, longer values are clamped
     */
    public void request(long nowMs, long durationMs) {
        if (maxMs == 0 || durationMs <= 0) {
            return;
        }
        long ceiling = nowMs + maxMs;
        long requestedEnd = nowMs + Math.min(durationMs, maxMs);
        long end = Math.max(frozenUntilMs, requestedEnd);
        frozenUntilMs = Math.min(end, ceiling);
    }

    public boolean isFrozen(long nowMs) {
        return nowMs < frozenUntilMs;
    }

    public long getRemainingMs(long nowMs) {
        return Math.max(0L, frozenUntilMs - nowMs);
    }

    public long getMaxMs() {
        return maxMs;
    }

    /** Ends any freeze immediately; used when a run restarts or a screen is left. */
    public void reset() {
        frozenUntilMs = Long.MIN_VALUE;
    }
}
