package combat.clothing.bottoms;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class BasicPants extends ClothingItem {
    public BasicPants() {
        super("BasicPants", ClothingType.BOTTOM, "assets/clothing/bottoms/basicpants", Color.WHITE, true);
        if (true) addDefaultStyle("Default");
        addStyle("Default");
        addStyle("Black");
    }
}
