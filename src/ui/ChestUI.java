package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import combat.Item;
import world.chests.Chest;

public class ChestUI {

    private int slotSize = 50;
    private int slotSpacing = 5;
    private int hoveredSlot = -1;

    public void draw(Graphics2D g, Chest chest, int chestX, int chestY, int screenWidth, int screenHeight) {
        if (!chest.isOpen()) {
            return;
        }

        int slotCount = chest.getSlotCount();
        Item[] items = chest.getItems();

        int totalWidth = (slotSize * slotCount) + (slotSpacing * (slotCount - 1));
        int startX = chestX - (totalWidth / 2);
        int startY = chestY - slotSize - 40;

        for (int i = 0; i < slotCount; i++) {
            int slotX = startX + (i * (slotSize + slotSpacing));

            if (i == hoveredSlot) {
                g.setColor(Color.YELLOW);
                g.fillRect(slotX - 2, startY - 2, slotSize + 4, slotSize + 4);
            }

            g.setColor(Color.GRAY);
            g.fillRect(slotX, startY, slotSize, slotSize);

            if (items[i] != null) {
                Item item = items[i];
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 10));
                String name = item.getName();
                if (name != null) {
                    g.drawString(name, slotX + 5, startY + 15);
                }

                g.setFont(new Font("Arial", Font.PLAIN, 8));
                String rarity = item.getRarity();
                if (rarity != null) {
                    g.drawString(rarity, slotX + 5, startY + 40);
                }
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString(String.valueOf(i + 1), slotX + slotSize - 15, startY + slotSize - 5);
        }
    }

    public void setHoveredSlot(int slot) {
        this.hoveredSlot = slot;
    }

    public int getHoveredSlot() {
        return hoveredSlot;
    }

    public int getSlotAtPosition(int mouseX, int mouseY, Chest chest, int chestX, int chestY) {
        if (!chest.isOpen()) {
            return -1;
        }

        int slotCount = chest.getSlotCount();
        int totalWidth = (slotSize * slotCount) + (slotSpacing * (slotCount - 1));
        int startX = chestX - (totalWidth / 2);
        int startY = chestY - slotSize - 40;

        for (int i = 0; i < slotCount; i++) {
            int slotX = startX + (i * (slotSize + slotSpacing));
            if (mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= startY && mouseY < startY + slotSize) {
                return i;
            }
        }
        return -1;
    }

    public int getSlotSize() { return slotSize; }
    public int getSlotSpacing() { return slotSpacing; }
}
