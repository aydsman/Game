package world;

import java.awt.Color;
import java.awt.Graphics2D;

public class Arena {
    protected int width;
    protected int height;
    protected GameMap gameMap;

    public Arena(int width, int height) {
        this.width = width;
        this.height = height;
        this.gameMap = new GameMap();
    }

    public int getWidth()  { return width; }
    public int getHeight() { return height; }

    public void draw(Graphics2D g, int screenWidth, int screenHeight, int cameraX, int cameraY) {
        // draw out of bounds (black)
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // draw tiles
        gameMap.draw(g, screenWidth, screenHeight, cameraX, cameraY, width, height);
    }
}
