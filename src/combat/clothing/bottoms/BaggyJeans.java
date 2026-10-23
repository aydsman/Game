package combat.clothing.bottoms;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class BaggyJeans extends ClothingItem {
    public BaggyJeans() {
        super("BaggyJeans", ClothingType.BOTTOM, "assets/player/shared/bottoms/baggyjeans.png", new Color(30, 60, 120), false);
        this.tier = 3;
        addStyle("Light Wash");
        addStyle("Gray");
        addStyle("Dark Wash");
        addStyle("Dark");
        addStyle("Beige");
        addUnlockableStyle("Acid Wash");
    }
}
