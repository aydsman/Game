package ui.screens;

import entity.Player;
import util.Camera;
import util.KeyHandler;
import world.arenas.ArenaTest;
import java.awt.Graphics2D;

public class GameScreen {

    ArenaTest arena = new ArenaTest();
    Player player = new Player(arena.getWidth() / 2, arena.getHeight() / 2);
    Camera camera = new Camera();

    public void update(KeyHandler key, int screenWidth, int screenHeight) {
        int arenaWidth  = arena.getWidth();
        int arenaHeight = arena.getHeight();
        player.update(key, arenaWidth, arenaHeight);
        camera.follow(player.getX(), player.getY(), player.getW(), player.getL(), screenWidth, screenHeight, arenaWidth, arenaHeight);
    }

    public void draw(Graphics2D g, int screenWidth, int screenHeight) {
        arena.draw(g, screenWidth, screenHeight, camera.x, camera.y);
        player.draw(g, camera.x, camera.y);
    }
}
