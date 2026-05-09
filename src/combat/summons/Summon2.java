package combat.summons;

public class Summon2 extends Summon {
    public Summon2() {
        this(2);
    }

    public Summon2(int tier) {
        super(tier);
        name = "Lil Volcano";
        description = "A little volcano that shoots enemies with lava.";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}