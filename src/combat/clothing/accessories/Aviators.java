package combat.clothing.accessories;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Aviators extends ClothingItem {
    public Aviators() {
        super("Aviators", ClothingType.ACCESSORY, "assets/player/shared/accessories/aviators.png", new Color(180, 140, 0), false);
        this.tier = 3;
        addDefaultStyle("Black");
        addStyle("Silver");
        addUnlockableStyle("Gold");
    }
}
