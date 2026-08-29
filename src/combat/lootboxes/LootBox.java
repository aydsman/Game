package combat.lootboxes;

import combat.Item;
import combat.ItemRegistry;
import combat.clothing.ClothingItem;
import java.util.*;

public class LootBox {
    protected String name;
    protected Map<Integer, Double> tierProbabilities = new HashMap<>();
    protected Map<String, Double> itemTypeProbabilities = new HashMap<>();
    protected Map<Integer, Map<String, List<String>>> itemCatalog = new HashMap<>();
    protected final Random random = new Random();
    protected final ItemRegistry itemRegistry = new ItemRegistry();

    public LootBox(String name) { this.name = name; }

    public void setTierProbability(int tier, double probability) { tierProbabilities.put(tier, probability); }
    public void setItemTypeProbability(String type, double probability) { itemTypeProbabilities.put(type, probability); }

    public void addItem(int tier, String type, String itemName) {
        itemCatalog.computeIfAbsent(tier, k -> new HashMap<>())
                .computeIfAbsent(type, k -> new ArrayList<>()).add(itemName);
    }

    public void addItem(String type, String itemName) { addItem(1, type, itemName); }
    public void addItem(String type, String itemName, double weight) { addItem(1, type, itemName); }

    public Item open() {
        Integer tier = rollForTier();
        if (tier == null) return null;
        String type = rollForItemType();
        if (type == null) return null;
        String itemName = pickRandomItem(tier, type);
        if (itemName == null) return null;
        return createItem(itemName, tier);
    }

    protected Integer rollForTier() {
        if (tierProbabilities.isEmpty()) return 1;
        double total = tierProbabilities.values().stream().mapToDouble(Double::doubleValue).sum();
        double roll = random.nextDouble() * total;
        double cumulative = 0;
        for (Map.Entry<Integer, Double> e : tierProbabilities.entrySet()) {
            cumulative += e.getValue();
            if (roll <= cumulative) return e.getKey();
        }
        return tierProbabilities.keySet().iterator().next();
    }

    protected String rollForItemType() {
        if (itemTypeProbabilities.isEmpty()) return "Weapon";
        double total = itemTypeProbabilities.values().stream().mapToDouble(Double::doubleValue).sum();
        double roll = random.nextDouble() * total;
        double cumulative = 0;
        for (Map.Entry<String, Double> e : itemTypeProbabilities.entrySet()) {
            cumulative += e.getValue();
            if (roll <= cumulative) return e.getKey();
        }
        return itemTypeProbabilities.keySet().iterator().next();
    }

    protected String pickRandomItem(int tier, String type) {
        Map<String, List<String>> tierMap = itemCatalog.get(tier);
        if (tierMap == null) return null;
        List<String> items = tierMap.get(type);
        if (items == null || items.isEmpty()) return null;
        return items.get(random.nextInt(items.size()));
    }

    protected Item createItem(String itemName, int tier) {
        try {
            for (Item template : itemRegistry.getAllItems()) {
                if (template.getClass().getSimpleName().equals(itemName)) {
                    Item clone = template.clone();
                    clone.setTier(tier);
                    return clone;
                }
            }
            Class<?> cls = Class.forName(resolveClassName(itemName));
            Item item = (Item) cls.getConstructor().newInstance();
            item.setTier(tier);
            return item;
        } catch (Exception e) {
            System.err.println("Failed to create loot item: " + itemName);
            return null;
        }
    }

    private String resolveClassName(String itemName) {
        String[] packages = {
            "combat.ranged.pistols.", "combat.ranged.rifles.", "combat.ranged.shotguns.",
            "combat.ranged.smgs.", "combat.ranged.snipers.", "combat.melee.swords.",
            "combat.melee.hammers.", "combat.melee.daggers.", "combat.melee.maces.",
            "combat.melee.scythes.", "combat.charms.", "combat.powers.",
            "combat.powers.fruits.", "combat.summons.", "combat.consumables."
        };
        for (String pkg : packages) {
            try {
                Class.forName(pkg + itemName);
                return pkg + itemName;
            } catch (ClassNotFoundException ignored) {}
        }
        return "combat.Item";
    }
}
