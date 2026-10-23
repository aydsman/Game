package combat.powers;

public class Magma extends Power {
    public Magma() {
        this(2);
    }

    public Magma(int tier) {
        super(tier);
        name = "Magma";
        iconPath = "assets/items/powers/magma/magma_icon.png";
        description = "Harness molten lava to overwhelm your enemies.";

        // Shares the same behavior shape as Fire's projectile bomb with tuned values.
        addTemplatedMove("Volcanic Missiles", 1, MoveTemplates.projectileBomb(
                2600, 34, 850, 85,
                "assets/effects/magma/magma_bomb_cast.png",
                "assets/effects/magma/magma_bomb_hit.png"
        ));
        addMove("Lava Ring", 2);
        addMove("Magma Fist", 3);

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}
