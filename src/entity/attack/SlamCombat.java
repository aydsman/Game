package entity.attack;

import java.util.logging.Level;
import java.util.logging.Logger;

import entity.Entity;
import entity.Player;

/**
 * Starts and resolves {@link TelegraphedSlam} attacks for any entity that has one.
 *
 * <p>The slam is anchored on the player's position at the start of the wind-up. After that the
 * circle does not follow, so a dodge out of it is always a successful read.
 */
public final class SlamCombat {

    private static final Logger LOG = Logger.getLogger(SlamCombat.class.getName());

    /** Extra reach beyond the slam radius used to decide when the attacker commits. */
    private static final int COMMIT_PADDING_PX = 70;

    private SlamCombat() {}

    /**
     * Begins a slam if the attacker is close enough and the slam is ready.
     *
     * @return true if a new slam started this call
     */
    public static boolean tryCommit(Entity attacker, Player player, long nowMs) {
        if (attacker == null || player == null || attacker.isDead() || player.isDead()) {
            return false;
        }
        TelegraphedSlam slam = attacker.getSlam();
        if (slam == null) {
            return false;
        }
        if (distance(attacker, player) > slam.getRadiusPx() + COMMIT_PADDING_PX) {
            return false;
        }
        return slam.tryBegin(nowMs, player.getCenterX(), player.getCenterY());
    }

    /**
     * Applies slam impact damage exactly once per attack if the player is still in the locked circle.
     *
     * @return true if a hit was resolved this call (landed or missed)
     */
    public static boolean resolveImpact(Entity attacker, Player player, long nowMs) {
        if (attacker == null || player == null) {
            return false;
        }
        TelegraphedSlam slam = attacker.getSlam();
        if (slam == null || !slam.consumeImpact(nowMs)) {
            return false;
        }
        if (player.isDead()) {
            return true;
        }
        if (!slam.covers(player.getCenterX(), player.getCenterY())) {
            LOG.log(Level.FINE, "Slam missed");
            return true;
        }
        double amount = attacker.getDamage() * slam.getDamageMultiplier();
        if (!Double.isFinite(amount) || amount <= 0) {
            LOG.warning("Ignoring slam with non-positive damage from " + attacker.getClass().getSimpleName());
            return true;
        }
        player.takeDamage(amount);
        LOG.log(Level.FINE, () -> "Slam hit for " + amount);
        return true;
    }

    private static double distance(Entity attacker, Player player) {
        long dx = (long) player.getCenterX() - attacker.getCenterX();
        long dy = (long) player.getCenterY() - attacker.getCenterY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
