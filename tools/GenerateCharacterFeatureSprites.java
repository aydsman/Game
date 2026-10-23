import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** Generates face and hair layers aligned to the 96x96 male body. */
public final class GenerateCharacterFeatureSprites {
    private static final int SIZE = 96;
    private static final Color OUTLINE = new Color(0x2B, 0x29, 0x30);
    private static final Color EYE_WHITE = new Color(0xF2, 0xEE, 0xE8);
    private static final Color EYE_HIGHLIGHT = new Color(0x8F, 0xCB, 0xFF);
    private static final Color EYE_BASE = new Color(0x4A, 0x82, 0xB8);
    private static final Color EYE_SHADOW = new Color(0x24, 0x49, 0x6B);
    private static final Color MOUTH = new Color(0x63, 0x35, 0x39);
    private static final Color MOUTH_LIGHT = new Color(0xC9, 0x72, 0x78);
    private static final Color HAIR_HIGHLIGHT = new Color(0xDD, 0xDD, 0xDD);
    private static final Color HAIR_BASE = new Color(0x99, 0x99, 0x99);
    private static final Color HAIR_SHADOW = new Color(0x55, 0x55, 0x55);

    private GenerateCharacterFeatureSprites() {}

    public static void main(String[] args) throws Exception {
        for (String style : new String[] {"round", "narrow", "wide", "almond"}) {
            writeEyes(style, false);
            writeEyes(style, true);
            writeDiagonalEyes(style);
        }
        for (String expression : new String[] {"neutral", "smile", "open", "sad", "surprised"}) {
            writeMouth(expression, false);
            writeMouth(expression, true);
            writeDiagonalMouth(expression);
        }
        for (String style : new String[] {"short", "long", "buzzcut", "ponytail"}) {
            for (String direction : new String[] {"front", "front_right", "right", "back_right", "back"}) {
                writeHair(style, direction, false);
                writeHair(style, direction, true);
            }
        }
        for (String marking : new String[] {"scar1", "freckles", "tattoo1"}) {
            for (String direction : new String[] {"front", "front_right", "right"}) {
                writeFaceMarking(marking, direction);
            }
        }
        for (String marking : new String[] {"scar1", "tattoo1", "tattoo2"}) {
            for (String direction : new String[] {"front", "front_right", "right", "back_right", "back"}) {
                writeBodyMarking(marking, direction);
            }
        }
    }

    private static void writeEyes(String style, boolean side) throws Exception {
        BufferedImage image = canvas();
        Graphics2D g = graphics(image);
        if (side) {
            drawEye(g, style, 53, 16, true);
        } else {
            drawEye(g, style, 42, 16, false);
            drawEye(g, style, 51, 16, false);
        }
        g.dispose();
        write(image, "assets/player/shared/eyes/" + style + "_" + (side ? "right" : "front") + ".png");
    }

    private static void writeDiagonalEyes(String style) throws Exception {
        BufferedImage image = canvas();
        Graphics2D g = graphics(image);
        drawEye(g, style, 43, 16, true);
        drawEye(g, style, 52, 16, false);
        g.dispose();
        write(image, "assets/player/shared/eyes/" + style + "_front_right.png");
    }

    private static void drawEye(Graphics2D g, String style, int x, int y, boolean side) {
        switch (style) {
            case "narrow" -> {
                g.setColor(OUTLINE);
                g.fillRect(x - 1, y, side ? 5 : 6, 3);
                g.setColor(EYE_WHITE);
                g.fillRect(x, y + 1, side ? 3 : 4, 1);
            }
            case "wide" -> {
                g.setColor(OUTLINE);
                g.fillRect(x - 1, y - 1, side ? 6 : 7, 6);
                g.setColor(EYE_WHITE);
                g.fillRect(x, y, side ? 4 : 5, 4);
            }
            case "almond" -> {
                g.setColor(OUTLINE);
                g.fillPolygon(poly(x - 1, y + 2, x + 2, y - 1, x + 6, y + 2, x + 2, y + 5));
                g.setColor(EYE_WHITE);
                g.fillPolygon(poly(x, y + 2, x + 2, y, x + 5, y + 2, x + 2, y + 4));
            }
            default -> {
                g.setColor(OUTLINE);
                g.fillOval(x - 1, y - 1, side ? 6 : 7, 7);
                g.setColor(EYE_WHITE);
                g.fillOval(x, y, side ? 4 : 5, 5);
            }
        }

        int irisX = x + 2;
        int irisY = y + 1;
        switch (style) {
            case "narrow" -> {
                // Small horizontal iris with a sharp single-pixel pupil.
                g.setColor(EYE_SHADOW);
                g.fillRect(irisX - 1, irisY, 3, 1);
                g.setColor(EYE_BASE);
                g.fillRect(irisX, irisY, 2, 1);
                g.setColor(EYE_HIGHLIGHT);
                g.fillRect(irisX, irisY, 1, 1);
                g.setColor(OUTLINE);
                g.fillRect(irisX + 1, irisY, 1, 1);
            }
            case "wide" -> {
                // Large iris with a tall pupil to emphasize the open eye.
                g.setColor(EYE_SHADOW);
                g.fillRect(irisX - 1, irisY - 1, 4, 5);
                g.setColor(EYE_BASE);
                g.fillRect(irisX, irisY - 1, 2, 4);
                g.setColor(EYE_HIGHLIGHT);
                g.fillRect(irisX, irisY - 1, 1, 1);
                g.setColor(OUTLINE);
                g.fillRect(irisX + 1, irisY, 1, 3);
            }
            case "almond" -> {
                // Diamond iris and angled two-pixel pupil follow the almond silhouette.
                g.setColor(EYE_SHADOW);
                g.fillPolygon(poly(irisX - 1, irisY + 1, irisX + 1, irisY - 1,
                        irisX + 3, irisY + 1, irisX + 1, irisY + 3));
                g.setColor(EYE_BASE);
                g.fillRect(irisX, irisY, 2, 2);
                g.setColor(EYE_HIGHLIGHT);
                g.fillRect(irisX, irisY, 1, 1);
                g.setColor(OUTLINE);
                g.fillRect(irisX + 1, irisY + 1, 1, 2);
            }
            default -> {
                // Round iris with a centered compact pupil.
                g.setColor(EYE_SHADOW);
                g.fillOval(irisX - 1, irisY - 1, 4, 5);
                g.setColor(EYE_BASE);
                g.fillOval(irisX, irisY - 1, 3, 4);
                g.setColor(EYE_HIGHLIGHT);
                g.fillRect(irisX, irisY, 1, 1);
                g.setColor(OUTLINE);
                g.fillRect(irisX + 1, irisY + 1, 1, 2);
            }
        }
    }

    private static void writeMouth(String expression, boolean side) throws Exception {
        BufferedImage image = canvas();
        Graphics2D g = graphics(image);
        int x = side ? 56 : 45;
        int y = 24;
        switch (expression) {
            case "smile" -> {
                g.setColor(MOUTH);
                g.fillRect(x, y, side ? 4 : 7, 2);
                g.fillRect(side ? x + 3 : x + 1, y + 2, side ? 1 : 5, 1);
                g.setColor(MOUTH_LIGHT);
                g.fillRect(side ? x + 2 : x + 2, y + 1, side ? 2 : 3, 1);
            }
            case "open" -> {
                g.setColor(OUTLINE);
                g.fillOval(x, y - 1, side ? 5 : 7, 6);
                g.setColor(MOUTH);
                g.fillOval(x + 1, y, side ? 3 : 5, 4);
                g.setColor(MOUTH_LIGHT);
                g.fillRect(x + 2, y + 3, side ? 1 : 3, 1);
            }
            case "sad" -> {
                g.setColor(MOUTH);
                g.fillRect(x, y + 2, side ? 4 : 7, 1);
                g.fillRect(side ? x + 3 : x + 1, y + 1, side ? 1 : 5, 1);
            }
            case "surprised" -> {
                g.setColor(OUTLINE);
                g.fillOval(x + 1, y - 1, side ? 4 : 5, 6);
                g.setColor(MOUTH);
                g.fillOval(x + 2, y, side ? 2 : 3, 4);
            }
            default -> {
                g.setColor(MOUTH);
                g.fillRect(x, y, side ? 4 : 7, 2);
                g.setColor(MOUTH_LIGHT);
                g.fillRect(x + 1, y, side ? 2 : 4, 1);
            }
        }
        g.dispose();
        write(image, "assets/player/shared/mouth/" + expression + "_" + (side ? "right" : "front") + ".png");
    }

    private static void writeDiagonalMouth(String expression) throws Exception {
        BufferedImage image = canvas();
        Graphics2D g = graphics(image);
        int x = 48;
        int y = 24;
        switch (expression) {
            case "smile" -> {
                g.setColor(MOUTH);
                g.fillRect(x, y, 6, 2);
                g.fillRect(x + 2, y + 2, 4, 1);
                g.setColor(MOUTH_LIGHT);
                g.fillRect(x + 2, y + 1, 3, 1);
            }
            case "open" -> {
                g.setColor(OUTLINE);
                g.fillOval(x, y - 1, 6, 6);
                g.setColor(MOUTH);
                g.fillOval(x + 1, y, 4, 4);
                g.setColor(MOUTH_LIGHT);
                g.fillRect(x + 2, y + 3, 2, 1);
            }
            case "sad" -> {
                g.setColor(MOUTH);
                g.fillRect(x, y + 2, 6, 1);
                g.fillRect(x + 2, y + 1, 4, 1);
            }
            case "surprised" -> {
                g.setColor(OUTLINE);
                g.fillOval(x + 1, y - 1, 5, 6);
                g.setColor(MOUTH);
                g.fillOval(x + 2, y, 3, 4);
            }
            default -> {
                g.setColor(MOUTH);
                g.fillRect(x, y, 6, 2);
                g.setColor(MOUTH_LIGHT);
                g.fillRect(x + 2, y, 3, 1);
            }
        }
        g.dispose();
        write(image, "assets/player/shared/mouth/" + expression + "_front_right.png");
    }

    private static void writeFaceMarking(String marking, String direction) throws Exception {
        BufferedImage image = canvas();
        Graphics2D g = graphics(image);
        boolean side = "right".equals(direction);
        boolean diagonal = "front_right".equals(direction);
        switch (marking) {
            case "scar1" -> {
                g.setColor(new Color(0x8A, 0x3F, 0x45));
                if (side) {
                    g.drawLine(55, 17, 59, 23);
                    g.drawLine(56, 19, 59, 18);
                } else if (diagonal) {
                    g.drawLine(44, 17, 49, 24);
                    g.drawLine(45, 20, 49, 19);
                } else {
                    g.drawLine(40, 17, 45, 24);
                    g.drawLine(41, 20, 45, 19);
                }
            }
            case "freckles" -> {
                g.setColor(new Color(0x8A, 0x5A, 0x3D));
                if (side) {
                    g.fillRect(55, 21, 1, 1);
                    g.fillRect(58, 21, 1, 1);
                    g.fillRect(57, 23, 1, 1);
                } else if (diagonal) {
                    int[] xs = {44, 47, 50, 53, 56};
                    int[] ys = {22, 21, 23, 21, 22};
                    for (int i = 0; i < xs.length; i++) {
                        g.fillRect(xs[i], ys[i], 1, 1);
                    }
                } else {
                    int[] xs = {40, 43, 45, 51, 53, 56};
                    int[] ys = {22, 21, 23, 23, 21, 22};
                    for (int i = 0; i < xs.length; i++) {
                        g.fillRect(xs[i], ys[i], 1, 1);
                    }
                }
            }
            case "tattoo1" -> {
                g.setColor(new Color(0x34, 0x45, 0x67));
                if (side) {
                    g.drawArc(53, 15, 8, 10, 250, 150);
                    g.fillRect(57, 20, 3, 1);
                } else if (diagonal) {
                    g.drawArc(43, 15, 9, 10, 250, 165);
                    g.fillRect(45, 20, 5, 1);
                    g.fillRect(47, 22, 2, 1);
                } else {
                    g.drawArc(39, 15, 9, 10, 250, 170);
                    g.fillRect(40, 20, 5, 1);
                    g.fillRect(42, 22, 2, 1);
                }
            }
            default -> {
                // "none" is represented by not loading a layer.
            }
        }
        g.dispose();
        write(image, "assets/player/shared/face_markings/" + marking + "_" + direction + ".png");
    }

    private static void writeBodyMarking(String marking, String direction) throws Exception {
        BufferedImage image = canvas();
        Graphics2D g = graphics(image);
        boolean side = "right".equals(direction);
        boolean back = "back".equals(direction);
        boolean diagonalFront = "front_right".equals(direction);
        boolean diagonalBack = "back_right".equals(direction);
        switch (marking) {
            case "scar1" -> {
                g.setColor(new Color(0x8A, 0x3F, 0x45));
                if (side) {
                    g.drawLine(49, 39, 54, 54);
                    g.drawLine(50, 44, 54, 42);
                } else if (diagonalFront || diagonalBack) {
                    g.drawLine(44, 39, 55, 54);
                    g.drawLine(48, 45, 53, 42);
                } else if (back) {
                    g.drawLine(42, 39, 53, 55);
                    g.drawLine(47, 46, 52, 43);
                } else {
                    g.drawLine(42, 39, 54, 54);
                    g.drawLine(46, 45, 51, 42);
                }
            }
            case "tattoo1" -> {
                g.setColor(new Color(0x34, 0x45, 0x67));
                if (side) {
                    g.fillRect(43, 37, 3, 17);
                    g.fillRect(46, 40, 2, 2);
                    g.fillRect(46, 47, 2, 2);
                } else if (diagonalFront || diagonalBack) {
                    int x = diagonalBack ? 50 : 41;
                    g.fillRect(x, 36, 3, 18);
                    g.fillRect(x + (diagonalBack ? -3 : 3), 39, 3, 2);
                    g.fillRect(x + (diagonalBack ? -3 : 3), 47, 3, 2);
                } else {
                    int x = back ? 51 : 40;
                    g.fillRect(x, 36, 3, 18);
                    g.fillRect(x + (back ? -3 : 3), 39, 3, 2);
                    g.fillRect(x + (back ? -3 : 3), 47, 3, 2);
                }
            }
            case "tattoo2" -> {
                g.setColor(new Color(0x4C, 0x32, 0x62));
                if (side) {
                    g.drawLine(44, 40, 53, 49);
                    g.drawLine(44, 49, 52, 40);
                } else if (diagonalFront || diagonalBack) {
                    g.drawLine(43, 41, 55, 53);
                    g.drawLine(43, 53, 54, 41);
                    g.fillRect(48, 45, 3, 5);
                } else {
                    g.drawLine(42, 41, 54, 53);
                    g.drawLine(42, 53, 54, 41);
                    g.fillRect(47, 45, 3, 5);
                }
            }
            default -> {
                // "none" is represented by not loading a layer.
            }
        }
        g.dispose();
        write(image, "assets/player/shared/body_markings/" + marking + "_" + direction + ".png");
    }

    private static void writeHair(String style, String direction, boolean rear) throws Exception {
        BufferedImage image = canvas();
        Graphics2D g = graphics(image);
        if (rear) {
            switch (style) {
                case "long" -> drawLongHairRear(g, direction);
                case "ponytail" -> drawPonytailRear(g, direction);
                default -> {
                    // Short hair and buzzcuts have no layer behind the body.
                }
            }
        } else {
            switch (style) {
                case "long" -> drawLongHairFront(g, direction);
                case "buzzcut" -> drawBuzzcut(g, direction);
                case "ponytail" -> drawPonytailFront(g, direction);
                default -> drawShortHair(g, direction);
            }
        }
        g.dispose();
        write(image, "assets/player/shared/hair/" + style + "_" + direction
                + (rear ? "_rear" : "") + ".png");
    }

    private static void drawShortHair(Graphics2D g, String direction) {
        switch (direction) {
            case "right" -> drawRightHair(g);
            case "front_right" -> drawDiagonalHair(g, false);
            case "back_right" -> drawDiagonalHair(g, true);
            case "back" -> drawBackHair(g);
            default -> drawFrontHair(g);
        }
    }

    private static void drawLongHairFront(Graphics2D g, String direction) {
        drawShortHair(g, direction);
        boolean side = "right".equals(direction);
        boolean diagonal = direction.endsWith("_right");
        boolean back = direction.startsWith("back");
        g.setColor(OUTLINE);
        if (side) {
            g.fillPolygon(poly(38, 14, 43, 15, 42, 37, 39, 43, 37, 38));
            g.fillPolygon(poly(55, 13, 60, 16, 62, 37, 59, 43, 56, 37));
        } else if (diagonal) {
            g.fillPolygon(poly(37, 14, 42, 15, 41, 38, 38, 44, 36, 38));
            g.fillPolygon(poly(56, 13, 61, 16, 62, back ? 40 : 34, 59, 43, 56, 36));
        } else {
            g.fillPolygon(poly(36, 14, 41, 15, 40, 38, 37, 44, 35, 38));
            g.fillPolygon(poly(56, 14, 61, 15, 62, 38, 59, 44, 56, 38));
        }
        g.setColor(HAIR_BASE);
        if (side) {
            g.fillPolygon(poly(40, 16, 42, 17, 41, 36, 39, 40));
            g.fillPolygon(poly(57, 15, 59, 17, 60, 36, 59, 40, 57, 35));
        } else if (diagonal) {
            g.fillPolygon(poly(39, 16, 41, 17, 40, 36, 38, 40));
            g.fillPolygon(poly(58, 15, 60, 17, 60, 36, 59, 40, 57, 35));
        } else {
            g.fillPolygon(poly(38, 16, 40, 17, 39, 36, 37, 40));
            g.fillPolygon(poly(58, 16, 60, 17, 60, 36, 59, 40, 57, 36));
        }
        g.setColor(HAIR_HIGHLIGHT);
        g.fillRect(side ? 40 : 38, 18, 1, 15);
        g.setColor(HAIR_SHADOW);
        g.fillRect(side ? 59 : 60, 19, 1, 17);
    }

    private static void drawLongHairRear(Graphics2D g, String direction) {
        boolean side = "right".equals(direction);
        boolean diagonal = direction.endsWith("_right");
        int left = side ? 36 : 33;
        int right = side ? 63 : diagonal ? 64 : 63;
        g.setColor(OUTLINE);
        g.fillPolygon(poly(left + 4, 8, right - 5, 6, right, 17, right - 1, 44,
                right - 5, 57, 49, 52, 42, 58, left, 44, left, 18));
        g.setColor(HAIR_BASE);
        g.fillPolygon(poly(left + 6, 9, right - 6, 8, right - 2, 18, right - 3, 42,
                right - 6, 53, 49, 49, 42, 54, left + 2, 42, left + 2, 19));
        g.setColor(HAIR_HIGHLIGHT);
        g.fillPolygon(poly(left + 5, 12, left + 9, 9, left + 11, 12, left + 8, 46,
                left + 5, 50, left + 4, 25));
        g.setColor(HAIR_SHADOW);
        g.fillPolygon(poly(right - 8, 9, right - 3, 17, right - 4, 43, right - 7, 52,
                right - 10, 48, right - 8, 25));
    }

    private static void drawBuzzcut(Graphics2D g, String direction) {
        boolean side = "right".equals(direction);
        boolean diagonal = direction.endsWith("_right");
        int left = side ? 37 : 36;
        int right = side ? 60 : diagonal ? 61 : 60;
        g.setColor(OUTLINE);
        g.fillPolygon(poly(left, 15, left + 1, 8, left + 6, 4, 49, 2,
                right - 4, 5, right + 1, 10, right, 15, right - 5, 12,
                50, 10, 44, 12, left + 3, 16));
        g.setColor(HAIR_BASE);
        g.fillPolygon(poly(left + 2, 13, left + 3, 9, left + 7, 6, 49, 4,
                right - 5, 7, right - 1, 10, right - 2, 13, right - 6, 10,
                50, 8, 44, 10, left + 4, 14));
        g.setColor(HAIR_HIGHLIGHT);
        g.fillPolygon(poly(left + 5, 8, 44, 5, 49, 4, 49, 7, 44, 9));
        g.setColor(HAIR_SHADOW);
        g.fillPolygon(poly(right - 6, 6, right - 1, 10, right - 2, 14, right - 5, 11));
    }

    private static void drawPonytailFront(Graphics2D g, String direction) {
        drawShortHair(g, direction);
        boolean back = direction.startsWith("back");
        g.setColor(OUTLINE);
        if (back) {
            g.fillRect(54, 14, 7, 5);
        } else if (direction.endsWith("_right") || "right".equals(direction)) {
            g.fillRect(57, 13, 6, 5);
        } else {
            g.fillRect(55, 13, 6, 5);
        }
        g.setColor(HAIR_BASE);
        g.fillRect(back ? 55 : direction.endsWith("_right") || "right".equals(direction) ? 58 : 56,
                14, 4, 3);
        g.setColor(HAIR_HIGHLIGHT);
        g.fillRect(back ? 55 : direction.endsWith("_right") || "right".equals(direction) ? 58 : 56,
                14, 1, 2);
    }

    private static void drawPonytailRear(Graphics2D g, String direction) {
        boolean back = direction.startsWith("back");
        boolean side = "right".equals(direction);
        int tieX = back ? 55 : side ? 59 : direction.endsWith("_right") ? 58 : 57;
        int tailX = back ? 53 : tieX + 2;
        g.setColor(OUTLINE);
        g.fillOval(tieX, 14, 8, 8);
        g.fillPolygon(poly(tailX + 2, 19, tailX + 9, 20, tailX + 12, 31,
                tailX + 9, 45, tailX + 3, 50, tailX, 44, tailX + 4, 32));
        g.setColor(HAIR_BASE);
        g.fillOval(tieX + 1, 15, 6, 6);
        g.fillPolygon(poly(tailX + 3, 21, tailX + 7, 22, tailX + 9, 31,
                tailX + 7, 42, tailX + 4, 46, tailX + 2, 43, tailX + 6, 31));
        g.setColor(HAIR_HIGHLIGHT);
        g.fillPolygon(poly(tailX + 3, 22, tailX + 5, 23, tailX + 5, 39,
                tailX + 4, 43, tailX + 3, 41));
        g.setColor(HAIR_SHADOW);
        g.fillPolygon(poly(tailX + 7, 23, tailX + 9, 31, tailX + 7, 42,
                tailX + 5, 45, tailX + 6, 32));
    }

    private static void drawFrontHair(Graphics2D g) {
        g.setColor(OUTLINE);
        g.fillPolygon(poly(36, 14, 37, 8, 41, 4, 49, 2, 57, 5, 61, 10, 60, 17,
                57, 14, 55, 10, 52, 14, 48, 10, 44, 14, 40, 11, 39, 17));
        g.setColor(HAIR_BASE);
        g.fillPolygon(poly(38, 13, 39, 9, 42, 6, 49, 4, 55, 6, 59, 10, 58, 14,
                55, 12, 55, 8, 52, 12, 48, 8, 44, 12, 41, 9, 40, 14));
        g.setColor(HAIR_HIGHLIGHT);
        g.fillPolygon(poly(41, 9, 44, 6, 49, 5, 49, 8, 45, 10));
        g.setColor(HAIR_SHADOW);
        g.fillPolygon(poly(55, 6, 59, 10, 58, 15, 55, 12, 55, 9));
    }

    private static void drawRightHair(Graphics2D g) {
        g.setColor(OUTLINE);
        g.fillPolygon(poly(37, 15, 38, 8, 43, 4, 51, 3, 58, 7, 61, 12, 59, 18,
                56, 14, 55, 10, 52, 13, 48, 10, 43, 14, 40, 18));
        g.setColor(HAIR_BASE);
        g.fillPolygon(poly(39, 14, 40, 9, 44, 6, 51, 5, 56, 8, 59, 12, 58, 15,
                55, 12, 55, 8, 52, 11, 48, 8, 43, 12, 41, 15));
        g.setColor(HAIR_HIGHLIGHT);
        g.fillPolygon(poly(42, 9, 45, 6, 50, 5, 50, 8, 45, 10));
        g.setColor(HAIR_SHADOW);
        g.fillPolygon(poly(55, 7, 59, 11, 58, 16, 55, 12, 55, 9));
    }

    private static void drawDiagonalHair(Graphics2D g, boolean back) {
        g.setColor(OUTLINE);
        g.fillPolygon(poly(36, 15, 37, 8, 42, 4, 50, 2, 58, 6, 61, 11, 60, 19,
                57, 15, 54, 11, 51, 15, 47, 11, 43, 15, 39, 19));
        g.setColor(HAIR_BASE);
        g.fillPolygon(poly(38, 14, 39, 9, 43, 6, 50, 4, 56, 7, 59, 11, 58, 16,
                55, 13, 54, 9, 51, 13, 47, 9, 43, 13, 40, 16));
        g.setColor(HAIR_HIGHLIGHT);
        g.fillPolygon(poly(41, 9, 44, 6, 49, 5, 49, 8, 44, 11));
        g.setColor(HAIR_SHADOW);
        g.fillPolygon(poly(55, 7, 59, 11, 58, back ? 18 : 16, 55, 13, 54, 9));
        if (back) {
            g.fillPolygon(poly(39, 15, 43, 13, 47, 16, 51, 13, 55, 16, 58, 18,
                    55, 20, 51, 16, 47, 20, 43, 16, 40, 20));
        }
    }

    private static void drawBackHair(Graphics2D g) {
        g.setColor(OUTLINE);
        g.fillPolygon(poly(36, 15, 37, 8, 42, 4, 49, 2, 57, 5, 61, 10, 60, 20,
                56, 16, 53, 19, 49, 15, 45, 19, 41, 15, 38, 20));
        g.setColor(HAIR_BASE);
        g.fillPolygon(poly(38, 14, 39, 9, 43, 6, 49, 4, 55, 6, 59, 10, 58, 17,
                55, 14, 53, 17, 49, 13, 45, 17, 42, 13, 40, 17));
        g.setColor(HAIR_HIGHLIGHT);
        g.fillPolygon(poly(41, 9, 44, 6, 49, 5, 49, 8, 44, 11));
        g.setColor(HAIR_SHADOW);
        g.fillPolygon(poly(55, 6, 59, 10, 58, 17, 55, 14, 54, 10));
    }

    private static BufferedImage canvas() {
        return new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
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
