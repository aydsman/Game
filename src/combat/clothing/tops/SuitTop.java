package combat.clothing.tops;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class SuitTop extends ClothingItem {
    public SuitTop() {
        super("SuitTop", ClothingType.TOP, "assets/clothing/tops/suittop", Color.WHITE, false);
        if (false) addDefaultStyle("White");
        addStyle("White");
        addStyle("Black");
    }
}
