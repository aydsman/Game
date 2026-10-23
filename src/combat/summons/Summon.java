package combat.summons;

import combat.Item;

/**
 * Equipped companion. Combat numbers live here so {@link SummonRuntime} can tick any summon
 * without a per-class switch.
 */
public class Summon extends Item {

    protected int tickDamage = 8;
    protected long tickIntervalMs = 1800;
    protected int attackRangePx = 280;
    protected long onHitStunMs = 0;

    public Summon() {
        super();
        name = "Summon";
    }

    public Summon(int tier) {
        super(tier);
        name = "Summon";
    }

    /** Scales the combat numbers that the subclass just assigned. */
    protected void applySummonTier() {
        if (tier <= 1) {
            return;
        }
        tickDamage = (int) Math.round(tickDamage * (1.0 + 0.15 * (tier - 1)));
        tickIntervalMs = Math.max(400, Math.round(tickIntervalMs * (1.0 - 0.06 * (tier - 1))));
        attackRangePx = (int) Math.round(attackRangePx * (1.0 + 0.05 * (tier - 1)));
    }

    public int getTickDamage() {
        return tickDamage;
    }

    public long getTickIntervalMs() {
        return tickIntervalMs;
    }

    public int getAttackRangePx() {
        return attackRangePx;
    }

    public long getOnHitStunMs() {
        return onHitStunMs;
    }
}
