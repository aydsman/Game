package world.chests;

import combat.Item;
import combat.ItemRegistry;

public class Chest {

    protected int x;
    protected int y;
    protected int tier;
    protected int slotCount;
    protected Item[] items;
    protected int interactionRadius;
    protected boolean isOpen;
    protected boolean isInRange;
    protected boolean isEmpty;

    private static ItemRegistry itemRegistry = new ItemRegistry();

    public Chest(int x, int y, int tier, int slotCount) {
        this.x = x;
        this.y = y;
        this.tier = tier;
        this.slotCount = slotCount;
        this.items = itemRegistry.generateChestLoot(tier, slotCount);
        this.interactionRadius = 100;
        this.isOpen = false;
        this.isInRange = false;
        updateEmptyStatus();
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getTier() { return tier; }
    public int getSlotCount() { return slotCount; }
    public Item[] getItems() { return items; }
    public int getInteractionRadius() { return interactionRadius; }
    public boolean isOpen() { return isOpen; }
    public boolean isInRange() { return isInRange; }
    public boolean isEmpty() { return isEmpty; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setOpen(boolean open) { this.isOpen = open; }
    public void setInRange(boolean inRange) { this.isInRange = inRange; }
    public void setEmpty(boolean empty) { this.isEmpty = empty; }

    public Item getItem(int slot) {
        if (slot >= 0 && slot < items.length) {
            return items[slot];
        }
        return null;
    }

    public void setItem(int slot, Item item) {
        if (slot >= 0 && slot < items.length) {
            items[slot] = item;
            updateEmptyStatus();
        }
    }

    private void updateEmptyStatus() {
        boolean hasItem = false;
        for (Item item : items) {
            if (item != null) {
                hasItem = true;
                break;
            }
        }
        this.isEmpty = !hasItem;
    }
}
