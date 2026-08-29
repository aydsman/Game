package combat.clothing.tops;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class BoxyTee extends ClothingItem {
    public BoxyTee() {
        super("BoxyTee", ClothingType.TOP, "assets/clothing/tops/boxytee", Color.WHITE, false);
        if (false) addDefaultStyle("White");
        addStyle("White");
        addStyle("Black");
    }
}
