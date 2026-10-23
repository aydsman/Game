package entity.attack;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * Draws the locked slam circle so the player can see the danger before it lands.
 */
public final class TelegraphedSlamRenderer {

    private static final Color WINDUP_FILL = new Color(220, 50, 40, 55);
    private static final Color WINDUP_RING = new Color(255, 80, 60, 210);
    private static final Color IMPACT_FILL = new Color(255, 220, 80, 90);
    private static final Color IMPACT_RING = new Color(255, 230, 120, 230);

    private TelegraphedSlamRenderer() {}

    public static void draw(Graphics2D g, TelegraphedSlam slam, long nowMs, int cameraX, int cameraY) {
        if (slam == null) {
            return;
        }
        TelegraphedSlam.Phase phase = slam.getPhase(nowMs);
        if (phase == TelegraphedSlam.Phase.IDLE) {
            return;
        }

        int radius = slam.getRadiusPx();
        int screenX = slam.getAnchorX() - cameraX - radius;
        int screenY = slam.getAnchorY() - cameraY - radius;
        int size = radius * 2;

        Stroke previous = g.getStroke();
        if (phase == TelegraphedSlam.Phase.WINDUP) {
            g.setColor(WINDUP_FILL);
            g.fillOval(screenX, screenY, size, size);
            g.setStroke(new BasicStroke(3f));
            g.setColor(WINDUP_RING);
            g.drawOval(screenX, screenY, size, size);

            // Inner ring grows toward the rim so the remaining reaction time is visible.
            double progress = slam.getWindupProgress(nowMs);
            int inner = (int) Math.round(radius * progress);
            if (inner > 2) {
                g.setStroke(new BasicStroke(2f));
                g.drawOval(slam.getAnchorX() - cameraX - inner, slam.getAnchorY() - cameraY - inner,
                        inner * 2, inner * 2);
            }
        } else {
            g.setColor(IMPACT_FILL);
            g.fillOval(screenX, screenY, size, size);
            g.setStroke(new BasicStroke(4f));
            g.setColor(IMPACT_RING);
            g.drawOval(screenX, screenY, size, size);
        }
        g.setStroke(previous);
    }
}
