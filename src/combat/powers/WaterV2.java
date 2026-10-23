package combat.powers;

public class WaterV2 extends Power {
    public WaterV2() {
        this(4);
    }

    public WaterV2(int tier) {
        super(tier);
        name = "WaterV2";
        //iconPath = "assets/items/powers/water/water_icon.png";
        description = "Enhanced water manipulation abilities.";

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
