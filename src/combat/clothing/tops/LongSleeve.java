package combat.clothing.tops;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class LongSleeve extends ClothingItem {
    public LongSleeve() {
        super("LongSleeve", ClothingType.TOP, "assets/player/shared/tops/longsleeve.png", Color.WHITE, false);
        this.tier = 1;
        addStyle("White");
        addStyle("Black");
    }
}
