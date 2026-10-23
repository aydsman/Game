package combat.powers;

public class LightningV2 extends Power {
    public LightningV2() {
        this(4);
    }

    public LightningV2(int tier) {
        super(tier);
        name = "LightningV2";
        //iconPath = "assets/items/powers/lightning/lightning_icon.png";
        description = "Enhanced lightning manipulation abilities.";

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
