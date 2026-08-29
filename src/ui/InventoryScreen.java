package ui;

import combat.Inventory;
import combat.charms.Charm;
import combat.powers.Power;
import combat.summons.Summon;
import java.awt.*;

public class InventoryScreen {
    private int hoveredSlot = -1;
    private int invStartX;
    private int invStartY;

    public void updatePositions(int playerScreenX, int playerScreenY, int screenWidth, int screenHeight,
                                int chestX, int chestY, boolean chestOpen) {
        invStartX = playerScreenX + 60;
        invStartY = playerScreenY - 30;
        if (chestOpen) {
            invStartX = chestX + 120;
            invStartY = chestY + 40;
        }
    }

    public void draw(Graphics2D g, Inventory inventory, int playerScreenX, int playerScreenY,
                     int screenWidth, int screenHeight, int chestX, int chestY, boolean chestOpen) {
        updatePositions(playerScreenX, playerScreenY, screenWidth, screenHeight, chestX, chestY, chestOpen);
        int slotSize = 44;
        for (int i = 0; i < inventory.getMaxCharms(); i++) {
            int x = invStartX + i * (slotSize + 6);
            int y = invStartY;
            g.setColor(i == hoveredSlot ? Color.YELLOW : Color.GRAY);
            g.fillRect(x, y, slotSize, slotSize);
            Charm c = inventory.getCharm(i);
            if (c != null) {
                g.setColor(Color.WHITE);
                g.drawString("C" + (i + 1), x + 8, y + 24);
            }
        }
        g.setColor(hoveredSlot == 10 ? Color.YELLOW : Color.GRAY);
        g.fillRect(invStartX, invStartY + slotSize + 10, slotSize, slotSize);
        Power p = inventory.getPower();
        if (p != null) {
            g.setColor(Color.WHITE);
            g.drawString("P", invStartX + 14, invStartY + slotSize + 34);
        }
        g.setColor(hoveredSlot == 11 ? Color.YELLOW : Color.GRAY);
        g.fillRect(invStartX + slotSize + 10, invStartY + slotSize + 10, slotSize, slotSize);
        Summon s = inventory.getSummon();
        if (s != null) {
            g.setColor(Color.WHITE);
            g.drawString("S", invStartX + slotSize + 24, invStartY + slotSize + 34);
        }
    }

    public int getSlotAtPosition(int mouseX, int mouseY) {
        int slotSize = 44;
        for (int i = 0; i < 3; i++) {
            int x = invStartX + i * (slotSize + 6);
            int y = invStartY;
            if (mouseX >= x && mouseX < x + slotSize && mouseY >= y && mouseY < y + slotSize) return i;
        }
        int py = invStartY + slotSize + 10;
        if (mouseX >= invStartX && mouseX < invStartX + slotSize && mouseY >= py && mouseY < py + slotSize) return 10;
        int sx = invStartX + slotSize + 10;
        if (mouseX >= sx && mouseX < sx + slotSize && mouseY >= py && mouseY < py + slotSize) return 11;
        return -1;
    }

    public void setHoveredSlot(int slot) { hoveredSlot = slot; }

    public boolean isValidDrop(int slot, combat.Item item) {
        if (item == null) return false;
        if (slot >= 0 && slot < 3) return item instanceof Charm;
        if (slot == 10) return item instanceof Power;
        if (slot == 11) return item instanceof Summon;
        return false;
    }
}
