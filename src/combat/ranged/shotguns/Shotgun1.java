package combat.ranged.shotguns;

public class Shotgun1 extends Shotgun {

    public Shotgun1() {
        this(1); // Default to Tier I
    }

    public Shotgun1(int tier) {
        super(tier);
        // Shotgun1 specific stats (basic pump shotgun)
        damage = 3;
        magazineSize = 6;
        pelletCount = 6;
        name = "Shotgun1";
    }
}
