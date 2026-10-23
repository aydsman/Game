package ui.screens;

import skilltree.SkillEffects;
import ui.GamePanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

public class SettingsScreen {

    private GamePanel gamePanel;
    private Rectangle backBtn = new Rectangle(10, 840, 120, 50);
    private Rectangle displayToggleBtn = new Rectangle(940, 250, 260, 52);
    private Rectangle helpBtn = new Rectangle(940, 330, 260, 52);
    private int hoverX;
    private int hoverY;

    public SettingsScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void handleMouseMove(int x, int y) {
        this.hoverX = x;
        this.hoverY = y;
    }

    public void handleClick(int x, int y) {
        if (backBtn.contains(x, y)) {
            gamePanel.switchScreen(gamePanel.getPreviousScreen());
            return;
        }
        if (displayToggleBtn.contains(x, y)) {
            gamePanel.setFullScreenDisplay(!gamePanel.isFullScreenDisplay());
            return;
        }
        if (helpBtn.contains(x, y)) {
            gamePanel.switchScreen("help");
        }
    }

    public void draw(Graphics2D g, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint bg = new GradientPaint(0, 0, new Color(24, 26, 34), 0, height, new Color(14, 16, 22));
        g.setPaint(bg);
        g.fillRect(0, 0, width, height);
        g.setPaint(null);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 40));
        g.drawString("Settings", 60, 90);

        drawDisplaySection(g);
        drawBonusSection(g);

        drawButton(g, backBtn, "Back", backBtn.contains(hoverX, hoverY));
    }

    private void drawDisplaySection(Graphics2D g) {
        g.setColor(new Color(150, 160, 185));
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g.drawString("Display", 940, 200);

        g.setColor(new Color(200, 205, 220));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        boolean fullScreen = gamePanel.isFullScreenDisplay();
        g.drawString(fullScreen ? "Borderless fullscreen" : "Windowed 1600x900", 940, 228);

        drawButton(g, displayToggleBtn, fullScreen ? "Switch to Windowed" : "Switch to Fullscreen",
                displayToggleBtn.contains(hoverX, hoverY));
        drawButton(g, helpBtn, "View Controls", helpBtn.contains(hoverX, hoverY));
    }

    private void drawBonusSection(Graphics2D g) {
        g.setColor(new Color(150, 160, 185));
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g.drawString("Active Skill Bonuses", 60, 200);

        String[][] rows = {
                {"Damage", pct(SkillEffects.damageBonusFraction())},
                {"Max HP", pct(SkillEffects.maxHpBonusFraction())},
                {"Move speed", pct(SkillEffects.moveSpeedBonusFraction())},
                {"Crit chance", pct(SkillEffects.critChance())},
                {"Reload speed", pct((1.0 / SkillEffects.reloadTimeMultiplier()) - 1.0)},
                {"Healing received", pct(SkillEffects.healingMultiplier() - 1.0)},
                {"Cash gain", pct(SkillEffects.cashGainMultiplier() - 1.0)},
                {"Gem gain", pct(SkillEffects.gemGainMultiplier() - 1.0)},
                {"Loot quality", pct(SkillEffects.lootQualityBonusFraction())}
        };

        int y = 240;
        g.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        for (String[] row : rows) {
            g.setColor(new Color(190, 195, 210));
            g.drawString(row[0], 60, y);
            g.setColor(row[1].equals("+0%") ? new Color(120, 126, 145) : new Color(120, 220, 255));
            g.drawString(row[1], 320, y);
            y += 30;
        }

        g.setColor(new Color(130, 136, 155));
        g.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        g.drawString("Unlock nodes in the Skill Tree to raise these.", 60, y + 14);
    }

    private String pct(double fraction) {
        return "+" + Math.round(fraction * 100) + "%";
    }

    private void drawButton(Graphics2D g, Rectangle btn, String label, boolean hovered) {
        GradientPaint gradient = hovered
                ? new GradientPaint(btn.x, btn.y, new Color(70, 90, 130), btn.x, btn.y + btn.height, new Color(50, 65, 95))
                : new GradientPaint(btn.x, btn.y, new Color(44, 48, 60), btn.x, btn.y + btn.height, new Color(34, 38, 48));
        g.setPaint(gradient);
        g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 10, 10);
        g.setPaint(null);

        g.setColor(hovered ? new Color(130, 180, 240) : new Color(80, 86, 100));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 10, 10);
        g.setStroke(new BasicStroke(1));

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 17));
        FontMetrics fm = g.getFontMetrics();
        int textX = btn.x + (btn.width - fm.stringWidth(label)) / 2;
        int textY = btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, textX, textY);
    }
}
