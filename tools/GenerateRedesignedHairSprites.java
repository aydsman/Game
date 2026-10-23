import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** Generates the fifteen three-tone hairstyles for the redesigned 96x96 male head. */
public final class GenerateRedesignedHairSprites {
    private static final int SIZE = 96;
    private static final Color OUTLINE = new Color(0x2B, 0x29, 0x30);
    private static final Color HIGHLIGHT = new Color(0xDD, 0xDD, 0xDD);
    private static final Color BASE = new Color(0x99, 0x99, 0x99);
    private static final Color SHADOW = new Color(0x55, 0x55, 0x55);
    private static final String[] STYLES = {
        "fringe", "buzzcut", "middle_part", "side_part", "curtains",
        "textured_crop", "crew_cut", "slick_back", "messy_hair", "caesar_cut",
        "undercut", "faux_hawk", "long_flow", "curly_top", "mohawk"
    };
    private static final String[] DIRECTIONS = {
        "front", "front_right", "right", "back_right", "back"
    };

    private GenerateRedesignedHairSprites() {}

    public static void main(String[] args) throws Exception {
        for (String style : STYLES) {
            for (String direction : DIRECTIONS) {
                BufferedImage front = canvas();
                Graphics2D frontGraphics = graphics(front);
                drawFront(frontGraphics, style, direction);
                frontGraphics.dispose();
                writeBoth(front, style + "_" + direction + ".png");

                BufferedImage rear = canvas();
                Graphics2D rearGraphics = graphics(rear);
                drawRear(rearGraphics, style, direction);
                rearGraphics.dispose();
                writeBoth(rear, style + "_" + direction + "_rear.png");
            }
        }
        System.out.println("Generated " + STYLES.length + " hairstyles in five native directions.");
    }

    private static void drawFront(Graphics2D g, String style, String direction) {
        boolean back = direction.startsWith("back") || "back".equals(direction);
        switch (style) {
            case "buzzcut" -> drawBuzzcut(g, direction);
            case "middle_part" -> drawMiddlePart(g, direction, false);
            case "side_part" -> drawSidePart(g, direction);
            case "curtains" -> drawMiddlePart(g, direction, true);
            case "textured_crop" -> drawTexturedCrop(g, direction);
            case "crew_cut" -> drawCrewCut(g, direction);
            case "slick_back" -> drawSlickBack(g, direction);
            case "messy_hair" -> drawMessy(g, direction);
            case "caesar_cut" -> drawCaesar(g, direction);
            case "undercut" -> drawUndercut(g, direction);
            case "faux_hawk" -> drawFauxHawk(g, direction);
            case "long_flow" -> drawLongFlowFront(g, direction);
            case "curly_top" -> drawCurlyTop(g, direction);
            case "mohawk" -> drawMohawk(g, direction);
            default -> drawFringe(g, direction);
        }
        if (back) {
            addBackTexture(g, style, direction);
        }
    }

    private static void drawRear(Graphics2D g, String style, String direction) {
        if (!"long_flow".equals(style)) {
            return;
        }
        int shift = "right".equals(direction) ? 2 : direction.endsWith("_right") ? 1 : 0;
        g.setColor(OUTLINE);
        g.fillPolygon(poly(39 + shift, 10, 56 + shift, 9, 61 + shift, 15,
                61 + shift, 31, 58 + shift, 39, 54 + shift, 34,
                49 + shift, 40, 43 + shift, 35, 37 + shift, 39,
                35 + shift, 30, 36 + shift, 15));
        g.setColor(BASE);
        g.fillPolygon(poly(40 + shift, 11, 55 + shift, 10, 59 + shift, 16,
                59 + shift, 30, 57 + shift, 36, 53 + shift, 32,
                49 + shift, 37, 44 + shift, 32, 39 + shift, 36,
                37 + shift, 29, 38 + shift, 16));
        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(40 + shift, 13, 43 + shift, 11, 44 + shift, 31,
                42 + shift, 34, 39 + shift, 29));
        g.setColor(SHADOW);
        g.fillPolygon(poly(55 + shift, 11, 59 + shift, 16, 59 + shift, 30,
                57 + shift, 35, 54 + shift, 31));
    }

    private static void drawCap(Graphics2D g, String direction, int top, int hairline) {
        int shift = capShift(direction);
        int left = 39 + shift;
        int right = 57 + shift;
        if ("right".equals(direction)) {
            left = 40;
            right = 60;
        }
        g.setColor(OUTLINE);
        g.fillPolygon(poly(left, hairline, left, 9, left + 4, 4, left + 9, top,
                right - 4, 4, right, 8, right + 1, hairline + 1,
                right - 2, hairline, right - 4, hairline - 2,
                left + 5, hairline - 1, left + 2, hairline + 1));
        g.setColor(BASE);
        g.fillPolygon(poly(left + 2, hairline - 1, left + 2, 9, left + 5, 6,
                left + 9, top + 2, right - 5, 6, right - 2, 9,
                right - 1, hairline - 1, right - 4, hairline - 2,
                left + 6, hairline - 2));
        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(left + 3, 9, left + 6, 6, left + 10, top + 2,
                left + 11, 6, left + 7, 9));
        g.setColor(SHADOW);
        g.fillPolygon(poly(right - 6, 5, right - 2, 8, right - 1, hairline - 1,
                right - 4, hairline - 2, right - 6, 9));
    }

    private static void drawFringe(Graphics2D g, String direction) {
        drawCap(g, direction, 2, 10);
        if (isBack(direction)) return;
        int s = shift(direction);
        g.setColor(OUTLINE);
        g.fillPolygon(poly(40 + s, 9, 44 + s, 8, 44 + s, 12, 47 + s, 9,
                49 + s, 13, 52 + s, 9, 54 + s, 12, 57 + s, 9, 57 + s, 12,
                53 + s, 13, 49 + s, 12, 45 + s, 13, 40 + s, 12));
        g.setColor(BASE);
        g.fillPolygon(poly(41 + s, 9, 44 + s, 9, 44 + s, 11, 47 + s, 9,
                49 + s, 12, 52 + s, 9, 54 + s, 11, 56 + s, 10, 56 + s, 11,
                53 + s, 12, 49 + s, 11, 45 + s, 12, 41 + s, 11));
    }

    private static void drawBuzzcut(Graphics2D g, String direction) {
        drawCap(g, direction, 4, 11);
        int s = capShift(direction);
        g.setColor(SHADOW);
        g.fillRect(40 + s, 10, 2, 4);
        g.fillRect(55 + s, 9, 2, 5);
        g.setColor(HIGHLIGHT);
        g.fillRect(44 + s, 6, 5, 1);
    }

    private static void drawMiddlePart(Graphics2D g, String direction, boolean curtains) {
        drawCap(g, direction, 2, 10);
        if (isBack(direction)) return;
        int s = shift(direction);
        clear(g, poly(47 + s, 7, 49 + s, 7, 50 + s, curtains ? 13 : 11,
                48 + s, curtains ? 12 : 10));
        g.setColor(OUTLINE);
        g.drawLine(47 + s, 7, 46 + s, curtains ? 13 : 11);
        g.drawLine(49 + s, 7, 51 + s, curtains ? 13 : 11);
        g.setColor(BASE);
        if (curtains) {
            g.fillPolygon(poly(40 + s, 9, 45 + s, 8, 46 + s, 13,
                    43 + s, 15, 40 + s, 13));
            g.fillPolygon(poly(51 + s, 8, 57 + s, 9, 57 + s, 13,
                    54 + s, 15, 51 + s, 13));
        }
        g.setColor(HIGHLIGHT);
        g.drawLine(43 + s, 8, 46 + s, 12);
        g.setColor(SHADOW);
        g.drawLine(53 + s, 8, 51 + s, 13);
    }

    private static void drawSidePart(Graphics2D g, String direction) {
        drawCap(g, direction, 2, 10);
        if (isBack(direction)) return;
        int s = shift(direction);
        clear(g, poly(43 + s, 8, 45 + s, 8, 44 + s, 13, 42 + s, 14));
        g.setColor(OUTLINE);
        g.fillPolygon(poly(44 + s, 8, 55 + s, 8, 58 + s, 10,
                56 + s, 13, 52 + s, 10, 47 + s, 11, 43 + s, 10));
        g.setColor(BASE);
        g.fillPolygon(poly(45 + s, 9, 54 + s, 9, 56 + s, 10,
                55 + s, 12, 52 + s, 9, 47 + s, 10, 44 + s, 9));
        g.setColor(HIGHLIGHT);
        g.drawLine(46 + s, 9, 53 + s, 11);
    }

    private static void drawTexturedCrop(Graphics2D g, String direction) {
        drawCap(g, direction, 1, 10);
        if (isBack(direction)) return;
        int s = capShift(direction);
        g.setColor(OUTLINE);
        for (int x = 40; x <= 56; x += 4) {
            g.fillRect(x + s, 8 + ((x / 4) % 2), 4, 4);
        }
        g.setColor(BASE);
        for (int x = 41; x <= 55; x += 4) {
            g.fillRect(x + s, 9 + ((x / 4) % 2), 3, 2);
        }
    }

    private static void drawCrewCut(Graphics2D g, String direction) {
        drawCap(g, direction, 3, 9);
        int s = capShift(direction);
        g.setColor(OUTLINE);
        g.fillRect(42 + s, 3, 13, 3);
        g.setColor(BASE);
        g.fillRect(43 + s, 4, 11, 2);
        g.setColor(HIGHLIGHT);
        g.fillRect(44 + s, 4, 5, 1);
    }

    private static void drawSlickBack(Graphics2D g, String direction) {
        drawCap(g, direction, 1, 9);
        int s = capShift(direction);
        g.setColor(OUTLINE);
        g.fillPolygon(poly(40 + s, 7, 43 + s, 2, 49 + s, 0, 56 + s, 3,
                59 + s, 7, 57 + s, 10, 52 + s, 7, 46 + s, 8));
        g.setColor(BASE);
        g.fillPolygon(poly(42 + s, 6, 44 + s, 3, 49 + s, 2, 55 + s, 4,
                57 + s, 7, 56 + s, 8, 52 + s, 6, 46 + s, 7));
        g.setColor(HIGHLIGHT);
        g.drawLine(44 + s, 4, 52 + s, 3);
        g.drawLine(43 + s, 6, 51 + s, 5);
    }

    private static void drawMessy(Graphics2D g, String direction) {
        drawCap(g, direction, 1, 10);
        int s = capShift(direction);
        g.setColor(OUTLINE);
        g.fillPolygon(poly(39 + s, 10, 37 + s, 6, 42 + s, 7, 41 + s, 2,
                46 + s, 5, 48 + s, 0, 51 + s, 5, 56 + s, 1,
                55 + s, 7, 61 + s, 6, 57 + s, 11));
        g.setColor(BASE);
        g.fillPolygon(poly(41 + s, 9, 40 + s, 7, 44 + s, 8, 43 + s, 4,
                47 + s, 6, 48 + s, 2, 50 + s, 7, 54 + s, 3,
                53 + s, 8, 58 + s, 8, 56 + s, 10));
    }

    private static void drawCaesar(Graphics2D g, String direction) {
        drawCap(g, direction, 3, 10);
        if (isBack(direction)) return;
        int s = shift(direction);
        g.setColor(OUTLINE);
        g.fillRect(40 + s, 9, 17, 3);
        g.setColor(BASE);
        g.fillRect(41 + s, 9, 15, 2);
        g.setColor(HIGHLIGHT);
        g.fillRect(42 + s, 9, 6, 1);
        g.setColor(SHADOW);
        g.fillRect(52 + s, 10, 4, 1);
    }

    private static void drawUndercut(Graphics2D g, String direction) {
        int s = capShift(direction);
        g.setColor(OUTLINE);
        g.fillRect(40 + s, 8, 3, 5);
        g.fillRect(55 + s, 8, 3, 5);
        g.fillPolygon(poly(42 + s, 8, 43 + s, 3, 48 + s, 1,
                56 + s, 3, 59 + s, 7, 57 + s, 10));
        g.setColor(SHADOW);
        g.fillRect(41 + s, 10, 2, 2);
        g.fillRect(55 + s, 10, 2, 2);
        g.setColor(BASE);
        g.fillPolygon(poly(43 + s, 8, 44 + s, 4, 48 + s, 2,
                55 + s, 4, 57 + s, 7, 56 + s, 9));
        g.setColor(HIGHLIGHT);
        g.fillRect(45 + s, 3, 6, 2);
    }

    private static void drawFauxHawk(Graphics2D g, String direction) {
        drawBuzzcut(g, direction);
        int s = capShift(direction);
        g.setColor(OUTLINE);
        g.fillPolygon(poly(44 + s, 7, 45 + s, 2, 48 + s, 4, 50 + s, 0,
                52 + s, 4, 55 + s, 2, 54 + s, 8));
        g.setColor(BASE);
        g.fillPolygon(poly(46 + s, 7, 46 + s, 4, 48 + s, 5, 50 + s, 2,
                51 + s, 5, 53 + s, 4, 52 + s, 7));
        g.setColor(HIGHLIGHT);
        g.fillRect(49 + s, 2, 1, 4);
    }

    private static void drawLongFlowFront(Graphics2D g, String direction) {
        drawMiddlePart(g, direction, true);
        int s = shift(direction);
        g.setColor(OUTLINE);
        g.fillPolygon(poly(38 + s, 12, 42 + s, 12, 42 + s, 27,
                40 + s, 32, 37 + s, 29));
        g.fillPolygon(poly(55 + s, 11, 59 + s, 12, 61 + s, 28,
                58 + s, 32, 55 + s, 26));
        g.setColor(BASE);
        g.fillPolygon(poly(39 + s, 13, 41 + s, 13, 41 + s, 26,
                40 + s, 29, 38 + s, 28));
        g.fillPolygon(poly(56 + s, 12, 58 + s, 13, 59 + s, 27,
                58 + s, 29, 56 + s, 25));
        g.setColor(HIGHLIGHT);
        g.fillRect(39 + s, 15, 1, 11);
        g.setColor(SHADOW);
        g.fillRect(58 + s, 15, 1, 11);
    }

    private static void drawCurlyTop(Graphics2D g, String direction) {
        int s = capShift(direction);
        int[][] curls = {
            {39, 7}, {42, 3}, {46, 1}, {50, 2}, {54, 3}, {57, 7},
            {40, 11}, {45, 7}, {50, 7}, {55, 10}
        };
        g.setColor(OUTLINE);
        for (int[] curl : curls) g.fillOval(curl[0] + s - 1, curl[1] - 1, 7, 7);
        g.setColor(BASE);
        for (int[] curl : curls) g.fillOval(curl[0] + s, curl[1], 5, 5);
        g.setColor(HIGHLIGHT);
        g.fillRect(43 + s, 4, 2, 2);
        g.fillRect(48 + s, 2, 2, 2);
        g.setColor(SHADOW);
        g.fillRect(56 + s, 8, 3, 4);
    }

    private static void drawMohawk(Graphics2D g, String direction) {
        drawBuzzcut(g, direction);
        int s = capShift(direction);
        if ("right".equals(direction)) {
            g.setColor(OUTLINE);
            g.fillPolygon(poly(41, 6, 43, 0, 46, 4, 49, 0, 52, 4,
                    55, 0, 58, 6));
            g.setColor(BASE);
            g.fillPolygon(poly(43, 6, 44, 2, 46, 5, 49, 2, 52, 5,
                    55, 2, 56, 6));
        } else {
            g.setColor(OUTLINE);
            g.fillPolygon(poly(45 + s, 7, 46 + s, 0, 49 + s, 3,
                    51 + s, 0, 53 + s, 7));
            g.setColor(BASE);
            g.fillPolygon(poly(47 + s, 7, 47 + s, 2, 49 + s, 5,
                    50 + s, 2, 51 + s, 7));
            g.setColor(HIGHLIGHT);
            g.fillRect(47 + s, 2, 1, 4);
        }
    }

    private static void addBackTexture(Graphics2D g, String style, String direction) {
        int s = capShift(direction);
        boolean closeCut = "buzzcut".equals(style) || "crew_cut".equals(style)
                || "caesar_cut".equals(style) || "undercut".equals(style);
        int bottom = closeCut ? 16 : 19;
        int left = 40 + s;
        int right = 58 + s;

        // Wrap the hair around the occipital curve. A previous rectangular
        // texture strip crossed the back of the head like a brown visor.
        g.setColor(OUTLINE);
        g.fillPolygon(poly(left, 8, left + 3, 5, right - 4, 5, right, 9,
                right, bottom - 3, right - 3, bottom, left + 4, bottom,
                left, bottom - 3));
        g.setColor(closeCut ? SHADOW : BASE);
        g.fillPolygon(poly(left + 2, 9, left + 4, 7, right - 5, 7,
                right - 2, 9, right - 2, bottom - 4, right - 4, bottom - 2,
                left + 5, bottom - 2, left + 2, bottom - 4));
        g.setColor(HIGHLIGHT);
        g.drawLine(left + 3, 8, left + 7, 6);
        g.setColor(SHADOW);
        g.drawLine(right - 5, 7, right - 2, 10);
        if (!closeCut) {
            g.drawLine(right - 3, 11, right - 3, bottom - 3);
        }
    }

    private static int shift(String direction) {
        // In a three-quarter view the facial centerline and the front hairline
        // wrap toward the visible side of the skull. The left-facing asset is
        // mirrored at runtime, preserving the same perspective.
        return direction.endsWith("_right") ? 2 : 0;
    }

    private static int capShift(String direction) {
        return direction.endsWith("_right") ? 1 : 0;
    }

    private static boolean isBack(String direction) {
        return direction.startsWith("back") || "back".equals(direction);
    }

    private static void clear(Graphics2D g, Polygon shape) {
        g.setComposite(AlphaComposite.Clear);
        g.fillPolygon(shape);
        g.setComposite(AlphaComposite.SrcOver);
    }

    private static BufferedImage canvas() {
        return new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
    }

    private static Graphics2D graphics(BufferedImage image) {
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        return g;
    }

    private static void writeBoth(BufferedImage image, String fileName) throws Exception {
        write(image, "assets/player/body/man/redesign/features/hair/" + fileName);
        write(image, "assets/player/shared/hair/" + fileName);
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
