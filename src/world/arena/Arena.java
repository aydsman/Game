package world.arena;

import world.GameMap;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Arena {
    protected int width;
    protected int height;
    protected GameMap gameMap;
    private final List<Rectangle> obstacles = new ArrayList<>();

    public Arena(int width, int height) {
        this.width = width;
        this.height = height;
        this.gameMap = new GameMap();
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public List<Rectangle> getObstacles() { return Collections.unmodifiableList(obstacles); }

    public void clearObstacles() { obstacles.clear(); }

    public void addObstacle(int x, int y, int w, int h) {
        obstacles.add(new Rectangle(x, y, w, h));
    }

    public void draw(Graphics2D g, int screenWidth, int screenHeight, int cameraX, int cameraY) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);
        if (gameMap != null) {
            gameMap.draw(g, screenWidth, screenHeight, cameraX, cameraY, width, height);
        }
        g.setColor(new Color(80, 80, 80, 120));
        for (Rectangle r : obstacles) {
            g.fillRect(r.x - cameraX, r.y - cameraY, r.width, r.height);
        }
    }

    public double getEnemyHealthMultiplier() { return 1.0; }
    public double getEnemySpeedMultiplier() { return 1.0; }
    public double getEnemyDamageMultiplier() { return 1.0; }
    public int getDifficultyLevel() { return 1; }
}
