package entity;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.Random;

public class MeleeEnemy extends Enemy {

    protected Random random = new Random();
    protected long lastAttackTime = 0;
    protected double minAttackCooldown; // seconds
    protected double maxAttackCooldown; // seconds
    protected double currentAttackCooldown; // seconds
    protected double preferredDistance; // pixels from player to maintain
    protected boolean isAttacking = false;

    public MeleeEnemy(int x, int y) {
        super(x, y);
        ranged = false; // Melee enemies don't use ranged combat
        // Melee enemies have more health but are easier to hit
        hp = 120;
        maxHp = 120;
        // Base cooldowns (will be adjusted by difficulty)
        minAttackCooldown = 1.0;
        maxAttackCooldown = 1.5;
        preferredDistance = 40.0; // Stay about 40 pixels from player
    }

    @Override
    public void move(Player player, int arenaWidth, int arenaHeight) {
        move(player, arenaWidth, arenaHeight, null);
    }

    @Override
    public void move(Player player, int arenaWidth, int arenaHeight, List<Rectangle> obstacles) {
        int playerCenterX = player.getCenterX();
        int playerCenterY = player.getCenterY();
        int enemyCenterX = getCenterX();
        int enemyCenterY = getCenterY();
        int dx = playerCenterX - enemyCenterX;
        int dy = playerCenterY - enemyCenterY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        int step = Math.max(1, (int) Math.round(speed));

        if (obstacles == null || obstacles.isEmpty()) {
            int ox = x;
            int oy = y;
            double moveSpeed = speed;
            if (distance > preferredDistance + 20) {
                if (distance > 0) {
                    x += (int) ((dx / distance) * moveSpeed);
                    y += (int) ((dy / distance) * moveSpeed);
                }
            } else if (distance < preferredDistance - 10) {
                if (distance > 0) {
                    x -= (int) ((dx / distance) * moveSpeed * 0.5);
                    y -= (int) ((dy / distance) * moveSpeed * 0.5);
                }
            } else {
                x += random.nextInt(5) - 2;
                y += random.nextInt(5) - 2;
            }
            x = Math.max(0, Math.min(x, arenaWidth - w));
            y = Math.max(0, Math.min(y, arenaHeight - l));
            return;
        }

        if (distance > preferredDistance + 70) {
            world.arena.ArenaPathfinding.applyChaseStep(this, player, obstacles, arenaWidth, arenaHeight, step);
            return;
        }

        int ox = x;
        int oy = y;
        double moveSpeed = speed;
        if (distance > preferredDistance + 20) {
            if (distance > 0) {
                x += (int) ((dx / distance) * moveSpeed);
                y += (int) ((dy / distance) * moveSpeed);
            }
        } else if (distance < preferredDistance - 10) {
            if (distance > 0) {
                x -= (int) ((dx / distance) * moveSpeed * 0.5);
                y -= (int) ((dy / distance) * moveSpeed * 0.5);
            }
        } else {
            x += random.nextInt(5) - 2;
            y += random.nextInt(5) - 2;
        }
        int[] r = world.arena.ArenaCollision.resolveMovement(ox, oy, x, y, w, l, obstacles, arenaWidth, arenaHeight);
        x = r[0];
        y = r[1];
    }

    public boolean canAttack() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastAttackTime) >= (currentAttackCooldown * 1000);
    }

    public void performAttack() {
        if (canAttack()) {
            isAttacking = true;
            lastAttackTime = System.currentTimeMillis();
            // Set next cooldown randomly
            currentAttackCooldown = minAttackCooldown +
                (random.nextDouble() * (maxAttackCooldown - minAttackCooldown));
        }
    }

    public void setDifficultyCooldowns(int difficultyLevel) {
        // Higher difficulty = faster attacks
        double cooldownReduction = (difficultyLevel - 1) * 0.2; // 0.2s reduction per level
        minAttackCooldown = Math.max(0.5, 1.0 - cooldownReduction);
        maxAttackCooldown = Math.max(0.8, 1.5 - cooldownReduction);
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public void setAttacking(boolean attacking) {
        isAttacking = attacking;
    }

    // Override shoot method to do nothing for melee enemies
    @Override
    public void shoot() {
        // Melee enemies don't shoot
    }
}
