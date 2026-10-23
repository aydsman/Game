package combat.clothing.bottoms;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class BasicPants extends ClothingItem {
    public BasicPants() {
        super("BasicPants", ClothingType.BOTTOM, "assets/player/shared/bottoms/basicpants.png", Color.GRAY, true);
        this.tier = 1;
        addDefaultStyle("Black");
        addStyle("Gray");
    }
}
