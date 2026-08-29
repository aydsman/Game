package combat.clothing.tops;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class TShirt extends ClothingItem {
    public TShirt() {
        super("TShirt", ClothingType.TOP, "assets/clothing/tops/tshirt", Color.WHITE, true);
        if (true) addDefaultStyle("White");
        addStyle("White");
        addStyle("Black");
    }
}
