package combat.ranged.smgs;

public class SMG1 extends SMG {

    public SMG1() {
        this(1); // Default to Tier I
    }

    public SMG1(int tier) {
        super(tier);
        // SMG1 specific stats (basic submachine gun)
        damage = 20;
        magazineSize = 20;
        fireRate = 0.1;
        name = "P90";
        iconPath = "assets/items/ranged/smgs/p90.png";
        description = "This SMG has a very high fire rate, but deals less damage per shot.";
        // Re-apply tier multipliers after setting base damage
        applyTierMultipliers();
    }
}
