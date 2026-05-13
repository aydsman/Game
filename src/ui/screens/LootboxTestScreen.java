package ui.screens;

import combat.Item;
import combat.clothing.ClothingItem;
import combat.clothing.bottoms.Shorts;
import combat.clothing.tops.TShirt;
import combat.lootboxes.ClothesLootbox1;
import combat.lootboxes.LootBox;
import combat.lootboxes.LootboxCatalog;
import combat.lootboxes.LootboxDefinition;
import combat.lootboxes.Lootbox1;
import combat.lootboxes.FruitLootbox;
import combat.lootboxes.LootboxPowerOnly;
import currency.CurrencyManager;
import save.SaveData;
import save.SaveManager;
import ui.GamePanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Screen for testing loot boxes.
 * Shows a grid of rolls with configurable quantities (x1, x5, x10, x25, x100).
 * Displays item name, tier, and type for each rolled item.
 */
public class LootboxTestScreen {

    private GamePanel gamePanel;
    private LootBox selectedLootbox;
    private LootboxDefinition selectedDefinition;

    private static final int TOP_ROW_Y = 12;
    private static final int BTN_H = 38;
    private static final int H_GAP = 14;
    /** Cheat row: +1 token per crate, directly under Buy / Clear. */
    private static final int TOKEN_GRANT_ROW_Y = TOP_ROW_Y + BTN_H + 6;
    private static final int GRANT_BTN_H = 34;
    private static final int OPEN_ROW_Y = TOKEN_GRANT_ROW_Y + GRANT_BTN_H + 8;
    /** Top chrome panel height from y=6 (covers through Open row). */
    private static final int HEADER_PANEL_H = OPEN_ROW_Y + BTN_H - 6 + 10;
    /** Results grid top y (below header). */
    private static final int RESULTS_PANEL_TOP = 6 + HEADER_PANEL_H + 8;
    private static final int RESULTS_PANEL_H = 756 - (RESULTS_PANEL_TOP - 120);

    private Rectangle backBtn = new Rectangle(14, TOP_ROW_Y, 88, BTN_H);
    // Crate row: spaced after Back
    private Rectangle testCrateBtn = new Rectangle(120, TOP_ROW_Y, 126, BTN_H);
    private Rectangle clothesCrateBtn = new Rectangle(testCrateBtn.x + testCrateBtn.width + H_GAP, TOP_ROW_Y, 146, BTN_H);
    private Rectangle powerCrateBtn = new Rectangle(clothesCrateBtn.x + clothesCrateBtn.width + H_GAP, TOP_ROW_Y, 170, BTN_H);
    private Rectangle fruitCrateBtn = new Rectangle(powerCrateBtn.x + powerCrateBtn.width + H_GAP, TOP_ROW_Y, 150, BTN_H);
    // Buy / clear: packed from the right with gaps
    private Rectangle clearClothingBtn = new Rectangle(1600 - 24 - 300, TOP_ROW_Y, 300, BTN_H);
    private Rectangle clearItemsBtn = new Rectangle(clearClothingBtn.x - H_GAP - 210, TOP_ROW_Y, 210, BTN_H);
    private Rectangle buyFiveBtn = new Rectangle(clearItemsBtn.x - H_GAP - 108, TOP_ROW_Y, 108, BTN_H);
    private Rectangle buyOneBtn = new Rectangle(buyFiveBtn.x - H_GAP - 108, TOP_ROW_Y, 108, BTN_H);
    private final int grantBtnW = (clearClothingBtn.x + clearClothingBtn.width - buyOneBtn.x - 2 * H_GAP) / 3;
    private Rectangle grantTestTokensBtn = new Rectangle(buyOneBtn.x, TOKEN_GRANT_ROW_Y, grantBtnW, GRANT_BTN_H);
    private Rectangle grantClothesTokensBtn = new Rectangle(buyOneBtn.x + grantBtnW + H_GAP, TOKEN_GRANT_ROW_Y, grantBtnW, GRANT_BTN_H);
    private Rectangle grantPowerTokensBtn = new Rectangle(buyOneBtn.x + 2 * (grantBtnW + H_GAP), TOKEN_GRANT_ROW_Y, grantBtnW, GRANT_BTN_H);
    // Open row: uniform spacing from left (aligned under crate column)
    private static final int OPEN_W = 108;
    private Rectangle x1Btn = new Rectangle(114, OPEN_ROW_Y, OPEN_W, BTN_H);
    private Rectangle x5Btn = new Rectangle(x1Btn.x + x1Btn.width + H_GAP, OPEN_ROW_Y, OPEN_W, BTN_H);
    private Rectangle x10Btn = new Rectangle(x5Btn.x + x5Btn.width + H_GAP, OPEN_ROW_Y, OPEN_W, BTN_H);
    private Rectangle x25Btn = new Rectangle(x10Btn.x + x10Btn.width + H_GAP, OPEN_ROW_Y, OPEN_W, BTN_H);
    private Rectangle x100Btn = new Rectangle(x25Btn.x + x25Btn.width + H_GAP, OPEN_ROW_Y, OPEN_W, BTN_H);
    private String statusMessage = "";

    private List<LootResult> results = new ArrayList<>();

    public LootboxTestScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void handleClick(int x, int y) {
        if (backBtn.contains(x, y)) {
            gamePanel.switchScreen("menu");
        } else if (testCrateBtn.contains(x, y)) {
            selectedDefinition = LootboxCatalog.get(LootboxCatalog.TEST_CRATE_ID);
            selectedLootbox = new Lootbox1();
            results.clear();
        } else if (clothesCrateBtn.contains(x, y)) {
            selectedDefinition = LootboxCatalog.get(LootboxCatalog.CLOTHES_CRATE_1_ID);
            selectedLootbox = new ClothesLootbox1();
            results.clear();
        } else if (powerCrateBtn.contains(x, y)) {
            selectedDefinition = LootboxCatalog.get(LootboxCatalog.SHOP_EXCLUSIVE_POWER_CRATE_ID);
            selectedLootbox = LootboxCatalog.createLootbox(LootboxCatalog.SHOP_EXCLUSIVE_POWER_CRATE_ID);
            results.clear();
        } else if (fruitCrateBtn.contains(x, y)) {
            selectedDefinition = LootboxCatalog.get(LootboxCatalog.FRUIT_CRATE_ID);
            selectedLootbox = LootboxCatalog.createLootbox(LootboxCatalog.FRUIT_CRATE_ID);
            results.clear();
        } else if (selectedDefinition != null && buyOneBtn.contains(x, y)) {
            buySelectedLootbox(1);
        } else if (selectedDefinition != null && buyFiveBtn.contains(x, y)) {
            buySelectedLootbox(5);
        } else if (clearItemsBtn.contains(x, y)) {
            clearAllItemsInPossession();
        } else if (clearClothingBtn.contains(x, y)) {
            clearClothingExceptDefaults();
        } else if (grantTestTokensBtn.contains(x, y)) {
            grantLootboxTokens(LootboxCatalog.TEST_CRATE_ID, "Test Crate");
        } else if (grantClothesTokensBtn.contains(x, y)) {
            grantLootboxTokens(LootboxCatalog.CLOTHES_CRATE_1_ID, "Clothes Crate 1");
        } else if (grantPowerTokensBtn.contains(x, y)) {
            grantLootboxTokens(LootboxCatalog.SHOP_EXCLUSIVE_POWER_CRATE_ID, "Power Surge Crate");
        } else if (selectedLootbox != null && x1Btn.contains(x, y)) {
            rollLootbox(1);
        } else if (selectedLootbox != null && x5Btn.contains(x, y)) {
            rollLootbox(5);
        } else if (selectedLootbox != null && x10Btn.contains(x, y)) {
            rollLootbox(10);
        } else if (selectedLootbox != null && x25Btn.contains(x, y)) {
            rollLootbox(25);
        } else if (selectedLootbox != null && x100Btn.contains(x, y)) {
            rollLootbox(100);
        }
    }

    private void rollLootbox(int count) {
        if (selectedLootbox == null || selectedDefinition == null) return;
        SaveData data = SaveManager.load();
        if (!data.tryUseLootboxToken(selectedDefinition.getId(), count)) {
            statusMessage = "Not enough tokens.";
            return;
        }
        
        results.clear();
        int compensation = 0;
        for (int i = 0; i < count; i++) {
            Item item = selectedLootbox.open();
            if (item != null) {
                LootResult result = new LootResult(item);
                results.add(result);
                // Unlock the item in LoadoutScreen
                if (result.type.equals("Clothing")) {
                    if (isDuplicateClothingStyle(data, result.itemName, result.styleName)) {
                        int duplicateCash = selectedDefinition.duplicateCashCompensation();
                        compensation += duplicateCash;
                        data.setCash(data.getCash() + duplicateCash);
                        gamePanel.getCurrencyManager().addCash(duplicateCash);
                    } else {
                        unlockClothingStyle(data, result.itemName, result.styleName);
                    }
                    String styleInfo = result.styleName != null ? " (Style: " + result.styleName + ")" : "";
                    System.out.println("Unlocked clothing: " + result.itemName + styleInfo);
                } else {
                    gamePanel.getLoadoutScreen().unlockItem(result.type, result.itemName, result.tier);
                }
            }
        }
        SaveManager.save(data);
        statusMessage = compensation > 0 ? ("Opened x" + count + " - Duplicate compensation: $" + compensation) : ("Opened x" + count);
    }

    private void clearAllItemsInPossession() {
        gamePanel.getLoadoutScreen().resetOwnedInventoryForTesting();
        SaveData data = SaveManager.load();
        data.setUnlockedConsumables(new HashMap<>());
        data.setConsumableStacks(new HashMap<>());
        data.setLootboxTokens(new HashMap<>());
        SaveManager.save(data);
        results.clear();
        statusMessage = "Cleared all non-clothing possessions. Starter items restored.";
    }

    private void clearClothingExceptDefaults() {
        SaveData data = SaveManager.load();
        Set<String> defaults = new HashSet<>();
        defaults.add("tshirt");
        defaults.add("shorts");
        data.setUnlockedClothingIds(defaults);

        Map<String, Set<String>> defaultStyles = new HashMap<>();
        String tshirtDefault = new TShirt().getDefaultStyle();
        String shortsDefault = new Shorts().getDefaultStyle();
        if (tshirtDefault != null && !tshirtDefault.isEmpty()) {
            Set<String> styles = new HashSet<>();
            styles.add(tshirtDefault);
            defaultStyles.put("TShirt", new HashSet<>(styles));
            defaultStyles.put("tshirt", new HashSet<>(styles));
        }
        if (shortsDefault != null && !shortsDefault.isEmpty()) {
            Set<String> styles = new HashSet<>();
            styles.add(shortsDefault);
            defaultStyles.put("Shorts", new HashSet<>(styles));
            defaultStyles.put("shorts", new HashSet<>(styles));
        }
        data.setUnlockedClothingStyles(defaultStyles);
        data.setEquippedClothingStyles(new HashMap<>());
        SaveManager.save(data);

        gamePanel.getWardrobe().setUnlockedClothingIds(defaults);
        results.clear();
        statusMessage = "Cleared clothing unlocks/styles. Kept default clothing only.";
    }

    private void grantLootboxTokens(String lootboxId, String displayLabel) {
        SaveData data = SaveManager.load();
        data.addLootboxTokens(lootboxId, 1);
        SaveManager.save(data);
        statusMessage = "+1 token: " + displayLabel;
    }

    private void buySelectedLootbox(int quantity) {
        if (selectedDefinition == null || quantity <= 0) return;
        CurrencyManager cm = gamePanel.getCurrencyManager();
        int cashCost = selectedDefinition.getPriceCash() * quantity;
        int gemCost = selectedDefinition.getPriceGems() * quantity;

        if (cm.getCash().getAmount() < cashCost || cm.getGems().getAmount() < gemCost) {
            statusMessage = "Not enough currency.";
            return;
        }
        if (cashCost > 0 && !cm.spendCash(cashCost)) {
            statusMessage = "Cash spend failed.";
            return;
        }
        if (gemCost > 0 && !cm.spendGems(gemCost)) {
            if (cashCost > 0) cm.addCash(cashCost);
            statusMessage = "Gem spend failed.";
            return;
        }
        SaveData data = SaveManager.load();
        data.setCash(cm.getCash().getAmount());
        data.setGems(cm.getGems().getAmount());
        data.addLootboxTokens(selectedDefinition.getId(), quantity);
        SaveManager.save(data);
        statusMessage = "Bought x" + quantity + " " + selectedDefinition.getDisplayName();
    }

    private boolean isDuplicateClothingStyle(SaveData data, String clothingName, String styleName) {
        if (styleName == null || styleName.isEmpty()) return false;
        return data.getUnlockedClothingStyles().getOrDefault(clothingName, new java.util.HashSet<>()).contains(styleName)
                || data.getUnlockedClothingStyles().getOrDefault(clothingName.toLowerCase(), new java.util.HashSet<>()).contains(styleName);
    }

    private void unlockClothingStyle(SaveData data, String clothingName, String styleName) {
        gamePanel.getWardrobe().unlock(clothingName);
        data.setUnlockedClothingIds(gamePanel.getWardrobe().getUnlockedClothingIds());
        data.getUnlockedClothingStyles().computeIfAbsent(clothingName, k -> new java.util.HashSet<>()).add(styleName);
        data.getUnlockedClothingStyles().computeIfAbsent(clothingName.toLowerCase(), k -> new java.util.HashSet<>()).add(styleName);
    }

    public void draw(Graphics2D g) {
        Object oldAa = g.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        GradientPaint bg = new GradientPaint(0, 0, new Color(20, 24, 38), 0, 900, new Color(10, 12, 20));
        g.setPaint(bg);
        g.fillRect(0, 0, 1600, 900);
        g.setPaint(null);
        g.setColor(new Color(255, 255, 255, 8));
        for (int x = 0; x < 1600; x += 32) g.drawLine(x, 0, x, 900);
        for (int y = 0; y < 900; y += 32) g.drawLine(0, y, 1600, y);

        g.setColor(new Color(0, 0, 0, 110));
        g.fillRoundRect(8, 6, 1584, HEADER_PANEL_H, 14, 14);
        g.setColor(new Color(95, 125, 170));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(8, 6, 1584, HEADER_PANEL_H, 14, 14);
        g.setStroke(new BasicStroke(1));

        drawButton(g, backBtn, "Back", new Color(75, 85, 110), true);
        
        // Lootbox selection buttons
        drawLootboxSelectionButton(g, testCrateBtn, "Test Crate", "test", new Color(95, 140, 215));
        drawLootboxSelectionButton(g, clothesCrateBtn, "Clothes Crate 1", "clothes", new Color(185, 120, 235));
        drawLootboxSelectionButton(g, powerCrateBtn, "Power Surge Crate", "power", new Color(255, 170, 75));
        drawLootboxSelectionButton(g, fruitCrateBtn, "Fruit Crate", "fruit", new Color(150, 220, 120));

        SaveData data = SaveManager.load();
        CurrencyManager cm = gamePanel.getCurrencyManager();
        drawPurchaseButton(g, buyOneBtn, "Buy x1", selectedDefinition != null, new Color(65, 130, 210));
        drawPurchaseButton(g, buyFiveBtn, "Buy x5", selectedDefinition != null, new Color(65, 130, 210));
        drawPurchaseButton(g, clearItemsBtn, "Clear All Items", true, new Color(170, 85, 85));
        drawPurchaseButton(g, clearClothingBtn, "Clear Clothing + Styles", true, new Color(150, 95, 165));

        Color grantBase = new Color(55, 115, 108);
        drawButton(g, grantTestTokensBtn, "+1 Test", grantBase, true);
        drawButton(g, grantClothesTokensBtn, "+1 Clothes", grantBase, true);
        drawButton(g, grantPowerTokensBtn, "+1 Power", grantBase, true);

        // Roll buttons (disabled if no lootbox selected)
        boolean canRoll = selectedLootbox != null;
        drawRollButton(g, x1Btn, "Open x1", canRoll);
        drawRollButton(g, x5Btn, "Open x5", canRoll);
        drawRollButton(g, x10Btn, "Open x10", canRoll);
        drawRollButton(g, x25Btn, "Open x25", canRoll);
        drawRollButton(g, x100Btn, "Open x100", canRoll);

        // Tokens / price in the gap before Buy (drawn after roll buttons so text stays visible)
        if (selectedDefinition != null) {
            g.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            int tokens = data.getLootboxTokenCount(selectedDefinition.getId());
            String tokStr = "Tokens: " + tokens;
            String priceText = selectedDefinition.getPriceCash() > 0
                    ? ("Price: $" + selectedDefinition.getPriceCash())
                    : ("Price: ◆" + selectedDefinition.getPriceGems());
            int right = buyOneBtn.x - 10;
            int minLeft = x100Btn.x + x100Btn.width + 8;
            int priceX = right - fm.stringWidth(priceText);
            int tokX = right - fm.stringWidth(tokStr);
            int lineTokY = OPEN_ROW_Y + 16;
            int linePriceY = OPEN_ROW_Y + 32;
            if (priceX < minLeft || tokX < minLeft) {
                g.setFont(new Font("Segoe UI", Font.BOLD, 13));
                fm = g.getFontMetrics();
                priceX = right - fm.stringWidth(priceText);
                tokX = right - fm.stringWidth(tokStr);
                linePriceY = OPEN_ROW_Y + 31;
                lineTokY = OPEN_ROW_Y + 15;
            }
            g.drawString(tokStr, Math.max(minLeft, tokX), lineTokY);
            g.drawString(priceText, Math.max(minLeft, priceX), linePriceY);
        }

        // Cash / gems strip (after buttons)
        drawLootboxStatusStrip(g, cm);

        // Draw results grid
        drawResultsGrid(g);

        if (oldAa != null) {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, oldAa);
        } else {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
        }
    }

    /** Cash + gems after header buttons (no prompt text — avoids overlap). */
    private void drawLootboxStatusStrip(Graphics2D g, CurrencyManager cm) {
        int pillX = powerCrateBtn.x + powerCrateBtn.width + H_GAP;
        int pillW = Math.max(120, buyOneBtn.x - pillX - H_GAP);
        int pillY = TOP_ROW_Y;
        int pillH = BTN_H;

        g.setColor(new Color(8, 10, 18, 230));
        g.fillRoundRect(pillX, pillY, pillW, pillH, 8, 8);
        g.setColor(new Color(70, 95, 130));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(pillX, pillY, pillW, pillH, 8, 8);
        g.setStroke(new BasicStroke(1f));

        Font curFont = new Font("Segoe UI", Font.BOLD, 12);
        g.setFont(curFont);
        FontMetrics fmCur = g.getFontMetrics();
        String cashStr = "Cash $" + cm.getCash().getAmount();
        String gemStr = "Gems ◆" + cm.getGems().getAmount();
        int rightPad = 10;
        int cy = pillY + (pillH + fmCur.getAscent() - fmCur.getDescent()) / 2;

        int gemX = pillX + pillW - rightPad - fmCur.stringWidth(gemStr);
        int cashX = gemX - 14 - fmCur.stringWidth(cashStr);
        drawShadowedString(g, cashStr, cashX, cy, new Color(200, 255, 220));
        drawShadowedString(g, gemStr, gemX, cy, new Color(210, 235, 255));
    }

    private static void drawShadowedString(Graphics2D g, String s, int x, int y, Color c) {
        g.setColor(new Color(0, 0, 0, 200));
        g.drawString(s, x + 1, y + 1);
        g.setColor(c);
        g.drawString(s, x, y);
    }

    private void drawRollButton(Graphics2D g, Rectangle btn, String label, boolean enabled) {
        Color base = enabled ? new Color(75, 170, 95) : new Color(60, 60, 65);
        drawButton(g, btn, label, base, enabled);
    }
    
    private void drawLootboxSelectionButton(Graphics2D g, Rectangle btn, String label, String type, Color selectedColor) {
        boolean isSelected = (type.equals("test") && selectedLootbox instanceof Lootbox1) ||
                            (type.equals("clothes") && selectedLootbox instanceof ClothesLootbox1) ||
                            (type.equals("power") && selectedLootbox instanceof LootboxPowerOnly) ||
                            (type.equals("fruit") && selectedLootbox instanceof FruitLootbox);
        Color base = isSelected ? selectedColor : new Color(72, 78, 95);
        drawButton(g, btn, label, base, true);
    }

    private void drawResultsGrid(Graphics2D g) {
        final int panelX = 40;
        final int panelTop = RESULTS_PANEL_TOP;
        final int panelW = 1520;
        final int panelH = RESULTS_PANEL_H;

        int gridStartX = 60;
        int cellWidth = 360;
        int cellHeight = 54;
        int cellSpacingX = 18;
        int cellSpacingY = 10;
        int cellsPerRow = 4;

        g.setColor(new Color(0, 0, 0, 140));
        g.fillRoundRect(panelX, panelTop, panelW, panelH, 14, 14);
        g.setColor(new Color(105, 130, 170));
        g.drawRoundRect(panelX, panelTop, panelW, panelH, 14, 14);

        Font headerFont = new Font("Segoe UI", Font.BOLD, 15);
        g.setFont(headerFont);
        FontMetrics hfm = g.getFontMetrics();
        int headerPadTop = 14;
        int headerBaseline = panelTop + headerPadTop + hfm.getAscent();
        int gapBelowHeader = 12;
        int gridStartY = headerBaseline + hfm.getDescent() + gapBelowHeader;

        g.setColor(new Color(200, 228, 255));
        g.drawString("Item Name", gridStartX + 10, headerBaseline);
        g.drawString("Tier", gridStartX + 150, headerBaseline);
        g.drawString("Type", gridStartX + 200, headerBaseline);
        g.drawString("Style", gridStartX + 260, headerBaseline);

        g.setColor(new Color(65, 78, 105));
        int sepY = gridStartY - (gapBelowHeader / 2);
        g.drawLine(panelX + 16, sepY, panelX + panelW - 16, sepY);

        // Draw results
        for (int i = 0; i < results.size(); i++) {
            int row = i / cellsPerRow;
            int col = i % cellsPerRow;
            int x = gridStartX + col * (cellWidth + cellSpacingX);
            int y = gridStartY + row * (cellHeight + cellSpacingY);

            // Bounds check
            if (y + cellHeight > 880) break;

            LootResult result = results.get(i);

            // Cell background
            GradientPaint rowFill = new GradientPaint(x, y, new Color(53, 58, 74), x, y + cellHeight, new Color(40, 44, 58));
            g.setPaint(rowFill);
            g.fillRoundRect(x, y, cellWidth, cellHeight, 8, 8);
            g.setPaint(null);
            g.setColor(new Color(90, 98, 118));
            g.drawRoundRect(x, y, cellWidth, cellHeight, 8, 8);

            // Text
            g.setColor(Color.WHITE);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            // Item name
            String name = result.itemName;
            if (name.length() > 15) {
                name = name.substring(0, 12) + "...";
            }
            g.drawString(name, x + 10, y + 18);

            // Tier and Type
            String tierAndType = "T" + result.tier + " | " + result.type;
            g.drawString(tierAndType, x + 10, y + 35);
            if (result.styleName != null) {
                g.setColor(new Color(205, 180, 255));
                g.drawString(result.styleName, x + 220, y + 35);
            }
        }

        // Summary footer inside panel (avoid overlap)
        int footBaseline = panelTop + panelH - 14;
        if (!statusMessage.isEmpty()) {
            g.setFont(new Font("Segoe UI", Font.BOLD, 13));
            FontMetrics sfm = g.getFontMetrics();
            String msg = statusMessage;
            int maxW = panelW - 80;
            if (sfm.stringWidth(msg) > maxW) {
                while (msg.length() > 3 && sfm.stringWidth(msg + "...") > maxW) {
                    msg = msg.substring(0, msg.length() - 1);
                }
                msg = msg + "...";
            }
            int sy = footBaseline - (!results.isEmpty() ? 22 : 0);
            g.setColor(new Color(0, 0, 0, 180));
            g.drawString(msg, gridStartX + 11, sy + 1);
            g.setColor(new Color(255, 230, 140));
            g.drawString(msg, gridStartX + 10, sy);
        }
        if (!results.isEmpty()) {
            g.setColor(new Color(180, 220, 255));
            g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g.drawString("Total rolls: " + results.size(), gridStartX, footBaseline);
        }
    }

    private void drawPurchaseButton(Graphics2D g, Rectangle btn, String label, boolean enabled, Color baseColor) {
        Color base = enabled ? baseColor : new Color(60, 60, 60);
        drawButton(g, btn, label, base, enabled);
    }

    private void drawButton(Graphics2D g, Rectangle btn, String label, Color baseColor, boolean enabled) {
        Color top = enabled ? baseColor.brighter() : new Color(75, 75, 75);
        GradientPaint gradient = new GradientPaint(btn.x, btn.y, top, btn.x, btn.y + btn.height, baseColor.darker());
        g.setPaint(gradient);
        g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 10, 10);
        g.setPaint(null);
        g.setColor(enabled ? new Color(225, 235, 250) : new Color(130, 130, 130));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 10, 10);
        g.setStroke(new BasicStroke(1f));

        g.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g.setColor(enabled ? Color.WHITE : new Color(150, 150, 150));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, btn.x + (btn.width - fm.stringWidth(label)) / 2,
                btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2);
    }

    /**
     * Inner class to store loot results
     */
    private static class LootResult {
        String itemName;
        int tier;
        String type;
        String styleName;

        LootResult(Item item) {
            this.itemName = item.getClass().getSimpleName(); // Use class name, not display name
            this.tier = item.getTier();
            this.type = getItemType(item);
            this.styleName = null;

            // Get style name for clothing items
            if (item instanceof ClothingItem clothing) {
                this.styleName = clothing.getSelectedStyle();
            }
        }

        private static String getItemType(Item item) {
            String className = item.getClass().getName().toLowerCase();

            // Check class hierarchy and package names (case-insensitive)
            if (className.contains("ranged") || className.contains("melee") ||
                className.contains("pistol") || className.contains("rifle") ||
                className.contains("shotgun") || className.contains("smg") ||
                className.contains("sniper") || className.contains("sword") ||
                className.contains("hammer") || className.contains("dagger") ||
                className.contains("mace") || className.contains("scythe")) {
                return "Weapon";
            } else if (className.contains("charm")) {
                return "Charm";
            } else if (className.contains("power")) {
                return "Power";
            } else if (className.contains("summon")) {
                return "Summon";
            } else if (className.contains("consumable") || className.contains("potion")) {
                return "Consumable";
            } else if (className.contains("clothing") || className.contains("tops") ||
                className.contains("bottoms") || className.contains("accessories")) {
            return "Clothing";
        }
            return "Unknown";
        }
    }
}

