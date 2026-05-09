package combat.powers;

public class Rinnegan extends Power {
    public Rinnegan() {
        this(5);
    }

    public Rinnegan(int tier) {
        super(tier);
        name = "Rinnegan";
        iconPath = "assets/items/powers/rinnegan/rinnegan_icon.png";
        description = "The ultimate dojutsu, granting access to all six paths of the Rinnegan.";

        // Define moves using addMove()
        addMove(new Move("Human Path", 1));
        addMove(new Move("Preta Path", 2));
        addMove(new Move("Asura Path", 3));
        addMove(new Move("Animal Path", 4));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}
