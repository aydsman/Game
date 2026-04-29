package combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Ranged extends Item {

    protected double fireRate; // seconds between shots
    protected int damage; // damage per shot
    protected double accuracy; // 0.0 to 1.0 (100%)
    protected double accuracyAngle; // degrees of spread (e.g., 10 = ±10 degrees from barrel direction)
    protected int magazineSize; // bullets per magazine
    protected double reloadTime; // seconds to reload
    protected int currentAmmo; // current ammo in magazine
    protected boolean automatic; // true for automatic, false for semi-automatic
    protected String barrelColor; // color of the barrel (e.g., "red", "blue", "black")
    protected String projectileColor; // color of the projectile (e.g., "red", "blue", "white")
    protected int barrelLength; // length of the barrel (horizontal, extends right from center)
    protected int barrelHeight; // height of the barrel (vertical, centered on dot)
    protected boolean isReloading = false; // reload state
    protected long reloadStartTime = 0; // when reload started

    public Ranged() {
        super();
        // default stats (can be overridden by subclasses)
        fireRate = 0.5;
        damage = 10;
        accuracy = 0.8;
        accuracyAngle = 15.0; // ±15 degrees spread
        magazineSize = 10;
        reloadTime = 2.0;
        currentAmmo = magazineSize;

        // default visual properties (can be overridden by subclasses)
        barrelColor = "black";
        projectileColor = "black"; // default projectile matches barrel
        barrelLength = 35;
        barrelHeight = 12;
        name = "Weapon";
        automatic = false;
    }

    public Ranged(int tier) {
        super(tier);
        // default stats (can be overridden by subclasses)
        fireRate = 0.5;
        damage = 10;
        accuracy = 0.8;
        accuracyAngle = 15.0; // ±15 degrees spread
        magazineSize = 10;
        reloadTime = 2.0;
        currentAmmo = magazineSize;

        // default visual properties (can be overridden by subclasses)
        barrelColor = "black";
        projectileColor = "black"; // default projectile matches barrel
        barrelLength = 35;
        barrelHeight = 12;
        name = "Weapon";
        automatic = false;

        applyTierMultipliers();
    }

    protected void applyTierMultipliers() {
        double damageMultiplier = 1.0;
        double fireRateMultiplier = 1.0;
        double accuracyMultiplier = 1.0;
        double reloadTimeMultiplier = 1.0;
        int magazineBonus = 0;
        double accuracyAngleReduction = 0.0; // degrees to reduce spread

        switch (tier) {
            case 1: // Tier I - Common (no boost)
                damageMultiplier = 1.0;
                fireRateMultiplier = 1.0;
                accuracyMultiplier = 1.0;
                reloadTimeMultiplier = 1.0;
                magazineBonus = 0;
                accuracyAngleReduction = 0.0;
                break;
            case 2: // Tier II - Uncommon (minimal boost)
                damageMultiplier = 1.1;
                fireRateMultiplier = 1.05;
                accuracyMultiplier = 1.05;
                reloadTimeMultiplier = 0.95;
                magazineBonus = 2;
                accuracyAngleReduction = 2.0;
                break;
            case 3: // Tier III - Rare (minimal boost)
                damageMultiplier = 1.2;
                fireRateMultiplier = 1.1;
                accuracyMultiplier = 1.1;
                reloadTimeMultiplier = 0.9;
                magazineBonus = 4;
                accuracyAngleReduction = 4.0;
                break;
            case 4: // Tier IV - Epic (significantly better boost)
                damageMultiplier = 1.6;
                fireRateMultiplier = 1.25;
                accuracyMultiplier = 1.2;
                reloadTimeMultiplier = 0.8;
                magazineBonus = 8;
                accuracyAngleReduction = 8.0;
                break;
            case 5: // Tier V - Legendary (best boost - much better)
                damageMultiplier = 2.0;
                fireRateMultiplier = 1.4;
                accuracyMultiplier = 1.25;
                reloadTimeMultiplier = 0.7;
                magazineBonus = 12;
                accuracyAngleReduction = 12.0;
                break;
        }

        damage *= damageMultiplier;
        fireRate /= fireRateMultiplier; // Lower fireRate = faster shooting
        accuracy = Math.min(0.95, accuracy * accuracyMultiplier); // Cap at 95%
        reloadTime *= reloadTimeMultiplier; // Lower reloadTime = faster reload
        magazineSize += magazineBonus;
        accuracyAngle = Math.max(2.0, accuracyAngle - accuracyAngleReduction); // Minimum 2 degrees spread
        currentAmmo = magazineSize;

        // Round to 2 decimal places
        fireRate = Math.round(fireRate * 100.0) / 100.0;
        accuracy = Math.round(accuracy * 100.0) / 100.0;
        reloadTime = Math.round(reloadTime * 100.0) / 100.0;
        accuracyAngle = Math.round(accuracyAngle * 100.0) / 100.0;
    }

    public int[] getBarrelTip(int centerX, int centerY, double barrelAngle) {
        int tipX = (int) (centerX + barrelLength * Math.cos(barrelAngle));
        int tipY = (int) (centerY + barrelLength * Math.sin(barrelAngle));
        return new int[]{tipX, tipY};
    }

    public List<Projectile> shoot(int centerX, int centerY, double barrelAngle) {
        if (currentAmmo <= 0) {
            return null; // out of ammo
        }

        currentAmmo--;

        List<Projectile> projectiles = new ArrayList<>();

        // Get barrel tip position
        int[] tip = getBarrelTip(centerX, centerY, barrelAngle);

        // Apply accuracy angle spread
        double spreadRadians = Math.toRadians(accuracyAngle);
        double randomSpread = (Math.random() - 0.5) * 2.0 * spreadRadians; // -accuracyAngle to +accuracyAngle
        double finalAngle = barrelAngle + randomSpread;

        // Create projectile at barrel tip with final angle and weapon damage
        Color projectileColor = getProjectileColor();
        Projectile projectile = new Projectile(tip[0], tip[1], projectileColor, 10.0, finalAngle, damage);
        projectiles.add(projectile);

        return projectiles;
    }

    protected Color getProjectileColor() {
        switch (projectileColor.toLowerCase()) {
            case "red":
                return Color.RED;
            case "blue":
                return Color.BLUE;
            case "darkgreen":
                return new Color(0, 100, 0);
            case "brown":
                return new Color(139, 69, 19);
            case "white":
                return Color.WHITE;
            case "black":
            default:
                return Color.DARK_GRAY;
        }
    }

    public void reload() {
        if (!isReloading && currentAmmo < magazineSize) {
            isReloading = true;
            reloadStartTime = System.currentTimeMillis();
        }
    }

    public void updateReload() {
        if (isReloading) {
            long currentTime = System.currentTimeMillis();
            long reloadMs = (long) (reloadTime * 1000);
            if (currentTime - reloadStartTime >= reloadMs) {
                currentAmmo = magazineSize;
                isReloading = false;
            }
        }
    }

    // Getter methods for visual properties
    public String getBarrelColor() {
        return barrelColor;
    }

    public int getBarrelLength() {
        return barrelLength;
    }

    public int getBarrelHeight() {
        return barrelHeight;
    }

    public String getName() {
        return name;
    }

    public double getFireRate() {
        return fireRate;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public int getMagazineSize() {
        return magazineSize;
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public boolean isReloading() {
        return isReloading;
    }

    public int getDamage() {
        return damage;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getReloadTime() {
        return reloadTime;
    }

    public double getAccuracyAngle() {
        return accuracyAngle;
    }

    @Override
    public Ranged clone() {
        Ranged cloned = (Ranged) super.clone();
        cloned.fireRate = this.fireRate;
        cloned.damage = this.damage;
        cloned.accuracy = this.accuracy;
        cloned.accuracyAngle = this.accuracyAngle;
        cloned.magazineSize = this.magazineSize;
        cloned.reloadTime = this.reloadTime;
        cloned.currentAmmo = this.currentAmmo;
        cloned.automatic = this.automatic;
        cloned.barrelColor = this.barrelColor;
        cloned.projectileColor = this.projectileColor;
        cloned.barrelLength = this.barrelLength;
        cloned.barrelHeight = this.barrelHeight;
        cloned.isReloading = false;
        cloned.reloadStartTime = 0;
        return cloned;
    }
}
