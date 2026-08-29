package ui.screens;

import ui.GamePanel;
import java.awt.*;

public class ItemGalleryScreen {
    private final GamePanel gamePanel;
    private Rectangle backBtn = new Rectangle(50, 820, 120, 50);

    public ItemGalleryScreen(GamePanel gamePanel) { this.gamePanel = gamePanel; }

    public void handleClick(int x, int y) {
        if (backBtn.contains(x, y)) gamePanel.switchScreen("menu");
    }

    public void handleMouseScroll(int rotation) {}

    public void draw(Graphics2D g) {
        g.setColor(new Color(20, 22, 30));
        g.fillRect(0, 0, 1600, 900);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g.drawString("Item Gallery", 60, 60);
        g.fillRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height);
        g.drawString("Back", backBtn.x + 36, backBtn.y + 32);
    }
}
