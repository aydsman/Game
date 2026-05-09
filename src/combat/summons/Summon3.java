package combat.summons;

public class Summon3 extends Summon {
    public Summon3() {
        this(1);
    }

    public Summon3(int tier) {
        super(tier);
        name = "Flower Pot";
        description = "He stuns enemies for a second every 10 seconds.";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}

