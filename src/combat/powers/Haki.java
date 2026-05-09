package combat.powers;

public class Haki extends Power {
    public Haki() {
        this(3);
    }

    public Haki(int tier) {
        super(tier);
        name = "Haki";
        //iconPath;
        description = "It is a type of energy that can be harnessed to coat your body in invisible armor, predict enemy movements, or unleash your willpower to overpower those around you.";

        // Define moves using addMove()
        addMove(new Move("Armament Haki", 1)); // Coats hands and weapons in haki, increasing total damage, and increasing damage resistance.
        addMove(new Move("Observation Haki", 2)); // Allows user to be invincible to 6 attacks, simulating dodging.
        addMove(new Move("Conqueror's Haki", 3)); // Knocs out enemies within a radius for an amount of seconds. Once they are damaged, they instantly wake up.
        addMove(new Move("Advanced Haki", 4)); // slows down time, and gives COC haki coating, increasing effects of normal arm. haki.

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}
