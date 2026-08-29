package combat.clothing.accessories;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class AllSeeingEye extends ClothingItem {
    public AllSeeingEye() {
        super("AllSeeingEye", ClothingType.ACCESSORY, "assets/clothing/accessories/allseeingeye", Color.WHITE, false);
        addStyle("Default");
    }
}
