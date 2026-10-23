package combat.clothing.tops;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class SuitTop extends ClothingItem {
    public SuitTop() {
        super("SuitTop", ClothingType.TOP, "assets/player/shared/tops/suittop.png", new Color(30, 30, 30), false);
        this.tier = 3;
        addStyle("Black");
        addStyle("White");
        addUnlockableStyle("Gold");
    }
}
