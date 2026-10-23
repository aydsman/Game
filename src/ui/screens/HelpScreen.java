package ui.screens;

import ui.GamePanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

public class HelpScreen {

    private GamePanel gamePanel;
    private Rectangle backBtn = new Rectangle(10, 840, 120, 50);
    private int hoverX;
    private int hoverY;

    private static final String[][] COMBAT_CONTROLS = {
            {"WASD / Arrows", "Move"},
            {"Mouse", "Aim"},
            {"Left Click", "Shoot or melee swing"},
            {"Shift", "Dodge (brief invulnerability)"},
            {"R", "Reload"},
            {"1 - 5", "Power moves (when a power is equipped)"},
            {"Space", "Interact with chests"},
            {"Esc / P", "Pause"}
    };

    private static final String[][] SYSTEM_CONTROLS = {
            {"Mouse Wheel", "Cycle hotbar slots"},
            {"Tab", "Open / close inventory"},
            {"E", "Talk to NPCs and objects"},
            {"F", "Skip the wave grace period"},
            {"O", "Toggle debug overlays"},
            {"U", "Toggle stats panel"},
            {"K", "Kill all enemies (debug)"},
            {"L", "Advance dungeon level"}
    };

    public HelpScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void handleMouseMove(int x, int y) {
        this.hoverX = x;
        this.hoverY = y;
    }

    public void handleClick(int x, int y) {
        if (backBtn.contains(x, y)) gamePanel.switchScreen(gamePanel.getPreviousScreen());
    }

    public void draw(Graphics2D g, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint bg = new GradientPaint(0, 0, new Color(24, 26, 34), 0, height, new Color(14, 16, 22));
        g.setPaint(bg);
        g.fillRect(0, 0, width, height);
        g.setPaint(null);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 40));
        g.drawString("Controls", 60, 90);

        drawControlList(g, "Movement & Combat", COMBAT_CONTROLS, 60, 170);
        drawControlList(g, "Hotbar & System", SYSTEM_CONTROLS, 800, 170);

        drawTips(g, 60, 520);

        drawButton(g, backBtn, "Back", backBtn.contains(hoverX, hoverY));
    }

    private void drawControlList(Graphics2D g, String title, String[][] rows, int x, int y) {
        g.setColor(new Color(150, 160, 185));
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g.drawString(title, x, y);

        int rowY = y + 40;
        for (String[] row : rows) {
            g.setColor(new Color(120, 220, 255));
            g.setFont(new Font("Consolas", Font.BOLD, 17));
            g.drawString(row[0], x, rowY);

            g.setColor(new Color(200, 205, 220));
            g.setFont(new Font("Segoe UI", Font.PLAIN, 17));
            g.drawString(row[1], x + 200, rowY);
            rowY += 32;
        }
    }

    private void drawTips(Graphics2D g, int x, int y) {
        g.setColor(new Color(150, 160, 185));
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g.drawString("Getting Stronger", x, y);

        String[] tips = {
                "Spend skill points in the Skill Tree - nodes raise damage, HP, speed, crit, loot and cash.",
                "Talk to the Blacksmith in the hub to pick a loadout or merge gear into higher tiers.",
                "The Quest Giver tracks real progress; rewards unlock once their objective is met.",
                "Charms and potions stack with skill bonuses, so save them for tough waves.",
                "Dodge with Shift. Hammer slammers telegraph a red circle — leave it before it lands.",
                "Daggers combo on connecting hits. Shotguns fall off at range. Perks unlock at levels 5, 10 and 15."
        };

        int rowY = y + 40;
        g.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        g.setColor(new Color(190, 195, 210));
        for (String tip : tips) {
            g.drawString("- " + tip, x, rowY);
            rowY += 30;
        }
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
