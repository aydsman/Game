package combat.powers;

public class RinneSharingan extends Power {
    public RinneSharingan() {
        this(5);
    }

    public RinneSharingan(int tier) {
        super(tier);
        name = "Rinne Sharingan";
        iconPath = "assets/items/powers/rine-sharingan/rinnesharingan_icon.png";
        description = "The ultimate dojutsu combining Rinnegan and Sharingan abilities.";

        // Define moves using addMove()
        addMove(new Move("Kagutsuchi", 1));
        addMove(new Move("Tailed Beast Bomb", 2));
        addMove(new Move("Amenotejikara", 3));
        addMove(new Move("Kamui", 4));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}
