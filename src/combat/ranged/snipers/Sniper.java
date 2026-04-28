package combat.ranged.snipers;

import combat.Ranged;

public class Sniper extends Ranged {

    public Sniper() {
        // base sniper stats: very slow fire rate, very high damage, very high accuracy, long range
        fireRate = 1.2; // seconds between shots (very slow)
        damage = 7;
        accuracy = 0.98;
        accuracyAngle = 5.0; // ±5 degrees spread (very accurate)
        magazineSize = 5;
        reloadTime = 3.0;
        currentAmmo = magazineSize;

        // default sniper visual properties
        barrelColor = "blue";
        barrelLength = 70; // longest barrel length
        barrelHeight = 12; // thin height (6 above, 6 below center)
        name = "Sniper";
        automatic = false;
    }

    public Sniper(int tier) {
        super(tier);
        // base sniper stats: very slow fire rate, very high damage, very high accuracy, long range
        fireRate = 1.2; // seconds between shots (very slow)
        damage = 7;
        accuracy = 0.98;
        accuracyAngle = 5.0; // ±5 degrees spread (very accurate)
        magazineSize = 5;
        reloadTime = 3.0;
        currentAmmo = magazineSize;

        // default sniper visual properties
        barrelColor = "blue";
        barrelLength = 70; // longest barrel length
        barrelHeight = 12; // thin height (6 above, 6 below center)
        name = "Sniper";
        automatic = false;
    }
}
