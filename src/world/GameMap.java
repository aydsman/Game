package world;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GameMap {

    static final int TILE_SIZE = 128;

    Tile grassTile;

    public GameMap() {
        loadTiles();
    }

    private void loadTiles() {
        try {
            BufferedImage grassImg = ImageIO.read(new File("assets/tiles/grass_tile.png"));
            grassTile = new Tile(grassImg);
        } catch (IOException e) {
            System.out.println("Could not load grass_tile.png");
        }
    }

    public void draw(Graphics2D g, int screenWidth, int screenHeight, int cameraX, int cameraY, int arenaWidth, int arenaHeight) {
        int startX = Math.max(0, (cameraX / TILE_SIZE) * TILE_SIZE);
        int startY = Math.max(0, (cameraY / TILE_SIZE) * TILE_SIZE);
        int endX = Math.min(arenaWidth, cameraX + screenWidth);
        int endY = Math.min(arenaHeight, cameraY + screenHeight);

        for (int x = startX; x < endX; x += TILE_SIZE) {
            for (int y = startY; y < endY; y += TILE_SIZE) {
                grassTile.draw(g, x - cameraX, y - cameraY, TILE_SIZE);
            }
        }
    }
}
