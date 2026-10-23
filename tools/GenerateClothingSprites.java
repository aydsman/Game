import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** Generates 96x96 clothing layers aligned to every native male character view. */
public final class GenerateClothingSprites {
    private static final int SIZE = 96;
    private static final Color OUTLINE = new Color(0x2B, 0x2D, 0x34);
    private static final Color HIGHLIGHT = new Color(0xDD, 0xDD, 0xDD);
    private static final Color BASE = new Color(0x99, 0x99, 0x99);
    private static final Color SHADOW = new Color(0x55, 0x55, 0x55);
    private static final String[] DIRECTIONS =
            {"front", "front_right", "right", "back_right", "back"};
    private static final String[] TOPS =
            {"tshirt", "boxytee", "hoodie", "longsleeve", "tanktop", "suittop"};
    private static final String[] BOTTOMS =
            {"basicpants", "shorts", "jorts", "baggyjeans", "sweatpants", "swimshorts", "suitbottom"};
    private static final String[] ACCESSORIES =
            {"crown", "cape", "fedora", "allseeingeye", "headband",
                    "ninjaheadband", "glasses", "shades", "aviators"};

    private GenerateClothingSprites() {}

    public static void main(String[] args) throws Exception {
        for (String item : TOPS) {
            for (String direction : DIRECTIONS) {
                for (int step = 0; step < 3; step++) {
                    BufferedImage image = top(item, direction, step, false);
                    write(image, "assets/player/shared/tops/" + item + "/" + direction
                            + "_" + (step == 0 ? "idle" : "walk" + step) + ".png");
                    if ("front".equals(direction) && step == 0) {
                        write(image, "assets/player/shared/tops/" + item + ".png");
                    }
                }
                write(top(item, direction, 0, true),
                        "assets/player/shared/tops/" + item + "/" + direction + "_gun.png");
            }
        }
        for (String item : BOTTOMS) {
            for (String direction : DIRECTIONS) {
                for (int step = 0; step < 3; step++) {
                    BufferedImage image = bottom(item, direction, step);
                    write(image, "assets/player/shared/bottoms/" + item + "/" + direction
                            + "_" + (step == 0 ? "idle" : "walk" + step) + ".png");
                    if ("front".equals(direction) && step == 0) {
                        write(image, "assets/player/shared/bottoms/" + item + ".png");
                    }
                }
            }
        }
        for (String item : ACCESSORIES) {
            for (String direction : DIRECTIONS) {
                BufferedImage rear = accessory(item, direction, true);
                BufferedImage front = accessory(item, direction, false);
                write(rear, "assets/player/shared/accessories/" + item + "/" + direction + "_rear.png");
                write(front, "assets/player/shared/accessories/" + item + "/" + direction + "_front.png");
                if ("front".equals(direction)) {
                    write(combine(rear, front), "assets/player/shared/accessories/" + item + ".png");
                }
            }
        }
    }

    private static BufferedImage top(String item, String direction, int step, boolean gun) {
        BufferedImage image = canvas();
        Graphics2D g = graphics(image);
        int bob = step == 0 ? 0 : 1;
        boolean side = "right".equals(direction);
        boolean diagonal = direction.endsWith("_right");
        boolean tank = "tanktop".equals(item);
        boolean boxy = "boxytee".equals(item) || "hoodie".equals(item);
        boolean longSleeve = "hoodie".equals(item)
                || "longsleeve".equals(item) || "suittop".equals(item);
        int sleeveLength = longSleeve ? 29 : boxy ? 12 : 9;

        if (!gun && !tank) {
            if (side || diagonal) {
                int nearArm = step == 1 ? -2 : step == 2 ? 2 : 0;
                int farArm = -nearArm;
                drawSideSleeve(g, (diagonal ? 37 : 42) + farArm, 35 + bob, sleeveLength, true);
                drawSideSleeve(g, (diagonal ? 54 : 52) + nearArm, 34 + bob, sleeveLength, false);
            } else {
                int leftArm = step == 1 ? 2 : step == 2 ? -2 : 0;
                int rightArm = -leftArm;
                drawFrontSleeve(g, 34 + leftArm, 34 + bob, sleeveLength, true);
                drawFrontSleeve(g, 55 + rightArm, 34 + bob, sleeveLength, false);
            }
        }

        if (side) {
            fillGarment(g, poly(40, 33 + bob, 46, 30 + bob, 54, 32 + bob, 58, 38 + bob,
                    56, 60 + bob, 52, 64 + bob, 43, 63 + bob, 40, 58 + bob),
                    poly(42, 35 + bob, 47, 33 + bob, 52, 34 + bob, 56, 39 + bob,
                    54, 58 + bob, 51, 61 + bob, 45, 60 + bob, 42, 57 + bob));
        } else if (diagonal) {
            fillGarment(g, poly(36, 34 + bob, 43, 30 + bob, 53, 31 + bob, 59, 36 + bob,
                    57, 59 + bob, 52, 64 + bob, 42, 63 + bob, 38, 58 + bob),
                    poly(39, 35 + bob, 44, 32 + bob, 52, 33 + bob, 57, 37 + bob,
                    55, 58 + bob, 51, 61 + bob, 44, 60 + bob, 40, 57 + bob));
        } else {
            fillGarment(g, poly(37, 32 + bob, 43, 30 + bob, 53, 30 + bob, 59, 32 + bob,
                    57, 59 + bob, 53, 64 + bob, 43, 64 + bob, 39, 59 + bob),
                    poly(39, 34 + bob, 44, 32 + bob, 52, 32 + bob, 57, 34 + bob,
                    55, 58 + bob, 52, 61 + bob, 44, 61 + bob, 41, 58 + bob));
        }

        drawTopDetails(g, item, direction, bob);
        g.dispose();
        return image;
    }

    private static void fillGarment(Graphics2D g, Polygon outer, Polygon inner) {
        g.setColor(OUTLINE);
        g.fillPolygon(outer);
        g.setColor(BASE);
        g.fillPolygon(inner);
        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(inner.xpoints[0], inner.ypoints[0] + 2,
                inner.xpoints[1], inner.ypoints[1] + 1,
                inner.xpoints[1] + 3, inner.ypoints[1] + 2,
                inner.xpoints[1] + 2, inner.ypoints[inner.npoints - 1] - 2,
                inner.xpoints[0] + 1, inner.ypoints[inner.npoints - 1] - 3));
        g.setColor(SHADOW);
        int i = inner.npoints - 1;
        g.fillPolygon(poly(inner.xpoints[2], inner.ypoints[2] + 1,
                inner.xpoints[3], inner.ypoints[3] + 1,
                inner.xpoints[4], inner.ypoints[4],
                inner.xpoints[5], inner.ypoints[5],
                inner.xpoints[i - 1], inner.ypoints[i - 1],
                inner.xpoints[2] - 1, inner.ypoints[2] + 7));
    }

    private static void drawTopDetails(Graphics2D g, String item, String direction, int bob) {
        boolean back = direction.startsWith("back");
        if ("hoodie".equals(item)) {
            g.setColor(OUTLINE);
            if (back) {
                g.drawOval(41, 30 + bob, 15, 12);
            } else {
                g.drawLine(47, 33 + bob, 47, 45 + bob);
                g.drawLine(50, 33 + bob, 50, 45 + bob);
                g.drawRect(43, 49 + bob, 11, 7);
            }
        } else if ("tanktop".equals(item)) {
            g.setColor(OUTLINE);
            g.fillRect(42, 31 + bob, 3, 9);
            g.fillRect(52, 31 + bob, 3, 9);
            if (!back) g.drawArc(44, 31 + bob, 9, 8, 180, 180);
        } else if ("suittop".equals(item)) {
            g.setColor(HIGHLIGHT);
            g.drawLine(42, 34 + bob, 48, 44 + bob);
            g.drawLine(54, 34 + bob, 48, 44 + bob);
            if (!back) {
                g.setColor(OUTLINE);
                g.fillPolygon(poly(47, 36 + bob, 50, 36 + bob, 50, 48 + bob, 48, 51 + bob,
                        47, 48 + bob));
            }
        } else if ("boxytee".equals(item)) {
            g.setColor(SHADOW);
            g.fillRect(40, 58 + bob, 16, 3);
        } else {
            g.setColor(HIGHLIGHT);
            g.drawLine(42, 33 + bob, 54, 33 + bob);
        }
    }

    private static void drawFrontSleeve(
            Graphics2D g, int shoulderX, int shoulderY, int length, boolean left) {
        int sign = left ? -1 : 1;
        int elbowX = shoulderX + sign * 3;
        int endY = shoulderY + length;
        int endX = shoulderX + sign * Math.min(5, length / 5);
        g.setColor(OUTLINE);
        g.fillPolygon(poly(shoulderX, shoulderY, shoulderX + 6, shoulderY + 1,
                elbowX + 5, shoulderY + Math.min(15, length),
                endX + 5, endY, endX, endY + 2,
                elbowX, shoulderY + Math.min(17, length + 2)));
        g.setColor(left ? HIGHLIGHT : SHADOW);
        g.fillPolygon(poly(shoulderX + 2, shoulderY + 2, shoulderX + 4, shoulderY + 3,
                elbowX + 3, shoulderY + Math.min(15, length),
                endX + 3, endY, endX + 1, endY,
                elbowX + 2, shoulderY + Math.min(16, length + 1)));
    }

    private static void drawSideSleeve(
            Graphics2D g, int shoulderX, int shoulderY, int length, boolean far) {
        int endY = shoulderY + length;
        g.setColor(OUTLINE);
        g.fillPolygon(poly(shoulderX, shoulderY, shoulderX + 6, shoulderY + 2,
                shoulderX + 7, shoulderY + Math.min(16, length),
                shoulderX + 6, endY, shoulderX + 1, endY + 2,
                shoulderX, shoulderY + Math.min(17, length + 1)));
        g.setColor(far ? SHADOW : BASE);
        g.fillPolygon(poly(shoulderX + 2, shoulderY + 2, shoulderX + 4, shoulderY + 3,
                shoulderX + 5, shoulderY + Math.min(16, length),
                shoulderX + 4, endY, shoulderX + 2, endY,
                shoulderX + 2, shoulderY + Math.min(16, length)));
    }

    private static BufferedImage bottom(String item, String direction, int step) {
        BufferedImage image = canvas();
        Graphics2D g = graphics(image);
        int bob = step == 0 ? 0 : 1;
        boolean side = "right".equals(direction);
        boolean diagonal = direction.endsWith("_right");
        int nearStride = step == 1 ? 3 : step == 2 ? -3 : 0;
        int farStride = -nearStride;
        int length = ("shorts".equals(item) || "jorts".equals(item) || "swimshorts".equals(item))
                ? 80 : 90;
        boolean baggy = "baggyjeans".equals(item) || "sweatpants".equals(item);

        g.setColor(OUTLINE);
        g.fillPolygon(poly(39, 58 + bob, 57, 58 + bob, 56, 70 + bob,
                50, 72 + bob, 46, 72 + bob, 40, 69 + bob));
        g.setColor(BASE);
        g.fillPolygon(poly(41, 60 + bob, 55, 60 + bob, 54, 68 + bob,
                50, 70 + bob, 46, 70 + bob, 42, 67 + bob));

        int farHip = side ? 43 : diagonal ? 42 : 41;
        int nearHip = 49;
        drawPantLeg(g, farHip, 68 + bob, side || diagonal ? farStride : -farStride,
                length + bob, true, baggy);
        drawPantLeg(g, nearHip, 68 + bob, nearStride, length + bob, false, baggy);

        g.setColor(HIGHLIGHT);
        g.fillRect(42, 60 + bob, 12, 2);
        g.setColor(SHADOW);
        if ("jorts".equals(item)) {
            g.drawLine(42, 77 + bob, 47, 77 + bob);
            g.drawLine(50, 77 + bob, 55, 77 + bob);
        } else if ("sweatpants".equals(item)) {
            g.drawLine(46, 61 + bob, 48, 66 + bob);
            g.drawLine(50, 61 + bob, 49, 66 + bob);
        } else if ("suitbottom".equals(item)) {
            g.drawLine(44, 69 + bob, 44 + farStride, length - 2 + bob);
            g.drawLine(52, 69 + bob, 52 + nearStride, length - 2 + bob);
        } else if ("swimshorts".equals(item)) {
            g.fillRect(43, 64 + bob, 2, 2);
            g.fillRect(51, 69 + bob, 2, 2);
            g.setColor(HIGHLIGHT);
            g.fillRect(48, 62 + bob, 1, 2);
        }
        g.dispose();
        return image;
    }

    private static void drawPantLeg(
            Graphics2D g, int hipX, int hipY, int stride, int endY, boolean far, boolean baggy) {
        int kneeX = hipX + stride / 2;
        int footX = hipX + stride;
        int width = baggy ? 9 : 7;
        g.setColor(OUTLINE);
        g.fillPolygon(poly(hipX, hipY, hipX + width, hipY, kneeX + width, Math.min(79, endY),
                footX + width, endY, footX - (baggy ? 2 : 1), endY,
                kneeX, Math.min(79, endY)));
        g.setColor(far ? SHADOW : BASE);
        g.fillPolygon(poly(hipX + 2, hipY + 1, hipX + width - 2, hipY + 1,
                kneeX + width - 2, Math.min(78, endY - 1), footX + width - 2, endY - 2,
                footX + 1, endY - 2, kneeX + 2, Math.min(78, endY - 1)));
        if (!far) {
            g.setColor(HIGHLIGHT);
            g.drawLine(hipX + 2, hipY + 2, footX + 2, endY - 3);
        }
    }

    private static BufferedImage accessory(String item, String direction, boolean rear) {
        BufferedImage image = canvas();
        Graphics2D g = graphics(image);
        boolean side = "right".equals(direction);
        boolean diagonal = direction.endsWith("_right");
        boolean back = direction.startsWith("back");
        if (rear) {
            if ("cape".equals(item)) {
                int left = side ? 39 : diagonal ? 36 : 34;
                int right = side ? 59 : diagonal ? 61 : 62;
                g.setColor(OUTLINE);
                g.fillPolygon(poly(left + 5, 31, right - 4, 31, right, 72, 50, 84,
                        left, 72));
                g.setColor(BASE);
                g.fillPolygon(poly(left + 7, 33, right - 6, 33, right - 2, 70, 50, 80,
                        left + 2, 70));
                g.setColor(HIGHLIGHT);
                g.fillPolygon(poly(left + 7, 34, left + 11, 34, 48, 77, 43, 72));
                g.setColor(SHADOW);
                g.fillPolygon(poly(right - 10, 34, right - 6, 34, right - 3, 69,
                        51, 79, 51, 72));
            }
            g.dispose();
            return image;
        }

        switch (item) {
            case "crown" -> {
                g.setColor(OUTLINE);
                g.fillPolygon(poly(37, 8, 38, 0, 43, 5, 48, 0, 53, 5, 59, 0, 60, 10));
                g.setColor(BASE);
                g.fillPolygon(poly(39, 8, 40, 3, 44, 7, 48, 3, 53, 7, 57, 3, 58, 9));
                g.setColor(HIGHLIGHT);
                g.fillRect(40, 6, 17, 2);
            }
            case "fedora" -> {
                g.setColor(OUTLINE);
                g.fillOval(side ? 35 : 33, 2, side ? 30 : 32, 11);
                g.fillRect(side ? 38 : 36, 0, 23, 10);
                g.setColor(BASE);
                g.fillOval(side ? 37 : 35, 4, side ? 26 : 28, 6);
                g.fillRect(side ? 40 : 38, 2, 19, 6);
                g.setColor(SHADOW);
                g.fillRect(side ? 39 : 37, 7, 23, 3);
            }
            case "headband", "ninjaheadband" -> {
                g.setColor(OUTLINE);
                g.fillRect(side ? 38 : 36, 11, side ? 24 : 25, 6);
                g.setColor(BASE);
                g.fillRect(side ? 39 : 37, 12, side ? 22 : 23, 4);
                if ("ninjaheadband".equals(item)) {
                    g.setColor(HIGHLIGHT);
                    g.fillRect(side ? 48 : 43, 11, 11, 6);
                    g.setColor(OUTLINE);
                    g.drawRect(side ? 50 : 46, 13, 5, 2);
                }
            }
            case "glasses", "shades", "aviators" -> drawGlasses(g, item, side, diagonal);
            case "allseeingeye" -> {
                g.setColor(OUTLINE);
                g.fillOval(side ? 51 : 44, 8, side ? 8 : 10, 7);
                g.setColor(BASE);
                g.fillOval(side ? 52 : 45, 9, side ? 6 : 8, 5);
                g.setColor(HIGHLIGHT);
                g.fillRect(side ? 55 : 48, 10, 2, 2);
                g.setColor(OUTLINE);
                g.fillRect(side ? 56 : 50, 11, 1, 2);
            }
            case "cape" -> {
                g.setColor(HIGHLIGHT);
                g.fillRect(back ? 41 : 39, 31, back ? 15 : 18, 3);
                g.setColor(OUTLINE);
                g.drawRect(back ? 40 : 38, 30, back ? 17 : 20, 4);
            }
            default -> {
                // No front layer.
            }
        }
        g.dispose();
        return image;
    }

    private static void drawGlasses(Graphics2D g, String item, boolean side, boolean diagonal) {
        int x = side ? 52 : diagonal ? 43 : 40;
        g.setColor(OUTLINE);
        if (side) {
            g.drawRect(x, 15, 8, 6);
            g.drawLine(x + 8, 16, x + 11, 15);
        } else {
            g.drawRect(x, 15, 7, 6);
            g.drawRect(x + 10, 15, 7, 6);
            g.drawLine(x + 7, 17, x + 10, 17);
        }
        if ("shades".equals(item)) {
            g.fillRect(x + 1, 16, side ? 6 : 5, 4);
            if (!side) g.fillRect(x + 11, 16, 5, 4);
        } else {
            g.setColor("aviators".equals(item) ? SHADOW : BASE);
            g.fillRect(x + 1, 16, side ? 6 : 5, 3);
            if (!side) g.fillRect(x + 11, 16, 5, 3);
            g.setColor(HIGHLIGHT);
            g.fillRect(x + 1, 16, 2, 1);
            if (!side) g.fillRect(x + 11, 16, 2, 1);
        }
    }

    private static BufferedImage canvas() {
        return new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
    }

    private static BufferedImage combine(BufferedImage rear, BufferedImage front) {
        BufferedImage combined = canvas();
        Graphics2D g = combined.createGraphics();
        g.drawImage(rear, 0, 0, null);
        g.drawImage(front, 0, 0, null);
        g.dispose();
        return combined;
    }

    private static Graphics2D graphics(BufferedImage image) {
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        return g;
    }

    private static void write(BufferedImage image, String path) throws Exception {
        File file = new File(path);
        file.getParentFile().mkdirs();
        ImageIO.write(image, "png", file);
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
}
