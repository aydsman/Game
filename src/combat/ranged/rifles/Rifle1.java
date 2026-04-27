package combat.ranged.rifles;

public class Rifle1 extends Rifle {

    public Rifle1() {
        this(1); // Default to Tier I
    }

    public Rifle1(int tier) {
        super(tier);
        // Rifle1 specific stats (basic assault rifle)
        damage = 3;
        magazineSize = 25;
    }
}
