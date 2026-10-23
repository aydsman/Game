package combat.clothing.accessories;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class AllSeeingEye extends ClothingItem {
    public AllSeeingEye() {
        super("AllSeeingEye", ClothingType.ACCESSORY, "assets/player/shared/accessories/allseeingeye.png", new Color(180, 0, 255), false);
        this.tier = 5;
        addDefaultStyle("Default");
        addStyle("Gold");
        addUnlockableStyle("Dark");
    }
}
