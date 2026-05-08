package ui.screens;

import combat.Item;
import combat.lootboxes.Lootbox1;
import ui.GamePanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Screen for testing loot boxes.
 * Shows a grid of rolls with configurable quantities (x1, x5, x10, x25, x100).
 * Displays item name, tier, and type for each rolled item.
 */
public class LootboxTestScreen {

    private GamePanel gamePanel;
    private Lootbox1 testLootbox;

    private Rectangle backBtn = new Rectangle(10, 10, 80, 40);
    private Rectangle x1Btn = new Rectangle(150, 10, 100, 40);
    private Rectangle x5Btn = new Rectangle(260, 10, 100, 40);
    private Rectangle x10Btn = new Rectangle(370, 10, 100, 40);
    private Rectangle x25Btn = new Rectangle(480, 10, 100, 40);
    private Rectangle x100Btn = new Rectangle(590, 10, 100, 40);

    private List<LootResult> results = new ArrayList<>();

    public LootboxTestScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.testLootbox = new Lootbox1();
    }

    public void handleClick(int x, int y) {
        if (backBtn.contains(x, y)) {
            gamePanel.switchScreen("menu");
        } else if (x1Btn.contains(x, y)) {
            rollLootbox(1);
        } else if (x5Btn.contains(x, y)) {
            rollLootbox(5);
        } else if (x10Btn.contains(x, y)) {
            rollLootbox(10);
        } else if (x25Btn.contains(x, y)) {
            rollLootbox(25);
        } else if (x100Btn.contains(x, y)) {
            rollLootbox(100);
        }
    }

    private void rollLootbox(int count) {
        results.clear();
        for (int i = 0; i < count; i++) {
            Item item = testLootbox.open();
            if (item != null) {
                LootResult result = new LootResult(item);
                results.add(result);
                // Unlock the item in LoadoutScreen
                gamePanel.getLoadoutScreen().unlockItem(result.type, result.itemName, result.tier);
            }
        }
    }

    public void draw(Graphics2D g) {
        // Background
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, 1600, 900);

        // Back button
        g.setColor(new Color(70, 70, 70));
        g.fill(backBtn);
        g.setColor(Color.WHITE);
        g.draw(backBtn);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        FontMetrics fm = g.getFontMetrics();
        String backText = "Back";
        g.drawString(backText, backBtn.x + (backBtn.width - fm.stringWidth(backText)) / 2,
                backBtn.y + (backBtn.height + fm.getAscent() - fm.getDescent()) / 2);

        // Roll buttons
        drawRollButton(g, x1Btn, "x1");
        drawRollButton(g, x5Btn, "x5");
        drawRollButton(g, x10Btn, "x10");
        drawRollButton(g, x25Btn, "x25");
        drawRollButton(g, x100Btn, "x100");

        // Draw results grid
        drawResultsGrid(g);
    }

    private void drawRollButton(Graphics2D g, Rectangle btn, String label) {
        g.setColor(new Color(100, 150, 100));
        g.fill(btn);
        g.setColor(Color.WHITE);
        g.draw(btn);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, btn.x + (btn.width - fm.stringWidth(label)) / 2,
                btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2);
    }

    private void drawResultsGrid(Graphics2D g) {
        int gridStartX = 100;
        int gridStartY = 80;
        int cellWidth = 300;
        int cellHeight = 50;
        int cellSpacingX = 15;
        int cellSpacingY = 10;
        int cellsPerRow = 4;

        g.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();

        // Header
        g.setColor(Color.WHITE);
        g.drawString("Item Name", gridStartX + 10, gridStartY - 10);
        g.drawString("Tier", gridStartX + 150, gridStartY - 10);
        g.drawString("Type", gridStartX + 200, gridStartY - 10);

        // Draw results
        for (int i = 0; i < results.size(); i++) {
            int row = i / cellsPerRow;
            int col = i % cellsPerRow;
            int x = gridStartX + col * (cellWidth + cellSpacingX);
            int y = gridStartY + row * (cellHeight + cellSpacingY);

            // Bounds check
            if (y + cellHeight > 880) break;

            LootResult result = results.get(i);

            // Cell background
            g.setColor(new Color(60, 60, 60));
            g.fillRect(x, y, cellWidth, cellHeight);
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(x, y, cellWidth, cellHeight);

            // Text
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 11));

            // Item name
            String name = result.itemName;
            if (name.length() > 15) {
                name = name.substring(0, 12) + "...";
            }
            g.drawString(name, x + 10, y + 18);

            // Tier and Type
            String tierAndType = "T" + result.tier + " | " + result.type;
            g.drawString(tierAndType, x + 10, y + 35);
        }

        // Summary
        if (!results.isEmpty()) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.drawString("Total rolls: " + results.size(), gridStartX, 860);
        }
    }

    /**
     * Inner class to store loot results
     */
    private static class LootResult {
        String itemName;
        int tier;
        String type;

        LootResult(Item item) {
            this.itemName = item.getClass().getSimpleName(); // Use class name, not display name
            this.tier = item.getTier();
            this.type = getItemType(item);
        }

        private static String getItemType(Item item) {
            String className = item.getClass().getName().toLowerCase();

            // Check class hierarchy and package names (case-insensitive)
            if (className.contains("ranged") || className.contains("melee") ||
                className.contains("pistol") || className.contains("rifle") ||
                className.contains("shotgun") || className.contains("smg") ||
                className.contains("sniper") || className.contains("sword") ||
                className.contains("hammer") || className.contains("dagger") ||
                className.contains("mace") || className.contains("scythe")) {
                return "Weapon";
            } else if (className.contains("charm")) {
                return "Charm";
            } else if (className.contains("power")) {
                return "Power";
            } else if (className.contains("summon")) {
                return "Summon";
            } else if (className.contains("consumable") || className.contains("potion")) {
                return "Consumable";
            }
            return "Unknown";
        }
    }
}

