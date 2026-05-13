package entity;

import entity.enemies.Enemy1;
import entity.enemies.Enemy2;
import entity.enemies.Enemy3;
import entity.enemies.Enemy4;
import entity.enemies.Enemy5;
import entity.enemies.Enemy6;
import entity.enemies.Enemy7;
import entity.enemies.Enemy8;
import entity.enemies.Enemy9;
import entity.miniboss.Miniboss1;
import entity.boss.Boss1;
import combat.Projectile;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import world.arena.ArenaCollision;

public class EnemyManager {

    private ArrayList<Enemy> enemies = new ArrayList<>();
    private Boss activeBoss = null;

    public void spawnEnemy(int x, int y, int id) {
        Enemy enemy = switch(id) {
            case 1 -> new Enemy1(x, y);
            case 2 -> new Enemy2(x, y);
            case 3 -> new Enemy3(x, y);
            case 4 -> new Enemy4(x, y);
            case 5 -> new Enemy5(x, y);
            case 6 -> new Enemy6(x, y);
            case 7 -> new Enemy7(x, y);
            case 8 -> new Enemy8(x, y);
            case 9 -> new Enemy9(x, y);
            default -> {
                System.out.println("Error: no enemy exists with id " + id);
                yield null;
            }
        };
        if (enemy != null) enemies.add(enemy);
    }

    public void update(Player player, int arenaWidth, int arenaHeight) {
        update(player, arenaWidth, arenaHeight, null);
    }

    public void update(Player player, int arenaWidth, int arenaHeight, List<Rectangle> obstacles) {
        for (Enemy enemy : enemies) {
            enemy.move(player, arenaWidth, arenaHeight, obstacles);
            if (enemy.ranged) {
                enemy.aimBarrel(player);
                enemy.updateWeapon();
                if (enemy.isPlayerInRange(player)) {
                    boolean losClear = obstacles == null || obstacles.isEmpty()
                            || ArenaCollision.lineOfSightClear(
                                    enemy.getCenterX(), enemy.getCenterY(),
                                    player.getCenterX(), player.getCenterY(), obstacles);
                    if (losClear) {
                        enemy.shoot();
                    }
                }
            } else if (enemy instanceof MeleeEnemy) {
                // Handle melee enemy attacks
                MeleeEnemy meleeEnemy = (MeleeEnemy) enemy;
                int playerCenterX = player.getCenterX();
                int playerCenterY = player.getCenterY();
                int enemyCenterX = enemy.getCenterX();
                int enemyCenterY = enemy.getCenterY();

                double distance = Math.sqrt(Math.pow(playerCenterX - enemyCenterX, 2) +
                                          Math.pow(playerCenterY - enemyCenterY, 2));

                // If in attack range, attempt to attack
                if (distance <= meleeEnemy.preferredDistance + 10) {
                    meleeEnemy.performAttack();
                    if (meleeEnemy.isAttacking()) {
                        // Deal damage to player
                        player.takeDamage(meleeEnemy.damage);
                        player.getStats().addDamageTaken(meleeEnemy.damage);
                        meleeEnemy.setAttacking(false);
                    }
                }
            }
            enemy.updateProjectiles();
            cullEnemyProjectilesAgainstObstacles(enemy, obstacles);
            enemy.checkDeath();
        }
        // Remove dead enemies
        enemies.removeIf(Enemy::isDead);
    }

    public void setDebugMode(boolean debugMode) {
        for (Enemy enemy : enemies) {
            enemy.setDebugMode(debugMode);
        }
    }

    public void checkEnemyProjectileCollisions(Player player) {
        checkEnemyProjectileCollisions(player, null);
    }

    public void checkEnemyProjectileCollisions(Player player, List<Rectangle> obstacles) {
        for (Enemy enemy : enemies) {
            cullEnemyProjectilesAgainstObstacles(enemy, obstacles);
            ArrayList<Projectile> toRemove = new ArrayList<>();
            for (Projectile p : enemy.getProjectiles()) {
                if (player.checkCollision(p.getX(), p.getY())) {
                    player.takeDamage(p.getDamage());
                    player.getStats().addDamageTaken(p.getDamage());
                    toRemove.add(p);
                    break; // Bullet hits player once
                }
            }
            enemy.getProjectiles().removeAll(toRemove);
        }
    }

    public void drawEnemyProjectiles(Graphics2D g, int cameraX, int cameraY) {
        for (Enemy enemy : enemies) {
            for (Projectile p : enemy.getProjectiles()) {
                g.setColor(p.getColor());
                int radius = p.getRadius();
                g.fillOval(p.getX() - cameraX - radius, p.getY() - cameraY - radius, radius * 2, radius * 2);
            }
        }
    }

    public void draw(Graphics2D g, int cameraX, int cameraY) {
        for (Enemy enemy : enemies) {
            enemy.draw(g, cameraX, cameraY);
        }
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }

    public void clear() {
        enemies.clear();
    }

    public void killAllEnemies() {
        for (Enemy enemy : enemies) {
            enemy.hp = 0;
            enemy.checkDeath();
        }
        enemies.removeIf(Enemy::isDead);
        if (activeBoss != null) {
            activeBoss.hp = 0;
            activeBoss.checkDeath();
            activeBoss = null;
        }
    }

    public void spawnMiniboss(int x, int y, int id) {
        Boss miniboss = switch(id) {
            case 1 -> new Miniboss1(x, y);
            default -> {
                System.out.println("Error: no miniboss exists with id " + id);
                yield null;
            }
        };
        if (miniboss != null) activeBoss = miniboss;
    }

    public void spawnBoss(int x, int y, int id) {
        Boss boss = switch(id) {
            case 1 -> new Boss1(x, y);
            default -> {
                System.out.println("Error: no boss exists with id " + id);
                yield null;
            }
        };
        if (boss != null) activeBoss = boss;
    }

    public Boss getActiveBoss() {
        return activeBoss;
    }

    public void updateBoss(Player player, int arenaWidth, int arenaHeight) {
        updateBoss(player, arenaWidth, arenaHeight, null);
    }

    public void updateBoss(Player player, int arenaWidth, int arenaHeight, List<Rectangle> obstacles) {
        if (activeBoss != null) {
            activeBoss.move(player, arenaWidth, arenaHeight, obstacles);
            activeBoss.checkDeath();
            if (activeBoss.isDead()) {
                activeBoss = null;
            }
        }
    }

    private void cullEnemyProjectilesAgainstObstacles(Enemy enemy, List<Rectangle> obstacles) {
        if (obstacles == null || obstacles.isEmpty()) return;
        enemy.getProjectiles().removeIf(p ->
                ArenaCollision.projectileHitsObstacle(p.getX(), p.getY(), p.getRadius(), obstacles));
    }

    public void drawBoss(Graphics2D g, int cameraX, int cameraY) {
        if (activeBoss != null) {
            activeBoss.draw(g, cameraX, cameraY);
        }
    }

    public void applyDifficultyScaling(double healthMultiplier, double speedMultiplier, double damageMultiplier, int difficultyLevel) {
        for (Enemy enemy : enemies) {
            // Apply multipliers to enemy stats
            enemy.hp = (int) (enemy.hp * healthMultiplier);
            enemy.maxHp = (int) (enemy.maxHp * healthMultiplier);
            enemy.speed *= speedMultiplier;
            enemy.damage *= damageMultiplier;

            // Apply difficulty to melee enemies' attack cooldowns
            if (enemy instanceof MeleeEnemy) {
                ((MeleeEnemy) enemy).setDifficultyCooldowns(difficultyLevel);
            }
        }
    }

    public void applyArenaScaling(world.arena.arenas.StandardArena arena) {
        applyDifficultyScaling(arena.getEnemyHealthMultiplier(),
                             arena.getEnemySpeedMultiplier(),
                             arena.getEnemyDamageMultiplier(),
                             arena.getDifficultyLevel());
    }
}
