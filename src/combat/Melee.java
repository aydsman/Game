package combat;

public class Melee extends Item {

    protected double attackSpeed; // attacks per second
    protected int damage; // damage per attack
    protected double range; // attack range in pixels
    protected double knockback; // knockback force

    public Melee() {
        super();
        // default melee stats (can be overridden by subclasses)
        attackSpeed = 1.0; // 1 attack per second
        damage = 15;
        range = 50.0; // 50 pixel range
        knockback = 5.0;
    }

    public Melee(int tier) {
        super(tier);
        // default melee stats (can be overridden by subclasses)
        attackSpeed = 1.0; // 1 attack per second
        damage = 15;
        range = 50.0; // 50 pixel range
        knockback = 5.0;

        applyTierMultipliers();
    }

    protected void applyTierMultipliers() {
        double damageMultiplier = 1.0;
        double attackSpeedMultiplier = 1.0;
        double rangeMultiplier = 1.0;
        double knockbackMultiplier = 1.0;

        switch (tier) {
            case 1: // Tier I - Common (no boost)
                damageMultiplier = 1.0;
                attackSpeedMultiplier = 1.0;
                rangeMultiplier = 1.0;
                knockbackMultiplier = 1.0;
                break;
            case 2: // Tier II - Uncommon (minimal boost)
                damageMultiplier = 1.1;
                attackSpeedMultiplier = 1.05;
                rangeMultiplier = 1.05;
                knockbackMultiplier = 1.05;
                break;
            case 3: // Tier III - Rare (minimal boost)
                damageMultiplier = 1.2;
                attackSpeedMultiplier = 1.1;
                rangeMultiplier = 1.1;
                knockbackMultiplier = 1.1;
                break;
            case 4: // Tier IV - Epic (better boost)
                damageMultiplier = 1.4;
                attackSpeedMultiplier = 1.2;
                rangeMultiplier = 1.15;
                knockbackMultiplier = 1.2;
                break;
            case 5: // Tier V - Legendary (best boost)
                damageMultiplier = 1.7;
                attackSpeedMultiplier = 1.3;
                rangeMultiplier = 1.2;
                knockbackMultiplier = 1.3;
                break;
        }

        damage *= damageMultiplier;
        attackSpeed *= attackSpeedMultiplier;
        range *= rangeMultiplier;
        knockback *= knockbackMultiplier;

        // Round to 2 decimal places
        attackSpeed = Math.round(attackSpeed * 100.0) / 100.0;
        range = Math.round(range * 100.0) / 100.0;
        knockback = Math.round(knockback * 100.0) / 100.0;
    }

    public void attack() {
        // attack logic will go here
    }

    public int getDamage() {
        return damage;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public double getRange() {
        return range;
    }

    public double getKnockback() {
        return knockback;
    }
}
