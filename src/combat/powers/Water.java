package combat.powers;

public class Water extends Power {
    public Water() {
        this(1);
    }

    public Water(int tier) {
        super(tier);
        name = "Water";
        iconPath = "assets/items/powers/water/water_icon.png";
        description = "Control the tides to freeze, drown, or wash away enemies.";
        
        // Define moves using addMove()
        addMove(new Move("Hydro Slash", 1));
        addMove(new Move("Current Beam", 2));
        addMove(new Move("Tidal Crash", 3));
        
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}
