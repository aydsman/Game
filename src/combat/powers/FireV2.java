package combat.powers;

public class FireV2 extends Power {
    public FireV2() {
        this(4);
    }

    public FireV2(int tier) {
        super(tier);
        name = "FireV2";
        //iconPath = "assets/items/powers/fire/fire_icon.png";
        description = "Enhanced fire manipulation abilities.";

        // Define moves using addMove()
        addMove(new Move("Blank", 1));
        addMove(new Move("Blank", 2));
        addMove(new Move("Blank", 3));
        addMove(new Move("Blank", 4));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}
