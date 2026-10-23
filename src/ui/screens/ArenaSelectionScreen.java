package ui.screens;

import ui.GamePanel;
import save.SaveManager;
import save.SaveData;
import save.ArenaProgressionManager;
import java.awt.*;

/**
 * Arena selection: pick a region, then choose a stage from a dimmed popup overlay.
 */
public class ArenaSelectionScreen {

    private GamePanel gamePanel;
    private ArenaProgressionManager progressionManager;

    private static final String[] ARENA_NAMES = {"PlainsI", "PlainsII", "PlainsIII", "PlainsIV", "PlainsV"};
    private static final String[] ARENA_DISPLAY_NAMES = {"Plains I", "Plains II", "Plains III", "Plains IV", "Plains V"};
    private static final String[] ARENA_DIFFICULTIES = {"Easy", "Medium", "Hard", "Very Hard", "Extreme"};

    private Rectangle plainsCard;
    private Rectangle debugBtn;
    private Rectangle backBtn;
    private Rectangle popupBounds;
    private Rectangle popupCloseBtn;
    private Rectangle[] stageRects;

    private boolean showPlainsPopup = false;
    private boolean plainsHovered = false;
    private boolean debugBtnHovered = false;
    private boolean closeHovered = false;
    private int hoveredStageIndex = -1;

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
        showPlainsPopup = false;
    }

    private void initializeRectangles() {
        plainsCard = new Rectangle(540, 250, 520, 280);
        debugBtn = new Rectangle(1450, 850, 140, 40);
        backBtn = new Rectangle(20, 20, 120, 40);

        int popupW = 720;
        int popupH = 620;
        int popupX = (1600 - popupW) / 2;
        int popupY = (900 - popupH) / 2;
        popupBounds = new Rectangle(popupX, popupY, popupW, popupH);
        popupCloseBtn = new Rectangle(popupX + popupW - 56, popupY + 16, 40, 40);

        stageRects = new Rectangle[ARENA_NAMES.length];
        int rowX = popupX + 36;
        int rowY = popupY + 110;
        int rowW = popupW - 72;
        int rowH = 82;
        int rowGap = 12;
        for (int i = 0; i < ARENA_NAMES.length; i++) {
            stageRects[i] = new Rectangle(rowX, rowY + i * (rowH + rowGap), rowW, rowH);
        }
    }

    public void handleClick(int x, int y) {
        if (showPlainsPopup) {
            if (popupCloseBtn.contains(x, y) || !popupBounds.contains(x, y)) {
                showPlainsPopup = false;
                return;
            }
            for (int i = 0; i < stageRects.length; i++) {
                if (stageRects[i].contains(x, y) && isArenaUnlocked(i)) {
                    startArena(ARENA_NAMES[i]);
                    return;
                }
            }
            return;
        }

        if (backBtn.contains(x, y)) {
            gamePanel.switchScreen("modeselection");
            return;
        }

        if (debugBtn.contains(x, y)) {
            progressionManager.unlockAll();
            saveProgress();
            return;
        }

        if (plainsCard.contains(x, y)) {
            showPlainsPopup = true;
        }
    }

    public void handleMouseMove(int x, int y) {
        plainsHovered = false;
        debugBtnHovered = false;
        closeHovered = false;
        hoveredStageIndex = -1;

        if (showPlainsPopup) {
            closeHovered = popupCloseBtn.contains(x, y);
            for (int i = 0; i < stageRects.length; i++) {
                if (stageRects[i].contains(x, y) && isArenaUnlocked(i)) {
                    hoveredStageIndex = i;
                    break;
                }
            }
            return;
        }

        debugBtnHovered = debugBtn.contains(x, y);
        plainsHovered = plainsCard.contains(x, y);
    }

    private void startArena(String arenaName) {
        showPlainsPopup = false;
        gamePanel.getGameScreen().setArena(arenaName);
        gamePanel.switchScreen("game");
    }

    private boolean isArenaUnlocked(int index) {
        return progressionManager.isUnlocked(ARENA_NAMES[index])
                || (gamePanel != null && gamePanel.isSessionAdminMode());
    }

    private int countUnlockedStages() {
        int count = 0;
        for (int i = 0; i < ARENA_NAMES.length; i++) {
            if (isArenaUnlocked(i)) {
                count++;
            }
        }
        return count;
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
        g.setPaint(null);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String title = "SELECT ARENA";
        g.drawString(title, (screenWidth - fm.stringWidth(title)) / 2, 80);

        g.setColor(new Color(170, 185, 210));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        String modeLine = "Mode: " + gamePanel.getGameScreen().getGameMode().getDisplayName();
        g.drawString(modeLine, (screenWidth - g.getFontMetrics().stringWidth(modeLine)) / 2, 112);

        drawPlainsCard(g);
        drawButton(g, backBtn, "BACK", new Color(100, 100, 120, 200), false);
        drawButton(g, debugBtn, "DEBUG", new Color(255, 100, 100, 200), debugBtnHovered);

        if (showPlainsPopup) {
            drawPlainsPopup(g, screenWidth, screenHeight);
        }
    }

    private void drawPlainsCard(Graphics2D g) {
        if (plainsHovered && !showPlainsPopup) {
            g.setColor(new Color(100, 150, 200, 220));
        } else {
            g.setColor(new Color(60, 80, 120, 200));
        }
        g.fillRoundRect(plainsCard.x, plainsCard.y, plainsCard.width, plainsCard.height, 16, 16);

        g.setColor(plainsHovered && !showPlainsPopup ? new Color(150, 200, 255) : new Color(100, 150, 200));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(plainsCard.x, plainsCard.y, plainsCard.width, plainsCard.height, 16, 16);
        g.setStroke(new BasicStroke(1));

        g.setFont(new Font("Segoe UI", Font.BOLD, 40));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String name = "Plains";
        g.drawString(name, plainsCard.x + (plainsCard.width - fm.stringWidth(name)) / 2, plainsCard.y + 90);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        g.setColor(new Color(200, 200, 220));
        String stages = "5 stages";
        fm = g.getFontMetrics();
        g.drawString(stages, plainsCard.x + (plainsCard.width - fm.stringWidth(stages)) / 2, plainsCard.y + 135);

        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g.setColor(new Color(150, 200, 160));
        String progress = countUnlockedStages() + " / " + ARENA_NAMES.length + " unlocked";
        fm = g.getFontMetrics();
        g.drawString(progress, plainsCard.x + (plainsCard.width - fm.stringWidth(progress)) / 2, plainsCard.y + 175);

        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g.setColor(new Color(100, 255, 100));
        String hint = "CLICK TO VIEW";
        fm = g.getFontMetrics();
        g.drawString(hint, plainsCard.x + (plainsCard.width - fm.stringWidth(hint)) / 2, plainsCard.y + 230);
    }

    private void drawPlainsPopup(Graphics2D g, int screenWidth, int screenHeight) {
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, screenWidth, screenHeight);

        g.setColor(new Color(18, 24, 36, 245));
        g.fillRoundRect(popupBounds.x, popupBounds.y, popupBounds.width, popupBounds.height, 18, 18);
        g.setColor(new Color(115, 165, 225));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(popupBounds.x, popupBounds.y, popupBounds.width, popupBounds.height, 18, 18);
        g.setStroke(new BasicStroke(1));

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 32));
        FontMetrics fm = g.getFontMetrics();
        String heading = "Plains";
        g.drawString(heading, popupBounds.x + (popupBounds.width - fm.stringWidth(heading)) / 2, popupBounds.y + 48);

        g.setColor(new Color(170, 185, 210));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        fm = g.getFontMetrics();
        String subtitle = "Choose a stage";
        g.drawString(subtitle, popupBounds.x + (popupBounds.width - fm.stringWidth(subtitle)) / 2, popupBounds.y + 78);

        g.setColor(closeHovered ? new Color(80, 40, 45, 230) : new Color(50, 40, 48, 220));
        g.fillRoundRect(popupCloseBtn.x, popupCloseBtn.y, popupCloseBtn.width, popupCloseBtn.height, 8, 8);
        g.setColor(new Color(220, 140, 140));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(popupCloseBtn.x, popupCloseBtn.y, popupCloseBtn.width, popupCloseBtn.height, 8, 8);
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        fm = g.getFontMetrics();
        g.drawString("X", popupCloseBtn.x + (popupCloseBtn.width - fm.stringWidth("X")) / 2,
                popupCloseBtn.y + (popupCloseBtn.height + fm.getAscent() - fm.getDescent()) / 2);

        for (int i = 0; i < ARENA_NAMES.length; i++) {
            drawStageRow(g, i);
        }
    }

    private void drawStageRow(Graphics2D g, int index) {
        Rectangle rect = stageRects[index];
        String displayName = ARENA_DISPLAY_NAMES[index];
        String difficulty = ARENA_DIFFICULTIES[index];
        boolean unlocked = isArenaUnlocked(index);
        boolean hovered = index == hoveredStageIndex;

        if (unlocked) {
            g.setColor(hovered ? new Color(70, 95, 140, 235) : new Color(42, 52, 74, 230));
        } else {
            g.setColor(new Color(32, 34, 42, 230));
        }
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);

        g.setColor(unlocked ? (hovered ? new Color(150, 200, 255) : new Color(90, 130, 180)) : new Color(70, 70, 80));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);
        g.setStroke(new BasicStroke(1));

        g.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g.setColor(Color.WHITE);
        g.drawString(displayName, rect.x + 24, rect.y + 34);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        g.setColor(new Color(190, 200, 220));
        g.drawString("Difficulty: " + difficulty, rect.x + 24, rect.y + 60);

        g.setFont(new Font("Segoe UI", Font.BOLD, 15));
        FontMetrics fm = g.getFontMetrics();
        if (!unlocked) {
            g.setColor(new Color(255, 100, 100));
            String locked = "LOCKED";
            g.drawString(locked, rect.x + rect.width - fm.stringWidth(locked) - 24, rect.y + 48);
        } else {
            g.setColor(new Color(100, 255, 100));
            String ready = "CLICK TO START";
            g.drawString(ready, rect.x + rect.width - fm.stringWidth(ready) - 24, rect.y + 48);
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
