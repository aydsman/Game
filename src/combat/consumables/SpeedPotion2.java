package combat.consumables;

public class SpeedPotion2 extends Consumable {

    public SpeedPotion2() {
        this(2);
    }

    public SpeedPotion2(int tier) {
        super(tier);
        name = "Medium Speed Potion";
        description = "Increases movement speed by 40% for 12 seconds";
        iconPath = "assets/items/consumables/potions/speed/medium_speed_potion.png";
        durationMs = 12000;
        showStatus = true;
        effectType = ConsumableEffectType.SPEED_BOOST;
        effectMultiplier = 1.4;
        healthRestorePercent = 0.0;
    }
}
