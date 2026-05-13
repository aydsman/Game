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
import ui.screens.SkillTreeScreen;
import ui.screens.CraftingScreen;
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
    SkillTreeScreen skillTreeScreen;
    CraftingScreen craftingScreen;

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
    boolean showSkillTree = false;
    boolean showCrafting = false;

    // Track previous screen for navigation
    private String previousScreen = "menu";

    // Currency manager (persists across sessions for Cash and Gems)
    private CurrencyManager currencyManager;
    private Wardrobe wardrobe;
    private AutoSave autoSave;

    /** Session-only admin: big wallets / unlocks; cash & gem *earnings* still persist (see CurrencyManager bank). */
    private boolean sessionAdminMode;
    private Set<String> adminArenaSnap = new HashSet<>();
    private String adminLastArenaSnap;
    private final List<RecipeUnlockNotification> recipeUnlockNotifications = new ArrayList<>();
    private static final long RECIPE_UNLOCK_NOTIFICATION_MS = 4300;
    private static final long RECIPE_UNLOCK_CHECK_INTERVAL_MS = 700;
    private long lastRecipeUnlockCheckMs;

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
        skillTreeScreen = new SkillTreeScreen(this);
        craftingScreen = new CraftingScreen(this);
        
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
                boolean skillTreeActive = showSkillTree;
                boolean craftingActive = showCrafting;
                boolean gameActive = showGame;
                boolean dungeonActive = showDungeonArena;

                if (menuActive) menuScreen.handleClick(e.getX(), e.getY());
                if (hubActive) {
                    hubScreen.handleHubDevClick(e.getX(), e.getY(), SCREEN_WIDTH, SCREEN_HEIGHT);
                    hubScreen.handlePauseMenuClick(e.getX(), e.getY(), SCREEN_WIDTH, SCREEN_HEIGHT);
                }
                // Note: DO NOT call resetMouseClicks here - it prevents shooting!
                // resetMouseClicks is already called when switching screens
                if (customizeActive) customizeScreen.handleClick(e.getX(), e.getY());
                if (settingsActive) settingsScreen.handleClick(e.getX(), e.getY());
                if (helpActive) helpScreen.handleClick(e.getX(), e.getY());
                if (graphTestActive) graphTestScreen.handleClick(e.getX(), e.getY());
                if (itemsActive) itemGalleryScreen.handleClick(e.getX(), e.getY());
                if (loadoutActive) loadoutScreen.handleClick(e.getX(), e.getY());
                if (shopActive) shopScreen.handleClick(e.getX(), e.getY());
                if (lootboxActive) lootboxTestScreen.handleClick(e.getX(), e.getY());
                if (rewardsActive) rewardsScreen.handleClick(e.getX(), e.getY());
                if (arenaSelectionActive) arenaSelectionScreen.handleClick(e.getX(), e.getY());
                if (skillTreeActive) skillTreeScreen.handleClick(e.getX(), e.getY());
                if (craftingActive) craftingScreen.handleClick(e.getX(), e.getY());
                if (gameActive) {
                    requestFocusInWindow();
                    if (gameScreen.handleArenaCompletionClick(mouseHandler, e.getX(), e.getY(), SCREEN_WIDTH, SCREEN_HEIGHT)) {
                        return;
                    }
                    gameScreen.handlePauseMenuClick(e.getX(), e.getY(), SCREEN_WIDTH, SCREEN_HEIGHT);
                }
                if (dungeonActive) dungeonArenaScreen.handlePauseMenuClick(e.getX(), e.getY(), SCREEN_WIDTH, SCREEN_HEIGHT);
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
                if (showRewards) rewardsScreen.handleMouseMove(e.getX(), e.getY());
                if (showMenu) menuScreen.handleMouseMove(e.getX(), e.getY());
                if (showArenaSelection) arenaSelectionScreen.handleMouseMove(e.getX(), e.getY());
                if (showShop) shopScreen.handleMouseMove(e.getX(), e.getY());
                if (showSkillTree) skillTreeScreen.handleMouseMove(e.getX(), e.getY());
                if (showCrafting) craftingScreen.handleMouseMove(e.getX(), e.getY());
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
        showSkillTree = false;
        showCrafting = false;

        switch (screen) {
            case "menu"      -> {
                // Sync stats from current session before showing menu
                if (wasInGame) {
                    // Coming from arena - sync arena stats
                    save.SaveManager.syncPlayerStats(
                        gameScreen.getPlayer().getStats(),
                        gameScreen.getWaveManager().getCurrentWave(),
                        1
                    );
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
            case "dungeon"   -> showDungeonArena = true;
            case "loadout"   -> showLoadout = true;
            case "shop"      -> showShop = true;
            case "lootboxtest" -> showLootboxTest = true;
            case "rewards"   -> showRewards = true;
            case "arenaselection" -> {
                arenaSelectionScreen.refreshProgressionFromSave();
                showArenaSelection = true;
            }
            case "skilltree" -> showSkillTree = true;
            case "crafting" -> {
                craftingScreen.refreshFromSave();
                showCrafting = true;
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

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
        if (showSkillTree)    skillTreeScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showCrafting)     craftingScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
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
        if (showSkillTree) return "skilltree";
        if (showCrafting) return "crafting";
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
