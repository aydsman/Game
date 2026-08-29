package ui.screens;

import ui.GamePanel;
import java.awt.*;

public class ShopScreen {
    private final GamePanel gamePanel;
    private Rectangle backBtn = new Rectangle(50, 820, 120, 50);

    public ShopScreen(GamePanel gamePanel) { this.gamePanel = gamePanel; }

    public void handleClick(int x, int y) {
        if (backBtn.contains(x, y)) gamePanel.switchScreen("menu");
    }

    public void handleMouseMove(int x, int y) {}
    public void handleMouseScroll(int rotation) {}

    public void draw(Graphics2D g, int screenWidth, int screenHeight) {
        g.setColor(new Color(18, 24, 32));
        g.fillRect(0, 0, screenWidth, screenHeight);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g.drawString("Shop", 60, 60);
        g.fillRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height);
        g.drawString("Back", backBtn.x + 36, backBtn.y + 32);
    }
}
