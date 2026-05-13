package ui.screens;

import ui.GamePanel;
import java.awt.*;
import javax.swing.ImageIcon;

public class MenuScreen {

    private GamePanel gamePanel;
    private ImageIcon menuBackground;
    private String hoveredButton = null;

    // Main menu buttons (redesigned)
    private Rectangle arenaBtn     = new Rectangle(680, 200, 115, 60);
    private Rectangle dungeonBtn   = new Rectangle(805, 200, 115, 60);
    private Rectangle hubBtn       = new Rectangle(680, 280, 240, 60);
    private Rectangle craftingBtn  = new Rectangle(930, 280, 240, 60);
    private Rectangle loadoutBtn   = new Rectangle(680, 360, 240, 60);
    private Rectangle customizeBtn = new Rectangle(680, 440, 240, 60);
    private Rectangle shopBtn      = new Rectangle(680, 520, 240, 60);
    private Rectangle rewardsBtn   = new Rectangle(680, 600, 240, 60);
    private Rectangle skillTreeBtn = new Rectangle(680, 680, 240, 60);
    private Rectangle settingsBtn  = new Rectangle(680, 760, 240, 60);
    private Rectangle helpBtn      = new Rectangle(680, 840, 240, 50);
    
    // Debug/utility buttons (bottom row)
    private Rectangle graphTestBtn    = new Rectangle(10, 850, 140, 40);
    private Rectangle itemsBtn        = new Rectangle(160, 850, 140, 40);
    private Rectangle lootboxTestBtn  = new Rectangle(310, 850, 140, 40);
    private Rectangle statsBtn        = new Rectangle(460, 850, 140, 40);
    private Rectangle resetBtn        = new Rectangle(1450, 850, 140, 40);
    /** Session admin toggle — above RESET (same column). */
    private Rectangle adminToggleBtn   = new Rectangle(1450, 798, 140, 44);
    
    private boolean showStatsPanel = false;

    public MenuScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        loadMenuBackground();
    }
    
    private void loadMenuBackground() {
        try {
            menuBackground = new ImageIcon("assets/background/menu/menu.gif");
        } catch (Exception e) {
            System.err.println("Failed to load menu background: " + e.getMessage());
        }
    }

    public void handleClick(int x, int y) {
        if (arenaBtn.contains(x, y))     gamePanel.switchScreen("arenaselection");
        if (dungeonBtn.contains(x, y))   gamePanel.switchScreen("dungeon");
        if (hubBtn.contains(x, y))       gamePanel.switchScreen("hub");
        if (craftingBtn.contains(x, y))  gamePanel.switchScreen("crafting");
        if (loadoutBtn.contains(x, y))   gamePanel.switchScreen("loadout");
        if (customizeBtn.contains(x, y)) gamePanel.switchScreen("customize");
        if (shopBtn.contains(x, y))      gamePanel.switchScreen("shop");
        if (rewardsBtn.contains(x, y))   gamePanel.switchScreen("rewards");
        if (skillTreeBtn.contains(x, y)) gamePanel.switchScreen("skilltree");
        if (settingsBtn.contains(x, y))  gamePanel.switchScreen("settings");
        if (helpBtn.contains(x, y))      gamePanel.switchScreen("help");
        if (graphTestBtn.contains(x, y)) gamePanel.switchScreen("graphtest");
        if (itemsBtn.contains(x, y))     gamePanel.switchScreen("items");
        if (lootboxTestBtn.contains(x, y)) gamePanel.switchScreen("lootboxtest");
        if (statsBtn.contains(x, y))     showStatsPanel = !showStatsPanel;
        if (adminToggleBtn.contains(x, y)) gamePanel.toggleSessionAdminMode();
        if (resetBtn.contains(x, y))     handleReset();
    }
    
    public void handleMouseMove(int x, int y) {
        hoveredButton = null;
        if (arenaBtn.contains(x, y)) hoveredButton = "arena";
        if (dungeonBtn.contains(x, y)) hoveredButton = "dungeon";
        if (hubBtn.contains(x, y)) hoveredButton = "hub";
        if (craftingBtn.contains(x, y)) hoveredButton = "crafting";
        if (loadoutBtn.contains(x, y)) hoveredButton = "loadout";
        if (customizeBtn.contains(x, y)) hoveredButton = "customize";
        if (shopBtn.contains(x, y)) hoveredButton = "shop";
        if (rewardsBtn.contains(x, y)) hoveredButton = "rewards";
        if (skillTreeBtn.contains(x, y)) hoveredButton = "skilltree";
        if (settingsBtn.contains(x, y)) hoveredButton = "settings";
        if (helpBtn.contains(x, y)) hoveredButton = "help";
        if (graphTestBtn.contains(x, y)) hoveredButton = "graph";
        if (itemsBtn.contains(x, y)) hoveredButton = "items";
        if (lootboxTestBtn.contains(x, y)) hoveredButton = "lootbox";
        if (statsBtn.contains(x, y)) hoveredButton = "stats";
        if (adminToggleBtn.contains(x, y)) hoveredButton = "admin";
        if (resetBtn.contains(x, y)) hoveredButton = "reset";
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
        // Draw menu background (animated GIF support, scaled larger to hide name at bottom)
        if (menuBackground != null) {
            g.drawImage(menuBackground.getImage(), -80, -60, 1760, 1020, null);
        } else {
            // Fallback gradient background
            GradientPaint bgGradient = new GradientPaint(0, 0, new Color(30, 30, 40), 0, 900, new Color(20, 20, 30));
            g.setPaint(bgGradient);
            g.fillRect(0, 0, 1600, 900);
        }

        // Draw currencies in top left corner with modern styling
        drawCurrencies(g);

        // Title with pixel font and better readability
        g.setColor(Color.WHITE);
        Font pixelFont = new Font("Press Start 2P", Font.BOLD, 72);
        if (!pixelFont.getFamily().equals("Press Start 2P")) {
            // Fallback to Monospaced if pixel font not available
            pixelFont = new Font("Monospaced", Font.BOLD, 72);
        }
        g.setFont(pixelFont);
        FontMetrics fm = g.getFontMetrics();
        String title = "Abyss";
        int titleX = (1600 - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 140);

        // Subtitle
        g.setColor(new Color(200, 200, 220));
        Font pixelFontSmall = new Font("Press Start 2P", Font.PLAIN, 16);
        if (!pixelFontSmall.getFamily().equals("Press Start 2P")) {
            pixelFontSmall = new Font("Monospaced", Font.PLAIN, 16);
        }
        g.setFont(pixelFontSmall);
        String subtitle = "Choose your path";
        int subtitleX = (1600 - g.getFontMetrics().stringWidth(subtitle)) / 2;
        g.drawString(subtitle, subtitleX, 165);

        // Main menu buttons with modern styling
        drawStyledButton(g, arenaBtn, "Arena", new Color(231, 76, 60), hoveredButton == "arena");
        drawStyledButton(g, dungeonBtn, "Dungeon", new Color(192, 57, 43), hoveredButton == "dungeon");
        drawStyledButton(g, hubBtn, "Hub", new Color(52, 152, 219), hoveredButton == "hub");
        drawStyledButton(g, craftingBtn, "Crafting", new Color(80, 190, 140), hoveredButton == "crafting");
        drawStyledButton(g, loadoutBtn, "Loadout", new Color(147, 112, 219), hoveredButton == "loadout");
        drawStyledButton(g, customizeBtn, "Customize", new Color(155, 89, 182), hoveredButton == "customize");
        drawStyledButton(g, shopBtn, "Shop", new Color(46, 204, 113), hoveredButton == "shop");
        drawStyledButton(g, rewardsBtn, "Rewards", new Color(241, 196, 15), hoveredButton == "rewards");
        drawStyledButton(g, skillTreeBtn, "Skill Tree", new Color(255, 170, 60), hoveredButton == "skilltree");
        drawStyledButton(g, settingsBtn, "Settings", new Color(149, 165, 166), hoveredButton == "settings");
        drawStyledButton(g, helpBtn, "Help", new Color(149, 165, 166), hoveredButton == "help");

        // Debug/utility buttons with modern styling
        drawStyledSmallButton(g, graphTestBtn, "Graph Test", hoveredButton == "graph");
        drawStyledSmallButton(g, itemsBtn, "Gallery", hoveredButton == "items");
        drawStyledSmallButton(g, lootboxTestBtn, "Lootbox Test", hoveredButton == "lootbox");
        drawStyledSmallButton(g, statsBtn, "Stats", hoveredButton == "stats");
        
        // Draw Stats Panel if visible
        if (showStatsPanel) {
            drawStatsPanel(g);
        }

        drawMenuAdminToggle(g);

        // Draw Reset button (danger button)
        drawStyledResetButton(g, resetBtn, "RESET", hoveredButton == "reset");
    }

    private void drawMenuAdminToggle(Graphics2D g) {
        boolean on = gamePanel.isSessionAdminMode();
        boolean h = "admin".equals(hoveredButton);
        Color fill = h
                ? (on ? new Color(90, 170, 120) : new Color(210, 130, 55))
                : (on ? new Color(60, 130, 85) : new Color(145, 88, 32));
        g.setColor(fill);
        g.fillRoundRect(adminToggleBtn.x, adminToggleBtn.y, adminToggleBtn.width, adminToggleBtn.height, 8, 8);
        g.setColor(new Color(40, 38, 36));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(adminToggleBtn.x, adminToggleBtn.y, adminToggleBtn.width, adminToggleBtn.height, 8, 8);
        g.setStroke(new BasicStroke(1f));
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 13));
        String label = on ? "Admin: ON" : "Admin: OFF";
        FontMetrics fm = g.getFontMetrics();
        int tx = adminToggleBtn.x + (adminToggleBtn.width - fm.stringWidth(label)) / 2;
        int ty = adminToggleBtn.y + (adminToggleBtn.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, tx, ty);
    }

    private void drawStatsPanel(Graphics2D g) {
        int panelX = 400;
        int panelY = 200;
        int panelWidth = 800;
        int panelHeight = 500;
        
        // Modern panel background with gradient border
        g.setColor(new Color(0, 0, 0, 220));
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 16, 16);
        
        GradientPaint borderGradient = new GradientPaint(panelX, panelY, new Color(100, 200, 255), panelX + panelWidth, panelY + panelHeight, new Color(147, 112, 219));
        g.setPaint(borderGradient);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 16, 16);
        g.setStroke(new BasicStroke(1));
        g.setPaint(null);
        
        // Title
        g.setFont(new Font("Segoe UI", Font.BOLD, 32));
        g.setColor(new Color(100, 200, 255));
        String title = "PLAYER STATS";
        int titleX = panelX + (panelWidth - g.getFontMetrics().stringWidth(title)) / 2;
        g.drawString(title, titleX, panelY + 50);
        
        // Load saved data
        save.SaveData data = save.SaveManager.load();
        
        // Level and XP Bar
        int level = data.getPlayerLevel();
        int xp = data.getPlayerXP();
        int xpNeeded = level * 100;
        
        g.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g.setColor(new Color(255, 215, 0));
        String levelText = "Level " + level;
        g.drawString(levelText, panelX + 50, panelY + 100);
        
        // XP Bar
        int barX = panelX + 200;
        int barY = panelY + 75;
        int barWidth = 500;
        int barHeight = 35;
        
        // Bar background
        g.setColor(new Color(50, 50, 60));
        g.fillRoundRect(barX, barY, barWidth, barHeight, 8, 8);
        
        // XP fill
        double xpProgress = Math.min(1.0, (double) xp / xpNeeded);
        int fillWidth = (int) (barWidth * xpProgress);
        GradientPaint xpGradient = new GradientPaint(barX, barY, new Color(100, 200, 255), barX + fillWidth, barY, new Color(0, 191, 255));
        g.setPaint(xpGradient);
        g.fillRoundRect(barX, barY, fillWidth, barHeight, 8, 8);
        g.setPaint(null);
        
        // Bar border
        g.setColor(new Color(100, 200, 255));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(barX, barY, barWidth, barHeight, 8, 8);
        g.setStroke(new BasicStroke(1));
        
        // XP text
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g.setColor(Color.WHITE);
        String xpText = xp + " / " + xpNeeded + " XP";
        int xpTextX = barX + (barWidth - g.getFontMetrics().stringWidth(xpText)) / 2;
        int xpTextY = barY + 22;
        g.drawString(xpText, xpTextX, xpTextY);
        
        // Stats grid
        g.setFont(new Font("Segoe UI", Font.PLAIN, 18));
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
        g.setColor(new Color(46, 204, 113));
        g.drawString("Cash: $" + data.getCash(), col2X, statsY);
        g.setColor(new Color(52, 152, 219));
        g.drawString("Gems: ◆" + data.getGems(), col2X, statsY + lineHeight);
        g.setColor(new Color(255, 200, 80));
        g.drawString("Skill Points: " + data.getSkillPoints(), col2X, statsY + lineHeight * 2);
        
        // Close instruction
        g.setColor(new Color(150, 150, 170));
        g.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        String closeText = "Click Stats button again to close";
        int closeX = panelX + (panelWidth - g.getFontMetrics().stringWidth(closeText)) / 2;
        g.drawString(closeText, closeX, panelY + panelHeight - 30);
    }

    private void drawStyledButton(Graphics2D g, Rectangle btn, String label, Color accentColor, boolean isHovered) {
        // Button background with gradient
        if (isHovered) {
            GradientPaint gradient = new GradientPaint(btn.x, btn.y, accentColor.brighter(), btn.x, btn.y + btn.height, accentColor);
            g.setPaint(gradient);
        } else {
            GradientPaint gradient = new GradientPaint(btn.x, btn.y, new Color(50, 50, 60), btn.x, btn.y + btn.height, new Color(40, 40, 50));
            g.setPaint(gradient);
        }
        g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 12, 12);
        g.setPaint(null);

        // Border
        g.setColor(isHovered ? accentColor : new Color(80, 80, 90));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 12, 12);
        g.setStroke(new BasicStroke(1));

        // Text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        FontMetrics fm = g.getFontMetrics();
        int textX = btn.x + (btn.width - fm.stringWidth(label)) / 2;
        int textY = btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, textX, textY);
    }

    private void drawStyledSmallButton(Graphics2D g, Rectangle btn, String label, boolean isHovered) {
        // Button background with gradient
        if (isHovered) {
            GradientPaint gradient = new GradientPaint(btn.x, btn.y, new Color(100, 100, 120), btn.x, btn.y + btn.height, new Color(70, 70, 90));
            g.setPaint(gradient);
        } else {
            GradientPaint gradient = new GradientPaint(btn.x, btn.y, new Color(50, 50, 60), btn.x, btn.y + btn.height, new Color(40, 40, 50));
            g.setPaint(gradient);
        }
        g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        g.setPaint(null);

        // Border
        g.setColor(isHovered ? new Color(100, 200, 255) : new Color(70, 70, 80));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        g.setStroke(new BasicStroke(1));

        // Text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        FontMetrics fm = g.getFontMetrics();
        int textX = btn.x + (btn.width - fm.stringWidth(label)) / 2;
        int textY = btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, textX, textY);
    }

    private void drawStyledResetButton(Graphics2D g, Rectangle btn, String label, boolean isHovered) {
        // Danger button with red gradient
        if (isHovered) {
            GradientPaint gradient = new GradientPaint(btn.x, btn.y, new Color(220, 80, 80), btn.x, btn.y + btn.height, new Color(180, 50, 50));
            g.setPaint(gradient);
        } else {
            GradientPaint gradient = new GradientPaint(btn.x, btn.y, new Color(180, 50, 50), btn.x, btn.y + btn.height, new Color(150, 40, 40));
            g.setPaint(gradient);
        }
        g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        g.setPaint(null);

        // Border
        g.setColor(isHovered ? new Color(255, 100, 100) : new Color(200, 70, 70));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        g.setStroke(new BasicStroke(1));

        // Text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        int textX = btn.x + (btn.width - fm.stringWidth(label)) / 2;
        int textY = btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, textX, textY);
    }

    private void drawCurrencies(Graphics2D g) {
        int x = 20;
        int y = 30;
        int spacing = 30;

        // Draw currency panel background
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(x - 10, y - 25, 200, 70, 12, 12);
        g.setColor(new Color(100, 100, 120));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x - 10, y - 25, 200, 70, 12, 12);
        g.setStroke(new BasicStroke(1));

        g.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Cash (permanent currency)
        g.setColor(new Color(46, 204, 113));
        String cashText = "$ " + gamePanel.getCurrencyManager().getCash().getAmount();
        g.drawString(cashText, x, y);

        // Gems (permanent currency)
        g.setColor(new Color(52, 152, 219));
        String gemsText = "◆ " + gamePanel.getCurrencyManager().getGems().getAmount();
        g.drawString(gemsText, x, y + spacing);
    }
}
