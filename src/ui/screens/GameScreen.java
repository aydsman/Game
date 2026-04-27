package ui.screens;

import entity.EnemyManager;
import entity.Player;
import ui.HUD;
import util.Camera;
import util.KeyHandler;
import util.MouseHandler;
import world.arenas.ArenaTest;
import java.awt.Color;
import java.awt.Graphics2D;

public class GameScreen {

    ArenaTest arena = new ArenaTest();
    Player player = new Player(arena.getWidth() / 2, arena.getHeight() / 2);
    Camera camera = new Camera();
    HUD hud = new HUD();
    EnemyManager enemyManager = new EnemyManager();
    private boolean debugMode = false;

    public GameScreen() {
        // spawning enemies
        enemyManager.spawnEnemy(500, 1000, 1);
        enemyManager.spawnEnemy(70, 100, 1);
        enemyManager.spawnEnemy(1500, 1500, 1);
        enemyManager.spawnEnemy(1800, 9, 1);
        enemyManager.spawnEnemy(1000, 500, 2);
    }

    public void resetMouseClicks(MouseHandler mouse) {
        player.resetMouseClicks(mouse);
    }


    public void update(KeyHandler key, MouseHandler mouse, int screenWidth, int screenHeight) {
        int arenaWidth  = arena.getWidth();
        int arenaHeight = arena.getHeight();

        // Toggle debug mode with O key
        if (key.oPressed) {
            debugMode = !debugMode;
            key.oPressed = false; // Reset to prevent rapid toggling
        }

        // Handle hotbar scrolling
        if (mouse.scrollDirection != 0) {
            int currentSlot = hud.getInventoryUI().getSelectedSlot();
            int newSlot = currentSlot + mouse.scrollDirection;
            if (newSlot >= 5) newSlot = 0;
            if (newSlot < 0) newSlot = 4;
            hud.getInventoryUI().setSelectedSlot(newSlot);
            player.equipHotbarSlot(newSlot);
            mouse.resetScroll();
        }

        player.update(key, mouse, arenaWidth, arenaHeight);
        camera.follow(player.getX(), player.getY(), player.getW(), player.getL(), screenWidth, screenHeight, arenaWidth, arenaHeight);
        player.aimBarrel(mouse.mouseX + camera.x, mouse.mouseY + camera.y);
        player.checkProjectileCollisions(enemyManager);
        enemyManager.update(player, arenaWidth, arenaHeight);
        enemyManager.checkEnemyProjectileCollisions(player);
        enemyManager.setDebugMode(debugMode);
    }

    public void draw(Graphics2D g, int screenWidth, int screenHeight) {
        arena.draw(g, screenWidth, screenHeight, camera.x, camera.y);
        enemyManager.draw(g, camera.x, camera.y);
        enemyManager.drawEnemyProjectiles(g, camera.x, camera.y);
        player.draw(g, camera.x, camera.y);
        hud.draw(g, player, screenWidth, screenHeight);

        // Draw player projectiles
        for (combat.Projectile p : player.getProjectiles()) {
            g.setColor(p.getColor());
            int radius = p.getRadius();
            g.fillOval(p.getX() - camera.x - radius, p.getY() - camera.y - radius, radius * 2, radius * 2);
        }

        // Draw debug mode status (top right, smaller text)
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        String debugStatus = "debug mode: " + (debugMode ? "on" : "off");
        java.awt.FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(debugStatus);
        g.drawString(debugStatus, screenWidth - textWidth - 10, 20);

    }
}
