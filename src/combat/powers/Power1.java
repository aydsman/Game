package combat.powers;

public class Power1 extends Power {
    public Power1() {
        this(1);
    }

    public Power1(int tier) {
        super(tier);
        name = "Power1";
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}
