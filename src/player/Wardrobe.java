package player;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import java.util.*;

public class Wardrobe {

    private Set<String> unlockedClothingIds;

    public Wardrobe() {
        this.unlockedClothingIds = new HashSet<>();
    }

    public Wardrobe(Set<String> unlockedIds) {
        this.unlockedClothingIds = unlockedIds != null ? new HashSet<>(unlockedIds) : new HashSet<>();
    }

    public void unlock(String clothingId) {
        unlockedClothingIds.add(clothingId.toLowerCase());
    }

    public boolean isUnlocked(String clothingId) {
        return unlockedClothingIds.contains(clothingId.toLowerCase());
    }

    public List<ClothingItem> getUnlockedByType(ClothingType type) {
        List<ClothingItem> items = new ArrayList<>();
        if (type == ClothingType.TOP && isUnlocked("tshirt")) {
            items.add(new ClothingItem("TShirt", ClothingType.TOP, "assets/player/shared/tops/tshirt.png", java.awt.Color.WHITE, true));
        }
        if (type == ClothingType.BOTTOM && isUnlocked("basicpants")) {
            items.add(new ClothingItem("BasicPants", ClothingType.BOTTOM, "assets/player/shared/bottoms/basicpants.png", java.awt.Color.BLUE, true));
        }
        return items;
    }

    public Set<String> getUnlockedClothingIds() {
        return new HashSet<>(unlockedClothingIds);
    }

    public void unlockDefaults() {
        unlock("tshirt");
        unlock("basicpants");
    }
}
