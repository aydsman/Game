package ui;

import world.chests.Chest;
import java.awt.*;

public class ChestUI {
    private int hoveredSlot = -1;
    private static final int SLOT_SIZE = 50;
    private static final int SLOT_GAP = 8;

    public void draw(Graphics2D g, Chest chest, int chestX, int chestY, int screenWidth, int screenHeight) {
        int slots = chest.getSlotCount();
        int totalW = slots * SLOT_SIZE + (slots - 1) * SLOT_GAP;
        int startX = chestX - totalW / 2;
        int startY = chestY + 40;
        g.setColor(new Color(30, 30, 40, 220));
        g.fillRoundRect(startX - 10, startY - 10, totalW + 20, SLOT_SIZE + 20, 8, 8);
        for (int i = 0; i < slots; i++) {
            int x = startX + i * (SLOT_SIZE + SLOT_GAP);
            g.setColor(i == hoveredSlot ? Color.YELLOW : Color.DARK_GRAY);
            g.fillRect(x, startY, SLOT_SIZE, SLOT_SIZE);
            combat.Item item = chest.getItem(i);
            if (item != null) {
                g.setColor(Color.WHITE);
                g.drawString(item.getName(), x + 4, startY + 20);
            }
        }
    }

    public int getSlotAtPosition(int mouseX, int mouseY, Chest chest, int chestX, int chestY) {
        int slots = chest.getSlotCount();
        int totalW = slots * SLOT_SIZE + (slots - 1) * SLOT_GAP;
        int startX = chestX - totalW / 2;
        int startY = chestY + 40;
        for (int i = 0; i < slots; i++) {
            int x = startX + i * (SLOT_SIZE + SLOT_GAP);
            if (mouseX >= x && mouseX < x + SLOT_SIZE && mouseY >= startY && mouseY < startY + SLOT_SIZE) {
                return i;
            }
        }
        return -1;
    }

    public void setHoveredSlot(int slot) { hoveredSlot = slot; }
}
