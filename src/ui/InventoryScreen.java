package ui;

import combat.Inventory;
import combat.Item;
import combat.charms.Charm;
import combat.powers.Power;
import combat.summons.Summon;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class InventoryScreen {

    private static final int SLOT_SIZE = 50;
    private static final int SLOT_SPACING = 5;
    
    private int panelX, panelY, panelWidth, panelHeight;
    private int playerIconX, playerIconY;
    private int playerIconWidth = 120;
    private int playerIconHeight = 120;
    
    private int charmSlotsStartX, charmSlotsY;
    private int powerSlotX, powerSlotY;
    private int summonSlotX, summonSlotY;
    
    private int hoveredSlot = -1; // -1 = none, 0-2 = combat.charms, 3 = power, 4 = summon

    private Map<String, BufferedImage> iconCache = new HashMap<>();

    public void draw(Graphics2D g, Inventory inventory, int playerScreenX, int playerScreenY, int screenWidth, int screenHeight, int chestX, int chestY, boolean chestOpen) {
        // Update positions first (if not already updated this frame)
        updatePositions(playerScreenX, playerScreenY, screenWidth, screenHeight, chestX, chestY, chestOpen);

        // Background panel (semi-transparent)
        g.setColor(new Color(40, 40, 40, 200)); // 200 alpha = ~78% opacity
        g.fillRect(panelX, panelY + 5, panelWidth, panelHeight);
        g.setColor(new Color(255, 255, 255, 200)); // Semi-transparent white border
        g.drawRect(panelX, panelY + 5, panelWidth, panelHeight);
        
        // Player icon area
        g.setColor(new Color(60, 60, 60));
        g.fillRect(playerIconX, playerIconY, playerIconWidth, playerIconHeight);
        g.setColor(Color.WHITE);
        g.drawRect(playerIconX, playerIconY, playerIconWidth, playerIconHeight);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Player", playerIconX + 35, playerIconY + 65);
        
        // Charm slots label
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Charms", charmSlotsStartX, charmSlotsY - 8);
        
        // Draw 3 charm slots
        for (int i = 0; i < 3; i++) {
            int slotX = charmSlotsStartX + i * (SLOT_SIZE + SLOT_SPACING);
            drawSlot(g, slotX, charmSlotsY, inventory.getCharm(i), i == hoveredSlot);
        }
        
        // Power slot
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Power", powerSlotX, powerSlotY - 8);
        drawSlot(g, powerSlotX, powerSlotY, inventory.getPower(), hoveredSlot == 3);
        
        // Summon slot
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Summon", summonSlotX, summonSlotY - 8);
        drawSlot(g, summonSlotX, summonSlotY, inventory.getSummon(), hoveredSlot == 4);
    }
    
    private void drawSlot(Graphics2D g, int x, int y, Item item, boolean isHovered) {
        if (isHovered) {
            g.setColor(Color.YELLOW);
            g.fillRect(x - 2, y - 2, SLOT_SIZE + 4, SLOT_SIZE + 4);
        }

        g.setColor(Color.GRAY);
        g.fillRect(x, y, SLOT_SIZE, SLOT_SIZE);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, SLOT_SIZE, SLOT_SIZE);

        if (item != null) {
            BufferedImage icon = getIcon(item.getIconPath());
            if (icon != null) {
                int iconX = x + (SLOT_SIZE - icon.getWidth()) / 2;
                int iconY = y + (SLOT_SIZE - icon.getHeight()) / 2;
                g.drawImage(icon, iconX, iconY, null);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            String name = item.getName();
            if (name != null && name.length() > 8) {
                name = name.substring(0, 8) + ".";
            }
            g.drawString(name, x + 5, y + 15);

            g.setFont(new Font("Arial", Font.PLAIN, 8));
            String rarity = item.getRarity();
            if (rarity != null) {
                g.drawString(rarity, x + 5, y + 40);
            }
        }
    }
    
    public void setHoveredSlot(int slot) {
        this.hoveredSlot = slot;
    }
    
    public int getHoveredSlot() {
        return hoveredSlot;
    }
    
    public void updatePositions(int playerScreenX, int playerScreenY, int screenWidth, int screenHeight, int chestX, int chestY, boolean chestOpen) {
        panelWidth = 420;
        panelHeight = 250;

        if (chestOpen && chestX > 0 && chestY > 0) {
            // Position directly above chest hotbar
            // Chest hotbar is at chestY - slotSize - 40
            int chestHotbarY = chestY - SLOT_SIZE - 40;
            panelX = chestX - (panelWidth / 2);
            panelY = chestHotbarY - panelHeight - 10; // Directly above chest hotbar with small spacing
        } else {
            // Fixed position on screen - centered horizontally, near top
            panelX = (screenWidth - panelWidth) / 2;
            panelY = 150; // Fixed position 150 pixels from top
        }

        // Calculate all element positions relative to panel
        playerIconX = panelX + 20;
        playerIconY = panelY + 40;

        charmSlotsStartX = playerIconX + playerIconWidth + 30;
        charmSlotsY = panelY + 30;

        powerSlotX = charmSlotsStartX;
        powerSlotY = charmSlotsY + SLOT_SIZE + 35;

        summonSlotX = charmSlotsStartX;
        summonSlotY = powerSlotY + SLOT_SIZE + 35;
    }

    public int getSlotAtPosition(int mouseX, int mouseY) {
        // Check charm slots (0-2)
        for (int i = 0; i < 3; i++) {
            int slotX = charmSlotsStartX + i * (SLOT_SIZE + SLOT_SPACING);
            if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE &&
                mouseY >= charmSlotsY && mouseY < charmSlotsY + SLOT_SIZE) {
                return i;
            }
        }

        // Check power slot (3)
        if (mouseX >= powerSlotX && mouseX < powerSlotX + SLOT_SIZE &&
            mouseY >= powerSlotY && mouseY < powerSlotY + SLOT_SIZE) {
            return 3;
        }

        // Check summon slot (4)
        if (mouseX >= summonSlotX && mouseX < summonSlotX + SLOT_SIZE &&
            mouseY >= summonSlotY && mouseY < summonSlotY + SLOT_SIZE) {
            return 4;
        }

        return -1;
    }
    
    public boolean isValidDrop(int slot, Item item) {
        if (slot < 0 || slot > 4) return false;
        
        if (slot >= 0 && slot <= 2) {
            // Charm slots - only accept Charm items
            return item instanceof Charm;
        } else if (slot == 3) {
            // Power slot - only accept Power items
            return item instanceof Power;
        } else if (slot == 4) {
            // Summon slot - only accept Summon items
            return item instanceof Summon;
        }
        
        return false;
    }
    
    private BufferedImage getIcon(String iconPath) {
        if (iconPath == null || iconPath.isEmpty()) return null;
        if (iconCache.containsKey(iconPath)) {
            return iconCache.get(iconPath);
        }
        try {
            BufferedImage img = ImageIO.read(new File(iconPath));
            iconCache.put(iconPath, img);
            return img;
        } catch (IOException e) {
            System.err.println("Failed to load icon: " + iconPath);
            return null;
        }
    }
}
