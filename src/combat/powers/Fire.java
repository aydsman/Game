package combat.powers;

public class Fire extends Power {
    public Fire() {
        this(1);
    }

    public Fire(int tier) {
        super(tier);
        name = "Fire";
        iconPath = "assets/items/powers/fire/fire_icon.png";
        description = "Harness the power of flames to burn your enemies.";

        addMove(new Move("Flame Burst", 1, 400, 1400, null));
        addMove(new Move("Trail of Fire", 2, 2500, 6500, null));
        addMove(new Move("Grand Fireball", 3, 900, 9000, null));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}
