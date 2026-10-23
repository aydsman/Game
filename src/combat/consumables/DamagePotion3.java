package combat.consumables;

public class DamagePotion3 extends Consumable {

    public DamagePotion3() {
        this(4);
    }

    public DamagePotion3(int tier) {
        super(tier);
        name = "Large Damage Potion";
        description = "Doubles your damage for 20 seconds";
        iconPath = "assets/items/consumables/potions/damage/large_damage_potion.png";
        durationMs = 20000; // 20 seconds
        showStatus = true;
        effectType = ConsumableEffectType.DAMAGE_BOOST;
        effectMultiplier = 2.0;
        healthRestorePercent = 0.0; // No healing
    }
}
