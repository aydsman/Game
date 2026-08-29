package combat.clothing.accessories;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.awt.Color;

public class Fedora extends ClothingItem {
    public Fedora() {
        super("Fedora", ClothingType.ACCESSORY, "assets/clothing/accessories/fedora", Color.WHITE, false);
        addStyle("Default");
    }
}
