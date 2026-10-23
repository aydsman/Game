import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Generates two independent arm layers for each of sixteen aim directions.
 * The future gun is drawn between rear and front so both hands can wrap it.
 */
public final class GenerateGunArmSprites {
    private static final int SIZE = 96;
    private static final Color OUTLINE = new Color(0x34, 0x34, 0x3A);
    private static final Color HIGHLIGHT = new Color(0xBB, 0xBB, 0xBB);
    private static final Color BASE = new Color(0xAA, 0xAA, 0xAA);
    private static final Color SHADOW = new Color(0x77, 0x77, 0x77);

    private GenerateGunArmSprites() {}

    public static void main(String[] args) throws Exception {
        for (Aim aim : Aim.values()) {
            write(aim, true);
            write(aim, false);
        }
    }

    private static void write(Aim aim, boolean rear) throws Exception {
        BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        drawArm(g, aim, rear);
        g.dispose();

        String layer = rear ? "rear" : "front";
        File file = new File("assets/player/body/man/arms/gun/" + aim.fileName + "_" + layer + ".png");
        file.getParentFile().mkdirs();
        ImageIO.write(image, "png", file);
    }

    private static void drawArm(Graphics2D g, Aim aim, boolean rear) {
        double length = Math.sqrt(aim.dx * aim.dx + aim.dy * aim.dy);
        double dx = aim.dx / length;
        double dy = aim.dy / length;
        double px = -dy;
        double py = dx;

        int shoulderX;
        // The redesigned torso begins at y=28, so the gun pose must hinge from
        // the deltoids rather than from the old character's lower chest.
        int shoulderY = 31;
        if (aim.dx > 0.25) {
            shoulderX = rear ? 43 : 56;
        } else if (aim.dx < -0.25) {
            shoulderX = rear ? 53 : 40;
        } else {
            shoulderX = rear ? 37 : 59;
        }

        // The rear/support arm reaches along the fore-end. The front/trigger arm stays near the grip.
        double handDistance = rear ? 22.0 : 8.0;
        int handX = (int) Math.round(48 + dx * handDistance + px * (rear ? -1 : 2));
        int handY = (int) Math.round(40 + dy * handDistance + py * (rear ? -1 : 2));

        int midpointX = (shoulderX + handX) / 2;
        int midpointY = (shoulderY + handY) / 2;
        int bend = rear ? -5 : 3;
        int elbowX = (int) Math.round(midpointX + px * bend);
        int elbowY = (int) Math.round(midpointY + py * bend + (rear ? 5 : 3));

        int[] xs = {shoulderX, elbowX, handX};
        int[] ys = {shoulderY, elbowY, handY};

        g.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(OUTLINE);
        g.drawPolyline(xs, ys, xs.length);

        g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(rear ? SHADOW : BASE);
        g.drawPolyline(xs, ys, xs.length);

        if (!rear) {
            int highlightOffsetX = (int) Math.round(px);
            int highlightOffsetY = (int) Math.round(py);
            int[] highlightXs = {
                shoulderX + highlightOffsetX,
                elbowX + highlightOffsetX,
                handX + highlightOffsetX
            };
            int[] highlightYs = {
                shoulderY + highlightOffsetY,
                elbowY + highlightOffsetY,
                handY + highlightOffsetY
            };
            g.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(HIGHLIGHT);
            g.drawPolyline(highlightXs, highlightYs, highlightXs.length);
        }

        // Rounded hands surround the empty grip point where the future gun will sit.
        g.setColor(OUTLINE);
        g.fillOval(handX - 3, handY - 3, 7, 7);
        g.setColor(rear ? SHADOW : BASE);
        g.fillOval(handX - 1, handY - 1, 3, 3);
        if (!rear) {
            g.setColor(HIGHLIGHT);
            g.fillRect(handX - 1, handY - 1, 1, 1);
        }
    }

    private enum Aim {
        E("e", 0),
        ESE("ese", 22.5),
        SE("se", 45),
        SSE("sse", 67.5),
        S("s", 90),
        SSW("ssw", 112.5),
        SW("sw", 135),
        WSW("wsw", 157.5),
        W("w", 180),
        WNW("wnw", 202.5),
        NW("nw", 225),
        NNW("nnw", 247.5),
        N("n", 270),
        NNE("nne", 292.5),
        NE("ne", 315),
        ENE("ene", 337.5);

        private final String fileName;
        private final double dx;
        private final double dy;

        Aim(String fileName, double degrees) {
            this.fileName = fileName;
            double radians = Math.toRadians(degrees);
            this.dx = Math.cos(radians);
            this.dy = Math.sin(radians);
        }
    }
}
