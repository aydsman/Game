package combat.summons;

public class Summon1 extends Summon {
    public Summon1() {
        this(1);
    }

    public Summon1(int tier) {
        super(tier);
        name = "Slime";
        description = "A small slime that shoots slime pellets at enemies";
        tickDamage = 10;
        tickIntervalMs = 1400;
        attackRangePx = 260;
        applySummonTier();
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}

