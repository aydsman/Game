package combat.clothing.bottoms;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Sweatpants extends ClothingItem {
    public Sweatpants() {
        super("Sweatpants", ClothingType.BOTTOM, "assets/player/shared/bottoms/sweatpants.png", Color.DARK_GRAY, false);
        this.tier = 2;
        addStyle("Gray");
        addStyle("Black");
        addStyle("Navy");
        addUnlockableStyle("Red");
    }
}
