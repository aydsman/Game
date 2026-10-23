package combat.clothing.bottoms;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class SuitBottom extends ClothingItem {
    public SuitBottom() {
        super("SuitBottom", ClothingType.BOTTOM, "assets/player/shared/bottoms/suitbottom.png", new Color(30, 30, 30), false);
        this.tier = 3;
        addStyle("Black");
        addStyle("White");
        addUnlockableStyle("Gold");
    }
}
