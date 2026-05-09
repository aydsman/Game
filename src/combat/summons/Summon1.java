package combat.summons;

public class Summon1 extends Summon {
    public Summon1() {
        this(1);
    }

    public Summon1(int tier) {
        super(tier);
        name = "Slime";
        description = "A small slime that shoots slime pellets at enemies";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}

