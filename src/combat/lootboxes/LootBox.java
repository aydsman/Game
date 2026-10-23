package combat.lootboxes;

import combat.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Base class for loot boxes that contain randomized item rewards.
 * Uses a hierarchical probability system: first rolls for tier, then item type, then specific item.
 *
 * Usage:
 *   LootBox box = new CommonLootBox();
 *   box.setTierProbability(1, 0.5);    // 50% chance for tier 1
 *   box.setTierProbability(2, 0.3);    // 30% chance for tier 2
 *   box.setItemTypeProbability("Weapon", 0.4);      // 40% chance for weapon
 *   box.setItemTypeProbability("Charm", 0.2);       // 20% chance for charm
 *   box.addItem(1, "Weapon", "Glock");              // Add Glock to tier 1 weapons
 *   box.addItem(1, "Charm", "Charm1");              // Add Charm1 to tier 1 charms
 *   Item reward = box.open();                       // Rolls tier → type → item
 */
public abstract class LootBox {

    protected String name;
    protected Random random;

    // Tier probabilities (key: tier 1-5, value: probability weight)
    protected Map<Integer, Double> tierProbabilities;

    // Item type probabilities (key: type name, value: probability weight)
    protected Map<String, Double> itemTypeProbabilities;

    // Item catalog organized: [tier][itemType] -> List of item names
    protected Map<Integer, Map<String, List<String>>> itemCatalog;
    // Item weights organized: [tier][itemType][itemName] -> probability weight
    protected Map<Integer, Map<String, Map<String, Double>>> itemProbabilities;

    public LootBox(String name) {
        this.name = name;
        this.random = new Random();
        this.tierProbabilities = new HashMap<>();
        this.itemTypeProbabilities = new HashMap<>();
        this.itemCatalog = new HashMap<>();
        this.itemProbabilities = new HashMap<>();

        // Initialize catalog for tiers 1-5
        for (int tier = 1; tier <= 5; tier++) {
            itemCatalog.put(tier, new HashMap<>());
            itemProbabilities.put(tier, new HashMap<>());
        }
    }

    /**
     * Set the probability weight for a specific tier.
     *
     * @param tier Tier number (1-5)
     * @param probability Probability weight (higher = more likely)
     */
    public void setTierProbability(int tier, double probability) {
        if (tier < 1 || tier > 5) {
            throw new IllegalArgumentException("Tier must be between 1 and 5");
        }
        tierProbabilities.put(tier, probability);
    }

    /**
     * Set the probability weight for a specific item type.
     *
     * @param itemType Item type name (e.g., "Weapon", "Charm", "Power", "Summon", "Consumable")
     * @param probability Probability weight (higher = more likely)
     */
    public void setItemTypeProbability(String itemType, double probability) {
        itemTypeProbabilities.put(itemType, probability);
    }

    /**
     * Add an item to the catalog for a specific tier and item type.
     *
     * @param tier Tier number (1-5)
     * @param itemType Item type (e.g., "Weapon", "Charm", "Power", etc.)
     * @param itemName Name of the item class (e.g., "Glock", "Charm1", "Fire")
     */
    public void addItem(int tier, String itemType, String itemName) {
        addItem(tier, itemType, itemName, 1.0);
    }

    public void addItem(int tier, String itemType, String itemName, double itemWeight) {
        if (tier < 1 || tier > 5) {
            throw new IllegalArgumentException("Tier must be between 1 and 5");
        }

        Map<String, List<String>> tierMap = itemCatalog.get(tier);
        tierMap.computeIfAbsent(itemType, k -> new ArrayList<>()).add(itemName);
        setItemProbability(tier, itemType, itemName, itemWeight);
    }

    /**
     * Add a non-weapon item to the catalog (tier is automatically determined from the item).
     * Use this for charms, powers, consumables, summons, etc.
     *
     * @param itemType Item type (e.g., "Charm", "Power", "Consumable", "Summon")
     * @param itemName Name of the item class (e.g., "Charm1", "Fire")
     */
    public void addItem(String itemType, String itemName) {
        addItem(itemType, itemName, 1.0);
    }

    public void addItem(String itemType, String itemName, double itemWeight) {
        int correctTier = getItemTier(itemName);
        addItem(correctTier, itemType, itemName, itemWeight);
    }

    public void setItemProbability(int tier, String itemType, String itemName, double probabilityWeight) {
        if (tier < 1 || tier > 5) {
            throw new IllegalArgumentException("Tier must be between 1 and 5");
        }
        if (probabilityWeight <= 0) {
            throw new IllegalArgumentException("Item probability weight must be > 0");
        }
        itemProbabilities
                .computeIfAbsent(tier, k -> new HashMap<>())
                .computeIfAbsent(itemType, k -> new HashMap<>())
                .put(itemName, probabilityWeight);
    }

    /**
     * Determine the correct tier for an item by trying to instantiate it.
     *
     * @param itemName The item class name
     * @return The item's tier, or 1 if unable to determine
     */
    private int getItemTier(String itemName) {
        try {
            Class<?> itemClass = findItemClass(itemName);
            if (itemClass != null) {
                // Try to create instance and check tier
                try {
                    Item item = (Item) itemClass.getConstructor().newInstance();
                    return item.getTier();
                } catch (NoSuchMethodException e) {
                    // Try with tier parameter (default to 1)
                    try {
                        Item item = (Item) itemClass.getConstructor(int.class).newInstance(1);
                        return item.getTier();
                    } catch (Exception ex) {
                        // Fallback to 1
                        return 1;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Could not determine tier for item: " + itemName + ", defaulting to tier 1");
        }
        return 1;
    }

    /**
     * Opens the loot box and returns a random item based on two-stage probability rolling.
     * Stage 1: Roll for tier based on tier probabilities
     * Stage 2: Roll for item type based on type probabilities
     * Stage 3: Pick a random item from the selected tier + type
     *
     * For weapons: Uses the rolled tier to create tier variants (e.g., Glock T1, T2, T3)
     * For non-weapons: Uses tier 1 as default since items like Charm1, Charm2 have fixed tiers
     *
     * If no items exist for the rolled tier+type, rerolls until a valid item is found.
     *
     * @return The rolled item, or null if unable to find any valid item after multiple attempts
     */
    public Item open() {
        int maxAttempts = 50; // Prevent infinite loops
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

            // Stage 3: Pick a random item from the selected tier + type
            String itemName = pickRandomItem(selectedTier, selectedType);
            if (itemName != null) {
                // Found a valid item! Use the rolled tier for all item types
                return createItem(itemName, selectedTier);
            }
            // If itemName is null, loop and reroll
        }

        // Fallback: couldn't find any valid item
        System.err.println("LootBox '" + name + "' failed to find valid item after " + maxAttempts + " attempts!");
        return null;
    }

    /**
     * Roll for a tier based on tier probabilities.
     *
     * @return The selected tier (1-5), or null if no probabilities set
     */
    private Integer rollForTier() {
        if (tierProbabilities.isEmpty()) {
            return null;
        }

        // Skill tree loot quality compounds per tier, so higher tiers gain the most weight.
        double qualityBias = 1.0 + skilltree.SkillEffects.lootQualityBonusFraction();

        double totalWeight = 0.0;
        for (Map.Entry<Integer, Double> entry : tierProbabilities.entrySet()) {
            totalWeight += biasedTierWeight(entry, qualityBias);
        }
        double roll = random.nextDouble() * totalWeight;

        double currentWeight = 0.0;
        for (Map.Entry<Integer, Double> entry : tierProbabilities.entrySet()) {
            currentWeight += biasedTierWeight(entry, qualityBias);
            if (roll <= currentWeight) {
                return entry.getKey();
            }
        }

        // Fallback
        return tierProbabilities.keySet().iterator().next();
    }

    private double biasedTierWeight(Map.Entry<Integer, Double> tierEntry, double qualityBias) {
        return tierEntry.getValue() * Math.pow(qualityBias, tierEntry.getKey() - 1);
    }

    /**
     * Roll for an item type based on type probabilities.
     *
     * @return The selected item type, or null if no probabilities set
     */
    private String rollForItemType() {
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

        // Fallback
        return itemTypeProbabilities.keySet().iterator().next();
    }

    /**
     * Pick a random item from the selected tier and type.
     *
     * @param tier The selected tier
     * @param itemType The selected item type
     * @return The item name, or null if no items available
     */
    private String pickRandomItem(int tier, String itemType) {
        Map<String, List<String>> tierMap = itemCatalog.get(tier);
        if (tierMap == null) {
            return null;
        }

        List<String> items = tierMap.get(itemType);
        if (items == null || items.isEmpty()) {
            return null;
        }
        Map<String, Double> weights = itemProbabilities
                .getOrDefault(tier, new HashMap<>())
                .getOrDefault(itemType, new HashMap<>());
        if (weights.isEmpty()) {
            return items.get(random.nextInt(items.size()));
        }

        double total = 0.0;
        for (String item : items) {
            total += weights.getOrDefault(item, 1.0);
        }
        double roll = random.nextDouble() * total;
        double current = 0.0;
        for (String item : items) {
            current += weights.getOrDefault(item, 1.0);
            if (roll <= current) {
                return item;
            }
        }
        return items.get(items.size() - 1);
    }

    /**
     * Create an item instance from item name and tier.
     *
     * @param itemName The name of the item class
     * @param tier The tier (1-5)
     * @return The created item instance
     */
    protected Item createItem(String itemName, int tier) {
        try {
            Class<?> itemClass = findItemClass(itemName);
            if (itemClass != null) {
                // Try constructor with tier parameter
                try {
                    return (Item) itemClass.getConstructor(int.class).newInstance(tier);
                } catch (NoSuchMethodException e) {
                    // Try no-arg constructor
                    return (Item) itemClass.getConstructor().newInstance();
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to create item: " + itemName + " (tier " + tier + ")");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Find the item class by name.
     * Searches in common packages: ranged, melee, charms, powers, summons, consumables.
     *
     * @param itemName Simple class name (e.g., "Glock", "Charm1")
     * @return The Class object, or null if not found
     */
    protected Class<?> findItemClass(String itemName) {
        String[] packages = {
                "combat.ranged.pistols.",
                "combat.ranged.rifles.",
                "combat.ranged.shotguns.",
                "combat.ranged.smgs.",
                "combat.ranged.snipers.",
                "combat.melee.swords.",
                "combat.melee.hammers.",
                "combat.melee.daggers.",
                "combat.melee.maces.",
                "combat.melee.scythes.",
                "combat.charms.",
                "combat.powers.",
                "combat.powers.fruits.",
                "combat.summons.",
                "combat.consumables.",
                "combat.clothing.tops.",
                "combat.clothing.bottoms.",
                "combat.clothing.accessories."
        };

        for (String pkg : packages) {
            try {
                return Class.forName(pkg + itemName);
            } catch (ClassNotFoundException e) {
                // Continue searching
            }
        }
        return null;
    }

    /**
     * Get the name of this loot box.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the tier probabilities (for debugging/display).
     */
    public Map<Integer, Double> getTierProbabilities() {
        return new HashMap<>(tierProbabilities);
    }

    /**
     * Get the item type probabilities (for debugging/display).
     */
    public Map<String, Double> getItemTypeProbabilities() {
        return new HashMap<>(itemTypeProbabilities);
    }

    /**
     * Get the item catalog (for debugging/display).
     */
    public Map<Integer, Map<String, List<String>>> getItemCatalog() {
        return new HashMap<>(itemCatalog);
    }

    public Map<Integer, Map<String, Map<String, Double>>> getItemProbabilities() {
        return new HashMap<>(itemProbabilities);
    }
}
