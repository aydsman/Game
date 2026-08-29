package combat.clothing.bottoms;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class SwimShorts extends ClothingItem {
    public SwimShorts() {
        super("SwimShorts", ClothingType.BOTTOM, "assets/clothing/bottoms/swimshorts", Color.WHITE, false);
        if (false) addDefaultStyle("Default");
        addStyle("Default");
        addStyle("Black");
    }
}
