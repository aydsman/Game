package ui.screens;

import combat.Hitstop;
import combat.charms.Charm;
import combat.powers.runtime.PowerCombatContext;
import combat.powers.runtime.TemplatedMoveExecutor;
import combat.summons.SummonRuntime;
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
import world.arena.GameMode;
import world.arena.WaveManager;
import world.chests.ArenaChest;
import world.chests.Chest;
import util.CombatFeedback;
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
import mastery.MasteryService;

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
    boolean fivePressedLastFrame = false;
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

    /** Game-over overlay shown when the player dies mid-run. */
    private boolean deathOverlay = false;
    private int deathKills = 0;
    private double deathDamage = 0;
    private int deathWaves = 0;
    private int deathCash = 0;
    private final Rectangle deathRetryBtn = new Rectangle();
    private final Rectangle deathHubBtn = new Rectangle();

    private GameMode gameMode = GameMode.STANDARD;
    private boolean runStatsSynced = false;
    private final CombatFeedback feedback = new CombatFeedback();
    private final Hitstop hitstop = new Hitstop(combat.CombatTuning.active());
    private final TemplatedMoveExecutor templatedMoves = new TemplatedMoveExecutor();
    private final SummonRuntime summonRuntime = new SummonRuntime();
    private static final double LOW_HEALTH_FRACTION = 0.3;
    private boolean enterPressedLastFrame = false;
    private boolean spacePressedLastFrame = false;
    private boolean skipToWave10LPressedLast = false;
    
    // Auto-save tracking
    private long lastAutoSaveTime = 0;
    private static final long AUTO_SAVE_INTERVAL = 10000; // Auto-save every 10 seconds
    private int lastSavedKills = 0;

    private static class FireBullet {
        double x;
        double y;
        double angle;
        double speedPxPerMs = 0.9;
        int damage;
        int hitRadius;
        int explosionRadius;
        double traveled;
        int maxRange;
        boolean active = true;
    }

    private static class FirePatch {
        int x;
        int y;
        int radius;
        long endMs;
        long lastTickMs;
    }

    private static class ActiveMoveRuntime {
        String powerName;
        String moveName;
        int slot;
        long startMs;
        long endMs;
        long lastTickMs;
        boolean impactTriggered;
        double aimAngleAtCast;
        java.util.List<int[]> visualLineTargets = new ArrayList<>();
        int shotsFired;
        int anchorX;
        int anchorY;
    }

    private static final int RUBBER_JET_GATLING_FORWARD = 150;
    private static final int RUBBER_JET_GATLING_HALF_WIDTH = 45;
    private static final int RUBBER_ELEPHANT_GUN_FORWARD = 190;
    private static final int RUBBER_ELEPHANT_GUN_RADIUS = 95;
    private static final double RUBBER_ELEPHANT_GUN_WINDUP_FRAC = 0.6;

    private static final int STRING_OVERHEAT_FORWARD = 280;
    private static final int STRING_OVERHEAT_HALF_WIDTH = 10;
    private static final int STRING_OVERHEAT_DAMAGE = 30;
    private static final int STRING_PARASITE_RADIUS = 350;
    private static final int STRING_PARASITE_MAX_TARGETS = 6;
    private static final long STRING_PARASITE_STUN_MS = 5000;
    private static final int STRING_PARASITE_DAMAGE_PER_TARGET = 18;
    private static final int STRING_GOD_THREAD_RADIUS = 500;
    private static final int STRING_GOD_THREAD_STRINGS = 16;
    private static final int STRING_GOD_THREAD_DAMAGE_PER_STRING = 24;
    private static final long STRING_GOD_THREAD_SHOT_INTERVAL_MS = 100;
    private static final long STRING_GOD_THREAD_STUN_MS = 500;

    private static final int EARTH_ROCK_BARRAGE_SHOTS = 3;
    private static final long EARTH_ROCK_BARRAGE_SHOT_INTERVAL_MS = 80;
    private static final int EARTH_ROCK_BARRAGE_DAMAGE = 10;
    private static final int EARTH_ROCK_BARRAGE_HIT_RADIUS = 35;
    private static final int[] EARTH_ROCK_BARRAGE_DISTANCES = { 190, 205, 220 };
    private static final int EARTH_SPIKE_RING_RADIUS = 120;
    private static final int EARTH_SPIKE_RING_DAMAGE = 18;
    private static final long EARTH_SPIKE_RING_SLOW_MS = 2500;
    private static final double EARTH_SPIKE_RING_SLOW_MULTIPLIER = 0.7;
    private static final int EARTH_BOULDER_COUNT = 2;
    private static final long EARTH_BOULDER_SHOT_INTERVAL_MS = 350;
    private static final int EARTH_BOULDER_DAMAGE = 38;
    private static final int EARTH_BOULDER_RADIUS = 60;
    private static final int EARTH_BOULDER_RANGE = 320;
    private static final int EARTH_BOULDER_PATH_CHECK_RADIUS = 30;

    private static final int FIRE_TICK_DAMAGE = 4;
    private static final long FIRE_TICK_INTERVAL_MS = 500;
    private static final long FIRE_TICK_DURATION_MS = 2000;
    private static final int FIRE_FLAME_BURST_BULLETS = 6;
    private static final long FIRE_FLAME_BURST_SHOT_INTERVAL_MS = 60;
    private static final int FIRE_FLAME_BURST_BULLET_DAMAGE = 8;
    private static final int FIRE_FLAME_BURST_BULLET_RADIUS = 22;
    private static final int FIRE_FLAME_BURST_RANGE = 400;
    private static final double FIRE_FLAME_BURST_SPREAD_RAD = Math.toRadians(12);
    private static final int FIRE_TRAIL_DASH_DISTANCE = 130;
    private static final int FIRE_TRAIL_PATCH_COUNT = 5;
    private static final int FIRE_TRAIL_PATCH_RADIUS = 40;
    private static final long FIRE_TRAIL_PATCH_DURATION_MS = 2500;
    private static final double FIRE_GRAND_FIREBALL_WINDUP_FRAC = 0.35;
    private static final int FIRE_GRAND_FIREBALL_DAMAGE = 46;
    private static final int FIRE_GRAND_FIREBALL_RADIUS = 80;
    private static final int FIRE_GRAND_FIREBALL_RANGE = 420;

    private final ArrayList<ActiveMoveRuntime> activeMoveRuntimes = new ArrayList<>();
    private final ArrayList<FireBullet> activeFireBullets = new ArrayList<>();
    private final ArrayList<FirePatch> activeFirePatches = new ArrayList<>();
    private long lastFireBulletUpdateMs = 0;

    public GameScreen() {
        this(null);
    }

    public GameScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        player.setCombatFeedback(feedback);
        player.setHitstop(hitstop);
        rebuildWaveManager();

        // Chests spawn via waves
    }

    /** Fresh wave manager bound to the current arena and mode. */
    private void rebuildWaveManager() {
        waveManager = new WaveManager(enemyManager, player, arena.getWidth(), arena.getHeight());
        waveManager.setCurrentArena(arena);
        waveManager.setMode(gameMode);
        waveManager.setEnabled(wavesEnabled);
        runStatsSynced = false;
    }

    /**
     * Folds this run's stats into the save exactly once, however the run ends.
     * {@link SaveManager#syncPlayerStats} accumulates, so calling it twice would double-count the run.
     */
    /** Called when leaving the arena by any route (hub, menu, retry) so the run is banked once. */
    public void syncRunStatsOnExit() {
        syncRunStats();
    }

    private void syncRunStats() {
        if (runStatsSynced) {
            return;
        }
        runStatsSynced = true;
        SaveManager.syncPlayerStats(player.getStats(), waveManager.getCurrentWave(), 1);
    }

    /** Selected before entering the arena; kept across retries within the same run. */
    public void setGameMode(GameMode mode) {
        this.gameMode = (mode == null ? GameMode.STANDARD : mode);
    }

    public GameMode getGameMode() {
        return gameMode;
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
        rebuildWaveManager();
        
        // Reset state
        chests.clear();
        activeChest = null;
        inventoryOpen = false;
        draggedItem = null;
        camera.x = 0;
        camera.y = 0;
        arenaRunCompleteOverlay = false;
        deathOverlay = false;
        enterPressedLastFrame = false;
        spacePressedLastFrame = false;
        skipToWave10LPressedLast = false;
        activeMoveRuntimes.clear();
        activeFireBullets.clear();
        activeFirePatches.clear();
        feedback.clear();
        hitstop.reset();
        summonRuntime.reset();
        lastFireBulletUpdateMs = 0;
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
        rebuildWaveManager();

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
        deathOverlay = false;
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
        fivePressedLastFrame = false;
        pPressedLastFrame = false;
        activeMoveRuntimes.clear();
        activeFireBullets.clear();
        activeFirePatches.clear();
        feedback.clear();
        hitstop.reset();
        summonRuntime.reset();
        lastFireBulletUpdateMs = 0;
    }


    public void update(KeyHandler key, MouseHandler mouse, int screenWidth, int screenHeight) {
        int arenaWidth  = arena.getWidth();
        int arenaHeight = arena.getHeight();

        // Runs even while paused or behind a modal so shake settles back to zero instead of freezing offset.
        feedback.update();

        // Handle pause with P key (disabled while a results modal is open)
        if (key.pPressed && !pPressedLastFrame && !arenaRunCompleteOverlay && !deathOverlay) {
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

        // Player died — freeze the run behind the game-over modal
        if (deathOverlay) {
            updateDeathOverlay(key, mouse, screenWidth, screenHeight);
            return;
        }
        if (player.isDead()) {
            openDeathOverlay();
            return;
        }

        // Only entity simulation pauses. Input already ran (pause / overlays); camera and HUD keep moving.
        if (hitstop.isFrozen(System.currentTimeMillis())) {
            onePressedLastFrame = key.onePressed;
            twoPressedLastFrame = key.twoPressed;
            threePressedLastFrame = key.threePressed;
            fourPressedLastFrame = key.fourPressed;
            fivePressedLastFrame = key.fivePressed;
            camera.follow(player.getX(), player.getY(), player.getW(), player.getL(),
                    screenWidth, screenHeight, arenaWidth, arenaHeight);
            player.setCameraOffset(camera.x, camera.y);
            player.aimBarrel(mouse.mouseX + camera.x, mouse.mouseY + camera.y);
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
        updateActiveMoveRuntimes();
        updateFireTickDamage();
        updateFireBullets();
        updateFirePatches();

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
        fivePressedLastFrame = key.fivePressed;

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
        summonRuntime.update(player, enemyManager, System.currentTimeMillis());
        enemyManager.update(player, arenaWidth, arenaHeight, arena.getObstacles());
        enemyManager.updateBoss(player, arenaWidth, arenaHeight, arena.getObstacles());
        enemyManager.checkEnemyProjectileCollisions(player, arena.getObstacles());
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

            if (!arenaRunCompleteOverlay && waveManager.isRunComplete()) {
                openArenaRunCompleteOverlay();
            }
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
        // World layer is offset by the current screen shake; UI on top of it is not.
        // Clip so shake cannot smear tiles or walls into the letterbox.
        java.awt.Shape previousClip = g.getClip();
        g.clipRect(0, 0, screenWidth, screenHeight);
        int shakeX = feedback.getShakeOffsetX();
        int shakeY = feedback.getShakeOffsetY();
        g.translate(shakeX, shakeY);
        drawWorld(g, screenWidth, screenHeight);
        g.translate(-shakeX, -shakeY);
        g.setClip(previousClip);

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

        // Draw wave / mode status (top right, below debug status)
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        String waveText;
        if (waveManager.isInGracePeriod()) {
            int seconds = waveManager.getGracePeriodRemaining() / 60;
            waveText = "Next wave in: " + seconds + "s (F to skip)";
        } else {
            waveText = "Wave " + waveManager.getCurrentWave();
            if (gameMode == GameMode.STANDARD || gameMode == GameMode.BOSS_RUSH) {
                waveText += " / " + waveManager.getMaxWaves();
            }
        }
        fm = g.getFontMetrics();
        textWidth = fm.stringWidth(waveText);
        g.drawString(waveText, screenWidth - textWidth - 10, 45);

        g.setColor(new Color(180, 200, 230));
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 15));
        String modeText = gameMode.getDisplayName();
        if (gameMode.hasTimeLimit()) {
            int secondsLeft = waveManager.getTimeRemainingFrames() / 60;
            modeText += "  " + (secondsLeft / 60) + ":" + String.format("%02d", secondsLeft % 60);
            if (secondsLeft <= 30) {
                g.setColor(new Color(255, 120, 120));
            }
        }
        fm = g.getFontMetrics();
        g.drawString(modeText, screenWidth - fm.stringWidth(modeText) - 10, 68);

        // Draw Gold (top left)
        g.setColor(new Color(255, 215, 0)); // Gold color
        g.setFont(new java.awt.Font("Arial", Font.BOLD, 18));
        String goldText = "G " + player.getCurrencyManager().getGold().getAmount();
        g.drawString(goldText, 10, 30);

        // Draw stats panel if visible (bottom left corner)
        if (statsPanelVisible) {
            drawStatsPanel(g, 10, screenHeight - 210);
        }

        drawLowHealthVignette(g, screenWidth, screenHeight);

        // Draw pause menu if paused
        if (paused) {
            drawPauseMenu(g, screenWidth, screenHeight);
        }

        if (arenaRunCompleteOverlay) {
            drawArenaRunCompleteOverlay(g, screenWidth, screenHeight);
        }

        if (deathOverlay) {
            drawDeathOverlay(g, screenWidth, screenHeight);
        }
    }

    /** Everything positioned in world space, drawn under the HUD and shifted by screen shake. */
    private void drawWorld(Graphics2D g, int screenWidth, int screenHeight) {
        arena.draw(g, screenWidth, screenHeight, camera.x, camera.y);

        for (Chest chest : chests) {
            drawChest(g, chest);
        }

        enemyManager.draw(g, camera.x, camera.y);
        enemyManager.drawBoss(g, camera.x, camera.y);
        enemyManager.drawEnemyProjectiles(g, camera.x, camera.y);

        drawMeleeSwing(g, player, camera.x, camera.y);

        player.draw(g, camera.x, camera.y);
        drawActivePowerMoveVisuals(g);

        for (combat.Projectile p : player.getProjectiles()) {
            g.setColor(p.getColor());
            int radius = p.getRadius();
            g.fillOval(p.getX() - camera.x - radius, p.getY() - camera.y - radius, radius * 2, radius * 2);
        }

        feedback.draw(g, camera.x, camera.y);
    }

    /** Red edge glow that grows as the player's health drops, so low HP is readable without a sound. */
    private void drawLowHealthVignette(Graphics2D g, int screenWidth, int screenHeight) {
        double hpFraction = player.getMaxHp() <= 0 ? 1.0 : player.getHp() / player.getMaxHp();
        if (hpFraction >= LOW_HEALTH_FRACTION || player.isDead()) {
            return;
        }
        double severity = 1.0 - (hpFraction / LOW_HEALTH_FRACTION);
        // Pulse so it reads as urgency rather than a static tint.
        double pulse = 0.75 + 0.25 * Math.sin(System.currentTimeMillis() / 220.0);
        int bands = 26;
        int maxAlpha = (int) Math.round(110 * severity * pulse);
        for (int i = 0; i < bands; i++) {
            int alpha = (int) Math.round(maxAlpha * (1.0 - i / (double) bands));
            if (alpha <= 0) {
                continue;
            }
            g.setColor(new Color(170, 20, 20, alpha));
            g.drawRect(i, i, screenWidth - 1 - i * 2, screenHeight - 1 - i * 2);
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

        for (entity.Enemy enemy : new ArrayList<>(enemyManager.getEnemies())) {
            applyMeleeHit(melee, enemy);
        }
        applyMeleeHit(melee, enemyManager.getActiveBoss());
    }

    /** Hits one target if the swing reaches it and this swing has not already connected with it. */
    private void applyMeleeHit(combat.Melee melee, entity.Entity target) {
        if (target == null || target.isDead()) return;
        if (melee.getHitEntitiesThisSwing().contains(target)) return;
        if (!melee.isInSwingArc(target.getCenterX(), target.getCenterY(),
                player.getCenterX(), player.getCenterY())) {
            return;
        }
        melee.getHitEntitiesThisSwing().add(target);
        melee.registerComboHit(System.currentTimeMillis());
        combat.DamageRoll roll = player.rollMeleeDamage();
        player.dealDamage(target, roll, true, melee.getActiveSwingKnockbackPixels(), melee.getOnHitStunMs());
        feedback.addShake(roll.isCritical() ? 4.0 : 2.5, roll.isCritical() ? 120 : 90);
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
        equippedPower.applyMasteryMoveUnlocks();

        triggerPowerMoveIfPressed(equippedPower, 1, key.onePressed, onePressedLastFrame);
        triggerPowerMoveIfPressed(equippedPower, 2, key.twoPressed, twoPressedLastFrame);
        triggerPowerMoveIfPressed(equippedPower, 3, key.threePressed, threePressedLastFrame);
        triggerPowerMoveIfPressed(equippedPower, 4, key.fourPressed, fourPressedLastFrame);
        triggerPowerMoveIfPressed(equippedPower, 5, key.fivePressed, fivePressedLastFrame);
    }

    private void triggerPowerMoveIfPressed(Power power, int slot, boolean pressedNow, boolean pressedLastFrame) {
        if (!pressedNow || pressedLastFrame) {
            return;
        }
        Move move = power.useMoveWithResult(slot);
        if (move == null) {
            return;
        }
        startMoveRuntime(power, move);
        executeMoveCastLogic(power, move);
        awardPowerUseMasteryXP(power, move);
    }

    private void startMoveRuntime(Power power, Move move) {
        long nowMs = System.currentTimeMillis();
        ActiveMoveRuntime runtime = new ActiveMoveRuntime();
        runtime.powerName = power.getName();
        runtime.moveName = move.getName();
        runtime.slot = move.getSlot();
        runtime.startMs = nowMs;
        runtime.endMs = nowMs + Math.max(1, move.getDurationMs());
        runtime.lastTickMs = 0;
        runtime.impactTriggered = false;
        runtime.aimAngleAtCast = computeAimAngleToMouse();
        activeMoveRuntimes.add(runtime);
    }

    private double computeAimAngleToMouse() {
        int dx = (lastMouseX + camera.x) - player.getCenterX();
        int dy = (lastMouseY + camera.y) - player.getCenterY();
        return Math.atan2(dy, dx);
    }

    private void executeMoveCastLogic(Power power, Move move) {
        if (move.getTemplate() != null) {
            templatedMoves.execute(move, new PowerCombatContext(
                    player,
                    enemyManager,
                    computeAimAngleToMouse(),
                    arena.getObstacles(),
                    arena.getWidth(),
                    arena.getHeight()));
        }
        // Add per-cast one-shot behavior here.
        if ("Rubber Fruit".equals(power.getName()) && move.getSlot() == 2) {
            performRubberShaveDash();
        }
        if ("String Fruit".equals(power.getName())) {
            executeStringMoveCast(move);
        }
        if ("Earth".equals(power.getName())) {
            executeEarthMoveCast(move);
        }
        if ("Fire".equals(power.getName())) {
            executeFireMoveCast(move);
        }
    }

    private void executeStringMoveCast(Move move) {
        ActiveMoveRuntime runtime = findLatestRuntimeFor("String Fruit", move.getSlot());
        switch (move.getSlot()) {
            case 1 -> performStringOverheat(runtime);
            case 2 -> performStringParasite(runtime);
            case 3 -> performStringGodThread(runtime);
            default -> {
                // No String fruit logic beyond slot 3.
            }
        }
    }

    private void executeEarthMoveCast(Move move) {
        ActiveMoveRuntime runtime = findLatestRuntimeFor("Earth", move.getSlot());
        switch (move.getSlot()) {
            case 1 -> performEarthRockBarrage(runtime);
            case 2 -> performEarthSpikeRing(runtime);
            case 3 -> performEarthBoulderBarrage(runtime);
            default -> {
                // No Earth logic beyond slot 3.
            }
        }
    }

    private void performEarthRockBarrage(ActiveMoveRuntime runtime) {
        if (runtime == null) {
            return;
        }
        runtime.shotsFired = 0;
        runtime.lastTickMs = System.currentTimeMillis();
        fireOneEarthRock(runtime);
    }

    private void fireOneEarthRock(ActiveMoveRuntime runtime) {
        if (runtime == null || runtime.shotsFired >= EARTH_ROCK_BARRAGE_SHOTS) {
            return;
        }
        double angle = runtime.aimAngleAtCast;
        int dist = EARTH_ROCK_BARRAGE_DISTANCES[Math.min(runtime.shotsFired, EARTH_ROCK_BARRAGE_DISTANCES.length - 1)];
        int impactX = player.getCenterX() + (int) Math.round(Math.cos(angle) * dist);
        int impactY = player.getCenterY() + (int) Math.round(Math.sin(angle) * dist);
        applyPowerAoEDamage(impactX, impactY, EARTH_ROCK_BARRAGE_HIT_RADIUS, EARTH_ROCK_BARRAGE_DAMAGE, false);
        runtime.visualLineTargets.add(new int[] { impactX, impactY });
        runtime.shotsFired++;
    }

    private void performEarthSpikeRing(ActiveMoveRuntime runtime) {
        if (runtime == null) {
            return;
        }
        runtime.anchorX = player.getCenterX();
        runtime.anchorY = player.getCenterY();
        applyEarthSpikeRingEffect(runtime.anchorX, runtime.anchorY);
    }

    private void applyEarthSpikeRingEffect(int anchorX, int anchorY) {
        for (entity.Enemy enemy : findEnemiesInRadius(anchorX, anchorY, EARTH_SPIKE_RING_RADIUS, Integer.MAX_VALUE)) {
            applyPowerDamage(enemy, EARTH_SPIKE_RING_DAMAGE);
            enemy.applySlow(EARTH_SPIKE_RING_SLOW_MS, EARTH_SPIKE_RING_SLOW_MULTIPLIER);
        }
        entity.Boss boss = enemyManager.getActiveBoss();
        if (boss != null && !boss.isDead()
                && isPointInRadius(anchorX, anchorY, boss.getCenterX(), boss.getCenterY(), EARTH_SPIKE_RING_RADIUS)) {
            applyPowerDamage(boss, EARTH_SPIKE_RING_DAMAGE);
            boss.applySlow(EARTH_SPIKE_RING_SLOW_MS, EARTH_SPIKE_RING_SLOW_MULTIPLIER);
        }
    }

    private void performEarthBoulderBarrage(ActiveMoveRuntime runtime) {
        if (runtime == null) {
            return;
        }
        runtime.shotsFired = 0;
        runtime.lastTickMs = System.currentTimeMillis();
        fireOneEarthBoulder(runtime);
    }

    private void fireOneEarthBoulder(ActiveMoveRuntime runtime) {
        if (runtime == null || runtime.shotsFired >= EARTH_BOULDER_COUNT) {
            return;
        }
        double angle = runtime.aimAngleAtCast;
        int startX = player.getCenterX();
        int startY = player.getCenterY();
        int impactX = startX + (int) Math.round(Math.cos(angle) * EARTH_BOULDER_RANGE);
        int impactY = startY + (int) Math.round(Math.sin(angle) * EARTH_BOULDER_RANGE);

        for (int dist = 40; dist <= EARTH_BOULDER_RANGE; dist += 20) {
            int checkX = startX + (int) Math.round(Math.cos(angle) * dist);
            int checkY = startY + (int) Math.round(Math.sin(angle) * dist);
            entity.Enemy hitEnemy = findFirstEnemyInRadius(checkX, checkY, EARTH_BOULDER_PATH_CHECK_RADIUS);
            entity.Boss boss = enemyManager.getActiveBoss();
            boolean bossHit = boss != null && !boss.isDead()
                    && isPointInRadius(checkX, checkY, boss.getCenterX(), boss.getCenterY(), EARTH_BOULDER_PATH_CHECK_RADIUS);
            if (hitEnemy != null || bossHit) {
                impactX = checkX;
                impactY = checkY;
                break;
            }
        }

        applyPowerAoEDamage(impactX, impactY, EARTH_BOULDER_RADIUS, EARTH_BOULDER_DAMAGE, false);
        runtime.visualLineTargets.add(new int[] { impactX, impactY });
        runtime.shotsFired++;
    }

    private entity.Enemy findFirstEnemyInRadius(int cx, int cy, int radius) {
        for (entity.Enemy enemy : enemyManager.getEnemies()) {
            if (enemy == null || enemy.isDead()) {
                continue;
            }
            if (isPointInRadius(cx, cy, enemy.getCenterX(), enemy.getCenterY(), radius)) {
                return enemy;
            }
        }
        return null;
    }

    private void executeFireMoveCast(Move move) {
        ActiveMoveRuntime runtime = findLatestRuntimeFor("Fire", move.getSlot());
        switch (move.getSlot()) {
            case 1 -> performFireFlameBurst(runtime);
            case 2 -> performFireTrailOfFire(runtime);
            case 3 -> performFireGrandFireball(runtime);
            default -> {
                // No Fire logic beyond slot 3.
            }
        }
    }

    private void performFireFlameBurst(ActiveMoveRuntime runtime) {
        if (runtime == null) {
            return;
        }
        runtime.shotsFired = 0;
        runtime.lastTickMs = System.currentTimeMillis();
        fireOneFlameBurstBullet(runtime);
    }

    private void fireOneFlameBurstBullet(ActiveMoveRuntime runtime) {
        if (runtime == null || runtime.shotsFired >= FIRE_FLAME_BURST_BULLETS) {
            return;
        }
        double spreadOffset = 0;
        if (FIRE_FLAME_BURST_BULLETS > 1) {
            spreadOffset = (runtime.shotsFired - ((FIRE_FLAME_BURST_BULLETS - 1) / 2.0))
                    * (FIRE_FLAME_BURST_SPREAD_RAD / (FIRE_FLAME_BURST_BULLETS - 1));
        }
        spawnFireBullet(
                player.getCenterX(),
                player.getCenterY(),
                runtime.aimAngleAtCast + spreadOffset,
                FIRE_FLAME_BURST_BULLET_DAMAGE,
                FIRE_FLAME_BURST_BULLET_RADIUS,
                FIRE_FLAME_BURST_BULLET_RADIUS,
                FIRE_FLAME_BURST_RANGE,
                1.1);
        runtime.shotsFired++;
    }

    private void performFireTrailOfFire(ActiveMoveRuntime runtime) {
        int startX = player.getCenterX();
        int startY = player.getCenterY();
        double angle = runtime != null ? runtime.aimAngleAtCast : computeAimAngleToMouse();
        int endX = startX + (int) Math.round(Math.cos(angle) * FIRE_TRAIL_DASH_DISTANCE);
        int endY = startY + (int) Math.round(Math.sin(angle) * FIRE_TRAIL_DASH_DISTANCE);
        int clampedX = Math.max(player.getW() / 2, Math.min(arena.getWidth() - player.getW() / 2, endX));
        int clampedY = Math.max(player.getL() / 2, Math.min(arena.getHeight() - player.getL() / 2, endY));
        player.setX(clampedX - player.getW() / 2);
        player.setY(clampedY - player.getL() / 2);

        long patchEndMs = System.currentTimeMillis() + FIRE_TRAIL_PATCH_DURATION_MS;
        for (int i = 0; i < FIRE_TRAIL_PATCH_COUNT; i++) {
            double t = FIRE_TRAIL_PATCH_COUNT == 1 ? 0 : (double) i / (FIRE_TRAIL_PATCH_COUNT - 1);
            int patchX = startX + (int) Math.round((clampedX - startX) * t);
            int patchY = startY + (int) Math.round((clampedY - startY) * t);
            FirePatch patch = new FirePatch();
            patch.x = patchX;
            patch.y = patchY;
            patch.radius = FIRE_TRAIL_PATCH_RADIUS;
            patch.endMs = patchEndMs;
            patch.lastTickMs = 0;
            activeFirePatches.add(patch);
            if (runtime != null) {
                runtime.visualLineTargets.add(new int[] { patchX, patchY });
            }
        }
        applyFirePatchTick(startX, startY, FIRE_TRAIL_PATCH_RADIUS);
        applyFirePatchTick(clampedX, clampedY, FIRE_TRAIL_PATCH_RADIUS);
    }

    private void performFireGrandFireball(ActiveMoveRuntime runtime) {
        if (runtime != null) {
            runtime.impactTriggered = false;
        }
    }

    private void spawnFireBullet(int startX,
                                 int startY,
                                 double angle,
                                 int damage,
                                 int hitRadius,
                                 int explosionRadius,
                                 int maxRange,
                                 double speedPxPerMs) {
        FireBullet bullet = new FireBullet();
        bullet.x = startX;
        bullet.y = startY;
        bullet.angle = angle;
        bullet.damage = damage;
        bullet.hitRadius = hitRadius;
        bullet.explosionRadius = explosionRadius;
        bullet.maxRange = maxRange;
        bullet.speedPxPerMs = speedPxPerMs;
        bullet.traveled = 0;
        bullet.active = true;
        activeFireBullets.add(bullet);
    }

    private void spawnGrandFireball(ActiveMoveRuntime runtime) {
        if (runtime == null) {
            return;
        }
        spawnFireBullet(
                player.getCenterX(),
                player.getCenterY(),
                runtime.aimAngleAtCast,
                FIRE_GRAND_FIREBALL_DAMAGE,
                FIRE_GRAND_FIREBALL_RADIUS / 2,
                FIRE_GRAND_FIREBALL_RADIUS,
                FIRE_GRAND_FIREBALL_RANGE,
                0.75);
    }

    private void applyFireHitToEnemy(entity.Enemy enemy, int immediateDamage) {
        if (enemy == null || enemy.isDead()) {
            return;
        }
        if (immediateDamage > 0) {
            applyPowerDamage(enemy, immediateDamage);
        }
        enemy.applyFireTick(FIRE_TICK_DAMAGE, FIRE_TICK_INTERVAL_MS, FIRE_TICK_DURATION_MS);
    }

    private void applyFireHitToBoss(entity.Boss boss, int immediateDamage) {
        if (boss == null || boss.isDead()) {
            return;
        }
        if (immediateDamage > 0) {
            applyPowerDamage(boss, immediateDamage);
        }
        boss.applyFireTick(FIRE_TICK_DAMAGE, FIRE_TICK_INTERVAL_MS, FIRE_TICK_DURATION_MS);
    }

    private void applyFireAoEDamage(int centerX, int centerY, int radius, int immediateDamage) {
        for (entity.Enemy enemy : findEnemiesInRadius(centerX, centerY, radius, Integer.MAX_VALUE)) {
            applyFireHitToEnemy(enemy, immediateDamage);
        }
        entity.Boss boss = enemyManager.getActiveBoss();
        if (boss != null && !boss.isDead()
                && isPointInRadius(centerX, centerY, boss.getCenterX(), boss.getCenterY(), radius)) {
            applyFireHitToBoss(boss, immediateDamage);
        }
    }

    private void applyFirePatchTick(int centerX, int centerY, int radius) {
        for (entity.Enemy enemy : findEnemiesInRadius(centerX, centerY, radius, Integer.MAX_VALUE)) {
            if (enemy != null && !enemy.isDead()) {
                enemy.applyFireTick(FIRE_TICK_DAMAGE, FIRE_TICK_INTERVAL_MS, FIRE_TICK_DURATION_MS);
            }
        }
        entity.Boss boss = enemyManager.getActiveBoss();
        if (boss != null && !boss.isDead()
                && isPointInRadius(centerX, centerY, boss.getCenterX(), boss.getCenterY(), radius)) {
            boss.applyFireTick(FIRE_TICK_DAMAGE, FIRE_TICK_INTERVAL_MS, FIRE_TICK_DURATION_MS);
        }
    }

    private void updateFireTickDamage() {
        for (entity.Enemy enemy : enemyManager.getEnemies()) {
            if (enemy == null || enemy.isDead()) {
                continue;
            }
            int tickDamage = enemy.consumeFireTickIfDue();
            if (tickDamage > 0) {
                applyPowerDamage(enemy, tickDamage);
            }
        }
        entity.Boss boss = enemyManager.getActiveBoss();
        if (boss != null && !boss.isDead()) {
            int tickDamage = boss.consumeFireTickIfDue();
            if (tickDamage > 0) {
                applyPowerDamage(boss, tickDamage);
            }
        }
    }

    private void updateFireBullets() {
        if (activeFireBullets.isEmpty()) {
            return;
        }
        long nowMs = System.currentTimeMillis();
        if (lastFireBulletUpdateMs == 0) {
            lastFireBulletUpdateMs = nowMs;
        }
        long deltaMs = Math.max(1, nowMs - lastFireBulletUpdateMs);
        lastFireBulletUpdateMs = nowMs;

        activeFireBullets.removeIf(bullet -> {
            if (bullet == null || !bullet.active) {
                return true;
            }
            double move = bullet.speedPxPerMs * deltaMs;
            bullet.traveled += move;
            bullet.x += Math.cos(bullet.angle) * move;
            bullet.y += Math.sin(bullet.angle) * move;

            entity.Enemy hitEnemy = findFirstEnemyInRadius((int) Math.round(bullet.x), (int) Math.round(bullet.y), bullet.hitRadius);
            entity.Boss boss = enemyManager.getActiveBoss();
            boolean bossHit = boss != null && !boss.isDead()
                    && isPointInRadius((int) Math.round(bullet.x), (int) Math.round(bullet.y),
                            boss.getCenterX(), boss.getCenterY(), bullet.hitRadius);

            if (hitEnemy != null || bossHit) {
                applyFireAoEDamage((int) Math.round(bullet.x), (int) Math.round(bullet.y),
                        bullet.explosionRadius, bullet.damage);
                return true;
            }
            if (bullet.traveled >= bullet.maxRange) {
                applyFireAoEDamage((int) Math.round(bullet.x), (int) Math.round(bullet.y),
                        bullet.explosionRadius, Math.max(1, bullet.damage / 2));
                return true;
            }
            return false;
        });
    }

    private void updateFirePatches() {
        if (activeFirePatches.isEmpty()) {
            return;
        }
        long nowMs = System.currentTimeMillis();
        activeFirePatches.removeIf(patch -> nowMs >= patch.endMs);
        for (FirePatch patch : activeFirePatches) {
            if (nowMs - patch.lastTickMs < FIRE_TICK_INTERVAL_MS) {
                continue;
            }
            patch.lastTickMs = nowMs;
            applyFirePatchTick(patch.x, patch.y, patch.radius);
        }
    }

    private ActiveMoveRuntime findLatestRuntimeFor(String powerName, int slot) {
        for (int i = activeMoveRuntimes.size() - 1; i >= 0; i--) {
            ActiveMoveRuntime r = activeMoveRuntimes.get(i);
            if (r != null && powerName.equals(r.powerName) && r.slot == slot) {
                return r;
            }
        }
        return null;
    }

    private void performStringOverheat(ActiveMoveRuntime runtime) {
        double angle = computeAimAngleToMouse();
        if (runtime != null) {
            runtime.aimAngleAtCast = angle;
            int endX = player.getCenterX() + (int) Math.round(Math.cos(angle) * STRING_OVERHEAT_FORWARD);
            int endY = player.getCenterY() + (int) Math.round(Math.sin(angle) * STRING_OVERHEAT_FORWARD);
            runtime.visualLineTargets.add(new int[] { endX, endY });
        }
        applyForwardRectDamage(
                player.getCenterX(),
                player.getCenterY(),
                angle,
                STRING_OVERHEAT_FORWARD,
                STRING_OVERHEAT_HALF_WIDTH,
                STRING_OVERHEAT_DAMAGE);
    }

    private void performStringParasite(ActiveMoveRuntime runtime) {
        java.util.List<entity.Enemy> targets = findEnemiesInRadius(
                player.getCenterX(), player.getCenterY(), STRING_PARASITE_RADIUS, STRING_PARASITE_MAX_TARGETS);
        for (entity.Enemy enemy : targets) {
            enemy.applyStun(STRING_PARASITE_STUN_MS);
            applyPowerDamage(enemy, STRING_PARASITE_DAMAGE_PER_TARGET);
            if (runtime != null) {
                runtime.visualLineTargets.add(new int[] { enemy.getCenterX(), enemy.getCenterY() });
            }
        }
    }

    private void performStringGodThread(ActiveMoveRuntime runtime) {
        if (runtime == null) {
            return;
        }
        runtime.shotsFired = 0;
        runtime.lastTickMs = System.currentTimeMillis();
        fireOneGodThreadString(runtime);
    }

    private void fireOneGodThreadString(ActiveMoveRuntime runtime) {
        entity.Enemy nearestEnemy = findNearestEnemyInRadius(
                player.getCenterX(), player.getCenterY(), STRING_GOD_THREAD_RADIUS);
        entity.Boss boss = enemyManager.getActiveBoss();
        boolean bossNear = boss != null && !boss.isDead()
                && isPointInRadius(player.getCenterX(), player.getCenterY(),
                        boss.getCenterX(), boss.getCenterY(), STRING_GOD_THREAD_RADIUS);

        int targetX;
        int targetY;
        if (nearestEnemy != null && (!bossNear || closerToPlayer(nearestEnemy, boss))) {
            targetX = nearestEnemy.getCenterX();
            targetY = nearestEnemy.getCenterY();
            applyPowerDamage(nearestEnemy, STRING_GOD_THREAD_DAMAGE_PER_STRING);
            if (!nearestEnemy.isDead()) {
                nearestEnemy.applyStun(STRING_GOD_THREAD_STUN_MS);
            }
        } else if (bossNear) {
            targetX = boss.getCenterX();
            targetY = boss.getCenterY();
            applyPowerDamage(boss, STRING_GOD_THREAD_DAMAGE_PER_STRING);
            if (!boss.isDead()) {
                boss.applyStun(STRING_GOD_THREAD_STUN_MS);
            }
        } else {
            // No target in range: still show the string flying toward cursor for visual feedback.
            double angle = computeAimAngleToMouse();
            targetX = player.getCenterX() + (int) Math.round(Math.cos(angle) * STRING_GOD_THREAD_RADIUS);
            targetY = player.getCenterY() + (int) Math.round(Math.sin(angle) * STRING_GOD_THREAD_RADIUS);
        }
        runtime.visualLineTargets.add(new int[] { targetX, targetY });
        runtime.shotsFired++;
    }

    private boolean closerToPlayer(entity.Enemy enemy, entity.Boss boss) {
        long ex = enemy.getCenterX() - player.getCenterX();
        long ey = enemy.getCenterY() - player.getCenterY();
        long bx = boss.getCenterX() - player.getCenterX();
        long by = boss.getCenterY() - player.getCenterY();
        return (ex * ex + ey * ey) <= (bx * bx + by * by);
    }

    private java.util.List<entity.Enemy> findEnemiesInRadius(int cx, int cy, int radius, int maxResults) {
        java.util.List<entity.Enemy> list = new ArrayList<>();
        for (entity.Enemy enemy : enemyManager.getEnemies()) {
            if (enemy == null || enemy.isDead()) {
                continue;
            }
            if (isPointInRadius(cx, cy, enemy.getCenterX(), enemy.getCenterY(), radius)) {
                list.add(enemy);
            }
        }
        list.sort((a, b) -> {
            long dax = a.getCenterX() - cx;
            long day = a.getCenterY() - cy;
            long dbx = b.getCenterX() - cx;
            long dby = b.getCenterY() - cy;
            return Long.compare(dax * dax + day * day, dbx * dbx + dby * dby);
        });
        if (list.size() > maxResults) {
            return new ArrayList<>(list.subList(0, maxResults));
        }
        return list;
    }

    private entity.Enemy findNearestEnemyInRadius(int cx, int cy, int radius) {
        entity.Enemy nearest = null;
        long bestSq = Long.MAX_VALUE;
        for (entity.Enemy enemy : enemyManager.getEnemies()) {
            if (enemy == null || enemy.isDead()) {
                continue;
            }
            long dx = enemy.getCenterX() - cx;
            long dy = enemy.getCenterY() - cy;
            long sq = dx * dx + dy * dy;
            if (sq <= (long) radius * radius && sq < bestSq) {
                bestSq = sq;
                nearest = enemy;
            }
        }
        return nearest;
    }

    private void updateActiveMoveRuntimes() {
        if (activeMoveRuntimes.isEmpty()) {
            return;
        }
        long nowMs = System.currentTimeMillis();
        activeMoveRuntimes.removeIf(runtime -> {
            updateActiveMoveRuntime(runtime, nowMs);
            return nowMs >= runtime.endMs;
        });
    }

    private void updateActiveMoveRuntime(ActiveMoveRuntime runtime, long nowMs) {
        if ("Rubber Fruit".equals(runtime.powerName)) {
            updateRubberMoveRuntime(runtime, nowMs);
        } else if ("String Fruit".equals(runtime.powerName)) {
            updateStringMoveRuntime(runtime, nowMs);
        } else if ("Earth".equals(runtime.powerName)) {
            updateEarthMoveRuntime(runtime, nowMs);
        } else if ("Fire".equals(runtime.powerName)) {
            updateFireMoveRuntime(runtime, nowMs);
        }
    }

    private void updateStringMoveRuntime(ActiveMoveRuntime runtime, long nowMs) {
        if (runtime.slot == 3 && runtime.shotsFired < STRING_GOD_THREAD_STRINGS
                && nowMs - runtime.lastTickMs >= STRING_GOD_THREAD_SHOT_INTERVAL_MS) {
            runtime.lastTickMs = nowMs;
            fireOneGodThreadString(runtime);
        }
    }

    private void updateEarthMoveRuntime(ActiveMoveRuntime runtime, long nowMs) {
        switch (runtime.slot) {
            case 1 -> {
                if (runtime.shotsFired < EARTH_ROCK_BARRAGE_SHOTS
                        && nowMs - runtime.lastTickMs >= EARTH_ROCK_BARRAGE_SHOT_INTERVAL_MS) {
                    runtime.lastTickMs = nowMs;
                    fireOneEarthRock(runtime);
                }
            }
            case 3 -> {
                if (runtime.shotsFired < EARTH_BOULDER_COUNT
                        && nowMs - runtime.lastTickMs >= EARTH_BOULDER_SHOT_INTERVAL_MS) {
                    runtime.lastTickMs = nowMs;
                    fireOneEarthBoulder(runtime);
                }
            }
            default -> {
                // Spike Ring is fully resolved on cast.
            }
        }
    }

    private void updateFireMoveRuntime(ActiveMoveRuntime runtime, long nowMs) {
        switch (runtime.slot) {
            case 1 -> {
                if (runtime.shotsFired < FIRE_FLAME_BURST_BULLETS
                        && nowMs - runtime.lastTickMs >= FIRE_FLAME_BURST_SHOT_INTERVAL_MS) {
                    runtime.lastTickMs = nowMs;
                    fireOneFlameBurstBullet(runtime);
                }
            }
            case 3 -> {
                long total = Math.max(1, runtime.endMs - runtime.startMs);
                double progress = (double) (nowMs - runtime.startMs) / (double) total;
                if (!runtime.impactTriggered && progress >= FIRE_GRAND_FIREBALL_WINDUP_FRAC) {
                    runtime.impactTriggered = true;
                    spawnGrandFireball(runtime);
                }
            }
            default -> {
                // Trail of Fire resolves on cast.
            }
        }
    }

    private void updateRubberMoveRuntime(ActiveMoveRuntime runtime, long nowMs) {
        switch (runtime.slot) {
            case 1 -> {
                // Jet Gatling: repeating punch damage inside a rectangle in front of the player.
                final long tickEveryMs = 130;
                if (runtime.lastTickMs == 0 || nowMs - runtime.lastTickMs >= tickEveryMs) {
                    runtime.lastTickMs = nowMs;
                    double angle = computeAimAngleToMouse();
                    runtime.aimAngleAtCast = angle;
                    applyForwardRectDamage(
                            player.getCenterX(),
                            player.getCenterY(),
                            angle,
                            RUBBER_JET_GATLING_FORWARD,
                            RUBBER_JET_GATLING_HALF_WIDTH,
                            16);
                }
            }
            case 3 -> {
                // Elephant Gun: wind-up phase then a single big punch in front of the player.
                long elapsed = nowMs - runtime.startMs;
                long total = Math.max(1, runtime.endMs - runtime.startMs);
                double progress = (double) elapsed / (double) total;
                if (!runtime.impactTriggered && progress >= RUBBER_ELEPHANT_GUN_WINDUP_FRAC) {
                    runtime.impactTriggered = true;
                    int forwardOffset = RUBBER_ELEPHANT_GUN_FORWARD;
                    int impactX = player.getCenterX()
                            + (int) Math.round(Math.cos(runtime.aimAngleAtCast) * forwardOffset);
                    int impactY = player.getCenterY()
                            + (int) Math.round(Math.sin(runtime.aimAngleAtCast) * forwardOffset);
                    applyPowerAoEDamage(impactX, impactY, RUBBER_ELEPHANT_GUN_RADIUS, 65, false);
                }
            }
            default -> {
                // No runtime update needed.
            }
        }
    }

    private void applyForwardRectDamage(int originX, int originY, double angle, int forwardLength, int halfWidth, int damage) {
        if (forwardLength <= 0 || halfWidth <= 0 || damage <= 0) {
            return;
        }
        for (entity.Enemy enemy : enemyManager.getEnemies()) {
            if (enemy == null || enemy.isDead()) {
                continue;
            }
            if (isPointInForwardRect(enemy.getCenterX(), enemy.getCenterY(), originX, originY, angle, forwardLength, halfWidth)) {
                applyPowerDamage(enemy, damage);
            }
        }
        entity.Boss boss = enemyManager.getActiveBoss();
        if (boss != null && !boss.isDead()
                && isPointInForwardRect(boss.getCenterX(), boss.getCenterY(), originX, originY, angle, forwardLength, halfWidth)) {
            applyPowerDamage(boss, damage);
        }
    }

    private boolean isPointInForwardRect(int targetX, int targetY, int originX, int originY, double angle, int forwardLength, int halfWidth) {
        double dx = targetX - originX;
        double dy = targetY - originY;
        double cos = Math.cos(-angle);
        double sin = Math.sin(-angle);
        double localX = dx * cos - dy * sin;
        double localY = dx * sin + dy * cos;
        return localX >= 0 && localX <= forwardLength && Math.abs(localY) <= halfWidth;
    }

    private void applyPowerAoEDamage(int centerX, int centerY, int radius, int damage, boolean canHitMultipleTimes) {
        if (radius <= 0 || damage <= 0) {
            return;
        }
        for (entity.Enemy enemy : enemyManager.getEnemies()) {
            if (enemy == null || enemy.isDead()) {
                continue;
            }
            if (isPointInRadius(centerX, centerY, enemy.getCenterX(), enemy.getCenterY(), radius)) {
                applyPowerDamage(enemy, damage);
                if (!canHitMultipleTimes) {
                    // Intentionally continue: heavy AoE should hit all enemies once.
                }
            }
        }

        entity.Boss boss = enemyManager.getActiveBoss();
        if (boss != null && !boss.isDead()
                && isPointInRadius(centerX, centerY, boss.getCenterX(), boss.getCenterY(), radius)) {
            applyPowerDamage(boss, damage);
        }
    }

    /** Power / burn damage against any target, enemy or boss. */
    private void applyPowerDamage(entity.Entity target, int damage) {
        player.dealDamage(target, damage, false, 0);
    }

    private boolean isPointInRadius(int centerX, int centerY, int x, int y, int radius) {
        long dx = x - centerX;
        long dy = y - centerY;
        return dx * dx + dy * dy <= (long) radius * radius;
    }

    private void performRubberShaveDash() {
        int targetWorldX = lastMouseX + camera.x;
        int targetWorldY = lastMouseY + camera.y;
        int clampedX = Math.max(player.getW() / 2, Math.min(arena.getWidth() - player.getW() / 2, targetWorldX));
        int clampedY = Math.max(player.getL() / 2, Math.min(arena.getHeight() - player.getL() / 2, targetWorldY));
        player.setX(clampedX - player.getW() / 2);
        player.setY(clampedY - player.getL() / 2);
    }

    private void awardPowerUseMasteryXP(Power power, Move move) {
        if (power == null || move == null) {
            return;
        }
        String moveName = move.getName() == null ? "" : move.getName().toLowerCase();
        boolean utilityMove = moveName.contains("dash")
                || moveName.contains("blink")
                || moveName.contains("teleport")
                || moveName.contains("mobility")
                || moveName.contains("shield")
                || moveName.contains("buff")
                || moveName.contains("heal")
                || moveName.contains("speed");
        MasteryService.grantPowerUseXP(power.getName(), utilityMove);
    }

    private void drawActivePowerMoveVisuals(Graphics2D g) {
        long nowMs = System.currentTimeMillis();
        for (ActiveMoveRuntime runtime : activeMoveRuntimes) {
            if (runtime == null || nowMs >= runtime.endMs) {
                continue;
            }
            if ("Rubber Fruit".equals(runtime.powerName)) {
                drawRubberMoveVisual(g, runtime, nowMs);
            } else if ("String Fruit".equals(runtime.powerName)) {
                drawStringMoveVisual(g, runtime, nowMs);
            } else if ("Earth".equals(runtime.powerName)) {
                drawEarthMoveVisual(g, runtime, nowMs);
            } else if ("Fire".equals(runtime.powerName)) {
                drawFireMoveVisual(g, runtime, nowMs);
            } else {
                drawGenericMoveVisual(g, runtime);
            }
        }
        drawFireBullets(g);
        drawFirePatches(g);
    }

    private void drawStringMoveVisual(Graphics2D g, ActiveMoveRuntime runtime, long nowMs) {
        int cx = player.getCenterX() - camera.x;
        int cy = player.getCenterY() - camera.y;
        java.awt.Stroke oldStroke = g.getStroke();
        switch (runtime.slot) {
            case 1 -> {
                g.setColor(new Color(255, 230, 140, 230));
                g.setStroke(new java.awt.BasicStroke(2.4f));
                for (int[] target : runtime.visualLineTargets) {
                    g.drawLine(cx, cy, target[0] - camera.x, target[1] - camera.y);
                }
                g.setColor(new Color(255, 200, 110, 90));
                int radius = 22;
                g.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
                drawMoveLabel(g, runtime.moveName, cx, cy - 36);
            }
            case 2 -> {
                drawAreaRing(g, cx, cy, STRING_PARASITE_RADIUS,
                        new Color(180, 130, 255, 40), new Color(200, 160, 255, 170));
                g.setColor(new Color(190, 150, 255, 220));
                g.setStroke(new java.awt.BasicStroke(1.6f));
                for (int[] target : runtime.visualLineTargets) {
                    g.drawLine(cx, cy, target[0] - camera.x, target[1] - camera.y);
                    int tx = target[0] - camera.x;
                    int ty = target[1] - camera.y;
                    int r = 6;
                    g.setColor(new Color(220, 180, 255, 220));
                    g.fillOval(tx - r, ty - r, r * 2, r * 2);
                    g.setColor(new Color(190, 150, 255, 220));
                }
                drawMoveLabel(g, runtime.moveName, cx, cy - STRING_PARASITE_RADIUS - 10);
            }
            case 3 -> {
                drawAreaRing(g, cx, cy, STRING_GOD_THREAD_RADIUS,
                        new Color(180, 130, 255, 40), new Color(200, 160, 255, 170));
                g.setStroke(new java.awt.BasicStroke(1.6f));
                for (int[] target : runtime.visualLineTargets) {
                    int tx = target[0] - camera.x;
                    int ty = target[1] - camera.y;
                    g.setColor(new Color(190, 150, 255, 220));
                    g.drawLine(cx, cy, tx, ty);
                    int r = 6;
                    g.setColor(new Color(220, 180, 255, 220));
                    g.fillOval(tx - r, ty - r, r * 2, r * 2);
                }
                drawMoveLabel(g, runtime.moveName, cx, cy - STRING_GOD_THREAD_RADIUS - 10);
            }
            default -> drawGenericMoveVisual(g, runtime);
        }
        g.setStroke(oldStroke);
    }

    private void drawEarthMoveVisual(Graphics2D g, ActiveMoveRuntime runtime, long nowMs) {
        int cx = player.getCenterX() - camera.x;
        int cy = player.getCenterY() - camera.y;
        java.awt.Stroke oldStroke = g.getStroke();
        switch (runtime.slot) {
            case 1 -> {
                g.setColor(new Color(160, 120, 80, 220));
                g.setStroke(new java.awt.BasicStroke(2.0f));
                for (int[] target : runtime.visualLineTargets) {
                    int tx = target[0] - camera.x;
                    int ty = target[1] - camera.y;
                    g.drawLine(cx, cy, tx, ty);
                    int r = 10;
                    g.setColor(new Color(120, 90, 55, 210));
                    g.fillOval(tx - r, ty - r, r * 2, r * 2);
                    g.setColor(new Color(160, 120, 80, 220));
                }
                drawMoveLabel(g, runtime.moveName, cx, cy - 36);
            }
            case 2 -> {
                int anchorScreenX = runtime.anchorX - camera.x;
                int anchorScreenY = runtime.anchorY - camera.y;
                drawAreaRing(g, anchorScreenX, anchorScreenY, EARTH_SPIKE_RING_RADIUS,
                        new Color(120, 90, 55, 45), new Color(170, 130, 80, 200));
                g.setColor(new Color(100, 75, 45, 220));
                g.setStroke(new java.awt.BasicStroke(2.0f));
                for (int i = 0; i < 12; i++) {
                    double angle = (Math.PI * 2.0 * i) / 12.0;
                    int innerX = anchorScreenX + (int) Math.round(Math.cos(angle) * (EARTH_SPIKE_RING_RADIUS - 18));
                    int innerY = anchorScreenY + (int) Math.round(Math.sin(angle) * (EARTH_SPIKE_RING_RADIUS - 18));
                    int outerX = anchorScreenX + (int) Math.round(Math.cos(angle) * (EARTH_SPIKE_RING_RADIUS + 8));
                    int outerY = anchorScreenY + (int) Math.round(Math.sin(angle) * (EARTH_SPIKE_RING_RADIUS + 8));
                    g.drawLine(innerX, innerY, outerX, outerY);
                }
                drawMoveLabel(g, runtime.moveName, anchorScreenX, anchorScreenY - EARTH_SPIKE_RING_RADIUS - 10);
            }
            case 3 -> {
                g.setStroke(new java.awt.BasicStroke(2.4f));
                for (int[] target : runtime.visualLineTargets) {
                    int tx = target[0] - camera.x;
                    int ty = target[1] - camera.y;
                    g.setColor(new Color(140, 100, 60, 220));
                    g.drawLine(cx, cy, tx, ty);
                    int r = 18;
                    g.setColor(new Color(100, 70, 40, 220));
                    g.fillOval(tx - r, ty - r, r * 2, r * 2);
                    g.setColor(new Color(180, 130, 70, 180));
                    g.drawOval(tx - (EARTH_BOULDER_RADIUS / 2), ty - (EARTH_BOULDER_RADIUS / 2),
                            EARTH_BOULDER_RADIUS, EARTH_BOULDER_RADIUS);
                }
                drawMoveLabel(g, runtime.moveName, cx, cy - 48);
            }
            default -> drawGenericMoveVisual(g, runtime);
        }
        g.setStroke(oldStroke);
    }

    private void drawFireMoveVisual(Graphics2D g, ActiveMoveRuntime runtime, long nowMs) {
        int cx = player.getCenterX() - camera.x;
        int cy = player.getCenterY() - camera.y;
        switch (runtime.slot) {
            case 1 -> drawMoveLabel(g, runtime.moveName, cx, cy - 36);
            case 2 -> {
                g.setColor(new Color(255, 120, 40, 180));
                for (int[] target : runtime.visualLineTargets) {
                    int tx = target[0] - camera.x;
                    int ty = target[1] - camera.y;
                    drawAreaRing(g, tx, ty, FIRE_TRAIL_PATCH_RADIUS,
                            new Color(255, 90, 20, 55), new Color(255, 170, 60, 200));
                }
                drawMoveLabel(g, runtime.moveName, cx, cy - 48);
            }
            case 3 -> {
                long total = Math.max(1, runtime.endMs - runtime.startMs);
                double progress = (double) (nowMs - runtime.startMs) / (double) total;
                if (!runtime.impactTriggered) {
                    int chargeRadius = 16 + (int) (10 * (progress / FIRE_GRAND_FIREBALL_WINDUP_FRAC));
                    g.setColor(new Color(255, 180, 60, 170));
                    g.fillOval(cx - chargeRadius, cy - chargeRadius, chargeRadius * 2, chargeRadius * 2);
                }
                drawMoveLabel(g, runtime.moveName, cx, cy - 56);
            }
            default -> drawGenericMoveVisual(g, runtime);
        }
    }

    private void drawFireBullets(Graphics2D g) {
        for (FireBullet bullet : activeFireBullets) {
            if (bullet == null || !bullet.active) {
                continue;
            }
            int bx = (int) Math.round(bullet.x) - camera.x;
            int by = (int) Math.round(bullet.y) - camera.y;
            int r = bullet.explosionRadius >= FIRE_GRAND_FIREBALL_RADIUS / 2 ? 14 : 7;
            g.setColor(new Color(255, 210, 80, 230));
            g.fillOval(bx - r, by - r, r * 2, r * 2);
            g.setColor(new Color(255, 120, 20, 220));
            g.drawOval(bx - r, by - r, r * 2, r * 2);
        }
    }

    private void drawFirePatches(Graphics2D g) {
        long nowMs = System.currentTimeMillis();
        for (FirePatch patch : activeFirePatches) {
            if (patch == null || nowMs >= patch.endMs) {
                continue;
            }
            int px = patch.x - camera.x;
            int py = patch.y - camera.y;
            drawAreaRing(g, px, py, patch.radius,
                    new Color(255, 80, 10, 50), new Color(255, 150, 40, 180));
        }
    }

    private void drawAreaRing(Graphics2D g, int cx, int cy, int radius, Color fill, Color outline) {
        g.setColor(fill);
        g.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
        g.setColor(outline);
        g.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
    }

    private void drawRubberMoveVisual(Graphics2D g, ActiveMoveRuntime runtime, long nowMs) {
        int cx = player.getCenterX() - camera.x;
        int cy = player.getCenterY() - camera.y;
        switch (runtime.slot) {
            case 1 -> {
                double angle = computeAimAngleToMouse();
                drawForwardRect(g, cx, cy, angle,
                        RUBBER_JET_GATLING_FORWARD,
                        RUBBER_JET_GATLING_HALF_WIDTH,
                        new Color(255, 210, 50, 110),
                        new Color(255, 245, 180, 220));
                int fistOffset = 22;
                int fistX = cx + (int) Math.round(Math.cos(angle) * fistOffset);
                int fistY = cy + (int) Math.round(Math.sin(angle) * fistOffset);
                int fistRadius = 14 + (int) (4 * Math.sin(nowMs / 40.0));
                g.setColor(new Color(255, 230, 120, 200));
                g.fillOval(fistX - fistRadius, fistY - fistRadius, fistRadius * 2, fistRadius * 2);
                drawMoveLabel(g, runtime.moveName, cx, cy - 70);
            }
            case 2 -> {
                int radius = 50;
                g.setColor(new Color(150, 220, 255, 90));
                g.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
                g.setColor(new Color(220, 240, 255, 200));
                g.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
                drawMoveLabel(g, runtime.moveName, cx, cy - radius - 8);
            }
            case 3 -> {
                long elapsed = nowMs - runtime.startMs;
                long total = Math.max(1, runtime.endMs - runtime.startMs);
                double progress = Math.min(1.0, (double) elapsed / (double) total);
                double angle = runtime.aimAngleAtCast;
                if (progress < RUBBER_ELEPHANT_GUN_WINDUP_FRAC) {
                    double windupProgress = progress / RUBBER_ELEPHANT_GUN_WINDUP_FRAC;
                    int baseFistRadius = 18;
                    int maxFistRadius = 65;
                    int fistRadius = (int) Math.round(baseFistRadius + (maxFistRadius - baseFistRadius) * windupProgress);
                    int fistOffset = 30 + (int) Math.round(20 * windupProgress);
                    int fistX = cx + (int) Math.round(Math.cos(angle) * fistOffset);
                    int fistY = cy + (int) Math.round(Math.sin(angle) * fistOffset);
                    g.setColor(new Color(255, 170, 110, 200));
                    g.fillOval(fistX - fistRadius, fistY - fistRadius, fistRadius * 2, fistRadius * 2);
                    g.setColor(new Color(255, 220, 180, 220));
                    g.drawOval(fistX - fistRadius, fistY - fistRadius, fistRadius * 2, fistRadius * 2);
                    int puffOffset = -10 - (int) Math.round(8 * windupProgress);
                    int puffX = cx + (int) Math.round(Math.cos(angle) * puffOffset);
                    int puffY = cy + (int) Math.round(Math.sin(angle) * puffOffset);
                    int puffRadius = 8 + (int) Math.round(6 * windupProgress);
                    g.setColor(new Color(220, 230, 245, 150));
                    g.fillOval(puffX - puffRadius, puffY - puffRadius, puffRadius * 2, puffRadius * 2);
                } else {
                    double punchProgress = (progress - RUBBER_ELEPHANT_GUN_WINDUP_FRAC) / (1.0 - RUBBER_ELEPHANT_GUN_WINDUP_FRAC);
                    int impactScreenX = cx + (int) Math.round(Math.cos(angle) * RUBBER_ELEPHANT_GUN_FORWARD);
                    int impactScreenY = cy + (int) Math.round(Math.sin(angle) * RUBBER_ELEPHANT_GUN_FORWARD);
                    int fistRadius = (int) Math.round(70 - 25 * punchProgress);
                    g.setColor(new Color(255, 140, 90, 200));
                    g.fillOval(impactScreenX - fistRadius, impactScreenY - fistRadius, fistRadius * 2, fistRadius * 2);
                    int shockRadius = (int) Math.round(RUBBER_ELEPHANT_GUN_RADIUS * (0.6 + 0.6 * punchProgress));
                    g.setColor(new Color(255, 180, 120, 110));
                    g.fillOval(impactScreenX - shockRadius, impactScreenY - shockRadius, shockRadius * 2, shockRadius * 2);
                    g.setColor(new Color(255, 240, 200, 220));
                    g.drawOval(impactScreenX - shockRadius, impactScreenY - shockRadius, shockRadius * 2, shockRadius * 2);
                    drawForwardRect(g, cx, cy, angle,
                            RUBBER_ELEPHANT_GUN_FORWARD,
                            RUBBER_JET_GATLING_HALF_WIDTH,
                            new Color(255, 150, 100, 70),
                            new Color(255, 200, 150, 160));
                }
                drawMoveLabel(g, runtime.moveName, cx, cy - 90);
            }
            default -> drawGenericMoveVisual(g, runtime);
        }
    }

    private void drawForwardRect(Graphics2D g, int cx, int cy, double angle, int forwardLength, int halfWidth, Color fill, Color outline) {
        java.awt.geom.AffineTransform old = g.getTransform();
        g.rotate(angle, cx, cy);
        g.setColor(fill);
        g.fillRect(cx, cy - halfWidth, forwardLength, halfWidth * 2);
        g.setColor(outline);
        g.drawRect(cx, cy - halfWidth, forwardLength, halfWidth * 2);
        g.setTransform(old);
    }

    private void drawGenericMoveVisual(Graphics2D g, ActiveMoveRuntime runtime) {
        int cx = player.getCenterX() - camera.x;
        int cy = player.getCenterY() - camera.y;
        int radius = 70;
        g.setColor(new Color(180, 180, 255, 75));
        g.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
        g.setColor(new Color(255, 255, 255, 180));
        g.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
        drawMoveLabel(g, runtime.moveName, cx, cy - radius - 8);
    }

    private void drawMoveLabel(Graphics2D g, String label, int cx, int topY) {
        if (label == null || label.isBlank()) {
            return;
        }
        g.setFont(new Font("Arial", Font.BOLD, 11));
        int textWidth = g.getFontMetrics().stringWidth(label);
        g.setColor(new Color(255, 255, 255, 220));
        g.drawString(label, cx - (textWidth / 2), topY);
    }

    private void drawPowerMovesUI(Graphics2D g, int screenWidth, int screenHeight) {
        // Only draw if a power is equipped
        Power equippedPower = player.getInventory().getPower();
        if (equippedPower == null) return;
        equippedPower.applyMasteryMoveUnlocks();

        int maxMoves = equippedPower.getMaxMoves();
        if (maxMoves <= 0) return;

        // Box dimensions
        int boxWidth = 200;
        int boxHeight = 40;
        int boxSpacing = 5;
        int padding = 20;

        // Starting position (bottom-right corner)
        int startX = screenWidth - boxWidth - padding;
        long nowMs = System.currentTimeMillis();

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

            long remainingCooldown = move.getRemainingCooldownMs(nowMs);
            long remainingActive = move.getRemainingActiveMs(nowMs);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 11));
            if (remainingActive > 0) {
                g.setColor(new Color(120, 255, 170));
                g.drawString("Active " + String.format("%.1fs", remainingActive / 1000.0), startX + 10, y + 38);
            } else if (remainingCooldown > 0) {
                g.setColor(new Color(255, 210, 120));
                g.drawString("CD " + String.format("%.1fs", remainingCooldown / 1000.0), startX + 10, y + 38);
            } else {
                g.setColor(new Color(170, 255, 170));
                g.drawString("Ready", startX + 10, y + 38);
            }

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

    /**
     * Run payout, shared by the victory and death screens.
     *
     * @param payoutFraction share of the full reward to pay (see {@link GameMode#getDeathPayoutFraction()})
     */
    private int computeRunCashReward(int kills, int wavesSurvived, double damageDealt, double payoutFraction) {
        int fromWaves = 35 * wavesSurvived;
        int fromKills = 12 * kills;
        int fromDamage = (int) (damageDealt / 40.0);
        int base = Math.max(150, fromWaves + fromKills + fromDamage);
        double adjusted = base * gameMode.getRewardMultiplier() * payoutFraction;
        return skilltree.SkillEffects.scaleCashGain((int) Math.round(adjusted));
    }

    private void grantCash(int amount) {
        if (gamePanel == null || amount <= 0) {
            return;
        }
        gamePanel.getCurrencyManager().addCash(amount);
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
        if (!waveManager.isRunComplete()) {
            return;
        }
        arenaRunCompleteKills = player.getStats().getKills();
        arenaRunCompleteDamage = player.getStats().getDamageDealt();
        arenaRunCompleteWaves = waveManager.getWavesSurvived();
        arenaRunCompleteCash = computeRunCashReward(
                arenaRunCompleteKills, arenaRunCompleteWaves, arenaRunCompleteDamage, 1.0);
        grantCash(arenaRunCompleteCash);
        if (gameMode.unlocksNextArena()) {
            persistArenaRunVictoryProgression();
        }
        arenaRunCompleteOverlay = true;
        enterPressedLastFrame = true;
        spacePressedLastFrame = true;
        if (gamePanel != null) {
            gamePanel.requestFocusInWindow();
        }
    }

    /** Pays out the run, records the death and freezes the arena behind the game-over modal. */
    private void openDeathOverlay() {
        if (deathOverlay) {
            return;
        }
        deathOverlay = true;
        player.getStats().addDeath();
        deathKills = player.getStats().getKills();
        deathDamage = player.getStats().getDamageDealt();
        deathWaves = waveManager.getWavesSurvived();
        deathCash = computeRunCashReward(deathKills, deathWaves, deathDamage,
                gameMode.getDeathPayoutFraction());
        grantCash(deathCash);
        syncRunStats();
        feedback.addShake(11.0, 450);
        enterPressedLastFrame = true;
        spacePressedLastFrame = true;
        if (gamePanel != null) {
            gamePanel.requestFocusInWindow();
        }
    }

    /**
     * Handles results / game-over modal clicks on the EDT (before the game tick).
     *
     * @return true if the click was consumed by a modal
     */
    public boolean handleOverlayClick(MouseHandler mouse, int x, int y, int screenWidth, int screenHeight) {
        Rectangle primary;
        Rectangle secondary;
        if (arenaRunCompleteOverlay) {
            primary = arenaRunCompleteContinueBtn;
            secondary = arenaRunCompleteNextArenaBtn;
        } else if (deathOverlay) {
            primary = deathRetryBtn;
            secondary = deathHubBtn;
        } else {
            return false;
        }

        layoutResultsButtons(primary, secondary, screenWidth, screenHeight);
        overlayMouseX = x;
        overlayMouseY = y;
        if (!mouse.leftClicked) {
            return false;
        }
        mouse.leftClicked = false;
        int action = primary.contains(x, y) ? RESULT_PRIMARY
                : (secondary.contains(x, y) ? RESULT_SECONDARY : RESULT_NONE);
        if (arenaRunCompleteOverlay) {
            applyArenaCompleteAction(action);
        } else {
            applyDeathAction(action);
        }
        return true;
    }

    private static final int RESULTS_MODAL_W = 720;
    private static final int RESULTS_MODAL_H = 460;
    private static final int RESULT_NONE = 0;
    private static final int RESULT_PRIMARY = 1;
    private static final int RESULT_SECONDARY = 2;
    private static final int RESULT_CONFIRM_KEY = 3;

    /** Shared button bounds for both results modals; draw and input must agree on this layout. */
    private void layoutResultsButtons(Rectangle primary, Rectangle secondary, int screenWidth, int screenHeight) {
        int my = (screenHeight - RESULTS_MODAL_H) / 2;
        int btnW = 300;
        int btnH = 48;
        int gap = 20;
        int btnY = my + RESULTS_MODAL_H - btnH * 2 - gap - 36;
        int rowX = (screenWidth - btnW) / 2;
        primary.setBounds(rowX, btnY, btnW, btnH);
        secondary.setBounds(rowX, btnY + btnH + gap, btnW, btnH);
    }

    /** Reads mouse / Enter / Space for a two-button modal and reports which action was chosen. */
    private int pollResultsModal(KeyHandler key, MouseHandler mouse, Rectangle primary, Rectangle secondary,
                                 int screenWidth, int screenHeight) {
        layoutResultsButtons(primary, secondary, screenWidth, screenHeight);
        overlayMouseX = mouse.mouseX;
        overlayMouseY = mouse.mouseY;

        boolean confirmEdge = (key.enterPressed && !enterPressedLastFrame)
                || (key.spacePressed && !spacePressedLastFrame);
        enterPressedLastFrame = key.enterPressed;
        spacePressedLastFrame = key.spacePressed;

        if (mouse.leftClicked) {
            mouse.leftClicked = false;
            if (primary.contains(mouse.mouseX, mouse.mouseY)) {
                return RESULT_PRIMARY;
            }
            if (secondary.contains(mouse.mouseX, mouse.mouseY)) {
                return RESULT_SECONDARY;
            }
            return RESULT_NONE;
        }
        return confirmEdge ? RESULT_CONFIRM_KEY : RESULT_NONE;
    }

    private void updateArenaRunCompleteOverlay(KeyHandler key, MouseHandler mouse, int screenWidth, int screenHeight) {
        applyArenaCompleteAction(pollResultsModal(key, mouse,
                arenaRunCompleteContinueBtn, arenaRunCompleteNextArenaBtn, screenWidth, screenHeight));
    }

    private void applyArenaCompleteAction(int action) {
        boolean hasNextArena = gameMode.unlocksNextArena() && arenaIdAfter(selectedArenaName) != null;
        switch (action) {
            case RESULT_PRIMARY -> exitToHub();
            case RESULT_SECONDARY -> closeArenaRunCompleteGoNextArena();
            case RESULT_CONFIRM_KEY -> {
                if (hasNextArena) {
                    closeArenaRunCompleteGoNextArena();
                } else {
                    exitToHub();
                }
            }
            default -> { /* nothing picked yet */ }
        }
    }

    private void updateDeathOverlay(KeyHandler key, MouseHandler mouse, int screenWidth, int screenHeight) {
        applyDeathAction(pollResultsModal(key, mouse, deathRetryBtn, deathHubBtn, screenWidth, screenHeight));
    }

    private void applyDeathAction(int action) {
        switch (action) {
            case RESULT_PRIMARY, RESULT_CONFIRM_KEY -> retryRun();
            case RESULT_SECONDARY -> exitToHub();
            default -> { /* nothing picked yet */ }
        }
    }

    /** Restarts the same arena and mode from wave 1. */
    private void retryRun() {
        deathOverlay = false;
        resetGame();
        if (gamePanel != null) {
            gamePanel.getKeyHandler().lPressed = false;
            resetMouseClicks(gamePanel.getMouseHandler());
            gamePanel.requestFocusInWindow();
        }
    }

    private void exitToHub() {
        arenaRunCompleteOverlay = false;
        deathOverlay = false;
        if (gamePanel != null) {
            syncRunStats();
            gamePanel.switchScreen("hub");
        }
    }

    private void closeArenaRunCompleteGoNextArena() {
        arenaRunCompleteOverlay = false;
        if (gamePanel == null) {
            return;
        }
        gamePanel.getKeyHandler().lPressed = false;
        syncRunStats();
        if (!gameMode.unlocksNextArena()) {
            // Side modes have no arena ladder; replay the same arena instead.
            retryRun();
            return;
        }
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
        boolean hasNextArena = gameMode.unlocksNextArena() && arenaIdAfter(selectedArenaName) != null;
        String title = switch (gameMode) {
            case STANDARD -> "Arena cleared";
            case BOSS_RUSH -> "Boss rush cleared";
            case TIME_ATTACK -> "Time's up";
            case ENDLESS -> "Run complete";
        };
        drawResultsModal(g, screenWidth, screenHeight,
                title, new Color(120, 200, 255),
                arenaRunCompleteKills, arenaRunCompleteDamage, arenaRunCompleteWaves, arenaRunCompleteCash,
                "Reward is based on kills, total damage, and waves cleared.",
                arenaRunCompleteContinueBtn, hasNextArena ? "Return to Hub" : "Return to Hub (Enter)",
                arenaRunCompleteNextArenaBtn, hasNextArena ? "Next Arena (Enter)" : "Play again");
    }

    private void drawDeathOverlay(Graphics2D g, int screenWidth, int screenHeight) {
        int payoutPercent = (int) Math.round(gameMode.getDeathPayoutFraction() * 100);
        String footnote = payoutPercent >= 100
                ? "Full reward paid out for this mode."
                : "You keep " + payoutPercent + "% of the run reward for dying.";
        drawResultsModal(g, screenWidth, screenHeight,
                "You died", new Color(235, 90, 90),
                deathKills, deathDamage, deathWaves, deathCash, footnote,
                deathRetryBtn, "Retry Arena (Enter)",
                deathHubBtn, "Return to Hub");
    }

    /** Shared results modal used by both the victory and game-over screens. */
    private void drawResultsModal(Graphics2D g, int screenWidth, int screenHeight,
                                  String title, Color accent,
                                  int kills, double damage, int waves, int cash, String footnote,
                                  Rectangle primary, String primaryLabel,
                                  Rectangle secondary, String secondaryLabel) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, screenWidth, screenHeight);

        int mw = RESULTS_MODAL_W;
        int mh = RESULTS_MODAL_H;
        int mx = (screenWidth - mw) / 2;
        int my = (screenHeight - mh) / 2;

        g.setColor(new Color(20, 22, 30, 238));
        g.fillRoundRect(mx, my, mw, mh, 18, 18);
        g.setColor(accent);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(mx, my, mw, mh, 18, 18);
        g.setStroke(new BasicStroke(1));

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 34));
        g.drawString(title, mx + 28, my + 52);

        g.setColor(new Color(150, 165, 190));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g.drawString(gameMode.getDisplayName() + " - " + selectedArenaName, mx + 30, my + 76);

        g.setColor(new Color(190, 200, 220));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        int lineY = my + 120;
        g.drawString("Kills: " + kills, mx + 36, lineY);
        lineY += 36;
        g.drawString("Damage dealt: " + String.format("%,.0f", damage), mx + 36, lineY);
        lineY += 36;
        g.drawString("Waves survived: " + waves, mx + 36, lineY);
        lineY += 36;
        g.setColor(new Color(120, 230, 160));
        g.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g.drawString("Cash earned: $" + cash, mx + 36, lineY);
        lineY += 40;
        g.setColor(new Color(160, 170, 190));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g.drawString(footnote, mx + 36, lineY);

        layoutResultsButtons(primary, secondary, screenWidth, screenHeight);
        drawOverlayChoiceButton(g, primary, primaryLabel, primary.contains(overlayMouseX, overlayMouseY));
        drawOverlayChoiceButton(g, secondary, secondaryLabel, secondary.contains(overlayMouseX, overlayMouseY));
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
