package combat.powers;

public class MagmaV2 extends Power {
    public MagmaV2() {
        this(4);
    }

    public MagmaV2(int tier) {
        super(tier);
        name = "MagmaV2";
        //iconPath = "assets/items/powers/magma/magma_icon.png";
        description = "Enhanced magma manipulation abilities.";

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
