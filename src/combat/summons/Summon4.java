package combat.summons;

public class Summon4 extends Summon {
    public Summon4() {
        this(4);
    }

    public Summon4(int tier) {
        super(tier);
        name = "Summon4";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}

