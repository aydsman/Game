package ui.screens;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import ui.GamePanel;

public class MenuScreen {
    private final GamePanel gamePanel;
    private ImageIcon menuBackground;
    private String hoveredButton = null;
    private boolean showPlayScreen = false;

    // Main menu buttons
    private final Rectangle playBtn = new Rectangle(540, 250, 520, 56);
    private final Rectangle hubBtn = new Rectangle(540, 320, 254, 56);
    private final Rectangle craftingBtn = new Rectangle(806, 320, 254, 56);
    // Replaces old Loadout + Customize pair
    private final Rectangle inventoryBtn = new Rectangle(540, 390, 254, 56);
    private final Rectangle shopBtn = new Rectangle(806, 390, 254, 56);
    private final Rectangle rewardsBtn = new Rectangle(540, 460, 254, 56);
    private final Rectangle skillTreeBtn = new Rectangle(806, 460, 254, 56);
    private final Rectangle settingsBtn = new Rectangle(540, 530, 254, 56);
    private final Rectangle helpBtn = new Rectangle(806, 530, 254, 56);
    private final Rectangle quitBtn = new Rectangle(540, 600, 520, 56);
    private final Rectangle arenaOptionBtn = new Rectangle(580, 340, 440, 74);
    private final Rectangle dungeonOptionBtn = new Rectangle(580, 435, 440, 74);
    private final Rectangle playBackBtn = new Rectangle(580, 545, 440, 56);

    // Utility buttons
    private final Rectangle graphTestBtn = new Rectangle(10, 850, 140, 40);
    private final Rectangle itemsBtn = new Rectangle(160, 850, 140, 40);
    private final Rectangle lootboxTestBtn = new Rectangle(310, 850, 140, 40);
    private final Rectangle statsBtn = new Rectangle(460, 850, 140, 40);
    private final Rectangle masteryBtn = new Rectangle(610, 850, 140, 40);
    private final Rectangle characterBtn = new Rectangle(760, 850, 140, 40);
    private final Rectangle adminToggleBtn = new Rectangle(1450, 798, 140, 44);
    private final Rectangle resetBtn = new Rectangle(1450, 850, 140, 40);

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
        if (showPlayScreen) {
            if (arenaOptionBtn.contains(x, y)) {
                showPlayScreen = false;
                gamePanel.switchScreen("modeselection");
                return;
            }
            if (dungeonOptionBtn.contains(x, y)) {
                showPlayScreen = false;
                gamePanel.switchScreen("dungeon");
                return;
            }
            if (playBackBtn.contains(x, y)) {
                showPlayScreen = false;
            }
            return;
        }
        if (playBtn.contains(x, y)) {
            showPlayScreen = true;
            return;
        }
        if (hubBtn.contains(x, y)) gamePanel.switchScreen("hub");
        if (craftingBtn.contains(x, y)) gamePanel.switchScreen("crafting");
        if (inventoryBtn.contains(x, y)) gamePanel.switchScreen("inventory");
        if (shopBtn.contains(x, y)) gamePanel.switchScreen("shop");
        if (rewardsBtn.contains(x, y)) gamePanel.switchScreen("rewards");
        if (skillTreeBtn.contains(x, y)) gamePanel.switchScreen("skilltree");
        if (settingsBtn.contains(x, y)) gamePanel.switchScreen("settings");
        if (helpBtn.contains(x, y)) gamePanel.switchScreen("help");
        if (quitBtn.contains(x, y)) System.exit(0);

        if (graphTestBtn.contains(x, y)) gamePanel.switchScreen("graphtest");
        if (itemsBtn.contains(x, y)) gamePanel.switchScreen("items");
        if (lootboxTestBtn.contains(x, y)) gamePanel.switchScreen("lootboxtest");
        if (statsBtn.contains(x, y)) showStatsPanel = !showStatsPanel;
        if (masteryBtn.contains(x, y)) gamePanel.switchScreen("mastery");
        if (characterBtn.contains(x, y)) gamePanel.switchScreen("character");
        if (adminToggleBtn.contains(x, y)) gamePanel.toggleSessionAdminMode();
        if (resetBtn.contains(x, y)) handleReset();
    }

    public void handleMouseMove(int x, int y) {
        hoveredButton = null;
        if (showPlayScreen) {
            if (arenaOptionBtn.contains(x, y)) hoveredButton = "play_arena";
            if (dungeonOptionBtn.contains(x, y)) hoveredButton = "play_dungeon";
            if (playBackBtn.contains(x, y)) hoveredButton = "play_back";
            return;
        }
        if (playBtn.contains(x, y)) hoveredButton = "play";
        if (hubBtn.contains(x, y)) hoveredButton = "hub";
        if (craftingBtn.contains(x, y)) hoveredButton = "crafting";
        if (inventoryBtn.contains(x, y)) hoveredButton = "inventory";
        if (shopBtn.contains(x, y)) hoveredButton = "shop";
        if (rewardsBtn.contains(x, y)) hoveredButton = "rewards";
        if (skillTreeBtn.contains(x, y)) hoveredButton = "skilltree";
        if (settingsBtn.contains(x, y)) hoveredButton = "settings";
        if (helpBtn.contains(x, y)) hoveredButton = "help";
        if (quitBtn.contains(x, y)) hoveredButton = "quit";
        if (graphTestBtn.contains(x, y)) hoveredButton = "graph";
        if (itemsBtn.contains(x, y)) hoveredButton = "items";
        if (lootboxTestBtn.contains(x, y)) hoveredButton = "lootbox";
        if (statsBtn.contains(x, y)) hoveredButton = "stats";
        if (masteryBtn.contains(x, y)) hoveredButton = "mastery";
        if (characterBtn.contains(x, y)) hoveredButton = "character";
        if (adminToggleBtn.contains(x, y)) hoveredButton = "admin";
        if (resetBtn.contains(x, y)) hoveredButton = "reset";
    }

    public void draw(Graphics2D g) {
        if (menuBackground != null) {
            g.drawImage(menuBackground.getImage(), -80, -60, 1760, 1020, null);
        } else {
            GradientPaint bgGradient = new GradientPaint(0, 0, new Color(30, 30, 40), 0, 900, new Color(20, 20, 30));
            g.setPaint(bgGradient);
            g.fillRect(0, 0, 1600, 900);
            g.setPaint(null);
        }

        drawCurrencies(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 72));
        String title = "Abyss";
        FontMetrics titleFm = g.getFontMetrics();
        g.drawString(title, (1600 - titleFm.stringWidth(title)) / 2, 170);

        g.setColor(new Color(210, 220, 235));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        String subtitle = "Choose your path";
        FontMetrics subFm = g.getFontMetrics();
        g.drawString(subtitle, (1600 - subFm.stringWidth(subtitle)) / 2, 210);

        if (showPlayScreen) {
            drawPlayScreen(g);
            return;
        }

        drawMainButtonsPanel(g);

        drawStyledButton(g, playBtn, "Play", new Color(231, 76, 60), "play".equals(hoveredButton));
        drawStyledButton(g, hubBtn, "Hub", new Color(52, 152, 219), "hub".equals(hoveredButton));
        drawStyledButton(g, craftingBtn, "Crafting", new Color(80, 190, 140), "crafting".equals(hoveredButton));
        drawStyledButton(g, inventoryBtn, "Inventory", new Color(116, 116, 212), "inventory".equals(hoveredButton));
        drawStyledButton(g, shopBtn, "Shop", new Color(46, 204, 113), "shop".equals(hoveredButton));
        drawStyledButton(g, rewardsBtn, "Rewards", new Color(241, 196, 15), "rewards".equals(hoveredButton));
        drawStyledButton(g, skillTreeBtn, "Skill Tree", new Color(255, 170, 60), "skilltree".equals(hoveredButton));
        drawStyledButton(g, settingsBtn, "Settings", new Color(149, 165, 166), "settings".equals(hoveredButton));
        drawStyledButton(g, helpBtn, "Help", new Color(149, 165, 166), "help".equals(hoveredButton));
        drawStyledButton(g, quitBtn, "Quit", new Color(192, 57, 43), "quit".equals(hoveredButton));

        drawStyledSmallButton(g, graphTestBtn, "Graph Test", "graph".equals(hoveredButton));
        drawStyledSmallButton(g, itemsBtn, "Gallery", "items".equals(hoveredButton));
        drawStyledSmallButton(g, lootboxTestBtn, "Lootbox Test", "lootbox".equals(hoveredButton));
        drawStyledSmallButton(g, statsBtn, "Stats", "stats".equals(hoveredButton));
        drawStyledSmallButton(g, masteryBtn, "Mastery", "mastery".equals(hoveredButton));
        drawStyledSmallButton(g, characterBtn, "Character", "character".equals(hoveredButton));

        drawMenuAdminToggle(g);
        drawStyledResetButton(g, resetBtn, "RESET", "reset".equals(hoveredButton));

        if (showStatsPanel) {
            drawStatsPanel(g);
        }
    }

    private void drawMainButtonsPanel(Graphics2D g) {
        int x = playBtn.x - 22;
        int y = playBtn.y - 18;
        int w = playBtn.width + 44;
        int h = (quitBtn.y + quitBtn.height) - y + 18;

        g.setColor(new Color(0, 0, 0, 120));
        g.fillRoundRect(x, y, w, h, 16, 16);
        g.setColor(new Color(110, 140, 185));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, w, h, 16, 16);
        g.setStroke(new BasicStroke(1));
    }

    private void drawPlayScreen(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 165));
        g.fillRect(0, 0, 1600, 900);

        int panelX = 540;
        int panelY = 270;
        int panelW = 520;
        int panelH = 360;
        g.setColor(new Color(18, 24, 36, 245));
        g.fillRoundRect(panelX, panelY, panelW, panelH, 16, 16);
        g.setColor(new Color(115, 165, 225));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(panelX, panelY, panelW, panelH, 14, 14);
        g.setStroke(new BasicStroke(1));

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 32));
        String heading = "Choose Mode";
        FontMetrics hfm = g.getFontMetrics();
        g.drawString(heading, panelX + (panelW - hfm.stringWidth(heading)) / 2, panelY + 52);

        drawStyledButton(g, arenaOptionBtn, "Arena", new Color(231, 76, 60), "play_arena".equals(hoveredButton));
        drawStyledButton(g, dungeonOptionBtn, "Dungeon", new Color(192, 57, 43), "play_dungeon".equals(hoveredButton));
        drawStyledButton(g, playBackBtn, "Back", new Color(105, 120, 145), "play_back".equals(hoveredButton));
    }

    private void drawCurrencies(Graphics2D g) {
        int x = 22;
        int y = 30;
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(10, 8, 240, 80, 12, 12);
        g.setColor(new Color(120, 130, 150));
        g.drawRoundRect(10, 8, 240, 80, 12, 12);

        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g.setColor(new Color(65, 220, 140));
        g.drawString("$ " + gamePanel.getCurrencyManager().getCash().getAmount(), x, y + 5);
        g.setColor(new Color(90, 175, 255));
        g.drawString("◆ " + gamePanel.getCurrencyManager().getGems().getAmount(), x, y + 39);
    }

    private void drawStyledButton(Graphics2D g, Rectangle btn, String label, Color accentColor, boolean isHovered) {
        if (isHovered) {
            g.setPaint(new GradientPaint(btn.x, btn.y, accentColor.brighter(), btn.x, btn.y + btn.height, accentColor));
        } else {
            g.setPaint(new GradientPaint(btn.x, btn.y, new Color(50, 50, 60), btn.x, btn.y + btn.height, new Color(40, 40, 50)));
        }
        g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 12, 12);
        g.setPaint(null);
        g.setColor(isHovered ? accentColor : new Color(80, 80, 90));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 12, 12);
        g.setStroke(new BasicStroke(1));

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        FontMetrics fm = g.getFontMetrics();
        int tx = btn.x + (btn.width - fm.stringWidth(label)) / 2;
        int ty = btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, tx, ty);
    }

    private void drawStyledSmallButton(Graphics2D g, Rectangle btn, String label, boolean isHovered) {
        if (isHovered) {
            g.setPaint(new GradientPaint(btn.x, btn.y, new Color(100, 100, 120), btn.x, btn.y + btn.height, new Color(70, 70, 90)));
        } else {
            g.setPaint(new GradientPaint(btn.x, btn.y, new Color(50, 50, 60), btn.x, btn.y + btn.height, new Color(40, 40, 50)));
        }
        g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        g.setPaint(null);
        g.setColor(isHovered ? new Color(100, 200, 255) : new Color(70, 70, 80));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        FontMetrics fm = g.getFontMetrics();
        int tx = btn.x + (btn.width - fm.stringWidth(label)) / 2;
        int ty = btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, tx, ty);
    }

    private void drawStyledResetButton(Graphics2D g, Rectangle btn, String label, boolean isHovered) {
        if (isHovered) {
            g.setPaint(new GradientPaint(btn.x, btn.y, new Color(220, 80, 80), btn.x, btn.y + btn.height, new Color(180, 50, 50)));
        } else {
            g.setPaint(new GradientPaint(btn.x, btn.y, new Color(180, 50, 50), btn.x, btn.y + btn.height, new Color(150, 40, 40)));
        }
        g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        g.setPaint(null);
        g.setColor(isHovered ? new Color(255, 100, 100) : new Color(200, 70, 70));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        int tx = btn.x + (btn.width - fm.stringWidth(label)) / 2;
        int ty = btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, tx, ty);
    }

    private void drawMenuAdminToggle(Graphics2D g) {
        boolean on = gamePanel.isSessionAdminMode();
        boolean hover = "admin".equals(hoveredButton);
        Color fill = hover
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

        g.setColor(new Color(0, 0, 0, 220));
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 16, 16);
        g.setColor(new Color(120, 180, 230));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 16, 16);
        g.setStroke(new BasicStroke(1));

        save.SaveData data = save.SaveManager.load();
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 30));
        g.drawString("PLAYER STATS", panelX + 280, panelY + 50);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        g.drawString("Level: " + data.getPlayerLevel(), panelX + 80, panelY + 120);
        g.drawString("XP: " + data.getPlayerXP(), panelX + 80, panelY + 160);
        g.drawString("Total Kills: " + data.getTotalKills(), panelX + 80, panelY + 200);
        g.drawString("Total Damage: " + data.getTotalDamageDealt(), panelX + 80, panelY + 240);
        g.drawString("Highest Wave: " + data.getHighestWave(), panelX + 80, panelY + 280);
        g.drawString("Games Played: " + data.getGamesPlayed(), panelX + 80, panelY + 320);
        g.drawString("Cash: $" + data.getCash(), panelX + 430, panelY + 200);
        g.drawString("Gems: ◆" + data.getGems(), panelX + 430, panelY + 240);
        g.drawString("Skill Points: " + data.getSkillPoints(), panelX + 430, panelY + 280);
    }

    private void handleReset() {
        save.SaveManager.resetSave();
        javax.swing.SwingUtilities.invokeLater(() -> {
            java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor((java.awt.Component) gamePanel);
            if (window instanceof javax.swing.JFrame) {
                ((javax.swing.JFrame) window).dispose();
            }
            try {
                Class<?> mainClass = Class.forName("Main");
                mainClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
