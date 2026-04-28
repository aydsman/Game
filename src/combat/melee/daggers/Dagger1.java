package combat.melee.daggers;

public class Dagger1 extends Dagger {

    public Dagger1() {
        this(1); // Default to Tier I
    }

    public Dagger1(int tier) {
        super(tier);
        // Dagger1 specific stats (basic starter dagger)
        damage = 10;
        attackSpeed = 2.0;
        name = "Dagger1";
    }
}
