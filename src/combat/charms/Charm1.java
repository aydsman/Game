package combat.charms;

public class Charm1 extends Charm {
    public Charm1() {
        this(1);
    }

    public Charm1(int tier) {
        super(tier);
        name = "Charm1";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
        // For now, just ensures tier is set correctly
    }
}
