package world;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Tile {

    BufferedImage image;

    public Tile(BufferedImage image) {
        this.image = image;
    }

    public void draw(Graphics2D g, int x, int y, int size) {
        g.drawImage(image, x, y, size, size, null);
    }
}
