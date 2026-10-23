package combat.powers;

public class Infinity extends Power {
    public Infinity() {
        this(5);
    }

    public Infinity(int tier) {
        super(tier);
        name = "Infinity";
        iconPath = "assets/items/powers/infinity/infinity_icon.png";
        description = "Limitless cursed technique - manipulate space at the atomic level.";

        // Define moves using addMove()
        addMove(new Move("Limitless Barrier", 1));
        addMove(new Move("Blue", 2));
        addMove(new Move("Red", 3));
        addMove(new Move("Hollow Purple", 4));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}
