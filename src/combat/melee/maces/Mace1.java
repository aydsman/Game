package combat.melee.maces;

public class Mace1 extends Mace {

    public Mace1() {
        this(1); // Default to Tier I
    }

    public Mace1(int tier) {
        super(tier);
        // Mace1 specific stats (basic starter mace)
        damage = 25;
        attackSpeed = 0.9;
    }
}
