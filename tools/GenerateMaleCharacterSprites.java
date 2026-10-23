import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** Generates native 96x96 clothing-ready male body sprites with exact palette keys. */
public final class GenerateMaleCharacterSprites {
    private static final int SIZE = 96;
    private static final Color OUTLINE = new Color(0x34, 0x34, 0x3A);
    private static final Color HIGHLIGHT = new Color(0xBB, 0xBB, 0xBB);
    private static final Color BASE = new Color(0xAA, 0xAA, 0xAA);
    private static final Color SHADOW = new Color(0x77, 0x77, 0x77);
    private static final Color UNDERWEAR = new Color(0x2F, 0x34, 0x42);
    private static final Color UNDERWEAR_LIGHT = new Color(0x45, 0x4B, 0x5C);

    private GenerateMaleCharacterSprites() {}

    public static void main(String[] args) throws Exception {
        write("assets/player/body/man/idle/idle_front.png", Facing.FRONT, 0);
        write("assets/player/body/man/idle/idle_front_right.png", Facing.FRONT_RIGHT, 0);
        write("assets/player/body/man/idle/idle_back.png", Facing.BACK, 0);
        write("assets/player/body/man/idle/idle_back_right.png", Facing.BACK_RIGHT, 0);
        write("assets/player/body/man/idle/idle_right.png", Facing.RIGHT, 0);
        write("assets/player/body/man/walking/forward/front_walk1.png", Facing.FRONT, 1);
        write("assets/player/body/man/walking/forward/front_walk2.png", Facing.FRONT, 2);
        write("assets/player/body/man/walking/forward_right/front_right_walk1.png", Facing.FRONT_RIGHT, 1);
        write("assets/player/body/man/walking/forward_right/front_right_walk2.png", Facing.FRONT_RIGHT, 2);
        write("assets/player/body/man/walking/backward/back_walk1.png", Facing.BACK, 1);
        write("assets/player/body/man/walking/backward/back_walk2.png", Facing.BACK, 2);
        write("assets/player/body/man/walking/backward_right/back_right_walk1.png", Facing.BACK_RIGHT, 1);
        write("assets/player/body/man/walking/backward_right/back_right_walk2.png", Facing.BACK_RIGHT, 2);
        write("assets/player/body/man/walking/right/right_walk1.png", Facing.RIGHT, 1);
        write("assets/player/body/man/walking/right/right_walk2.png", Facing.RIGHT, 2);

        write("assets/player/body/man/gun/idle/idle_front.png", Facing.FRONT, 0, false);
        write("assets/player/body/man/gun/idle/idle_front_right.png", Facing.FRONT_RIGHT, 0, false);
        write("assets/player/body/man/gun/idle/idle_back.png", Facing.BACK, 0, false);
        write("assets/player/body/man/gun/idle/idle_back_right.png", Facing.BACK_RIGHT, 0, false);
        write("assets/player/body/man/gun/idle/idle_right.png", Facing.RIGHT, 0, false);
        write("assets/player/body/man/gun/walking/forward/front_walk1.png", Facing.FRONT, 1, false);
        write("assets/player/body/man/gun/walking/forward/front_walk2.png", Facing.FRONT, 2, false);
        write("assets/player/body/man/gun/walking/forward_right/front_right_walk1.png", Facing.FRONT_RIGHT, 1, false);
        write("assets/player/body/man/gun/walking/forward_right/front_right_walk2.png", Facing.FRONT_RIGHT, 2, false);
        write("assets/player/body/man/gun/walking/backward/back_walk1.png", Facing.BACK, 1, false);
        write("assets/player/body/man/gun/walking/backward/back_walk2.png", Facing.BACK, 2, false);
        write("assets/player/body/man/gun/walking/backward_right/back_right_walk1.png", Facing.BACK_RIGHT, 1, false);
        write("assets/player/body/man/gun/walking/backward_right/back_right_walk2.png", Facing.BACK_RIGHT, 2, false);
        write("assets/player/body/man/gun/walking/right/right_walk1.png", Facing.RIGHT, 1, false);
        write("assets/player/body/man/gun/walking/right/right_walk2.png", Facing.RIGHT, 2, false);
    }

    private static void write(String path, Facing facing, int step) throws Exception {
        write(path, facing, step, true);
    }

    private static void write(String path, Facing facing, int step, boolean includeArms) throws Exception {
        BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        switch (facing) {
            case RIGHT -> drawRight(g, step, includeArms);
            case FRONT_RIGHT, BACK_RIGHT ->
                    drawDiagonal(g, facing == Facing.BACK_RIGHT, step, includeArms);
            case FRONT, BACK -> drawFrontBack(g, facing == Facing.BACK, step, includeArms);
        }
        g.dispose();
        File file = new File(path);
        file.getParentFile().mkdirs();
        ImageIO.write(image, "png", file);
    }

    private static void drawFrontBack(Graphics2D g, boolean back, int step, boolean includeArms) {
        int bob = step == 0 ? 0 : 1;
        int leftStride = step == 1 ? -3 : step == 2 ? 2 : 0;
        int rightStride = -leftStride;
        int leftArm = step == 1 ? 2 : step == 2 ? -2 : 0;
        int rightArm = -leftArm;

        if (includeArms) {
            // Arms sit behind the torso so future tops can cover the shoulder seams.
            drawFrontArm(g, 34 + leftArm, 34 + bob, true);
            drawFrontArm(g, 55 + rightArm, 34 + bob, false);
        }

        // Head: bald and featureless except for a tiny baked-in nose shadow on front.
        g.setColor(OUTLINE);
        g.fillOval(36, 5 + bob, 24, 27);
        g.setColor(BASE);
        g.fillOval(38, 7 + bob, 20, 23);
        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(39, 11 + bob, 42, 8 + bob, 46, 7 + bob, 46, 28 + bob, 41, 25 + bob, 39, 20 + bob));
        g.setColor(SHADOW);
        g.fillPolygon(poly(54, 9 + bob, 58, 13 + bob, 58, 22 + bob, 54, 28 + bob, 51, 29 + bob, 53, 21 + bob));
        if (!back) {
            g.fillRect(48, 19 + bob, 2, 3);
        }

        // Neck.
        g.setColor(OUTLINE);
        g.fillRect(42, 28 + bob, 12, 8);
        g.setColor(BASE);
        g.fillRect(44, 28 + bob, 8, 7);
        g.setColor(HIGHLIGHT);
        g.fillRect(44, 29 + bob, 3, 5);
        g.setColor(SHADOW);
        g.fillRect(50, 29 + bob, 2, 6);

        // Tapered torso.
        g.setColor(OUTLINE);
        g.fillPolygon(poly(37, 32 + bob, 43, 30 + bob, 53, 30 + bob, 59, 32 + bob,
                57, 59 + bob, 53, 64 + bob, 43, 64 + bob, 39, 59 + bob));
        g.setColor(BASE);
        g.fillPolygon(poly(39, 34 + bob, 44, 32 + bob, 52, 32 + bob, 57, 34 + bob,
                55, 58 + bob, 52, 61 + bob, 44, 61 + bob, 41, 58 + bob));
        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(40, 36 + bob, 44, 33 + bob, 47, 33 + bob, 46, 59 + bob, 43, 59 + bob, 41, 56 + bob));
        g.setColor(SHADOW);
        g.fillPolygon(poly(53, 33 + bob, 57, 35 + bob, 55, 58 + bob, 52, 61 + bob, 50, 59 + bob, 52, 39 + bob));
        if (back) {
            g.fillRect(43, 35 + bob, 10, 2);
        } else {
            g.fillRect(47, 40 + bob, 2, 2);
            g.fillRect(48, 55 + bob, 2, 2);
        }

        // Underwear is deliberately separate from all three skin keys.
        g.setColor(OUTLINE);
        g.fillPolygon(poly(39, 58 + bob, 57, 58 + bob, 56, 69 + bob, 50, 72 + bob, 46, 72 + bob, 40, 69 + bob));
        g.setColor(UNDERWEAR);
        g.fillPolygon(poly(41, 60 + bob, 55, 60 + bob, 54, 67 + bob, 50, 70 + bob, 46, 70 + bob, 42, 67 + bob));
        g.setColor(UNDERWEAR_LIGHT);
        g.fillRect(42, 60 + bob, 12, 2);

        drawFrontLeg(g, 41, 68 + bob, leftStride, true);
        drawFrontLeg(g, 49, 68 + bob, rightStride, false);
    }

    private static void drawFrontArm(Graphics2D g, int shoulderX, int shoulderY, boolean left) {
        int sign = left ? -1 : 1;
        int elbowX = shoulderX + sign * 3;
        int handX = shoulderX + sign * 5;
        g.setColor(OUTLINE);
        g.fillPolygon(poly(shoulderX, shoulderY, shoulderX + 6, shoulderY + 1,
                elbowX + 5, shoulderY + 15, handX + 5, shoulderY + 29,
                handX, shoulderY + 31, elbowX, shoulderY + 17));
        g.setColor(BASE);
        g.fillPolygon(poly(shoulderX + 2, shoulderY + 2, shoulderX + 4, shoulderY + 3,
                elbowX + 3, shoulderY + 15, handX + 3, shoulderY + 28,
                handX + 1, shoulderY + 29, elbowX + 2, shoulderY + 16));
        g.setColor(left ? HIGHLIGHT : SHADOW);
        g.fillPolygon(poly(shoulderX + 2, shoulderY + 3, shoulderX + 3, shoulderY + 3,
                elbowX + 2, shoulderY + 15, handX + 2, shoulderY + 27,
                handX + 1, shoulderY + 27, elbowX + 1, shoulderY + 15));
    }

    private static void drawFrontLeg(Graphics2D g, int hipX, int hipY, int stride, boolean left) {
        int kneeX = hipX + stride / 2;
        int footX = hipX + stride;
        g.setColor(OUTLINE);
        g.fillPolygon(poly(hipX, hipY, hipX + 7, hipY, kneeX + 7, 79, footX + 7, 88,
                footX + 6, 91, footX - 2, 91, footX, 87, kneeX, 79));
        g.setColor(BASE);
        g.fillPolygon(poly(hipX + 2, hipY + 1, hipX + 5, hipY + 1, kneeX + 5, 79,
                footX + 5, 88, footX + 4, 89, footX, 89, footX + 2, 86, kneeX + 2, 78));
        g.setColor(left ? HIGHLIGHT : SHADOW);
        g.fillPolygon(poly(hipX + 2, hipY + 2, hipX + 3, hipY + 2, kneeX + 3, 79,
                footX + 3, 87, footX + 1, 87, kneeX + 1, 79));
    }

    private static void drawRight(Graphics2D g, int step, boolean includeArms) {
        int bob = step == 0 ? 0 : 1;
        int nearStride = step == 1 ? 3 : step == 2 ? -3 : 0;
        int farStride = -nearStride;
        int nearArm = step == 1 ? -2 : step == 2 ? 2 : 0;
        int farArm = -nearArm;

        if (includeArms) {
            drawSideArm(g, 42 + farArm, 35 + bob, true);
        }

        // Profile head and nose.
        g.setColor(OUTLINE);
        g.fillOval(37, 5 + bob, 23, 27);
        g.fillPolygon(poly(56, 15 + bob, 63, 18 + bob, 58, 22 + bob));
        g.setColor(BASE);
        g.fillOval(39, 7 + bob, 19, 23);
        g.fillPolygon(poly(56, 16 + bob, 61, 18 + bob, 57, 20 + bob));
        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(40, 11 + bob, 43, 8 + bob, 47, 7 + bob, 46, 29 + bob, 42, 25 + bob, 40, 20 + bob));
        g.setColor(SHADOW);
        g.fillPolygon(poly(54, 9 + bob, 58, 13 + bob, 58, 24 + bob, 54, 29 + bob, 51, 29 + bob, 53, 20 + bob));

        g.setColor(OUTLINE);
        g.fillRect(43, 28 + bob, 11, 8);
        g.setColor(BASE);
        g.fillRect(45, 28 + bob, 7, 7);
        g.setColor(HIGHLIGHT);
        g.fillRect(45, 29 + bob, 2, 5);
        g.setColor(SHADOW);
        g.fillRect(50, 29 + bob, 2, 6);

        // Narrow profile torso.
        g.setColor(OUTLINE);
        g.fillPolygon(poly(40, 32 + bob, 46, 30 + bob, 54, 32 + bob, 58, 38 + bob,
                56, 60 + bob, 52, 64 + bob, 43, 63 + bob, 40, 58 + bob));
        g.setColor(BASE);
        g.fillPolygon(poly(42, 34 + bob, 46, 32 + bob, 52, 34 + bob, 56, 38 + bob,
                54, 58 + bob, 51, 61 + bob, 45, 60 + bob, 42, 57 + bob));
        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(42, 35 + bob, 46, 33 + bob, 48, 34 + bob, 47, 59 + bob, 44, 58 + bob));
        g.setColor(SHADOW);
        g.fillPolygon(poly(52, 34 + bob, 56, 38 + bob, 54, 58 + bob, 51, 61 + bob, 49, 59 + bob, 51, 39 + bob));

        g.setColor(OUTLINE);
        g.fillPolygon(poly(41, 58 + bob, 56, 58 + bob, 55, 70 + bob, 45, 71 + bob, 41, 67 + bob));
        g.setColor(UNDERWEAR);
        g.fillPolygon(poly(43, 60 + bob, 54, 60 + bob, 53, 68 + bob, 46, 69 + bob, 43, 66 + bob));
        g.setColor(UNDERWEAR_LIGHT);
        g.fillRect(43, 60 + bob, 10, 2);

        drawSideLeg(g, 43, 68 + bob, farStride, true);
        drawSideLeg(g, 49, 68 + bob, nearStride, false);
        if (includeArms) {
            drawSideArm(g, 52 + nearArm, 34 + bob, false);
        }
    }

    private static void drawDiagonal(Graphics2D g, boolean back, int step, boolean includeArms) {
        int bob = step == 0 ? 0 : 1;
        int nearStride = step == 1 ? 3 : step == 2 ? -3 : 0;
        int farStride = -nearStride;
        int nearArm = step == 1 ? -2 : step == 2 ? 2 : 0;
        int farArm = -nearArm;

        if (includeArms) {
            drawSideArm(g, 37 + farArm, 35 + bob, true);
        }

        // Three-quarter head: broad like the front view, but shifted toward the visible profile.
        g.setColor(OUTLINE);
        g.fillOval(36, 5 + bob, 25, 27);
        if (!back) {
            g.fillPolygon(poly(57, 16 + bob, 63, 19 + bob, 58, 22 + bob));
        }
        g.setColor(BASE);
        g.fillOval(38, 7 + bob, 21, 23);
        if (!back) {
            g.fillPolygon(poly(57, 17 + bob, 61, 19 + bob, 58, 20 + bob));
        }
        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(39, 11 + bob, 42, 8 + bob, 47, 7 + bob, 47, 29 + bob,
                42, 26 + bob, 39, 20 + bob));
        g.setColor(SHADOW);
        g.fillPolygon(poly(54, 8 + bob, 59, 12 + bob, 59, 24 + bob, 55, 29 + bob,
                51, 29 + bob, 53, 20 + bob));
        if (back) {
            g.fillRect(42, 10 + bob, 2, 15);
        } else {
            g.fillRect(50, 19 + bob, 2, 3);
        }

        g.setColor(OUTLINE);
        g.fillPolygon(poly(41, 28 + bob, 55, 28 + bob, 55, 36 + bob, 42, 36 + bob));
        g.setColor(BASE);
        g.fillPolygon(poly(43, 28 + bob, 53, 28 + bob, 53, 35 + bob, 44, 35 + bob));
        g.setColor(HIGHLIGHT);
        g.fillRect(44, 29 + bob, 3, 5);
        g.setColor(SHADOW);
        g.fillRect(51, 29 + bob, 2, 6);

        // Rotated torso narrows on the far side and keeps the near shoulder prominent.
        g.setColor(OUTLINE);
        g.fillPolygon(poly(36, 34 + bob, 43, 30 + bob, 53, 31 + bob, 59, 36 + bob,
                57, 59 + bob, 52, 64 + bob, 42, 63 + bob, 38, 58 + bob));
        g.setColor(BASE);
        g.fillPolygon(poly(39, 35 + bob, 44, 32 + bob, 52, 33 + bob, 57, 37 + bob,
                55, 58 + bob, 51, 61 + bob, 44, 60 + bob, 40, 57 + bob));
        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(40, 36 + bob, 44, 33 + bob, 48, 33 + bob, 47, 59 + bob,
                43, 59 + bob, 40, 56 + bob));
        g.setColor(SHADOW);
        g.fillPolygon(poly(52, 33 + bob, 57, 37 + bob, 55, 58 + bob, 51, 61 + bob,
                49, 59 + bob, 51, 39 + bob));
        if (back) {
            g.fillRect(42, 35 + bob, 11, 2);
        } else {
            g.fillRect(48, 41 + bob, 2, 2);
            g.fillRect(49, 55 + bob, 2, 2);
        }

        g.setColor(OUTLINE);
        g.fillPolygon(poly(39, 58 + bob, 57, 58 + bob, 56, 69 + bob, 51, 72 + bob,
                45, 71 + bob, 40, 68 + bob));
        g.setColor(UNDERWEAR);
        g.fillPolygon(poly(41, 60 + bob, 55, 60 + bob, 54, 67 + bob, 50, 70 + bob,
                46, 69 + bob, 42, 67 + bob));
        g.setColor(UNDERWEAR_LIGHT);
        g.fillRect(42, 60 + bob, 12, 2);

        drawSideLeg(g, 42, 68 + bob, farStride, true);
        drawSideLeg(g, 49, 68 + bob, nearStride, false);
        if (includeArms) {
            drawSideArm(g, 54 + nearArm, 34 + bob, false);
        }
    }

    private static void drawSideArm(Graphics2D g, int shoulderX, int shoulderY, boolean far) {
        int elbowX = shoulderX + 3;
        int handX = shoulderX + 1;
        g.setColor(OUTLINE);
        g.fillPolygon(poly(shoulderX, shoulderY, shoulderX + 6, shoulderY + 2,
                elbowX + 4, shoulderY + 16, handX + 5, shoulderY + 31,
                handX, shoulderY + 32, elbowX, shoulderY + 17));
        g.setColor(far ? SHADOW : BASE);
        g.fillPolygon(poly(shoulderX + 2, shoulderY + 2, shoulderX + 4, shoulderY + 3,
                elbowX + 2, shoulderY + 16, handX + 3, shoulderY + 29,
                handX + 1, shoulderY + 30, elbowX + 2, shoulderY + 16));
        if (!far) {
            g.setColor(HIGHLIGHT);
            g.fillPolygon(poly(shoulderX + 2, shoulderY + 3, shoulderX + 3, shoulderY + 3,
                    elbowX + 1, shoulderY + 15, handX + 2, shoulderY + 28,
                    handX + 1, shoulderY + 28, elbowX, shoulderY + 15));
        }
    }

    private static void drawSideLeg(Graphics2D g, int hipX, int hipY, int stride, boolean far) {
        int kneeX = hipX + stride / 2;
        int footX = hipX + stride;
        g.setColor(OUTLINE);
        g.fillPolygon(poly(hipX, hipY, hipX + 7, hipY, kneeX + 7, 79, footX + 9, 88,
                footX + 8, 91, footX - 1, 91, footX + 1, 87, kneeX, 78));
        g.setColor(far ? SHADOW : BASE);
        g.fillPolygon(poly(hipX + 2, hipY + 1, hipX + 5, hipY + 1, kneeX + 5, 79,
                footX + 7, 88, footX + 6, 89, footX + 1, 89, footX + 3, 86, kneeX + 2, 78));
        if (!far) {
            g.setColor(HIGHLIGHT);
            g.fillPolygon(poly(hipX + 2, hipY + 2, hipX + 3, hipY + 2, kneeX + 3, 78,
                    footX + 5, 87, footX + 2, 87, kneeX + 1, 78));
        }
    }

    private static Polygon poly(int... coordinates) {
        int points = coordinates.length / 2;
        int[] xs = new int[points];
        int[] ys = new int[points];
        for (int i = 0; i < points; i++) {
            xs[i] = coordinates[i * 2];
            ys[i] = coordinates[i * 2 + 1];
        }
        return new Polygon(xs, ys, points);
    }

    private enum Facing {
        FRONT, FRONT_RIGHT, RIGHT, BACK_RIGHT, BACK
    }
}
