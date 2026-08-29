package world.arena;

import java.awt.Rectangle;
import java.util.List;

public final class ArenaCollision {
    private ArenaCollision() {}

    public static int[] resolveMovement(int oldX, int oldY, int newX, int newY, int w, int l,
                                        List<Rectangle> obstacles, int arenaWidth, int arenaHeight) {
        int x = clamp(newX, 0, arenaWidth - w);
        int y = clamp(newY, 0, arenaHeight - l);
        if (obstacles == null || obstacles.isEmpty()) {
            return new int[] { x, y };
        }
        Rectangle body = new Rectangle(x, y, w, l);
        for (Rectangle o : obstacles) {
            if (body.intersects(o)) {
                Rectangle oldBody = new Rectangle(oldX, oldY, w, l);
                if (!oldBody.intersects(o)) {
                    x = oldX;
                    y = oldY;
                    body.setLocation(x, y);
                } else {
                    if (!new Rectangle(newX, oldY, w, l).intersects(o)) {
                        x = newX;
                        y = oldY;
                    } else if (!new Rectangle(oldX, newY, w, l).intersects(o)) {
                        x = oldX;
                        y = newY;
                    } else {
                        x = oldX;
                        y = oldY;
                    }
                    body.setLocation(x, y);
                }
            }
        }
        return new int[] { x, y };
    }

    public static boolean projectileHitsObstacle(int px, int py, int radius, List<Rectangle> obstacles) {
        if (obstacles == null || obstacles.isEmpty()) return false;
        Rectangle p = new Rectangle(px - radius, py - radius, radius * 2, radius * 2);
        for (Rectangle o : obstacles) {
            if (p.intersects(o)) return true;
        }
        return false;
    }

    public static boolean lineOfSightClear(int x1, int y1, int x2, int y2, List<Rectangle> obstacles) {
        if (obstacles == null || obstacles.isEmpty()) return true;
        for (Rectangle o : obstacles) {
            if (o.intersectsLine(x1, y1, x2, y2)) return false;
        }
        return true;
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
