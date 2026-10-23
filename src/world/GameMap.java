package world;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GameMap {

    static final int TILE_SIZE = 128;
    /** Hides 1px cracks between tiles after the 1600x900 frame is scaled to the window. */
    private static final int TILE_OVERLAP_PX = 1;
    private static final Color GRASS_BASE = new Color(46, 92, 38);

    Tile grassTile;
    Tile hubTile;
    private Tile currentTile;

    public GameMap() {
        loadTiles();
        currentTile = grassTile;
    }

    public GameMap(String tileType) {
        loadTiles();
        if (tileType.equals("hub")) {
            currentTile = hubTile;
        } else {
            currentTile = grassTile;
        }
    }

    private void loadTiles() {
        try {
            BufferedImage grassImg = ImageIO.read(new File("assets/tiles/grass_tile.png"));
            grassTile = new Tile(grassImg);
        } catch (IOException e) {
            System.out.println("Could not load grass_tile.png");
        }
        try {
            BufferedImage hubImg = ImageIO.read(new File("assets/tiles/hub_tile.png"));
            hubTile = new Tile(hubImg);
        } catch (IOException e) {
            System.out.println("Could not load hub_tile.png");
        }
    }

    public void draw(Graphics2D g, int screenWidth, int screenHeight, int cameraX, int cameraY, int arenaWidth, int arenaHeight) {
        if (currentTile == null || arenaWidth <= 0 || arenaHeight <= 0) {
            return;
        }

        int arenaScreenX = -cameraX;
        int arenaScreenY = -cameraY;

        // Solid fill under the tiles so scale-seams never flash the black void.
        g.setColor(GRASS_BASE);
        g.fillRect(arenaScreenX, arenaScreenY, arenaWidth, arenaHeight);

        Shape previousClip = g.getClip();
        Rectangle screen = new Rectangle(0, 0, screenWidth, screenHeight);
        Rectangle arenaOnScreen = new Rectangle(arenaScreenX, arenaScreenY, arenaWidth, arenaHeight);
        Rectangle visible = screen.intersection(arenaOnScreen);
        if (visible.isEmpty()) {
            g.setClip(previousClip);
            return;
        }
        g.clip(visible);
        Object previousInterp = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int startX = Math.max(0, (cameraX / TILE_SIZE) * TILE_SIZE);
        int startY = Math.max(0, (cameraY / TILE_SIZE) * TILE_SIZE);
        int endX = Math.min(arenaWidth, cameraX + screenWidth);
        int endY = Math.min(arenaHeight, cameraY + screenHeight);

        for (int x = startX; x < endX; x += TILE_SIZE) {
            for (int y = startY; y < endY; y += TILE_SIZE) {
                // +1px overlap closes the hairline gaps that appear when the frame is scaled.
                currentTile.draw(g, x - cameraX, y - cameraY, TILE_SIZE + TILE_OVERLAP_PX);
            }
        }

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                previousInterp != null ? previousInterp : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setClip(previousClip);
    }
}
