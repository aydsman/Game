package combat.powers;

public class Storm extends Power {
    public Storm() {
        this(2);
    }

    public Storm(int tier) {
        super(tier);
        // Storm: chain lightning and waterfield pressure.
        name = "Storm";
        iconPath = "assets/items/powers/storm/storm_icon.png";
        description = "Call violent weather to chain shocks and flood zones with static.";

        addMove(new Move("Chain Current", 1));
        addMove(new Move("Tidal Static", 2));
        addMove(new Move("Thunder Drift", 3));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
    }
}
