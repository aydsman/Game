package ui.screens;

import entity.EnemyManager;
import entity.Player;
import ui.HUD;
import ui.ChestUI;
import util.Camera;
import util.KeyHandler;
import util.MouseHandler;
import world.arena.arenas.ArenaTest;
import world.arena.WaveManager;
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
    WaveManager waveManager;
    ArrayList<Chest> chests = new ArrayList<>();
    Chest activeChest = null;
    combat.Item draggedItem = null;
    int dragSource = -1;
    int dragSourceSlot = -1;
    boolean wasLeftPressedLastFrame = false;
    boolean ePressedLastFrame = false;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private boolean debugMode = false;
    private boolean wavesEnabled = true; // Set to false to disable waves for debugging
    private boolean statsPanelVisible = false; // Toggle with U key for debugging

    public GameScreen() {
        waveManager = new WaveManager(enemyManager, player, arena.getWidth(), arena.getHeight());
        waveManager.setEnabled(wavesEnabled);

        // spawning enemies
        /*enemyManager.spawnEnemy(500, 1000, 1);
        enemyManager.spawnEnemy(70, 100, 1);
        enemyManager.spawnEnemy(1500, 1500, 1);
        enemyManager.spawnEnemy(1800, 9, 1);
        enemyManager.spawnEnemy(1000, 500, 2);*/

        // Chests removed - waves enabled
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

        // Toggle stats panel with U key
        if (key.uPressed) {
            statsPanelVisible = !statsPanelVisible;
            key.uPressed = false;
        }

        // Kill all enemies with K key (debug)
        if (key.kPressed) {
            enemyManager.killAllEnemies();
            key.kPressed = false;
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

        // Use consumable on left-click when equipped
        if (mouse.leftClicked) {
            int selectedSlot = hud.getInventoryUI().getSelectedSlot();
            if (selectedSlot >= 0 && selectedSlot < 5) {
                Object item = player.getHotbar().get(selectedSlot);
                if (item instanceof combat.consumables.Consumable) {
                    combat.consumables.Consumable consumable = (combat.consumables.Consumable) item;
                    double healAmount = player.getMaxHp() * consumable.getHealthRestorePercent();
                    player.heal(healAmount);
                    player.getHotbar().set(selectedSlot, null);
                    player.equipHotbarSlot(selectedSlot);
                    mouse.leftClicked = false; // Prevent shooting after using consumable
                }
            }
        }

        player.update(key, mouse, arenaWidth, arenaHeight);

        // Restore leftPressed so drag doesn't break (don't restore leftClicked - it's one-shot)
        mouse.leftPressed = wasLeftPressed;
        camera.follow(player.getX(), player.getY(), player.getW(), player.getL(), screenWidth, screenHeight, arenaWidth, arenaHeight);
        player.setCameraOffset(camera.x, camera.y);
        player.aimBarrel(mouse.mouseX + camera.x, mouse.mouseY + camera.y);

        // Check melee swing collisions
        if (player.getHeldWeapon() instanceof combat.Melee) {
            checkMeleeCollisions(player, enemyManager);
        }
        player.checkProjectileCollisions(enemyManager);
        enemyManager.update(player, arenaWidth, arenaHeight);
        enemyManager.updateBoss(player, arenaWidth, arenaHeight);
        enemyManager.checkEnemyProjectileCollisions(player);

        // Check boss melee collision
        if (player.getHeldWeapon() instanceof combat.Melee) {
            checkMeleeBossCollision(player, enemyManager);
        }
        enemyManager.setDebugMode(debugMode);
        player.setDebugMode(debugMode);
        if (wavesEnabled) {
            WaveManager.WaveState prevState = waveManager.getState();
            waveManager.update(key.fPressed);
            WaveManager.WaveState newState = waveManager.getState();

            // Spawn chest when wave completes (transitions from ACTIVE to GRACE_PERIOD or COMPLETED)
            if (prevState == WaveManager.WaveState.ACTIVE &&
                (newState == WaveManager.WaveState.GRACE_PERIOD || newState == WaveManager.WaveState.COMPLETED)) {
                int waveJustCompleted = waveManager.getCurrentWave();
                int chestTier = waveManager.getChestTierForWave(waveJustCompleted);
                int centerX = arena.getWidth() / 2;
                int centerY = arena.getHeight() / 2;
                chests.add(new ArenaChest(centerX, centerY, chestTier));
            }

            // Clear chests when next wave starts (transition from GRACE_PERIOD to ACTIVE)
            if (prevState == WaveManager.WaveState.GRACE_PERIOD &&
                newState == WaveManager.WaveState.ACTIVE) {
                chests.clear();
                activeChest = null;
            }
        }

        // Track player death
        if (player.isDead() && player.getStats().getDeaths() == 0) {
            player.getStats().addDeath();
        }
    }

    public void draw(Graphics2D g, int screenWidth, int screenHeight) {
        arena.draw(g, screenWidth, screenHeight, camera.x, camera.y);

        // Draw chests
        for (Chest chest : chests) {
            drawChest(g, chest);
        }

        enemyManager.draw(g, camera.x, camera.y);
        enemyManager.drawBoss(g, camera.x, camera.y);
        enemyManager.drawEnemyProjectiles(g, camera.x, camera.y);

        // Draw melee swing arc
        drawMeleeSwing(g, player, camera.x, camera.y);

        player.draw(g, camera.x, camera.y);

        // Draw boss healthbar if active
        if (enemyManager.getActiveBoss() != null) {
            enemyManager.getActiveBoss().drawHealthBar(g, screenWidth, screenHeight);
        }

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

        // Draw wave number (top right, below debug status)
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        String waveText;
        if (waveManager.isInGracePeriod()) {
            int seconds = waveManager.getGracePeriodRemaining() / 60;
            waveText = "Next wave in: " + seconds + "s (F to skip)";
        } else {
            waveText = "Wave " + waveManager.getCurrentWave();
        }
        fm = g.getFontMetrics();
        textWidth = fm.stringWidth(waveText);
        g.drawString(waveText, screenWidth - textWidth - 10, 45);

        // Draw stats panel if visible (bottom left corner)
        if (statsPanelVisible) {
            drawStatsPanel(g, 10, screenHeight - 160);
        }

    }

    private void drawStatsPanel(Graphics2D g, int x, int y) {
        entity.PlayerStats stats = player.getStats();

        // Panel background
        int panelWidth = 200;
        int panelHeight = 140;
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(x, y, panelWidth, panelHeight);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, panelWidth, panelHeight);

        // Title
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        g.drawString("=== STATS ===", x + 10, y + 20);

        // Stats
        g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        int lineY = y + 40;
        int lineHeight = 16;

        g.drawString("Kills: " + stats.getKills(), x + 10, lineY);
        lineY += lineHeight;
        g.drawString("Deaths: " + stats.getDeaths(), x + 10, lineY);
        lineY += lineHeight;
        g.drawString("Damage Dealt: " + (int)stats.getDamageDealt(), x + 10, lineY);
        lineY += lineHeight;
        g.drawString("Damage Taken: " + (int)stats.getDamageTaken(), x + 10, lineY);
        lineY += lineHeight;
        g.drawString("Accuracy: " + String.format("%.1f", stats.getAccuracy()) + "%", x + 10, lineY);
        lineY += lineHeight;
        g.drawString("Shots: " + stats.getShotsHit() + "/" + stats.getShotsFired(), x + 10, lineY);
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

        // Mouse press - start drag (only on first frame of press)
        if (leftPressed && draggedItem == null && !wasLeftPressedLastFrame) {
            int inventorySlot = getInventorySlotAtPositionSimple(mouseX, mouseY, screenWidth, screenHeight);
            if (inventorySlot >= 0 && inventorySlot < 5) {
                Object item = player.getHotbar().get(inventorySlot);
                if (item instanceof combat.Item) {
                    draggedItem = (combat.Item) item;
                    dragSource = -1;
                    dragSourceSlot = inventorySlot;
                }
            }

            if (draggedItem == null) {
                int chestX = activeChest.getX() - (int)camera.x;
                int chestY = activeChest.getY() - (int)camera.y;
                int chestSlot = chestUI.getSlotAtPosition(mouseX, mouseY, activeChest, chestX, chestY);
                if (chestSlot >= 0) {
                    draggedItem = activeChest.getItem(chestSlot);
                    if (draggedItem != null) {
                        dragSource = 0;
                        dragSourceSlot = chestSlot;
                    }
                }
            }
        }
        // Also check for drag start on subsequent frames if not dragging yet (for when mouse moves onto item while holding)
        if (leftPressed && draggedItem == null && wasLeftPressedLastFrame) {
            int inventorySlot = getInventorySlotAtPositionSimple(mouseX, mouseY, screenWidth, screenHeight);
            if (inventorySlot >= 0 && inventorySlot < 5) {
                Object item = player.getHotbar().get(inventorySlot);
                if (item instanceof combat.Item) {
                    draggedItem = (combat.Item) item;
                    dragSource = -1;
                    dragSourceSlot = inventorySlot;
                }
            }
        }
        wasLeftPressedLastFrame = leftPressed;

        // Mouse release - end drag
        if (!leftPressed && draggedItem != null) {
            boolean placed = false;

            // Check inventory slots with simple point detection
            int inventorySlot = getInventorySlotAtPositionSimple(mouseX, mouseY, screenWidth, screenHeight);
            if (inventorySlot >= 0 && inventorySlot < 5) {
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
                int chestX = activeChest.getX() - (int)camera.x;
                int chestY = activeChest.getY() - (int)camera.y;
                int chestSlot = chestUI.getSlotAtPosition(mouseX, mouseY, activeChest, chestX, chestY);
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
        int hotbarSize = 5;
        int totalWidth = (slotSize * hotbarSize) + (slotSpacing * (hotbarSize - 1));
        int startX = (screenWidth - totalWidth) / 2;
        int startY = screenHeight - slotSize - 20;

        int bestSlot = -1;
        int bestOverlap = 0;
        int minOverlap = 100; // Minimum overlap area to register (10x10 pixels)

        for (int i = 0; i < 5; i++) {
            int slotX = startX + (i * (slotSize + slotSpacing));
            int overlapX = Math.max(0, Math.min(itemX + itemW, slotX + slotSize) - Math.max(itemX, slotX));
            int overlapY = Math.max(0, Math.min(itemY + itemH, startY + slotSize) - Math.max(itemY, startY));
            int overlapArea = overlapX * overlapY;

            if (overlapArea > bestOverlap && overlapArea >= minOverlap) {
                bestOverlap = overlapArea;
                bestSlot = i;
            }
        }
        return bestSlot;
    }

    private int getInventorySlotAtPositionSimple(int mouseX, int mouseY, int screenWidth, int screenHeight) {
        int slotSize = 50;
        int slotSpacing = 5;
        int hotbarSize = 5;
        int totalWidth = (slotSize * hotbarSize) + (slotSpacing * (hotbarSize - 1));
        int startX = (screenWidth - totalWidth) / 2;
        int startY = screenHeight - slotSize - 20;

        for (int i = 0; i < 5; i++) {
            int slotX = startX + (i * (slotSize + slotSpacing));
            if (mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= startY && mouseY < startY + slotSize) {
                return i;
            }
        }
        return -1;
    }

    private void checkMeleeCollisions(Player player, entity.EnemyManager enemyManager) {
        if (!(player.getHeldWeapon() instanceof combat.Melee)) return;
        combat.Melee melee = (combat.Melee) player.getHeldWeapon();
        if (!melee.isSwinging()) return;

        int playerX = player.getCenterX();
        int playerY = player.getCenterY();

        for (entity.Enemy enemy : enemyManager.getEnemies()) {
            // Skip if already hit this swing
            if (melee.getHitEntitiesThisSwing().contains(enemy)) continue;

            // Check if enemy is within swing arc
            if (melee.isInSwingArc(enemy.getCenterX(), enemy.getCenterY(), playerX, playerY)) {
                enemy.takeDamage(melee.getDamage());
                player.getStats().addDamageDealt(melee.getDamage());
                melee.getHitEntitiesThisSwing().add(enemy);

                if (enemy.isDead()) {
                    player.getStats().addKill();
                }
            }
        }
    }

    private void checkMeleeBossCollision(Player player, entity.EnemyManager enemyManager) {
        if (!(player.getHeldWeapon() instanceof combat.Melee)) return;
        combat.Melee melee = (combat.Melee) player.getHeldWeapon();
        if (!melee.isSwinging()) return;

        entity.Boss boss = enemyManager.getActiveBoss();
        if (boss == null) return;

        // Check if boss was already hit this swing
        if (melee.getHitEntitiesThisSwing().contains(boss)) return;

        int playerX = player.getCenterX();
        int playerY = player.getCenterY();

        if (melee.isInSwingArc(boss.getCenterX(), boss.getCenterY(), playerX, playerY)) {
            boss.takeDamage(melee.getDamage());
            player.getStats().addDamageDealt(melee.getDamage());
            melee.getHitEntitiesThisSwing().add(boss);
        }
    }

    public void drawMeleeSwing(Graphics2D g, Player player, int cameraX, int cameraY) {
        if (player.getHeldWeapon() instanceof combat.Melee) {
            combat.Melee melee = (combat.Melee) player.getHeldWeapon();
            if (melee.isSwinging()) {
                int playerX = player.getCenterX() - cameraX;
                int playerY = player.getCenterY() - cameraY;
                double range = melee.getRange();

                // Draw the swing arc
                double startAngleRad = melee.getSwingAngleStart();
                double endAngleRad = melee.getSwingAngleEnd();

                // Convert to degrees for drawing (Swing uses 0 at 3 o'clock, counter-clockwise)
                // Java arc angles: 0 is at 3 o'clock, positive is counter-clockwise
                double startAngleDeg = Math.toDegrees(startAngleRad);
                double endAngleDeg = Math.toDegrees(endAngleRad);

                // Normalize to draw the arc properly
                double arcStartDeg;
                double arcExtentDeg;

                if (Math.abs(endAngleDeg - startAngleDeg) <= 180) {
                    arcStartDeg = startAngleDeg;
                    arcExtentDeg = endAngleDeg - startAngleDeg;
                } else {
                    // Arc crosses 0/360 boundary, adjust
                    arcStartDeg = endAngleDeg;
                    arcExtentDeg = startAngleDeg - endAngleDeg;
                }

                // Normalize arc start to 0-360
                while (arcStartDeg < 0) arcStartDeg += 360;
                while (arcStartDeg > 360) arcStartDeg -= 360;

                // Flip for screen coordinates (Y increases downward)
                arcStartDeg = -arcStartDeg - arcExtentDeg;

                // Draw filled arc with semi-transparent color
                g.setColor(new Color(255, 100, 100, 100));
                g.fillArc((int)(playerX - range), (int)(playerY - range),
                         (int)(range * 2), (int)(range * 2),
                         (int)arcStartDeg, (int)arcExtentDeg);

                // Draw arc outline
                g.setColor(new Color(255, 50, 50, 200));
                g.drawArc((int)(playerX - range), (int)(playerY - range),
                         (int)(range * 2), (int)(range * 2),
                         (int)arcStartDeg, (int)arcExtentDeg);

                // Draw swing line showing current position
                double currentAngle = melee.getCurrentSwingAngle();
                int lineEndX = playerX + (int)(range * Math.cos(currentAngle));
                int lineEndY = playerY + (int)(range * Math.sin(currentAngle));
                g.setColor(Color.RED);
                g.drawLine(playerX, playerY, lineEndX, lineEndY);
            }
        }
    }
}
