package combat.ranged.shotguns;

import combat.Ranged;

public class Shotgun extends Ranged {

    protected int pelletCount;

    public Shotgun() {
        // base shotgun stats: slow fire rate, high damage, spread pattern, close range
        fireRate = 0.8; // seconds between shots
        damage = 3;
        accuracy = 0.6;
        magazineSize = 8;
        reloadTime = 2.5;
        pelletCount = 8; // shotguns fire multiple pellets
        currentAmmo = magazineSize;

        // default shotgun visual properties
        barrelColor = "brown";
        barrelLength = 55; // large barrel length
        barrelHeight = 16; // large height (8 above, 8 below center)
        name = "Shotgun";
        automatic = false;
    }

    public Shotgun(int tier) {
        super(tier);
        // base shotgun stats: slow fire rate, high damage, spread pattern, close range
        fireRate = 0.8; // seconds between shots
        damage = 3;
        accuracy = 0.6;
        magazineSize = 8;
        reloadTime = 2.5;
        pelletCount = 8; // shotguns fire multiple pellets
        currentAmmo = magazineSize;

        // default shotgun visual properties
        barrelColor = "brown";
        barrelLength = 55; // large barrel length
        barrelHeight = 16; // large height (8 above, 8 below center)
        name = "Shotgun";
        automatic = false;
    }
}
