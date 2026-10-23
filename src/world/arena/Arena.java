package world.arena;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import world.GameMap;

public class Arena {
    protected int width;
    protected int height;
    protected GameMap gameMap;
    /** Solid axis-aligned obstacles (world space). */
    protected final List<Rectangle> obstacles = new ArrayList<>();

    public Arena(int width, int height) {
        this.width = width;
        this.height = height;
        this.gameMap = new GameMap();
    }

    public int getWidth()  { return width; }
    public int getHeight() { return height; }

    public List<Rectangle> getObstacles() {
        return Collections.unmodifiableList(obstacles);
    }

    protected void clearObstacles() {
        obstacles.clear();
    }

    protected void addObstacle(int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) return;
        obstacles.add(new Rectangle(x, y, w, h));
    }

    /**
     * Closed ring of walls on the playable bounds. Without this the map just stops, and leftover
     * interior cover reads as a broken border.
     *
     * @param thickness wall depth in pixels; values below 1 are ignored
     */
    protected void addPerimeterWalls(int thickness) {
        if (thickness < 1) {
            return;
        }
        int w = width;
        int h = height;
        addObstacle(0, 0, w, thickness);
        addObstacle(0, h - thickness, w, thickness);
        addObstacle(0, thickness, thickness, h - thickness * 2);
        addObstacle(w - thickness, thickness, thickness, h - thickness * 2);
    }

    public void draw(Graphics2D g, int screenWidth, int screenHeight, int cameraX, int cameraY) {
        // draw out of bounds (black)
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // draw tiles
        gameMap.draw(g, screenWidth, screenHeight, cameraX, cameraY, width, height);

        // Solid obstacles (cover / walls)
        for (Rectangle r : obstacles) {
            int sx = r.x - cameraX;
            int sy = r.y - cameraY;
            g.setColor(new Color(48, 52, 62));
            g.fillRect(sx, sy, r.width, r.height);
            g.setColor(new Color(85, 92, 108));
            g.drawRect(sx, sy, r.width, r.height);
        }
    }
}
