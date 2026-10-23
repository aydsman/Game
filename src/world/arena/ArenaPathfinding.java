package world.arena;

import entity.Entity;
import entity.Player;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Grid A* pathfinding around axis-aligned obstacles for arena enemies.
 * Recomputes when the player's goal cell changes or on a short cooldown so movement reacts to last-second dodges.
 */
public final class ArenaPathfinding {

    public static final int CELL = 40;
    /** Frames between full A* recomputes when goal cell unchanged. */
    private static final int RECOMPUTE_INTERVAL = 10;

    public static final class PathAgentState {
        int recomputeCooldown;
        int lastGoalGx = Integer.MIN_VALUE;
        int lastGoalGy = Integer.MIN_VALUE;
        final ArrayList<int[]> pathCells = new ArrayList<>();
    }

    private ArenaPathfinding() {}

    public static void applyChaseStep(Entity e, Player player, List<Rectangle> obstacles, int arenaW, int arenaH, int stepPx) {
        if (obstacles == null || obstacles.isEmpty()) {
            applyDirectChase(e, player, arenaW, arenaH, stepPx, null);
            return;
        }
        PathAgentState st = e.getPathAgentState();
        int ecx = e.getCenterX();
        int ecy = e.getCenterY();
        int tcx = player.getCenterX();
        int tcy = player.getCenterY();
        int cols = Math.max(1, (arenaW + CELL - 1) / CELL);
        int rows = Math.max(1, (arenaH + CELL - 1) / CELL);
        int tgx = clamp(tcx / CELL, 0, cols - 1);
        int tgy = clamp(tcy / CELL, 0, rows - 1);

        boolean goalMoved = tgx != st.lastGoalGx || tgy != st.lastGoalGy;
        if (goalMoved) {
            st.lastGoalGx = tgx;
            st.lastGoalGy = tgy;
            st.recomputeCooldown = 0;
        }
        if (--st.recomputeCooldown <= 0 || goalMoved || st.pathCells.isEmpty()) {
            st.recomputeCooldown = RECOMPUTE_INTERVAL;
            int sgx = clamp(ecx / CELL, 0, cols - 1);
            int sgy = clamp(ecy / CELL, 0, rows - 1);
            st.pathCells.clear();
            List<int[]> full = aStar(sgx, sgy, tgx, tgy, cols, rows, e.getW(), e.getL(), obstacles, arenaW, arenaH);
            if (full != null && !full.isEmpty()) {
                // Drop same cell as start
                int i0 = 0;
                while (i0 < full.size() && full.get(i0)[0] == sgx && full.get(i0)[1] == sgy) {
                    i0++;
                }
                for (int i = i0; i < full.size(); i++) {
                    st.pathCells.add(full.get(i));
                }
            }
        }

        int ox = e.getX();
        int oy = e.getY();
        int pw = e.getW();
        int ph = e.getL();

        if (st.pathCells.isEmpty()) {
            applyDirectChase(e, player, arenaW, arenaH, stepPx, obstacles);
            return;
        }
        int[] next = st.pathCells.get(0);
        int ncx;
        int ncy;
        double dx;
        double dy;
        double dist;
        while (true) {
            ncx = next[0] * CELL + CELL / 2;
            ncy = next[1] * CELL + CELL / 2;
            dx = ncx - ecx;
            dy = ncy - ecy;
            dist = Math.hypot(dx, dy);
            if (dist >= 1e-3) break;
            st.pathCells.remove(0);
            if (st.pathCells.isEmpty()) {
                applyDirectChase(e, player, arenaW, arenaH, stepPx, obstacles);
                return;
            }
            next = st.pathCells.get(0);
        }
        if (dist <= stepPx) {
            st.pathCells.remove(0);
        }
        double ux = dx / dist;
        double uy = dy / dist;
        int ndx = (int) Math.round(ux * stepPx);
        int ndy = (int) Math.round(uy * stepPx);
        int tcxStep = ecx + ndx;
        int tcyStep = ecy + ndy;
        e.setX(tcxStep - pw / 2);
        e.setY(tcyStep - ph / 2);
        int[] r = ArenaCollision.resolveMovement(ox, oy, e.getX(), e.getY(), pw, ph, obstacles, arenaW, arenaH);
        e.setX(r[0]);
        e.setY(r[1]);
    }

    /** Direct pursuit + collision resolve (no pathfinding). */
    public static void applyDirectChase(Entity e, Player player, int arenaW, int arenaH, int stepPx, List<Rectangle> obstacles) {
        int ox = e.getX();
        int oy = e.getY();
        int pw = e.getW();
        int ph = e.getL();
        int ecx = e.getCenterX();
        int ecy = e.getCenterY();
        int tcx = player.getCenterX();
        int tcy = player.getCenterY();
        int dx = tcx - ecx;
        int dy = tcy - ecy;
        double dist = Math.hypot(dx, dy);
        if (dist < 1e-3) return;
        int ndx = (int) Math.round(dx / dist * stepPx);
        int ndy = (int) Math.round(dy / dist * stepPx);
        e.setX(ecx + ndx - pw / 2);
        e.setY(ecy + ndy - ph / 2);
        if (obstacles == null || obstacles.isEmpty()) {
            e.setX(clamp(e.getX(), 0, arenaW - pw));
            e.setY(clamp(e.getY(), 0, arenaH - ph));
        } else {
            int[] r = ArenaCollision.resolveMovement(ox, oy, e.getX(), e.getY(), pw, ph, obstacles, arenaW, arenaH);
            e.setX(r[0]);
            e.setY(r[1]);
        }
    }

    private static List<int[]> aStar(int sgx, int sgy, int tgx, int tgy, int cols, int rows,
                                     int ew, int el, List<Rectangle> obstacles, int arenaW, int arenaH) {
        if (!walkable(tgx, tgy, ew, el, obstacles, arenaW, arenaH)) {
            int[] near = nearestWalkable(tgx, tgy, ew, el, obstacles, arenaW, arenaH, cols, rows, 12);
            if (near != null) {
                tgx = near[0];
                tgy = near[1];
            }
        }
        if (!walkable(sgx, sgy, ew, el, obstacles, arenaW, arenaH)) {
            return Collections.emptyList();
        }

        int startKey = pack(sgx, sgy, cols);
        int goalKey = pack(tgx, tgy, cols);

        PriorityQueue<AStarNode> open = new PriorityQueue<>();
        Map<Integer, Double> gScore = new HashMap<>();
        Map<Integer, Integer> cameFrom = new HashMap<>();
        gScore.put(startKey, 0.0);
        open.add(new AStarNode(sgx, sgy, 0, heuristic(sgx, sgy, tgx, tgy)));

        int[][] dirs = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        while (!open.isEmpty()) {
            AStarNode cur = open.poll();
            int ck = pack(cur.gx, cur.gy, cols);
            if (ck == goalKey) {
                return reconstruct(cameFrom, cur.gx, cur.gy, sgx, sgy, cols);
            }
            double knownG = gScore.getOrDefault(ck, Double.POSITIVE_INFINITY);
            if (cur.g > knownG + 1e-6) continue;

            for (int[] d : dirs) {
                int ngx = cur.gx + d[0];
                int ngy = cur.gy + d[1];
                if (ngx < 0 || ngx >= cols || ngy < 0 || ngy >= rows) continue;
                if (d[0] != 0 && d[1] != 0) {
                    if (!walkable(cur.gx + d[0], cur.gy, ew, el, obstacles, arenaW, arenaH)
                            || !walkable(cur.gx, cur.gy + d[1], ew, el, obstacles, arenaW, arenaH)) {
                        continue;
                    }
                }
                if (!walkable(ngx, ngy, ew, el, obstacles, arenaW, arenaH)) continue;

                double stepCost = (d[0] != 0 && d[1] != 0) ? 1.414 : 1.0;
                double tg = cur.g + stepCost;
                int nk = pack(ngx, ngy, cols);
                if (tg < gScore.getOrDefault(nk, Double.POSITIVE_INFINITY)) {
                    cameFrom.put(nk, ck);
                    gScore.put(nk, tg);
                    open.add(new AStarNode(ngx, ngy, tg, tg + heuristic(ngx, ngy, tgx, tgy)));
                }
            }
        }
        return Collections.emptyList();
    }

    private static List<int[]> reconstruct(Map<Integer, Integer> cameFrom, int gx, int gy, int sgx, int sgy, int cols) {
        ArrayList<int[]> out = new ArrayList<>();
        int cur = pack(gx, gy, cols);
        final int start = pack(sgx, sgy, cols);
        while (true) {
            out.add(new int[]{unpackGx(cur, cols), unpackGy(cur, cols)});
            if (cur == start) break;
            Integer p = cameFrom.get(cur);
            if (p == null) break;
            cur = p;
        }
        Collections.reverse(out);
        return out;
    }

    private static int pack(int gx, int gy, int cols) {
        return gx + gy * cols;
    }

    private static int unpackGx(int k, int cols) {
        return k % cols;
    }

    private static int unpackGy(int k, int cols) {
        return k / cols;
    }

    private static double heuristic(int gx, int gy, int tgx, int tgy) {
        int dx = Math.abs(gx - tgx);
        int dy = Math.abs(gy - tgy);
        return Math.hypot(dx, dy);
    }

    private static boolean walkable(int gx, int gy, int ew, int el, List<Rectangle> obstacles, int arenaW, int arenaH) {
        int cx = gx * CELL + CELL / 2;
        int cy = gy * CELL + CELL / 2;
        int ex = cx - ew / 2;
        int ey = cy - el / 2;
        if (ex < 0 || ey < 0 || ex + ew > arenaW || ey + el > arenaH) return false;
        return !ArenaCollision.entityIntersectsAny(ex, ey, ew, el, obstacles);
    }

    private static int[] nearestWalkable(int gx, int gy, int ew, int el, List<Rectangle> obstacles, int arenaW, int arenaH,
                                         int cols, int rows, int maxRadius) {
        for (int r = 0; r <= maxRadius; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dy = -r; dy <= r; dy++) {
                    if (Math.abs(dx) != r && Math.abs(dy) != r && r > 0) continue;
                    int ngx = gx + dx;
                    int ngy = gy + dy;
                    if (ngx < 0 || ngx >= cols || ngy < 0 || ngy >= rows) continue;
                    if (walkable(ngx, ngy, ew, el, obstacles, arenaW, arenaH)) {
                        return new int[]{ngx, ngy};
                    }
                }
            }
        }
        return null;
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(v, hi));
    }

    private static final class AStarNode implements Comparable<AStarNode> {
        final int gx, gy;
        final double g, f;

        AStarNode(int gx, int gy, double g, double f) {
            this.gx = gx;
            this.gy = gy;
            this.g = g;
            this.f = f;
        }

        @Override
        public int compareTo(AStarNode o) {
            int c = Double.compare(f, o.f);
            if (c != 0) return c;
            return Integer.compare(gx + gy * 10000, o.gx + o.gy * 10000);
        }
    }
}
