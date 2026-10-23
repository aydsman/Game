package combat.powers;

public class Crystal extends Power {
    public Crystal() {
        this(2);
    }

    public Crystal(int tier) {
        super(tier);
        // Crystal: defensive zoning with shards, walls, and spikes.
        name = "Crystal";
        iconPath = "assets/items/powers/crystal/crystal_icon.png";
        description = "Forge crystalline barriers and jagged spikes to dominate space.";

        addMove(new Move("Prism Lance", 1));
        addMove(new Move("Facet Wall", 2));
        addMove(new Move("Spire Eruption", 3));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
    }
}
