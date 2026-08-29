package combat.clothing.accessories;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Cape extends ClothingItem {
    public Cape() {
        super("Cape", ClothingType.ACCESSORY, "assets/clothing/accessories/cape", Color.WHITE, false);
        addStyle("Default");
    }
}
