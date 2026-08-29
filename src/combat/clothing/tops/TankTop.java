package combat.clothing.tops;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class TankTop extends ClothingItem {
    public TankTop() {
        super("TankTop", ClothingType.TOP, "assets/clothing/tops/tanktop", Color.WHITE, false);
        if (false) addDefaultStyle("White");
        addStyle("White");
        addStyle("Black");
    }
}
