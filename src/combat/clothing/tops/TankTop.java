package combat.clothing.tops;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class TankTop extends ClothingItem {
    public TankTop() {
        super("TankTop", ClothingType.TOP, "assets/player/shared/tops/tanktop.png", Color.WHITE, false);
        this.tier = 1;
        addStyle("Black");
        addStyle("White");
        addStyle("Green");
        addUnlockableStyle("Red");
    }
}
