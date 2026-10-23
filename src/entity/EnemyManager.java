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
import entity.attack.SlamCombat;
import world.arena.ArenaCollision;

public class EnemyManager {

    private ArrayList<Enemy> enemies = new ArrayList<>();
    private Boss activeBoss = null;
    private double healthMultiplier = 1.0;
    private double speedMultiplier = 1.0;
    private double damageMultiplier = 1.0;
    private int difficultyLevel = 1;

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
        if (enemy != null) {
            applyDifficultyScaling(enemy);
            enemies.add(enemy);
        }
    }

    public void update(Player player, int arenaWidth, int arenaHeight) {
        update(player, arenaWidth, arenaHeight, null);
    }

    public void update(Player player, int arenaWidth, int arenaHeight, List<Rectangle> obstacles) {
        long now = System.currentTimeMillis();
        for (Enemy enemy : enemies) {
            boolean stunned = enemy.isStunned();
            boolean slamBusy = enemy.getSlam() != null && enemy.getSlam().isBusy(now);
            if (!stunned && !slamBusy) {
                enemy.move(player, arenaWidth, arenaHeight, obstacles);
            }
            if (!stunned && enemy.getSlam() != null) {
                SlamCombat.tryCommit(enemy, player, now);
            }
            SlamCombat.resolveImpact(enemy, player, now);
            if (enemy.ranged) {
                enemy.updateWeapon();
                if (!stunned && !slamBusy) {
                    enemy.aimBarrel(player);
                    if (enemy.isPlayerInRange(player)) {
                        boolean losClear = obstacles == null || obstacles.isEmpty()
                                || ArenaCollision.lineOfSightClear(
                                        enemy.getCenterX(), enemy.getCenterY(),
                                        player.getCenterX(), player.getCenterY(), obstacles);
                        if (losClear) {
                            enemy.shoot();
                        }
                    }
                }
            } else if (enemy instanceof MeleeEnemy && enemy.getSlam() == null) {
                MeleeEnemy meleeEnemy = (MeleeEnemy) enemy;
                if (!stunned) {
                    int playerCenterX = player.getCenterX();
                    int playerCenterY = player.getCenterY();
                    int enemyCenterX = enemy.getCenterX();
                    int enemyCenterY = enemy.getCenterY();

                    double distance = Math.sqrt(Math.pow(playerCenterX - enemyCenterX, 2) +
                                              Math.pow(playerCenterY - enemyCenterY, 2));

                    if (distance <= meleeEnemy.preferredDistance + 10) {
                        meleeEnemy.performAttack();
                        if (meleeEnemy.isAttacking()) {
                            player.takeDamage(meleeEnemy.damage);
                            meleeEnemy.setAttacking(false);
                        }
                    }
                }
            }
            enemy.updateProjectiles();
            cullEnemyProjectilesAgainstObstacles(enemy, obstacles);
            enemy.checkDeath();
        }
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
        if (miniboss != null) {
            applyDifficultyScaling(miniboss);
            activeBoss = miniboss;
        }
    }

    public void spawnBoss(int x, int y, int id) {
        Boss boss = switch(id) {
            case 1 -> new Boss1(x, y);
            default -> {
                System.out.println("Error: no boss exists with id " + id);
                yield null;
            }
        };
        if (boss != null) {
            applyDifficultyScaling(boss);
            activeBoss = boss;
        }
    }

    public Boss getActiveBoss() {
        return activeBoss;
    }

    public void updateBoss(Player player, int arenaWidth, int arenaHeight) {
        updateBoss(player, arenaWidth, arenaHeight, null);
    }

    public void updateBoss(Player player, int arenaWidth, int arenaHeight, List<Rectangle> obstacles) {
        if (activeBoss != null) {
            long now = System.currentTimeMillis();
            boolean slamBusy = activeBoss.getSlam() != null && activeBoss.getSlam().isBusy(now);
            if (!activeBoss.isStunned() && !slamBusy) {
                activeBoss.move(player, arenaWidth, arenaHeight, obstacles);
            }
            if (!activeBoss.isStunned() && activeBoss.getSlam() != null) {
                SlamCombat.tryCommit(activeBoss, player, now);
            }
            SlamCombat.resolveImpact(activeBoss, player, now);
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

    /**
     * Stores the arena's difficulty multipliers. Scaling is applied when each enemy is spawned
     * (see {@link #applyDifficultyScaling(Entity)}) rather than in a pass over the live list,
     * which would miss every enemy that has not spawned yet.
     */
    public void setDifficultyScaling(double healthMultiplier, double speedMultiplier,
                                     double damageMultiplier, int difficultyLevel) {
        this.healthMultiplier = Math.max(0.1, healthMultiplier);
        this.speedMultiplier = Math.max(0.1, speedMultiplier);
        this.damageMultiplier = Math.max(0.1, damageMultiplier);
        this.difficultyLevel = Math.max(1, difficultyLevel);
    }

    /** Applies the current arena multipliers to one freshly constructed enemy, miniboss or boss. */
    private void applyDifficultyScaling(Entity entity) {
        entity.maxHp = Math.round(entity.maxHp * healthMultiplier);
        entity.hp = entity.maxHp;
        entity.speed *= speedMultiplier;
        entity.damage *= damageMultiplier;
        if (entity instanceof MeleeEnemy) {
            ((MeleeEnemy) entity).setDifficultyCooldowns(difficultyLevel);
        }
    }
}
