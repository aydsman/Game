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
import world.dungeon.DungeonArenaScreen;
import util.KeyHandler;
import util.MouseHandler;
import currency.CurrencyManager;
import save.SaveManager;
import save.SaveData;
import save.AutoSave;
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

    // Currency manager (persists across sessions for Cash and Gems)
    private CurrencyManager currencyManager;
    private AutoSave autoSave;

    Thread gameThread;

    public GamePanel() {
        // Load saved data first
        SaveData saveData = SaveManager.load();
        currencyManager = new CurrencyManager(0, saveData.getCash(), saveData.getGems());
        
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
                if (showMenu) menuScreen.handleClick(e.getX(), e.getY());
                if (showHub) hubScreen.handlePauseMenuClick(e.getX(), e.getY(), SCREEN_WIDTH, SCREEN_HEIGHT);
                // Note: DO NOT call resetMouseClicks here - it prevents shooting!
                // resetMouseClicks is already called when switching screens
                if (showCustomize) customizeScreen.handleClick(e.getX(), e.getY());
                if (showSettings) settingsScreen.handleClick(e.getX(), e.getY());
                if (showHelp) helpScreen.handleClick(e.getX(), e.getY());
                if (showGraphTest) graphTestScreen.handleClick(e.getX(), e.getY());
                if (showItems) itemGalleryScreen.handleClick(e.getX(), e.getY());
                if (showLoadout) loadoutScreen.handleClick(e.getX(), e.getY());
                if (showShop) shopScreen.handleClick(e.getX(), e.getY());
                if (showLootboxTest) lootboxTestScreen.handleClick(e.getX(), e.getY());
                if (showGame) gameScreen.handlePauseMenuClick(e.getX(), e.getY(), SCREEN_WIDTH, SCREEN_HEIGHT);
                if (showDungeonArena) dungeonArenaScreen.handlePauseMenuClick(e.getX(), e.getY(), SCREEN_WIDTH, SCREEN_HEIGHT);
            }
        });

        addMouseWheelListener(e -> {
            if (showItems) itemGalleryScreen.handleMouseScroll(e.getWheelRotation());
        });
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void switchScreen(String screen) {
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
    }

    // ========== GETTERS ==========
    public LoadoutScreen getLoadoutScreen() {
        return loadoutScreen;
    }

    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    public void resetCurrencyManager() {
        SaveData data = SaveManager.load();
        this.currencyManager = new CurrencyManager(0, data.getCash(), data.getGems());
    }
}
