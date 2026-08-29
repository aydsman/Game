package world.chests;

import combat.Item;
import combat.ItemRegistry;

public class ArenaChest extends Chest {
  private static final int DEFAULT_SLOTS = 5;

  public ArenaChest(int x, int y, int tier) {
    super(x, y, tier, DEFAULT_SLOTS);
    ItemRegistry registry = new ItemRegistry();
    Item[] loot = registry.generateChestLoot(tier, DEFAULT_SLOTS);
    for (int i = 0; i < loot.length && i < DEFAULT_SLOTS; i++) {
      setItem(i, loot[i]);
    }
  }
}
