package combat.consumables;

public class DamagePotion extends Consumable {

    public DamagePotion() {
        this(2);
    }

    public DamagePotion(int tier) {
        super(tier);
        name = "Small Damage Potion";
        description = "Doubles your damage for 10 seconds";
        iconPath = "assets/items/consumables/potions/damage/small_damage_potion.png";
        durationMs = 10000; // 10 seconds
        showStatus = true;
        effectType = ConsumableEffectType.DAMAGE_BOOST;
        effectMultiplier = 2.0;
        healthRestorePercent = 0.0; // No healing
    }
}
