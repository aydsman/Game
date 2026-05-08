package combat.summons;

public class Summon2 extends Summon {
    public Summon2() {
        this(2);
    }

    public Summon2(int tier) {
        super(tier);
        name = "Summon2";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}

