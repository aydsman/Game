package combat.clothing.accessories;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class NinjaHeadband extends ClothingItem {
    public NinjaHeadband() {
        super("NinjaHeadband", ClothingType.ACCESSORY, "assets/player/shared/accessories/ninjaheadband.png", Color.GRAY, false);
        this.tier = 2;
        addUnlockableStyle("Leaf");
        addStyle("Rain");
        addUnlockableStyle("Sand");
        addStyle("Mist");
        addStyle("Sound");
    }
}
