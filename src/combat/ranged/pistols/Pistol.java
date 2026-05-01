package combat.ranged.pistols;

import combat.Ranged;

public class Pistol extends Ranged {

    public Pistol() {
        // base pistol stats: fast fire rate, low damage, high accuracy
        fireRate = 0.3; // seconds between shots
        damage = 10;
        accuracy = 0.95;
        accuracyAngle = 15.0; // ±15 degrees spread
        magazineSize = 12;
        reloadTime = 1.5;
        currentAmmo = magazineSize;

        // default pistol visual properties
        barrelColor = "black";
        barrelLength = 35; // horizontal length from center
        barrelHeight = 14; // vertical height (7 above, 7 below center)
        name = "Pistol";
        automatic = false;
    }

    public Pistol(int tier) {
        super(tier);
        // base pistol stats: fast fire rate, low damage, high accuracy
        fireRate = 0.3; // seconds between shots
        damage = 40;
        accuracy = 0.85;
        accuracyAngle = 15.0; // ±15 degrees spread
        magazineSize = 12;
        reloadTime = 1.5;
        currentAmmo = magazineSize;

        // default pistol visual properties
        barrelColor = "black";
        barrelLength = 35; // horizontal length from center
        barrelHeight = 14; // vertical height (7 above, 7 below center)
        name = "Pistol";
        automatic = false;

        // Apply tier multipliers after setting base stats
        applyTierMultipliers();
    }
}
