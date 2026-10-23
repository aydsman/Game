package combat.clothing.bottoms;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Shorts extends ClothingItem {
    public Shorts() {
        super("Shorts", ClothingType.BOTTOM, "assets/player/shared/bottoms/shorts.png", new Color(100, 149, 237), false);
        this.tier = 1;
        addDefaultStyle("Black");
        addStyle("Gray");
    }
}
