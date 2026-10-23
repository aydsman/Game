package combat.ranged.shotguns;

import combat.CombatTuning;
import combat.Projectile;
import combat.Ranged;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Close-range weapon. Its identity comes from damage drop-off: every pellet carries full weapon
 * damage, so without decay a shotgun out-bursts a sniper at any range it can reach. Drop-off keeps
 * the burst but confines it to the range the weapon is meant to own.
 */
public class Shotgun extends Ranged {

    protected int pelletCount;

    public Shotgun() {
        // base shotgun stats: slow fire rate, high damage, spread pattern, close range
        fireRate = 0.4; // seconds between shots (faster firing)
        damage = 30;
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
        damage = 20;
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
        return shoot(centerX, centerY, barrelAngle, 1.0);
    }

    @Override
    public List<Projectile> shoot(int centerX, int centerY, double barrelAngle, double playerDamageMultiplier) {
        return shoot(centerX, centerY, barrelAngle, playerDamageMultiplier, false);
    }

    @Override
    public List<Projectile> shoot(int centerX,
                                  int centerY,
                                  double barrelAngle,
                                  double playerDamageMultiplier,
                                  boolean critical) {
        if (currentAmmo <= 0) {
            return null;
        }

        currentAmmo--;

        List<Projectile> pellets = new ArrayList<>();
        int[] tip = getBarrelTip(centerX, centerY, barrelAngle);
        Color projectileColor = getProjectileColor();
        int totalDamage = (int) (damage * playerDamageMultiplier);

        for (int i = 0; i < pelletCount; i++) {
            // Each pellet gets a random spread within the accuracy angle
            double spreadRadians = Math.toRadians(accuracyAngle);
            double randomSpread = (Math.random() - 0.5) * 2.0 * spreadRadians;
            double pelletAngle = barrelAngle + randomSpread;

            // Smaller radius (3) for pellets
            Projectile pellet = new Projectile(tip[0], tip[1], projectileColor, 10.0, pelletAngle, totalDamage, 3);
            pellet.setCritical(critical);
            configureProjectile(pellet);
            pellets.add(pellet);
        }

        return pellets;
    }

    @Override
    protected void configureProjectile(Projectile projectile) {
        CombatTuning tuning = CombatTuning.active();
        projectile.configureFalloff(
                tuning.getShotgunFalloffStartPx(),
                tuning.getShotgunFalloffEndPx(),
                tuning.getShotgunFalloffMinFraction());
    }
}
