package ui.screens;

import ui.GamePanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class MenuScreen {

    private GamePanel gamePanel;

    private Rectangle arenaBtn     = new Rectangle(680, 200, 115, 50);
    private Rectangle dungeonBtn   = new Rectangle(805, 200, 115, 50);
    private Rectangle hubBtn       = new Rectangle(680, 260, 240, 50);
    private Rectangle loadoutBtn   = new Rectangle(680, 320, 240, 50);
    private Rectangle customizeBtn = new Rectangle(680, 400, 240, 50);
    private Rectangle shopBtn      = new Rectangle(680, 480, 240, 50);
    private Rectangle settingsBtn  = new Rectangle(680, 560, 240, 50);
    private Rectangle helpBtn         = new Rectangle(680, 640, 240, 50);
    private Rectangle graphTestBtn    = new Rectangle(10, 850, 140, 30);
    private Rectangle itemsBtn        = new Rectangle(160, 850, 140, 30);
    private Rectangle lootboxTestBtn  = new Rectangle(310, 850, 140, 30);
    private Rectangle statsBtn        = new Rectangle(460, 850, 140, 30);
    private Rectangle resetBtn        = new Rectangle(1450, 850, 140, 40);
    
    private boolean showStatsPanel = false;

    public MenuScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void handleClick(int x, int y) {
        if (arenaBtn.contains(x, y))     gamePanel.switchScreen("game");
        if (dungeonBtn.contains(x, y))   gamePanel.switchScreen("dungeon");
        if (hubBtn.contains(x, y))       gamePanel.switchScreen("hub");
        if (loadoutBtn.contains(x, y))   gamePanel.switchScreen("loadout");
        if (customizeBtn.contains(x, y)) gamePanel.switchScreen("customize");
        if (shopBtn.contains(x, y))      gamePanel.switchScreen("shop");
        if (settingsBtn.contains(x, y))  gamePanel.switchScreen("settings");
        if (helpBtn.contains(x, y))      gamePanel.switchScreen("help");
        if (graphTestBtn.contains(x, y)) gamePanel.switchScreen("graphtest");
        if (itemsBtn.contains(x, y))     gamePanel.switchScreen("items");
        if (lootboxTestBtn.contains(x, y)) gamePanel.switchScreen("lootboxtest");
        if (statsBtn.contains(x, y))     showStatsPanel = !showStatsPanel;
        if (resetBtn.contains(x, y))     handleReset();
    }
    
    private void handleReset() {
        save.SaveManager.resetSave();
        // Reload the game to apply reset values
        javax.swing.SwingUtilities.invokeLater(() -> {
            java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor((java.awt.Component) gamePanel);
            if (window instanceof javax.swing.JFrame) {
                ((javax.swing.JFrame) window).dispose();
            }
            // Use reflection to create new Main instance (Main is in default package)
            try {
                Class<?> mainClass = Class.forName("Main");
                mainClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, 1600, 900);

        // Draw currencies in top left corner
        drawCurrencies(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 52));
        FontMetrics fm = g.getFontMetrics();
        String title = "Abyss";
        g.drawString(title, (1600 - fm.stringWidth(title)) / 2, 150);

        drawButton(g, arenaBtn, "Arena");
        drawButton(g, dungeonBtn, "Dungeon");
        drawButton(g, hubBtn, "Hub");
        drawButton(g, loadoutBtn, "Loadout");
        drawButton(g, customizeBtn, "Customize");
        drawButton(g, shopBtn, "Shop");
        drawButton(g, settingsBtn, "Settings");
        drawButton(g, helpBtn, "Help");

        // Draw Graph Test button (bottom left)
        g.setColor(new Color(70, 70, 70));
        g.fill(graphTestBtn);
        g.setColor(Color.WHITE);
        g.draw(graphTestBtn);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics btnFm = g.getFontMetrics();
        String btnText = "Graph Test";
        int btnTextX = graphTestBtn.x + (graphTestBtn.width - btnFm.stringWidth(btnText)) / 2;
        int btnTextY = graphTestBtn.y + (graphTestBtn.height + btnFm.getAscent() - btnFm.getDescent()) / 2;
        g.drawString(btnText, btnTextX, btnTextY);

        // Draw Items button (next to Graph Test)
        g.setColor(new Color(70, 70, 70));
        g.fill(itemsBtn);
        g.setColor(Color.WHITE);
        g.draw(itemsBtn);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        String itemsText = "Gallery";
        int itemsTextX = itemsBtn.x + (itemsBtn.width - btnFm.stringWidth(itemsText)) / 2;
        int itemsTextY = itemsBtn.y + (itemsBtn.height + btnFm.getAscent() - btnFm.getDescent()) / 2;
        g.drawString(itemsText, itemsTextX, itemsTextY);

        // Draw Lootbox Test button (next to Gallery)
        g.setColor(new Color(70, 70, 70));
        g.fill(lootboxTestBtn);
        g.setColor(Color.WHITE);
        g.draw(lootboxTestBtn);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        String lootboxText = "Lootbox Test";
        int lootboxTextX = lootboxTestBtn.x + (lootboxTestBtn.width - btnFm.stringWidth(lootboxText)) / 2;
        int lootboxTextY = lootboxTestBtn.y + (lootboxTestBtn.height + btnFm.getAscent() - btnFm.getDescent()) / 2;
        g.drawString(lootboxText, lootboxTextX, lootboxTextY);

        // Draw Stats button (next to Lootbox Test)
        g.setColor(new Color(70, 70, 70));
        g.fill(statsBtn);
        g.setColor(Color.WHITE);
        g.draw(statsBtn);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        String statsText = "Stats";
        int statsTextX = statsBtn.x + (statsBtn.width - btnFm.stringWidth(statsText)) / 2;
        int statsTextY = statsBtn.y + (statsBtn.height + btnFm.getAscent() - btnFm.getDescent()) / 2;
        g.drawString(statsText, statsTextX, statsTextY);

        // Draw Stats Panel if visible
        if (showStatsPanel) {
            drawStatsPanel(g);
        }

        // Draw Reset button (bottom right) - RED color to indicate danger
        g.setColor(new Color(180, 50, 50));
        g.fill(resetBtn);
        g.setColor(Color.WHITE);
        g.draw(resetBtn);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        String resetText = "RESET";
        int resetTextX = resetBtn.x + (resetBtn.width - btnFm.stringWidth(resetText)) / 2;
        int resetTextY = resetBtn.y + (resetBtn.height + btnFm.getAscent() - btnFm.getDescent()) / 2;
        g.drawString(resetText, resetTextX, resetTextY);
    }

    private void drawStatsPanel(Graphics2D g) {
        int panelX = 400;
        int panelY = 200;
        int panelWidth = 800;
        int panelHeight = 500;
        
        // Semi-transparent dark background
        g.setColor(new Color(30, 30, 30, 240));
        g.fillRect(panelX, panelY, panelWidth, panelHeight);
        g.setColor(Color.WHITE);
        g.drawRect(panelX, panelY, panelWidth, panelHeight);
        
        // Title
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.setColor(Color.WHITE);
        String title = "PLAYER STATS";
        int titleX = panelX + (panelWidth - g.getFontMetrics().stringWidth(title)) / 2;
        g.drawString(title, titleX, panelY + 50);
        
        // Load saved data
        save.SaveData data = save.SaveManager.load();
        
        // Level and XP Bar
        int level = data.getPlayerLevel();
        int xp = data.getPlayerXP();
        int xpNeeded = level * 100; // Simple formula: need level*100 XP for next level
        
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String levelText = "Level " + level;
        g.drawString(levelText, panelX + 50, panelY + 100);
        
        // XP Bar
        int barX = panelX + 200;
        int barY = panelY + 80;
        int barWidth = 500;
        int barHeight = 30;
        
        // Bar background
        g.setColor(new Color(50, 50, 50));
        g.fillRect(barX, barY, barWidth, barHeight);
        
        // XP fill
        double xpProgress = Math.min(1.0, (double) xp / xpNeeded);
        int fillWidth = (int) (barWidth * xpProgress);
        g.setColor(new Color(0, 191, 255)); // Blue for XP
        g.fillRect(barX, barY, fillWidth, barHeight);
        
        // Bar border
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);
        
        // XP text
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        String xpText = xp + " / " + xpNeeded + " XP";
        int xpTextX = barX + (barWidth - g.getFontMetrics().stringWidth(xpText)) / 2;
        int xpTextY = barY + 20;
        g.drawString(xpText, xpTextX, xpTextY);
        
        // Stats grid
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        int statsX = panelX + 100;
        int statsY = panelY + 160;
        int lineHeight = 35;
        
        // Column 1
        g.setColor(new Color(200, 200, 200));
        g.drawString("Total Kills: " + data.getTotalKills(), statsX, statsY);
        g.drawString("Total Damage: " + data.getTotalDamageDealt(), statsX, statsY + lineHeight);
        g.drawString("Highest Wave: " + data.getHighestWave(), statsX, statsY + lineHeight * 2);
        g.drawString("Games Played: " + data.getGamesPlayed(), statsX, statsY + lineHeight * 3);
        
        // Column 2 - Currency
        int col2X = panelX + 450;
        g.setColor(new Color(34, 139, 34)); // Green for cash
        g.drawString("Cash: $" + data.getCash(), col2X, statsY);
        g.setColor(new Color(0, 191, 255)); // Blue for gems
        g.drawString("Gems: ◆" + data.getGems(), col2X, statsY + lineHeight);
        
        // Close instruction
        g.setColor(new Color(150, 150, 150));
        g.setFont(new Font("Arial", Font.ITALIC, 14));
        String closeText = "Click Stats button again to close";
        int closeX = panelX + (panelWidth - g.getFontMetrics().stringWidth(closeText)) / 2;
        g.drawString(closeText, closeX, panelY + panelHeight - 30);
    }

    private void drawButton(Graphics2D g, Rectangle btn, String label) {
        g.setColor(new Color(70, 70, 70));
        g.fill(btn);
        g.setColor(Color.WHITE);
        g.draw(btn);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        FontMetrics fm = g.getFontMetrics();
        int textX = btn.x + (btn.width - fm.stringWidth(label)) / 2;
        int textY = btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, textX, textY);
    }

    private void drawCurrencies(Graphics2D g) {
        int x = 20;
        int y = 30;
        int spacing = 25;

        g.setFont(new Font("Arial", Font.BOLD, 18));

        // Cash (permanent currency)
        g.setColor(new Color(34, 139, 34)); // Green color
        String cashText = "$ " + gamePanel.getCurrencyManager().getCash().getAmount();
        g.drawString(cashText, x, y);

        // Gems (permanent currency)
        g.setColor(new Color(0, 191, 255)); // Blue color
        String gemsText = "◆ " + gamePanel.getCurrencyManager().getGems().getAmount();
        g.drawString(gemsText, x, y + spacing);
    }
}
