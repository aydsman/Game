package combat.clothing.accessories;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Aviators extends ClothingItem {
    public Aviators() {
        super("Aviators", ClothingType.ACCESSORY, "assets/clothing/accessories/aviators", Color.WHITE, false);
        addStyle("Default");
    }
}
