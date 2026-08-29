package world.arena;

import entity.Entity;
import entity.Player;
import java.awt.Rectangle;
import java.util.List;

public final class ArenaPathfinding {
    private ArenaPathfinding() {}

    public static class PathAgentState {
        public int stuckTicks = 0;
    }

    public static void applyChaseStep(Entity entity, Player player, List<Rectangle> obstacles,
                                      int arenaWidth, int arenaHeight, int step) {
        int tx = player.getCenterX() - entity.getW() / 2;
        int ty = player.getCenterY() - entity.getL() / 2;
        int dx = Integer.compare(tx, entity.getX());
        int dy = Integer.compare(ty, entity.getY());
        int newX = entity.getX() + dx * step;
        int newY = entity.getY() + dy * step;
        int[] resolved = ArenaCollision.resolveMovement(
                entity.getX(), entity.getY(), newX, newY,
                entity.getW(), entity.getL(), obstacles, arenaWidth, arenaHeight);
        entity.setX(resolved[0]);
        entity.setY(resolved[1]);
    }
}
