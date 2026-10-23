package combat.powers;

public class Light extends Power {
    public Light() {
        this(1);
    }

    public Light(int tier) {
        super(tier);
        name = "Light";
        iconPath = "assets/items/powers/light/light_icon.png";
        description = "Harness the power of divine light to heal and protect.";
        
        // Define moves using addMove()
        addMove(new Move("Laser Storm", 1));
        addMove(new Move("Light Nova", 2));
        addMove(new Move("Photon Cannon", 3));
        
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}
