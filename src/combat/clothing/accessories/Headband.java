package combat.clothing.accessories;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Headband extends ClothingItem {
    public Headband() {
        super("Headband", ClothingType.ACCESSORY, "assets/player/shared/accessories/headband.png", Color.WHITE, false);
        this.tier = 1;
        addDefaultStyle("Black");
        addStyle("White");
        addStyle("Red");
        addStyle("Blue");
        addUnlockableStyle("Purple");
    }
}
