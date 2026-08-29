package combat.clothing.bottoms;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Jorts extends ClothingItem {
    public Jorts() {
        super("Jorts", ClothingType.BOTTOM, "assets/clothing/bottoms/jorts", Color.WHITE, false);
        if (false) addDefaultStyle("Default");
        addStyle("Default");
        addStyle("Black");
    }
}
