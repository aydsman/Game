package ui.screens;

import combat.Item;
import combat.Melee;
import combat.Ranged;
import combat.charms.Charm;
import combat.clothing.ClothingItem;
import combat.consumables.Consumable;
import combat.powers.Power;
import combat.summons.Summon;
import currency.CurrencyManager;
import save.SaveData;
import save.SaveManager;
import shop.ShopBundle;
import shop.ShopCatalog;
import shop.ShopEntry;
import shop.ShopReward;
import ui.GamePanel;
import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShopScreen {

    private final GamePanel gamePanel;
    private final Rectangle backBtn = new Rectangle(24, 24, 120, 42);

    private final List<ShopEntry> smallOffers;
    private final List<ShopBundle> featuredBundles;
    private final List<CardPlacement> placements = new ArrayList<>();
    private final Map<String, BufferedImage> iconCache = new HashMap<>();

    private int scrollOffsetY = 0;
    private static final int CONTENT_START_Y = 110;
    private static final int CONTENT_BOTTOM_Y = 862;
    private static final int CONTENT_HEIGHT = CONTENT_BOTTOM_Y - CONTENT_START_Y;

    private static final int LEFT_X = 55;
    private static final int LEFT_W = 360;
    private static final int RIGHT_X = 445;
    private static final int RIGHT_W = 1095;
    private static final int SMALL_H = 100;
    private static final int SMALL_GAP = 14;
    private static final int ROW_H = SMALL_H * 3 + SMALL_GAP * 2;
    private static final int BLOCK_GAP = 26;

    private ShopEntry selectedEntry = null;
    private Rectangle modalRect = new Rectangle();
    private Rectangle modalBuyBtn = new Rectangle();
    private Rectangle modalCloseBtn = new Rectangle();
    private String purchaseMessage = "";
    private int hoverX = -1;
    private int hoverY = -1;

    public ShopScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.smallOffers = ShopCatalog.smallOffers();
        this.featuredBundles = ShopCatalog.featuredBundles();
        rebuildPlacements();
    }

    public void handleClick(int x, int y) {
        hoverX = x;
        hoverY = y;
        if (backBtn.contains(x, y)) {
            gamePanel.switchScreen(gamePanel.getPreviousScreen());
            return;
        }

        // Modal captures all input while open.
        if (selectedEntry != null) {
            if (!modalRect.contains(x, y)) {
                selectedEntry = null; // option A: click outside closes modal
                return;
            }
            if (modalCloseBtn.contains(x, y)) {
                selectedEntry = null;
                return;
            }
            if (modalBuyBtn.contains(x, y)) {
                purchaseSelectedEntry();
                return;
            }
            return;
        }

        for (CardPlacement p : placements) {
            Rectangle onScreen = toScreenRect(p.rect);
            if (isCardVisible(onScreen) && onScreen.contains(x, y)) {
                selectedEntry = p.entry;
                purchaseMessage = "";
                return;
            }
        }
    }

    public void handleMouseMove(int x, int y) {
        hoverX = x;
        hoverY = y;
    }

    public void handleMouseScroll(int wheelRotation) {
        if (selectedEntry != null) return; // lock background while modal open
        int maxOffset = Math.max(0, getContentHeight() - CONTENT_HEIGHT);
        scrollOffsetY += wheelRotation * 42;
        if (scrollOffsetY < 0) scrollOffsetY = 0;
        if (scrollOffsetY > maxOffset) scrollOffsetY = maxOffset;
    }

    public void draw(Graphics2D g, int width, int height) {
        drawBackground(g, width, height);
        drawHeader(g, width);
        drawCards(g);
        drawScrollbar(g);

        if (selectedEntry != null) {
            drawModalOverlay(g, width, height);
            drawEntryModal(g, selectedEntry, width, height);
        }
    }

    private void drawBackground(Graphics2D g, int width, int height) {
        GradientPaint bg = new GradientPaint(0, 0, new Color(26, 28, 40), 0, height, new Color(14, 16, 24));
        g.setPaint(bg);
        g.fillRect(0, 0, width, height);
        g.setPaint(null);
        g.setColor(new Color(255, 255, 255, 8));
        for (int x = 0; x < width; x += 40) g.drawLine(x, 0, x, height);
        for (int y = 0; y < height; y += 40) g.drawLine(0, y, width, y);
    }

    private void drawHeader(Graphics2D g, int width) {
        g.setColor(new Color(0, 0, 0, 130));
        g.fillRect(0, 0, width, 88);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 36));
        g.drawString("Shop", 170, 58);

        CurrencyManager cm = gamePanel.getCurrencyManager();
        String cash = "$ " + cm.getCash().getAmount();
        String gems = "◆ " + cm.getGems().getAmount();
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g.setColor(new Color(80, 220, 120));
        int cashW = g.getFontMetrics().stringWidth(cash);
        g.drawString(cash, width - cashW - 28, 38);
        g.setColor(new Color(100, 180, 255));
        int gemsW = g.getFontMetrics().stringWidth(gems);
        g.drawString(gems, width - gemsW - 28, 68);

        drawButton(g, backBtn, "Back", new Color(75, 85, 110), new Color(110, 130, 170), backBtn.contains(hoverX, hoverY));
    }

    private void drawCards(Graphics2D g) {
        for (CardPlacement p : placements) {
            Rectangle onScreen = toScreenRect(p.rect);
            if (!isCardVisible(onScreen)) continue;
            drawCard(g, p.entry, onScreen);
        }
    }

    private void drawCard(Graphics2D g, ShopEntry entry, Rectangle r) {
        boolean hover = r.contains(hoverX, hoverY);
        int yOffset = hover ? -3 : 0;
        int x = r.x;
        int y = r.y + yOffset;
        int w = r.width;
        int h = r.height;

        Color accent = entry.isBundle() ? new Color(255, 190, 80) : new Color(100, 180, 255);
        GradientPaint fill = new GradientPaint(x, y, new Color(46, 50, 68), x, y + h, new Color(33, 36, 50));
        g.setPaint(fill);
        g.fillRoundRect(x, y, w, h, 14, 14);
        g.setPaint(null);
        g.setColor(new Color(0, 0, 0, 55));
        g.fillRoundRect(x + 3, y + 3, w, h, 14, 14);
        g.setColor(hover ? accent.brighter() : accent);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, w, h, 14, 14);
        g.setStroke(new BasicStroke(1));

        BufferedImage icon = loadIcon(entry.getIconPath());
        if (icon != null) {
            int iconSize = entry.isBundle() ? 72 : 48;
            drawScaledImage(g, icon, x + 14, y + 12, iconSize, iconSize);
        }

        int textX = x + (entry.isBundle() ? 100 : 70);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, entry.isBundle() ? 24 : 18));
        g.drawString(entry.getTitle(), textX, y + 34);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g.setColor(new Color(180, 190, 210));
        String desc = entry.getDescription();
        if (desc.length() > 70) desc = desc.substring(0, 70) + "...";
        g.drawString(desc, textX, y + 56);

        g.setColor(new Color(255, 220, 140));
        g.setFont(new Font("Segoe UI", Font.BOLD, entry.isBundle() ? 18 : 14));
        g.drawString(entry.getPriceLabel(), x + 14, y + h - 14);

        if (entry.isBundle()) {
            drawBundleRewardIcons(g, entry, x + 340, y + h - 58, w - 360);
            g.setColor(new Color(255, 190, 80));
            g.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g.drawString("BUNDLE", x + w - 78, y + 20);
        }
    }

    private void drawModalOverlay(Graphics2D g, int width, int height) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, width, height);
    }

    private void drawEntryModal(Graphics2D g, ShopEntry entry, int width, int height) {
        int w = 860;
        int h = 540;
        int x = (width - w) / 2;
        int y = (height - h) / 2;
        modalRect = new Rectangle(x, y, w, h);

        g.setColor(new Color(20, 22, 30, 238));
        g.fillRoundRect(x, y, w, h, 18, 18);
        g.setColor(entry.isBundle() ? new Color(255, 190, 80) : new Color(120, 200, 255));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(x, y, w, h, 18, 18);
        g.setStroke(new BasicStroke(1));

        BufferedImage icon = loadIcon(entry.getIconPath());
        if (icon != null) {
            drawScaledImage(g, icon, x + 24, y + 26, 120, 120);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 30));
        g.drawString(entry.getTitle(), x + 165, y + 60);
        g.setColor(new Color(180, 190, 210));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        drawWrapped(g, entry.getDescription(), x + 165, y + 90, w - 195, 22, 3);

        g.setColor(new Color(255, 220, 140));
        g.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g.drawString("Price: " + entry.getPriceLabel(), x + 165, y + 168);

        g.setColor(new Color(210, 220, 240));
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g.drawString("Includes", x + 24, y + 230);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        int lineY = y + 258;
        if (entry.getRewards().isEmpty()) {
            g.setColor(new Color(140, 150, 170));
            g.drawString("• No rewards configured yet.", x + 36, lineY);
        } else {
            for (ShopReward reward : entry.getRewards()) {
                g.setColor(new Color(190, 205, 230));
                g.drawString("• " + rewardText(reward), x + 36, lineY);
                lineY += 26;
                if (lineY > y + h - 130) break;
            }
        }

        if (!purchaseMessage.isEmpty()) {
            g.setColor(purchaseMessage.startsWith("Purchased") ? new Color(120, 220, 140) : new Color(255, 140, 140));
            g.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g.drawString(purchaseMessage, x + 24, y + h - 82);
        }

        modalBuyBtn = new Rectangle(x + w - 280, y + h - 74, 160, 46);
        modalCloseBtn = new Rectangle(x + w - 100, y + 20, 70, 34);
        drawButton(g, modalBuyBtn, "Buy", new Color(50, 160, 95), new Color(80, 205, 120), modalBuyBtn.contains(hoverX, hoverY));
        drawButton(g, modalCloseBtn, "Close", new Color(95, 95, 110), new Color(130, 130, 150), modalCloseBtn.contains(hoverX, hoverY));
    }

    private void drawButton(Graphics2D g, Rectangle btn, String label, Color base, Color hover, boolean isHovered) {
        Color top = isHovered ? hover : base;
        GradientPaint gradient = new GradientPaint(btn.x, btn.y, top, btn.x, btn.y + btn.height, top.darker());
        g.setPaint(gradient);
        g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 10, 10);
        g.setPaint(null);
        g.setColor(new Color(230, 230, 240));
        g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();
        int tx = btn.x + (btn.width - fm.stringWidth(label)) / 2;
        int ty = btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, tx, ty);
    }

    private void drawScrollbar(Graphics2D g) {
        int contentHeight = getContentHeight();
        if (contentHeight <= CONTENT_HEIGHT) return;

        int trackX = 1552;
        int trackY = CONTENT_START_Y;
        int trackH = CONTENT_HEIGHT;
        g.setColor(new Color(70, 74, 90));
        g.fillRoundRect(trackX, trackY, 10, trackH, 10, 10);

        int thumbH = Math.max(50, (int) ((CONTENT_HEIGHT / (double) contentHeight) * trackH));
        int maxOffset = contentHeight - CONTENT_HEIGHT;
        int thumbY = trackY + (int) ((scrollOffsetY / (double) maxOffset) * (trackH - thumbH));
        g.setColor(new Color(130, 180, 255));
        g.fillRoundRect(trackX, thumbY, 10, thumbH, 10, 10);
    }

    private void rebuildPlacements() {
        placements.clear();
        int rows = Math.max(featuredBundles.size(), (int) Math.ceil(smallOffers.size() / 3.0));
        int smallIndex = 0;
        for (int row = 0; row < rows; row++) {
            int baseY = CONTENT_START_Y + row * (ROW_H + BLOCK_GAP);

            for (int i = 0; i < 3; i++) {
                if (smallIndex >= smallOffers.size()) break;
                Rectangle r = new Rectangle(LEFT_X, baseY + i * (SMALL_H + SMALL_GAP), LEFT_W, SMALL_H);
                placements.add(new CardPlacement(smallOffers.get(smallIndex), r));
                smallIndex++;
            }

            if (row < featuredBundles.size()) {
                Rectangle big = new Rectangle(RIGHT_X, baseY, RIGHT_W, ROW_H);
                placements.add(new CardPlacement(featuredBundles.get(row), big));
            }
        }
    }

    private int getContentHeight() {
        int max = CONTENT_HEIGHT;
        for (CardPlacement p : placements) {
            int bottom = p.rect.y + p.rect.height;
            if (bottom > max) max = bottom;
        }
        return max - CONTENT_START_Y + 24;
    }

    private boolean isCardVisible(Rectangle onScreenRect) {
        return onScreenRect.y + onScreenRect.height >= CONTENT_START_Y && onScreenRect.y <= CONTENT_BOTTOM_Y;
    }

    private Rectangle toScreenRect(Rectangle source) {
        return new Rectangle(source.x, source.y - scrollOffsetY, source.width, source.height);
    }

    private BufferedImage loadIcon(String path) {
        if (path == null || path.isEmpty()) return null;
        if (iconCache.containsKey(path)) return iconCache.get(path);
        try {
            BufferedImage img = ImageIO.read(new File(path));
            iconCache.put(path, img);
            return img;
        } catch (Exception e) {
            iconCache.put(path, null);
            return null;
        }
    }

    private void drawScaledImage(Graphics2D g, BufferedImage image, int x, int y, int maxW, int maxH) {
        double scale = Math.min((double) maxW / image.getWidth(), (double) maxH / image.getHeight());
        int w = (int) (image.getWidth() * scale);
        int h = (int) (image.getHeight() * scale);
        g.drawImage(image, x + (maxW - w) / 2, y + (maxH - h) / 2, w, h, null);
    }

    private void drawWrapped(Graphics2D g, String text, int x, int y, int maxWidth, int lineHeight, int maxLines) {
        if (text == null) return;
        String[] words = text.split(" ");
        String line = "";
        int lineCount = 0;
        for (String word : words) {
            String test = line.isEmpty() ? word : line + " " + word;
            if (g.getFontMetrics().stringWidth(test) <= maxWidth) {
                line = test;
            } else {
                g.drawString(line, x, y + lineCount * lineHeight);
                lineCount++;
                if (lineCount >= maxLines) return;
                line = word;
            }
        }
        if (lineCount < maxLines && !line.isEmpty()) {
            g.drawString(line, x, y + lineCount * lineHeight);
        }
    }

    private String rewardText(ShopReward reward) {
        return switch (reward.getType()) {
            case ITEM -> reward.getQuantity() + "x " + reward.getItemClassName() + " (Tier " + reward.getTier() + ")";
            case LOOTBOX_TOKEN -> reward.getQuantity() + "x " + reward.getLootboxId().replace("_", " ");
            case CLOTHING_UNLOCK -> "Unlock clothing: " + reward.getClothingId();
            case CLOTHING_STYLE_UNLOCK -> "Unlock style: " + reward.getClothingId() + " - " + reward.getStyleId();
            case CASH -> "+" + reward.getAmount() + " Cash";
            case GEMS -> "+" + reward.getAmount() + " Gems";
        };
    }

    private void drawBundleRewardIcons(Graphics2D g, ShopEntry entry, int startX, int y, int maxWidth) {
        int size = 34;
        int gap = 8;
        int x = startX;
        for (ShopReward reward : entry.getRewards()) {
            if (x + size > startX + maxWidth) break;
            String iconPath = rewardIconPath(reward);
            if (iconPath == null) continue;
            BufferedImage icon = loadIcon(iconPath);
            if (icon == null) continue;
            g.setColor(new Color(255, 255, 255, 35));
            g.fillRoundRect(x - 2, y - 2, size + 4, size + 4, 8, 8);
            drawScaledImage(g, icon, x, y, size, size);
            x += size + gap;
        }
    }

    private String rewardIconPath(ShopReward reward) {
        return switch (reward.getType()) {
            case ITEM -> {
                Item item = createItem(reward.getItemClassName(), reward.getTier());
                yield item != null ? item.getIconPath() : null;
            }
            case LOOTBOX_TOKEN -> "assets/items/powers/infinity/infinity_icon.png";
            case CLOTHING_UNLOCK -> {
                Item item = createItem(reward.getClothingId(), 1);
                yield item != null ? item.getIconPath() : null;
            }
            case CLOTHING_STYLE_UNLOCK -> {
                Item item = createItem(reward.getClothingId(), 1);
                yield item != null ? item.getIconPath() : null;
            }
            case CASH -> "assets/items/powers/earth/earth_icon.png";
            case GEMS -> "assets/items/powers/infinity/infinity_icon.png";
        };
    }

    private void purchaseSelectedEntry() {
        if (selectedEntry == null) return;
        SaveData data = SaveManager.load();
        CurrencyManager cm = gamePanel.getCurrencyManager();

        if (cm.getCash().getAmount() < selectedEntry.getPriceCash() || cm.getGems().getAmount() < selectedEntry.getPriceGems()) {
            purchaseMessage = "Not enough currency.";
            return;
        }

        if (selectedEntry.getPriceCash() > 0 && !cm.spendCash(selectedEntry.getPriceCash())) {
            purchaseMessage = "Could not spend cash.";
            return;
        }
        if (selectedEntry.getPriceGems() > 0 && !cm.spendGems(selectedEntry.getPriceGems())) {
            // rollback cash if gem spend fails
            if (selectedEntry.getPriceCash() > 0) cm.addCash(selectedEntry.getPriceCash());
            purchaseMessage = "Could not spend gems.";
            return;
        }

        data.setCash(cm.getCash().getAmount());
        data.setGems(cm.getGems().getAmount());

        for (ShopReward reward : selectedEntry.getRewards()) {
            applyReward(data, reward);
        }
        SaveManager.save(data);
        purchaseMessage = "Purchased!";
    }

    private void applyReward(SaveData data, ShopReward reward) {
        switch (reward.getType()) {
            case CASH -> {
                data.setCash(data.getCash() + reward.getAmount());
                gamePanel.getCurrencyManager().addCash(reward.getAmount());
            }
            case GEMS -> {
                data.setGems(data.getGems() + reward.getAmount());
                gamePanel.getCurrencyManager().addGems(reward.getAmount());
            }
            case LOOTBOX_TOKEN -> data.addLootboxTokens(reward.getLootboxId(), Math.max(1, reward.getQuantity()));
            case CLOTHING_UNLOCK -> {
                Set<String> ids = new HashSet<>(data.getUnlockedClothingIds());
                ids.add(reward.getClothingId().toLowerCase());
                data.setUnlockedClothingIds(ids);
                gamePanel.getWardrobe().unlock(reward.getClothingId());
            }
            case CLOTHING_STYLE_UNLOCK -> {
                Map<String, Set<String>> styles = data.getUnlockedClothingStyles();
                // Keep exact-case key for style selector, plus lowercase for compatibility.
                styles.computeIfAbsent(reward.getClothingId(), k -> new HashSet<>()).add(reward.getStyleId());
                styles.computeIfAbsent(reward.getClothingId().toLowerCase(), k -> new HashSet<>()).add(reward.getStyleId());
            }
            case ITEM -> applyItemReward(data, reward);
        }
    }

    private void applyItemReward(SaveData data, ShopReward reward) {
        Item item = createItem(reward.getItemClassName(), reward.getTier());
        if (item == null) return;

        String itemName = item.getClass().getSimpleName();
        int quantity = Math.max(1, reward.getQuantity());

        if (item instanceof ClothingItem clothing) {
            Set<String> ids = new HashSet<>(data.getUnlockedClothingIds());
            ids.add(clothing.getName().toLowerCase());
            data.setUnlockedClothingIds(ids);
            gamePanel.getWardrobe().unlock(clothing.getName());
            return;
        }
        if (item instanceof Ranged || item instanceof Melee) {
            addTierUnlock(data.getUnlockedWeapons(), itemName, reward.getTier());
            incrementStack(data.getWeaponStacks(), itemName, reward.getTier(), quantity);
            return;
        }
        if (item instanceof Charm) {
            addTierUnlock(data.getUnlockedCharms(), itemName, reward.getTier());
            incrementStack(data.getCharmStacks(), itemName, reward.getTier(), quantity);
            return;
        }
        if (item instanceof Power) {
            addTierUnlock(data.getUnlockedPowers(), itemName, reward.getTier());
            incrementStack(data.getPowerStacks(), itemName, reward.getTier(), quantity);
            return;
        }
        if (item instanceof Summon) {
            addTierUnlock(data.getUnlockedSummons(), itemName, reward.getTier());
            incrementStack(data.getSummonStacks(), itemName, reward.getTier(), quantity);
            return;
        }
        if (item instanceof Consumable) {
            addTierUnlock(data.getUnlockedConsumables(), itemName, reward.getTier());
            incrementStack(data.getConsumableStacks(), itemName, reward.getTier(), quantity);
        }
    }

    private void addTierUnlock(Map<String, List<Integer>> unlockMap, String itemName, int tier) {
        List<Integer> tiers = new ArrayList<>(unlockMap.getOrDefault(itemName, new ArrayList<>()));
        if (!tiers.contains(tier)) {
            tiers.add(tier);
            tiers.sort(Integer::compareTo);
        }
        unlockMap.put(itemName, tiers);
    }

    private void incrementStack(Map<String, Map<Integer, Integer>> stackMap, String itemName, int tier, int amount) {
        Map<Integer, Integer> tierMap = stackMap.computeIfAbsent(itemName, k -> new HashMap<>());
        tierMap.put(tier, tierMap.getOrDefault(tier, 0) + amount);
    }

    private Item createItem(String className, int tier) {
        Class<?> cls = findItemClass(className);
        if (cls == null) return null;
        try {
            try {
                return (Item) cls.getConstructor(int.class).newInstance(tier);
            } catch (NoSuchMethodException ignore) {
                return (Item) cls.getConstructor().newInstance();
            }
        } catch (Exception e) {
            return null;
        }
    }

    private Class<?> findItemClass(String className) {
        String[] packages = {
                "combat.ranged.pistols.",
                "combat.ranged.rifles.",
                "combat.ranged.shotguns.",
                "combat.ranged.smgs.",
                "combat.ranged.snipers.",
                "combat.melee.swords.",
                "combat.melee.hammers.",
                "combat.melee.daggers.",
                "combat.melee.maces.",
                "combat.melee.scythes.",
                "combat.charms.",
                "combat.powers.",
                "combat.summons.",
                "combat.consumables.",
                "combat.clothing.tops.",
                "combat.clothing.bottoms.",
                "combat.clothing.accessories."
        };
        for (String pkg : packages) {
            try {
                return Class.forName(pkg + className);
            } catch (ClassNotFoundException ignored) {
            }
        }
        return null;
    }

    private record CardPlacement(ShopEntry entry, Rectangle rect) {}
}
