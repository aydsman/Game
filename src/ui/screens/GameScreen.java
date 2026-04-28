package ui.screens;

import entity.EnemyManager;
import entity.Player;
import ui.HUD;
import ui.ChestUI;
import util.Camera;
import util.KeyHandler;
import util.MouseHandler;
import world.arenas.ArenaTest;
import world.chests.ArenaChest;
import world.chests.Chest;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class GameScreen {

    ArenaTest arena = new ArenaTest();
    Player player = new Player(arena.getWidth() / 2, arena.getHeight() / 2);
    Camera camera = new Camera();
    HUD hud = new HUD();
    ChestUI chestUI = new ChestUI();
    EnemyManager enemyManager = new EnemyManager();
    ArrayList<Chest> chests = new ArrayList<>();
    Chest activeChest = null;
    combat.Item draggedItem = null;
    int dragSource = -1;
    int dragSourceSlot = -1;
    boolean ePressedLastFrame = false;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private boolean debugMode = false;

    public GameScreen() {
        // spawning enemies
        /*enemyManager.spawnEnemy(500, 1000, 1);
        enemyManager.spawnEnemy(70, 100, 1);
        enemyManager.spawnEnemy(1500, 1500, 1);
        enemyManager.spawnEnemy(1800, 9, 1);
        enemyManager.spawnEnemy(1000, 500, 2);*/

        // Spawn test chests (tiers I-V) next to each other
        int chestBaseX = arena.getWidth() / 2 - 100;
        int chestY = arena.getHeight() / 2;
        for (int tier = 1; tier <= 5; tier++) {
            chests.add(new ArenaChest(chestBaseX + (tier - 1) * 60, chestY, tier));
        }
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
            mouse.leftClicked = false; // Prevent auto-shoot when switching weapons
            mouse.resetScroll();
        }

        // Handle chest interaction with E key
        if (key.ePressed && !ePressedLastFrame) {
            Chest closestChest = findClosestChestInRange();
            if (closestChest != null) {
                if (activeChest == closestChest) {
                    activeChest.setOpen(false);
                    activeChest = null;
                } else {
                    if (activeChest != null) {
                        activeChest.setOpen(false);
                    }
                    activeChest = closestChest;
                    activeChest.setOpen(true);
                }
            } else if (activeChest != null) {
                activeChest.setOpen(false);
                activeChest = null;
            }
        }
        ePressedLastFrame = key.ePressed;

        // Update chest range status and auto-close if out of range
        updateChestRanges();

        // Store mouse position for draw method
        lastMouseX = mouse.mouseX;
        lastMouseY = mouse.mouseY;

        // Handle mouse drag and drop
        handleDragAndDrop(mouse.leftPressed, mouse.leftClicked, mouse.mouseX, mouse.mouseY, screenWidth, screenHeight);

        // Temporarily disable shooting when chest is open or dragging
        boolean wasLeftPressed = mouse.leftPressed;
        if ((activeChest != null && activeChest.isOpen()) || draggedItem != null) {
            mouse.leftClicked = false;
            mouse.leftPressed = false;
        }

        player.update(key, mouse, arenaWidth, arenaHeight);

        // Restore leftPressed so drag doesn't break (don't restore leftClicked - it's one-shot)
        mouse.leftPressed = wasLeftPressed;
        camera.follow(player.getX(), player.getY(), player.getW(), player.getL(), screenWidth, screenHeight, arenaWidth, arenaHeight);
        player.aimBarrel(mouse.mouseX + camera.x, mouse.mouseY + camera.y);
        player.checkProjectileCollisions(enemyManager);
        enemyManager.update(player, arenaWidth, arenaHeight);
        enemyManager.checkEnemyProjectileCollisions(player);
        enemyManager.setDebugMode(debugMode);
    }

    public void draw(Graphics2D g, int screenWidth, int screenHeight) {
        arena.draw(g, screenWidth, screenHeight, camera.x, camera.y);

        // Draw chests
        for (Chest chest : chests) {
            drawChest(g, chest);
        }

        enemyManager.draw(g, camera.x, camera.y);
        enemyManager.drawEnemyProjectiles(g, camera.x, camera.y);
        player.draw(g, camera.x, camera.y);

        // Draw chest UI if active
        if (activeChest != null && activeChest.isOpen()) {
            chestUI.draw(g, activeChest, activeChest.getX() - (int)camera.x, activeChest.getY() - (int)camera.y, screenWidth, screenHeight);
        }

        hud.draw(g, player, screenWidth, screenHeight);

        // Draw player projectiles
        for (combat.Projectile p : player.getProjectiles()) {
            g.setColor(p.getColor());
            int radius = p.getRadius();
            g.fillOval(p.getX() - camera.x - radius, p.getY() - camera.y - radius, radius * 2, radius * 2);
        }

        // Draw dragged item following mouse
        if (draggedItem != null) {
            g.setColor(Color.YELLOW);
            int dragSize = 40;
            g.fillRect(lastMouseX - dragSize / 2, lastMouseY - dragSize / 2, dragSize, dragSize);
            g.setColor(Color.WHITE);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10));
            String name = draggedItem.getName();
            if (name != null) {
                g.drawString(name, lastMouseX - dragSize / 2 + 2, lastMouseY - dragSize / 2 + 14);
            }
        }

        // Draw debug mode status (top right, smaller text)
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        String debugStatus = "debug mode: " + (debugMode ? "on" : "off");
        java.awt.FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(debugStatus);
        g.drawString(debugStatus, screenWidth - textWidth - 10, 20);

    }

    private void drawChest(Graphics2D g, Chest chest) {
        int chestX = chest.getX() - (int)camera.x;
        int chestY = chest.getY() - (int)camera.y;

        // Draw chest (30x30 orange box)
        g.setColor(Color.ORANGE);
        g.fillRect(chestX - 15, chestY - 15, 30, 30);

        // Draw debug radius circle
        if (debugMode) {
            g.setColor(Color.RED);
            g.drawOval(chestX - chest.getInteractionRadius(), chestY - chest.getInteractionRadius(),
                       chest.getInteractionRadius() * 2, chest.getInteractionRadius() * 2);
        }

        // Draw E button if in range
        if (chest.isInRange()) {
            g.setColor(Color.WHITE);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
            String eText = "E";
            int eWidth = g.getFontMetrics().stringWidth(eText);
            int eHeight = g.getFontMetrics().getHeight();
            int eBoxX = chestX - eWidth / 2 - 4;
            int eBoxY = chestY - 25 - eHeight + 2;
            // Draw square outline around E
            g.setColor(Color.WHITE);
            g.drawRect(eBoxX, eBoxY, eWidth + 8, eHeight + 4);
            // Draw E text
            g.drawString(eText, chestX - eWidth / 2, chestY - 25);
        }
    }

    private Chest findClosestChestInRange() {
        Chest closest = null;
        double closestDistance = Double.MAX_VALUE;

        for (Chest chest : chests) {
            double distance = getDistanceToChest(chest);
            if (distance <= chest.getInteractionRadius() && distance < closestDistance) {
                closest = chest;
                closestDistance = distance;
            }
        }
        return closest;
    }

    private double getDistanceToChest(Chest chest) {
        double dx = player.getCenterX() - chest.getX();
        double dy = player.getCenterY() - chest.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void updateChestRanges() {
        for (Chest chest : chests) {
            double distance = getDistanceToChest(chest);
            chest.setInRange(distance <= chest.getInteractionRadius());

            if (activeChest == chest && !chest.isInRange()) {
                chest.setOpen(false);
                activeChest = null;
            }
        }
    }

    private void handleDragAndDrop(boolean leftPressed, boolean leftClicked, int mouseX, int mouseY, int screenWidth, int screenHeight) {
        if (activeChest == null || !activeChest.isOpen() || !activeChest.isInRange()) {
            draggedItem = null;
            dragSource = -1;
            dragSourceSlot = -1;
            return;
        }

        // Mouse press - start drag
        if (leftPressed && draggedItem == null) {
            int inventorySlot = getInventorySlotAtPosition(mouseX, mouseY, 1, 1, screenWidth, screenHeight);
            if (inventorySlot >= 0 && inventorySlot < player.getHotbar().size()) {
                Object item = player.getHotbar().get(inventorySlot);
                if (item instanceof combat.Item) {
                    draggedItem = (combat.Item) item;
                    dragSource = -1;
                    dragSourceSlot = inventorySlot;
                }
            }

            if (draggedItem == null) {
                int chestSlot = chestUI.getSlotAtPosition(mouseX, mouseY, activeChest, activeChest.getX() - (int)camera.x, activeChest.getY() - (int)camera.y);
                if (chestSlot >= 0) {
                    draggedItem = activeChest.getItem(chestSlot);
                    if (draggedItem != null) {
                        dragSource = 0;
                        dragSourceSlot = chestSlot;
                    }
                }
            }
        }

        // Mouse release - end drag
        if (!leftPressed && draggedItem != null) {
            int dragSize = 40;
            int itemX = mouseX - dragSize / 2;
            int itemY = mouseY - dragSize / 2;

            boolean placed = false;

            // Check inventory slots with overlap
            int inventorySlot = getInventorySlotAtPosition(itemX, itemY, dragSize, dragSize, screenWidth, screenHeight);
            if (inventorySlot >= 0 && inventorySlot < player.getHotbar().size()) {
                if (dragSource == 0) {
                    // From chest to inventory
                    Object destItem = player.getHotbar().get(inventorySlot);
                    if (destItem == null) {
                        player.getHotbar().set(inventorySlot, draggedItem);
                        activeChest.setItem(dragSourceSlot, null);
                    } else if (destItem instanceof combat.Item) {
                        activeChest.setItem(dragSourceSlot, (combat.Item) destItem);
                        player.getHotbar().set(inventorySlot, draggedItem);
                    }
                    placed = true;
                } else if (dragSource == -1 && inventorySlot != dragSourceSlot) {
                    // From inventory to different inventory slot - swap
                    Object destItem = player.getHotbar().get(inventorySlot);
                    player.getHotbar().set(dragSourceSlot, destItem);
                    player.getHotbar().set(inventorySlot, draggedItem);
                    placed = true;
                }
            }

            // Check chest slots with overlap
            if (!placed) {
                int chestSlot = chestUI.getSlotAtPosition(itemX, itemY, dragSize, dragSize, activeChest, activeChest.getX() - (int)camera.x, activeChest.getY() - (int)camera.y);
                if (chestSlot >= 0) {
                    if (dragSource == -1) {
                        // From inventory to chest
                        combat.Item destItem = activeChest.getItem(chestSlot);
                        if (destItem == null) {
                            activeChest.setItem(chestSlot, draggedItem);
                            player.getHotbar().set(dragSourceSlot, null);
                        } else {
                            player.getHotbar().set(dragSourceSlot, destItem);
                            activeChest.setItem(chestSlot, draggedItem);
                        }
                        placed = true;
                    } else if (dragSource == 0 && chestSlot != dragSourceSlot) {
                        // From chest to different chest slot - swap
                        combat.Item destItem = activeChest.getItem(chestSlot);
                        activeChest.setItem(dragSourceSlot, destItem);
                        activeChest.setItem(chestSlot, draggedItem);
                        placed = true;
                    }
                }
            }

            // If not placed on any valid slot, return to source
            // (item stays where it was since we never removed it)
            draggedItem = null;
            dragSource = -1;
            dragSourceSlot = -1;

            // Re-equip selected hotbar slot to update held weapon/barrel
            player.equipHotbarSlot(hud.getInventoryUI().getSelectedSlot());
        }
    }

    private int getInventorySlotAtPosition(int itemX, int itemY, int itemW, int itemH, int screenWidth, int screenHeight) {
        int slotSize = 50;
        int slotSpacing = 5;
        int totalWidth = (slotSize * 5) + (slotSpacing * 4);
        int startX = (screenWidth - totalWidth) / 2;
        int startY = screenHeight - slotSize - 20;

        int bestSlot = -1;
        int bestOverlap = 0;

        for (int i = 0; i < 5; i++) {
            int slotX = startX + (i * (slotSize + slotSpacing));
            int overlapX = Math.max(0, Math.min(itemX + itemW, slotX + slotSize) - Math.max(itemX, slotX));
            int overlapY = Math.max(0, Math.min(itemY + itemH, startY + slotSize) - Math.max(itemY, startY));
            int overlapArea = overlapX * overlapY;

            if (overlapArea > bestOverlap) {
                bestOverlap = overlapArea;
                bestSlot = i;
            }
        }
        return bestSlot;
    }
}
