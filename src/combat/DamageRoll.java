package combat;

/**
 * One resolved damage number together with whether it crit.
 *
 * <p>Crit chance used to be folded straight into a damage multiplier, which meant nothing downstream
 * could tell a crit from a big hit — so crits had no feedback. Rolling into this value object keeps
 * the crit flag alive all the way to the damage popup and the hitstop request.
 *
 * <p>The roll takes its random sample as an argument instead of calling {@link Math#random()}, which
 * keeps it deterministic under test.
 */
public final class DamageRoll {

    private final int amount;
    private final boolean critical;

    private DamageRoll(int amount, boolean critical) {
        this.amount = amount;
        this.critical = critical;
    }

    /**
     * Resolves a damage number.
     *
     * <p>A hit that would round to zero is raised to 1, because the damage pipeline discards
     * non-positive amounts and a visible hit that does nothing reads as a bug.
     *
     * @param baseDamage     weapon or ability damage before modifiers; <= 0 yields a zero roll
     * @param multiplier     stacked player multipliers (charms, potions, skill tree); <= 0 yields a
     *                       zero roll
     * @param critChance     0.0 - 1.0; values outside that range are clamped
     * @param critMultiplier damage multiplier applied on a crit; values < 1 are treated as 1
     * @param critSample     uniform random sample in [0, 1), normally {@code Math.random()}
     */
    public static DamageRoll of(double baseDamage,
                                double multiplier,
                                double critChance,
                                double critMultiplier,
                                double critSample) {
        if (!isUsable(baseDamage) || !isUsable(multiplier) || baseDamage <= 0 || multiplier <= 0) {
            return new DamageRoll(0, false);
        }
        boolean critical = rollsCritical(critChance, critSample);

        double critScale = 1.0;
        if (critical) {
            critScale = isUsable(critMultiplier) ? Math.max(1.0, critMultiplier) : 1.0;
        }

        double raw = baseDamage * multiplier * critScale;
        int rounded = (int) Math.round(raw);
        if (rounded <= 0) {
            rounded = 1;
        }
        return new DamageRoll(rounded, critical);
    }

    /**
     * The single crit decision used across the game, so shots and swings cannot drift apart.
     *
     * @param critChance 0.0 - 1.0; values outside that range are clamped, non-finite means no crit
     * @param critSample uniform random sample in [0, 1), normally {@code Math.random()}
     */
    public static boolean rollsCritical(double critChance, double critSample) {
        if (!isUsable(critChance) || !isUsable(critSample)) {
            return false;
        }
        double chance = Math.max(0.0, Math.min(1.0, critChance));
        return chance > 0 && critSample < chance;
    }

    /** A fixed, non-critical amount, for damage sources that do not roll (burn ticks, hazards). */
    public static DamageRoll flat(double amount) {
        return resolved(amount, false);
    }

    /**
     * An amount whose crit state was already decided elsewhere — a projectile that rolled when it was
     * fired and only lands later.
     */
    public static DamageRoll resolved(double amount, boolean critical) {
        int rounded = (int) Math.round(amount);
        if (rounded <= 0) {
            return new DamageRoll(0, critical);
        }
        return new DamageRoll(rounded, critical);
    }

    private static boolean isUsable(double value) {
        return Double.isFinite(value);
    }

    public int getAmount() {
        return amount;
    }

    public boolean isCritical() {
        return critical;
    }

    /** Returns a roll with the same crit flag and a scaled amount, for combo finishers. */
    public DamageRoll scaled(double factor) {
        if (!isUsable(factor) || factor <= 0 || amount <= 0) {
            return new DamageRoll(0, critical);
        }
        int rounded = (int) Math.round(amount * factor);
        return new DamageRoll(Math.max(1, rounded), critical);
    }

    @Override
    public String toString() {
        return "DamageRoll{amount=" + amount + ", critical=" + critical + '}';
    }
}
