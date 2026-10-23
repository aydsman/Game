package combat.powers;

public class Eclipse extends Power {
    public Eclipse() {
        this(3);
    }

    public Eclipse(int tier) {
        super(tier);
        // Eclipse: light + shadow fusion with burst and veil utility.
        name = "Eclipse";
        iconPath = "assets/items/powers/eclipse/eclipse_icon.png";
        description = "Blend radiance and darkness into devastating bursts and shrouded control.";

        addMove(new Move("Totality Burst", 1));
        addMove(new Move("Penumbra Veil", 2));
        addMove(new Move("Solar-Lunar Arc", 3));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
    }
}
