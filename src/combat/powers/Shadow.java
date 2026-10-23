package combat.powers;

public class Shadow extends Power {
    public Shadow() {
        this(1);
    }

    public Shadow(int tier) {
        super(tier);
        // Shadow: control + mobility through darkness pressure.
        name = "Shadow";
        iconPath = "assets/items/powers/shadow/shadow_icon.png";
        description = "Manipulate darkness to snare, reposition, and strike from the blind spots.";

        addMove(new Move("Umbral Snare", 1));
        addMove(new Move("Shade Step", 2));
        addMove(new Move("Dark Pulse", 3));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
    }
}
