package world.chests;

import combat.Item;

public class Chest {
    protected int x;
    protected int y;
    protected int tier;
    protected int interactionRadius = 80;
    protected boolean inRange = false;
    protected boolean open = false;
    protected final Item[] slots;
    protected final int slotCount;

    public Chest(int x, int y, int tier, int slotCount) {
        this.x = x;
        this.y = y;
        this.tier = tier;
        this.slotCount = slotCount;
        this.slots = new Item[slotCount];
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getTier() { return tier; }
    public int getInteractionRadius() { return interactionRadius; }
    public boolean isInRange() { return inRange; }
    public void setInRange(boolean inRange) { this.inRange = inRange; }
    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }
    public Item getItem(int slot) {
        if (slot < 0 || slot >= slotCount) return null;
        return slots[slot];
    }
    public void setItem(int slot, Item item) {
        if (slot >= 0 && slot < slotCount) slots[slot] = item;
    }
    public int getSlotCount() { return slotCount; }
}
