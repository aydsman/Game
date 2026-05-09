package combat.clothing;

import combat.Item;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ClothingItem extends Item {

    private ClothingType type;
    private String assetPath;
    private Color defaultColor;
    private boolean unlockedByDefault;
    private List<String> styleOptions; // For lootbox rolls
    private List<String> unlockableStyles; // For non-lootbox unlocks (quests, events, etc.)
    private String selectedStyle;
    private String defaultStyle;

    public ClothingItem() {
        super();
        this.type = ClothingType.TOP;
        this.assetPath = "";
        this.defaultColor = Color.WHITE;
        this.unlockedByDefault = false;
        this.styleOptions = new ArrayList<>();
        this.unlockableStyles = new ArrayList<>();
        this.selectedStyle = null;
        this.defaultStyle = null;
    }

    public ClothingItem(String name, ClothingType type, String assetPath, Color defaultColor, boolean unlockedByDefault) {
        super();
        this.name = name;
        this.type = type;
        this.assetPath = assetPath;
        this.defaultColor = defaultColor;
        this.unlockedByDefault = unlockedByDefault;
        this.styleOptions = new ArrayList<>();
        this.unlockableStyles = new ArrayList<>();
        this.selectedStyle = null;
        this.defaultStyle = null;
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

    protected void addStyle(String style) {
        if (!styleOptions.contains(style)) {
            styleOptions.add(style);
        }
    }

    protected void addDefaultStyle(String style) {
        this.defaultStyle = style;
        // Default style is NOT added to styleOptions so it won't appear in lootboxes
    }

    protected void addUnlockableStyle(String style) {
        if (!unlockableStyles.contains(style)) {
            unlockableStyles.add(style);
        }
        // Not added to styleOptions - available for selection but not in lootboxes
    }

    public List<String> getStyleOptions() {
        // Return lootbox-only styles (excludes default and unlockable styles)
        return new ArrayList<>(styleOptions);
    }

    public List<String> getUnlockableStyles() {
        return new ArrayList<>(unlockableStyles);
    }

    public String getDefaultStyle() {
        return defaultStyle;
    }

    public void setSelectedStyle(String style) {
        if (styleOptions.contains(style) || style.equals(defaultStyle) || unlockableStyles.contains(style)) {
            this.selectedStyle = style;
        }
    }

    public String getSelectedStyle() {
        if (selectedStyle != null) {
            return selectedStyle;
        }
        // Return default style if nothing selected
        return defaultStyle != null ? defaultStyle : (styleOptions.isEmpty() ? "White" : styleOptions.get(0));
    }
}
