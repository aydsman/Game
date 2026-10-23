package combat.consumables;

public class SpeedPotion3 extends Consumable {

    public SpeedPotion3() {
        this(3);
    }

    public SpeedPotion3(int tier) {
        super(tier);
        name = "Large Speed Potion";
        description = "Increases movement speed by 60% for 15 seconds";
        iconPath = "assets/items/consumables/potions/speed/large_speed_potion.png";
        durationMs = 15000;
        showStatus = true;
        effectType = ConsumableEffectType.SPEED_BOOST;
        effectMultiplier = 1.6;
        healthRestorePercent = 0.0;
    }
}
