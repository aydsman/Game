package ui;

import ui.screens.MenuScreen;
import ui.screens.GameScreen;
import ui.screens.PauseScreen;
import ui.screens.SettingsScreen;
import ui.screens.CustomizeScreen;
import ui.screens.HelpScreen;
import ui.screens.GraphTestScreen;
import ui.screens.ItemGalleryScreen;
import world.dungeon.DungeonArenaScreen;
import util.KeyHandler;
import util.MouseHandler;
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
    GameScreen gameScreen = new GameScreen();
    PauseScreen pauseScreen = new PauseScreen();
    CustomizeScreen customizeScreen;
    SettingsScreen settingsScreen;
    HelpScreen helpScreen;
    GraphTestScreen graphTestScreen = new GraphTestScreen(this);
    ItemGalleryScreen itemGalleryScreen = new ItemGalleryScreen(this);
    DungeonArenaScreen dungeonArenaScreen = new DungeonArenaScreen();

    // screen states
    boolean showMenu = true;
    boolean showGame = false;
    boolean showPause = false;
    boolean showCustomize = false;
    boolean showSettings = false;
    boolean showHelp = false;
    boolean showGraphTest = false;
    boolean showItems = false;
    boolean showDungeonArena = false;

    Thread gameThread;

    public GamePanel() {
        menuScreen = new MenuScreen(this);
        customizeScreen = new CustomizeScreen(this);
        settingsScreen = new SettingsScreen(this);
        helpScreen = new HelpScreen(this);
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
                if (showDungeonArena) dungeonArenaScreen.resetMouseClicks(mouseHandler);
                if (showCustomize) customizeScreen.handleClick(e.getX(), e.getY());
                if (showSettings) settingsScreen.handleClick(e.getX(), e.getY());
                if (showHelp) helpScreen.handleClick(e.getX(), e.getY());
                if (showGraphTest) graphTestScreen.handleClick(e.getX(), e.getY());
                if (showItems) itemGalleryScreen.handleClick(e.getX(), e.getY());
            }
        });

        addMouseWheelListener(e -> {
            if (showItems) itemGalleryScreen.handleMouseScroll(e.getWheelRotation());
        });
        setFocusable(true);
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void switchScreen(String screen) {
        showMenu = false;
        showGame = false;
        showPause = false;
        showCustomize = false;
        showSettings = false;
        showHelp = false;
        showGraphTest = false;
        showItems = false;
        showDungeonArena = false;

        switch (screen) {
            case "menu"      -> showMenu = true;
            case "game"      -> showGame = true;
            case "pause"     -> showPause = true;
            case "customize" -> showCustomize = true;
            case "settings"  -> showSettings = true;
            case "help"      -> showHelp = true;
            case "graphtest" -> showGraphTest = true;
            case "items"     -> showItems = true;
            case "dungeon"   -> showDungeonArena = true;
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
        if (showDungeonArena) dungeonArenaScreen.update(keyHandler, mouseHandler, SCREEN_WIDTH, SCREEN_HEIGHT);
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
        if (showPause)        pauseScreen.draw(g2);
        if (showCustomize)    customizeScreen.draw(g2);
        if (showSettings)     settingsScreen.draw(g2);
        if (showHelp)         helpScreen.draw(g2);
        if (showGraphTest)    graphTestScreen.draw(g2);
        if (showItems)        itemGalleryScreen.draw(g2);
        if (showDungeonArena) dungeonArenaScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
    }
}
