package combat.summons;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import combat.DamageRoll;
import entity.Boss;
import entity.Enemy;
import entity.EnemyManager;
import entity.Entity;
import entity.Player;

/**
 * Turns the equipped summon into a periodic combat actor.
 *
 * <p>The summon is not a world entity: it sits on the player and ticks from the game loop. Each tick
 * picks the nearest living target in range and either deals damage or applies the summon's stun.
 */
public final class SummonRuntime {

    private static final Logger LOG = Logger.getLogger(SummonRuntime.class.getName());

    private long lastTickMs = Long.MIN_VALUE;

    /**
     * Advances the equipped summon by one frame.
     *
     * @param nowMs current time; injected so the interval is testable
     * @return true if the summon acted this call
     */
    public boolean update(Player player, EnemyManager enemies, long nowMs) {
        if (player == null || enemies == null || player.isDead()) {
            return false;
        }
        Summon summon = player.getInventory() == null ? null : player.getInventory().getSummon();
        if (summon == null) {
            return false;
        }
        long interval = summon.getTickIntervalMs();
        if (interval <= 0) {
            LOG.warning("Summon '" + summon.getName() + "' has a non-positive tick interval; skipping");
            return false;
        }
        if (lastTickMs != Long.MIN_VALUE && nowMs - lastTickMs < interval) {
            return false;
        }

        Entity target = nearestTarget(player, enemies, summon.getAttackRangePx());
        if (target == null) {
            return false;
        }

        lastTickMs = nowMs;
        int damage = summon.getTickDamage();
        if (damage > 0) {
            player.dealDamage(target, DamageRoll.flat(damage), false, 0, 0L);
        }
        long stunMs = summon.getOnHitStunMs();
        if (stunMs > 0) {
            target.applyStun(stunMs);
        }
        LOG.log(Level.FINE, () -> "Summon '" + summon.getName() + "' acted on " + target.getClass().getSimpleName());
        return true;
    }

    public void reset() {
        lastTickMs = Long.MIN_VALUE;
    }

    static Entity nearestTarget(Player player, EnemyManager enemies, int rangePx) {
        if (rangePx <= 0) {
            return null;
        }
        Entity closest = null;
        long best = (long) rangePx * rangePx;
        List<Enemy> living = enemies.getEnemies();
        if (living != null) {
            for (Enemy enemy : living) {
                if (enemy == null || enemy.isDead()) {
                    continue;
                }
                long distSq = distanceSq(player, enemy);
                if (distSq <= best) {
                    best = distSq;
                    closest = enemy;
                }
            }
        }
        Boss boss = enemies.getActiveBoss();
        if (boss != null && !boss.isDead()) {
            long distSq = distanceSq(player, boss);
            if (distSq <= best) {
                closest = boss;
            }
        }
        return closest;
    }

    private static long distanceSq(Entity a, Entity b) {
        long dx = (long) a.getCenterX() - b.getCenterX();
        long dy = (long) a.getCenterY() - b.getCenterY();
        return dx * dx + dy * dy;
    }
}
