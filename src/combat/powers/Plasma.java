package combat.powers;

public class Plasma extends Power {
    public Plasma() {
        this(3);
    }

    public Plasma(int tier) {
        super(tier);
        // Plasma: high-speed energy strikes with burst windows.
        name = "Plasma";
        iconPath = "assets/items/powers/plasma/plasma_icon.png";
        description = "Superheated arcs and ionized blasts overwhelm enemies in rapid bursts.";

        addMove(new Move("Ion Bolt", 1));
        addMove(new Move("Arc Furnace", 2));
        addMove(new Move("Volt Eruption", 3));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
    }
}
