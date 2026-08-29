package combat.clothing.accessories;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Glasses extends ClothingItem {
    public Glasses() {
        super("Glasses", ClothingType.ACCESSORY, "assets/clothing/accessories/glasses", Color.WHITE, false);
        addStyle("Default");
    }
}
