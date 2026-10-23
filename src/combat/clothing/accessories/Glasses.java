package combat.clothing.accessories;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Glasses extends ClothingItem {
    public Glasses() {
        super("Glasses", ClothingType.ACCESSORY, "assets/player/shared/accessories/glasses.png", new Color(50, 50, 50), false);
        this.tier = 1;
        addDefaultStyle("Black");
        addStyle("Silver");
        addUnlockableStyle("Gold");
    }
}
