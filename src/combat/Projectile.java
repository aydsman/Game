package combat;

import java.awt.Color;

public class Projectile {
    private int x;
    private int y;
    private Color color;
    private double speed;
    private double angle;
    private int damage;
    private int radius;

    public Projectile(int x, int y, Color color, double speed) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.speed = speed;
        this.angle = 0;
        this.damage = 10;
        this.radius = 6;
    }

    public Projectile(int x, int y, Color color, double speed, double angle, int damage) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.speed = speed;
        this.angle = angle;
        this.damage = damage;
        this.radius = 6;
    }

    public void update() {
        x += speed * Math.cos(angle);
        y += speed * Math.sin(angle);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAngle() {
        return angle;
    }

    public int getDamage() {
        return damage;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public int getRadius() {
        return radius;
    }
}
