package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import combat.Projectile;
import entity.ai.EnemyRole;
import entity.attack.TelegraphedSlamRenderer;

public class Enemy extends Entity {

    private ArrayList<Projectile> projectiles = new ArrayList<>();
    private long lastShotTime = 0;
    protected int detectionRadius = 300; // pixels
    private boolean debugMode = false;
    protected EnemyRole role = EnemyRole.STRIKER;
    /** Distance a kiter tries to hold. */
    protected int kiteDistancePx = 240;
    /** Distance a closer stops advancing so their short-range weapon can fire. */
    protected int closerDistancePx = 110;

    public Enemy(int x, int y) {
        super(x, y);
        // health
        hp = 100;
        maxHp = 100;
        // multipliers
        damage = 10.0;
        speed = 2.0;
        // combat
        ranged = true;
        // visuals
        color = Color.WHITE;
        // XP value
        xpValue = 25; // XP given when killed
        // Gold value
        goldValue = 10; // Gold given when killed
    }

    public void move(Player player, int arenaWidth, int arenaHeight) {
        move(player, arenaWidth, arenaHeight, null);
    }

    public void move(Player player, int arenaWidth, int arenaHeight, java.util.List<java.awt.Rectangle> obstacles) {
        if (slam != null && slam.isBusy(System.currentTimeMillis())) {
            return;
        }
        int step = Math.max(1, (int) Math.round(getEffectiveSpeed()));
        if (role == EnemyRole.KITER) {
            moveKite(player, arenaWidth, arenaHeight, obstacles, step);
            return;
        }
        if (role == EnemyRole.CLOSER) {
            moveCloser(player, arenaWidth, arenaHeight, obstacles, step);
            return;
        }
        world.arena.ArenaPathfinding.applyChaseStep(this, player, obstacles, arenaWidth, arenaHeight, step);
    }

    public EnemyRole getRole() {
        return role;
    }

    private void moveKite(Player player, int arenaWidth, int arenaHeight, List<Rectangle> obstacles, int step) {
        int dx = player.getCenterX() - getCenterX();
        int dy = player.getCenterY() - getCenterY();
        double distance = Math.sqrt((long) dx * dx + (long) dy * dy);
        int band = 36;
        if (distance > kiteDistancePx + band) {
            world.arena.ArenaPathfinding.applyChaseStep(this, player, obstacles, arenaWidth, arenaHeight, step);
            return;
        }
        int oldX = x;
        int oldY = y;
        if (distance < 1) {
            return;
        }
        if (distance < kiteDistancePx - band) {
            x -= (int) Math.round((dx / distance) * step);
            y -= (int) Math.round((dy / distance) * step);
        } else {
            // Strafe so the kiter is not a stationary turret.
            x += (int) Math.round((-dy / distance) * step);
            y += (int) Math.round((dx / distance) * step);
        }
        int[] resolved = world.arena.ArenaCollision.resolveMovement(
                oldX, oldY, x, y, w, l, obstacles, arenaWidth, arenaHeight);
        x = resolved[0];
        y = resolved[1];
    }

    private void moveCloser(Player player, int arenaWidth, int arenaHeight, List<Rectangle> obstacles, int step) {
        int dx = player.getCenterX() - getCenterX();
        int dy = player.getCenterY() - getCenterY();
        double distance = Math.sqrt((long) dx * dx + (long) dy * dy);
        if (distance > closerDistancePx) {
            world.arena.ArenaPathfinding.applyChaseStep(this, player, obstacles, arenaWidth, arenaHeight, step);
        }
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
        TelegraphedSlamRenderer.draw(g, slam, System.currentTimeMillis(), cameraX, cameraY);
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
        if (heldWeapon instanceof combat.Ranged) {
            combat.Ranged ranged = (combat.Ranged) heldWeapon;
            if (!ranged.isReloading()) {
                long currentTime = System.currentTimeMillis();
                long fireRateMs = (long) (ranged.getFireRate() * 1000);
                if (currentTime - lastShotTime >= fireRateMs) {
                    List<Projectile> bullets = ranged.shoot(getCenterX(), getCenterY(), barrelAngle);
                    if (bullets != null) {
                        projectiles.addAll(bullets);
                        lastShotTime = currentTime;
                    }
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
        if (heldWeapon instanceof combat.Ranged) {
            combat.Ranged ranged = (combat.Ranged) heldWeapon;
            ranged.updateReload();
            // Auto-reload when ammo is 0
            if (ranged.getCurrentAmmo() == 0 && !ranged.isReloading()) {
                ranged.reload();
            }
        }
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }
}
