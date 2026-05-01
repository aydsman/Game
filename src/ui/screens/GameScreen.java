package ui.screens;

import combat.charms.Charm;
import entity.EnemyManager;
import entity.Player;
import combat.powers.Power;
import combat.powers.Move;
import ui.HUD;
import ui.ChestUI;
import ui.InventoryScreen;
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
    InventoryScreen inventoryScreen = new InventoryScreen();
    EnemyManager enemyManager = new EnemyManager();
    WaveManager waveManager;
    ArrayList<Chest> chests = new ArrayList<>();
    Chest activeChest = null;
    combat.Item draggedItem = null;
    int dragSource = -1;
    int dragSourceSlot = -1;
    boolean wasLeftPressedLastFrame = false;
    boolean ePressedLastFrame = false;
    boolean inventoryOpen = false;
    boolean tabPressedLastFrame = false;
    boolean onePressedLastFrame = false;
    boolean twoPressedLastFrame = false;
    boolean threePressedLastFrame = false;
    boolean fourPressedLastFrame = false;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private boolean debugMode = false;
    private boolean wavesEnabled = false; // Disable waves for testing
    private boolean statsPanelVisible = false; // Toggle with U key for debugging

    public GameScreen() {
        waveManager = new WaveManager(enemyManager, player, arena.getWidth(), arena.getHeight());
        waveManager.setEnabled(wavesEnabled);

        // Give player Fire power for testing
        player.getInventory().equipPower(new combat.powers.Fire());

        // spawning enemies
        /*enemyManager.spawnEnemy(500, 1000, 1);
        enemyManager.spawnEnemy(70, 100, 1);
        enemyManager.spawnEnemy(1500, 1500, 1);
        enemyManager.spawnEnemy(1800, 9, 1);
        enemyManager.spawnEnemy(1000, 500, 2);*/

        // Chests spawn via waves
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

        // Handle power moves with keys 1-4
        handlePowerMoves(key);

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
                    inventoryOpen = false; // Close inventory when closing chest
                } else {
                    if (activeChest != null) {
                        activeChest.setOpen(false);
                    }
                    activeChest = closestChest;
                    activeChest.setOpen(true);
                    inventoryOpen = true; // Open inventory when opening chest
                }
            } else if (activeChest != null) {
                activeChest.setOpen(false);
                activeChest = null;
                inventoryOpen = false; // Close inventory when closing chest
            }
        }
        ePressedLastFrame = key.ePressed;

        // Handle inventory toggle with Tab key
        if (key.tabPressed && !tabPressedLastFrame) {
            inventoryOpen = !inventoryOpen;
            // Close chest if opening inventory
            if (inventoryOpen && activeChest != null) {
                activeChest.setOpen(false);
                activeChest = null;
            }
        }
        tabPressedLastFrame = key.tabPressed;

        // Update power key states
        onePressedLastFrame = key.onePressed;
        twoPressedLastFrame = key.twoPressed;
        threePressedLastFrame = key.threePressed;
        fourPressedLastFrame = key.fourPressed;

        // Update chest range status and auto-close if out of range
        updateChestRanges();

        // Update chest hover
        if (activeChest != null && activeChest.isOpen()) {
            int chestX = activeChest.getX() - (int)camera.x;
            int chestY = activeChest.getY() - (int)camera.y;
            int chestSlot = chestUI.getSlotAtPosition(mouse.mouseX, mouse.mouseY, activeChest, chestX, chestY);
            chestUI.setHoveredSlot(chestSlot);
        } else {
            chestUI.setHoveredSlot(-1);
        }

        // Store mouse position for draw method
        lastMouseX = mouse.mouseX;
        lastMouseY = mouse.mouseY;

        // Update inventory hover
        if (inventoryOpen) {
            int playerScreenX = player.getCenterX() - camera.x;
            int playerScreenY = player.getCenterY() - camera.y;
            int chestX = (activeChest != null && activeChest.isOpen()) ? activeChest.getX() - (int)camera.x : 0;
            int chestY = (activeChest != null && activeChest.isOpen()) ? activeChest.getY() - (int)camera.y : 0;
            boolean chestOpen = (activeChest != null && activeChest.isOpen());
            inventoryScreen.updatePositions(playerScreenX, playerScreenY, screenWidth, screenHeight, chestX, chestY, chestOpen);
            int invSlot = inventoryScreen.getSlotAtPosition(mouse.mouseX, mouse.mouseY);
            inventoryScreen.setHoveredSlot(invSlot);
        } else {
            inventoryScreen.setHoveredSlot(-1);
        }

        // Handle mouse drag and drop
        handleDragAndDrop(mouse.leftPressed, mouse.leftClicked, mouse.mouseX, mouse.mouseY, screenWidth, screenHeight);

        // Temporarily disable shooting when chest is open, inventory is open, or dragging
        boolean wasLeftPressed = mouse.leftPressed;
        if ((activeChest != null && activeChest.isOpen()) || inventoryOpen || draggedItem != null) {
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

        // Draw inventory UI if open
        if (inventoryOpen) {
            int playerScreenX = player.getCenterX() - camera.x;
            int playerScreenY = player.getCenterY() - camera.y;
            int chestX = (activeChest != null && activeChest.isOpen()) ? activeChest.getX() - (int)camera.x : 0;
            int chestY = (activeChest != null && activeChest.isOpen()) ? activeChest.getY() - (int)camera.y : 0;
            boolean chestOpen = (activeChest != null && activeChest.isOpen());
            inventoryScreen.draw(g, player.getInventory(), playerScreenX, playerScreenY, screenWidth, screenHeight, chestX, chestY, chestOpen);
        }

        hud.draw(g, player, screenWidth, screenHeight);

        // Draw power moves UI if power is equipped
        drawPowerMovesUI(g, screenWidth, screenHeight);

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

        // Draw debug commands when debug mode is on (bottom right)
        if (debugMode) {
            g.setColor(new Color(255, 255, 0, 200)); // Yellow, semi-transparent
            g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
            String[] commands = {
                "DEBUG COMMANDS:",
                "K - Kill all enemies",
                "U - Toggle stats panel",
                "O - Toggle debug mode",
                "F - Skip grace period"
            };
            int cmdY = screenHeight - 100;
            for (String cmd : commands) {
                int cmdTextWidth = fm.stringWidth(cmd);
                g.drawString(cmd, screenWidth - cmdTextWidth - 10, cmdY);
                cmdY += 16;
            }
        }

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
            drawStatsPanel(g, 10, screenHeight - 190);
        }

    }

    private void drawStatsPanel(Graphics2D g, int x, int y) {
        entity.PlayerStats stats = player.getStats();

        // Calculate multipliers
        int charmCount = 0;
        for (int i = 0; i < player.getInventory().getMaxCharms(); i++) {
            if (player.getInventory().getCharm(i) != null) {
                charmCount++;
            }
        }
        int hpMultiplier = charmCount * 10; // Each charm gives 10%
        double damageMultiplier = (player.getDamage() - 1.0) * 100; // Convert to percentage
        double speedMultiplier = (player.getSpeed() - 7.0) / 7.0 * 100; // Base speed is 7.0

        // Panel background
        int panelWidth = 200;
        int panelHeight = 180;
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

        g.drawString("HP Multiplier: +" + hpMultiplier + "%", x + 10, lineY);
        lineY += lineHeight;
        g.drawString("Damage Multiplier: +" + (int)damageMultiplier + "%", x + 10, lineY);
        lineY += lineHeight;
        g.drawString("Speed Multiplier: +" + (int)speedMultiplier + "%", x + 10, lineY);
        lineY += lineHeight;
        g.drawString("---", x + 10, lineY);
        lineY += lineHeight;
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
                inventoryOpen = false; // Close inventory when walking away from chest
            }
        }
    }

    private void handleDragAndDrop(boolean leftPressed, boolean leftClicked, int mouseX, int mouseY, int screenWidth, int screenHeight) {
        // Reset drag if neither chest nor inventory is open
        if ((activeChest == null || !activeChest.isOpen() || !activeChest.isInRange()) && !inventoryOpen) {
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

            if (draggedItem == null && activeChest != null && activeChest.isOpen()) {
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

            // Check inventory slots for drag start
            if (draggedItem == null && inventoryOpen) {
                // Update positions before checking slot positions
                int playerScreenX = player.getCenterX() - camera.x;
                int playerScreenY = player.getCenterY() - camera.y;
                int chestX = (activeChest != null && activeChest.isOpen()) ? activeChest.getX() - (int)camera.x : 0;
            int chestY = (activeChest != null && activeChest.isOpen()) ? activeChest.getY() - (int)camera.y : 0;
            boolean chestOpen = (activeChest != null && activeChest.isOpen());
            inventoryScreen.updatePositions(playerScreenX, playerScreenY, screenWidth, screenHeight, chestX, chestY, chestOpen);
                int invSlot = inventoryScreen.getSlotAtPosition(mouseX, mouseY);
                if (invSlot >= 0 && invSlot <= 4) {
                    if (invSlot >= 0 && invSlot <= 2) {
                        // Charm slot
                        Charm charm = player.getInventory().getCharm(invSlot);
                        if (charm != null) {
                            draggedItem = charm;
                            dragSource = 1; // 1 = inventory charm
                            dragSourceSlot = invSlot;
                        }
                    } else if (invSlot == 3) {
                        // Power slot
                        Power power = player.getInventory().getPower();
                        if (power != null) {
                            draggedItem = power;
                            dragSource = 2; // 2 = inventory power
                            dragSourceSlot = invSlot;
                        }
                    } else if (invSlot == 4) {
                        // Summon slot
                        combat.summons.Summon summon = player.getInventory().getSummon();
                        if (summon != null) {
                            draggedItem = summon;
                            dragSource = 3; // 3 = inventory summon
                            dragSourceSlot = invSlot;
                        }
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
                } else if (dragSource >= 1 && dragSource <= 3) {
                    // From inventory slots to hotbar
                    Object destItem = player.getHotbar().get(inventorySlot);
                    if (destItem == null) {
                        player.getHotbar().set(inventorySlot, draggedItem);
                        removeFromInventorySlot(dragSource, dragSourceSlot);
                        placed = true;
                    }
                    // If destination is occupied, don't allow drop (no stacking)
                }
            }

            // Check inventory slots (charm/power/summon)
            if (!placed && inventoryOpen) {
                // Update positions before checking slot positions
                int playerScreenX = player.getCenterX() - camera.x;
                int playerScreenY = player.getCenterY() - camera.y;
                int chestX = (activeChest != null && activeChest.isOpen()) ? activeChest.getX() - (int)camera.x : 0;
            int chestY = (activeChest != null && activeChest.isOpen()) ? activeChest.getY() - (int)camera.y : 0;
            boolean chestOpen = (activeChest != null && activeChest.isOpen());
            inventoryScreen.updatePositions(playerScreenX, playerScreenY, screenWidth, screenHeight, chestX, chestY, chestOpen);
                int invSlot = inventoryScreen.getSlotAtPosition(mouseX, mouseY);
                if (invSlot >= 0 && invSlot <= 4 && inventoryScreen.isValidDrop(invSlot, draggedItem)) {
                    if (dragSource == -1) {
                        // From hotbar to inventory
                        if (invSlot >= 0 && invSlot <= 2) {
                            // Charm slot - only allow if empty
                            if (player.getInventory().getCharm(invSlot) == null) {
                                player.getInventory().equipCharm(invSlot, (Charm) draggedItem);
                                player.getHotbar().set(dragSourceSlot, null);
                                placed = true;
                            }
                        } else if (invSlot == 3) {
                            // Power slot - only allow if empty
                            if (player.getInventory().getPower() == null) {
                                player.getInventory().equipPower((Power) draggedItem);
                                player.getHotbar().set(dragSourceSlot, null);
                                placed = true;
                            }
                        } else if (invSlot == 4) {
                            // Summon slot - only allow if empty
                            if (player.getInventory().getSummon() == null) {
                                player.getInventory().equipSummon((combat.summons.Summon) draggedItem);
                                player.getHotbar().set(dragSourceSlot, null);
                                placed = true;
                            }
                        }
                    } else if (dragSource == 0) {
                        // From chest to inventory
                        if (invSlot >= 0 && invSlot <= 2) {
                            // Charm slot - only allow if empty
                            if (player.getInventory().getCharm(invSlot) == null) {
                                player.getInventory().equipCharm(invSlot, (Charm) draggedItem);
                                activeChest.setItem(dragSourceSlot, null);
                                placed = true;
                            }
                        } else if (invSlot == 3) {
                            // Power slot - only allow if empty
                            if (player.getInventory().getPower() == null) {
                                player.getInventory().equipPower((Power) draggedItem);
                                activeChest.setItem(dragSourceSlot, null);
                                placed = true;
                            }
                        } else if (invSlot == 4) {
                            // Summon slot - only allow if empty
                            if (player.getInventory().getSummon() == null) {
                                player.getInventory().equipSummon((combat.summons.Summon) draggedItem);
                                activeChest.setItem(dragSourceSlot, null);
                                placed = true;
                            }
                        }
                    } else if (dragSource >= 1 && dragSource <= 3 && invSlot != dragSourceSlot) {
                        // Between inventory slots - only allow if destination is empty
                        if (invSlot >= 0 && invSlot <= 2 && dragSource == 1) {
                            // Charm to charm - only if empty
                            if (player.getInventory().getCharm(invSlot) == null) {
                                player.getInventory().removeCharm(dragSourceSlot);
                                player.getInventory().equipCharm(invSlot, (Charm) draggedItem);
                                placed = true;
                            }
                        } else if (invSlot == 3 && dragSource == 2) {
                            // Power to power (only one slot, so this shouldn't happen)
                        } else if (invSlot == 4 && dragSource == 3) {
                            // Summon to summon (only one slot, so this shouldn't happen)
                        }
                    }
                }
            }

            // Check chest slots with overlap
            if (!placed && activeChest != null && activeChest.isOpen()) {
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

    private void removeFromInventorySlot(int dragSource, int dragSourceSlot) {
        if (dragSource == 1) {
            // Charm slot
            player.getInventory().removeCharm(dragSourceSlot);
        } else if (dragSource == 2) {
            // Power slot
            player.getInventory().removePower();
        } else if (dragSource == 3) {
            // Summon slot
            player.getInventory().removeSummon();
        }
    }

    private void placeInInventorySlot(combat.Item item, int dragSource, int dragSourceSlot) {
        if (dragSource == 1 && item instanceof Charm) {
            player.getInventory().equipCharm(dragSourceSlot, (Charm) item);
        } else if (dragSource == 2 && item instanceof Power) {
            player.getInventory().equipPower((Power) item);
        } else if (dragSource == 3 && item instanceof combat.summons.Summon) {
            player.getInventory().equipSummon((combat.summons.Summon) item);
        }
    }

    private boolean canPlaceInInventorySlot(combat.Item item, int dragSource, int dragSourceSlot) {
        if (dragSource == 1) {
            return item instanceof Charm;
        } else if (dragSource == 2) {
            return item instanceof Power;
        } else if (dragSource == 3) {
            return item instanceof combat.summons.Summon;
        }
        return false;
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

    private void handlePowerMoves(util.KeyHandler key) {
        // Only process if a power is equipped
        Power equippedPower = player.getInventory().getPower();
        if (equippedPower == null) return;

        // Handle key presses 1-4 for power moves
        if (key.onePressed && !onePressedLastFrame) {
            equippedPower.useMove(1);
        }
        if (key.twoPressed && !twoPressedLastFrame) {
            equippedPower.useMove(2);
        }
        if (key.threePressed && !threePressedLastFrame) {
            equippedPower.useMove(3);
        }
        if (key.fourPressed && !fourPressedLastFrame) {
            equippedPower.useMove(4);
        }
    }

    private void drawPowerMovesUI(Graphics2D g, int screenWidth, int screenHeight) {
        // Only draw if a power is equipped
        Power equippedPower = player.getInventory().getPower();
        if (equippedPower == null) return;

        int maxMoves = equippedPower.getMaxMoves();
        if (maxMoves <= 0) return;

        // Box dimensions
        int boxWidth = 200;
        int boxHeight = 40;
        int boxSpacing = 5;
        int padding = 20;

        // Starting position (bottom-right corner)
        int startX = screenWidth - boxWidth - padding;

        // Draw from top to bottom (highest slot first)
        for (int i = maxMoves; i >= 1; i--) {
            Move move = equippedPower.getMove(i);
            if (move == null) continue;

            // Calculate Y position (slot maxMoves at top, slot 1 at bottom)
            int slotIndexFromBottom = maxMoves - i;
            int y = screenHeight - padding - boxHeight - (slotIndexFromBottom * (boxHeight + boxSpacing));

            // Draw box background
            g.setColor(new Color(50, 50, 70, 200)); // Semi-transparent dark blue
            g.fillRect(startX, y, boxWidth, boxHeight);

            // Draw box border
            g.setColor(Color.WHITE);
            g.drawRect(startX, y, boxWidth, boxHeight);

            // Draw move name
            g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
            g.setColor(Color.WHITE);
            String moveName = move.getName();
            g.drawString(moveName, startX + 10, y + 26);

            // Draw slot number on the right side
            g.setColor(new Color(100, 100, 150));
            g.fillRect(startX + boxWidth - 30, y, 30, boxHeight);
            g.setColor(Color.WHITE);
            g.drawRect(startX + boxWidth - 30, y, 30, boxHeight);

            // Center the number
            String number = String.valueOf(i);
            java.awt.FontMetrics fm = g.getFontMetrics();
            int numberWidth = fm.stringWidth(number);
            int numberX = startX + boxWidth - 15 - (numberWidth / 2);
            g.drawString(number, numberX, y + 26);
        }
    }
}
