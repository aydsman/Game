package combat.consumables;

import combat.Item;

public class Consumable extends Item {

    public enum ConsumableEffectType {
        NONE,
        HEALTH_RESTORE,
        DAMAGE_BOOST,
        SPEED_BOOST,
        DEFENSE_BOOST
    }

    protected double healthRestorePercent = 0.0; // 0.0 to 1.0 representing 0% to 100%
    protected long durationMs = 0; // Duration of effect in milliseconds (0 = instant)
    protected boolean showStatus = false; // If true, show icon and timer in top-left
    protected ConsumableEffectType effectType = ConsumableEffectType.HEALTH_RESTORE;
    protected double effectMultiplier = 1.0; // Multiplier for buff effects (e.g., 2.0 for 2x damage)

    public Consumable() {
        super();
        name = "Consumable";
    }

    public Consumable(int tier) {
        super(tier);
        name = "Consumable";
    }

    public double getHealthRestorePercent() {
        return healthRestorePercent;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public boolean isShowStatus() {
        return showStatus;
    }

    public ConsumableEffectType getEffectType() {
        return effectType;
    }

    public double getEffectMultiplier() {
        return effectMultiplier;
    }

    @Override
    public Consumable clone() {
        Consumable cloned = (Consumable) super.clone();
        cloned.healthRestorePercent = this.healthRestorePercent;
        cloned.durationMs = this.durationMs;
        cloned.showStatus = this.showStatus;
        cloned.effectType = this.effectType;
        cloned.effectMultiplier = this.effectMultiplier;
        return cloned;
    }
}
