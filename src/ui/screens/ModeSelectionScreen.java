package ui.screens;

import ui.GamePanel;
import world.arena.GameMode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Picks the run rules (Standard, Endless, Boss Rush, Time Attack) before choosing an arena.
 */
public class ModeSelectionScreen {

    private static final GameMode[] MODES = GameMode.values();
    private static final Color[] ACCENTS = {
            new Color(120, 200, 255),
            new Color(180, 130, 255),
            new Color(235, 110, 90),
            new Color(255, 195, 80)
    };

    private final GamePanel gamePanel;
    private final Rectangle[] modeRects = new Rectangle[MODES.length];
    private final Rectangle backBtn = new Rectangle(20, 20, 120, 40);
    private int hoveredIndex = -1;

    public ModeSelectionScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        int cardWidth = 620;
        int cardHeight = 116;
        int spacing = 22;
        int x = (1600 - cardWidth) / 2;
        int y = 200;
        for (int i = 0; i < MODES.length; i++) {
            modeRects[i] = new Rectangle(x, y + i * (cardHeight + spacing), cardWidth, cardHeight);
        }
    }

    public void handleClick(int x, int y) {
        if (backBtn.contains(x, y)) {
            gamePanel.switchScreen("menu");
            return;
        }
        for (int i = 0; i < modeRects.length; i++) {
            if (modeRects[i].contains(x, y)) {
                gamePanel.getGameScreen().setGameMode(MODES[i]);
                gamePanel.switchScreen("arenaselection");
                return;
            }
        }
    }

    public void handleMouseMove(int x, int y) {
        hoveredIndex = -1;
        for (int i = 0; i < modeRects.length; i++) {
            if (modeRects[i].contains(x, y)) {
                hoveredIndex = i;
                return;
            }
        }
    }

    public void draw(Graphics2D g, int screenWidth, int screenHeight) {
        g.setPaint(new GradientPaint(0, 0, new Color(30, 30, 40), 0, screenHeight, new Color(20, 20, 30)));
        g.fillRect(0, 0, screenWidth, screenHeight);
        g.setPaint(null);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String title = "SELECT MODE";
        g.drawString(title, (screenWidth - fm.stringWidth(title)) / 2, 90);

        g.setColor(new Color(170, 185, 210));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        fm = g.getFontMetrics();
        String subtitle = "Pick the run rules, then pick an arena";
        g.drawString(subtitle, (screenWidth - fm.stringWidth(subtitle)) / 2, 126);

        GameMode current = gamePanel.getGameScreen().getGameMode();
        for (int i = 0; i < MODES.length; i++) {
            drawModeCard(g, i, MODES[i] == current);
        }

        drawBackButton(g);
    }

    private void drawModeCard(Graphics2D g, int index, boolean selected) {
        Rectangle rect = modeRects[index];
        GameMode mode = MODES[index];
        Color accent = ACCENTS[index % ACCENTS.length];
        boolean hovered = index == hoveredIndex;

        g.setColor(hovered ? new Color(52, 62, 84, 235) : new Color(34, 40, 55, 220));
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 16, 16);
        g.setColor(hovered || selected ? accent : new Color(70, 78, 95));
        g.setStroke(new BasicStroke(selected ? 3 : 2));
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 16, 16);
        g.setStroke(new BasicStroke(1));

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 26));
        g.drawString(mode.getDisplayName(), rect.x + 24, rect.y + 42);

        g.setColor(new Color(195, 205, 225));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g.drawString(mode.getDescription(), rect.x + 24, rect.y + 72);

        g.setColor(accent);
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g.drawString(buildSummary(mode), rect.x + 24, rect.y + 98);

        if (selected) {
            g.setColor(accent);
            g.setFont(new Font("Segoe UI", Font.BOLD, 14));
            String tag = "SELECTED";
            g.drawString(tag, rect.x + rect.width - g.getFontMetrics().stringWidth(tag) - 24, rect.y + 42);
        }
    }

    private String buildSummary(GameMode mode) {
        StringBuilder summary = new StringBuilder();
        if (mode.hasTimeLimit()) {
            summary.append(mode.getTimeLimitSeconds() / 60).append(" min limit");
        } else if (mode.getMaxWaves() >= 999) {
            summary.append("Unlimited waves");
        } else {
            summary.append(mode.getMaxWaves()).append(" waves");
        }
        summary.append("   Reward x").append(String.format("%.2f", mode.getRewardMultiplier()));
        summary.append("   Break ").append(mode.getGracePeriodFrames() / 60).append("s");
        if (mode.unlocksNextArena()) {
            summary.append("   Unlocks arenas");
        }
        return summary.toString();
    }

    private void drawBackButton(Graphics2D g) {
        g.setColor(new Color(100, 100, 120, 200));
        g.fillRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 8, 8);
        g.setColor(new Color(150, 150, 170));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 8, 8);
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        g.drawString("BACK", backBtn.x + (backBtn.width - fm.stringWidth("BACK")) / 2,
                backBtn.y + (backBtn.height + fm.getAscent() - fm.getDescent()) / 2);
    }
}
