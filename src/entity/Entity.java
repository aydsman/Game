package entity;

import java.awt.Color;
import java.awt.Graphics2D;

public class Entity {
    protected int x, y; // position
    protected int w = 30, l = 30; // size (default 30x30, override in subclass if needed)
    protected double hp = 100, maxHp = 100; // health
    protected double damage = 1.0; // damage multiplier (default damage 1)
    protected double speed = 1.0; // speed multiplier (default speed 1)
    protected Color color = Color.WHITE; // default color

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getW() { return w; }
    public int getL() { return l; }

    public void draw(Graphics2D g, int cameraX, int cameraY) {
        g.setColor(color);
        g.fillRect(x - cameraX, y - cameraY, w, l);
    }
}
