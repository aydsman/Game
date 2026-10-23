package combat.summons;

public class Summon3 extends Summon {
    public Summon3() {
        this(1);
    }

    public Summon3(int tier) {
        super(tier);
        name = "Flower Pot";
        description = "He stuns enemies for a second every 10 seconds.";
        tickDamage = 4;
        tickIntervalMs = 10000;
        attackRangePx = 320;
        onHitStunMs = 1000;
        applySummonTier();
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}

