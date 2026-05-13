package player;

import combat.clothing.ClothingItem;
import combat.clothing.ClothingType;
import combat.clothing.tops.*;
import combat.clothing.bottoms.*;
import combat.clothing.accessories.*;
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
        List<ClothingItem> all = getAllClothingItems();
        List<ClothingItem> result = new ArrayList<>();
        for (ClothingItem item : all) {
            if (item.getType() == type && isUnlocked(item.getName().toLowerCase())) {
                result.add(item);
            }
        }
        return result;
    }

    private List<ClothingItem> getAllClothingItems() {
        List<ClothingItem> all = new ArrayList<>();

        // Tops
        all.add(new TShirt());
        all.add(new BoxyTee());
        all.add(new Hoodie());
        all.add(new LongSleeve());
        all.add(new TankTop());
        all.add(new SuitTop());

        // Bottoms
        all.add(new BasicPants());
        all.add(new Shorts());
        all.add(new Jorts());
        all.add(new BaggyJeans());
        all.add(new Sweatpants());
        all.add(new SwimShorts());
        all.add(new SuitBottom());

        // Accessories
        all.add(new Crown());
        all.add(new Cape());
        all.add(new Fedora());
        all.add(new AllSeeingEye());
        all.add(new Headband());
        all.add(new NinjaHeadband());
        all.add(new Glasses());
        all.add(new Shades());
        all.add(new Aviators());

        return all;
    }

    public Set<String> getUnlockedClothingIds() {
        return new HashSet<>(unlockedClothingIds);
    }

    public void setUnlockedClothingIds(Set<String> unlockedIds) {
        this.unlockedClothingIds.clear();
        if (unlockedIds != null) {
            for (String id : unlockedIds) {
                if (id != null && !id.isEmpty()) {
                    this.unlockedClothingIds.add(id.toLowerCase());
                }
            }
        }
    }

    public void unlockDefaults() {
        unlock("tshirt");
        unlock("shorts");
    }
}
