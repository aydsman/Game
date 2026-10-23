package combat.clothing.accessories;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Cape extends ClothingItem {
    public Cape() {
        super("Cape", ClothingType.ACCESSORY, "assets/player/shared/accessories/cape.png", new Color(150, 0, 0), false);
        this.tier = 4;
        addStyle("Default");
        addStyle("Dark");
    }
}
