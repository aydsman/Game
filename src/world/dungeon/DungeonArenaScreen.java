package world.dungeon;

import entity.Player;
import world.DungeonArena;
import util.Camera;
import util.KeyHandler;
import util.MouseHandler;
import ui.HUD;
import java.awt.Color;
import java.awt.Graphics2D;

public class DungeonArenaScreen {

    private DungeonArena arena;
    private Player player;
    private Camera camera;
    private DungeonGenerator generator;
    private int currentLevel;
    private HUD hud;
    private double targetCameraX;
    private double targetCameraY;
    private static final double CAMERA_LERP_SPEED = 0.1;
    private long lastShotTime = 0;
    private boolean canShoot = false;
    private boolean lPressedLastFrame = false;

    public DungeonArenaScreen() {
        this.generator = new DungeonGenerator();
        this.currentLevel = 1;
        this.camera = new Camera();
        this.hud = new HUD();
        this.lastShotTime = System.currentTimeMillis();
        generateLevel();
    }

    private void generateLevel() {
        var rooms = generator.generateLevel(currentLevel, true); // true = for gameplay (scaled)
        var hallways = generator.getHallways();
        var spawnRoom = generator.getSpawnRoom();

        this.arena = new DungeonArena(rooms, hallways, spawnRoom);

        // Place player in center of spawn room
        int playerX = spawnRoom.getCenterX() - 25; // 25 is half of player width (50)
        int playerY = spawnRoom.getCenterY() - 25;
        this.player = new Player(playerX, playerY);

        // Ensure weapon is equipped (player spawns with hotbar items but we need to equip one)
        if (player.getHeldWeapon() == null) {
            player.equipHotbarSlot(0);
        }
    }

    public void update(KeyHandler key, MouseHandler mouse, int screenWidth, int screenHeight) {
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

        // Handle level advance with L key (single press only, cap at level 5)
        if (key.lPressed && !lPressedLastFrame && currentLevel < 5) {
            currentLevel++;
            generateLevel();
        }
        lPressedLastFrame = key.lPressed;

        // Calculate intended movement
        int newX = player.getX();
        int newY = player.getY();
        int speed = (int) player.getSpeed();

        if (key.upPressed)    newY -= speed;
        if (key.downPressed)  newY += speed;
        if (key.leftPressed)  newX -= speed;
        if (key.rightPressed) newX += speed;

        // Apply collision detection
        int[] constrainedPos = constrainMovement(player.getX(), player.getY(), newX, newY);
        player.setX(constrainedPos[0]);
        player.setY(constrainedPos[1]);

        // Call player.update() for shooting logic (use large bounds to avoid clamping)
        player.update(key, mouse, 10000, 10000);

        // Restore position after player.update() moved it
        player.setX(constrainedPos[0]);
        player.setY(constrainedPos[1]);

        // Update player aiming (for turning around) - use target camera position for accuracy
        player.aimBarrel(mouse.mouseX + (int)targetCameraX, mouse.mouseY + (int)targetCameraY);

        // Update camera based on player position
        updateCamera(screenWidth, screenHeight);

        // Smooth camera transition
        lerpCamera();
    }

    private int[] constrainMovement(int oldX, int oldY, int newX, int newY) {
        // Find current room or hallway
        Room currentRoom = findRoomContainingPlayer();
        Hallway currentHallway = findHallwayContainingPlayer();

        if (currentRoom != null) {
            // Constrain to room bounds, but allow movement to connected hallways
            return constrainToRoom(oldX, oldY, newX, newY, currentRoom);
        } else if (currentHallway != null) {
            // Constrain to hallway bounds, but allow movement to connected rooms
            return constrainToHallway(oldX, oldY, newX, newY, currentHallway);
        } else {
            // Not in any room or hallway - constrain to spawn room as fallback
            Room spawn = arena.getSpawnRoom();
            return constrainToRoom(oldX, oldY, newY, newY, spawn);
        }
    }

    private int[] constrainToRoom(int oldX, int oldY, int newX, int newY, Room room) {
        // Get room bounds
        int roomLeft = room.getX();
        int roomRight = room.getX() + room.getWidth();
        int roomTop = room.getY();
        int roomBottom = room.getY() + room.getHeight();

        // Check if player is trying to leave the room
        boolean leavingX = newX < roomLeft || newX + player.getW() > roomRight;
        boolean leavingY = newY < roomTop || newY + player.getL() > roomBottom;

        if (!leavingX && !leavingY) {
            return new int[]{newX, newY}; // Not leaving room
        }

        // Check if leaving through a hallway opening
        for (Room connected : room.getConnections()) {
            Hallway hallway = findHallwayBetween(room, connected);
            if (hallway != null && isPassingThroughOpening(newX, newY, room, hallway)) {
                return new int[]{newX, newY};
            }
        }

        // Not leaving through an opening - constrain to room bounds
        int constrainedX = Math.max(roomLeft, Math.min(newX, roomRight - player.getW()));
        int constrainedY = Math.max(roomTop, Math.min(newY, roomBottom - player.getL()));
        return new int[]{constrainedX, constrainedY};
    }

    private boolean isPassingThroughOpening(int x, int y, Room room, Hallway hallway) {
        // Calculate the opening rectangle (intersection of room and hallway)
        int hallLeft = hallway.getX();
        int hallRight = hallway.getX() + (hallway.getOrientation() == Hallway.Orientation.HORIZONTAL ? hallway.getLength() : hallway.getWidth());
        int hallTop = hallway.getY();
        int hallBottom = hallway.getY() + (hallway.getOrientation() == Hallway.Orientation.HORIZONTAL ? hallway.getWidth() : hallway.getLength());

        int roomLeft = room.getX();
        int roomRight = room.getX() + room.getWidth();
        int roomTop = room.getY();
        int roomBottom = room.getY() + room.getHeight();

        // Intersection (the opening)
        int openLeft = Math.max(roomLeft, hallLeft);
        int openRight = Math.min(roomRight, hallRight);
        int openTop = Math.max(roomTop, hallTop);
        int openBottom = Math.min(roomBottom, hallBottom);

        // Check if player overlaps with the opening
        return x < openRight && x + player.getW() > openLeft &&
               y < openBottom && y + player.getL() > openTop;
    }

    private int[] constrainToHallway(int oldX, int oldY, int newX, int newY, Hallway hallway) {
        // Get hallway bounds
        int hallLeft = hallway.getX();
        int hallRight = hallway.getX() + (hallway.getOrientation() == Hallway.Orientation.HORIZONTAL ? hallway.getLength() : hallway.getWidth());
        int hallTop = hallway.getY();
        int hallBottom = hallway.getY() + (hallway.getOrientation() == Hallway.Orientation.HORIZONTAL ? hallway.getWidth() : hallway.getLength());

        // Check if player is trying to leave the hallway
        boolean leavingX = newX < hallLeft || newX + player.getW() > hallRight;
        boolean leavingY = newY < hallTop || newY + player.getL() > hallBottom;

        if (!leavingX && !leavingY) {
            return new int[]{newX, newY}; // Not leaving hallway
        }

        // Check if leaving through a room opening
        if (isPassingThroughOpening(newX, newY, hallway.getStartRoom(), hallway) ||
            isPassingThroughOpening(newX, newY, hallway.getEndRoom(), hallway)) {
            return new int[]{newX, newY};
        }

        // Not leaving through an opening - constrain to hallway bounds
        int constrainedX = Math.max(hallLeft, Math.min(newX, hallRight - player.getW()));
        int constrainedY = Math.max(hallTop, Math.min(newY, hallBottom - player.getL()));
        return new int[]{constrainedX, constrainedY};
    }

    private Hallway findHallwayBetween(Room room1, Room room2) {
        for (Hallway hallway : arena.getHallways()) {
            if ((hallway.getStartRoom() == room1 && hallway.getEndRoom() == room2) ||
                (hallway.getStartRoom() == room2 && hallway.getEndRoom() == room1)) {
                return hallway;
            }
        }
        return null;
    }

    private boolean overlapsWithHallway(int x, int y, Hallway hallway) {
        int hallLeft = hallway.getX();
        int hallRight = hallway.getX() + (hallway.getOrientation() == Hallway.Orientation.HORIZONTAL ? hallway.getLength() : hallway.getWidth());
        int hallTop = hallway.getY();
        int hallBottom = hallway.getY() + (hallway.getOrientation() == Hallway.Orientation.HORIZONTAL ? hallway.getWidth() : hallway.getLength());

        // Calculate overlap area
        int overlapLeft = Math.max(x, hallLeft);
        int overlapRight = Math.min(x + player.getW(), hallRight);
        int overlapTop = Math.max(y, hallTop);
        int overlapBottom = Math.min(y + player.getL(), hallBottom);

        int overlapWidth = overlapRight - overlapLeft;
        int overlapHeight = overlapBottom - overlapTop;
        int overlapArea = overlapWidth * overlapHeight;

        // Require at least 75% overlap to allow transition
        int playerArea = player.getW() * player.getL();
        return overlapArea >= playerArea * 0.75;
    }

    private boolean overlapsWithRoom(int x, int y, Room room) {
        // Calculate overlap area
        int overlapLeft = Math.max(x, room.getX());
        int overlapRight = Math.min(x + player.getW(), room.getX() + room.getWidth());
        int overlapTop = Math.max(y, room.getY());
        int overlapBottom = Math.min(y + player.getL(), room.getY() + room.getHeight());

        int overlapWidth = overlapRight - overlapLeft;
        int overlapHeight = overlapBottom - overlapTop;
        int overlapArea = overlapWidth * overlapHeight;

        // Require at least 75% overlap to allow transition
        int playerArea = player.getW() * player.getL();
        return overlapArea >= playerArea * 0.75;
    }

    private void updateCamera(int screenWidth, int screenHeight) {
        // Determine if player is in a room or hallway
        Room currentRoom = findRoomContainingPlayer();
        Hallway currentHallway = findHallwayContainingPlayer();

        if (currentRoom != null) {
            // Camera centered on room
            targetCameraX = currentRoom.getCenterX() - screenWidth / 2;
            targetCameraY = currentRoom.getCenterY() - screenHeight / 2;
        } else if (currentHallway != null) {
            // Camera follows player on one axis, centered on hallway on other
            if (currentHallway.getOrientation() == Hallway.Orientation.HORIZONTAL) {
                targetCameraX = player.getCenterX() - screenWidth / 2;
                targetCameraY = currentHallway.getY() + currentHallway.getWidth() / 2 - screenHeight / 2;
            } else {
                targetCameraX = currentHallway.getX() + currentHallway.getWidth() / 2 - screenWidth / 2;
                targetCameraY = player.getCenterY() - screenHeight / 2;
            }
        } else {
            // Fallback: follow player
            targetCameraX = player.getCenterX() - screenWidth / 2;
            targetCameraY = player.getCenterY() - screenHeight / 2;
        }
    }

    private void lerpCamera() {
        // Smoothly interpolate camera position toward target
        camera.x += (targetCameraX - camera.x) * CAMERA_LERP_SPEED;
        camera.y += (targetCameraY - camera.y) * CAMERA_LERP_SPEED;
    }

    private Room findRoomContainingPlayer() {
        for (Room room : arena.getRooms()) {
            if (player.getCenterX() >= room.getX() && player.getCenterX() <= room.getX() + room.getWidth() &&
                player.getCenterY() >= room.getY() && player.getCenterY() <= room.getY() + room.getHeight()) {
                return room;
            }
        }
        return null;
    }

    private Hallway findHallwayContainingPlayer() {
        for (Hallway hallway : arena.getHallways()) {
            int hx = hallway.getX();
            int hy = hallway.getY();
            int hw = hallway.getOrientation() == Hallway.Orientation.HORIZONTAL ? hallway.getLength() : hallway.getWidth();
            int hh = hallway.getOrientation() == Hallway.Orientation.HORIZONTAL ? hallway.getWidth() : hallway.getLength();

            if (player.getCenterX() >= hx && player.getCenterX() <= hx + hw &&
                player.getCenterY() >= hy && player.getCenterY() <= hy + hh) {
                return hallway;
            }
        }
        return null;
    }

    public void draw(Graphics2D g, int screenWidth, int screenHeight) {
        // Draw arena (rooms and hallways)
        arena.draw(g, screenWidth, screenHeight, camera.x, camera.y);

        // Draw player
        player.draw(g, camera.x, camera.y);

        // Draw player projectiles
        for (combat.Projectile p : player.getProjectiles()) {
            g.setColor(p.getColor());
            int radius = p.getRadius();
            g.fillOval(p.getX() - camera.x - radius, p.getY() - camera.y - radius, radius * 2, radius * 2);
        }

        // Draw HUD (hotbar, HP, XP)
        hud.draw(g, player, screenWidth, screenHeight);

        // Draw level indicator (top-right corner)
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        String levelText = "Level " + currentLevel + "/5";
        java.awt.FontMetrics fm = g.getFontMetrics();
        int levelWidth = fm.stringWidth(levelText);
        g.drawString(levelText, screenWidth - levelWidth - 20, 40);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        String advanceText = "Press L to advance";
        int advanceWidth = g.getFontMetrics().stringWidth(advanceText);
        g.drawString(advanceText, screenWidth - advanceWidth - 20, 60);
    }

    public void resetMouseClicks(MouseHandler mouse) {
        player.resetMouseClicks(mouse);
    }
}
