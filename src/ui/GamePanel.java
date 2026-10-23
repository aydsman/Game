package ui;

import ui.screens.MenuScreen;
import ui.screens.GameScreen;
import ui.screens.HubScreen;
import ui.screens.PauseScreen;
import ui.screens.SettingsScreen;
import ui.screens.CustomizeScreen;
import ui.screens.HelpScreen;
import ui.screens.GraphTestScreen;
import ui.screens.ItemGalleryScreen;
import ui.screens.LoadoutScreen;
import ui.screens.ShopScreen;
import ui.screens.LootboxTestScreen;
import ui.screens.RewardsScreen;
import ui.screens.ArenaSelectionScreen;
import ui.screens.ModeSelectionScreen;
import ui.screens.SkillTreeScreen;
import ui.screens.CraftingScreen;
import ui.screens.MasteryScreen;
import ui.screens.InventoryHubScreen;
import ui.screens.ProfileScreen;
import ui.screens.CharacterViewerScreen;
import world.dungeon.DungeonArenaScreen;
import player.Wardrobe;
import util.KeyHandler;
import util.MouseHandler;
import currency.CurrencyManager;
import crafting.CraftingRecipe;
import crafting.CraftingService;
import save.SaveManager;
import save.SaveData;
import save.AutoSave;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// deals with switching screens
public class GamePanel extends JPanel implements Runnable {

    static final int SCREEN_WIDTH = 1600;
    static final int SCREEN_HEIGHT = 900;
    static final int FPS = 60;

    // input
    KeyHandler keyHandler = new KeyHandler();
    MouseHandler mouseHandler = new MouseHandler();

    // screens
    MenuScreen menuScreen;
    GameScreen gameScreen;
    HubScreen hubScreen = new HubScreen(this);
    PauseScreen pauseScreen = new PauseScreen();
    CustomizeScreen customizeScreen;
    SettingsScreen settingsScreen;
    HelpScreen helpScreen;
    GraphTestScreen graphTestScreen = new GraphTestScreen(this);
    ItemGalleryScreen itemGalleryScreen = new ItemGalleryScreen(this);
    DungeonArenaScreen dungeonArenaScreen;
    LoadoutScreen loadoutScreen;
    ShopScreen shopScreen;
    LootboxTestScreen lootboxTestScreen;
    RewardsScreen rewardsScreen;
    ArenaSelectionScreen arenaSelectionScreen;
    ModeSelectionScreen modeSelectionScreen;
    SkillTreeScreen skillTreeScreen;
    CraftingScreen craftingScreen;
    MasteryScreen masteryScreen;
    InventoryHubScreen inventoryHubScreen;
    ProfileScreen profileScreen;
    CharacterViewerScreen characterViewerScreen;

    // screen states
    boolean showMenu = true;
    boolean showGame = false;
    boolean showHub = false;
    boolean showPause = false;
    boolean showCustomize = false;
    boolean showSettings = false;
    boolean showHelp = false;
    boolean showGraphTest = false;
    boolean showItems = false;
    boolean showDungeonArena = false;
    boolean showLoadout = false;
    boolean showShop = false;
    boolean showLootboxTest = false;
    boolean showRewards = false;
    boolean showArenaSelection = false;
    boolean showModeSelection = false;
    boolean showSkillTree = false;
    boolean showCrafting = false;
    boolean showMastery = false;
    boolean showInventoryHub = false;
    boolean showProfile = false;
    boolean showCharacterViewer = false;

    // Track previous screen for navigation
    private String previousScreen = "menu";

    // Currency manager (persists across sessions for Cash and Gems)
    private CurrencyManager currencyManager;
    private Wardrobe wardrobe;
    private AutoSave autoSave;

    /** Session-only admin: big wallets / unlocks; cash & gem *earnings* still persist (see CurrencyManager bank). */
    private boolean sessionAdminMode;
    /** Mirror of {@link #sessionAdminMode} accessible to model classes that don't hold a GamePanel reference. */
    private static volatile boolean sessionAdminActive = false;
    public static boolean isAdminModeActive() { return sessionAdminActive; }
    private Set<String> adminArenaSnap = new HashSet<>();
    private String adminLastArenaSnap;
    private final List<RecipeUnlockNotification> recipeUnlockNotifications = new ArrayList<>();
    private static final long RECIPE_UNLOCK_NOTIFICATION_MS = 4300;
    private static final long RECIPE_UNLOCK_CHECK_INTERVAL_MS = 700;
    private long lastRecipeUnlockCheckMs;

    /** Scales logical 1600x900 content to fill the current panel size. */
    private double renderScale = 1.0;
    private int renderOffsetX = 0;
    private int renderOffsetY = 0;

    Thread gameThread;

    public GamePanel() {
        // Load saved data first
        SaveData saveData = SaveManager.load();
        currencyManager = new CurrencyManager(0, saveData.getCash(), saveData.getGems());
        wardrobe = new Wardrobe(saveData.getUnlockedClothingIds());
        if (wardrobe.getUnlockedClothingIds().isEmpty()) {
            wardrobe.unlockDefaults();
        }
        
        menuScreen = new MenuScreen(this);
        gameScreen = new GameScreen(this);
        dungeonArenaScreen = new DungeonArenaScreen(this);
        customizeScreen = new CustomizeScreen(this);
        settingsScreen = new SettingsScreen(this);
        helpScreen = new HelpScreen(this);
        // Clear save cache to ensure defaults are loaded fresh
        SaveManager.clearCache();
        loadoutScreen = new LoadoutScreen(this);
        shopScreen = new ShopScreen(this);
        lootboxTestScreen = new LootboxTestScreen(this);
        rewardsScreen = new RewardsScreen(this);
        arenaSelectionScreen = new ArenaSelectionScreen(this);
        modeSelectionScreen = new ModeSelectionScreen(this);
        skillTreeScreen = new SkillTreeScreen(this);
        craftingScreen = new CraftingScreen(this);
        masteryScreen = new MasteryScreen(this);
        inventoryHubScreen = new InventoryHubScreen(this);
        profileScreen = new ProfileScreen(this);
        characterViewerScreen = new CharacterViewerScreen(this);
        
        SaveManager.setPreSaveMutator(this::applySessionAdminPreSave);

        // Start auto-save
        autoSave = new AutoSave(this);
        autoSave.start();
        
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        addKeyListener(keyHandler);
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addMouseWheelListener(mouseHandler);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Snapshot active screen to avoid routing this same click into a newly switched screen.
                boolean menuActive = showMenu;
                boolean hubActive = showHub;
                boolean customizeActive = showCustomize;
                boolean settingsActive = showSettings;
                boolean helpActive = showHelp;
                boolean graphTestActive = showGraphTest;
                boolean itemsActive = showItems;
                boolean loadoutActive = showLoadout;
                boolean shopActive = showShop;
                boolean lootboxActive = showLootboxTest;
                boolean rewardsActive = showRewards;
                boolean arenaSelectionActive = showArenaSelection;
                boolean modeSelectionActive = showModeSelection;
                boolean skillTreeActive = showSkillTree;
                boolean craftingActive = showCrafting;
                boolean masteryActive = showMastery;
                boolean inventoryActive = showInventoryHub;
                boolean profileActive = showProfile;
                boolean characterViewerActive = showCharacterViewer;
                boolean gameActive = showGame;
                boolean dungeonActive = showDungeonArena;

                int mx = toLogicalX(e.getX());
                int my = toLogicalY(e.getY());

                if (menuActive) menuScreen.handleClick(mx, my);
                if (hubActive) {
                    hubScreen.handleHubDevClick(mx, my, SCREEN_WIDTH, SCREEN_HEIGHT);
                    hubScreen.handlePauseMenuClick(mx, my, SCREEN_WIDTH, SCREEN_HEIGHT);
                }
                // Note: DO NOT call resetMouseClicks here - it prevents shooting!
                // resetMouseClicks is already called when switching screens
                if (customizeActive) customizeScreen.handleClick(mx, my);
                if (settingsActive) settingsScreen.handleClick(mx, my);
                if (helpActive) helpScreen.handleClick(mx, my);
                if (graphTestActive) graphTestScreen.handleClick(mx, my);
                if (itemsActive) itemGalleryScreen.handleClick(mx, my);
                if (loadoutActive) loadoutScreen.handleClick(mx, my);
                if (shopActive) shopScreen.handleClick(mx, my);
                if (lootboxActive) lootboxTestScreen.handleClick(mx, my);
                if (rewardsActive) rewardsScreen.handleClick(mx, my);
                if (arenaSelectionActive) arenaSelectionScreen.handleClick(mx, my);
                if (modeSelectionActive) modeSelectionScreen.handleClick(mx, my);
                if (skillTreeActive) skillTreeScreen.handleClick(mx, my);
                if (craftingActive) craftingScreen.handleClick(mx, my);
                if (masteryActive) masteryScreen.handleClick(mx, my);
                if (inventoryActive) inventoryHubScreen.handleClick(mx, my);
                if (profileActive) profileScreen.handleClick(mx, my);
                if (characterViewerActive) characterViewerScreen.handleClick(mx, my);
                if (gameActive) {
                    requestFocusInWindow();
                    if (gameScreen.handleOverlayClick(mouseHandler, mx, my, SCREEN_WIDTH, SCREEN_HEIGHT)) {
                        return;
                    }
                    gameScreen.handlePauseMenuClick(mx, my, SCREEN_WIDTH, SCREEN_HEIGHT);
                }
                if (dungeonActive) dungeonArenaScreen.handlePauseMenuClick(mx, my, SCREEN_WIDTH, SCREEN_HEIGHT);
            }
        });

        addMouseWheelListener(e -> {
            if (showItems) itemGalleryScreen.handleMouseScroll(e.getWheelRotation());
            if (showShop) shopScreen.handleMouseScroll(e.getWheelRotation());
            if (showCrafting) craftingScreen.handleMouseScroll(e.getWheelRotation());
        });
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int mx = toLogicalX(e.getX());
                int my = toLogicalY(e.getY());
                if (showRewards) rewardsScreen.handleMouseMove(mx, my);
                if (showMenu) menuScreen.handleMouseMove(mx, my);
                if (showArenaSelection) arenaSelectionScreen.handleMouseMove(mx, my);
                if (showModeSelection) modeSelectionScreen.handleMouseMove(mx, my);
                if (showShop) shopScreen.handleMouseMove(mx, my);
                if (showSkillTree) skillTreeScreen.handleMouseMove(mx, my);
                if (showCrafting) craftingScreen.handleMouseMove(mx, my);
                if (showMastery) masteryScreen.handleMouseMove(mx, my);
                if (showInventoryHub) inventoryHubScreen.handleMouseMove(mx, my);
                if (showProfile) profileScreen.handleMouseMove(mx, my);
                if (showCharacterViewer) characterViewerScreen.handleMouseMove(mx, my);
                if (showSettings) settingsScreen.handleMouseMove(mx, my);
                if (showHelp) helpScreen.handleMouseMove(mx, my);
            }
        });
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void switchScreen(String screen) {
        // Set previous screen before switching
        previousScreen = getCurrentScreen();
        
        // Remember which screen we're coming from before clearing
        boolean wasInGame = showGame;
        boolean wasInDungeon = showDungeonArena;
        
        showMenu = false;
        showGame = false;
        showHub = false;
        showPause = false;
        showCustomize = false;
        showSettings = false;
        showHelp = false;
        showGraphTest = false;
        showItems = false;
        showDungeonArena = false;
        showLoadout = false;
        showShop = false;
        showLootboxTest = false;
        showRewards = false;
        showArenaSelection = false;
        showModeSelection = false;
        showSkillTree = false;
        showCrafting = false;
        showMastery = false;
        showInventoryHub = false;
        showProfile = false;
        showCharacterViewer = false;

        switch (screen) {
            case "menu"      -> {
                // Sync stats from current session before showing menu
                if (wasInGame) {
                    // Coming from arena - sync arena stats
                    gameScreen.syncRunStatsOnExit();
                } else if (wasInDungeon) {
                    // Coming from dungeon - sync dungeon stats  
                    save.SaveManager.syncPlayerStats(
                        dungeonArenaScreen.getPlayer().getStats(),
                        dungeonArenaScreen.getCurrentLevel(),
                        1
                    );
                }
                showMenu = true;
            }
            case "game"      -> {
                showGame = true;
                // Reset game state when starting arena
                gameScreen.resetGame();
                requestFocusInWindow();
                // Apply loadout when starting game
                gameScreen.getPlayer().applyLoadout(
                    loadoutScreen.getSelectedWeapon(),
                    loadoutScreen.getSelectedWeaponTier(),
                    loadoutScreen.getSelectedCharm(),
                    loadoutScreen.getSelectedCharmTier(),
                    loadoutScreen.getSelectedPower(),
                    loadoutScreen.getSelectedPowerTier(),
                    loadoutScreen.getSelectedSummon(),
                    loadoutScreen.getSelectedSummonTier(),
                    "None",  // consumables removed from loadout
                    1        // consumables removed from loadout
                );
                // Reset mouse clicks to prevent auto-shoot
                gameScreen.resetMouseClicks(mouseHandler);
            }
            case "hub"       -> {
                showHub = true;
                hubScreen.getPlayer().refreshCharacterAppearance();
                // Apply loadout when entering hub
                hubScreen.getPlayer().applyLoadout(
                    loadoutScreen.getSelectedWeapon(),
                    loadoutScreen.getSelectedWeaponTier(),
                    loadoutScreen.getSelectedCharm(),
                    loadoutScreen.getSelectedCharmTier(),
                    loadoutScreen.getSelectedPower(),
                    loadoutScreen.getSelectedPowerTier(),
                    loadoutScreen.getSelectedSummon(),
                    loadoutScreen.getSelectedSummonTier(),
                    "None",  // consumables removed from loadout
                    1        // consumables removed from loadout
                );
                // Reset mouse clicks to prevent auto-shoot
                hubScreen.resetMouseClicks(mouseHandler);
            }
            case "pause"     -> showPause = true;
            case "customize" -> showCustomize = true;
            case "settings"  -> showSettings = true;
            case "help"      -> showHelp = true;
            case "graphtest" -> showGraphTest = true;
            case "items"     -> showItems = true;
            case "dungeon"   -> {
                dungeonArenaScreen.getPlayer().refreshCharacterAppearance();
                showDungeonArena = true;
            }
            case "loadout"   -> showLoadout = true;
            case "shop"      -> showShop = true;
            case "lootboxtest" -> showLootboxTest = true;
            case "rewards"   -> showRewards = true;
            case "arenaselection" -> {
                arenaSelectionScreen.refreshProgressionFromSave();
                showArenaSelection = true;
            }
            case "modeselection" -> showModeSelection = true;
            case "skilltree" -> showSkillTree = true;
            case "crafting" -> {
                craftingScreen.refreshFromSave();
                showCrafting = true;
            }
            case "mastery" -> {
                masteryScreen.setReturnScreen(previousScreen);
                masteryScreen.refreshWeaponList();
                showMastery = true;
            }
            case "inventory" -> showInventoryHub = true;
            case "profile" -> showProfile = true;
            case "character" -> {
                characterViewerScreen.refresh();
                showCharacterViewer = true;
            }
        }
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        if (showGame) gameScreen.update(keyHandler, mouseHandler, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showHub) hubScreen.update(keyHandler, mouseHandler, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showDungeonArena) dungeonArenaScreen.update(keyHandler, mouseHandler, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showCrafting) craftingScreen.update(mouseHandler);
        if (showCharacterViewer) characterViewerScreen.update();
        updateCraftingRecipeUnlocks();
        
        // Customize screen input handling
        if (showCustomize) {
            customizeScreen.handleMouseMove(mouseHandler.mouseX, mouseHandler.mouseY);
            if (keyHandler.ePressed) {
                customizeScreen.handleKeyPress(java.awt.event.KeyEvent.VK_E);
                keyHandler.ePressed = false;
            }
            if (keyHandler.uPressed) {
                customizeScreen.handleKeyPress(java.awt.event.KeyEvent.VK_U);
                keyHandler.uPressed = false;
            }
        }
        
        if (showLoadout) {
            if (keyHandler.uPressed) {
                loadoutScreen.toggleDebugPanel();
                keyHandler.uPressed = false;
            }
        }
        if (showGraphTest) {
            if (keyHandler.spacePressed) {
                graphTestScreen.regenerateLevel();
                keyHandler.spacePressed = false;
            }
            if (keyHandler.onePressed) {
                graphTestScreen.setLevel(1);
                keyHandler.onePressed = false;
            }
            if (keyHandler.twoPressed) {
                graphTestScreen.setLevel(2);
                keyHandler.twoPressed = false;
            }
            if (keyHandler.threePressed) {
                graphTestScreen.setLevel(3);
                keyHandler.threePressed = false;
            }
            if (keyHandler.fourPressed) {
                graphTestScreen.setLevel(4);
                keyHandler.fourPressed = false;
            }
            if (keyHandler.fivePressed) {
                graphTestScreen.setLevel(5);
                keyHandler.fivePressed = false;
            }
        }
    }

    private void updateRenderTransform() {
        int panelW = Math.max(1, getWidth());
        int panelH = Math.max(1, getHeight());
        renderScale = Math.min(panelW / (double) SCREEN_WIDTH, panelH / (double) SCREEN_HEIGHT);
        int drawW = (int) Math.round(SCREEN_WIDTH * renderScale);
        int drawH = (int) Math.round(SCREEN_HEIGHT * renderScale);
        renderOffsetX = (panelW - drawW) / 2;
        renderOffsetY = (panelH - drawH) / 2;
        mouseHandler.setDisplayTransform(renderScale, renderScale, renderOffsetX, renderOffsetY);
    }

    private int toLogicalX(int panelX) {
        updateRenderTransform();
        return (int) ((panelX - renderOffsetX) / renderScale);
    }

    private int toLogicalY(int panelY) {
        updateRenderTransform();
        return (int) ((panelY - renderOffsetY) / renderScale);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateRenderTransform();

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.translate(renderOffsetX, renderOffsetY);
        g2.scale(renderScale, renderScale);

        if (showMenu)         menuScreen.draw(g2);
        if (showGame)         gameScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showHub)          hubScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showPause)        pauseScreen.draw(g2);
        if (showCustomize)    customizeScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showSettings)     settingsScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showHelp)         helpScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showGraphTest)    graphTestScreen.draw(g2);
        if (showItems)        itemGalleryScreen.draw(g2);
        if (showDungeonArena) dungeonArenaScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showLoadout)      loadoutScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showShop)         shopScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showLootboxTest)  lootboxTestScreen.draw(g2);
        if (showRewards)      rewardsScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showArenaSelection) arenaSelectionScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showModeSelection) modeSelectionScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showSkillTree)    skillTreeScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showCrafting)     craftingScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showMastery)      masteryScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showInventoryHub) inventoryHubScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showProfile)      profileScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showCharacterViewer) characterViewerScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        drawRecipeUnlockNotifications(g2);
    }

    // ========== GETTERS ==========
    public LoadoutScreen getLoadoutScreen() {
        return loadoutScreen;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public KeyHandler getKeyHandler() {
        return keyHandler;
    }

    public MouseHandler getMouseHandler() {
        return mouseHandler;
    }

    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    public Wardrobe getWardrobe() { return wardrobe; }

    public boolean isSessionAdminMode() {
        return sessionAdminMode;
    }

    public void toggleSessionAdminMode() {
        if (sessionAdminMode) {
            disableSessionAdminMode();
        } else {
            enableSessionAdminMode();
        }
    }

    private void enableSessionAdminMode() {
        final int cap = 999_999;
        SaveData d = SaveManager.load();
        adminArenaSnap = new HashSet<>(d.getUnlockedArenas());
        adminLastArenaSnap = d.getLastCompletedArena();
        d.activateSessionAdminSkillStipend(cap);
        d.activateSessionAdminLootboxStipends(cap);
        currencyManager.beginSessionAdminCurrencies(cap, cap);
        grantSessionAdminGoldToPlayers(cap);
        sessionAdminMode = true;
        sessionAdminActive = true;
        refreshEquippedPowerMoveUnlocks();
    }

    private void disableSessionAdminMode() {
        SaveData d = SaveManager.load();
        d.clearSessionSkillStipend();
        d.clearSessionLootboxStipends();
        currencyManager.endSessionAdminCurrencies();
        d.setUnlockedArenas(new HashSet<>(adminArenaSnap));
        d.setLastCompletedArena(adminLastArenaSnap);
        SaveManager.save(d);
        sessionAdminMode = false;
        sessionAdminActive = false;
        refreshEquippedPowerMoveUnlocks();
    }

    private void refreshEquippedPowerMoveUnlocks() {
        if (gameScreen != null && gameScreen.getPlayer() != null) {
            combat.powers.Power equipped = gameScreen.getPlayer().getInventory().getPower();
            if (equipped != null) {
                equipped.applyMasteryMoveUnlocks();
            }
        }
        if (hubScreen != null && hubScreen.getPlayer() != null) {
            combat.powers.Power equipped = hubScreen.getPlayer().getInventory().getPower();
            if (equipped != null) {
                equipped.applyMasteryMoveUnlocks();
            }
        }
    }

    private void grantSessionAdminGoldToPlayers(int amount) {
        gameScreen.getPlayer().getCurrencyManager().getGold().setAmount(amount);
        hubScreen.getPlayer().getCurrencyManager().getGold().setAmount(amount);
    }

    private void applySessionAdminPreSave(SaveData data) {
        if (!sessionAdminMode) {
            return;
        }
        data.setCash(currencyManager.getPersistableCash());
        data.setGems(currencyManager.getPersistableGems());
    }

    /** True when the window currently covers the whole monitor. */
    public boolean isFullScreenDisplay() {
        java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
        if (window == null) {
            return true;
        }
        java.awt.Rectangle screen = window.getGraphicsConfiguration().getBounds();
        return window.getWidth() >= screen.width && window.getHeight() >= screen.height;
    }

    /** Switches between borderless fullscreen and a centered {@value #SCREEN_WIDTH}x{@value #SCREEN_HEIGHT} window. */
    public void setFullScreenDisplay(boolean fullScreen) {
        java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
        if (window == null) {
            return;
        }
        java.awt.Rectangle screen = window.getGraphicsConfiguration().getBounds();
        if (fullScreen) {
            window.setBounds(screen);
        } else {
            int w = Math.min(SCREEN_WIDTH, screen.width);
            int h = Math.min(SCREEN_HEIGHT, screen.height);
            window.setBounds(screen.x + (screen.width - w) / 2, screen.y + (screen.height - h) / 2, w, h);
        }
        window.validate();
        requestFocusInWindow();
    }

    /**
     * Wipes save and restarts the app (same as main menu RESET).
     */
    public void resetSaveAndRelaunch() {
        SaveManager.resetSave();
        javax.swing.SwingUtilities.invokeLater(() -> {
            java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
            if (window instanceof javax.swing.JFrame) {
                ((javax.swing.JFrame) window).dispose();
            }
            try {
                Class<?> mainClass = Class.forName("Main");
                mainClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void resetCurrencyManager() {
        SaveData data = SaveManager.load();
        this.currencyManager = new CurrencyManager(0, data.getCash(), data.getGems());
    }

    /**
     * @deprecated Use {@link #toggleSessionAdminMode()} — session admin does not write inflated values to disk.
     */
    @Deprecated
    public void applyAdminSave() {
        toggleSessionAdminMode();
    }

    private String getCurrentScreen() {
        if (showMenu) return "menu";
        if (showGame) return "game";
        if (showHub) return "hub";
        if (showPause) return "pause";
        if (showCustomize) return "customize";
        if (showSettings) return "settings";
        if (showHelp) return "help";
        if (showGraphTest) return "graphtest";
        if (showItems) return "items";
        if (showDungeonArena) return "dungeon";
        if (showLoadout) return "loadout";
        if (showShop) return "shop";
        if (showLootboxTest) return "lootboxtest";
        if (showRewards) return "rewards";
        if (showArenaSelection) return "arenaselection";
        if (showModeSelection) return "modeselection";
        if (showSkillTree) return "skilltree";
        if (showCrafting) return "crafting";
        if (showMastery) return "mastery";
        if (showInventoryHub) return "inventory";
        if (showProfile) return "profile";
        if (showCharacterViewer) return "character";
        return "menu"; // default
    }

    public String getPreviousScreen() {
        return previousScreen;
    }
    
    public int getMouseX() {
        return mouseHandler.mouseX;
    }
    
    public int getMouseY() {
        return mouseHandler.mouseY;
    }

    public void syncLoadoutFromSave() {
        loadoutScreen.refreshFromSaveData();
    }

    private void updateCraftingRecipeUnlocks() {
        long now = System.currentTimeMillis();
        if (now - lastRecipeUnlockCheckMs < RECIPE_UNLOCK_CHECK_INTERVAL_MS) {
            pruneExpiredRecipeNotifications(now);
            return;
        }
        lastRecipeUnlockCheckMs = now;

        SaveData data = SaveManager.load();
        Set<String> known = new HashSet<>(data.getDiscoveredCraftingRecipeIds());
        List<CraftingRecipe> newlyCraftable = CraftingService.findNewlyCraftableRecipes(data, known);
        if (!newlyCraftable.isEmpty()) {
            for (CraftingRecipe recipe : newlyCraftable) {
                recipeUnlockNotifications.add(new RecipeUnlockNotification(
                        "New recipe unlocked: " + recipe.getDisplayName(),
                        now));
                known.add(recipe.getId());
            }
            data.setDiscoveredCraftingRecipeIds(known);
            SaveManager.save(data);
        }
        pruneExpiredRecipeNotifications(now);
    }

    private void pruneExpiredRecipeNotifications(long now) {
        recipeUnlockNotifications.removeIf(n -> now - n.createdAtMs > RECIPE_UNLOCK_NOTIFICATION_MS);
    }

    private void drawRecipeUnlockNotifications(Graphics2D g) {
        if (recipeUnlockNotifications.isEmpty()) {
            return;
        }

        int maxToShow = 5;
        int start = Math.max(0, recipeUnlockNotifications.size() - maxToShow);
        int y = 20;
        int boxW = 450;
        int boxH = 40;
        int gap = 8;
        for (int i = start; i < recipeUnlockNotifications.size(); i++) {
            RecipeUnlockNotification n = recipeUnlockNotifications.get(i);
            int x = SCREEN_WIDTH - boxW - 18;
            g.setColor(new Color(20, 30, 45, 220));
            g.fillRoundRect(x, y, boxW, boxH, 10, 10);
            g.setColor(new Color(120, 205, 255));
            g.setStroke(new java.awt.BasicStroke(2f));
            g.drawRoundRect(x, y, boxW, boxH, 10, 10);
            g.setStroke(new java.awt.BasicStroke(1f));
            g.setColor(Color.WHITE);
            g.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
            String message = n.message;
            java.awt.FontMetrics fm = g.getFontMetrics();
            if (fm.stringWidth(message) > boxW - 24) {
                while (message.length() > 4 && fm.stringWidth(message + "...") > boxW - 24) {
                    message = message.substring(0, message.length() - 1);
                }
                message = message + "...";
            }
            g.drawString(message, x + 12, y + 25);
            y += boxH + gap;
        }
    }

    private static class RecipeUnlockNotification {
        private final String message;
        private final long createdAtMs;

        private RecipeUnlockNotification(String message, long createdAtMs) {
            this.message = message;
            this.createdAtMs = createdAtMs;
        }
    }
}
