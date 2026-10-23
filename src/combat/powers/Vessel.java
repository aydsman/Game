package combat.powers;

public class Vessel extends Power {
    public Vessel() {
        this(2);
    }

    public Vessel(int tier) {
        super(tier);
        // Vessel: cursed-energy container that absorbs hits and converts them into pressure bursts.
        name = "Vessel";
        iconPath = "assets/items/powers/vessel/vessel_icon.png";
        description = "Contain volatile cursed energy, harden against incoming blows, and release it as a violent backlash.";

        addMove(new Move("Cursed Bind", 1));
        addMove(new Move("Resonant Shell", 2));
        addMove(new Move("Vessel Eruption", 3));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
    }
}
