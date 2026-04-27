package combat.melee.hammers;

public class Hammer1 extends Hammer {

    public Hammer1() {
        this(1); // Default to Tier I
    }

    public Hammer1(int tier) {
        super(tier);
        // Hammer1 specific stats (basic starter hammer)
        damage = 35;
        attackSpeed = 0.7;
    }
}
