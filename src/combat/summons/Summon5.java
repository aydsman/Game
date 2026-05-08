package combat.summons;

public class Summon5 extends Summon {
    public Summon5() {
        this(5);
    }

    public Summon5(int tier) {
        super(tier);
        name = "Summon5";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}

