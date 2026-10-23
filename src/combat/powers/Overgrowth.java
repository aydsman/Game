package combat.powers;

public class Overgrowth extends Power {
    public Overgrowth() {
        this(2);
    }

    public Overgrowth(int tier) {
        super(tier);
        name = "Overgrowth";
        iconPath = "assets/items/powers/overgrowth/overgrowth_icon.png";
        description = "Command vines and roots to snare foes, block paths, and strike from the canopy.";

        addMove(new Move("Vine Lash", 1));
        addMove(new Move("Root Snare", 2));
        addMove(new Move("Canopy Crash", 3));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
    }
}
