package progression;

/**
 * A permanent combat bonus unlocked by account level. Perks are additive with charms and the skill
 * tree; they never replace those systems.
 */
public final class Perk {

    private final String id;
    private final String displayName;
    private final String description;
    private final int unlockLevel;
    private final double critChanceBonus;
    private final double dodgeCooldownMultiplier;
    private final double lifestealFraction;

    public Perk(String id,
                String displayName,
                String description,
                int unlockLevel,
                double critChanceBonus,
                double dodgeCooldownMultiplier,
                double lifestealFraction) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Perk id is required");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Perk display name is required");
        }
        if (unlockLevel < 1) {
            throw new IllegalArgumentException("Perk unlock level must be >= 1, got " + unlockLevel);
        }
        if (!Double.isFinite(critChanceBonus) || critChanceBonus < 0) {
            throw new IllegalArgumentException("Crit chance bonus must be >= 0, got " + critChanceBonus);
        }
        if (!Double.isFinite(dodgeCooldownMultiplier) || dodgeCooldownMultiplier <= 0 || dodgeCooldownMultiplier > 1) {
            throw new IllegalArgumentException(
                    "Dodge cooldown multiplier must be in (0, 1], got " + dodgeCooldownMultiplier);
        }
        if (!Double.isFinite(lifestealFraction) || lifestealFraction < 0 || lifestealFraction > 1) {
            throw new IllegalArgumentException("Lifesteal must be in [0, 1], got " + lifestealFraction);
        }
        this.id = id;
        this.displayName = displayName;
        this.description = description == null ? "" : description;
        this.unlockLevel = unlockLevel;
        this.critChanceBonus = critChanceBonus;
        this.dodgeCooldownMultiplier = dodgeCooldownMultiplier;
        this.lifestealFraction = lifestealFraction;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getUnlockLevel() {
        return unlockLevel;
    }

    public double getCritChanceBonus() {
        return critChanceBonus;
    }

    public double getDodgeCooldownMultiplier() {
        return dodgeCooldownMultiplier;
    }

    public double getLifestealFraction() {
        return lifestealFraction;
    }
}
