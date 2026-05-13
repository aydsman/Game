package ui.screens;

import ui.GamePanel;
import save.SaveManager;
import save.SaveData;
import save.ArenaProgressionManager;
import java.awt.*;

/**
 * Arena selection screen for choosing which arena to play.
 */
public class ArenaSelectionScreen {
    
    private GamePanel gamePanel;
    private ArenaProgressionManager progressionManager;
    
    // Arena data
    private static final String[] ARENA_NAMES = {"PlainsI", "PlainsII", "PlainsIII", "PlainsIV", "PlainsV"};
    private static final String[] ARENA_DISPLAY_NAMES = {"Plains I", "Plains II", "Plains III", "Plains IV", "Plains V"};
    private static final String[] ARENA_DIFFICULTIES = {"Easy", "Medium", "Hard", "Very Hard", "Extreme"};
    
    // UI rectangles
    private Rectangle[] arenaRects;
    private Rectangle debugBtn;
    private Rectangle backBtn;
    private int hoveredIndex = -1;
    private boolean debugBtnHovered = false;
    
    public ArenaSelectionScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        SaveData saveData = SaveManager.load();
        this.progressionManager = new ArenaProgressionManager();
        this.progressionManager.setUnlockedArenas(saveData.getUnlockedArenas());
        this.progressionManager.setLastCompletedArena(saveData.getLastCompletedArena());
        initializeRectangles();
    }

    /** Call when showing this screen so unlocks match the latest save (e.g. after clearing an arena). */
    public void refreshProgressionFromSave() {
        SaveData saveData = SaveManager.load();
        progressionManager.setUnlockedArenas(saveData.getUnlockedArenas());
        progressionManager.setLastCompletedArena(saveData.getLastCompletedArena());
    }
    
    private void initializeRectangles() {
        arenaRects = new Rectangle[ARENA_NAMES.length];
        int startX = 300;
        int startY = 200;
        int cardWidth = 280;
        int cardHeight = 200;
        int spacing = 40;
        
        for (int i = 0; i < ARENA_NAMES.length; i++) {
            int row = i / 3;
            int col = i % 3;
            int x = startX + (col * (cardWidth + spacing));
            int y = startY + (row * (cardHeight + spacing + 60));
            arenaRects[i] = new Rectangle(x, y, cardWidth, cardHeight);
        }
        
        debugBtn = new Rectangle(1450, 850, 140, 40);
        backBtn = new Rectangle(20, 20, 120, 40);
    }
    
    public void handleClick(int x, int y) {
        if (backBtn.contains(x, y)) {
            gamePanel.switchScreen("menu");
            return;
        }
        
        if (debugBtn.contains(x, y)) {
            progressionManager.unlockAll();
            saveProgress();
            return;
        }
        
        for (int i = 0; i < arenaRects.length; i++) {
            if (arenaRects[i].contains(x, y) && isArenaUnlocked(i)) {
                startArena(ARENA_NAMES[i]);
                return;
            }
        }
    }
    
    public void handleMouseMove(int x, int y) {
        hoveredIndex = -1;
        debugBtnHovered = debugBtn.contains(x, y);
        for (int i = 0; i < arenaRects.length; i++) {
            if (arenaRects[i].contains(x, y) && isArenaUnlocked(i)) {
                hoveredIndex = i;
                break;
            }
        }
    }
    
    private void startArena(String arenaName) {
        gamePanel.getGameScreen().setArena(arenaName);
        gamePanel.switchScreen("game");
    }

    private boolean isArenaUnlocked(int index) {
        return progressionManager.isUnlocked(ARENA_NAMES[index])
                || (gamePanel != null && gamePanel.isSessionAdminMode());
    }
    
    private void saveProgress() {
        SaveData saveData = SaveManager.load();
        saveData.setUnlockedArenas(progressionManager.getUnlockedArenas());
        saveData.setLastCompletedArena(progressionManager.getLastCompletedArena());
        SaveManager.save(saveData);
    }
    
    public void draw(Graphics2D g, int screenWidth, int screenHeight) {
        GradientPaint bgGradient = new GradientPaint(0, 0, new Color(30, 30, 40), 0, screenHeight, new Color(20, 20, 30));
        g.setPaint(bgGradient);
        g.fillRect(0, 0, screenWidth, screenHeight);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String title = "SELECT ARENA";
        int titleX = (screenWidth - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX, 80);
        
        for (int i = 0; i < ARENA_NAMES.length; i++) {
            drawArenaCard(g, i);
        }
        
        drawButton(g, backBtn, "BACK", new Color(100, 100, 120, 200), false);
        drawButton(g, debugBtn, "DEBUG", new Color(255, 100, 100, 200), debugBtnHovered);
    }
    
    private void drawArenaCard(Graphics2D g, int index) {
        Rectangle rect = arenaRects[index];
        String arenaName = ARENA_NAMES[index];
        String displayName = ARENA_DISPLAY_NAMES[index];
        String difficulty = ARENA_DIFFICULTIES[index];
        boolean isUnlocked = isArenaUnlocked(index);
        boolean isHovered = index == hoveredIndex;
        
        if (isUnlocked) {
            g.setColor(isHovered ? new Color(100, 150, 200, 220) : new Color(60, 80, 120, 200));
        } else {
            g.setColor(new Color(40, 40, 50, 200));
        }
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 16, 16);
        
        g.setColor(isUnlocked ? (isHovered ? new Color(150, 200, 255) : new Color(100, 150, 200)) : new Color(80, 80, 90));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 16, 16);
        g.setStroke(new BasicStroke(1));
        
        g.setFont(new Font("Segoe UI", Font.BOLD, 24));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        int nameX = rect.x + (rect.width - fm.stringWidth(displayName)) / 2;
        int nameY = rect.y + 50;
        g.drawString(displayName, nameX, nameY);
        
        g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g.setColor(new Color(200, 200, 220));
        String diffStr = "Difficulty: " + difficulty;
        int diffX = rect.x + (rect.width - g.getFontMetrics().stringWidth(diffStr)) / 2;
        int diffY = nameY + 35;
        g.drawString(diffStr, diffX, diffY);
        
        if (!isUnlocked) {
            g.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g.setColor(new Color(255, 100, 100));
            String lockedStr = "LOCKED";
            int lockedX = rect.x + (rect.width - g.getFontMetrics().stringWidth(lockedStr)) / 2;
            int lockedY = diffY + 35;
            g.drawString(lockedStr, lockedX, lockedY);
        } else {
            g.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g.setColor(new Color(100, 255, 100));
            String readyStr = "CLICK TO START";
            int readyX = rect.x + (rect.width - g.getFontMetrics().stringWidth(readyStr)) / 2;
            int readyY = diffY + 40;
            g.drawString(readyStr, readyX, readyY);
        }
    }
    
    private void drawButton(Graphics2D g, Rectangle btn, String label, Color bgColor, boolean isHovered) {
        g.setColor(bgColor);
        g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        
        g.setColor(isHovered ? new Color(200, 150, 150) : new Color(150, 100, 100));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 8, 8);
        g.setStroke(new BasicStroke(1));
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        int textX = btn.x + (btn.width - fm.stringWidth(label)) / 2;
        int textY = btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, textX, textY);
    }
}




