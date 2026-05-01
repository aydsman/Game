package combat.ranged.shotguns;

public class Shotgun1 extends Shotgun {

    public Shotgun1() {
        this(1); // Default to Tier I
    }

    public Shotgun1(int tier) {
        super(tier);
        // Shotgun1 specific stats (basic pump shotgun)
        damage = 30;
        magazineSize = 6;
        pelletCount = 6;
        name = "Pump - Action";
        iconPath = "assets/items/ranged/shotguns/pump.png";
        description = "This shotgun deals the most amount of damage up close, but has less range.";
        // Re-apply tier multipliers after setting base damage
        applyTierMultipliers();
    }
}
