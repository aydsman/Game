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
    private Rectangle customizeBtn = new Rectangle(680, 280, 240, 50);
    private Rectangle settingsBtn  = new Rectangle(680, 360, 240, 50);
    private Rectangle helpBtn      = new Rectangle(680, 440, 240, 50);
    private Rectangle graphTestBtn = new Rectangle(10, 850, 140, 30);

    public MenuScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void handleClick(int x, int y) {
        if (arenaBtn.contains(x, y))     gamePanel.switchScreen("game");
        if (dungeonBtn.contains(x, y))   gamePanel.switchScreen("dungeon");
        if (customizeBtn.contains(x, y)) gamePanel.switchScreen("customize");
        if (settingsBtn.contains(x, y))  gamePanel.switchScreen("settings");
        if (helpBtn.contains(x, y))      gamePanel.switchScreen("help");
        if (graphTestBtn.contains(x, y)) gamePanel.switchScreen("graphtest");
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, 1600, 900);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 52));
        FontMetrics fm = g.getFontMetrics();
        String title = "SummerGame";
        g.drawString(title, (1600 - fm.stringWidth(title)) / 2, 150);

        drawButton(g, arenaBtn, "Arena");
        drawButton(g, dungeonBtn, "Dungeon");
        drawButton(g, customizeBtn, "Customize");
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
