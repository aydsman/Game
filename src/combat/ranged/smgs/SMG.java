package combat.ranged.smgs;

import combat.Ranged;

public class SMG extends Ranged {

    public SMG() {
        // base SMG stats: very fast fire rate, low damage, medium-low accuracy
        fireRate = 0.08; // seconds between shots (very fast)
        damage = 2;
        accuracy = 0.75;
        magazineSize = 25;
        reloadTime = 1.8;

        // SMG visual properties
        barrelColor = "red";
        barrelLength = 40;
        barrelHeight = 14;
        name = "SMG";
        automatic = true;
    }

    public SMG(int tier) {
        super(tier);
        // base SMG stats: very fast fire rate, low damage, medium-low accuracy
        fireRate = 0.08; // seconds between shots (very fast)
        damage = 2;
        accuracy = 0.75;
        magazineSize = 25;
        reloadTime = 1.8;

        // SMG visual properties
        barrelColor = "red";
        barrelLength = 40;
        barrelHeight = 14;
        name = "SMG";
        automatic = true;
    }
}
