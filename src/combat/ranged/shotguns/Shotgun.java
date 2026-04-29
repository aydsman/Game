package combat.ranged.shotguns;

import combat.Projectile;
import combat.Ranged;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Shotgun extends Ranged {

    protected int pelletCount;

    public Shotgun() {
        // base shotgun stats: slow fire rate, high damage, spread pattern, close range
        fireRate = 0.4; // seconds between shots (faster firing)
        damage = 3;
        accuracy = 0.6;
        accuracyAngle = 15.0; // ±15 degrees spread for tighter pellet pattern
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
        fireRate = 0.4; // seconds between shots (faster firing)
        damage = 2;
        accuracy = 0.6;
        accuracyAngle = 15.0; // ±15 degrees spread for tighter pellet pattern
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

        // Apply tier multipliers after setting base stats
        applyTierMultipliers();
    }

    @Override
    public List<Projectile> shoot(int centerX, int centerY, double barrelAngle) {
        if (currentAmmo <= 0) {
            return null;
        }

        currentAmmo--;

        List<Projectile> pellets = new ArrayList<>();
        int[] tip = getBarrelTip(centerX, centerY, barrelAngle);
        Color projectileColor = getProjectileColor();

        for (int i = 0; i < pelletCount; i++) {
            // Each pellet gets a random spread within the accuracy angle
            double spreadRadians = Math.toRadians(accuracyAngle);
            double randomSpread = (Math.random() - 0.5) * 2.0 * spreadRadians;
            double pelletAngle = barrelAngle + randomSpread;

            // Smaller radius (3) for pellets
            Projectile pellet = new Projectile(tip[0], tip[1], projectileColor, 10.0, pelletAngle, damage, 3);
            pellets.add(pellet);
        }

        return pellets;
    }
}
