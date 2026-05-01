package combat.melee.swords;

public class Sword1 extends Sword {

    public Sword1() {
        this(1); // Default to Tier I
    }

    public Sword1(int tier) {
        super(tier);
        // Sword1 specific stats (basic starter sword)
        damage = 180;
        attackSpeed = 1.0;
        name = "Sword1";
        // Re-apply tier multipliers after setting base stats
        applyTierMultipliers();
    }
}
