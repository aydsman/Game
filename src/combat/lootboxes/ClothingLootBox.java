package combat.lootboxes;

import combat.Item;
import combat.clothing.ClothingItem;
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

            // Stage 3: Get all items for this tier + type
            List<String> itemsWithStyles = getItemsForTierType(selectedTier, selectedType);
            if (itemsWithStyles.isEmpty()) return null;

            // Pick a random item from those with available styles
            String itemName = itemsWithStyles.get(new Random().nextInt(itemsWithStyles.size()));

            // Create the item to access its style options
            Item baseItem = createItem(itemName, selectedTier);
            if (baseItem instanceof ClothingItem) {
                ClothingItem clothing = (ClothingItem) baseItem;
                String clothingName = clothing.getName();
                List<String> allowedStyles = getAllowedStylesForItem(clothingName, clothing.getStyleOptions());
                if (allowedStyles.isEmpty()) {
                    continue;
                }
                String selectedStyle = allowedStyles.get(new Random().nextInt(allowedStyles.size()));
                clothing.setSelectedStyle(selectedStyle);
                return clothing;
            } else {
                // Not a clothing item, return it as-is
                return baseItem;
            }
        }

        // Fallback: couldn't find any valid item
        return null;
    }

    /**
     * Get list of items in a tier that have at least one available style
     */
    private List<String> getItemsForTierType(int tier, String itemType) {
        List<String> result = new ArrayList<>();
        Map<String, List<String>> tierMap = itemCatalog.get(tier);
        if (tierMap == null) return result;

        List<String> items = tierMap.get(itemType);
        if (items == null) return result;
        result.addAll(items);
        return result;
    }

    /**
     * Override this in subclasses to filter which styles each item can drop from this lootbox.
     * Return allStyles to allow all styles (default behavior).
     */
    protected abstract List<String> getAllowedStylesForItem(String itemName, List<String> allStyles);

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
