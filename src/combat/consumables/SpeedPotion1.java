package combat.consumables;

public class SpeedPotion1 extends Consumable {

    public SpeedPotion1() {
        this(1);
    }

    public SpeedPotion1(int tier) {
        super(tier);
        name = "Small Speed Potion";
        description = "Increases movement speed by 20% for 10 seconds";
        iconPath = "assets/items/consumables/potions/speed/small_speed_potion.png";
        durationMs = 10000;
        showStatus = true;
        effectType = ConsumableEffectType.SPEED_BOOST;
        effectMultiplier = 1.2;
        healthRestorePercent = 0.0;
    }
}
