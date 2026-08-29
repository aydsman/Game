package combat.clothing.tops;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class LongSleeve extends ClothingItem {
    public LongSleeve() {
        super("LongSleeve", ClothingType.TOP, "assets/clothing/tops/longsleeve", Color.WHITE, false);
        if (false) addDefaultStyle("White");
        addStyle("White");
        addStyle("Black");
    }
}
