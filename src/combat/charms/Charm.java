package combat.charms;

import combat.Item;

public class Charm extends Item {

    public Charm() {
        super();
        this.name = "Charm";
    }

    public Charm(int tier) {
        super(tier);
        this.name = "Charm";
    }

    /**
     * Extra max HP as a fraction of the player's {@code baseMaxHp} (e.g. {@code 0.1} = +10%).
     * Summed across all equipped charm slots; default is no bonus.
     */
    public double getMaxHpBonusFraction() {
        return 0;
    }

    /**
     * Extra movement speed as a fraction of the player's {@code baseSpeed} (e.g. {@code 0.05} = +5%).
     * Summed across all equipped charm slots; default is no bonus.
     */
    public double getSpeedBonusFraction() {
        return 0;
    }

    /**
     * Extra damage multiplier as a fraction of the player's {@code baseDamage} (e.g. {@code 0.1} = +10%).
     * Applied in {@code Player.recalculateCharmEffects}; default is no bonus.
     */
    public double getDamageBonusFraction() {
        return 0;
    }

    /** Tier scaling: T1 = 1.0, each tier above +5%. */
    protected double tierMultiplier() {
        int t = getTier();
        if (t < 1) {
            t = 1;
        }
        return 1.0 + 0.05 * (t - 1);
    }
}
