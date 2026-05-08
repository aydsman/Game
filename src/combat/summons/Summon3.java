package combat.summons;

public class Summon3 extends Summon {
    public Summon3() {
        this(3);
    }

    public Summon3(int tier) {
        super(tier);
        name = "Summon3";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}

