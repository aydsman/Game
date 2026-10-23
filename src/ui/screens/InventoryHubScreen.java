package ui.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import ui.GamePanel;

public class InventoryHubScreen {
    private final GamePanel gamePanel;

    private final Rectangle backBtn = new Rectangle(40, 30, 120, 46);
    private final Rectangle loadoutBtn = new Rectangle(590, 260, 420, 84);
    private final Rectangle customizeBtn = new Rectangle(590, 380, 420, 84);
    private final Rectangle profileBtn = new Rectangle(590, 500, 420, 84);
    private String hovered = null;

    public InventoryHubScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void handleClick(int x, int y) {
        if (backBtn.contains(x, y)) {
            gamePanel.switchScreen("menu");
            return;
        }
        if (loadoutBtn.contains(x, y)) {
            gamePanel.switchScreen("loadout");
            return;
        }
        if (customizeBtn.contains(x, y)) {
            gamePanel.switchScreen("customize");
            return;
        }
        if (profileBtn.contains(x, y)) {
            gamePanel.switchScreen("profile");
        }
    }

    public void handleMouseMove(int x, int y) {
        hovered = null;
        if (backBtn.contains(x, y)) hovered = "back";
        if (loadoutBtn.contains(x, y)) hovered = "loadout";
        if (customizeBtn.contains(x, y)) hovered = "customize";
        if (profileBtn.contains(x, y)) hovered = "profile";
    }

    public void draw(Graphics2D g, int screenWidth, int screenHeight) {
        g.setPaint(new GradientPaint(0, 0, new Color(19, 26, 38), 0, screenHeight, new Color(12, 18, 29)));
        g.fillRect(0, 0, screenWidth, screenHeight);
        g.setPaint(null);

        drawButton(g, backBtn, "Back", "back".equals(hovered), new Color(65, 85, 118));

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 44));
        g.drawString("Inventory", 610, 170);
        g.setColor(new Color(186, 205, 233));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        g.drawString("Choose a section", 650, 202);

        drawButton(g, loadoutBtn, "Loadout", "loadout".equals(hovered), new Color(68, 118, 175));
        drawButton(g, customizeBtn, "Customize", "customize".equals(hovered), new Color(133, 95, 177));
        drawButton(g, profileBtn, "Profile", "profile".equals(hovered), new Color(189, 123, 62));
    }

    private void drawButton(Graphics2D g, Rectangle rect, String label, boolean hover, Color accent) {
        Color fill = hover ? accent.brighter() : accent;
        g.setColor(fill);
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 14, 14);
        g.setColor(new Color(226, 236, 248));
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 14, 14);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 28));
        FontMetrics fm = g.getFontMetrics();
        int tx = rect.x + (rect.width - fm.stringWidth(label)) / 2;
        int ty = rect.y + (rect.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, tx, ty);
    }
}
