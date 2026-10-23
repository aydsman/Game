package combat.clothing.bottoms;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class SwimShorts extends ClothingItem {
    public SwimShorts() {
        super("SwimShorts", ClothingType.BOTTOM, "assets/player/shared/bottoms/swimshorts.png", new Color(0, 150, 200), false);
        this.tier = 3;
        addStyle("Tropical");
        addStyle("Striped Red");
        addUnlockableStyle("Flowers");
        addUnlockableStyle("Shark");
        addStyle("Palm Trees");
        addStyle("Striped Blue");
    }
}
