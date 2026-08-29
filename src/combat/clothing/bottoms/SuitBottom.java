package combat.clothing.bottoms;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class SuitBottom extends ClothingItem {
    public SuitBottom() {
        super("SuitBottom", ClothingType.BOTTOM, "assets/clothing/bottoms/suitbottom", Color.WHITE, false);
        if (false) addDefaultStyle("Default");
        addStyle("Default");
        addStyle("Black");
    }
}
