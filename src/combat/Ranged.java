package combat;

import java.awt.Color;

public class Ranged extends Item {

    protected double fireRate; // seconds between shots
    protected int damage; // damage per shot
    protected double accuracy; // 0.0 to 1.0 (100%)
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

    private void applyTierMultipliers() {
        double damageMultiplier = 1.0;
        double fireRateMultiplier = 1.0;
        double accuracyMultiplier = 1.0;
        int magazineBonus = 0;

        switch (tier) {
            case 1: // Tier I - Common (no boost)
                damageMultiplier = 1.0;
                fireRateMultiplier = 1.0;
                accuracyMultiplier = 1.0;
                magazineBonus = 0;
                break;
            case 2: // Tier II - Uncommon (minimal boost)
                damageMultiplier = 1.1;
                fireRateMultiplier = 1.05;
                accuracyMultiplier = 1.05;
                magazineBonus = 2;
                break;
            case 3: // Tier III - Rare (minimal boost)
                damageMultiplier = 1.2;
                fireRateMultiplier = 1.1;
                accuracyMultiplier = 1.1;
                magazineBonus = 4;
                break;
            case 4: // Tier IV - Epic (better boost)
                damageMultiplier = 1.4;
                fireRateMultiplier = 1.2;
                accuracyMultiplier = 1.15;
                magazineBonus = 6;
                break;
            case 5: // Tier V - Legendary (best boost)
                damageMultiplier = 1.7;
                fireRateMultiplier = 1.3;
                accuracyMultiplier = 1.2;
                magazineBonus = 8;
                break;
        }

        damage *= damageMultiplier;
        fireRate /= fireRateMultiplier; // Lower fireRate = faster shooting
        accuracy = Math.min(1.0, accuracy * accuracyMultiplier);
        magazineSize += magazineBonus;
        currentAmmo = magazineSize;
    }

    public int[] getBarrelTip(int centerX, int centerY, double barrelAngle) {
        int tipX = (int) (centerX + barrelLength * Math.cos(barrelAngle));
        int tipY = (int) (centerY + barrelLength * Math.sin(barrelAngle));
        return new int[]{tipX, tipY};
    }

    public Projectile shoot(int centerX, int centerY, double barrelAngle) {
        if (currentAmmo <= 0) {
            return null; // out of ammo
        }
        
        currentAmmo--;
        
        // Get barrel tip position
        int[] tip = getBarrelTip(centerX, centerY, barrelAngle);
        
        // Create projectile at barrel tip with barrel angle and weapon damage
        Color projectileColor = getProjectileColor();
        Projectile projectile = new Projectile(tip[0], tip[1], projectileColor, 5.0, barrelAngle, damage);
        
        return projectile;
    }

    private Color getProjectileColor() {
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
}
