package combat.clothing.accessories;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Shades extends ClothingItem {
    public Shades() {
        super("Shades", ClothingType.ACCESSORY, "assets/clothing/accessories/shades", Color.WHITE, false);
        addStyle("Default");
    }
}
