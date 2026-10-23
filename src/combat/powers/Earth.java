package combat.powers;

public class Earth extends Power {
    public Earth() {
        this(1);
    }

    public Earth(int tier) {
        super(tier);
        name = "Earth";
        iconPath = "assets/items/powers/earth/earth_icon.png";
        description = "Command the earth to create barriers and crush enemies.";
        
        addMove(new Move("Rock Barrage", 1, 280, 900, null));
        addMove(new Move("Spike Ring", 2, 2500, 7500, null));
        addMove(new Move("Boulder Barrage", 3, 900, 9500, null));
        
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}
