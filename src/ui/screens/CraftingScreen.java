package ui.screens;

import combat.Item;
import crafting.CraftingEvaluation;
import crafting.CraftingIngredient;
import crafting.CraftingItemType;
import crafting.CraftingRecipe;
import crafting.CraftingService;
import save.SaveData;
import save.SaveManager;
import ui.GamePanel;
import util.MouseHandler;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class CraftingScreen {
    private final GamePanel gamePanel;

    private final Rectangle backBtn = new Rectangle(24, 24, 130, 42);
    private final Rectangle listBounds = new Rectangle(60, 120, 1480, 730);
    private final Rectangle scrollbarTrack = new Rectangle(1510, 140, 18, 690);
    private Rectangle scrollbarThumb = new Rectangle(1510, 140, 18, 120);
    private static final int CARD_COLUMNS = 2;
    private static final int CARD_HEIGHT = 86;
    private static final int CARD_GAP_X = 16;
    private static final int CARD_GAP_Y = 12;

    private final List<RecipeCardPlacement> placements = new ArrayList<>();
    private int scrollOffset = 0;
    private int contentHeight = 0;

    private int hoverX = -1;
    private int hoverY = -1;

    private boolean draggingScrollbar = false;
    private int dragThumbOffsetY = 0;

    private CraftingRecipe selectedRecipe = null;
    private Rectangle modalRect = new Rectangle();
    private Rectangle modalCraftBtn = new Rectangle();
    private Rectangle modalCloseBtn = new Rectangle();
    private String statusMessage = "";
    private final Map<String, Image> iconCache = new HashMap<>();

    public CraftingScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        rebuildRecipePlacements();
    }

    public void refreshFromSave() {
        rebuildRecipePlacements();
        selectedRecipe = null;
        statusMessage = "";
        clampScroll();
    }

    public void update(MouseHandler mouse) {
        if (!draggingScrollbar) {
            return;
        }
        if (!mouse.leftPressed) {
            draggingScrollbar = false;
            return;
        }
        int minY = scrollbarTrack.y;
        int maxY = scrollbarTrack.y + scrollbarTrack.height - scrollbarThumb.height;
        int thumbY = mouse.mouseY - dragThumbOffsetY;
        thumbY = Math.max(minY, Math.min(maxY, thumbY));
        scrollbarThumb.y = thumbY;
        float t = (float) (thumbY - minY) / Math.max(1, maxY - minY);
        int maxOffset = Math.max(0, contentHeight - listBounds.height + 16);
        scrollOffset = (int) (maxOffset * t);
    }

    public void handleMouseMove(int x, int y) {
        hoverX = x;
        hoverY = y;
    }

    public void handleMouseScroll(int wheelRotation) {
        int maxOffset = Math.max(0, contentHeight - listBounds.height + 16);
        scrollOffset += wheelRotation * 42;
        if (scrollOffset < 0) {
            scrollOffset = 0;
        }
        if (scrollOffset > maxOffset) {
            scrollOffset = maxOffset;
        }
        syncScrollbarFromScroll();
    }

    public void handleClick(int x, int y) {
        hoverX = x;
        hoverY = y;

        if (selectedRecipe != null) {
            handleModalClick(x, y);
            return;
        }

        if (backBtn.contains(x, y)) {
            gamePanel.switchScreen(gamePanel.getPreviousScreen());
            return;
        }

        if (scrollbarThumb.contains(x, y)) {
            draggingScrollbar = true;
            dragThumbOffsetY = y - scrollbarThumb.y;
            return;
        }

        for (RecipeCardPlacement placement : placements) {
            Rectangle onScreen = toScreenRect(placement.bounds);
            if (!isVisibleInList(onScreen)) {
                continue;
            }
            if (onScreen.contains(x, y)) {
                selectedRecipe = placement.recipe;
                statusMessage = "";
                return;
            }
        }
    }

    private void handleModalClick(int x, int y) {
        if (!modalRect.contains(x, y)) {
            selectedRecipe = null;
            return;
        }
        if (modalCloseBtn.contains(x, y)) {
            selectedRecipe = null;
            return;
        }
        if (!modalCraftBtn.contains(x, y)) {
            return;
        }

        SaveData data = SaveManager.load();
        crafting.CraftingResult result = CraftingService.craft(data, gamePanel.getCurrencyManager(), selectedRecipe);
        statusMessage = result.getMessage();
        if (result.isSuccess()) {
            SaveManager.save(data);
            rebuildRecipePlacements();
            gamePanel.syncLoadoutFromSave();
        }
    }

    public void draw(Graphics2D g, int width, int height) {
        drawBackground(g, width, height);
        drawHeader(g, width);
        drawRecipeList(g);
        drawScrollbar(g);

        if (selectedRecipe != null) {
            drawModalOverlay(g, width, height);
            drawRecipeModal(g, width, height);
        }
    }

    private void drawBackground(Graphics2D g, int width, int height) {
        GradientPaint bg = new GradientPaint(0, 0, new Color(25, 26, 35), 0, height, new Color(15, 16, 24));
        g.setPaint(bg);
        g.fillRect(0, 0, width, height);
        g.setPaint(null);

        g.setColor(new Color(255, 255, 255, 8));
        for (int x = 0; x < width; x += 40) {
            g.drawLine(x, 0, x, height);
        }
        for (int y = 0; y < height; y += 40) {
            g.drawLine(0, y, width, y);
        }
    }

    private void drawHeader(Graphics2D g, int width) {
        g.setColor(new Color(0, 0, 0, 130));
        g.fillRect(0, 0, width, 92);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 34));
        g.drawString("Crafting", 180, 58);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        g.setColor(new Color(190, 200, 220));
        g.drawString("Select a recipe and confirm crafting. Recipes you can craft are listed first.", 180, 82);

        boolean hover = backBtn.contains(hoverX, hoverY);
        drawButton(g, backBtn, "Back", new Color(70, 82, 108), new Color(105, 130, 170), hover);
    }

    private void drawRecipeList(Graphics2D g) {
        g.setColor(new Color(18, 20, 28, 210));
        g.fillRoundRect(listBounds.x, listBounds.y, listBounds.width, listBounds.height, 16, 16);
        g.setColor(new Color(100, 170, 230));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(listBounds.x, listBounds.y, listBounds.width, listBounds.height, 16, 16);
        g.setStroke(new BasicStroke(1));

        if (placements.isEmpty()) {
            g.setColor(new Color(190, 200, 220));
            g.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            String line1 = "No crafting recipes added yet.";
            String line2 = "Part 2 will plug recipes into this screen.";
            FontMetrics fm = g.getFontMetrics();
            int x1 = listBounds.x + (listBounds.width - fm.stringWidth(line1)) / 2;
            int x2 = listBounds.x + (listBounds.width - fm.stringWidth(line2)) / 2;
            int y = listBounds.y + listBounds.height / 2;
            g.drawString(line1, x1, y - 10);
            g.drawString(line2, x2, y + 26);
            return;
        }

        java.awt.Shape oldClip = g.getClip();
        g.setClip(listBounds.x + 2, listBounds.y + 2, listBounds.width - 22, listBounds.height - 4);
        for (RecipeCardPlacement placement : placements) {
            Rectangle onScreen = toScreenRect(placement.bounds);
            drawRecipeCard(g, placement, onScreen);
        }
        g.setClip(oldClip);
    }

    private void drawRecipeCard(Graphics2D g, RecipeCardPlacement placement, Rectangle card) {
        boolean hover = card.contains(hoverX, hoverY);
        int yOffset = hover ? -2 : 0;
        Rectangle r = new Rectangle(card.x, card.y + yOffset, card.width, card.height);
        CraftingRecipe recipe = placement.recipe;
        CraftingEvaluation evaluation = placement.evaluation;
        boolean craftable = evaluation.isCraftable();

        int nonCraftAlpha = hover ? 205 : 165;
        Color accent = craftable
                ? new Color(96, 225, 140)
                : new Color(86, 94, 112, hover ? 205 : 170);
        GradientPaint fill = craftable
                ? new GradientPaint(r.x, r.y, new Color(45, 48, 66), r.x, r.y + r.height, new Color(32, 35, 48))
                : new GradientPaint(
                        r.x, r.y, new Color(28, 30, 38, nonCraftAlpha),
                        r.x, r.y + r.height, new Color(20, 22, 30, nonCraftAlpha - 8));
        g.setPaint(fill);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 14, 14);
        g.setPaint(null);

        g.setColor(accent);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(r.x, r.y, r.width, r.height, 14, 14);
        g.setStroke(new BasicStroke(1));

        int iconSize = 44;
        int iconX = r.x + 12;
        int iconY = r.y + (r.height - iconSize) / 2;
        drawOutputIcon(g, recipe, iconX, iconY, iconSize, iconSize);

        int textX = iconX + iconSize + 12;
        int textWidth = r.width - (textX - r.x) - 14;

        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g.setColor(craftable ? Color.WHITE : new Color(185, 192, 206, hover ? 220 : 188));
        String title = recipe.getDisplayName();
        FontMetrics tfm = g.getFontMetrics();
        if (tfm.stringWidth(title) > textWidth) {
            while (title.length() > 4 && tfm.stringWidth(title + "...") > textWidth) {
                title = title.substring(0, title.length() - 1);
            }
            title = title + "...";
        }
        g.drawString(title, textX, r.y + 26);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g.setColor(craftable ? new Color(185, 196, 216) : new Color(130, 140, 160, hover ? 212 : 180));
        String desc = recipe.getDescription() == null ? "" : recipe.getDescription();
        while (desc.length() > 3 && g.getFontMetrics().stringWidth(desc + "...") > textWidth) {
            desc = desc.substring(0, desc.length() - 1);
        }
        if (g.getFontMetrics().stringWidth(desc) > textWidth) {
            desc = desc + "...";
        }
        g.drawString(desc, textX, r.y + 46);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.setColor(craftable ? new Color(205, 215, 235) : new Color(140, 150, 170, hover ? 212 : 182));
        String output = recipe.getOutput().getItemId() + " (T" + recipe.getOutput().getTier() + ") x" + recipe.getOutput().getQuantity();
        g.drawString("Output: " + output, textX, r.y + 66);

        String status;
        Color statusColor;
        if (evaluation.isCraftable()) {
            status = "CAN CRAFT";
            statusColor = new Color(96, 225, 140);
        } else if (!evaluation.isAffordable()) {
            status = "COST LOCK";
            statusColor = new Color(235, 172, 75);
        } else if (evaluation.isBlockedByLoadout()) {
            status = "LOADOUT LOCK";
            statusColor = new Color(255, 140, 140);
        } else {
            status = "NEEDS MATS";
            statusColor = new Color(255, 140, 140);
        }
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.setColor(statusColor);
        FontMetrics sfm = g.getFontMetrics();
        g.drawString(status, r.x + r.width - sfm.stringWidth(status) - 12, r.y + 26);
    }

    private void drawScrollbar(Graphics2D g) {
        g.setColor(new Color(35, 40, 55));
        g.fillRoundRect(scrollbarTrack.x, scrollbarTrack.y, scrollbarTrack.width, scrollbarTrack.height, 9, 9);
        g.setColor(new Color(90, 110, 150));
        g.drawRoundRect(scrollbarTrack.x, scrollbarTrack.y, scrollbarTrack.width, scrollbarTrack.height, 9, 9);

        if (contentHeight <= listBounds.height) {
            return;
        }

        Color thumb = scrollbarThumb.contains(hoverX, hoverY) || draggingScrollbar
                ? new Color(120, 170, 240)
                : new Color(95, 130, 190);
        g.setColor(thumb);
        g.fillRoundRect(scrollbarThumb.x, scrollbarThumb.y, scrollbarThumb.width, scrollbarThumb.height, 9, 9);
    }

    private void drawModalOverlay(Graphics2D g, int width, int height) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, width, height);
    }

    private void drawRecipeModal(Graphics2D g, int width, int height) {
        int mw = 980;
        int mh = 560;
        int mx = (width - mw) / 2;
        int my = (height - mh) / 2;
        modalRect = new Rectangle(mx, my, mw, mh);

        g.setColor(new Color(20, 22, 30, 238));
        g.fillRoundRect(mx, my, mw, mh, 18, 18);
        g.setColor(new Color(120, 200, 255));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(mx, my, mw, mh, 18, 18);
        g.setStroke(new BasicStroke(1));

        CraftingEvaluation eval = CraftingService.evaluate(SaveManager.load(), selectedRecipe, gamePanel.getCurrencyManager());
        int leftX = mx + 30;
        int rightPanelW = 280;
        int rightPanelX = mx + mw - rightPanelW - 30;
        int leftContentWidth = rightPanelX - leftX - 24;

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 30));
        g.drawString(selectedRecipe.getDisplayName(), leftX, my + 52);

        g.setColor(new Color(188, 198, 218));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        drawWrapped(g, selectedRecipe.getDescription(), leftX, my + 82, leftContentWidth, 24);

        g.setColor(new Color(210, 220, 240));
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g.drawString("Ingredients", leftX, my + 170);
        int lineY = my + 200;
        g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        for (CraftingIngredient ingredient : selectedRecipe.getIngredients()) {
            int owned = getAvailableCount(SaveManager.load(), ingredient);
            String line = "- " + ingredient.getItemId() + " (T" + ingredient.getTier() + ") x" + ingredient.getQuantity()
                    + "  | owned: " + owned;
            g.setColor(owned >= ingredient.getQuantity() ? new Color(130, 235, 150) : new Color(255, 150, 150));
            g.drawString(line, leftX + 6, lineY);
            lineY += 26;
        }

        lineY += 20;
        g.setColor(new Color(210, 220, 240));
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g.drawString("Output", leftX, lineY);
        int outputLineY = lineY + 34;
        g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        String output = selectedRecipe.getOutput().getItemId() + " (T" + selectedRecipe.getOutput().getTier() + ") x"
                + selectedRecipe.getOutput().getQuantity();
        g.setColor(new Color(130, 225, 255));
        int outputIconSize = 40;
        int outputIconX = leftX + 4;
        int outputIconY = outputLineY - 30;
        drawOutputIcon(g, selectedRecipe, outputIconX, outputIconY, outputIconSize, outputIconSize);
        g.drawString(output, outputIconX + outputIconSize + 12, outputLineY);

        int costY = outputLineY + 44;
        g.setColor(new Color(210, 220, 240));
        g.setFont(new Font("Segoe UI", Font.BOLD, 17));
        g.drawString(
                "Cost: Gold " + selectedRecipe.getGoldCost() + "  |  Cash " + selectedRecipe.getCashCost() + "  |  Gems " + selectedRecipe.getGemsCost(),
                leftX,
                costY);

        // Right-side result preview panel with a larger crafted-item icon.
        int previewY = my + 92;
        int previewH = 324;
        g.setColor(new Color(28, 32, 45, 230));
        g.fillRoundRect(rightPanelX, previewY, rightPanelW, previewH, 16, 16);
        g.setColor(new Color(95, 145, 205));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(rightPanelX, previewY, rightPanelW, previewH, 16, 16);
        g.setStroke(new BasicStroke(1));

        g.setColor(new Color(210, 220, 240));
        g.setFont(new Font("Segoe UI", Font.BOLD, 19));
        g.drawString("Result Preview", rightPanelX + 18, previewY + 34);

        int bigIconSize = 188;
        int bigIconX = rightPanelX + (rightPanelW - bigIconSize) / 2;
        int bigIconY = previewY + 56;
        drawOutputIcon(g, selectedRecipe, bigIconX, bigIconY, bigIconSize, bigIconSize);

        g.setColor(new Color(145, 220, 255));
        g.setFont(new Font("Segoe UI", Font.BOLD, 17));
        String resultLabel = selectedRecipe.getOutput().getItemId();
        FontMetrics resultFm = g.getFontMetrics();
        int resultX = rightPanelX + (rightPanelW - resultFm.stringWidth(resultLabel)) / 2;
        g.drawString(resultLabel, resultX, bigIconY + bigIconSize + 34);

        g.setColor(new Color(185, 198, 220));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        String tierQty = "Tier " + selectedRecipe.getOutput().getTier() + "  x" + selectedRecipe.getOutput().getQuantity();
        FontMetrics tqFm = g.getFontMetrics();
        int tqX = rightPanelX + (rightPanelW - tqFm.stringWidth(tierQty)) / 2;
        g.drawString(tierQty, tqX, bigIconY + bigIconSize + 58);

        modalCraftBtn = new Rectangle(mx + 30, my + mh - 72, 230, 44);
        modalCloseBtn = new Rectangle(mx + mw - 260, my + mh - 72, 230, 44);

        boolean craftHover = modalCraftBtn.contains(hoverX, hoverY);
        boolean closeHover = modalCloseBtn.contains(hoverX, hoverY);

        String craftLabel = eval.isCraftable() ? "Craft" : "Cannot Craft";
        Color craftBase = eval.isCraftable() ? new Color(70, 170, 105) : new Color(80, 90, 105);
        Color craftHoverColor = eval.isCraftable() ? new Color(90, 205, 130) : new Color(100, 110, 125);
        drawButton(g, modalCraftBtn, craftLabel, craftBase, craftHoverColor, craftHover);
        drawButton(g, modalCloseBtn, "Close", new Color(70, 85, 110), new Color(110, 135, 175), closeHover);

        if (!statusMessage.isEmpty()) {
            g.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g.setColor(eval.isCraftable() ? new Color(130, 255, 170) : new Color(255, 180, 140));
            g.drawString(statusMessage, mx + 290, my + mh - 44);
        }
    }

    private int getAvailableCount(SaveData data, CraftingIngredient ingredient) {
        return CraftingService.getAvailableCount(data, ingredient);
    }

    private void rebuildRecipePlacements() {
        placements.clear();
        SaveData data = SaveManager.load();
        List<CraftingRecipe> recipes = CraftingService.getRecipesSortedByCraftability(data, gamePanel.getCurrencyManager());
        int innerX = listBounds.x + 16;
        int innerY = listBounds.y + 16;
        int innerW = listBounds.width - 32;
        int cardW = (innerW - CARD_GAP_X) / CARD_COLUMNS;

        int index = 0;
        int maxY = innerY;
        for (CraftingRecipe recipe : recipes) {
            CraftingEvaluation evaluation = CraftingService.evaluate(data, recipe, gamePanel.getCurrencyManager());
            int col = index % CARD_COLUMNS;
            int row = index / CARD_COLUMNS;
            int x = innerX + col * (cardW + CARD_GAP_X);
            int y = innerY + row * (CARD_HEIGHT + CARD_GAP_Y);
            Rectangle card = new Rectangle(x, y, cardW, CARD_HEIGHT);
            placements.add(new RecipeCardPlacement(recipe, evaluation, card));
            maxY = Math.max(maxY, y + CARD_HEIGHT);
            index++;
        }
        contentHeight = Math.max(0, maxY - innerY + 10);
        clampScroll();
        syncScrollbarFromScroll();
    }

    private void clampScroll() {
        int maxOffset = Math.max(0, contentHeight - listBounds.height + 16);
        if (scrollOffset < 0) {
            scrollOffset = 0;
        } else if (scrollOffset > maxOffset) {
            scrollOffset = maxOffset;
        }
    }

    private void syncScrollbarFromScroll() {
        int maxOffset = Math.max(0, contentHeight - listBounds.height + 16);
        if (maxOffset == 0) {
            scrollbarThumb = new Rectangle(scrollbarTrack.x, scrollbarTrack.y, scrollbarTrack.width, scrollbarTrack.height);
            return;
        }
        float viewportRatio = Math.max(0.12f, (float) listBounds.height / Math.max(listBounds.height, contentHeight));
        int thumbHeight = (int) (scrollbarTrack.height * viewportRatio);
        int minY = scrollbarTrack.y;
        int maxY = scrollbarTrack.y + scrollbarTrack.height - thumbHeight;
        float t = (float) scrollOffset / maxOffset;
        int thumbY = minY + (int) ((maxY - minY) * t);
        scrollbarThumb = new Rectangle(scrollbarTrack.x, thumbY, scrollbarTrack.width, thumbHeight);
    }

    private Rectangle toScreenRect(Rectangle contentRect) {
        return new Rectangle(contentRect.x, contentRect.y - scrollOffset, contentRect.width, contentRect.height);
    }

    private boolean isVisibleInList(Rectangle rect) {
        return rect.y + rect.height >= listBounds.y + 8 && rect.y <= listBounds.y + listBounds.height - 8;
    }

    private void drawButton(Graphics2D g, Rectangle r, String text, Color base, Color hover, boolean isHover) {
        g.setColor(isHover ? hover : base);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        g.setStroke(new BasicStroke(1));
        g.setFont(new Font("Segoe UI", Font.BOLD, 17));
        FontMetrics fm = g.getFontMetrics();
        int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
        int ty = r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(text, tx, ty);
    }

    private void drawWrapped(Graphics2D g, String text, int x, int y, int width, int lineHeight) {
        if (text == null || text.isEmpty()) {
            return;
        }
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        int drawY = y;
        for (String word : words) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if (fm.stringWidth(candidate) > width) {
                g.drawString(line.toString(), x, drawY);
                line = new StringBuilder(word);
                drawY += lineHeight;
            } else {
                line = new StringBuilder(candidate);
            }
        }
        if (!line.isEmpty()) {
            g.drawString(line.toString(), x, drawY);
        }
    }

    private void drawOutputIcon(Graphics2D g, CraftingRecipe recipe, int x, int y, int w, int h) {
        g.setColor(new Color(28, 34, 46, 230));
        g.fillRoundRect(x, y, w, h, 10, 10);
        g.setColor(new Color(92, 128, 176));
        g.drawRoundRect(x, y, w, h, 10, 10);

        String iconPath = resolveOutputIconPath(recipe);
        Image icon = loadIcon(iconPath);
        if (icon == null) {
            g.setColor(new Color(150, 160, 182));
            g.setFont(new Font("Segoe UI", Font.BOLD, Math.max(11, w / 5)));
            String fallback = "?";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(fallback, x + (w - fm.stringWidth(fallback)) / 2, y + (h + fm.getAscent() - fm.getDescent()) / 2);
            return;
        }

        int srcW = Math.max(1, icon.getWidth(gamePanel));
        int srcH = Math.max(1, icon.getHeight(gamePanel));
        double scale = Math.min((double) (w - 6) / srcW, (double) (h - 6) / srcH);
        int drawW = Math.max(1, (int) (srcW * scale));
        int drawH = Math.max(1, (int) (srcH * scale));
        int drawX = x + (w - drawW) / 2;
        int drawY = y + (h - drawH) / 2;
        g.drawImage(icon, drawX, drawY, drawW, drawH, gamePanel);
    }

    private String resolveOutputIconPath(CraftingRecipe recipe) {
        if (recipe == null || recipe.getOutput() == null) {
            return null;
        }
        if ("FruitRoll".equalsIgnoreCase(recipe.getOutput().getItemId())) {
            return "assets/items/powers/fruits/mysteryfruit_icon.gif";
        }
        Item outputItem = createOutputItem(recipe.getOutput().getItemType(), recipe.getOutput().getItemId(), recipe.getOutput().getTier());
        return outputItem != null ? outputItem.getIconPath() : null;
    }

    private Item createOutputItem(CraftingItemType type, String itemId, int tier) {
        if (itemId == null || itemId.isBlank()) {
            return null;
        }
        Class<?> cls = switch (type) {
            case WEAPON -> findItemClass(itemId, "combat.ranged.pistols.", "combat.ranged.rifles.", "combat.ranged.shotguns.",
                    "combat.ranged.smgs.", "combat.ranged.snipers.", "combat.melee.swords.", "combat.melee.hammers.",
                    "combat.melee.daggers.", "combat.melee.maces.", "combat.melee.scythes.");
            case CHARM -> findItemClass(itemId, "combat.charms.");
            case POWER -> findItemClass(itemId, "combat.powers.", "combat.powers.fruits.");
            case SUMMON -> findItemClass(itemId, "combat.summons.");
            case CONSUMABLE -> findItemClass(itemId, "combat.consumables.");
        };
        if (cls == null) {
            return null;
        }
        try {
            try {
                return (Item) cls.getConstructor(int.class).newInstance(tier);
            } catch (NoSuchMethodException ignored) {
                return (Item) cls.getConstructor().newInstance();
            }
        } catch (Exception ignored) {
            return null;
        }
    }

    private Class<?> findItemClass(String className, String... packages) {
        for (String pkg : packages) {
            try {
                return Class.forName(pkg + className);
            } catch (ClassNotFoundException ignored) {
            }
        }
        return null;
    }

    private Image loadIcon(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        if (iconCache.containsKey(path)) {
            return iconCache.get(path);
        }
        try {
            Image img;
            if (path.toLowerCase().endsWith(".gif")) {
                // ImageIcon keeps GIF frame metadata so animation can advance during repaints.
                img = new ImageIcon(path).getImage();
            } else {
                img = ImageIO.read(new File(path));
            }
            iconCache.put(path, img);
            return img;
        } catch (Exception e) {
            iconCache.put(path, null);
            return null;
        }
    }

    private record RecipeCardPlacement(CraftingRecipe recipe, CraftingEvaluation evaluation, Rectangle bounds) {
    }
}
