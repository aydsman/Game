package combat.powers;

public class Cataclysm extends Power {
    public Cataclysm() {
        this(5);
    }

    public Cataclysm(int tier) {
        super(tier);
        // Cataclysm: endgame devastation via magma + eclipse.
        name = "Cataclysm";
        iconPath = "assets/items/powers/cataclysm/cataclysm_icon.png";
        description = "Unleash apocalyptic ruptures that scorch and shatter entire battlefields.";

        addMove(new Move("Rift of Cinders", 1));
        addMove(new Move("Void Eruption", 2));
        addMove(new Move("Apocalypse Core", 3));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
    }
}
