package ui.screens;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import mastery.MasteryConstants;
import mastery.MasteryRewardCatalog;
import mastery.MasteryRewardSlot;
import mastery.MasteryService;
import save.SaveData;
import save.SaveManager;
import ui.GamePanel;

public class MasteryScreen {
    private final GamePanel gamePanel;

    private final Rectangle backBtn = new Rectangle(30, 20, 120, 46);
    private final Rectangle resetAllBtn = new Rectangle(1000, 78, 290, 42);
    private final Rectangle claimBtn = new Rectangle(1100, 790, 330, 54);
    private final Rectangle weaponTabBtn = new Rectangle(40, 146, 170, 34);
    private final Rectangle powerTabBtn = new Rectangle(220, 146, 170, 34);

    private final List<String> visibleWeaponTypes = new ArrayList<>();
    private final List<Rectangle> weaponCards = new ArrayList<>();
    private final List<Rectangle> levelCells = new ArrayList<>();

    private int selectedWeaponIndex = -1;
    private int selectedLevel = 1;
    private String hoveredButton = null;
    private int hoveredWeaponIndex = -1;
    private int hoveredLevel = -1;
    private boolean viewingPowerMastery = false;
    private String returnScreen = "menu";

    private static final int WEAPON_CARD_W = 150;
    private static final int WEAPON_CARD_H = 68;
    private static final int WEAPON_CARD_GAP = 12;
    private static final int WEAPON_COLS = 4;
    private static final int WEAPON_GRID_START_X = 40;
    private static final int WEAPON_GRID_START_Y = 222;

    private static final int RIGHT_PANEL_X = 720;
    private static final int RIGHT_PANEL_Y = 120;
    private static final int RIGHT_PANEL_W = 850;
    private static final int RIGHT_PANEL_H = 750;

    private static final int LEVEL_GRID_X = 760;
    private static final int LEVEL_GRID_Y = 250;
    private static final int LEVEL_CELL_SIZE = 30;
    private static final int LEVEL_GRID_COLS = 10;
    private static final int LEVEL_GRID_ROWS = 10;

    public MasteryScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        refreshWeaponList();
        buildLevelCellLayout();
    }

    public void refreshWeaponList() {
        visibleWeaponTypes.clear();
        weaponCards.clear();

        SaveData data = SaveManager.load();
        Set<String> unlocked = viewingPowerMastery
                ? data.getUnlockedPowers().keySet()
                : data.getUnlockedWeapons().keySet();
        visibleWeaponTypes.addAll(unlocked);
        if (!viewingPowerMastery && visibleWeaponTypes.isEmpty()) {
            // Keep defaults visible even on brand-new saves.
            visibleWeaponTypes.add("Pistol1");
            visibleWeaponTypes.add("Rifle1");
        }
        Collections.sort(visibleWeaponTypes);

        for (int i = 0; i < visibleWeaponTypes.size(); i++) {
            int col = i % WEAPON_COLS;
            int row = i / WEAPON_COLS;
            int x = WEAPON_GRID_START_X + col * (WEAPON_CARD_W + WEAPON_CARD_GAP);
            int y = WEAPON_GRID_START_Y + row * (WEAPON_CARD_H + WEAPON_CARD_GAP);
            weaponCards.add(new Rectangle(x, y, WEAPON_CARD_W, WEAPON_CARD_H));
        }

        if (selectedWeaponIndex < 0 && !visibleWeaponTypes.isEmpty()) {
            selectedWeaponIndex = 0;
        } else if (selectedWeaponIndex >= visibleWeaponTypes.size()) {
            selectedWeaponIndex = visibleWeaponTypes.isEmpty() ? -1 : 0;
        }
    }

    private void buildLevelCellLayout() {
        levelCells.clear();
        for (int i = 0; i < LEVEL_GRID_COLS * LEVEL_GRID_ROWS; i++) {
            int col = i % LEVEL_GRID_COLS;
            int row = i / LEVEL_GRID_COLS;
            int x = LEVEL_GRID_X + col * LEVEL_CELL_SIZE;
            int y = LEVEL_GRID_Y + row * LEVEL_CELL_SIZE;
            levelCells.add(new Rectangle(x, y, LEVEL_CELL_SIZE - 2, LEVEL_CELL_SIZE - 2));
        }
    }

    public void handleClick(int x, int y) {
        if (backBtn.contains(x, y)) {
            gamePanel.switchScreen(returnScreen);
            return;
        }
        if (weaponTabBtn.contains(x, y)) {
            viewingPowerMastery = false;
            selectedLevel = 1;
            refreshWeaponList();
            return;
        }
        if (powerTabBtn.contains(x, y)) {
            viewingPowerMastery = true;
            selectedLevel = 1;
            refreshWeaponList();
            return;
        }
        if (!viewingPowerMastery && resetAllBtn.contains(x, y)) {
            MasteryService.resetAllWeaponMasteries();
            selectedLevel = 1;
            refreshWeaponList();
            return;
        }

        for (int i = 0; i < weaponCards.size(); i++) {
            if (weaponCards.get(i).contains(x, y)) {
                selectedWeaponIndex = i;
                return;
            }
        }

        for (int i = 0; i < levelCells.size(); i++) {
            if (levelCells.get(i).contains(x, y)) {
                selectedLevel = i + 1;
                return;
            }
        }

        if (claimBtn.contains(x, y)) {
            tryClaimSelectedLevel();
        }
    }

    public void handleMouseMove(int x, int y) {
        hoveredButton = null;
        hoveredWeaponIndex = -1;
        hoveredLevel = -1;
        if (backBtn.contains(x, y)) hoveredButton = "back";
        if (resetAllBtn.contains(x, y)) hoveredButton = "reset";
        if (claimBtn.contains(x, y)) hoveredButton = "claim";
        if (weaponTabBtn.contains(x, y)) hoveredButton = "tab_weapons";
        if (powerTabBtn.contains(x, y)) hoveredButton = "tab_powers";

        for (int i = 0; i < weaponCards.size(); i++) {
            if (weaponCards.get(i).contains(x, y)) {
                hoveredWeaponIndex = i;
                break;
            }
        }
        for (int i = 0; i < levelCells.size(); i++) {
            if (levelCells.get(i).contains(x, y)) {
                hoveredLevel = i + 1;
                break;
            }
        }
    }

    private void tryClaimSelectedLevel() {
        String selectedWeapon = getSelectedWeaponType();
        if (selectedWeapon == null) {
            return;
        }
        if (viewingPowerMastery) {
            MasteryService.claimPowerReward(selectedWeapon, selectedLevel);
        } else {
            MasteryService.claimWeaponReward(selectedWeapon, selectedLevel);
        }
    }

    public void draw(Graphics2D g, int screenWidth, int screenHeight) {
        drawBackground(g, screenWidth, screenHeight);
        refreshWeaponList();

        drawBackButton(g);
        drawHeader(g);
        drawWeaponGrid(g);
        drawRightPanel(g);
    }

    private void drawBackground(Graphics2D g, int screenWidth, int screenHeight) {
        g.setPaint(new java.awt.GradientPaint(0, 0, new Color(22, 26, 36), 0, screenHeight, new Color(15, 18, 26)));
        g.fillRect(0, 0, screenWidth, screenHeight);
        g.setPaint(null);
    }

    private void drawBackButton(Graphics2D g) {
        boolean hover = "back".equals(hoveredButton);
        g.setColor(hover ? new Color(90, 120, 165) : new Color(62, 79, 110));
        g.fillRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 10, 10);
        g.setColor(Color.WHITE);
        g.drawRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 10, 10);
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g.drawString("Back", backBtn.x + 36, backBtn.y + 29);
    }

    private void drawHeader(Graphics2D g) {
        SaveData data = SaveManager.load();
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 34));
        g.drawString(viewingPowerMastery ? "Power Mastery" : "Weapon Mastery", 40, 105);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g.setColor(new Color(190, 205, 226));
        g.drawString("Manual claim enabled. Mastery includes both weapons and powers.", 40, 132);

        g.setColor(new Color(255, 210, 90));
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g.drawString("Mastery Medals: " + data.getMasteryMedals(), 1300, 52);

        drawTabButton(g, weaponTabBtn, "Weapons", !viewingPowerMastery, "tab_weapons".equals(hoveredButton));
        drawTabButton(g, powerTabBtn, "Powers", viewingPowerMastery, "tab_powers".equals(hoveredButton));

        boolean resetHover = "reset".equals(hoveredButton);
        Color resetFill = viewingPowerMastery
                ? new Color(72, 72, 82)
                : (resetHover ? new Color(156, 68, 72) : new Color(120, 55, 60));
        g.setColor(resetFill);
        g.fillRoundRect(resetAllBtn.x, resetAllBtn.y, resetAllBtn.width, resetAllBtn.height, 10, 10);
        g.setColor(viewingPowerMastery ? new Color(128, 138, 160) : new Color(245, 170, 170));
        g.drawRoundRect(resetAllBtn.x, resetAllBtn.y, resetAllBtn.width, resetAllBtn.height, 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        String resetLabel = viewingPowerMastery ? "Reset disabled on Power tab" : "Reset All Weapon Masteries";
        FontMetrics resetFm = g.getFontMetrics();
        g.drawString(resetLabel, resetAllBtn.x + (resetAllBtn.width - resetFm.stringWidth(resetLabel)) / 2, resetAllBtn.y + 26);
    }

    private void drawWeaponGrid(Graphics2D g) {
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g.setColor(new Color(195, 210, 234));
        g.drawString(viewingPowerMastery ? "Unlocked Powers" : "Unlocked Weapons", 40, WEAPON_GRID_START_Y - 18);

        for (int i = 0; i < weaponCards.size(); i++) {
            Rectangle card = weaponCards.get(i);
            String weapon = visibleWeaponTypes.get(i);
            int level = viewingPowerMastery
                    ? MasteryService.getPowerMasteryLevel(weapon)
                    : MasteryService.getWeaponMasteryLevel(weapon);
            int unclaimed = viewingPowerMastery
                    ? MasteryService.getUnclaimedPowerRewardCount(weapon)
                    : MasteryService.getUnclaimedWeaponRewardCount(weapon);

            boolean selected = i == selectedWeaponIndex;
            boolean hover = i == hoveredWeaponIndex;
            Color fill = selected ? new Color(64, 95, 145) : (hover ? new Color(55, 73, 102) : new Color(42, 52, 75));
            g.setColor(fill);
            g.fillRoundRect(card.x, card.y, card.width, card.height, 10, 10);

            g.setColor(selected ? new Color(145, 205, 255) : new Color(103, 122, 160));
            g.setStroke(new BasicStroke(selected ? 2.2f : 1.5f));
            g.drawRoundRect(card.x, card.y, card.width, card.height, 10, 10);
            g.setStroke(new BasicStroke(1f));

            g.setColor(Color.WHITE);
            g.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g.drawString(shortWeaponName(weapon), card.x + 12, card.y + 28);

            g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            g.setColor(new Color(188, 205, 230));
            g.drawString("Lv " + level + " / " + MasteryConstants.MAX_LEVEL, card.x + 12, card.y + 50);

            if (unclaimed > 0) {
                int badgeR = 11;
                int cx = card.x + card.width - 16;
                int cy = card.y + 14;
                g.setColor(new Color(220, 55, 63));
                g.fillOval(cx - badgeR, cy - badgeR, badgeR * 2, badgeR * 2);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g.drawString("!", cx - 3, cy + 5);
            }
        }
    }

    private void drawRightPanel(Graphics2D g) {
        String weapon = getSelectedWeaponType();
        int level = weapon == null ? 1 : (viewingPowerMastery
                ? MasteryService.getPowerMasteryLevel(weapon)
                : MasteryService.getWeaponMasteryLevel(weapon));
        int xpNeeded = level >= MasteryConstants.MAX_LEVEL ? 0 : masteryXpRequiredForLevel(level);
        int totalXp = 0;
        if (weapon != null) {
            SaveData data = SaveManager.load();
            totalXp = viewingPowerMastery
                    ? data.getPowerMasteryXpByType().getOrDefault(weapon, 0)
                    : data.getWeaponMasteryXpByType().getOrDefault(weapon, 0);
        }
        int xpInto = weapon == null ? 0 : masteryXpIntoCurrentLevel(totalXp);

        g.setColor(new Color(26, 35, 52, 215));
        g.fillRoundRect(RIGHT_PANEL_X, RIGHT_PANEL_Y, RIGHT_PANEL_W, RIGHT_PANEL_H, 12, 12);
        g.setColor(new Color(100, 128, 176));
        g.drawRoundRect(RIGHT_PANEL_X, RIGHT_PANEL_Y, RIGHT_PANEL_W, RIGHT_PANEL_H, 12, 12);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 24));
        g.drawString(weapon == null ? "No weapon selected" : shortWeaponName(weapon), RIGHT_PANEL_X + 30, RIGHT_PANEL_Y + 33);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g.setColor(new Color(196, 211, 234));
        g.drawString("Mastery Level " + level + " / " + MasteryConstants.MAX_LEVEL, RIGHT_PANEL_X + 30, RIGHT_PANEL_Y + 58);

        drawXpBar(g, xpInto, xpNeeded);
        drawLevelGrid(g, weapon, level);
        drawClaimSection(g, weapon, level);
    }

    private void drawXpBar(Graphics2D g, int xpInto, int xpNeeded) {
        int x = 760;
        int y = 194;
        int w = 500;
        int h = 24;
        g.setColor(new Color(44, 57, 81));
        g.fillRoundRect(x, y, w, h, 8, 8);
        if (xpNeeded > 0) {
            int fill = (int) Math.round((xpInto / (double) xpNeeded) * w);
            g.setColor(new Color(96, 186, 255));
            g.fillRoundRect(x, y, Math.max(0, Math.min(w, fill)), h, 8, 8);
        } else {
            g.setColor(new Color(140, 214, 137));
            g.fillRoundRect(x, y, w, h, 8, 8);
        }
        g.setColor(Color.WHITE);
        g.drawRoundRect(x, y, w, h, 8, 8);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g.setColor(new Color(225, 236, 250));
        String text = xpNeeded <= 0 ? "MAX" : (xpInto + " / " + xpNeeded + " XP");
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, x + (w - fm.stringWidth(text)) / 2, y + 17);
    }

    private void drawLevelGrid(Graphics2D g, String weapon, int currentLevel) {
        List<MasteryRewardSlot> slots = weapon == null
                ? List.of()
                : (viewingPowerMastery ? MasteryService.getPowerRewardSlots(weapon) : MasteryService.getWeaponRewardSlots(weapon));
        g.setColor(new Color(196, 211, 234));
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g.drawString("Level Slots (1-100)", LEVEL_GRID_X, LEVEL_GRID_Y - 16);

        for (int i = 0; i < levelCells.size(); i++) {
            int level = i + 1;
            Rectangle cell = levelCells.get(i);
            boolean reached = level <= currentLevel;
            boolean selected = level == selectedLevel;
            boolean hovered = level == hoveredLevel;

            MasteryRewardSlot slot = i < slots.size() ? slots.get(i) : null;
            boolean configured = slot != null && slot.isConfigured();
            boolean claimed = slot != null && slot.isClaimed();

            Color fill = reached ? new Color(72, 109, 160) : new Color(42, 55, 80);
            if (claimed) {
                fill = new Color(62, 125, 92);
            } else if (configured && reached) {
                fill = new Color(141, 121, 62);
            } else if (hovered) {
                fill = new Color(86, 108, 147);
            }
            g.setColor(fill);
            g.fillRect(cell.x, cell.y, cell.width, cell.height);

            g.setColor(selected ? new Color(160, 220, 255) : new Color(95, 121, 165));
            g.setStroke(new BasicStroke(selected ? 2f : 1f));
            g.drawRect(cell.x, cell.y, cell.width, cell.height);
            g.setStroke(new BasicStroke(1f));

            g.setColor(new Color(218, 230, 248));
            g.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            String levelText = String.valueOf(level);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(levelText, cell.x + (cell.width - fm.stringWidth(levelText)) / 2, cell.y + 20);
        }
    }

    private void drawClaimSection(Graphics2D g, String weapon, int currentLevel) {
        g.setColor(new Color(196, 211, 234));
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g.drawString("Selected Level: " + selectedLevel, 1110, 620);

        String rewardKey = (viewingPowerMastery
                ? MasteryRewardCatalog.powerRewardsByLevel()
                : MasteryRewardCatalog.weaponRewardsByLevel()).get(selectedLevel);
        boolean configured = rewardKey != null && !rewardKey.isBlank();
        boolean reached = selectedLevel <= currentLevel;
        boolean claimed = false;
        if (weapon != null) {
            List<MasteryRewardSlot> slots = viewingPowerMastery
                    ? MasteryService.getPowerRewardSlots(weapon)
                    : MasteryService.getWeaponRewardSlots(weapon);
            if (selectedLevel - 1 < slots.size()) {
                claimed = slots.get(selectedLevel - 1).isClaimed();
            }
        }

        g.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        if (!configured) {
            g.setColor(new Color(245, 205, 110));
            g.drawString("Reward slot empty (placeholder for Part 3).", 1110, 652);
        } else {
            g.setColor(new Color(224, 236, 250));
            g.drawString("Reward: " + rewardKey, 1110, 652);
        }

        if (!reached) {
            g.setColor(new Color(255, 142, 142));
            g.drawString("Locked - reach this mastery level first.", 1110, 676);
        } else if (claimed) {
            g.setColor(new Color(142, 255, 185));
            g.drawString("Claimed.", 1110, 676);
        } else {
            g.setColor(new Color(186, 220, 255));
            g.drawString("Ready to claim.", 1110, 676);
        }

        boolean canClaim = weapon != null && configured && reached && !claimed;
        boolean hover = "claim".equals(hoveredButton);
        Color fill = canClaim ? (hover ? new Color(72, 146, 82) : new Color(56, 122, 70))
                              : new Color(82, 82, 94);
        g.setColor(fill);
        g.fillRoundRect(claimBtn.x, claimBtn.y, claimBtn.width, claimBtn.height, 10, 10);
        g.setColor(canClaim ? new Color(180, 255, 196) : new Color(152, 160, 174));
        g.drawRoundRect(claimBtn.x, claimBtn.y, claimBtn.width, claimBtn.height, 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        String label = canClaim ? "Claim Reward" : "Claim Unavailable";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, claimBtn.x + (claimBtn.width - fm.stringWidth(label)) / 2, claimBtn.y + 33);
    }

    private String getSelectedWeaponType() {
        if (selectedWeaponIndex < 0 || selectedWeaponIndex >= visibleWeaponTypes.size()) {
            return null;
        }
        return visibleWeaponTypes.get(selectedWeaponIndex);
    }

    private String shortWeaponName(String weapon) {
        if (weapon == null) {
            return "";
        }
        return switch (weapon) {
            case "Pistol1" -> "Glock";
            case "Rifle1" -> "AK-47";
            case "Shotgun1" -> "Shotgun";
            case "SMG1" -> "P90";
            default -> weapon;
        };
    }

    public void setReturnScreen(String returnScreen) {
        if (returnScreen == null || returnScreen.isBlank()) {
            this.returnScreen = "menu";
            return;
        }
        this.returnScreen = returnScreen;
    }

    private void drawTabButton(Graphics2D g, Rectangle rect, String label, boolean selected, boolean hovered) {
        Color fill = selected
                ? new Color(82, 128, 186)
                : (hovered ? new Color(60, 82, 116) : new Color(44, 59, 84));
        g.setColor(fill);
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);
        g.setColor(selected ? new Color(168, 224, 255) : new Color(100, 128, 170));
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, rect.x + (rect.width - fm.stringWidth(label)) / 2, rect.y + 22);
    }

    private int masteryXpIntoCurrentLevel(int totalXp) {
        int xp = Math.max(0, totalXp);
        int level = 1;
        while (level < MasteryConstants.MAX_LEVEL) {
            int req = masteryXpRequiredForLevel(level);
            if (xp < req) {
                break;
            }
            xp -= req;
            level++;
        }
        return xp;
    }

    private int masteryXpRequiredForLevel(int currentLevel) {
        return 60 + (currentLevel * 22);
    }
}
