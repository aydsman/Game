package player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import java.io.File;

public class CharacterRenderer {

    private Map<String, BufferedImage> cachedAssets = new HashMap<>();
    private CharacterAppearance currentAppearance;

    public void loadAssets(CharacterAppearance appearance, Color[][] skinColors) {
        this.currentAppearance = appearance;
        cachedAssets.clear();
        try {
            // Load test character asset
            BufferedImage original = ImageIO.read(new File("assets/player/body/man/idle/idle_front.png"));
            // Recolor based on skin color
            Color[] from = {new Color(0xBB, 0xBB, 0xBB), new Color(0xAA, 0xAA, 0xAA), new Color(0x77, 0x77, 0x77)};
            Color baseColor = skinColors[appearance.getSkinColorIndex() / 3][1]; // Base shade
            Color[] toColors = {baseColor.brighter(), baseColor, baseColor.darker()};
            BufferedImage recolored = recolor(original, from, toColors);
            cachedAssets.put("testCharacter", recolored);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BufferedImage recolor(BufferedImage src, Color[] from, Color[] to) {
        BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                int rgb = src.getRGB(x, y);
                Color pixel = new Color(rgb, true);
                boolean matched = false;
                for (int i = 0; i < from.length; i++) {
                    if (pixel.getRed() == from[i].getRed() &&
                            pixel.getGreen() == from[i].getGreen() &&
                            pixel.getBlue() == from[i].getBlue()) {
                        // Preserve original alpha
                        Color replacement = to[i];
                        int newRgb = (pixel.getAlpha() << 24) |
                                (replacement.getRed() << 16) |
                                (replacement.getGreen() << 8) |
                                replacement.getBlue();
                        result.setRGB(x, y, newRgb);
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    result.setRGB(x, y, rgb); // keep original pixel
                }
            }
        }
        return result;
    }

    public BufferedImage flipHorizontal(BufferedImage src) {
        // TODO: Flip image horizontally
        return src; // Placeholder
    }

    public void draw(Graphics2D g, int x, int y, Direction direction, int animFrame, WeaponType weaponType, double mouseAngle) {
        // TODO: Draw all layers in order at x,y
        // Use cached assets, apply direction/animation
    }

    public BufferedImage getGunArmSprite(double mouseAngle) {
        // TODO: Snap angle to 8 directions, return arm sprite
        return null; // Placeholder
    }

    public BufferedImage getTestCharacter() {
        return cachedAssets.get("testCharacter");
    }

    public enum Direction {
        DOWN, LEFT, RIGHT, UP
    }

    public enum WeaponType {
        NONE, PISTOL, RIFLE, MELEE
    }
}
