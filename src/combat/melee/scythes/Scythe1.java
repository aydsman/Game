package combat.melee.scythes;

public class Scythe1 extends Scythe {

    public Scythe1() {
        this(1); // Default to Tier I
    }

    public Scythe1(int tier) {
        super(tier);
        // Scythe1 specific stats (basic starter scythe)
        damage = 30;
        attackSpeed = 0.5;
        name = "Scythe1";
    }
}
