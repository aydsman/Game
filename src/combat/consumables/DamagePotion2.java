package combat.consumables;

public class DamagePotion2 extends Consumable {

    public DamagePotion2() {
        this(3);
    }

    public DamagePotion2(int tier) {
        super(tier);
        name = "Medium Damage Potion";
        description = "Doubles your damage for 15 seconds";
        iconPath = "assets/items/consumables/potions/damage/medium_damage_potion.png";
        durationMs = 15000; // 15 seconds
        showStatus = true;
        effectType = ConsumableEffectType.DAMAGE_BOOST;
        effectMultiplier = 2.0;
        healthRestorePercent = 0.0; // No healing
    }
}
