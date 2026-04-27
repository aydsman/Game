package ui.screens;

import ui.GamePanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class HelpScreen {

    private GamePanel gamePanel;
    private Rectangle backBtn = new Rectangle(10, 520, 120, 50);

    public HelpScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void handleClick(int x, int y) {
        if (backBtn.contains(x, y)) gamePanel.switchScreen("menu");
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, 800, 600);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Help", 20, 50);

        drawButton(g, backBtn, "Back");
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
}
