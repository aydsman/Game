package combat.defense;

import combat.CombatTuning;

/**
 * The player's dodge: a short burst of movement with invulnerability frames, followed by a cooldown.
 *
 * <p>The state machine is pure and time-injected — it holds no reference to the world and never reads
 * the clock itself. The owner drives it each frame:
 * <pre>
 *   dodge.tryStart(now, dirX, dirY);              // on the dodge key
 *   int[] step = dodge.sampleDisplacement(now);   // px to move this frame
 * </pre>
 * The caller is responsible for collision-resolving that displacement, so a dodge can never push the
 * player through a wall.
 *
 * <p>Distance is spread across the dash with an ease-out curve (fast off the mark, settling at the
 * end) and sub-pixel remainders are carried between frames, so the total travelled distance matches
 * the configured distance regardless of frame rate.
 */
public final class DodgeState {

    private final int distancePx;
    private final long durationMs;
    private final long cooldownMs;
    private final InvulnerabilityWindow invulnerability;

    private long startedAtMs = Long.MIN_VALUE;
    private long lastSampleMs = Long.MIN_VALUE;
    private double directionX;
    private double directionY;

    /** Sub-pixel movement not yet handed to the caller. */
    private double carryX;
    private double carryY;

    /**
     * Scales the configured cooldown. 1.0 is unchanged; values below 1 shorten it (Iron Reflex).
     * Never allowed below 0.5 so a perk stack cannot erase the commitment a dodge represents.
     */
    private double cooldownScale = 1.0;

    public DodgeState(CombatTuning tuning) {
        this(tuning.getDodgeDistancePx(),
                tuning.getDodgeDurationMs(),
                tuning.getDodgeInvulnerabilityMs(),
                tuning.getDodgeCooldownMs());
    }

    /**
     * @param distancePx        total distance travelled by one dodge
     * @param durationMs        how long the movement lasts
     * @param invulnerabilityMs how long hits are ignored, measured from the start of the dodge
     * @param cooldownMs        time between dodges, measured from the start of the dodge; raised to
     *                          {@code durationMs} if a shorter value is configured, so a dodge can
     *                          never be re-triggered while it is still running
     * @throws IllegalArgumentException if any argument is out of range
     */
    public DodgeState(int distancePx, long durationMs, long invulnerabilityMs, long cooldownMs) {
        if (distancePx <= 0) {
            throw new IllegalArgumentException("Dodge distance must be > 0, got " + distancePx);
        }
        if (durationMs <= 0) {
            throw new IllegalArgumentException("Dodge duration must be > 0, got " + durationMs);
        }
        if (invulnerabilityMs < 0) {
            throw new IllegalArgumentException("Dodge invulnerability must be >= 0, got " + invulnerabilityMs);
        }
        if (cooldownMs < 0) {
            throw new IllegalArgumentException("Dodge cooldown must be >= 0, got " + cooldownMs);
        }
        this.distancePx = distancePx;
        this.durationMs = durationMs;
        this.cooldownMs = Math.max(cooldownMs, durationMs);
        this.invulnerability = new InvulnerabilityWindow(invulnerabilityMs);
    }

    /**
     * Starts a dodge in the given direction.
     *
     * @param dirX x component of the direction; does not need to be normalised
     * @param dirY y component of the direction
     * @return true if the dodge started; false if it is on cooldown, already running, or the
     *         direction has no length (which would leave the player invulnerable but stationary)
     */
    public boolean tryStart(long nowMs, double dirX, double dirY) {
        if (!isReady(nowMs)) {
            return false;
        }
        double length = Math.sqrt(dirX * dirX + dirY * dirY);
        if (!Double.isFinite(length) || length < 1e-6) {
            return false;
        }
        directionX = dirX / length;
        directionY = dirY / length;
        startedAtMs = nowMs;
        lastSampleMs = nowMs;
        carryX = 0;
        carryY = 0;
        invulnerability.grant(nowMs);
        return true;
    }

    public boolean isReady(long nowMs) {
        return getCooldownRemainingMs(nowMs) <= 0;
    }

    public boolean isDashing(long nowMs) {
        return startedAtMs != Long.MIN_VALUE && nowMs < startedAtMs + durationMs;
    }

    public boolean isInvulnerable(long nowMs) {
        return invulnerability.isActive(nowMs);
    }

    /**
     * Shortens or restores the cooldown from perks. Values outside (0, 1] are rejected so a bad
     * perk definition cannot disable dodging or make it free.
     */
    public void setCooldownScale(double scale) {
        if (!Double.isFinite(scale) || scale <= 0 || scale > 1.0) {
            return;
        }
        this.cooldownScale = Math.max(0.5, scale);
    }

    public double getCooldownScale() {
        return cooldownScale;
    }

    public long getCooldownRemainingMs(long nowMs) {
        if (startedAtMs == Long.MIN_VALUE) {
            return 0L;
        }
        long scaledCooldown = Math.round(cooldownMs * cooldownScale);
        return Math.max(0L, startedAtMs + scaledCooldown - nowMs);
    }

    /** 0.0 right after a dodge, rising to 1.0 when the next one is available; drives the HUD pip. */
    public double getReadiness(long nowMs) {
        long scaledCooldown = Math.max(1L, Math.round(cooldownMs * cooldownScale));
        double remaining = getCooldownRemainingMs(nowMs);
        return Math.max(0.0, Math.min(1.0, 1.0 - remaining / (double) scaledCooldown));
    }

    /**
     * Movement owed to the dodge since the previous call, in whole pixels.
     *
     * <p>Returns {@code {0, 0}} when no dodge is in flight. Must be called at most once per frame:
     * it consumes the elapsed time, so calling it twice splits one frame's movement across both
     * calls rather than doubling it.
     *
     * @return a two-element array {@code {dx, dy}}
     */
    public int[] sampleDisplacement(long nowMs) {
        if (startedAtMs == Long.MIN_VALUE) {
            return new int[]{0, 0};
        }
        long endMs = startedAtMs + durationMs;
        long sampleAt = Math.min(nowMs, endMs);
        if (sampleAt <= lastSampleMs) {
            return new int[]{0, 0};
        }

        double travelled = (easedProgress(sampleAt) - easedProgress(lastSampleMs)) * distancePx;
        lastSampleMs = sampleAt;

        carryX += directionX * travelled;
        carryY += directionY * travelled;
        int stepX = (int) carryX;
        int stepY = (int) carryY;
        carryX -= stepX;
        carryY -= stepY;
        return new int[]{stepX, stepY};
    }

    /** Ease-out quadratic, so the dodge leads with speed and settles instead of stopping dead. */
    private double easedProgress(long atMs) {
        double t = (atMs - startedAtMs) / (double) durationMs;
        t = Math.max(0.0, Math.min(1.0, t));
        double inverse = 1.0 - t;
        return 1.0 - inverse * inverse;
    }

    public double getDirectionX() {
        return directionX;
    }

    public double getDirectionY() {
        return directionY;
    }

    public int getDistancePx() {
        return distancePx;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public long getCooldownMs() {
        return cooldownMs;
    }

    /** Clears the dodge and its cooldown; used when a run restarts. */
    public void reset() {
        startedAtMs = Long.MIN_VALUE;
        lastSampleMs = Long.MIN_VALUE;
        directionX = 0;
        directionY = 0;
        carryX = 0;
        carryY = 0;
        invulnerability.reset();
    }
}
