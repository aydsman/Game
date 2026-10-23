package combat.powers.runtime;

import java.util.logging.Logger;

import combat.powers.Move;
import combat.powers.MoveRole;
import combat.powers.MoveTemplate;
import entity.Boss;
import entity.Enemy;
import entity.Entity;
import entity.Player;
import world.arena.ArenaCollision;

/**
 * Executes a {@link MoveTemplate} against the live arena.
 *
 * <p>Powers that only register a template (Magma, Lightning) become real combat moves here instead
 * of falling through to a generic visual. Name-specific kits (Fire, Earth) keep their own handlers
 * and should not be routed through this class.
 */
public final class TemplatedMoveExecutor {

    private static final Logger LOG = Logger.getLogger(TemplatedMoveExecutor.class.getName());
    private static final long CONTROL_STUN_MS = 700;

    public void execute(Move move, PowerCombatContext context) {
        if (move == null || context == null) {
            return;
        }
        MoveTemplate template = move.getTemplate();
        if (template == null) {
            return;
        }
        Player player = context.getPlayer();
        if (player == null || player.isDead()) {
            return;
        }

        String id = template.getTemplateId();
        if (id == null || id.isBlank()) {
            LOG.warning("Move '" + move.getName() + "' has a template with no id");
            return;
        }
        switch (id) {
            case "projectile_bomb" -> strikeAlongAim(template, context);
            case "dash" -> dashTowardAim(template, context);
            case "slow_field" -> applyField(template, context, true);
            case "heavy_finisher" -> strikeAlongAim(template, context);
            default -> LOG.warning("Unknown move template '" + id + "' on move '" + move.getName() + "'");
        }
    }

    private void strikeAlongAim(MoveTemplate template, PowerCombatContext context) {
        Player player = context.getPlayer();
        double angle = context.getAimAngle();
        int range = Math.max(0, template.getRange());
        int impactX = player.getCenterX() + (int) Math.round(Math.cos(angle) * range);
        int impactY = player.getCenterY() + (int) Math.round(Math.sin(angle) * range);
        applyRadius(template, context, impactX, impactY, template.getRole() == MoveRole.CONTROL);
    }

    private void applyField(MoveTemplate template, PowerCombatContext context, boolean fromPlayer) {
        Player player = context.getPlayer();
        applyRadius(template, context, player.getCenterX(), player.getCenterY(), fromPlayer);
    }

    private void applyRadius(MoveTemplate template,
                             PowerCombatContext context,
                             int centerX,
                             int centerY,
                             boolean applyControl) {
        int radius = Math.max(0, template.getRadius());
        int damage = Math.max(0, template.getBasePower());
        long radiusSq = (long) radius * radius;
        boolean control = applyControl || template.getRole() == MoveRole.CONTROL;
        long slowMs = Math.max(0, template.getDurationMs());

        for (Enemy enemy : context.getEnemies()) {
            affectIfInside(context.getPlayer(), enemy, centerX, centerY, radiusSq, damage, control, slowMs);
        }
        Boss boss = context.getBoss();
        affectIfInside(context.getPlayer(), boss, centerX, centerY, radiusSq, damage, control, slowMs);
    }

    private void affectIfInside(Player player,
                                Entity target,
                                int centerX,
                                int centerY,
                                long radiusSq,
                                int damage,
                                boolean control,
                                long slowMs) {
        if (target == null || target.isDead()) {
            return;
        }
        long dx = (long) target.getCenterX() - centerX;
        long dy = (long) target.getCenterY() - centerY;
        if (dx * dx + dy * dy > radiusSq) {
            return;
        }
        if (damage > 0) {
            player.dealDamage(target, damage, false, 0);
        }
        if (control) {
            if (slowMs > 0) {
                target.applySlow(slowMs, 0.7);
            }
            target.applyStun(CONTROL_STUN_MS);
        }
    }

    private void dashTowardAim(MoveTemplate template, PowerCombatContext context) {
        Player player = context.getPlayer();
        int range = Math.max(0, template.getRange());
        if (range == 0) {
            return;
        }
        double angle = context.getAimAngle();
        int targetX = player.getX() + (int) Math.round(Math.cos(angle) * range);
        int targetY = player.getY() + (int) Math.round(Math.sin(angle) * range);
        int[] resolved = ArenaCollision.resolveMovement(
                player.getX(), player.getY(), targetX, targetY,
                player.getW(), player.getL(),
                context.getObstacles(), context.getArenaWidth(), context.getArenaHeight());
        player.setX(resolved[0]);
        player.setY(resolved[1]);
        player.getHurtInvulnerability().grant(System.currentTimeMillis(), 180);
    }
}
