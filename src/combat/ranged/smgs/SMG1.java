package combat.ranged.smgs;

public class SMG1 extends SMG {

    public SMG1() {
        this(1); // Default to Tier I
    }

    public SMG1(int tier) {
        super(tier);
        // SMG1 specific stats (basic submachine gun)
        damage = 2;
        magazineSize = 20;
        fireRate = 0.1;
        name = "SMG1";
    }
}
