package ui.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import profile.ProfileCatalog;
import profile.ProfileItem;
import profile.ProfileService;
import profile.banners.ProfileBanner;
import profile.killeffects.KillEffect;
import profile.titles.ProfileTitle;
import save.SaveData;
import ui.GamePanel;

public class ProfileScreen {
    private enum Tab {
        BANNERS, TITLES, KILL_EFFECTS
    }

    private final GamePanel gamePanel;

    private final Rectangle backBtn = new Rectangle(40, 24, 120, 44);
    private final Rectangle editUsernameBtn = new Rectangle(90, 486, 360, 46);
    private final Rectangle equipBtn = new Rectangle(1160, 790, 340, 54);

    private final Rectangle bannerTabBtn = new Rectangle(600, 112, 220, 50);
    private final Rectangle titleTabBtn = new Rectangle(834, 112, 220, 50);
    private final Rectangle killEffectTabBtn = new Rectangle(1068, 112, 220, 50);

    private final List<Rectangle> itemCards = new ArrayList<>();
    private Tab activeTab = Tab.BANNERS;
    private String hovered = null;
    private int hoveredItemIndex = -1;
    private int selectedItemIndex = 0;

    public ProfileScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        buildCardLayout();
        ProfileService.ensureDefaultsUnlocked();
    }

    private void buildCardLayout() {
        itemCards.clear();
        int startX = 600;
        int startY = 182;
        int cardW = 292;
        int cardH = 120;
        int gapX = 18;
        int gapY = 18;
        int cols = 3;
        int rows = 4;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = startX + col * (cardW + gapX);
                int y = startY + row * (cardH + gapY);
                itemCards.add(new Rectangle(x, y, cardW, cardH));
            }
        }
    }

    public void handleClick(int x, int y) {
        ProfileService.ensureDefaultsUnlocked();

        if (backBtn.contains(x, y)) {
            gamePanel.switchScreen("inventory");
            return;
        }
        if (editUsernameBtn.contains(x, y)) {
            openUsernameEditor();
            return;
        }
        if (bannerTabBtn.contains(x, y)) {
            activeTab = Tab.BANNERS;
            selectedItemIndex = 0;
            return;
        }
        if (titleTabBtn.contains(x, y)) {
            activeTab = Tab.TITLES;
            selectedItemIndex = 0;
            return;
        }
        if (killEffectTabBtn.contains(x, y)) {
            activeTab = Tab.KILL_EFFECTS;
            selectedItemIndex = 0;
            return;
        }

        List<? extends ProfileItem> items = getActiveItems();
        int visibleCount = Math.min(items.size(), itemCards.size());
        for (int i = 0; i < visibleCount; i++) {
            if (itemCards.get(i).contains(x, y)) {
                selectedItemIndex = i;
                return;
            }
        }

        if (equipBtn.contains(x, y)) {
            equipSelectedItem();
        }
    }

    public void handleMouseMove(int x, int y) {
        hovered = null;
        hoveredItemIndex = -1;

        if (backBtn.contains(x, y)) hovered = "back";
        if (editUsernameBtn.contains(x, y)) hovered = "edit";
        if (bannerTabBtn.contains(x, y)) hovered = "tab-banners";
        if (titleTabBtn.contains(x, y)) hovered = "tab-titles";
        if (killEffectTabBtn.contains(x, y)) hovered = "tab-kill";
        if (equipBtn.contains(x, y)) hovered = "equip";

        List<? extends ProfileItem> items = getActiveItems();
        int visibleCount = Math.min(items.size(), itemCards.size());
        for (int i = 0; i < visibleCount; i++) {
            if (itemCards.get(i).contains(x, y)) {
                hoveredItemIndex = i;
                break;
            }
        }
    }

    public void draw(Graphics2D g, int screenWidth, int screenHeight) {
        ProfileService.ensureDefaultsUnlocked();
        SaveData data = ProfileService.getProfileData();
        ProfileBanner equippedBanner = ProfileCatalog.findBanner(data.getEquippedProfileBanner());
        ProfileTitle equippedTitle = ProfileCatalog.findTitle(data.getEquippedTitle());
        KillEffect equippedEffect = ProfileCatalog.findKillEffect(data.getEquippedKillEffect());

        drawBackground(g, screenWidth, screenHeight);
        drawBackButton(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 36));
        g.drawString("Profile", 670, 66);

        drawPreviewPanel(g, data, equippedBanner, equippedTitle, equippedEffect);
        drawTabs(g);
        drawActiveGrid(g, data);
        drawSelectedDetailAndEquip(g, data);
    }

    private void drawBackground(Graphics2D g, int screenWidth, int screenHeight) {
        g.setPaint(new GradientPaint(0, 0, new Color(15, 21, 33), 0, screenHeight, new Color(10, 14, 23)));
        g.fillRect(0, 0, screenWidth, screenHeight);
        g.setPaint(null);
    }

    private void drawBackButton(Graphics2D g) {
        boolean hover = "back".equals(hovered);
        g.setColor(hover ? new Color(79, 105, 148) : new Color(57, 76, 110));
        g.fillRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 10, 10);
        g.setColor(Color.WHITE);
        g.drawRoundRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height, 10, 10);
        g.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g.drawString("Back", backBtn.x + 38, backBtn.y + 28);
    }

    private void drawPreviewPanel(Graphics2D g, SaveData data, ProfileBanner banner, ProfileTitle title, KillEffect effect) {
        int panelX = 40;
        int panelY = 100;
        int panelW = 520;
        int panelH = 760;

        g.setColor(new Color(24, 35, 54, 230));
        g.fillRoundRect(panelX, panelY, panelW, panelH, 16, 16);
        g.setColor(new Color(91, 120, 168));
        g.drawRoundRect(panelX, panelY, panelW, panelH, 16, 16);

        g.setFont(new Font("Segoe UI", Font.BOLD, 24));
        g.setColor(new Color(226, 236, 250));
        g.drawString("Player Card", panelX + 28, panelY + 38);

        int cardX = panelX + 50;
        int cardY = panelY + 70;
        int cardW = 420;
        int cardH = 380;

        g.setPaint(new GradientPaint(cardX, cardY, banner.getTopColor(), cardX, cardY + cardH, banner.getBottomColor()));
        g.fillRoundRect(cardX, cardY, cardW, cardH, 20, 20);
        g.setPaint(null);
        g.setColor(new Color(232, 241, 252));
        g.drawRoundRect(cardX, cardY, cardW, cardH, 20, 20);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 32));
        String username = data.getProfileUsername();
        g.drawString(username, cardX + 28, cardY + 86);

        g.setColor(title.getTextColor());
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g.drawString(title.getDisplayName(), cardX + 28, cardY + 126);

        g.setColor(new Color(224, 236, 250));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        g.drawString("Banner: " + banner.getDisplayName(), cardX + 28, cardY + 180);
        g.drawString("Kill Effect: " + effect.getDisplayName(), cardX + 28, cardY + 212);

        g.setColor(effect.getAccentColor());
        g.fillOval(cardX + 30, cardY + 248, 22, 22);
        g.setColor(new Color(230, 240, 252));
        g.drawString("Current Effect Accent", cardX + 66, cardY + 265);

        boolean hoverEdit = "edit".equals(hovered);
        g.setColor(hoverEdit ? new Color(108, 140, 186) : new Color(74, 96, 131));
        g.fillRoundRect(editUsernameBtn.x, editUsernameBtn.y, editUsernameBtn.width, editUsernameBtn.height, 10, 10);
        g.setColor(new Color(218, 230, 250));
        g.drawRoundRect(editUsernameBtn.x, editUsernameBtn.y, editUsernameBtn.width, editUsernameBtn.height, 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g.drawString("Edit Username", editUsernameBtn.x + 115, editUsernameBtn.y + 30);

        g.setColor(new Color(164, 186, 220));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g.drawString("Username rules: 3-16 chars, letters/numbers/spaces.", panelX + 34, panelY + 700);
        g.drawString("Leading/trailing spaces are trimmed automatically.", panelX + 34, panelY + 724);
    }

    private void drawTabs(Graphics2D g) {
        drawTab(g, bannerTabBtn, "Banners", activeTab == Tab.BANNERS, "tab-banners".equals(hovered));
        drawTab(g, titleTabBtn, "Titles", activeTab == Tab.TITLES, "tab-titles".equals(hovered));
        drawTab(g, killEffectTabBtn, "Kill Effects", activeTab == Tab.KILL_EFFECTS, "tab-kill".equals(hovered));
    }

    private void drawTab(Graphics2D g, Rectangle rect, String text, boolean active, boolean hover) {
        Color fill = active ? new Color(84, 128, 196) : (hover ? new Color(70, 95, 140) : new Color(53, 70, 102));
        g.setColor(fill);
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);
        g.setColor(active ? new Color(220, 236, 255) : new Color(149, 173, 210));
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        int tx = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int ty = rect.y + (rect.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(text, tx, ty);
    }

    private void drawActiveGrid(Graphics2D g, SaveData data) {
        List<? extends ProfileItem> items = getActiveItems();
        int visibleCount = Math.min(items.size(), itemCards.size());
        for (int i = 0; i < visibleCount; i++) {
            ProfileItem item = items.get(i);
            Rectangle card = itemCards.get(i);
            boolean selected = i == selectedItemIndex;
            boolean hover = i == hoveredItemIndex;
            boolean unlocked = isUnlocked(data, item.getId());
            boolean equipped = isEquipped(data, item.getId());

            Color fill = unlocked ? new Color(42, 60, 89) : new Color(38, 42, 55);
            if (selected) fill = new Color(67, 100, 150);
            else if (hover) fill = new Color(52, 82, 124);

            g.setColor(fill);
            g.fillRoundRect(card.x, card.y, card.width, card.height, 12, 12);
            g.setColor(selected ? new Color(188, 226, 255) : new Color(109, 136, 174));
            g.drawRoundRect(card.x, card.y, card.width, card.height, 12, 12);

            g.setColor(unlocked ? Color.WHITE : new Color(169, 176, 190));
            g.setFont(new Font("Segoe UI", Font.BOLD, 19));
            g.drawString(item.getDisplayName(), card.x + 16, card.y + 34);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            g.setColor(new Color(184, 202, 229));
            g.drawString(item.getDescription(), card.x + 16, card.y + 58);

            if (!unlocked) {
                g.setColor(new Color(244, 140, 140));
                g.drawString("Locked", card.x + 16, card.y + 91);
            } else if (equipped) {
                g.setColor(new Color(142, 236, 166));
                g.drawString("Equipped", card.x + 16, card.y + 91);
            } else {
                g.setColor(new Color(175, 211, 255));
                g.drawString("Unlocked", card.x + 16, card.y + 91);
            }
        }
    }

    private void drawSelectedDetailAndEquip(Graphics2D g, SaveData data) {
        List<? extends ProfileItem> items = getActiveItems();
        if (items.isEmpty()) {
            return;
        }
        int idx = Math.max(0, Math.min(selectedItemIndex, items.size() - 1));
        ProfileItem selected = items.get(idx);
        boolean unlocked = isUnlocked(data, selected.getId());
        boolean equipped = isEquipped(data, selected.getId());

        g.setColor(new Color(192, 210, 235));
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g.drawString("Selected: " + selected.getDisplayName(), 600, 762);

        boolean canEquip = unlocked && !equipped;
        boolean hover = "equip".equals(hovered);
        Color fill = canEquip ? (hover ? new Color(74, 153, 92) : new Color(58, 129, 74)) : new Color(86, 92, 108);
        g.setColor(fill);
        g.fillRoundRect(equipBtn.x, equipBtn.y, equipBtn.width, equipBtn.height, 10, 10);
        g.setColor(canEquip ? new Color(184, 255, 196) : new Color(165, 174, 192));
        g.drawRoundRect(equipBtn.x, equipBtn.y, equipBtn.width, equipBtn.height, 10, 10);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        String label = equipped ? "Already Equipped" : (canEquip ? "Equip Selected" : "Locked");
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, equipBtn.x + (equipBtn.width - fm.stringWidth(label)) / 2, equipBtn.y + 35);
    }

    private void openUsernameEditor() {
        SaveData data = ProfileService.getProfileData();
        String input = JOptionPane.showInputDialog(
                null,
                "Enter username (3-16 chars, letters/numbers/spaces):",
                data.getProfileUsername());

        if (input == null) {
            return;
        }
        String error = ProfileService.validateUsername(input);
        if (error != null) {
            JOptionPane.showMessageDialog(null, error, "Invalid Username", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String normalized = ProfileService.normalizeUsername(input);
        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Set username to \"" + normalized + "\"?",
                "Confirm Username",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            ProfileService.setUsername(normalized);
        }
    }

    private List<? extends ProfileItem> getActiveItems() {
        return switch (activeTab) {
            case BANNERS -> ProfileCatalog.banners();
            case TITLES -> ProfileCatalog.titles();
            case KILL_EFFECTS -> ProfileCatalog.killEffects();
        };
    }

    private boolean isUnlocked(SaveData data, String itemId) {
        return switch (activeTab) {
            case BANNERS -> data.getUnlockedProfileBanners().contains(itemId);
            case TITLES -> data.getUnlockedTitles().contains(itemId);
            case KILL_EFFECTS -> data.getUnlockedKillEffects().contains(itemId);
        };
    }

    private boolean isEquipped(SaveData data, String itemId) {
        return switch (activeTab) {
            case BANNERS -> itemId.equals(data.getEquippedProfileBanner());
            case TITLES -> itemId.equals(data.getEquippedTitle());
            case KILL_EFFECTS -> itemId.equals(data.getEquippedKillEffect());
        };
    }

    private void equipSelectedItem() {
        List<? extends ProfileItem> items = getActiveItems();
        if (items.isEmpty()) {
            return;
        }
        int idx = Math.max(0, Math.min(selectedItemIndex, items.size() - 1));
        ProfileItem selected = items.get(idx);

        switch (activeTab) {
            case BANNERS -> ProfileService.equipBanner(selected.getId());
            case TITLES -> ProfileService.equipTitle(selected.getId());
            case KILL_EFFECTS -> ProfileService.equipKillEffect(selected.getId());
        }
    }
}
