package combat.powers;

public class Limitless extends Power {
    public Limitless() {
        this(2);
    }

    public Limitless(int tier) {
        super(tier);
        // Limitless: space manipulation built around pull/push pressure and clutch repositioning.
        name = "Limitless";
        iconPath = "assets/items/powers/limitless/limitless_icon.png";
        description = "Bend space around you to attract, repel, and slip through gaps your enemies can't reach.";

        addMove(new Move("Blue", 1));
        addMove(new Move("Red", 2));
        addMove(new Move("Cursed Distance", 3));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
    }
}
