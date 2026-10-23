package combat.powers.runtime;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;

import entity.Boss;
import entity.Enemy;
import entity.EnemyManager;
import entity.Player;

/**
 * The slice of the arena a templated move is allowed to see. Passed in by the game screen so the
 * executor never reaches into UI state.
 */
public final class PowerCombatContext {

    private final Player player;
    private final EnemyManager enemies;
    private final double aimAngle;
    private final List<Rectangle> obstacles;
    private final int arenaWidth;
    private final int arenaHeight;

    public PowerCombatContext(Player player,
                              EnemyManager enemies,
                              double aimAngle,
                              List<Rectangle> obstacles,
                              int arenaWidth,
                              int arenaHeight) {
        if (player == null) {
            throw new IllegalArgumentException("Power combat context requires a player");
        }
        if (enemies == null) {
            throw new IllegalArgumentException("Power combat context requires an enemy manager");
        }
        this.player = player;
        this.enemies = enemies;
        this.aimAngle = aimAngle;
        this.obstacles = obstacles == null ? List.of() : List.copyOf(obstacles);
        this.arenaWidth = Math.max(0, arenaWidth);
        this.arenaHeight = Math.max(0, arenaHeight);
    }

    public Player getPlayer() {
        return player;
    }

    public List<Enemy> getEnemies() {
        List<Enemy> live = enemies.getEnemies();
        return live == null ? Collections.emptyList() : live;
    }

    public Boss getBoss() {
        return enemies.getActiveBoss();
    }

    public double getAimAngle() {
        return aimAngle;
    }

    public List<Rectangle> getObstacles() {
        return obstacles;
    }

    public int getArenaWidth() {
        return arenaWidth;
    }

    public int getArenaHeight() {
        return arenaHeight;
    }
}
