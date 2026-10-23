package combat.clothing.bottoms;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Jorts extends ClothingItem {
    public Jorts() {
        super("Jorts", ClothingType.BOTTOM, "assets/player/shared/bottoms/jorts.png", new Color(70, 130, 180), false);
        this.tier = 2;
        addStyle("Light Wash");
        addStyle("Dark");
    }
}
