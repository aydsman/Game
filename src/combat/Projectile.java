package combat;

import java.awt.Color;

/**
 * A travelling shot. Beyond position and damage it carries two pieces of state that let downstream
 * systems react to the shot rather than just the number:
 * <ul>
 *   <li>{@link #isCritical()} — rolled once when the shot is fired, so the hit that lands can be
 *       drawn as a crit;</li>
 *   <li>optional distance drop-off — configured per weapon, so close-range weapons stop being
 *       long-range weapons. See {@link #configureFalloff(int, int, double)}.</li>
 * </ul>
 */
public class Projectile {
    private int x;
    private int y;
    private Color color;
    private double speed;
    private double angle;
    private int damage;
    private int radius;
    private boolean critical;

    /** Distance flown, accumulated from {@link #speed} to avoid the integer truncation in x/y. */
    private double traveledPx;

    // Drop-off. A negative start distance means this projectile has no drop-off.
    private int falloffStartPx = -1;
    private int falloffEndPx = -1;
    private double falloffMinFraction = 1.0;

    public Projectile(int x, int y, Color color, double speed) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.speed = speed;
        this.angle = 0;
        this.damage = 10;
        this.radius = 6;
    }

    public Projectile(int x, int y, Color color, double speed, double angle, int damage) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.speed = speed;
        this.angle = angle;
        this.damage = damage;
        this.radius = 6;
    }

    public Projectile(int x, int y, Color color, double speed, double angle, int damage, int radius) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.speed = speed;
        this.angle = angle;
        this.damage = damage;
        this.radius = radius;
    }

    public void update() {
        x += speed * Math.cos(angle);
        y += speed * Math.sin(angle);
        traveledPx += Math.abs(speed);
    }

    /**
     * Makes this projectile lose damage with distance.
     *
     * <p>Damage is full up to {@code startPx}, falls linearly to {@code minFraction} of its value at
     * {@code endPx}, and stays there beyond it.
     *
     * @param startPx     distance at which damage begins to drop; must be >= 0
     * @param endPx       distance at which damage reaches the floor; must be greater than startPx
     * @param minFraction floor as a fraction of full damage, between 0 and 1
     * @throws IllegalArgumentException if the window is inverted or the fraction is out of range
     */
    public void configureFalloff(int startPx, int endPx, double minFraction) {
        if (startPx < 0) {
            throw new IllegalArgumentException("Falloff start must be >= 0, got " + startPx);
        }
        if (endPx <= startPx) {
            throw new IllegalArgumentException(
                    "Falloff end (" + endPx + ") must be greater than start (" + startPx + ")");
        }
        if (!Double.isFinite(minFraction) || minFraction < 0 || minFraction > 1) {
            throw new IllegalArgumentException("Falloff minimum fraction must be in [0, 1], got " + minFraction);
        }
        this.falloffStartPx = startPx;
        this.falloffEndPx = endPx;
        this.falloffMinFraction = minFraction;
    }

    public boolean hasFalloff() {
        return falloffStartPx >= 0;
    }

    public double getTraveledPx() {
        return traveledPx;
    }

    /**
     * Damage this projectile deals right now, after distance drop-off.
     *
     * <p>Collision code should use this instead of {@link #getDamage()}, which is the undecayed value.
     * A shot that still has damage never decays to zero, because the damage pipeline discards
     * non-positive amounts and a visible hit for no damage reads as a bug.
     */
    public int getEffectiveDamage() {
        if (!hasFalloff() || damage <= 0 || traveledPx <= falloffStartPx) {
            return damage;
        }
        double fraction;
        if (traveledPx >= falloffEndPx) {
            fraction = falloffMinFraction;
        } else {
            double span = falloffEndPx - falloffStartPx;
            double decayed = (traveledPx - falloffStartPx) / span;
            fraction = 1.0 - decayed * (1.0 - falloffMinFraction);
        }
        int scaled = (int) Math.round(damage * fraction);
        return Math.max(1, scaled);
    }

    public boolean isCritical() {
        return critical;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAngle() {
        return angle;
    }

    public int getDamage() {
        return damage;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public int getRadius() {
        return radius;
    }
}
