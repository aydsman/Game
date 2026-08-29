package combat.clothing.accessories;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Headband extends ClothingItem {
    public Headband() {
        super("Headband", ClothingType.ACCESSORY, "assets/clothing/accessories/headband", Color.WHITE, false);
        addStyle("Default");
    }
}
