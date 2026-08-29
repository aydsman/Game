package combat.clothing.accessories;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class NinjaHeadband extends ClothingItem {
    public NinjaHeadband() {
        super("NinjaHeadband", ClothingType.ACCESSORY, "assets/clothing/accessories/ninjaheadband", Color.WHITE, false);
        addStyle("Default");
    }
}
