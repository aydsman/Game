package combat;

import java.awt.Color;
import java.util.ArrayList;

import combat.melee.MeleeComboTracker;

public class Melee extends Item {

    protected double attackSpeed; // attacks per second
    protected int damage; // damage per attack
    protected double range; // attack range in pixels
    protected double knockback; // knockback force

    // Swing properties
    protected double swingAngle; // total arc width in degrees
    protected double swingSpeed; // animation speed (0.0 to 1.0 per frame)
    protected double attackDelay; // seconds between attacks
    protected boolean automatic; // hold to auto-swing

    // Weapon identity: combo chain. Length 1 means this weapon has no combo.
    protected int comboChainLength = MeleeComboTracker.NO_COMBO;
    protected long comboWindowMs = 0; // 0 defers to CombatTuning's shared window
    protected double finisherDamageMultiplier = 1.0;
    protected double finisherKnockbackMultiplier = 1.0;
    protected double finisherArcBonusDegrees = 0.0;

    // Weapon identity: stun applied to whatever this weapon hits. 0 means no stun.
    protected long onHitStunMs = 0;

    // Active swing state (not serialized, transient)
    protected transient boolean isSwinging = false;
    protected transient double swingProgress = 0.0; // 0.0 to 1.0
    protected transient double swingAngleStart = 0.0; // radians
    protected transient double swingAngleEnd = 0.0; // radians
    protected transient double swingCenterAngle = 0.0; // radians, direction of swing
    protected transient long lastAttackTime = 0;
    protected transient java.util.List<entity.Entity> hitEntitiesThisSwing = new java.util.ArrayList<>();
    protected transient MeleeComboTracker comboTracker;
    protected transient int activeComboStep = 0;

    public Melee() {
        super();
        // default melee stats (can be overridden by subclasses)
        attackSpeed = 1.0; // 1 attack per second
        damage = 1500;
        range = 50.0; // 50 pixel range
        knockback = 5.0;

        // default swing properties
        swingAngle = 90.0; // 90 degree arc
        swingSpeed = 0.15; // fast swing
        attackDelay = 0.5; // 0.5 seconds between attacks
        automatic = false;
    }

    public Melee(int tier) {
        super(tier);
        // default melee stats (can be overridden by subclasses)
        attackSpeed = 1.0; // 1 attack per second
        damage = 1500;
        range = 50.0; // 50 pixel range
        knockback = 5.0;

        // default swing properties
        swingAngle = 90.0; // 90 degree arc
        swingSpeed = 0.15; // fast swing
        attackDelay = 0.5; // 0.5 seconds between attacks
        automatic = false;

        applyTierMultipliers();
    }

    protected void applyTierMultipliers() {
        double damageMultiplier = 1.0;
        double attackSpeedMultiplier = 1.0;
        double rangeMultiplier = 1.0;
        double knockbackMultiplier = 1.0;

        switch (tier) {
            case 1: // Tier I - Common (no boost)
                damageMultiplier = 1.0;
                attackSpeedMultiplier = 1.0;
                rangeMultiplier = 1.0;
                knockbackMultiplier = 1.0;
                break;
            case 2: // Tier II - Uncommon (minimal boost)
                damageMultiplier = 1.1;
                attackSpeedMultiplier = 1.05;
                rangeMultiplier = 1.05;
                knockbackMultiplier = 1.05;
                break;
            case 3: // Tier III - Rare (minimal boost)
                damageMultiplier = 1.2;
                attackSpeedMultiplier = 1.1;
                rangeMultiplier = 1.1;
                knockbackMultiplier = 1.1;
                break;
            case 4: // Tier IV - Epic (better boost)
                damageMultiplier = 1.4;
                attackSpeedMultiplier = 1.2;
                rangeMultiplier = 1.15;
                knockbackMultiplier = 1.2;
                break;
            case 5: // Tier V - Legendary (best boost)
                damageMultiplier = 1.7;
                attackSpeedMultiplier = 1.3;
                rangeMultiplier = 1.2;
                knockbackMultiplier = 1.3;
                break;
        }

        damage *= damageMultiplier;
        attackSpeed *= attackSpeedMultiplier;
        range *= rangeMultiplier;
        knockback *= knockbackMultiplier;

        // Round to 2 decimal places
        attackSpeed = Math.round(attackSpeed * 100.0) / 100.0;
        range = Math.round(range * 100.0) / 100.0;
        knockback = Math.round(knockback * 100.0) / 100.0;
    }

    public void attack() {
        // attack logic will go here
    }

    // Swing mechanics
    public boolean canAttack() {
        long currentTime = System.currentTimeMillis();
        return !isSwinging && (currentTime - lastAttackTime) >= (long)(attackDelay * 1000);
    }

    public void startSwing(double centerAngleRadians) {
        startSwing(centerAngleRadians, System.currentTimeMillis());
    }

    /**
     * Begins a swing, advancing this weapon's combo chain.
     *
     * <p>A finisher swings a wider arc, which widens collision and the drawn arc together because
     * both read {@link #getSwingAngleStart()} / {@link #getSwingAngleEnd()}.
     *
     * @param nowMs current time; injected so combo behaviour is testable
     */
    public void startSwing(double centerAngleRadians, long nowMs) {
        if (!canAttack()) return;

        activeComboStep = getComboTracker().onSwingStart(nowMs);

        isSwinging = true;
        swingProgress = 0.0;
        swingCenterAngle = centerAngleRadians;

        // Calculate arc: swing from -angle/2 to +angle/2 relative to center
        double arcDegrees = swingAngle + (isFinisherSwing() ? finisherArcBonusDegrees : 0.0);
        double halfAngleRad = Math.toRadians(arcDegrees / 2.0);
        swingAngleStart = centerAngleRadians - halfAngleRad;
        swingAngleEnd = centerAngleRadians + halfAngleRad;

        hitEntitiesThisSwing.clear();
    }

    /** Combo tracker for this weapon instance, created on first use from the weapon's own config. */
    public MeleeComboTracker getComboTracker() {
        if (comboTracker == null) {
            long window = comboWindowMs > 0 ? comboWindowMs : CombatTuning.active().getComboWindowMs();
            comboTracker = new MeleeComboTracker(Math.max(MeleeComboTracker.NO_COMBO, comboChainLength), window);
        }
        return comboTracker;
    }

    /** Records that the in-flight swing connected, so the chain survives into the next swing. */
    public void registerComboHit(long nowMs) {
        getComboTracker().onHitLanded(nowMs);
    }

    /** 1-based step of the swing in flight; 0 before the first swing. */
    public int getActiveComboStep() {
        return activeComboStep;
    }

    public boolean isFinisherSwing() {
        return getComboTracker().isFinisherStep(activeComboStep);
    }

    public boolean hasCombo() {
        return comboChainLength > MeleeComboTracker.NO_COMBO;
    }

    public int getComboChainLength() {
        return comboChainLength;
    }

    public void updateSwing() {
        if (!isSwinging) return;

        swingProgress += swingSpeed;

        if (swingProgress >= 1.0) {
            endSwing();
        }
    }

    public void endSwing() {
        isSwinging = false;
        swingProgress = 0.0;
        lastAttackTime = System.currentTimeMillis();
        hitEntitiesThisSwing.clear();
    }

    // Get current angle of the swing for visual arc drawing
    public double getCurrentSwingAngle() {
        if (!isSwinging) return swingCenterAngle;
        // Interpolate from start to end based on progress
        return swingAngleStart + (swingAngleEnd - swingAngleStart) * swingProgress;
    }

    // Check if a point is within the current swing arc
    public boolean isInSwingArc(double px, double py, int playerX, int playerY) {
        if (!isSwinging) return false;

        double dx = px - playerX;
        double dy = py - playerY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > range) return false;

        double angleToPoint = Math.atan2(dy, dx);

        // Normalize angles to [0, 2PI]
        double normalizedPoint = normalizeAngle(angleToPoint);
        double normalizedStart = normalizeAngle(swingAngleStart);
        double normalizedEnd = normalizeAngle(swingAngleEnd);

        // Check if point is within the arc
        if (normalizedStart <= normalizedEnd) {
            return normalizedPoint >= normalizedStart && normalizedPoint <= normalizedEnd;
        } else {
            // Arc crosses the 0/2PI boundary
            return normalizedPoint >= normalizedStart || normalizedPoint <= normalizedEnd;
        }
    }

    private double normalizeAngle(double angle) {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }

    // Getters for swing properties
    public boolean isSwinging() { return isSwinging; }
    public double getSwingProgress() { return swingProgress; }
    public double getSwingAngle() { return swingAngle; }
    public double getSwingAngleStart() { return swingAngleStart; }
    public double getSwingAngleEnd() { return swingAngleEnd; }
    public double getSwingCenterAngle() { return swingCenterAngle; }
    public double getRange() { return range; }
    public boolean isAutomatic() { return automatic; }
    public double getAttackDelay() { return attackDelay; }
    public java.util.List<entity.Entity> getHitEntitiesThisSwing() { return hitEntitiesThisSwing; }

    public int getDamage() {
        return damage;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public double getKnockback() {
        return knockback;
    }

    /**
     * This weapon's knockback expressed in pixels.
     *
     * <p>{@link #knockback} is an abstract force, so it is mapped through {@link CombatTuning} rather
     * than used as a pixel count: a flat base keeps light weapons from feeling weightless, and the
     * per-force term is what separates a dagger tap from a hammer blow.
     */
    public int getKnockbackPixels() {
        return getKnockbackPixels(CombatTuning.active());
    }

    public int getKnockbackPixels(CombatTuning tuning) {
        double pixels = tuning.getMeleeKnockbackBasePx() + knockback * tuning.getMeleeKnockbackPerForcePx();
        return clampKnockback(pixels, tuning);
    }

    /** Knockback for the swing in flight, including the combo finisher bonus. */
    public int getActiveSwingKnockbackPixels() {
        return getActiveSwingKnockbackPixels(CombatTuning.active());
    }

    public int getActiveSwingKnockbackPixels(CombatTuning tuning) {
        double pixels = tuning.getMeleeKnockbackBasePx() + knockback * tuning.getMeleeKnockbackPerForcePx();
        if (isFinisherSwing()) {
            pixels *= finisherKnockbackMultiplier;
        }
        return clampKnockback(pixels, tuning);
    }

    private static int clampKnockback(double pixels, CombatTuning tuning) {
        if (!Double.isFinite(pixels) || pixels <= 0) {
            return 0;
        }
        return (int) Math.round(Math.min(pixels, tuning.getMeleeKnockbackMaxPx()));
    }

    /** Damage multiplier for the swing in flight; above 1.0 only on a combo finisher. */
    public double getActiveSwingDamageMultiplier() {
        return isFinisherSwing() ? finisherDamageMultiplier : 1.0;
    }

    /** Stun this weapon applies on hit, in milliseconds; 0 for weapons that do not stun. */
    public long getOnHitStunMs() {
        return onHitStunMs;
    }

    public void setSwingProperties(double swingAngle, double swingSpeed, double attackDelay, boolean automatic) {
        this.swingAngle = swingAngle;
        this.swingSpeed = swingSpeed;
        this.attackDelay = attackDelay;
        this.automatic = automatic;
    }

    /**
     * Gives this weapon a combo chain.
     *
     * @param chainLength                number of swings in a chain; 1 disables combos
     * @param finisherDamageMultiplier   damage multiplier on the final swing
     * @param finisherKnockbackMultiplier knockback multiplier on the final swing
     * @param finisherArcBonusDegrees    extra arc width on the final swing
     * @throws IllegalArgumentException if the chain length or multipliers are out of range
     */
    public void setComboProperties(int chainLength,
                                   double finisherDamageMultiplier,
                                   double finisherKnockbackMultiplier,
                                   double finisherArcBonusDegrees) {
        if (chainLength < MeleeComboTracker.NO_COMBO) {
            throw new IllegalArgumentException("Combo chain length must be >= 1, got " + chainLength);
        }
        if (!Double.isFinite(finisherDamageMultiplier) || finisherDamageMultiplier <= 0) {
            throw new IllegalArgumentException("Finisher damage multiplier must be > 0, got " + finisherDamageMultiplier);
        }
        if (!Double.isFinite(finisherKnockbackMultiplier) || finisherKnockbackMultiplier <= 0) {
            throw new IllegalArgumentException("Finisher knockback multiplier must be > 0, got " + finisherKnockbackMultiplier);
        }
        if (!Double.isFinite(finisherArcBonusDegrees) || finisherArcBonusDegrees < 0) {
            throw new IllegalArgumentException("Finisher arc bonus must be >= 0, got " + finisherArcBonusDegrees);
        }
        this.comboChainLength = chainLength;
        this.finisherDamageMultiplier = finisherDamageMultiplier;
        this.finisherKnockbackMultiplier = finisherKnockbackMultiplier;
        this.finisherArcBonusDegrees = finisherArcBonusDegrees;
        this.comboTracker = null; // rebuilt on next use with the new chain length
    }

    /**
     * Makes this weapon stun what it hits.
     *
     * @param stunMs stun duration in milliseconds; 0 removes the stun
     * @throws IllegalArgumentException if negative
     */
    public void setOnHitStun(long stunMs) {
        if (stunMs < 0) {
            throw new IllegalArgumentException("On-hit stun must be >= 0, got " + stunMs);
        }
        this.onHitStunMs = stunMs;
    }

    @Override
    public Melee clone() {
        Melee cloned = (Melee) super.clone();
        cloned.attackSpeed = this.attackSpeed;
        cloned.damage = this.damage;
        cloned.range = this.range;
        cloned.knockback = this.knockback;
        cloned.swingAngle = this.swingAngle;
        cloned.swingSpeed = this.swingSpeed;
        cloned.attackDelay = this.attackDelay;
        cloned.automatic = this.automatic;
        cloned.comboChainLength = this.comboChainLength;
        cloned.comboWindowMs = this.comboWindowMs;
        cloned.finisherDamageMultiplier = this.finisherDamageMultiplier;
        cloned.finisherKnockbackMultiplier = this.finisherKnockbackMultiplier;
        cloned.finisherArcBonusDegrees = this.finisherArcBonusDegrees;
        cloned.onHitStunMs = this.onHitStunMs;
        cloned.isSwinging = false;
        cloned.swingProgress = 0;
        cloned.swingAngleStart = 0;
        cloned.swingAngleEnd = 0;
        cloned.swingCenterAngle = 0;
        cloned.lastAttackTime = 0;
        cloned.hitEntitiesThisSwing = new ArrayList<>();
        cloned.comboTracker = null; // a clone starts its own chain
        cloned.activeComboStep = 0;
        return cloned;
    }
}
