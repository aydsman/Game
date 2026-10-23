package combat.clothing.accessories;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Fedora extends ClothingItem {
    public Fedora() {
        super("Fedora", ClothingType.ACCESSORY, "assets/player/shared/accessories/fedora.png", new Color(80, 60, 40), false);
        this.tier = 3;
        addDefaultStyle("Black");
        addStyle("White");
        addUnlockableStyle("Gold");
    }
}
