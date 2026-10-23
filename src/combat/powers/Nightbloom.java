package combat.powers;

public class Nightbloom extends Power {
    public Nightbloom() {
        this(4);
    }

    public Nightbloom(int tier) {
        super(tier);
        // Nightbloom: overgrowth + eclipse control with dark-vine pressure.
        name = "Nightbloom";
        iconPath = "assets/items/powers/nightbloom/nightbloom_icon.png";
        description = "Dark vines spread and bloom into lethal shadow-laced eruptions.";

        addMove(new Move("Nocturne Roots", 1));
        addMove(new Move("Bloom of Dusk", 2));
        addMove(new Move("Wither Garden", 3));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
    }
}
