package combat.consumables;

/**
 * Timed incoming-damage reduction. {@link #effectMultiplier} is the fraction of damage removed
 * (0.25 = take 25% less), not a damage multiplier — that matches how the HUD labels the buff.
 */
public class DefensePotion extends Consumable {

    public DefensePotion() {
        this(1);
    }

    public DefensePotion(int tier) {
        super(tier);
        name = "Small Defense Potion";
        description = "Reduces incoming damage by 25% for 10 seconds";
        iconPath = "assets/items/consumables/potions/defense/small_defense_potion.png";
        durationMs = 10000;
        showStatus = true;
        effectType = ConsumableEffectType.DEFENSE_BOOST;
        effectMultiplier = 0.25;
        healthRestorePercent = 0.0;
    }
}
