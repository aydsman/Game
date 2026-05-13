package ui.screens;

import combat.charms.Charm;
import entity.EnemyManager;
import entity.Player;
import combat.powers.Power;
import combat.powers.Move;
import ui.GamePanel;
import ui.HUD;
import ui.ChestUI;
import ui.InventoryScreen;
import util.Camera;
import util.KeyHandler;
import util.MouseHandler;
import world.arena.arenas.PlainsI;
import world.arena.arenas.StandardArena;
import world.arena.arenas.PlainsII;
import world.arena.arenas.PlainsIII;
import world.arena.arenas.PlainsIV;
import world.arena.arenas.PlainsV;
import world.arena.WaveManager;
import world.chests.ArenaChest;
import world.chests.Chest;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;

import save.ArenaProgressionManager;
import save.SaveData;
import save.SaveManager;

public class GameScreen {

    StandardArena arena = new PlainsI();
    private String selectedArenaName = "PlainsI";
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
    private boolean wavesEnabled = true; // Enable waves
    private boolean statsPanelVisible = false; // Toggle with U key for debugging
    private boolean paused = false;
    private boolean pPressedLastFrame = false;
    private static final int PAUSE_MENU_MARGIN = 150;
    private GamePanel gamePanel;

    /** Full clear overlay after beating all arena waves. */
    private boolean arenaRunCompleteOverlay = false;
    private int arenaRunCompleteKills = 0;
    private double arenaRunCompleteDamage = 0;
    private int arenaRunCompleteWaves = 10;
    private int arenaRunCompleteCash = 0;
    private Rectangle arenaRunCompleteContinueBtn = new Rectangle();
    private Rectangle arenaRunCompleteNextArenaBtn = new Rectangle();
    private int overlayMouseX;
    private int overlayMouseY;
    private boolean enterPressedLastFrame = false;
    private boolean spacePressedLastFrame = false;
    private boolean skipToWave10LPressedLast = false;
    
    // Auto-save tracking
    private long lastAutoSaveTime = 0;
    private static final long AUTO_SAVE_INTERVAL = 10000; // Auto-save every 10 seconds
    private int lastSavedKills = 0;

    public GameScreen() {
        waveManager = new WaveManager(enemyManager, player, arena.getWidth(), arena.getHeight());
        waveManager.setCurrentArena(arena);
        waveManager.setEnabled(wavesEnabled);

        // Chests spawn via waves
    }

    public GameScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        waveManager = new WaveManager(enemyManager, player, arena.getWidth(), arena.getHeight());
        waveManager.setCurrentArena(arena);
        waveManager.setEnabled(wavesEnabled);

        // Chests spawn via waves
    }
    
    /**
     * Set the arena to play by name
     */
    public void setArena(String arenaName) {
        this.selectedArenaName = arenaName;
        
        // Create appropriate arena based on name
        switch (arenaName) {
            case "PlainsI" -> arena = new PlainsI();
            case "PlainsII" -> arena = new PlainsII();
            case "PlainsIII" -> arena = new PlainsIII();
            case "PlainsIV" -> arena = new PlainsIV();
            case "PlainsV" -> arena = new PlainsV();
            default -> arena = new PlainsI();
        }
        
        // Reset player position and state
        player.setX(arena.getWidth() / 2);
        player.setY(arena.getHeight() / 2);
        player.playerSpawn();
        player.getProjectiles().clear();
        
        // Reset managers
        enemyManager.clear();
        waveManager = new WaveManager(enemyManager, player, arena.getWidth(), arena.getHeight());
        waveManager.setCurrentArena(arena);
        waveManager.setEnabled(wavesEnabled);
        
        // Reset state
        chests.clear();
        activeChest = null;
        inventoryOpen = false;
        draggedItem = null;
        camera.x = 0;
        camera.y = 0;
        arenaRunCompleteOverlay = false;
        enterPressedLastFrame = false;
        spacePressedLastFrame = false;
        skipToWave10LPressedLast = false;
    }

    public void resetMouseClicks(MouseHandler mouse) {
        player.resetMouseClicks(mouse);
    }

    public void resetGame() {
        // Reset player
        player.setX(arena.getWidth() / 2);
        player.setY(arena.getHeight() / 2);
        player.playerSpawn();
        player.getProjectiles().clear();

        // Reset wave manager
        waveManager = new WaveManager(enemyManager, player, arena.getWidth(), arena.getHeight());
        waveManager.setCurrentArena(arena);
        waveManager.setEnabled(wavesEnabled);

        // Reset enemy manager
        enemyManager.clear();

        // Reset chests
        chests.clear();
        activeChest = null;

        // Reset inventory state
        inventoryOpen = false;
        draggedItem = null;
        dragSource = -1;
        dragSourceSlot = -1;

        // Reset pause state
        paused = false;
        arenaRunCompleteOverlay = false;
        enterPressedLastFrame = false;
        spacePressedLastFrame = false;
        skipToWave10LPressedLast = false;

        // Reset camera
        camera.x = 0;
        camera.y = 0;

        // Reset key press states
        wasLeftPressedLastFrame = false;
        ePressedLastFrame = false;
        tabPressedLastFrame = false;
        onePressedLastFrame = false;
        twoPressedLastFrame = false;
        threePressedLastFrame = false;
        fourPressedLastFrame = false;
        pPressedLastFrame = false;
    }


    public void update(KeyHandler key, MouseHandler mouse, int screenWidth, int screenHeight) {
        int arenaWidth  = arena.getWidth();
        int arenaHeight = arena.getHeight();

        // Handle pause with P key (disabled while arena clear overlay is open)
        if (key.pPressed && !pPressedLastFrame && !arenaRunCompleteOverlay) {
            paused = !paused;
        }
        pPressedLastFrame = key.pPressed;

        // If paused, skip all update logic
        if (paused) {
            return;
        }

        // All waves cleared — modal blocks gameplay (same dim style as shop purchase modal)
        if (arenaRunCompleteOverlay) {
            updateArenaRunCompleteOverlay(key, mouse, screenWidth, screenHeight);
            return;
        }

        // Dev: L jumps to wave 10 while run is in progress (testing)
        if (wavesEnabled) {
            boolean lEdge = key.lPressed && !skipToWave10LPressedLast;
            skipToWave10LPressedLast = key.lPressed;
            if (lEdge && waveManager.getCurrentWave() < waveManager.getMaxWaves()) {
                enemyManager.killAllEnemies();
                chests.clear();
                activeChest = null;
                inventoryOpen = false;
                player.getProjectiles().clear();
                waveManager.forceBeginWave(10);
                key.lPressed = false;
                skipToWave10LPressedLast = false;
            }
        } else {
            skipToWave10LPressedLast = key.lPressed;
        }

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

        // Use the shared shooting mechanics method (handles movement, shooting, projectiles)
        player.handleShootingMechanics(mouse, key, (activeChest != null && activeChest.isOpen()) || inventoryOpen || draggedItem != null, () -> {
            // Pre-update logic: handle consumables
            if (mouse.leftClicked) {
                int selectedSlot = hud.getInventoryUI().getSelectedSlot();
                if (selectedSlot >= 0 && selectedSlot < 5) {
                    Object item = player.getHotbar().get(selectedSlot);
                    if (item instanceof combat.consumables.Consumable) {
                        combat.consumables.Consumable consumable = (combat.consumables.Consumable) item;

                        // Apply health restore if any
                        if (consumable.getHealthRestorePercent() > 0) {
                            double healAmount = player.getMaxHp() * consumable.getHealthRestorePercent();
                            player.heal(healAmount);
                        }

                        // Apply effect if it has a duration
                        if (consumable.getDurationMs() > 0 && consumable.getEffectType() != combat.consumables.Consumable.ConsumableEffectType.NONE) {
                            player.addConsumableEffect(consumable);
                        }

                        player.getHotbar().set(selectedSlot, null);
                        player.equipHotbarSlot(selectedSlot);
                        mouse.leftClicked = false; // Prevent shooting after using consumable
                    }
                }
            }
        }, arenaWidth, arenaHeight, arena.getObstacles());

        camera.follow(player.getX(), player.getY(), player.getW(), player.getL(), screenWidth, screenHeight, arenaWidth, arenaHeight);
        player.setCameraOffset(camera.x, camera.y);
        player.aimBarrel(mouse.mouseX + camera.x, mouse.mouseY + camera.y);

        // Check melee swing collisions
        if (player.getHeldWeapon() instanceof combat.Melee) {
            checkMeleeCollisions(player, enemyManager);
        }
        player.checkProjectileCollisions(enemyManager);
        enemyManager.update(player, arenaWidth, arenaHeight, arena.getObstacles());
        enemyManager.updateBoss(player, arenaWidth, arenaHeight, arena.getObstacles());
        enemyManager.checkEnemyProjectileCollisions(player, arena.getObstacles());

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

            if (!arenaRunCompleteOverlay && waveManager.isArenaRunFullyComplete()) {
                openArenaRunCompleteOverlay();
            }
        }

        // Track player death
        if (player.isDead() && player.getStats().getDeaths() == 0) {
            player.getStats().addDeath();
        }
        
        // Auto-save stats periodically
        performAutoSave();
    }
    
    /**
     * Periodically auto-save player stats to prevent data loss
     */
    private void performAutoSave() {
        long currentTime = System.currentTimeMillis();
        if (lastAutoSaveTime == 0) {
            lastAutoSaveTime = currentTime;
        }
        
        // Auto-save every interval or when kills change
        int currentKills = player.getStats().getKills();
        if (currentTime - lastAutoSaveTime >= AUTO_SAVE_INTERVAL || currentKills > lastSavedKills) {
            save.SaveManager.quickSaveStats(currentKills, (int) player.getStats().getDamageDealt());
            lastAutoSaveTime = currentTime;
            lastSavedKills = currentKills;
            if (lastSavedKills % 5 == 0 && lastSavedKills > 0) {
                System.out.println("Auto-saved: " + lastSavedKills + " kills");
            }
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

        // Draw Gold (top left)
        g.setColor(new Color(255, 215, 0)); // Gold color
        g.setFont(new java.awt.Font("Arial", Font.BOLD, 18));
        String goldText = "G " + player.getCurrencyManager().getGold().getAmount();
        g.drawString(goldText, 10, 30);

        // Draw stats panel if visible (bottom left corner)
        if (statsPanelVisible) {
            drawStatsPanel(g, 10, screenHeight - 210);
        }

        // Draw pause menu if paused
        if (paused) {
            drawPauseMenu(g, screenWidth, screenHeight);
        }

        if (arenaRunCompleteOverlay) {
            drawArenaRunCompleteOverlay(g, screenWidth, screenHeight);
        }
    }

    private void drawStatsPanel(Graphics2D g, int x, int y) {
        entity.PlayerStats stats = player.getStats();

        double charmHpFrac = 0;
        double charmSpeedFrac = 0;
        double charmDamageFrac = 0;
        for (int i = 0; i < player.getInventory().getMaxCharms(); i++) {
            Charm c = player.getInventory().getCharm(i);
            if (c != null) {
                charmHpFrac += c.getMaxHpBonusFraction();
                charmSpeedFrac += c.getSpeedBonusFraction();
                charmDamageFrac += c.getDamageBonusFraction();
            }
        }
        int hpCharmPct = (int) Math.round(charmHpFrac * 100);
        int speedCharmPct = (int) Math.round(charmSpeedFrac * 100);
        int damageCharmPct = (int) Math.round(charmDamageFrac * 100);

        int playerLevel = player.getPlayerLevel();

        // Panel background
        int panelWidth = 200;
        int panelHeight = 200;
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(x, y, panelWidth, panelHeight);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, panelWidth, panelHeight);

        // Title with player level
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        g.drawString("=== LEVEL " + playerLevel + " ===", x + 10, y + 20);

        // Stats
        g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        int lineY = y + 40;
        int lineHeight = 16;

        g.drawString("Base HP: " + (int) player.getBaseMaxHp(), x + 10, lineY);
        lineY += lineHeight;
        g.drawString("Charm HP: +" + hpCharmPct + "%", x + 10, lineY);
        lineY += lineHeight;
        g.drawString("Charm Dmg: +" + damageCharmPct + "%", x + 10, lineY);
        lineY += lineHeight;
        g.drawString("Charm Spd: +" + speedCharmPct + "%", x + 10, lineY);
        lineY += lineHeight;
        g.drawString("---", x + 10, lineY);
        lineY += lineHeight;
        g.drawString("Kills: " + stats.getKills(), x + 10, lineY);
        lineY += lineHeight;
        g.drawString("Deaths: " + stats.getDeaths(), x + 10, lineY);
        lineY += lineHeight;
        g.drawString("Damage Dealt: " + (int)stats.getDamageDealt(), x + 10, lineY);
        lineY += lineHeight;
        g.drawString("Accuracy: " + String.format("%.1f", stats.getAccuracy()) + "%", x + 10, lineY);
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
                    } else if (dragSource >= 1 && dragSource <= 3) {
                        // From inventory (charm, power, or summon) to chest
                        combat.Item destItem = activeChest.getItem(chestSlot);
                        if (destItem == null) {
                            // Empty slot - just place it
                            activeChest.setItem(chestSlot, draggedItem);
                            removeFromInventorySlot(dragSource, dragSourceSlot);
                            placed = true;
                        } else {
                            // Occupied slot - swap
                            activeChest.setItem(chestSlot, draggedItem);
                            removeFromInventorySlot(dragSource, dragSourceSlot);
                            placeInInventorySlot(destItem, dragSource, dragSourceSlot);
                            placed = true;
                        }
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
        int effectiveDamage = player.getEffectiveMeleeDamage();

        for (entity.Enemy enemy : enemyManager.getEnemies()) {
            // Skip if already hit this swing
            if (melee.getHitEntitiesThisSwing().contains(enemy)) continue;

            // Check if enemy is within swing arc
            if (melee.isInSwingArc(enemy.getCenterX(), enemy.getCenterY(), playerX, playerY)) {
                enemy.takeDamage(effectiveDamage);
                player.getStats().addDamageDealt(effectiveDamage);
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
        int effectiveDamage = player.getEffectiveMeleeDamage();

        if (melee.isInSwingArc(boss.getCenterX(), boss.getCenterY(), playerX, playerY)) {
            boss.takeDamage(effectiveDamage);
            player.getStats().addDamageDealt(effectiveDamage);
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

    public Player getPlayer() {
        return player;
    }

    public WaveManager getWaveManager() { return waveManager; }

    private void drawPauseMenu(Graphics2D g, int screenWidth, int screenHeight) {
        // Calculate menu box dimensions
        int menuWidth = screenWidth - (PAUSE_MENU_MARGIN * 2);
        int menuHeight = screenHeight - (PAUSE_MENU_MARGIN * 2);
        int menuX = PAUSE_MENU_MARGIN;
        int menuY = PAUSE_MENU_MARGIN;

        // Draw translucent background
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Draw menu box
        g.setColor(new Color(50, 50, 70, 220));
        g.fillRect(menuX, menuY, menuWidth, menuHeight);
        g.setColor(Color.WHITE);
        g.drawRect(menuX, menuY, menuWidth, menuHeight);

        // Draw title
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 48));
        String title = "PAUSED";
        java.awt.FontMetrics fm = g.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int titleX = menuX + (menuWidth - titleWidth) / 2;
        int titleY = menuY + 80;
        g.drawString(title, titleX, titleY);

        // Button dimensions
        int buttonWidth = 200;
        int buttonHeight = 50;
        int buttonSpacing = 20;
        int totalButtonHeight = (buttonHeight * 4) + (buttonSpacing * 3);
        int startButtonY = menuY + (menuHeight - totalButtonHeight) / 2;

        // Resume button
        int settingsX = menuX + (menuWidth - buttonWidth) / 2;
        int resumeY = startButtonY;
        drawPauseButton(g, "Resume", settingsX, resumeY, buttonWidth, buttonHeight);

        // Settings button
        int settingsY = resumeY + buttonHeight + buttonSpacing;
        drawPauseButton(g, "Settings", settingsX, settingsY, buttonWidth, buttonHeight);

        // Help button
        int helpY = settingsY + buttonHeight + buttonSpacing;
        drawPauseButton(g, "Help", settingsX, helpY, buttonWidth, buttonHeight);

        // Quit button
        int quitY = helpY + buttonHeight + buttonSpacing;
        drawPauseButton(g, "Quit", settingsX, quitY, buttonWidth, buttonHeight);

        // Instructions
        g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
        String instructions = "Press P to resume";
        int instructionsWidth = g.getFontMetrics().stringWidth(instructions);
        int instructionsX = menuX + (menuWidth - instructionsWidth) / 2;
        int instructionsY = menuY + menuHeight - 40;
        g.drawString(instructions, instructionsX, instructionsY);
    }

    private void drawPauseButton(Graphics2D g, String text, int x, int y, int width, int height) {
        // Button background
        g.setColor(new Color(100, 100, 150, 200));
        g.fillRect(x, y, width, height);

        // Button border
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);

        // Button text
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
        java.awt.FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height + textHeight) / 2 - 5;
        g.drawString(text, textX, textY);
    }

    private int computeArenaRunCashReward(int kills, int wavesSurvived, double damageDealt) {
        int fromWaves = 35 * wavesSurvived;
        int fromKills = 12 * kills;
        int fromDamage = (int) (damageDealt / 40.0);
        return Math.max(150, fromWaves + fromKills + fromDamage);
    }

    private void grantArenaRunCashReward() {
        if (gamePanel == null) {
            return;
        }
        gamePanel.getCurrencyManager().addCash(arenaRunCompleteCash);
        SaveData data = SaveManager.load();
        data.setCash(gamePanel.getCurrencyManager().getCash().getAmount());
        SaveManager.save(data);
    }

    /** Marks the current arena cleared and unlocks the next; safe to call once per victory. */
    private void persistArenaRunVictoryProgression() {
        SaveData d = SaveManager.load();
        ArenaProgressionManager pm = new ArenaProgressionManager();
        pm.setUnlockedArenas(new HashSet<>(d.getUnlockedArenas()));
        pm.setLastCompletedArena(d.getLastCompletedArena());
        pm.completeArena(selectedArenaName);
        d.setUnlockedArenas(pm.getUnlockedArenas());
        d.setLastCompletedArena(pm.getLastCompletedArena());
        if (d.getPlayerLevel() <= 0) {
            d.setSkillPoints(0);
        }
        SaveManager.save(d);
    }

    private void openArenaRunCompleteOverlay() {
        if (arenaRunCompleteOverlay) {
            return;
        }
        if (!waveManager.isArenaRunFullyComplete()) {
            return;
        }
        arenaRunCompleteKills = player.getStats().getKills();
        arenaRunCompleteDamage = player.getStats().getDamageDealt();
        arenaRunCompleteWaves = waveManager.getMaxWaves();
        arenaRunCompleteCash = computeArenaRunCashReward(
                arenaRunCompleteKills, arenaRunCompleteWaves, arenaRunCompleteDamage);
        grantArenaRunCashReward();
        persistArenaRunVictoryProgression();
        arenaRunCompleteOverlay = true;
        enterPressedLastFrame = true;
        spacePressedLastFrame = true;
        if (gamePanel != null) {
            gamePanel.requestFocusInWindow();
        }
    }

    /**
     * Handles arena-clear modal clicks on the EDT (before the game tick). Returns true if the click was consumed.
     */
    public boolean handleArenaCompletionClick(MouseHandler mouse, int x, int y, int screenWidth, int screenHeight) {
        if (!arenaRunCompleteOverlay) {
            return false;
        }
        layoutArenaRunCompleteOverlayButtons(screenWidth, screenHeight);
        overlayMouseX = x;
        overlayMouseY = y;
        if (!mouse.leftClicked) {
            return false;
        }
        mouse.leftClicked = false;
        if (arenaRunCompleteContinueBtn.contains(x, y)) {
            closeArenaRunCompleteToHub();
            return true;
        }
        if (arenaRunCompleteNextArenaBtn.contains(x, y)) {
            closeArenaRunCompleteGoNextArena();
            return true;
        }
        return true;
    }

    private static final int ARENA_COMPLETE_MODAL_W = 720;
    private static final int ARENA_COMPLETE_MODAL_H = 460;

    /** Button bounds must be set before {@link #updateArenaRunCompleteOverlay}; draw reuses the same layout. */
    private void layoutArenaRunCompleteOverlayButtons(int screenWidth, int screenHeight) {
        int mh = ARENA_COMPLETE_MODAL_H;
        int my = (screenHeight - mh) / 2;
        int btnW = 300;
        int btnH = 48;
        int gap = 20;
        int btnY = my + mh - btnH * 2 - gap - 36;
        int rowX = (screenWidth - btnW) / 2;
        arenaRunCompleteContinueBtn.setBounds(rowX, btnY, btnW, btnH);
        arenaRunCompleteNextArenaBtn.setBounds(rowX, btnY + btnH + gap, btnW, btnH);
    }

    private void updateArenaRunCompleteOverlay(KeyHandler key, MouseHandler mouse, int screenWidth, int screenHeight) {
        layoutArenaRunCompleteOverlayButtons(screenWidth, screenHeight);
        overlayMouseX = mouse.mouseX;
        overlayMouseY = mouse.mouseY;

        boolean continueEdge = (key.enterPressed && !enterPressedLastFrame)
                || (key.spacePressed && !spacePressedLastFrame);
        enterPressedLastFrame = key.enterPressed;
        spacePressedLastFrame = key.spacePressed;

        if (mouse.leftClicked) {
            if (arenaRunCompleteContinueBtn.contains(mouse.mouseX, mouse.mouseY)) {
                mouse.leftClicked = false;
                closeArenaRunCompleteToHub();
                return;
            }
            if (arenaRunCompleteNextArenaBtn.contains(mouse.mouseX, mouse.mouseY)) {
                mouse.leftClicked = false;
                closeArenaRunCompleteGoNextArena();
                return;
            }
            mouse.leftClicked = false;
        }
        if (continueEdge) {
            if (arenaIdAfter(selectedArenaName) != null) {
                closeArenaRunCompleteGoNextArena();
            } else {
                closeArenaRunCompleteToHub();
            }
            return;
        }
    }

    private void closeArenaRunCompleteToHub() {
        arenaRunCompleteOverlay = false;
        if (gamePanel != null) {
            SaveManager.syncPlayerStats(player.getStats(), waveManager.getCurrentWave(), 1);
            gamePanel.switchScreen("hub");
        }
    }

    private void closeArenaRunCompleteGoNextArena() {
        arenaRunCompleteOverlay = false;
        if (gamePanel != null) {
            gamePanel.getKeyHandler().lPressed = false;
        }
        if (gamePanel == null) {
            return;
        }
        SaveManager.syncPlayerStats(player.getStats(), waveManager.getCurrentWave(), 1);
        SaveData d = SaveManager.load();
        ArenaProgressionManager pm = new ArenaProgressionManager();
        pm.setUnlockedArenas(new HashSet<>(d.getUnlockedArenas()));

        String next = arenaIdAfter(selectedArenaName);
        if (next != null && pm.isUnlocked(next)) {
            setArena(next);
            resetGame();
            resetMouseClicks(gamePanel.getMouseHandler());
            gamePanel.requestFocusInWindow();
        } else {
            gamePanel.switchScreen("hub");
        }
    }

    private static String arenaIdAfter(String current) {
        String[] all = ArenaProgressionManager.getAllArenas();
        for (int i = 0; i < all.length; i++) {
            if (all[i].equals(current) && i + 1 < all.length) {
                return all[i + 1];
            }
        }
        return null;
    }

    private void drawArenaRunCompleteOverlay(Graphics2D g, int screenWidth, int screenHeight) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, screenWidth, screenHeight);

        int mw = ARENA_COMPLETE_MODAL_W;
        int mh = ARENA_COMPLETE_MODAL_H;
        int mx = (screenWidth - mw) / 2;
        int my = (screenHeight - mh) / 2;

        g.setColor(new Color(20, 22, 30, 238));
        g.fillRoundRect(mx, my, mw, mh, 18, 18);
        g.setColor(new Color(120, 200, 255));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(mx, my, mw, mh, 18, 18);
        g.setStroke(new BasicStroke(1));

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 34));
        g.drawString("Arena cleared", mx + 28, my + 52);

        g.setColor(new Color(190, 200, 220));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        int lineY = my + 100;
        g.drawString("Kills: " + arenaRunCompleteKills, mx + 36, lineY);
        lineY += 36;
        g.drawString("Damage dealt: " + String.format("%,.0f", arenaRunCompleteDamage), mx + 36, lineY);
        lineY += 36;
        g.drawString("Waves survived: " + arenaRunCompleteWaves, mx + 36, lineY);
        lineY += 36;
        g.setColor(new Color(120, 230, 160));
        g.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g.drawString("Cash earned: $" + arenaRunCompleteCash, mx + 36, lineY);
        lineY += 44;
        g.setColor(new Color(160, 170, 190));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g.drawString("Reward is based on kills, total damage, and waves cleared.", mx + 36, lineY);

        layoutArenaRunCompleteOverlayButtons(screenWidth, screenHeight);
        boolean h1 = arenaRunCompleteContinueBtn.contains(overlayMouseX, overlayMouseY);
        String nextId = arenaIdAfter(selectedArenaName);
        String hubLabel = nextId != null ? "Return to Hub" : "Return to Hub (Enter)";
        drawOverlayChoiceButton(g, arenaRunCompleteContinueBtn, hubLabel, h1);

        boolean h2 = arenaRunCompleteNextArenaBtn.contains(overlayMouseX, overlayMouseY);
        String nextLabel = nextId != null ? "Next Arena (Enter)" : "Continue";
        drawOverlayChoiceButton(g, arenaRunCompleteNextArenaBtn, nextLabel, h2);
    }

    private void drawOverlayChoiceButton(Graphics2D g, Rectangle r, String label, boolean hover) {
        Color fill = hover ? new Color(90, 110, 160, 230) : new Color(60, 70, 95, 220);
        g.setColor(fill);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        g.setStroke(new BasicStroke(1));
        g.setFont(new Font("Segoe UI", Font.BOLD, 17));
        FontMetrics fm = g.getFontMetrics();
        int tx = r.x + (r.width - fm.stringWidth(label)) / 2;
        int ty = r.y + (r.height + fm.getAscent()) / 2 - 4;
        g.drawString(label, tx, ty);
    }

    public void handlePauseMenuClick(int mouseX, int mouseY, int screenWidth, int screenHeight) {
        if (!paused) return;

        // Calculate menu box dimensions
        int menuWidth = screenWidth - (PAUSE_MENU_MARGIN * 2);
        int menuHeight = screenHeight - (PAUSE_MENU_MARGIN * 2);
        int menuX = PAUSE_MENU_MARGIN;
        int menuY = PAUSE_MENU_MARGIN;

        // Button dimensions
        int buttonWidth = 200;
        int buttonHeight = 50;
        int buttonSpacing = 20;
        int totalButtonHeight = (buttonHeight * 4) + (buttonSpacing * 3);
        int startButtonY = menuY + (menuHeight - totalButtonHeight) / 2;

        int buttonX = menuX + (menuWidth - buttonWidth) / 2;

        // Resume button
        int resumeY = startButtonY;
        if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
            mouseY >= resumeY && mouseY <= resumeY + buttonHeight) {
            paused = false;
            return;
        }

        // Settings button
        int settingsY = resumeY + buttonHeight + buttonSpacing;
        if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
            mouseY >= settingsY && mouseY <= settingsY + buttonHeight) {
            if (gamePanel != null) {
                gamePanel.switchScreen("settings");
            }
            return;
        }

        // Help button
        int helpY = settingsY + buttonHeight + buttonSpacing;
        if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
            mouseY >= helpY && mouseY <= helpY + buttonHeight) {
            if (gamePanel != null) {
                gamePanel.switchScreen("help");
            }
            return;
        }

        // Quit button
        int quitY = helpY + buttonHeight + buttonSpacing;
        if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
            mouseY >= quitY && mouseY <= quitY + buttonHeight) {
            if (gamePanel != null) {
                gamePanel.switchScreen("menu");
            }
            return;
        }
    }
}
