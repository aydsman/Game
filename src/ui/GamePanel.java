package ui;

import ui.screens.MenuScreen;
import ui.screens.GameScreen;
import ui.screens.PauseScreen;
import ui.screens.SettingsScreen;
import ui.screens.HelpScreen;
import util.KeyHandler;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

// deals with switching screens
public class GamePanel extends JPanel implements Runnable {

    static final int SCREEN_WIDTH = 800;
    static final int SCREEN_HEIGHT = 600;
    static final int FPS = 60;

    // input
    KeyHandler keyHandler = new KeyHandler();

    // screens
    MenuScreen menuScreen = new MenuScreen();
    GameScreen gameScreen = new GameScreen();
    PauseScreen pauseScreen = new PauseScreen();
    SettingsScreen settingsScreen = new SettingsScreen();
    HelpScreen helpScreen = new HelpScreen();

    // screen states
    boolean showMenu = false;
    boolean showGame = true;
    boolean showPause = false;
    boolean showSettings = false;
    boolean showHelp = false;

    Thread gameThread;

    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        addKeyListener(keyHandler);
        setFocusable(true);
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void switchScreen(String screen) {
        showMenu = false;
        showGame = false;
        showPause = false;
        showSettings = false;
        showHelp = false;

        switch (screen) {
            case "menu"     -> showMenu = true;
            case "game"     -> showGame = true;
            case "pause"    -> showPause = true;
            case "settings" -> showSettings = true;
            case "help"     -> showHelp = true;
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
        if (showGame) gameScreen.update(keyHandler, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (showMenu)     menuScreen.draw(g2);
        if (showGame)     gameScreen.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (showPause)    pauseScreen.draw(g2);
        if (showSettings) settingsScreen.draw(g2);
        if (showHelp)     helpScreen.draw(g2);
    }
}
