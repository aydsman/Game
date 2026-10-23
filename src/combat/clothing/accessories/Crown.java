package combat.clothing.accessories;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Crown extends ClothingItem {
    public Crown() {
        super("Crown", ClothingType.ACCESSORY, "assets/player/shared/accessories/crown.png", new Color(255, 215, 0), false);
        this.tier = 4;
        addStyle("Gold");
        addUnlockableStyle("Dark");
    }
}
