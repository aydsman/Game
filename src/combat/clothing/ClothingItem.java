package combat.clothing;

import combat.Item;
import java.awt.Color;

public class ClothingItem extends Item {

    private ClothingType type;
    private String assetPath;
    private Color defaultColor;
    private boolean unlockedByDefault;

    public ClothingItem() {
        super();
        this.type = ClothingType.TOP;
        this.assetPath = "";
        this.defaultColor = Color.WHITE;
        this.unlockedByDefault = false;
    }

    public ClothingItem(String name, ClothingType type, String assetPath, Color defaultColor, boolean unlockedByDefault) {
        super();
        this.name = name;
        this.type = type;
        this.assetPath = assetPath;
        this.defaultColor = defaultColor;
        this.unlockedByDefault = unlockedByDefault;
    }

    public ClothingType getType() {
        return type;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public boolean isUnlockedByDefault() {
        return unlockedByDefault;
    }
}
