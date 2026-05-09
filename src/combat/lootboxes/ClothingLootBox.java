package combat.lootboxes;

import combat.Item;
import combat.clothing.ClothingItem;
import save.SaveManager;
import save.SaveData;
import java.util.*;

/**
 * Base class for clothing-specific loot boxes.
 * Handles the selection of clothing styles (colors) from available unlocked options.
 * When a clothing item is selected:
 * - If all styles are unlocked, the clothing is skipped (reroll)
 * - If styles remain, a random unlocked style is selected
 * - The newly unlocked style is added to SaveData
 */
public abstract class ClothingLootBox extends LootBox {

    public ClothingLootBox(String name) {
        super(name);
    }

    /**
     * Override open to handle clothing-specific style selection logic
     */
    @Override
    public Item open() {
        SaveData saveData = SaveManager.load();
        int maxAttempts = 50;
        int attempts = 0;

        while (attempts < maxAttempts) {
            attempts++;

            // Stage 1: Roll for tier
            Integer selectedTier = rollForTier();
            if (selectedTier == null) {
                System.err.println("LootBox '" + name + "' has no tier probabilities set!");
                return null;
            }

            // Stage 2: Roll for item type
            String selectedType = rollForItemType();
            if (selectedType == null) {
                System.err.println("LootBox '" + name + "' has no item type probabilities set!");
                return null;
            }

            // Stage 3: Get all items with available styles for this tier + type
            List<String> itemsWithStyles = getItemsWithAvailableStyles(selectedTier, selectedType, saveData);

            // If no items have available styles, show message and return null
            if (itemsWithStyles.isEmpty()) {
                System.out.println("All styles in this lootbox are already unlocked!");
                return null;
            }

            // Pick a random item from those with available styles
            String itemName = itemsWithStyles.get(new Random().nextInt(itemsWithStyles.size()));

            // Create the item to access its style options
            Item baseItem = createItem(itemName, selectedTier);
            if (baseItem instanceof ClothingItem) {
                ClothingItem clothing = (ClothingItem) baseItem;
                String clothingName = clothing.getName();
                List<String> availableStyles = getAvailableStyles(clothingName, clothing.getStyleOptions(), saveData);

                // Pick a random available style
                String selectedStyle = availableStyles.get(new Random().nextInt(availableStyles.size()));
                clothing.setSelectedStyle(selectedStyle);

                // Unlock this style in SaveData
                unlockClothingStyle(clothingName, selectedStyle, saveData);

                return clothing;
            } else {
                // Not a clothing item, return it as-is
                return baseItem;
            }
        }

        // Fallback: couldn't find any valid item (all unlocked)
        System.out.println("All styles in this lootbox are already unlocked!");
        return null;
    }

    /**
     * Get list of items in a tier that have at least one available style
     */
    private List<String> getItemsWithAvailableStyles(int tier, String itemType, SaveData saveData) {
        List<String> result = new ArrayList<>();
        Map<String, List<String>> tierMap = itemCatalog.get(tier);
        if (tierMap == null) return result;

        List<String> items = tierMap.get(itemType);
        if (items == null) return result;

        for (String itemName : items) {
            Item baseItem = createItem(itemName, tier);
            if (baseItem instanceof ClothingItem clothing) {
                List<String> availableStyles = getAvailableStyles(clothing.getName(), clothing.getStyleOptions(), saveData);
                if (!availableStyles.isEmpty()) {
                    result.add(itemName);
                }
            } else {
                // Non-clothing items are always available
                result.add(itemName);
            }
        }
        return result;
    }

    /**
     * Override this in subclasses to filter which styles each item can drop from this lootbox.
     * Return allStyles to allow all styles (default behavior).
     */
    protected abstract List<String> getAllowedStylesForItem(String itemName, List<String> allStyles);

    /**
     * Get available styles for a clothing item (styles not yet unlocked and allowed by this lootbox)
     */
    private List<String> getAvailableStyles(String clothingName, List<String> allStyles, SaveData saveData) {
        // First, filter by what this lootbox allows
        List<String> allowedStyles = getAllowedStylesForItem(clothingName, allStyles);

        // Then, filter out already unlocked styles
        Set<String> unlockedStyles = saveData.getUnlockedClothingStyles().getOrDefault(clothingName, new HashSet<>());
        List<String> availableStyles = new ArrayList<>();

        for (String style : allowedStyles) {
            if (!unlockedStyles.contains(style)) {
                availableStyles.add(style);
            }
        }

        return availableStyles;
    }

    /**
     * Unlock a clothing style in SaveData
     */
    private void unlockClothingStyle(String clothingName, String styleName, SaveData saveData) {
        Map<String, Set<String>> unlockedStyles = saveData.getUnlockedClothingStyles();
        unlockedStyles.computeIfAbsent(clothingName, k -> new HashSet<>()).add(styleName);
        
        // Also unlock the clothing item ID
        saveData.getUnlockedClothingIds().add(clothingName.toLowerCase());
        
        SaveManager.save(saveData);
    }

    protected Integer rollForTier() {
        if (tierProbabilities.isEmpty()) {
            return null;
        }

        double totalWeight = tierProbabilities.values().stream().mapToDouble(Double::doubleValue).sum();
        double roll = random.nextDouble() * totalWeight;

        double currentWeight = 0.0;
        for (Map.Entry<Integer, Double> entry : tierProbabilities.entrySet()) {
            currentWeight += entry.getValue();
            if (roll <= currentWeight) {
                return entry.getKey();
            }
        }

        return tierProbabilities.keySet().iterator().next();
    }

    protected String rollForItemType() {
        if (itemTypeProbabilities.isEmpty()) {
            return null;
        }

        double totalWeight = itemTypeProbabilities.values().stream().mapToDouble(Double::doubleValue).sum();
        double roll = random.nextDouble() * totalWeight;

        double currentWeight = 0.0;
        for (Map.Entry<String, Double> entry : itemTypeProbabilities.entrySet()) {
            currentWeight += entry.getValue();
            if (roll <= currentWeight) {
                return entry.getKey();
            }
        }

        return itemTypeProbabilities.keySet().iterator().next();
    }

    protected String pickRandomItem(int tier, String itemType) {
        Map<String, List<String>> tierMap = itemCatalog.get(tier);
        if (tierMap == null) {
            return null;
        }

        List<String> items = tierMap.get(itemType);
        if (items == null || items.isEmpty()) {
            return null;
        }

        return items.get(random.nextInt(items.size()));
    }
}
