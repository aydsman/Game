package combat.clothing.bottoms;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class BaggyJeans extends ClothingItem {
    public BaggyJeans() {
        super("BaggyJeans", ClothingType.BOTTOM, "assets/clothing/bottoms/baggyjeans", Color.WHITE, false);
        if (false) addDefaultStyle("Default");
        addStyle("Default");
        addStyle("Black");
    }
}
