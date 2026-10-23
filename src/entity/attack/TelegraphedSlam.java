package entity.attack;

import combat.CombatTuning;

/**
 * A ground slam that announces itself before it lands: wind-up (the telegraph), a brief active
 * window where the hitbox is live, then recovery.
 *
 * <p>The impact point is locked when the wind-up starts and does not follow the target, so walking or
 * dodging out of the marked circle is always the right answer. That is the whole point of the
 * attack — it gives the player something to read and react to instead of a hit that simply happens.
 *
 * <p>Time is passed in by the caller; the state machine never reads the clock and holds no world
 * references.
 */
public final class TelegraphedSlam {

    public enum Phase {
        /** Not attacking; may or may not be off cooldown. */
        IDLE,
        /** Telegraph is showing, impact has not happened yet, the target can still leave. */
        WINDUP,
        /** Hitbox is live. */
        ACTIVE,
        /** Attack is over but the attacker is still committed and cannot move or re-attack. */
        RECOVERY
    }

    private final long windupMs;
    private final long activeMs;
    private final long recoveryMs;
    private final long cooldownMs;
    private final int radiusPx;
    private final double damageMultiplier;

    private long startedAtMs = Long.MIN_VALUE;
    private boolean impactConsumed;
    private int anchorX;
    private int anchorY;

    public TelegraphedSlam(CombatTuning tuning) {
        this(tuning.getSlamWindupMs(),
                tuning.getSlamActiveMs(),
                tuning.getSlamRecoveryMs(),
                tuning.getSlamCooldownMs(),
                tuning.getSlamRadiusPx(),
                tuning.getSlamDamageMultiplier());
    }

    /**
     * @param windupMs         telegraph length; this is the player's reaction window
     * @param activeMs         how long the hitbox stays live
     * @param recoveryMs       committed time after the hit, during which the attacker is punishable
     * @param cooldownMs       time between slams, measured from the start of the wind-up; raised to
     *                         the full attack length if a shorter value is configured
     * @param radiusPx         impact radius around the locked anchor
     * @param damageMultiplier multiplier applied to the attacker's damage on impact
     * @throws IllegalArgumentException if any argument is out of range
     */
    public TelegraphedSlam(long windupMs,
                           long activeMs,
                           long recoveryMs,
                           long cooldownMs,
                           int radiusPx,
                           double damageMultiplier) {
        if (windupMs <= 0) {
            throw new IllegalArgumentException("Slam wind-up must be > 0 so the attack can be read, got " + windupMs);
        }
        if (activeMs <= 0) {
            throw new IllegalArgumentException("Slam active window must be > 0, got " + activeMs);
        }
        if (recoveryMs < 0) {
            throw new IllegalArgumentException("Slam recovery must be >= 0, got " + recoveryMs);
        }
        if (cooldownMs < 0) {
            throw new IllegalArgumentException("Slam cooldown must be >= 0, got " + cooldownMs);
        }
        if (radiusPx <= 0) {
            throw new IllegalArgumentException("Slam radius must be > 0, got " + radiusPx);
        }
        if (!Double.isFinite(damageMultiplier) || damageMultiplier <= 0) {
            throw new IllegalArgumentException("Slam damage multiplier must be > 0, got " + damageMultiplier);
        }
        this.windupMs = windupMs;
        this.activeMs = activeMs;
        this.recoveryMs = recoveryMs;
        this.radiusPx = radiusPx;
        this.damageMultiplier = damageMultiplier;
        this.cooldownMs = Math.max(cooldownMs, windupMs + activeMs + recoveryMs);
    }

    /**
     * Starts a wind-up anchored at the given world point.
     *
     * @return true if the slam started; false while one is in progress or on cooldown
     */
    public boolean tryBegin(long nowMs, int anchorCenterX, int anchorCenterY) {
        if (getCooldownRemainingMs(nowMs) > 0) {
            return false;
        }
        startedAtMs = nowMs;
        impactConsumed = false;
        anchorX = anchorCenterX;
        anchorY = anchorCenterY;
        return true;
    }

    public Phase getPhase(long nowMs) {
        if (startedAtMs == Long.MIN_VALUE) {
            return Phase.IDLE;
        }
        long elapsed = nowMs - startedAtMs;
        if (elapsed < 0) {
            return Phase.IDLE;
        }
        if (elapsed < windupMs) {
            return Phase.WINDUP;
        }
        if (elapsed < windupMs + activeMs) {
            return Phase.ACTIVE;
        }
        if (elapsed < windupMs + activeMs + recoveryMs) {
            return Phase.RECOVERY;
        }
        return Phase.IDLE;
    }

    /** True while the attacker is committed to the slam and should not move or start anything else. */
    public boolean isBusy(long nowMs) {
        return getPhase(nowMs) != Phase.IDLE;
    }

    /** 0.0 at the start of the telegraph, 1.0 at the moment of impact; drives the telegraph ring. */
    public double getWindupProgress(long nowMs) {
        if (startedAtMs == Long.MIN_VALUE) {
            return 0.0;
        }
        double elapsed = nowMs - startedAtMs;
        return Math.max(0.0, Math.min(1.0, elapsed / (double) windupMs));
    }

    /**
     * Returns true exactly once per slam, on the first call at or after the end of the wind-up.
     *
     * <p>It stays true past the active window rather than expiring, so a frame-rate hitch that skips
     * over the active window still resolves the attack instead of silently dropping it.
     */
    public boolean consumeImpact(long nowMs) {
        if (startedAtMs == Long.MIN_VALUE || impactConsumed) {
            return false;
        }
        if (nowMs - startedAtMs < windupMs) {
            return false;
        }
        impactConsumed = true;
        return true;
    }

    /** True when the point is inside the locked impact circle. */
    public boolean covers(int pointX, int pointY) {
        long dx = (long) pointX - anchorX;
        long dy = (long) pointY - anchorY;
        return dx * dx + dy * dy <= (long) radiusPx * radiusPx;
    }

    public long getCooldownRemainingMs(long nowMs) {
        if (startedAtMs == Long.MIN_VALUE) {
            return 0L;
        }
        return Math.max(0L, startedAtMs + cooldownMs - nowMs);
    }

    public int getAnchorX() {
        return anchorX;
    }

    public int getAnchorY() {
        return anchorY;
    }

    public int getRadiusPx() {
        return radiusPx;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public long getWindupMs() {
        return windupMs;
    }

    public long getActiveMs() {
        return activeMs;
    }

    public long getRecoveryMs() {
        return recoveryMs;
    }

    public long getCooldownMs() {
        return cooldownMs;
    }

    /** Cancels any slam in progress and clears the cooldown. */
    public void reset() {
        startedAtMs = Long.MIN_VALUE;
        impactConsumed = false;
        anchorX = 0;
        anchorY = 0;
    }
}
