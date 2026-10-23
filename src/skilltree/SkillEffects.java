package skilltree;

import save.SaveData;
import save.SaveManager;

import java.util.Map;

/**
 * Live gameplay view of unlocked skill-tree bonuses.
 *
 * Totals are cached per {@link SaveData} instance; call {@link #invalidate()} after
 * unlocking or respeccing so the next read picks up the change.
 */
public final class SkillEffects {

    /** Damage dealt by a critical hit, as a multiplier of normal damage. */
    public static final double CRIT_DAMAGE_MULTIPLIER = 2.0;

    public static final double MAX_CRIT_CHANCE = 0.75;

    private static SaveData cachedFor;
    private static Map<SkillEffectType, Double> cachedTotals;

    private SkillEffects() {}

    public static void invalidate() {
        cachedFor = null;
        cachedTotals = null;
    }

    private static synchronized Map<SkillEffectType, Double> totals() {
        SaveData data = SaveManager.load();
        if (cachedTotals == null || cachedFor != data) {
            cachedFor = data;
            cachedTotals = new SkillTreeState(data).getTotalEffects();
        }
        return cachedTotals;
    }

    /** Raw percent total for an effect (e.g. 13.0 for +13%). */
    private static double percent(SkillEffectType type) {
        Double value = totals().get(type);
        return value == null ? 0.0 : value;
    }

    private static double fraction(SkillEffectType type) {
        return percent(type) / 100.0;
    }

    public static double damageBonusFraction() {
        return fraction(SkillEffectType.DAMAGE_MULTIPLIER_PCT);
    }

    public static double maxHpBonusFraction() {
        return fraction(SkillEffectType.MAX_HP_PCT);
    }

    public static double moveSpeedBonusFraction() {
        return fraction(SkillEffectType.MOVE_SPEED_PCT);
    }

    public static double healingMultiplier() {
        return 1.0 + fraction(SkillEffectType.HEALING_RECEIVED_PCT);
    }

    /** Reload durations are multiplied by this; higher reload speed means a smaller value. */
    public static double reloadTimeMultiplier() {
        return 1.0 / (1.0 + fraction(SkillEffectType.RELOAD_SPEED_PCT));
    }

    public static double critChance() {
        return Math.min(MAX_CRIT_CHANCE, Math.max(0.0, fraction(SkillEffectType.CRIT_CHANCE_PCT)));
    }

    /** Rolls a crit, returning either 1.0 or {@link #CRIT_DAMAGE_MULTIPLIER}. */
    public static double rollCritMultiplier() {
        double chance = critChance();
        if (chance <= 0.0) {
            return 1.0;
        }
        return Math.random() < chance ? CRIT_DAMAGE_MULTIPLIER : 1.0;
    }

    public static double cashGainMultiplier() {
        return 1.0 + fraction(SkillEffectType.CASH_GAIN_PCT);
    }

    public static double gemGainMultiplier() {
        return 1.0 + fraction(SkillEffectType.GEM_GAIN_PCT);
    }

    /** Bias applied to loot tier weights; 0 means unmodified odds. */
    public static double lootQualityBonusFraction() {
        return fraction(SkillEffectType.LOOT_QUALITY_PCT);
    }

    /** Scales an earned cash reward. Spending and refunds should not use this. */
    public static int scaleCashGain(int amount) {
        if (amount <= 0) {
            return amount;
        }
        return (int) Math.round(amount * cashGainMultiplier());
    }

    /** Scales an earned gem reward. Spending and refunds should not use this. */
    public static int scaleGemGain(int amount) {
        if (amount <= 0) {
            return amount;
        }
        return (int) Math.round(amount * gemGainMultiplier());
    }
}
