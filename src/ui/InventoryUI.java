package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import combat.Item;

public class InventoryUI {

    private int slotSize = 50; // Size of each hotbar slot
    private int slotSpacing = 5; // Space between slots
    private int selectedSlot = 0; // Currently selected slot (0-4)

    private Map<String, BufferedImage> iconCache = new HashMap<>();

    public void draw(Graphics2D g, ArrayList<Object> hotbar, int screenWidth, int screenHeight) {
        // Calculate starting position to center hotbar at bottom
        int totalWidth = (slotSize * 5) + (slotSpacing * 4);
        int startX = (screenWidth - totalWidth) / 2;
        int startY = screenHeight - slotSize - 20; // 20px from bottom

        // Draw each slot
        for (int i = 0; i < 5; i++) {
            int slotX = startX + (i * (slotSize + slotSpacing));

            // Draw slot background
            if (i == selectedSlot) {
                g.setColor(Color.YELLOW); // Highlight selected slot
                g.fillRect(slotX - 2, startY - 2, slotSize + 4, slotSize + 4);
            }
            g.setColor(Color.GRAY);
            g.fillRect(slotX, startY, slotSize, slotSize);

            // Draw item if exists
            if (i < hotbar.size() && hotbar.get(i) != null) {
                Object item = hotbar.get(i);
                if (item instanceof Item) {
                    Item weapon = (Item) item;
                    BufferedImage icon = getIcon(weapon.getIconPath());
                    if (icon != null) {
                        int iconX = slotX + (slotSize - icon.getWidth()) / 2;
                        int iconY = startY + (slotSize - icon.getHeight()) / 2;
                        g.drawImage(icon, iconX, iconY, null);
                    }
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.PLAIN, 10));
                    String name = weapon.getName();
                    if (name != null) {
                        g.drawString(name, slotX + 5, startY + 15);
                    }

                    // Draw tier
                    g.setFont(new Font("Arial", Font.PLAIN, 8));
                    String rarity = weapon.getRarity();
                    if (rarity != null) {
                        g.drawString(rarity, slotX + 5, startY + 40);
                    }
                }
            }

            // Draw slot number
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString(String.valueOf(i + 1), slotX + slotSize - 15, startY + slotSize - 5);
        }
    }

    public void setSelectedSlot(int slot) {
        if (slot >= 0 && slot < 5) {
            selectedSlot = slot;
        }
    }

    public int getSelectedSlot() {
        return selectedSlot;
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
