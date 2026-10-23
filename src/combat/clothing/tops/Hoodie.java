package combat.clothing.tops;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Hoodie extends ClothingItem {
    public Hoodie() {
        super("Hoodie", ClothingType.TOP, "assets/player/shared/tops/hoodie.png", Color.GRAY, false);
        this.tier = 2;
        addStyle("Gray");
        addStyle("Black");
        addStyle("White");
        addStyle("Navy");
    }
}
