package combat.ranged.pistols;

public class Pistol1 extends Pistol {

    public Pistol1() {
        this(1); // Default to Tier I
    }

    public Pistol1(int tier) {
        super(tier);
        // Pistol1 specific stats (basic starter pistol)
        // damage inherited from Pistol (4 for tier 1)
        magazineSize = 10;
        currentAmmo = magazineSize;

        // Pistol1 visual properties - gray barrel, white bullets
        barrelColor = "black";
        projectileColor = "white";
        barrelLength = 40; // shorter barrel for starter pistol
        barrelHeight = 14; // smaller height (7 above, 7 below center)
        name = "Pistol1";
    }
}
