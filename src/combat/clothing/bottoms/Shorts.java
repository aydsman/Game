package combat.clothing.bottoms;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Shorts extends ClothingItem {
    public Shorts() {
        super("Shorts", ClothingType.BOTTOM, "assets/clothing/bottoms/shorts", Color.WHITE, true);
        if (true) addDefaultStyle("Default");
        addStyle("Default");
        addStyle("Black");
    }
}
