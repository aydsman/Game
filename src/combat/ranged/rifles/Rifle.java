package combat.ranged.rifles;

import combat.Ranged;

public class Rifle extends Ranged {

    public Rifle() {
        // base rifle stats: medium fire rate, medium-high damage, medium accuracy
        fireRate = 0.15; // seconds between shots
        damage = 3;
        accuracy = 0.85;
        accuracyAngle = 20.0; // ±20 degrees spread
        magazineSize = 25;
        reloadTime = 2.0;
        currentAmmo = magazineSize;

        // default rifle visual properties
        barrelColor = "darkgreen";
        barrelLength = 50; // medium barrel length
        barrelHeight = 14; // medium height (7 above, 7 below center)
        name = "Rifle";
        automatic = true;
    }

    public Rifle(int tier) {
        super(tier);
        // base rifle stats: medium fire rate, medium-high damage, medium accuracy
        fireRate = 0.15; // seconds between shots
        damage = 6;
        accuracy = 0.85;
        accuracyAngle = 20.0; // ±20 degrees spread
        magazineSize = 25;
        reloadTime = 2.0;
        currentAmmo = magazineSize;

        // default rifle visual properties
        barrelColor = "darkgreen";
        barrelLength = 50; // medium barrel length
        barrelHeight = 14; // medium height (7 above, 7 below center)
        name = "Rifle";
        automatic = true;

        // Apply tier multipliers after setting base stats
        applyTierMultipliers();
    }
}
