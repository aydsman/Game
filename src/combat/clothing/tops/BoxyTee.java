package combat.clothing.tops;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class BoxyTee extends ClothingItem {
    public BoxyTee() {
        super("BoxyTee", ClothingType.TOP, "assets/player/shared/tops/boxytee.png", Color.WHITE, false);
        this.tier = 2;
        addDefaultStyle("White");
        addStyle("Black");
        addStyle("Gray");
        addStyle("Navy");
    }
}
