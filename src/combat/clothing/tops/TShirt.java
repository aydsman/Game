package combat.clothing.tops;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class TShirt extends ClothingItem {
    public TShirt() {
        super("TShirt", ClothingType.TOP, "assets/player/shared/tops/tshirt.png", Color.WHITE, true);
        this.tier = 1;
        addDefaultStyle("Black");
        addStyle("White");
    }
}
