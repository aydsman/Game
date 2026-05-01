package combat;

import java.awt.Color;
import java.util.ArrayList;

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

    // Active swing state (not serialized, transient)
    protected transient boolean isSwinging = false;
    protected transient double swingProgress = 0.0; // 0.0 to 1.0
    protected transient double swingAngleStart = 0.0; // radians
    protected transient double swingAngleEnd = 0.0; // radians
    protected transient double swingCenterAngle = 0.0; // radians, direction of swing
    protected transient long lastAttackTime = 0;
    protected transient java.util.List<entity.Entity> hitEntitiesThisSwing = new java.util.ArrayList<>();

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
        if (!canAttack()) return;

        isSwinging = true;
        swingProgress = 0.0;
        swingCenterAngle = centerAngleRadians;

        // Calculate arc: swing from -angle/2 to +angle/2 relative to center
        double halfAngleRad = Math.toRadians(swingAngle / 2.0);
        swingAngleStart = centerAngleRadians - halfAngleRad;
        swingAngleEnd = centerAngleRadians + halfAngleRad;

        hitEntitiesThisSwing.clear();
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

    public void setSwingProperties(double swingAngle, double swingSpeed, double attackDelay, boolean automatic) {
        this.swingAngle = swingAngle;
        this.swingSpeed = swingSpeed;
        this.attackDelay = attackDelay;
        this.automatic = automatic;
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
        cloned.isSwinging = false;
        cloned.swingProgress = 0;
        cloned.swingAngleStart = 0;
        cloned.swingAngleEnd = 0;
        cloned.swingCenterAngle = 0;
        cloned.lastAttackTime = 0;
        cloned.hitEntitiesThisSwing = new ArrayList<>();
        return cloned;
    }
}
