package combat.ranged.snipers;

public class Sniper1 extends Sniper {

    public Sniper1() {
        this(1); // Default to Tier I
    }

    public Sniper1(int tier) {
        super(tier);
        // Sniper1 specific stats (basic bolt-action sniper)
        damage = 100;
        magazineSize = 5;
        fireRate = 1.5;
        name = "Sniper1";
        // Re-apply tier multipliers after setting base damage
        applyTierMultipliers();
    }
}
