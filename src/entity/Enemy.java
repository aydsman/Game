package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import combat.Projectile;

public class Enemy extends Entity {

    private ArrayList<Projectile> projectiles = new ArrayList<>();
    private long lastShotTime = 0;
    protected int detectionRadius = 300; // pixels
    private boolean debugMode = false;
    protected int xpValue = 25; // XP given when killed

    public Enemy(int x, int y) {
        super(x, y);
        // health
        hp = 10;
        maxHp = 10;
        // multipliers
        damage = 1.0;
        speed = 4.0;
        // combat
        ranged = true;
        // visuals
        color = Color.WHITE;
        // XP value
        xpValue = 25; // XP given when killed
    }

    public void move(Player player, int arenaWidth, int arenaHeight) {
        int playerCenterX = player.getCenterX();
        int playerCenterY = player.getCenterY();
        int enemyCenterX = getCenterX();
        int enemyCenterY = getCenterY();

        // calculate direction to player
        int dx = playerCenterX - enemyCenterX;
        int dy = playerCenterY - enemyCenterY;

        // normalize and move toward player
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance > 0) {
            x += (int) ((dx / distance) * speed);
            y += (int) ((dy / distance) * speed);
        }

        // clamp to arena bounds
        x = Math.max(0, Math.min(x, arenaWidth - w));
        y = Math.max(0, Math.min(y, arenaHeight - l));
    }

    public void aimBarrel(Player player) {
        int playerCenterX = player.getCenterX();
        int playerCenterY = player.getCenterY();
        int centerX = getCenterX();
        int centerY = getCenterY();
        barrelAngle = Math.atan2(playerCenterY - centerY, playerCenterX - centerX);
    }

    public boolean isPlayerInRange(Player player) {
        int playerCenterX = player.getCenterX();
        int playerCenterY = player.getCenterY();
        int centerX = getCenterX();
        int centerY = getCenterY();

        double distance = Math.sqrt(Math.pow(playerCenterX - centerX, 2) + Math.pow(playerCenterY - centerY, 2));
        return distance <= detectionRadius;
    }

    public int getDetectionRadius() {
        return detectionRadius;
    }

    public void setDetectionRadius(int radius) {
        this.detectionRadius = radius;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public void draw(Graphics2D g, int cameraX, int cameraY) {
        super.draw(g, cameraX, cameraY);
        // Draw detection circle outline only in debug mode
        if (debugMode) {
            g.setColor(Color.RED);
            java.awt.Stroke oldStroke = g.getStroke();
            g.setStroke(new java.awt.BasicStroke(3)); // Thicker line
            g.drawOval(getCenterX() - cameraX - detectionRadius, getCenterY() - cameraY - detectionRadius,
                        detectionRadius * 2, detectionRadius * 2);
            g.setStroke(oldStroke); // Restore original stroke
        }
    }

    public void shoot() {
        if (heldWeapon != null && !heldWeapon.isReloading()) {
            long currentTime = System.currentTimeMillis();
            long fireRateMs = (long) (heldWeapon.getFireRate() * 1000);
            if (currentTime - lastShotTime >= fireRateMs) {
                Projectile bullet = heldWeapon.shoot(getCenterX(), getCenterY(), barrelAngle);
                if (bullet != null) {
                    projectiles.add(bullet);
                    lastShotTime = currentTime;
                }
            }
        }
    }

    public void updateProjectiles() {
        for (Projectile p : projectiles) {
            p.update();
        }
        // Remove projectiles that go off screen
        projectiles.removeIf(p -> p.getX() < 0 || p.getX() > 2000 || p.getY() < 0 || p.getY() > 2000);
    }

    public void updateWeapon() {
        if (heldWeapon != null) {
            heldWeapon.updateReload();
            // Auto-reload when ammo is 0
            if (heldWeapon.getCurrentAmmo() == 0 && !heldWeapon.isReloading()) {
                heldWeapon.reload();
            }
        }
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }
}
