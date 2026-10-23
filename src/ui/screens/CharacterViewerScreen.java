package ui.screens;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import player.CharacterAppearance;
import player.CharacterRenderer;
import player.CharacterRenderer.Direction;
import save.SaveData;
import save.SaveManager;
import ui.GamePanel;

/** Displays the saved character and lets the player rotate through all eight views. */
public class CharacterViewerScreen {
    private static final Direction[] ROTATION = {
        Direction.DOWN, Direction.DOWN_RIGHT, Direction.RIGHT, Direction.UP_RIGHT,
        Direction.UP, Direction.UP_LEFT, Direction.LEFT, Direction.DOWN_LEFT
    };
    private static final String[] DIRECTION_NAMES = {
        "Front", "Front Right", "Right", "Back Right",
        "Back", "Back Left", "Left", "Front Left"
    };
    private static final long AUTO_SPIN_DELAY_MS = 850;

    private final GamePanel gamePanel;
    private final CharacterRenderer renderer = new CharacterRenderer();
    private final Rectangle backBtn = new Rectangle(40, 24, 120, 44);
    private final Rectangle leftBtn = new Rectangle(430, 402, 90, 90);
    private final Rectangle rightBtn = new Rectangle(1080, 402, 90, 90);
    private final Rectangle spinBtn = new Rectangle(610, 760, 180, 52);
    private final Rectangle customizeBtn = new Rectangle(810, 760, 180, 52);
    private final Rectangle characterArea = new Rectangle(550, 175, 500, 535);

    private int directionIndex;
    private boolean autoSpin;
    private long lastSpinMs;
    private String hovered;

    public CharacterViewerScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        refresh();
    }

    public void refresh() {
        SaveData data = SaveManager.load();
        CharacterAppearance appearance = data.getAppearance();
        if (appearance == null) {
            appearance = new CharacterAppearance();
        }
        renderer.loadAssets(
                appearance,
                CharacterRenderer.getDefaultSkinPalettes(),
                data.getEquippedClothingStyles());
        directionIndex = 0;
        lastSpinMs = System.currentTimeMillis();
    }

    public void update() {
        if (!autoSpin) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastSpinMs >= AUTO_SPIN_DELAY_MS) {
            rotate(1);
            lastSpinMs = now;
        }
    }

    public void handleClick(int x, int y) {
        if (backBtn.contains(x, y)) {
            gamePanel.switchScreen("menu");
        } else if (leftBtn.contains(x, y)) {
            autoSpin = false;
            rotate(-1);
        } else if (rightBtn.contains(x, y) || characterArea.contains(x, y)) {
            autoSpin = false;
            rotate(1);
        } else if (spinBtn.contains(x, y)) {
            autoSpin = !autoSpin;
            lastSpinMs = System.currentTimeMillis();
        } else if (customizeBtn.contains(x, y)) {
            gamePanel.switchScreen("customize");
        }
    }

    public void handleMouseMove(int x, int y) {
        hovered = null;
        if (backBtn.contains(x, y)) hovered = "back";
        else if (leftBtn.contains(x, y)) hovered = "left";
        else if (rightBtn.contains(x, y)) hovered = "right";
        else if (spinBtn.contains(x, y)) hovered = "spin";
        else if (customizeBtn.contains(x, y)) hovered = "customize";
        else if (characterArea.contains(x, y)) hovered = "character";
    }

    private void rotate(int amount) {
        directionIndex = Math.floorMod(directionIndex + amount, ROTATION.length);
    }

    public void draw(Graphics2D g, int screenWidth, int screenHeight) {
        g.setPaint(new GradientPaint(
                0, 0, new Color(14, 24, 41),
                0, screenHeight, new Color(8, 12, 23)));
        g.fillRect(0, 0, screenWidth, screenHeight);
        g.setPaint(null);

        drawBackButton(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 44));
        drawCentered(g, "YOUR CHARACTER", screenWidth / 2, 78);
        g.setColor(new Color(158, 179, 211));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        drawCentered(g, "Use the arrows, click the character, or turn on auto spin", screenWidth / 2, 112);

        g.setColor(new Color(20, 30, 48, 235));
        g.fillRoundRect(characterArea.x, characterArea.y, characterArea.width, characterArea.height, 24, 24);
        g.setColor("character".equals(hovered) ? new Color(115, 180, 235) : new Color(73, 104, 151));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(characterArea.x, characterArea.y, characterArea.width, characterArea.height, 24, 24);
        g.setStroke(new BasicStroke(1));

        drawCharacter(g);
        drawRotationButton(g, leftBtn, "<", "left".equals(hovered));
        drawRotationButton(g, rightBtn, ">", "right".equals(hovered));
        drawActionButton(g, spinBtn, autoSpin ? "Auto Spin: ON" : "Auto Spin: OFF",
                new Color(117, 91, 190), "spin".equals(hovered));
        drawActionButton(g, customizeBtn, "Customize",
                new Color(53, 145, 205), "customize".equals(hovered));
    }

    private void drawCharacter(Graphics2D g) {
        BufferedImage frame = renderer.getRedesignedBodyFrame(ROTATION[directionIndex]);
        if (frame == null) {
            frame = renderer.getFrame(ROTATION[directionIndex], false, 0);
        }
        if (frame == null) {
            return;
        }

        int scale = 5;
        int drawW = frame.getWidth() * scale;
        int drawH = frame.getHeight() * scale;
        int drawX = characterArea.x + (characterArea.width - drawW) / 2;
        int drawY = characterArea.y + 24;

        g.setColor(new Color(0, 0, 0, 85));
        g.fillOval(characterArea.x + 170, characterArea.y + 467, 160, 25);

        Object previousInterpolation = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(frame, drawX, drawY, drawW, drawH, null);
        if (previousInterpolation != null) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, previousInterpolation);
        }

        g.setColor(new Color(225, 234, 248));
        g.setFont(new Font("Segoe UI", Font.BOLD, 22));
        drawCentered(g, DIRECTION_NAMES[directionIndex], characterArea.x + characterArea.width / 2,
                characterArea.y + characterArea.height - 20);
    }

    private void drawBackButton(Graphics2D g) {
        boolean isHovered = "back".equals(hovered);
        g.setColor(isHovered ? new Color(79, 105, 148) : new Color(57, 76, 110));
        g.fillRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 10, 10);
        g.setColor(Color.WHITE);
        g.drawRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 10, 10);
        g.setFont(new Font("Segoe UI", Font.BOLD, 15));
        drawCentered(g, "Back", backBtn.x + backBtn.width / 2, backBtn.y + 28);
    }

    private void drawRotationButton(Graphics2D g, Rectangle button, String label, boolean isHovered) {
        g.setColor(isHovered ? new Color(69, 103, 151) : new Color(38, 55, 82));
        g.fillRoundRect(button.x, button.y, button.width, button.height, 18, 18);
        g.setColor(isHovered ? new Color(135, 194, 245) : new Color(82, 119, 168));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(button.x, button.y, button.width, button.height, 18, 18);
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 42));
        drawCentered(g, label, button.x + button.width / 2, button.y + 60);
    }

    private void drawActionButton(Graphics2D g, Rectangle button, String label, Color accent, boolean isHovered) {
        g.setColor(isHovered ? accent : new Color(38, 45, 62));
        g.fillRoundRect(button.x, button.y, button.width, button.height, 12, 12);
        g.setColor(isHovered ? accent.brighter() : accent);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(button.x, button.y, button.width, button.height, 12, 12);
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label,
                button.x + (button.width - fm.stringWidth(label)) / 2,
                button.y + (button.height + fm.getAscent() - fm.getDescent()) / 2);
    }

    private void drawCentered(Graphics2D g, String text, int centerX, int baselineY) {
        g.drawString(text, centerX - g.getFontMetrics().stringWidth(text) / 2, baselineY);
    }
}
