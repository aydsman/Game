import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public final class InspectDiagonalPose {
    public static void main(String[] args) throws Exception {
        String[] names = {"front_idle", "front_right_idle", "right_idle"};
        int scale = 9;
        int crop = 96;
        BufferedImage sheet = new BufferedImage(crop * scale * names.length, crop * scale,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = sheet.createGraphics();
        g.setColor(new Color(30, 36, 48));
        g.fillRect(0, 0, sheet.getWidth(), sheet.getHeight());
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
        for (int i = 0; i < names.length; i++) {
            BufferedImage src = ImageIO.read(
                    new File("assets/player/body/man/redesign/" + names[i] + ".png"));
            int originX = i * crop * scale;
            g.drawImage(src, originX, 0, crop * scale, crop * scale, null);
            g.setColor(new Color(255, 255, 255, 40));
            for (int x = 0; x <= crop; x += 10) {
                g.drawLine(originX + x * scale, 0, originX + x * scale, crop * scale);
            }
            for (int y = 0; y <= crop; y += 10) {
                g.drawLine(originX, y * scale, originX + crop * scale, y * scale);
            }
            g.setColor(Color.WHITE);
            g.drawString(names[i], originX + 12, 28);
            for (int x = 0; x <= crop; x += 10) {
                g.drawString(String.valueOf(x), originX + x * scale + 2, crop * scale - 6);
            }
        }
        g.dispose();
        ImageIO.write(sheet, "png", new File("tools/out/diagonal_inspect.png"));
        System.out.println("Wrote tools/out/diagonal_inspect.png");
    }
}
