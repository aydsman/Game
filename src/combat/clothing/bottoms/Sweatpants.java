package combat.clothing.bottoms;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Sweatpants extends ClothingItem {
    public Sweatpants() {
        super("Sweatpants", ClothingType.BOTTOM, "assets/clothing/bottoms/sweatpants", Color.WHITE, false);
        if (false) addDefaultStyle("Default");
        addStyle("Default");
        addStyle("Black");
    }
}
