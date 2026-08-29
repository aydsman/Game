package combat.clothing.accessories;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Crown extends ClothingItem {
    public Crown() {
        super("Crown", ClothingType.ACCESSORY, "assets/clothing/accessories/crown", Color.WHITE, false);
        addStyle("Default");
    }
}
