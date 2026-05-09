package combat.powers;

public class Sharingan extends Power {
    public Sharingan() {
        this(5);
    }

    public Sharingan(int tier) {
        super(tier);
        name = "Sharingan";
        iconPath = "assets/items/powers/sharingan/sharingan_icon.png";
        description = "The dojutsu that grants the user the ability to see chakra and predict movements.";

        // Define moves using addMove()
        addMove(new Move("Genjutsu", 1));
        addMove(new Move("Illusion", 2));
        addMove(new Move("Copy Technique", 3));
        addMove(new Move("Fire Style", 4));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}
