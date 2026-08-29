package combat.clothing.tops;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Hoodie extends ClothingItem {
    public Hoodie() {
        super("Hoodie", ClothingType.TOP, "assets/clothing/tops/hoodie", Color.WHITE, false);
        if (false) addDefaultStyle("White");
        addStyle("White");
        addStyle("Black");
    }
}
