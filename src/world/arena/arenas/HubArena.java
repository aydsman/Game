package world.arena.arenas;

import world.arena.Arena;
import world.GameMap;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Hub arena with hub tiles.
 * Used as a safe zone for players.
 */
public class HubArena extends Arena {

    public HubArena() {
        super(2000, 2000);
        this.gameMap = new GameMap("hub");
    }

    @Override
    public void draw(Graphics2D g, int screenWidth, int screenHeight, int cameraX, int cameraY) {
        // Draw black out of bounds
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Draw grass tiles using GameMap
        gameMap.draw(g, screenWidth, screenHeight, cameraX, cameraY, width, height);
    }
}

