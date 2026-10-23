package combat.powers;

public class EarthV2 extends Power {
    public EarthV2() {
        this(4);
    }

    public EarthV2(int tier) {
        super(tier);
        name = "EarthV2";
        //iconPath = "assets/items/powers/earth/earth_icon.png";
        description = "Enhanced earth manipulation abilities.";

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
