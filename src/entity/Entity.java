package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import combat.Ranged;

public class Entity {
    protected int x, y; // position
    protected int w = 50, l = 50; // size (default 50x50, override in subclass if needed)
    protected double hp = 100, maxHp = 100; // health
    protected double damage = 1.0; // damage multiplier (default damage 1)
    protected double speed = 1.0; // speed multiplier (default speed 1)
    public boolean ranged = false; // whether this entity uses ranged attacks
    protected double barrelAngle = 0; // angle the barrel is pointing (in radians)
    protected Color color = Color.WHITE; // default color
    protected Ranged heldWeapon; // weapon the entity is holding (null if nothing)
    protected boolean dead = false; // death status
    protected int level = 1; // entity level
    protected double currentXP = 0; // current experience points
    protected double maxXP = 100; // XP needed for next level

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public int getW() { return w; }
    public int getL() { return l; }
    public double getSpeed() { return speed; }
    public double getHp() { return hp; }
    public double getMaxHp() { return maxHp; }
    public int getCenterX() { return x + w / 2; }
    public int getCenterY() { return y + l / 2; }
    public boolean isDead() { return dead; }
    public int getLevel() { return level; }
    public double getCurrentXP() { return currentXP; }
    public double getMaxXP() { return maxXP; }

    public boolean checkCollision(int px, int py) {
        return px >= x && px <= x + w && py >= y && py <= y + l;
    }

    public void checkDeath() {
        if (hp <= 0) {
            hp = 0; // Clamp health to 0
            dead = true;
        }
    }

    public void gainXP(double amount) {
        currentXP += amount;
        if (currentXP >= maxXP) {
            levelUp();
        }
    }

    public void levelUp() {
        level++;
        currentXP = currentXP - maxXP;
        maxXP = maxXP * 1.5; // Increase XP requirement by 50% each level
    }

    // Weapon holding methods
    public void setWeapon(Ranged weapon) {
        this.heldWeapon = weapon;
        this.ranged = (weapon != null);
    }
    
    public Ranged getHeldWeapon() {
        return heldWeapon;
    }
    
    public void selectWeapon(String weaponType, int weaponId) {
        Ranged weapon = switch(weaponType.toLowerCase()) {
            case "pistol" -> switch(weaponId) {
                case 1 -> new combat.ranged.pistols.Pistol1();
                default -> new combat.ranged.pistols.Pistol();
            };
            case "rifle" -> switch(weaponId) {
                case 1 -> new combat.ranged.rifles.Rifle1();
                default -> new combat.ranged.rifles.Rifle();
            };
            case "shotgun" -> switch(weaponId) {
                case 1 -> new combat.ranged.shotguns.Shotgun1();
                default -> new combat.ranged.shotguns.Shotgun();
            };
            case "sniper" -> switch(weaponId) {
                case 1 -> new combat.ranged.snipers.Sniper1();
                default -> new combat.ranged.snipers.Sniper();
            };
            default -> null;
        };
        setWeapon(weapon);
    }

    public void draw(Graphics2D g, int cameraX, int cameraY) {
        g.setColor(color);
        g.fillRect(x - cameraX, y - cameraY, w, l);
        if (ranged) {
            drawBarrel(g, cameraX, cameraY);
        }
        displayStats(g, cameraX, cameraY);
    }

    private void drawBarrel(Graphics2D g, int cameraX, int cameraY) {
        if (heldWeapon == null) {
            return; // no weapon, don't draw barrel
        }
        
        // Use weapon properties for barrel appearance
        int barrelLength = heldWeapon.getBarrelLength();
        int barrelHeight = heldWeapon.getBarrelHeight();
        int centerX = getCenterX() - cameraX;
        int centerY = getCenterY() - cameraY;

        // save original transform
        java.awt.geom.AffineTransform old = g.getTransform();

        // rotate around center
        g.rotate(barrelAngle, centerX, centerY);

        // draw barrel with weapon-specific color
        switch (heldWeapon.getBarrelColor().toLowerCase()) {
            case "red":
                g.setColor(Color.RED);
                break;
            case "blue":
                g.setColor(Color.BLUE);
                break;
            case "darkgreen":
                g.setColor(new Color(0, 100, 0));
                break;
            case "brown":
                g.setColor(new Color(139, 69, 19));
                break;
            case "black":
            default:
                g.setColor(Color.DARK_GRAY);
                break;
        }
        
        // Draw barrel: length extends right from center dot, height centered on dot
        g.fillRect(centerX, centerY - barrelHeight / 2, barrelLength, barrelHeight);

        // restore transform
        g.setTransform(old);
    }

    public void displayStats(Graphics2D g, int cameraX, int cameraY) {
        int barWidth = w;
        int barHeight = 5;
        int barX = x - cameraX - 1;
        int barY = y - cameraY - 10;

        // background (red)
        g.setColor(Color.RED);
        g.fillRect(barX, barY, barWidth, barHeight);

        // foreground (green) based on current hp
        g.setColor(Color.GREEN);
        int currentBarWidth = (int) ((hp / maxHp) * barWidth);
        g.fillRect(barX, barY, currentBarWidth, barHeight);

        // border (black)
        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);
    }
}
