package inventory;

import combat.Item;

public class Inventory {

    private Item[] hotbar; // 5 slots for quick access
    private int selectedSlot; // Currently selected hotbar slot (0-4)

    public Inventory() {
        hotbar = new Item[5]; // 5-slot hotbar
        selectedSlot = 0; // Default to first slot
    }

    public void setItem(int slot, Item item) {
        if (slot >= 0 && slot < hotbar.length) {
            hotbar[slot] = item;
        }
    }

    public Item getItem(int slot) {
        if (slot >= 0 && slot < hotbar.length) {
            return hotbar[slot];
        }
        return null;
    }

    public Item getSelectedItem() {
        return hotbar[selectedSlot];
    }

    public void setSelectedSlot(int slot) {
        if (slot >= 0 && slot < hotbar.length) {
            selectedSlot = slot;
        }
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public int getHotbarSize() {
        return hotbar.length;
    }

    public void scrollHotbar(int direction) {
        // direction: 1 for scroll down (next slot), -1 for scroll up (previous slot)
        selectedSlot += direction;

        // Loop around
        if (selectedSlot >= hotbar.length) {
            selectedSlot = 0;
        } else if (selectedSlot < 0) {
            selectedSlot = hotbar.length - 1;
        }
    }
}
