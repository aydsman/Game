package world.arena;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Axis-aligned obstacle collision for arena mode (player, enemies, projectiles).
 */
public final class ArenaCollision {

    private ArenaCollision() {}

    public static boolean entityIntersects(int px, int py, int pw, int ph, Rectangle o) {
        return o.intersects(new Rectangle(px, py, pw, ph));
    }

    public static boolean entityIntersectsAny(int px, int py, int pw, int ph, List<Rectangle> obstacles) {
        if (obstacles == null || obstacles.isEmpty()) return false;
        Rectangle b = new Rectangle(px, py, pw, ph);
        for (Rectangle o : obstacles) {
            if (o.intersects(b)) return true;
        }
        return false;
    }

    /**
     * Separate-axis resolution: try full X move from old Y, then Y at resolved X.
     */
    public static int[] resolveMovement(int ox, int oy, int nx, int ny, int pw, int ph,
                                        List<Rectangle> obstacles, int arenaW, int arenaH) {
        if (obstacles == null || obstacles.isEmpty()) {
            nx = clamp(nx, 0, arenaW - pw);
            ny = clamp(ny, 0, arenaH - ph);
            return new int[]{nx, ny};
        }
        int tx = nx;
        if (entityIntersectsAny(tx, oy, pw, ph, obstacles)) {
            tx = ox;
        }
        int ty = ny;
        if (entityIntersectsAny(tx, ty, pw, ph, obstacles)) {
            ty = oy;
        }
        tx = clamp(tx, 0, arenaW - pw);
        ty = clamp(ty, 0, arenaH - ph);
        return new int[]{tx, ty};
    }

    public static boolean projectileHitsObstacle(int px, int py, int radius, List<Rectangle> obstacles) {
        if (obstacles == null || obstacles.isEmpty()) return false;
        int r = Math.max(1, radius);
        for (Rectangle o : obstacles) {
            if (circleIntersectsRect(px, py, r, o)) return true;
        }
        return false;
    }

    private static boolean circleIntersectsRect(int cx, int cy, int rad, Rectangle rect) {
        int closestX = clamp(cx, rect.x, rect.x + rect.width);
        int closestY = clamp(cy, rect.y, rect.y + rect.height);
        int dx = cx - closestX;
        int dy = cy - closestY;
        return dx * dx + dy * dy <= rad * rad;
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(v, hi));
    }

    /**
     * True if a bullet (or vision) could travel from (x1,y1) to (x2,y2) without passing through an obstacle interior.
     */
    public static boolean lineOfSightClear(int x1, int y1, int x2, int y2, List<Rectangle> obstacles) {
        if (obstacles == null || obstacles.isEmpty()) return true;
        Line2D.Double line = new Line2D.Double(x1, y1, x2, y2);
        for (Rectangle r : obstacles) {
            Rectangle2D rd = new Rectangle2D.Double(r.x, r.y, r.width, r.height);
            if (rd.intersectsLine(line)) {
                return false;
            }
        }
        return true;
    }
}
