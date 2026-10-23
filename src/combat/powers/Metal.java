package combat.powers;

public class Metal extends Power {
    public Metal() {
        this(2);
    }

    public Metal(int tier) {
        super(tier);
        // Metal: armored bruiser that hardens defenses and lashes out with shrapnel.
        name = "Metal";
        iconPath = "assets/items/powers/metal/metal_icon.png";
        description = "Shape iron skin and razor shards to lock down lanes and punish reckless pushes.";

        addMove(new Move("Iron Shard", 1));
        addMove(new Move("Steel Aegis", 2));
        addMove(new Move("Shrapnel Burst", 3));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
    }
}
