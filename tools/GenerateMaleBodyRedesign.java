import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** Generates an isolated front-idle body candidate without changing gameplay assets. */
public final class GenerateMaleBodyRedesign {
    private static final int SIZE = 96;
    private static final Color OUTLINE = new Color(0x34, 0x34, 0x3A);
    private static final Color HIGHLIGHT = new Color(0xBB, 0xBB, 0xBB);
    private static final Color BASE = new Color(0xAA, 0xAA, 0xAA);
    private static final Color SHADOW = new Color(0x77, 0x77, 0x77);
    private static final Color UNDERWEAR = new Color(0x25, 0x29, 0x34);
    private static final Color UNDERWEAR_LIGHT = new Color(0x3D, 0x43, 0x52);
    private static final Color UNDERWEAR_SHADOW = new Color(0x16, 0x19, 0x22);

    private GenerateMaleBodyRedesign() {}

    public static void main(String[] args) throws Exception {
        BufferedImage front = createBody("front");
        BufferedImage frontRight = createBody("front_right");
        BufferedImage right = createBody("right");
        BufferedImage backRight = createBody("back_right");
        BufferedImage back = createBody("back");

        write(front, "assets/player/body/man/redesign/front_idle.png");
        write(frontRight, "assets/player/body/man/redesign/front_right_idle.png");
        write(right, "assets/player/body/man/redesign/right_idle.png");
        write(backRight, "assets/player/body/man/redesign/back_right_idle.png");
        write(back, "assets/player/body/man/redesign/back_idle.png");
        write(createGunBody("front"), "assets/player/body/man/redesign/front_gun.png");
        write(createGunBody("front_right"), "assets/player/body/man/redesign/front_right_gun.png");
        write(createGunBody("right"), "assets/player/body/man/redesign/right_gun.png");
        write(createGunBody("back_right"), "assets/player/body/man/redesign/back_right_gun.png");
        write(createGunBody("back"), "assets/player/body/man/redesign/back_gun.png");
        write(flipHorizontal(frontRight), "assets/player/body/man/redesign/front_left_idle.png");
        write(flipHorizontal(right), "assets/player/body/man/redesign/left_idle.png");
        write(flipHorizontal(backRight), "assets/player/body/man/redesign/back_left_idle.png");
        writePreview(front, new File("assets/player/body/man/redesign/front_idle_preview.png"));
        writeTurnaroundPreview(new BufferedImage[] {
            front, frontRight, right, backRight,
            back, flipHorizontal(backRight), flipHorizontal(right), flipHorizontal(frontRight)
        }, new File("assets/player/body/man/redesign/eight_angle_preview.png"));
    }

    private static BufferedImage createBody(String direction) {
        BufferedImage body = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = body.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        switch (direction) {
            case "front_right" -> drawDiagonalBody(g, false);
            case "right" -> drawSideBody(g);
            case "back_right" -> drawDiagonalBody(g, true);
            case "back" -> drawBackBody(g);
            default -> drawFrontBody(g);
        }
        g.dispose();
        return body;
    }

    private static BufferedImage createGunBody(String direction) {
        BufferedImage body = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = body.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        switch (direction) {
            case "front_right" -> {
                drawDiagonalHead(g, false);
                drawDiagonalTorso(g, false);
                drawDiagonalLeg(g, true);
                drawDiagonalLeg(g, false);
                drawDiagonalUnderwear(g);
            }
            case "right" -> {
                drawSideHead(g);
                drawSideTorso(g);
                drawSideLegs(g);
                drawSideUnderwear(g);
            }
            case "back_right" -> {
                drawDiagonalHead(g, true);
                drawDiagonalTorso(g, true);
                drawDiagonalLeg(g, true);
                drawDiagonalLeg(g, false);
                drawDiagonalUnderwear(g);
            }
            case "back" -> {
                drawBackHead(g);
                drawBackTorso(g);
                drawLeg(g, true);
                drawLeg(g, false);
                drawUnderwear(g);
            }
            default -> {
                drawHead(g);
                drawNeckAndTorso(g);
                drawLeg(g, true);
                drawLeg(g, false);
                drawUnderwear(g);
            }
        }
        g.dispose();
        return body;
    }

    private static void write(BufferedImage image, String path) throws Exception {
        File output = new File(path);
        output.getParentFile().mkdirs();
        ImageIO.write(image, "png", output);
    }

    private static void drawFrontBody(Graphics2D g) {
        drawHead(g);
        drawArms(g);
        drawNeckAndTorso(g);
        drawLeg(g, true);
        drawLeg(g, false);
        drawUnderwear(g);
    }

    private static void drawBackBody(Graphics2D g) {
        drawBackHead(g);
        drawArms(g);
        drawBackTorso(g);
        drawLeg(g, true);
        drawLeg(g, false);
        drawUnderwear(g);
    }

    private static void drawHead(Graphics2D g) {
        g.setColor(OUTLINE);
        g.fillPolygon(poly(43, 3, 53, 3, 56, 6, 57, 14, 55, 20, 51, 24,
                45, 24, 41, 20, 39, 14, 40, 6));
        g.fillRect(37, 10, 3, 7);
        g.fillRect(56, 10, 3, 7);

        g.setColor(BASE);
        g.fillPolygon(poly(44, 5, 52, 5, 54, 7, 55, 14, 53, 19, 50, 22,
                46, 22, 43, 19, 41, 14, 42, 7));
        g.fillRect(38, 11, 3, 5);
        g.fillRect(55, 11, 3, 5);

        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(43, 7, 45, 5, 48, 5, 47, 21, 45, 20, 42, 14));
        g.fillRect(38, 12, 2, 3);
        g.setColor(SHADOW);
        g.fillPolygon(poly(52, 5, 54, 7, 55, 14, 53, 19, 50, 22, 50, 17, 52, 12));
        g.fillRect(56, 12, 2, 3);

        // Jaw and cheek definition without baking in eyes or a mouth.
        g.fillRect(42, 16, 2, 2);
        g.fillRect(52, 16, 2, 2);
        g.fillRect(46, 21, 5, 1);
    }

    private static void drawBackHead(Graphics2D g) {
        g.setColor(OUTLINE);
        g.fillPolygon(poly(43, 3, 53, 3, 56, 6, 57, 15, 54, 21,
                51, 24, 45, 24, 42, 21, 39, 15, 40, 6));
        g.fillRect(37, 10, 3, 7);
        g.fillRect(56, 10, 3, 7);
        g.setColor(BASE);
        g.fillPolygon(poly(44, 5, 52, 5, 54, 7, 55, 15, 52, 20,
                50, 22, 46, 22, 44, 20, 41, 15, 42, 7));
        g.fillRect(38, 11, 3, 5);
        g.fillRect(55, 11, 3, 5);
        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(43, 7, 45, 5, 48, 5, 47, 21, 44, 19, 42, 14));
        g.setColor(SHADOW);
        g.fillPolygon(poly(52, 5, 54, 7, 55, 15, 52, 20, 50, 22,
                50, 16, 52, 11));
        g.fillRect(43, 19, 10, 2);
    }

    private static void drawBackTorso(Graphics2D g) {
        g.setColor(OUTLINE);
        g.fillPolygon(poly(43, 21, 53, 21, 54, 25, 61, 27, 65, 31,
                62, 39, 59, 44, 57, 54, 39, 54, 37, 44, 34, 39,
                31, 31, 35, 27, 42, 25));
        g.setColor(BASE);
        g.fillPolygon(poly(45, 22, 51, 22, 52, 27, 59, 29, 62, 32,
                59, 39, 56, 43, 55, 52, 41, 52, 40, 43, 37, 39,
                34, 32, 37, 29, 44, 27));
        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(36, 31, 43, 27, 47, 28, 46, 39, 41, 43, 37, 39));
        g.fillPolygon(poly(41, 43, 46, 39, 46, 51, 42, 51));
        g.setColor(SHADOW);
        g.fillPolygon(poly(49, 28, 53, 27, 60, 30, 62, 33, 59, 39, 54, 43, 50, 39));
        g.fillPolygon(poly(50, 39, 55, 43, 55, 51, 51, 51));
        g.fillRect(47, 28, 3, 23);
        g.fillRect(39, 34, 7, 2);
        g.fillRect(50, 34, 7, 2);
        g.fillRect(42, 42, 4, 2);
        g.fillRect(50, 42, 4, 2);
    }

    private static void drawDiagonalBody(Graphics2D g, boolean back) {
        drawDiagonalHead(g, back);
        drawDiagonalFarArm(g);
        drawDiagonalTorso(g, back);
        drawDiagonalLeg(g, true);
        drawDiagonalLeg(g, false);
        drawDiagonalUnderwear(g);
        drawDiagonalNearArm(g);
    }

    private static void drawDiagonalHead(Graphics2D g, boolean back) {
        g.setColor(OUTLINE);
        g.fillPolygon(poly(42, 3, 53, 3, 57, 7, 58, 15, 55, 21,
                51, 24, 45, 23, 41, 19, 39, 13, 40, 6));
        g.fillRect(57, 10, 3, 7);
        g.setColor(BASE);
        g.fillPolygon(poly(43, 5, 52, 5, 55, 8, 56, 15, 53, 20,
                50, 22, 46, 21, 43, 18, 41, 13, 42, 7));
        g.fillRect(56, 11, 3, 5);
        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(43, 7, 46, 5, 49, 5, 48, 21, 45, 19, 42, 13));
        g.setColor(SHADOW);
        g.fillPolygon(poly(52, 5, 55, 8, 56, 15, 53, 20, 50, 22,
                50, 16, 52, 11));
        if (back) {
            g.fillRect(43, 18, 10, 2);
        } else {
            g.fillRect(48, 20, 4, 1);
        }
    }

    private static void drawDiagonalFarArm(Graphics2D g) {
        // Receding left arm: thinner, shorter, smaller fist.
        g.setColor(OUTLINE);
        g.fillPolygon(poly(38, 29, 35, 31, 34, 36, 35, 46, 35, 53,
                34, 56, 36, 58, 39, 57, 40, 54, 40, 46, 40, 34));
        g.setColor(SHADOW);
        g.fillPolygon(poly(37, 31, 36, 32, 35, 37, 36, 46, 36, 53,
                36, 55, 37, 56, 38, 55, 39, 46, 39, 33));
    }

    private static void drawDiagonalNearArm(Graphics2D g) {
        // Near right arm hangs over the torso midline, not out to the side.
        g.setColor(OUTLINE);
        g.fillPolygon(poly(52, 27, 58, 28, 60, 33, 57, 42, 55, 50,
                56, 59, 54, 64, 47, 64, 45, 60, 49, 52, 50, 40, 50, 31));
        g.setColor(BASE);
        g.fillPolygon(poly(53, 29, 57, 30, 58, 34, 55, 42, 53, 50,
                54, 59, 52, 62, 48, 62, 47, 59, 50, 51, 51, 40, 51, 32));
        g.setColor(HIGHLIGHT);
        g.fillRect(52, 33, 2, 14);
        g.fillRect(50, 51, 2, 9);
    }

    private static void drawDiagonalTorso(Graphics2D g, boolean back) {
        // 3/4 turn: far ribs recede, near side plane comes forward, sternum shifts right.
        g.setColor(OUTLINE);
        g.fillPolygon(poly(41, 21, 52, 21, 54, 25, 58, 28, 61, 34,
                59, 42, 56, 54, 43, 54, 40, 45, 37, 37, 36, 30, 39, 25));
        g.setColor(BASE);
        g.fillPolygon(poly(42, 22, 51, 22, 53, 26, 56, 29, 59, 34,
                57, 42, 54, 52, 44, 52, 42, 44, 39, 37, 38, 30, 41, 26));
        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(41, 30, 47, 26, 50, 28, 49, 49, 44, 51, 41, 42));
        g.setColor(SHADOW);
        g.fillPolygon(poly(51, 27, 56, 29, 59, 34, 57, 42, 54, 52,
                51, 50, 51, 34));
        g.fillRect(50, 32, 2, 18);
        if (back) {
            g.fillRect(42, 34, 5, 2);
            g.fillRect(52, 34, 5, 2);
            g.fillRect(44, 42, 3, 2);
            g.fillRect(52, 42, 3, 2);
        } else {
            g.fillRect(42, 36, 5, 2);
            g.fillRect(52, 36, 6, 2);
            g.fillRect(44, 43, 3, 2);
            g.fillRect(52, 43, 4, 2);
        }
    }

    private static void drawDiagonalUnderwear(Graphics2D g) {
        g.setColor(OUTLINE);
        g.fillPolygon(poly(40, 51, 58, 51, 58, 62, 55, 64,
                49, 64, 47, 62, 45, 64, 41, 63, 39, 60, 39, 54));
        g.setColor(UNDERWEAR);
        g.fillPolygon(poly(42, 53, 56, 53, 56, 60, 53, 62,
                49, 62, 47, 60, 45, 62, 43, 61, 41, 59, 41, 54));
        g.setColor(UNDERWEAR_LIGHT);
        g.fillRect(42, 53, 14, 2);
        g.setColor(UNDERWEAR_SHADOW);
        g.fillRect(49, 55, 7, 6);
        g.setColor(OUTLINE);
        g.fillRect(47, 57, 2, 6);
    }

    private static void drawDiagonalLeg(Graphics2D g, boolean far) {
        // Front-right 3/4: both feet point toward the facing direction.
        // Far anatomical-left recedes; near anatomical-right overlaps in front.
        if (far) {
            g.setColor(OUTLINE);
            g.fillPolygon(poly(41, 62, 48, 62, 47, 76, 46, 84,
                    47, 91, 53, 93, 53, 95, 40, 95, 39, 93,
                    41, 89, 42, 78, 41, 68));
            g.setColor(SHADOW);
            g.fillPolygon(poly(42, 64, 47, 64, 46, 76, 45, 84,
                    46, 90, 51, 93, 42, 93, 42, 89, 43, 78, 42, 67));
            return;
        }
        g.setColor(OUTLINE);
        g.fillPolygon(poly(47, 62, 55, 62, 56, 76, 55, 83,
                57, 91, 64, 93, 64, 95, 49, 95, 48, 93,
                50, 89, 49, 78, 48, 68));
        g.setColor(BASE);
        g.fillPolygon(poly(48, 64, 54, 64, 55, 76, 54, 83,
                56, 90, 62, 93, 51, 93, 51, 89, 50, 78, 49, 66));
        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(49, 65, 52, 65, 53, 76, 52, 83,
                54, 90, 58, 92, 51, 92, 51, 84, 50, 75));
    }

    private static void drawSideBody(Graphics2D g) {
        drawSideHead(g);
        drawSideLeftArm(g);
        drawSideTorso(g);
        drawSideLegs(g);
        drawSideUnderwear(g);
        drawSideRightArm(g);
    }

    private static void drawSideHead(Graphics2D g) {
        g.setColor(OUTLINE);
        g.fillPolygon(poly(42, 3, 53, 3, 57, 7, 58, 12, 61, 14,
                58, 18, 56, 22, 51, 24, 44, 22, 40, 17, 40, 7));
        g.setColor(BASE);
        g.fillPolygon(poly(43, 5, 52, 5, 55, 8, 56, 13, 59, 14,
                56, 17, 54, 21, 50, 22, 45, 20, 42, 16, 42, 8));
        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(43, 7, 46, 5, 49, 5, 48, 20, 45, 18, 42, 14));
        g.setColor(SHADOW);
        g.fillPolygon(poly(52, 5, 55, 8, 56, 13, 58, 14, 55, 17,
                53, 21, 50, 22, 50, 14, 52, 10));
    }

    private static void drawSideRightArm(Graphics2D g) {
        g.setColor(OUTLINE);
        g.fillPolygon(poly(42, 29, 38, 31, 37, 42, 39, 49, 38, 58,
                40, 62, 44, 61, 45, 57, 43, 49, 45, 39, 46, 32));
        g.setColor(BASE);
        g.fillPolygon(poly(42, 31, 40, 33, 39, 42, 41, 49, 40, 57,
                41, 60, 43, 59, 42, 49, 43, 39, 44, 33));
        g.setColor(HIGHLIGHT);
        g.fillRect(40, 34, 2, 10);
        g.fillRect(40, 51, 2, 7);
    }

    private static void drawSideLeftArm(Graphics2D g) {
        // The anatomical left arm is farther from the camera in the right-facing view.
        g.translate(-5, 0);
        g.setColor(OUTLINE);
        g.fillPolygon(poly(55, 28, 60, 30, 63, 36, 63, 46, 61, 51,
                62, 58, 64, 60, 62, 63, 57, 62, 55, 59, 56, 51,
                55, 44, 57, 36, 53, 31));
        g.setColor(SHADOW);
        g.fillPolygon(poly(56, 30, 59, 32, 61, 37, 61, 45, 59, 51,
                60, 58, 61, 60, 59, 60, 57, 58, 58, 51, 57, 44,
                59, 37, 56, 32));
        g.translate(5, 0);
    }

    private static void drawSideTorso(Graphics2D g) {
        g.setColor(OUTLINE);
        g.fillPolygon(poly(44, 21, 53, 21, 54, 26, 59, 29, 61, 35,
                58, 43, 56, 54, 42, 54, 41, 45, 39, 39, 40, 31, 44, 26));
        g.setColor(BASE);
        g.fillPolygon(poly(46, 22, 51, 22, 52, 27, 57, 30, 59, 35,
                56, 42, 54, 52, 44, 52, 43, 44, 41, 39, 42, 32, 45, 28));
        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(43, 31, 47, 27, 49, 28, 48, 50, 45, 51, 43, 42));
        g.setColor(SHADOW);
        g.fillPolygon(poly(51, 27, 57, 30, 59, 35, 56, 42, 54, 52,
                50, 50, 50, 34));
        g.fillRect(48, 36, 5, 2);
        g.fillRect(49, 43, 4, 2);
    }

    private static void drawSideUnderwear(Graphics2D g) {
        g.setColor(OUTLINE);
        g.fillPolygon(poly(41, 51, 59, 51, 59, 61, 56, 64,
                46, 64, 41, 61));
        g.setColor(UNDERWEAR);
        g.fillPolygon(poly(43, 53, 57, 53, 57, 60, 54, 62,
                47, 62, 43, 60));
        g.setColor(UNDERWEAR_LIGHT);
        g.fillRect(43, 53, 13, 2);
        g.setColor(UNDERWEAR_SHADOW);
        g.fillRect(50, 55, 7, 7);
    }

    private static void drawSideLegs(Graphics2D g) {
        // In profile both legs share almost the same screen-space axis.
        // Draw the far leg first, then overlap it with the camera-facing leg.
        drawSideLeg(g, 45, 0, true);
        drawSideLeg(g, 47, 0, false);
    }

    private static void drawSideLeg(Graphics2D g, int hip, int shift, boolean far) {
        g.setColor(OUTLINE);
        g.fillPolygon(poly(hip, 62, hip + 8, 63, hip + 7 + shift, 77,
                hip + 5 + shift, 83, hip + 7 + shift, 92, hip + 6 + shift, 95,
                hip - 2 + shift, 95, hip - 2 + shift, 91, hip, 87,
                hip - 1, 76, hip - 2, 67));
        g.setColor(far ? SHADOW : BASE);
        g.fillPolygon(poly(hip + 2, 64, hip + 6, 64, hip + 5 + shift, 76,
                hip + 3 + shift, 82, hip + 5 + shift, 91, hip + 4 + shift, 93,
                hip + shift, 93, hip + shift, 90, hip + 2, 86, hip + 1, 75,
                hip, 66));
        if (!far) {
            g.setColor(HIGHLIGHT);
            g.fillPolygon(poly(hip + 2, 65, hip + 4, 65, hip + 4 + shift, 76,
                    hip + 2 + shift, 82, hip + 3 + shift, 90, hip + shift, 92,
                    hip + 1, 84, hip + 1, 74));
        }
    }

    private static void drawNeckAndTorso(Graphics2D g) {
        g.setColor(OUTLINE);
        g.fillPolygon(poly(43, 21, 53, 21, 54, 25, 61, 27, 65, 31,
                62, 39, 59, 44, 57, 54, 39, 54, 37, 44, 34, 39,
                31, 31, 35, 27, 42, 25));
        g.setColor(BASE);
        g.fillPolygon(poly(45, 22, 51, 22, 52, 27, 59, 29, 62, 32,
                59, 39, 56, 43, 55, 52, 41, 52, 40, 43, 37, 39,
                34, 32, 37, 29, 44, 27));

        // Collar bones, compact pectorals, tapered ribs, and abdominal center line.
        g.setColor(HIGHLIGHT);
        g.fillPolygon(poly(36, 31, 43, 27, 47, 28, 46, 36, 40, 38, 36, 36));
        g.fillPolygon(poly(40, 39, 46, 38, 46, 50, 42, 51, 40, 46));
        g.fillRect(45, 23, 2, 5);

        g.setColor(SHADOW);
        g.fillPolygon(poly(49, 28, 53, 27, 60, 30, 62, 33, 59, 38, 55, 39, 50, 36));
        g.fillPolygon(poly(50, 38, 56, 40, 55, 49, 53, 52, 50, 50));
        g.fillRect(48, 33, 2, 18);
        g.fillRect(40, 38, 6, 2);
        g.fillRect(50, 38, 6, 2);
        g.fillRect(42, 43, 4, 2);
        g.fillRect(50, 43, 4, 2);
        g.fillRect(43, 48, 3, 2);
        g.fillRect(50, 48, 3, 2);
        g.fillRect(39, 34, 2, 2);
        g.fillRect(55, 34, 2, 2);
        g.setColor(HIGHLIGHT);
        g.fillRect(58, 30, 3, 2);
        g.fillRect(52, 31, 3, 2);
        g.fillRect(52, 41, 2, 2);
        g.fillRect(51, 46, 2, 2);
    }

    private static void drawArms(Graphics2D g) {
        drawArm(g, true);
        drawArm(g, false);
    }

    private static void drawArm(Graphics2D g, boolean left) {
        g.setColor(OUTLINE);
        if (left) {
            g.fillPolygon(poly(36, 28, 31, 29, 27, 34, 27, 43, 28, 47,
                    28, 55, 27, 58, 28, 61, 34, 61, 36, 58, 36, 55,
                    36, 47, 36, 34, 38, 31));
        } else {
            g.fillPolygon(poly(60, 28, 65, 29, 69, 34, 69, 43, 68, 47,
                    68, 55, 69, 58, 68, 61, 62, 61, 60, 58, 60, 55,
                    60, 47, 60, 34, 58, 31));
        }
        g.setColor(BASE);
        if (left) {
            g.fillPolygon(poly(35, 30, 32, 30, 29, 35, 29, 43, 30, 47,
                    30, 55, 29, 58, 30, 59, 33, 59, 34, 57, 34, 55,
                    34, 47, 34, 35, 36, 32));
        } else {
            g.fillPolygon(poly(61, 30, 64, 30, 67, 35, 67, 43, 66, 47,
                    66, 55, 67, 58, 66, 59, 63, 59, 62, 57, 62, 55,
                    62, 47, 62, 35, 60, 32));
        }

        g.setColor(left ? HIGHLIGHT : SHADOW);
        if (left) {
            g.fillPolygon(poly(29, 36, 31, 33, 31, 45, 32, 47, 31, 55,
                    30, 58, 32, 58, 32, 54, 32, 47, 31, 44));
        } else {
            g.fillPolygon(poly(67, 36, 65, 33, 65, 45, 64, 47, 65, 55,
                    66, 58, 64, 58, 64, 54, 64, 47, 65, 44));
        }
        g.setColor(SHADOW);
        g.fillRect(left ? 29 : 65, 46, 3, 2);
        g.fillRect(left ? 29 : 65, 56, 3, 2);

        // Closed neutral fist at the end of the straight forearm.
        g.setColor(OUTLINE);
        if (left) {
            g.fillPolygon(poly(27, 55, 34, 55, 36, 58, 34, 61,
                    32, 62, 28, 61, 26, 59));
        } else {
            g.fillPolygon(poly(62, 55, 69, 55, 70, 59, 68, 61,
                    64, 62, 62, 61, 60, 58));
        }
        g.setColor(BASE);
        if (left) {
            g.fillPolygon(poly(29, 56, 33, 56, 34, 58, 33, 60,
                    29, 60, 28, 59));
        } else {
            g.fillPolygon(poly(63, 56, 67, 56, 68, 59, 67, 60,
                    63, 60, 62, 58));
        }
        g.setColor(left ? HIGHLIGHT : SHADOW);
        g.fillRect(left ? 28 : 66, 56, 2, 2);
        g.setColor(SHADOW);
        g.fillRect(left ? 30 : 64, 59, 3, 1);
    }

    private static void drawUnderwear(Graphics2D g) {
        g.setColor(OUTLINE);
        g.fillRect(37, 51, 22, 13);
        g.fillRect(39, 62, 9, 4);
        g.fillRect(49, 62, 8, 4);
        g.setColor(UNDERWEAR);
        g.fillRect(39, 53, 18, 10);
        g.fillRect(41, 62, 6, 2);
        g.fillRect(49, 62, 6, 2);
        g.setColor(UNDERWEAR_LIGHT);
        g.fillRect(40, 53, 16, 2);
        g.fillRect(40, 55, 7, 6);
        g.setColor(OUTLINE);
        g.fillRect(47, 56, 2, 10);
    }

    private static void drawLeg(Graphics2D g, boolean left) {
        g.setColor(OUTLINE);
        if (left) {
            g.fillPolygon(poly(39, 62, 48, 63, 47, 76, 45, 81, 46, 92,
                    45, 95, 34, 95, 34, 92, 38, 89, 38, 82, 36, 77, 37, 70));
        } else {
            g.fillPolygon(poly(48, 63, 57, 62, 59, 70, 60, 77, 58, 82,
                    58, 89, 62, 92, 62, 95, 51, 95, 50, 92, 51, 83, 49, 79));
        }
        g.setColor(BASE);
        if (left) {
            g.fillPolygon(poly(40, 64, 46, 64, 45, 75, 43, 80, 44, 91,
                    43, 93, 37, 93, 39, 89, 40, 82, 38, 76, 39, 65));
        } else {
            g.fillPolygon(poly(50, 64, 56, 64, 57, 70, 58, 77, 56, 82,
                    57, 89, 59, 93, 53, 93, 52, 91, 53, 82, 51, 78));
        }

        g.setColor(left ? HIGHLIGHT : SHADOW);
        if (left) {
            g.fillPolygon(poly(40, 65, 43, 64, 43, 75, 41, 80, 42, 90,
                    40, 92, 38, 92, 40, 88, 40, 81, 38, 77));
        } else {
            g.fillPolygon(poly(54, 65, 56, 66, 57, 77, 55, 82, 56, 89,
                    58, 92, 55, 92, 54, 89, 55, 81, 52, 78));
        }

        // Kneecap and calf separation.
        g.setColor(SHADOW);
        g.fillRect(left ? 40 : 53, 78, 3, 2);
        g.fillRect(left ? 40 : 55, 89, 2, 2);
        g.setColor(OUTLINE);
        g.fillRect(left ? 34 : 52, 93, 12, 3);
        g.setColor(BASE);
        g.fillRect(left ? 36 : 53, 93, 9, 1);
    }

    private static void writePreview(BufferedImage body, File output) throws Exception {
        BufferedImage recolored = recolorForPreview(body);
        BufferedImage preview = new BufferedImage(SIZE * 4, SIZE * 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = preview.createGraphics();
        g.setColor(new Color(76, 81, 86));
        g.fillRect(0, 0, preview.getWidth(), preview.getHeight());
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(recolored, 0, 0, preview.getWidth(), preview.getHeight(), null);
        g.dispose();
        ImageIO.write(preview, "png", output);
    }

    private static void writeTurnaroundPreview(BufferedImage[] bodies, File output) throws Exception {
        int scale = 3;
        BufferedImage preview = new BufferedImage(
                SIZE * scale * 4, SIZE * scale * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = preview.createGraphics();
        g.setColor(new Color(76, 81, 86));
        g.fillRect(0, 0, preview.getWidth(), preview.getHeight());
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        for (int i = 0; i < bodies.length; i++) {
            int x = (i % 4) * SIZE * scale;
            int y = (i / 4) * SIZE * scale;
            g.drawImage(recolorForPreview(bodies[i]), x, y, SIZE * scale, SIZE * scale, null);
        }
        g.dispose();
        ImageIO.write(preview, "png", output);
    }

    private static BufferedImage recolorForPreview(BufferedImage body) {
        BufferedImage recolored = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Color[] from = {HIGHLIGHT, BASE, SHADOW};
        Color[] to = {
            new Color(246, 190, 146),
            new Color(210, 142, 101),
            new Color(151, 88, 67)
        };
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                int rgb = body.getRGB(x, y);
                Color pixel = new Color(rgb, true);
                int replacement = rgb;
                for (int i = 0; i < from.length; i++) {
                    if (pixel.getRed() == from[i].getRed()
                            && pixel.getGreen() == from[i].getGreen()
                            && pixel.getBlue() == from[i].getBlue()) {
                        replacement = (pixel.getAlpha() << 24)
                                | (to[i].getRed() << 16)
                                | (to[i].getGreen() << 8)
                                | to[i].getBlue();
                        break;
                    }
                }
                recolored.setRGB(x, y, replacement);
            }
        }
        return recolored;
    }

    private static BufferedImage flipHorizontal(BufferedImage source) {
        BufferedImage flipped = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                flipped.setRGB(SIZE - 1 - x, y, source.getRGB(x, y));
            }
        }
        return flipped;
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
