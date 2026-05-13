package ui.screens;

import ui.GamePanel;
import java.util.ArrayList;
import player.CharacterAppearance;
import player.CharacterRenderer;
import player.Wardrobe;
import combat.clothing.ClothingType;
import combat.clothing.ClothingItem;
import save.SaveData;
import save.SaveManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class CustomizeScreen {

    private GamePanel gamePanel;
    private CharacterAppearance tempAppearance;
    private CharacterRenderer renderer;
    private Wardrobe wardrobe;
    private boolean bodyMode = false; // false = main clothing screen, true = body customization

    private boolean clothingSelectionMode = false;
    private ClothingType selectedClothingType = null;
    /** Below category tabs (y≈100–160) so the picker does not cover Accessories/Tops/Bottoms/Body. */
    private Rectangle clothingSelectionArea = new Rectangle(50, 182, 700, 580);

    // Style selection overlay state
    private boolean styleSelectionMode = false;
    private ClothingItem selectedClothingForStyling = null;
    private Rectangle styleSelectionArea = new Rectangle(150, 200, 600, 500);
    private Rectangle styleCloseBtn = new Rectangle(styleSelectionArea.x + styleSelectionArea.width - 60, styleSelectionArea.y + 10, 50, 30);

    private Map<String, BufferedImage> iconCache = new HashMap<>();

    // UI Elements - Main Screen (redesigned layout)
    private Rectangle accessoriesBtn = new Rectangle(50, 100, 180, 60);
    private Rectangle topsBtn = new Rectangle(250, 100, 180, 60);
    private Rectangle bottomsBtn = new Rectangle(450, 100, 180, 60);
    private Rectangle customizeBodyBtn = new Rectangle(650, 100, 200, 60);
    private Rectangle confirmBtn = new Rectangle(1200, 820, 180, 60);
    private Rectangle backBtn = new Rectangle(50, 820, 180, 60);

    // Hover state tracking
    private String hoveredButton = null;

    // Body Mode UI Elements (redesigned)
    private Rectangle bodyTypeBtn = new Rectangle(50, 150, 180, 60);
    private Rectangle skinColorGrid = new Rectangle(50, 240, 240, 180);
    private Rectangle eyeShapeLeft = new Rectangle(350, 150, 50, 50);
    private Rectangle eyeShapeRight = new Rectangle(450, 150, 50, 50);
    private Rectangle eyeColorGrid = new Rectangle(350, 220, 200, 120);
    private Rectangle mouthLeft = new Rectangle(600, 150, 50, 50);
    private Rectangle mouthRight = new Rectangle(700, 150, 50, 50);
    private Rectangle hairStyleLeft = new Rectangle(50, 450, 50, 50);
    private Rectangle hairStyleRight = new Rectangle(150, 450, 50, 50);
    private Rectangle hairColorGrid = new Rectangle(50, 520, 240, 120);
    private Rectangle faceMarkingGrid = new Rectangle(350, 380, 200, 180);
    private Rectangle bodyMarkingGrid = new Rectangle(600, 380, 200, 180);
    private Rectangle bodyBackBtn = new Rectangle(50, 700, 180, 60);

    // Preview area (redesigned)
    private Rectangle previewArea = new Rectangle(950, 150, 430, 550);
    private int previewX = 1015;
    private int previewY = 200;
    private int previewScale = 3;

    // Available options (hardcoded for now, later load from assets)
    private String[] eyeShapes = {"round", "narrow", "wide", "almond"};
    private String[] mouthExpressions = {"neutral", "smile", "open", "sad", "surprised"};
    private String[] hairStyles = {"short", "long", "buzzcut", "ponytail"};
    private String[] faceMarkings = {"none", "scar1", "freckles", "tattoo1"};
    private String[] bodyMarkings = {"none", "scar1", "tattoo1", "tattoo2"};

    // Color palettes (indices)
    private int selectedSkinColor = 0;
    private int selectedEyeColor = 0;
    private int selectedHairColor = 0;

    // Skin color palette
    private Color[][] skinColors = {
        {new Color(255, 224, 189), new Color(255, 206, 152), new Color(255, 188, 115)}, // Light
        {new Color(241, 194, 125), new Color(223, 166, 83), new Color(205, 138, 41)},   // Medium
        {new Color(198, 134, 66), new Color(141, 85, 36), new Color(83, 36, 7)},       // Dark
        {new Color(165, 107, 70), new Color(120, 63, 4), new Color(77, 25, 0)}         // Very Dark
    };

    public CustomizeScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        loadData();
        this.renderer = new CharacterRenderer();
        renderer.loadAssets(tempAppearance, skinColors);
    }

    private void loadData() {
        SaveData data = SaveManager.load();
        this.tempAppearance = data.getAppearance() != null ? data.getAppearance() : new CharacterAppearance();
        selectedSkinColor = tempAppearance.getSkinColorIndex();
        this.wardrobe = gamePanel.getWardrobe();
    }

    public void handleMouseMove(int x, int y) {
        if (styleSelectionMode) {
            // Don't update main buttons when style selector is open
            return;
        }
        if (accessoriesBtn.contains(x, y)) hoveredButton = "accessories";
        else if (topsBtn.contains(x, y)) hoveredButton = "tops";
        else if (bottomsBtn.contains(x, y)) hoveredButton = "bottoms";
        else if (customizeBodyBtn.contains(x, y)) hoveredButton = "body";
        else if (confirmBtn.contains(x, y)) hoveredButton = "confirm";
        else if (backBtn.contains(x, y)) hoveredButton = "back";
        else if (bodyTypeBtn.contains(x, y)) hoveredButton = "bodytype";
        else if (bodyBackBtn.contains(x, y)) hoveredButton = "bodyBack";
        else hoveredButton = null;
    }

    public void handleClick(int x, int y) {
        if (styleSelectionMode) {
            if (styleCloseBtn.contains(x, y)) {
                closeStyleSelector();
                return;
            }
            if (accessoriesBtn.contains(x, y)) {
                closeStyleSelector();
                clothingSelectionMode = true;
                selectedClothingType = ClothingType.ACCESSORY;
                return;
            }
            if (topsBtn.contains(x, y)) {
                closeStyleSelector();
                clothingSelectionMode = true;
                selectedClothingType = ClothingType.TOP;
                return;
            }
            if (bottomsBtn.contains(x, y)) {
                closeStyleSelector();
                clothingSelectionMode = true;
                selectedClothingType = ClothingType.BOTTOM;
                return;
            }
            if (customizeBodyBtn.contains(x, y)) {
                closeStyleSelector();
                clothingSelectionMode = false;
                selectedClothingType = null;
                bodyMode = true;
                return;
            }
            if (confirmBtn.contains(x, y)) {
                saveAppearance();
                gamePanel.switchScreen("menu");
                return;
            }
            if (backBtn.contains(x, y)) {
                gamePanel.switchScreen("menu");
                return;
            }
            if (styleSelectionArea.contains(x, y)) {
                handleStyleSelectionClick(x, y);
            } else {
                closeStyleSelector();
            }
            return;
        }

        if (clothingSelectionMode) {
            if (accessoriesBtn.contains(x, y)) {
                selectedClothingType = ClothingType.ACCESSORY;
                return;
            }
            if (topsBtn.contains(x, y)) {
                selectedClothingType = ClothingType.TOP;
                return;
            }
            if (bottomsBtn.contains(x, y)) {
                selectedClothingType = ClothingType.BOTTOM;
                return;
            }
            if (customizeBodyBtn.contains(x, y)) {
                clothingSelectionMode = false;
                selectedClothingType = null;
                bodyMode = true;
                return;
            }
            if (confirmBtn.contains(x, y)) {
                saveAppearance();
                gamePanel.switchScreen("menu");
                return;
            }
            if (backBtn.contains(x, y)) {
                gamePanel.switchScreen("menu");
                return;
            }
            if (clothingSelectionArea.contains(x, y)) {
                handleClothingSelectionClick(x, y);
            } else {
                clothingSelectionMode = false;
                selectedClothingType = null;
            }
            return;
        }

        if (!bodyMode) {
            // Main screen clicks
            if (accessoriesBtn.contains(x, y)) {
                clothingSelectionMode = true;
                selectedClothingType = ClothingType.ACCESSORY;
            } else if (topsBtn.contains(x, y)) {
                clothingSelectionMode = true;
                selectedClothingType = ClothingType.TOP;
            } else if (bottomsBtn.contains(x, y)) {
                clothingSelectionMode = true;
                selectedClothingType = ClothingType.BOTTOM;
            } else if (customizeBodyBtn.contains(x, y)) {
                bodyMode = true;
            } else if (confirmBtn.contains(x, y)) {
                saveAppearance();
                gamePanel.switchScreen("menu");
            } else if (backBtn.contains(x, y)) {
                gamePanel.switchScreen("menu");
            }
        } else {
            // Body mode clicks
            if (bodyTypeBtn.contains(x, y)) {
                toggleBodyType();
            } else if (skinColorGrid.contains(x, y)) {
                int cols = skinColors[0].length;
                int cellWidth = skinColorGrid.width / cols;
                int cellHeight = skinColorGrid.height / skinColors.length;
                int col = (x - skinColorGrid.x) / cellWidth;
                int row = (y - skinColorGrid.y) / cellHeight;
                selectedSkinColor = row * cols + col;
                tempAppearance.setSkinColorIndex(selectedSkinColor);
                renderer.loadAssets(tempAppearance, skinColors); // Reload with new color
            } else if (eyeShapeLeft.contains(x, y)) {
                cycleEyeShape(-1);
            } else if (eyeShapeRight.contains(x, y)) {
                cycleEyeShape(1);
            } else if (eyeColorGrid.contains(x, y)) {
                // TODO: Handle eye color grid
            } else if (mouthLeft.contains(x, y)) {
                cycleMouth(-1);
            } else if (mouthRight.contains(x, y)) {
                cycleMouth(1);
            } else if (hairStyleLeft.contains(x, y)) {
                cycleHairStyle(-1);
            } else if (hairStyleRight.contains(x, y)) {
                cycleHairStyle(1);
            } else if (hairColorGrid.contains(x, y)) {
                // TODO: Handle hair color grid
            } else if (faceMarkingGrid.contains(x, y)) {
                // TODO: Handle face marking grid
            } else if (bodyMarkingGrid.contains(x, y)) {
                // TODO: Handle body marking grid
            } else if (bodyBackBtn.contains(x, y)) {
                bodyMode = false;
            }
        }
    }

    private void toggleBodyType() {
        tempAppearance.setGender(tempAppearance.getGender() == CharacterAppearance.Gender.MALE ?
            CharacterAppearance.Gender.FEMALE : CharacterAppearance.Gender.MALE);
        renderer.loadAssets(tempAppearance, skinColors); // Reload assets for new gender
    }

    private void cycleEyeShape(int direction) {
        int currentIndex = java.util.Arrays.asList(eyeShapes).indexOf(tempAppearance.getEyeShape());
        currentIndex = (currentIndex + direction + eyeShapes.length) % eyeShapes.length;
        tempAppearance.setEyeShape(eyeShapes[currentIndex]);
    }

    private void cycleMouth(int direction) {
        int currentIndex = java.util.Arrays.asList(mouthExpressions).indexOf(tempAppearance.getMouthExpression());
        currentIndex = (currentIndex + direction + mouthExpressions.length) % mouthExpressions.length;
        tempAppearance.setMouthExpression(mouthExpressions[currentIndex]);
    }

    private void cycleHairStyle(int direction) {
        int currentIndex = java.util.Arrays.asList(hairStyles).indexOf(tempAppearance.getHairStyle());
        currentIndex = (currentIndex + direction + hairStyles.length) % hairStyles.length;
        tempAppearance.setHairStyle(hairStyles[currentIndex]);
    }

    private void cycleClothing(ClothingType type, int direction) {
        List<ClothingItem> unlocked = wardrobe.getUnlockedByType(type);
        if (unlocked.isEmpty()) return;

        String currentId = switch (type) {
            case TOP -> tempAppearance.getEquippedTopId();
            case BOTTOM -> tempAppearance.getEquippedBottomId();
            case ACCESSORY -> tempAppearance.getEquippedAccessoryId();
            default -> null;
        };

        int currentIndex = -1;
        for (int i = 0; i < unlocked.size(); i++) {
            if (unlocked.get(i).getName().equalsIgnoreCase(currentId)) {
                currentIndex = i;
                break;
            }
        }
        if (currentIndex == -1) currentIndex = 0;

        currentIndex = (currentIndex + direction + unlocked.size()) % unlocked.size();
        ClothingItem newItem = unlocked.get(currentIndex);

        switch (type) {
            case TOP -> tempAppearance.setEquippedTopId(newItem.getName().toLowerCase());
            case BOTTOM -> tempAppearance.setEquippedBottomId(newItem.getName().toLowerCase());
            case ACCESSORY -> tempAppearance.setEquippedAccessoryId(newItem.getName().toLowerCase());
        }
    }

    private void saveAppearance() {
        SaveData data = SaveManager.load();
        data.setAppearance(tempAppearance);
        data.setUnlockedClothingIds(wardrobe.getUnlockedClothingIds());
        SaveManager.save(data);
        renderer.loadAssets(tempAppearance, skinColors); // Ensure assets are loaded
    }

    public void draw(Graphics2D g, int width, int height) {
        // Modern gradient background
        GradientPaint bgGradient = new GradientPaint(0, 0, new Color(30, 30, 40), 0, height, new Color(20, 20, 30));
        g.setPaint(bgGradient);
        g.fillRect(0, 0, width, height);

        // Add subtle pattern overlay
        drawBackgroundPattern(g, width, height);

        if (!bodyMode) {
            drawMainScreen(g);
            if (clothingSelectionMode) {
                drawClothingSelection(g);
            }
            // Draw style selection overlay if active
            drawStyleSelection(g);
        } else {
            drawBodyScreen(g);
        }

        // Always draw preview
        drawPreview(g);
    }

    private void drawBackgroundPattern(Graphics2D g, int width, int height) {
        g.setColor(new Color(255, 255, 255, 5));
        for (int i = 0; i < width; i += 40) {
            g.drawLine(i, 0, i, height);
        }
        for (int i = 0; i < height; i += 40) {
            g.drawLine(0, i, width, i);
        }
    }

    private void drawMainScreen(Graphics2D g) {
        // Title with glow effect
        g.setColor(new Color(100, 200, 255));
        g.setFont(new Font("Segoe UI", Font.BOLD, 42));
        g.drawString("Customize Character", 50, 70);

        // Subtitle
        g.setColor(new Color(150, 150, 170));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g.drawString("Design your unique appearance", 50, 95);

        // Category section label
        g.setColor(new Color(180, 180, 200));
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g.drawString("CLOTHING", 50, 130);

        // Buttons with modern styling
        drawStyledButton(g, accessoriesBtn, "Accessories", new Color(147, 112, 219), hoveredButton == "accessories");
        drawStyledButton(g, topsBtn, "Tops", new Color(52, 152, 219), hoveredButton == "tops");
        drawStyledButton(g, bottomsBtn, "Bottoms", new Color(46, 204, 113), hoveredButton == "bottoms");
        drawStyledButton(g, customizeBodyBtn, "Customize Body", new Color(231, 76, 60), hoveredButton == "body");

        // Action buttons
        drawStyledButton(g, confirmBtn, "Confirm", new Color(39, 174, 96), hoveredButton == "confirm");
        drawStyledButton(g, backBtn, "Back", new Color(149, 165, 166), hoveredButton == "back");
    }

    private void drawBodyScreen(Graphics2D g) {
        // Title
        g.setColor(new Color(100, 200, 255));
        g.setFont(new Font("Segoe UI", Font.BOLD, 42));
        g.drawString("Customize Body", 50, 70);

        // Subtitle
        g.setColor(new Color(150, 150, 170));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g.drawString("Personalize your character's features", 50, 95);

        // Body type with card
        drawOptionCard(g, bodyTypeBtn, "Body Type", tempAppearance.getGender().toString(), new Color(155, 89, 182), hoveredButton == "bodytype");

        // Skin color section
        drawSectionLabel(g, "Skin Color", 50, 220);
        drawSkinColorPalette(g, skinColorGrid);

        // Eyes section
        drawSectionLabel(g, "Eyes", 350, 220);
        drawArrowPair(g, eyeShapeLeft, eyeShapeRight, tempAppearance.getEyeShape(), hoveredButton == "eyeLeft", hoveredButton == "eyeRight");

        // Mouth section
        drawSectionLabel(g, "Mouth", 600, 220);
        drawArrowPair(g, mouthLeft, mouthRight, tempAppearance.getMouthExpression(), hoveredButton == "mouthLeft", hoveredButton == "mouthRight");

        // Hair section
        drawSectionLabel(g, "Hair", 50, 430);
        drawArrowPair(g, hairStyleLeft, hairStyleRight, tempAppearance.getHairStyle(), hoveredButton == "hairLeft", hoveredButton == "hairRight");

        // Markings sections
        drawSectionLabel(g, "Face Markings", 350, 360);
        drawPlaceholderGrid(g, faceMarkingGrid, tempAppearance.getFaceMarkingId());
        drawSectionLabel(g, "Body Markings", 600, 360);
        drawPlaceholderGrid(g, bodyMarkingGrid, tempAppearance.getBodyMarkingId());

        // Back button
        drawStyledButton(g, bodyBackBtn, "Back", new Color(149, 165, 166), hoveredButton == "bodyBack");
    }

    private void drawPreview(Graphics2D g) {
        // Draw preview card with modern styling
        g.setColor(new Color(40, 40, 50));
        g.fill(previewArea);
        
        // Gradient border
        GradientPaint borderGradient = new GradientPaint(
            previewArea.x, previewArea.y, new Color(100, 200, 255),
            previewArea.x + previewArea.width, previewArea.y + previewArea.height, new Color(147, 112, 219)
        );
        g.setPaint(borderGradient);
        g.setStroke(new BasicStroke(3));
        g.draw(previewArea);
        g.setStroke(new BasicStroke(1));
        g.setPaint(null);

        // Preview label
        g.setColor(new Color(180, 180, 200));
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g.drawString("PREVIEW", previewArea.x + 20, previewArea.y + 30);

        BufferedImage img = renderer.getTestCharacter();
        if (img != null) {
            // Draw with shadow effect
            g.setColor(new Color(0, 0, 0, 50));
            g.fillRect(previewX + 10, previewY + 10, 96 * previewScale, 96 * previewScale);
            g.drawImage(img, previewX, previewY, 96 * previewScale, 96 * previewScale, null);
        } else {
            // Fallback with modern styling
            g.setColor(new Color(60, 60, 70));
            g.fillRect(previewX, previewY, 96 * previewScale, 96 * previewScale);
            g.setColor(new Color(100, 200, 255));
            g.drawRect(previewX, previewY, 96 * previewScale, 96 * previewScale);
            g.setColor(new Color(150, 150, 170));
            g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g.drawString("Character Preview", previewX + 40, previewY + 150);
        }
    }


    private void drawStyledButton(Graphics2D g, Rectangle btn, String label, Color accentColor, boolean isHovered) {
        // Button background with gradient
        if (isHovered) {
            GradientPaint gradient = new GradientPaint(btn.x, btn.y, accentColor.brighter(), btn.x, btn.y + btn.height, accentColor);
            g.setPaint(gradient);
        } else {
            GradientPaint gradient = new GradientPaint(btn.x, btn.y, new Color(50, 50, 60), btn.x, btn.y + btn.height, new Color(40, 40, 50));
            g.setPaint(gradient);
        }
        g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 12, 12);
        g.setPaint(null);

        // Border
        g.setColor(isHovered ? accentColor : new Color(80, 80, 90));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 12, 12);
        g.setStroke(new BasicStroke(1));

        // Text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();
        int textX = btn.x + (btn.width - fm.stringWidth(label)) / 2;
        int textY = btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, textX, textY);
    }

    private void drawArrowPair(Graphics2D g, Rectangle left, Rectangle right, String currentValue, boolean leftHovered, boolean rightHovered) {
        // Left arrow
        drawArrow(g, left, "◄", leftHovered);
        // Right arrow
        drawArrow(g, right, "►", rightHovered);
        // Current value display
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        g.drawString(currentValue, left.x + left.width + 20, left.y + 35);
    }

    private void drawArrow(Graphics2D g, Rectangle arrow, String symbol, boolean isHovered) {
        if (isHovered) {
            g.setColor(new Color(100, 200, 255));
        } else {
            g.setColor(new Color(60, 60, 70));
        }
        g.fillRoundRect(arrow.x, arrow.y, arrow.width, arrow.height, 8, 8);
        g.setColor(isHovered ? new Color(150, 220, 255) : new Color(100, 100, 110));
        g.drawRoundRect(arrow.x, arrow.y, arrow.width, arrow.height, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        int textX = arrow.x + (arrow.width - fm.stringWidth(symbol)) / 2;
        int textY = arrow.y + (arrow.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(symbol, textX, textY);
    }

    private void drawSkinColorPalette(Graphics2D g, Rectangle grid) {
        int rows = skinColors.length;
        int cols = skinColors[0].length;
        int cellWidth = grid.width / cols;
        int cellHeight = grid.height / rows;
        int padding = 4;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Color color = skinColors[r][c];
                int cellX = grid.x + c * cellWidth + padding;
                int cellY = grid.y + r * cellHeight + padding;
                int cellSize = Math.min(cellWidth, cellHeight) - padding * 2;

                // Draw color circle with shadow
                g.setColor(new Color(0, 0, 0, 30));
                g.fillOval(cellX + 3, cellY + 3, cellSize, cellSize);
                g.setColor(color);
                g.fillOval(cellX, cellY, cellSize, cellSize);
                g.setColor(new Color(255, 255, 255, 100));
                g.drawOval(cellX, cellY, cellSize, cellSize);
            }
        }

        // Highlight selected color with glow effect
        int selectedRow = selectedSkinColor / 3;
        int selectedCol = selectedSkinColor % 3;
        int cellX = grid.x + selectedCol * cellWidth + padding;
        int cellY = grid.y + selectedRow * cellHeight + padding;
        int cellSize = Math.min(cellWidth, cellHeight) - padding * 2;
        
        g.setColor(new Color(100, 200, 255, 150));
        g.fillOval(cellX - 4, cellY - 4, cellSize + 8, cellSize + 8);
        g.setColor(new Color(255, 255, 255));
        g.setStroke(new BasicStroke(3));
        g.drawOval(cellX - 4, cellY - 4, cellSize + 8, cellSize + 8);
        g.setStroke(new BasicStroke(1));
    }

    private void handleClothingSelectionClick(int x, int y) {
        List<ClothingItem> allItems = wardrobe.getUnlockedByType(selectedClothingType);

        int startX = clothingSelectionArea.x + 30;
        int startY = clothingSelectionArea.y + 90;
        int slotSize = 70;
        int spacing = 12;
        int itemsPerRow = 8;
        int currentY = startY;

        for (int tier = 1; tier <= 5; tier++) {
            List<ClothingItem> itemsInTier = new ArrayList<>();

            if (tier == 1 && selectedClothingType == ClothingType.ACCESSORY) {
                itemsInTier.add(null); // None option
            }

            for (ClothingItem item : allItems) {
                if (item.getTier() == tier) {
                    itemsInTier.add(item);
                }
            }

            if (itemsInTier.isEmpty()) continue;

            currentY += 30; // skip tier label

            int rows = (itemsInTier.size() + itemsPerRow - 1) / itemsPerRow;

            for (int i = 0; i < itemsInTier.size(); i++) {
                int col = i % itemsPerRow;
                int row = i / itemsPerRow;
                int itemX = startX + col * (slotSize + spacing);
                int itemY = currentY + row * (slotSize + spacing);

                if (x >= itemX && x <= itemX + slotSize && y >= itemY && y <= itemY + slotSize) {
                    ClothingItem selected = itemsInTier.get(i);
                    if (selected != null) {
                        // Auto-equip style if clothing has only one available style
                        List<String> styles = selected.getStyleOptions();
                        if (styles.size() == 1) {
                            selected.setSelectedStyle(styles.get(0));
                            SaveData data = SaveManager.load();
                            data.getEquippedClothingStyles().put(selected.getName().toLowerCase(), styles.get(0));
                            SaveManager.save(data);
                        }
                    }
                    switch (selectedClothingType) {
                        case TOP -> tempAppearance.setEquippedTopId(selected == null ? null : selected.getName().toLowerCase());
                        case BOTTOM -> tempAppearance.setEquippedBottomId(selected == null ? null : selected.getName().toLowerCase());
                        case ACCESSORY -> tempAppearance.setEquippedAccessoryId(selected == null ? null : selected.getName().toLowerCase());
                    }
                    return;
                }
            }

            currentY += rows * (slotSize + spacing) + 20;
        }
    }

    private void drawClothingSelection(Graphics2D g) {
        if (selectedClothingType == null) return;

        int pickerX = clothingSelectionArea.x;
        int pickerY = clothingSelectionArea.y;
        int pickerW = clothingSelectionArea.width;
        int pickerH = clothingSelectionArea.height;

        // Background
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRoundRect(pickerX, pickerY, pickerW, pickerH, 16, 16);

        // Gradient border
        GradientPaint borderGradient = new GradientPaint(
                pickerX, pickerY, new Color(100, 200, 255),
                pickerX + pickerW, pickerY + pickerH, new Color(147, 112, 219)
        );
        g.setPaint(borderGradient);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(pickerX, pickerY, pickerW, pickerH, 16, 16);
        g.setStroke(new BasicStroke(1));
        g.setPaint(null);

        // Title
        g.setColor(new Color(100, 200, 255));
        g.setFont(new Font("Segoe UI", Font.BOLD, 28));
        String title = "Select " + selectedClothingType.toString().charAt(0)
                + selectedClothingType.toString().substring(1).toLowerCase();
        g.drawString(title, pickerX + 30, pickerY + 40);

        g.setColor(new Color(150, 150, 170));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g.drawString("Click an item to equip it", pickerX + 30, pickerY + 65);

        List<ClothingItem> allItems = wardrobe.getUnlockedByType(selectedClothingType);

        if (allItems.isEmpty() && selectedClothingType != ClothingType.ACCESSORY) {
            g.setColor(new Color(150, 150, 170));
            g.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            g.drawString("No items unlocked yet!", pickerX + 30, pickerY + 120);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g.drawString("Play the game to unlock more clothing", pickerX + 30, pickerY + 145);
            return;
        }

        int startX = pickerX + 30;
        int startY = pickerY + 90;
        int slotSize = 70;
        int spacing = 12;
        int itemsPerRow = 8;
        int currentY = startY;

        // Group items by tier
        for (int tier = 1; tier <= 5; tier++) {
            List<ClothingItem> itemsInTier = new ArrayList<>();

            // Add None option for accessories only, at tier 1
            if (tier == 1 && selectedClothingType == ClothingType.ACCESSORY) {
                itemsInTier.add(null); // null = None
            }

            for (ClothingItem item : allItems) {
                if (item.getTier() == tier) {
                    itemsInTier.add(item);
                }
            }

            if (itemsInTier.isEmpty()) continue;

            // Tier label
            Color tierColor = switch (tier) {
                case 1 -> new Color(200, 200, 200);
                case 2 -> new Color(100, 200, 100);
                case 3 -> new Color(100, 150, 255);
                case 4 -> new Color(200, 100, 255);
                case 5 -> new Color(255, 215, 0);
                default -> Color.WHITE;
            };
            g.setColor(tierColor);
            g.setFont(new Font("Segoe UI", Font.BOLD, 20));
            g.drawString("Tier " + tier, startX, currentY);
            currentY += 30;

            for (int i = 0; i < itemsInTier.size(); i++) {
                ClothingItem item = itemsInTier.get(i);
                int col = i % itemsPerRow;
                int row = i / itemsPerRow;
                int itemX = startX + col * (slotSize + spacing);
                int itemY = currentY + row * (slotSize + spacing);

                // Check if selected
                String equippedId = switch (selectedClothingType) {
                    case TOP -> tempAppearance.getEquippedTopId();
                    case BOTTOM -> tempAppearance.getEquippedBottomId();
                    case ACCESSORY -> tempAppearance.getEquippedAccessoryId();
                    default -> null;
                };
                boolean isNone = item == null;
                boolean isSelected = isNone
                        ? (equippedId == null || equippedId.isEmpty())
                        : (item.getName().equalsIgnoreCase(equippedId));

                // Slot background
                if (isSelected) {
                    GradientPaint grad = new GradientPaint(itemX, itemY, new Color(100, 150, 200), itemX, itemY + slotSize, new Color(50, 100, 150));
                    g.setPaint(grad);
                } else {
                    GradientPaint grad = new GradientPaint(itemX, itemY, new Color(50, 50, 60), itemX, itemY + slotSize, new Color(40, 40, 50));
                    g.setPaint(grad);
                }
                g.fillRoundRect(itemX, itemY, slotSize, slotSize, 8, 8);
                g.setPaint(null);

                // Border
                g.setColor(isSelected ? new Color(100, 200, 255) : new Color(70, 70, 80));
                g.setStroke(new BasicStroke(2));
                g.drawRoundRect(itemX, itemY, slotSize, slotSize, 8, 8);
                g.setStroke(new BasicStroke(1));

                if (isNone) {
                    // Draw X for None
                    g.setColor(new Color(150, 150, 150));
                    g.setStroke(new BasicStroke(2));
                    g.drawLine(itemX + 15, itemY + 15, itemX + slotSize - 15, itemY + slotSize - 15);
                    g.drawLine(itemX + slotSize - 15, itemY + 15, itemX + 15, itemY + slotSize - 15);
                    g.setStroke(new BasicStroke(1));
                } else {
                    // Load icon from assetPath
                    BufferedImage icon = iconCache.get(item.getName());
                    if (icon == null) {
                        try {
                            icon = javax.imageio.ImageIO.read(new java.io.File(item.getAssetPath()));
                            iconCache.put(item.getName(), icon);
                        } catch (Exception e) {
                            icon = null;
                        }
                    }

                    if (icon != null) {
                        int maxW = slotSize - 4;
                        int maxH = slotSize - 14;
                        double scale = Math.min((double) maxW / icon.getWidth(), (double) maxH / icon.getHeight());
                        int drawW = (int) (icon.getWidth() * scale);
                        int drawH = (int) (icon.getHeight() * scale);
                        int drawX = itemX + (slotSize - drawW) / 2;
                        int drawY = itemY + (slotSize - 14 - drawH) / 2;
                        g.drawImage(icon, drawX, drawY, drawW, drawH, null);
                    } else {
                        // Fallback colored square
                        Color fallback = switch (selectedClothingType) {
                            case TOP -> new Color(52, 152, 219);
                            case BOTTOM -> new Color(46, 204, 113);
                            case ACCESSORY -> new Color(147, 112, 219);
                            default -> new Color(100, 100, 100);
                        };
                        g.setColor(fallback);
                        g.fillRoundRect(itemX + 10, itemY + 10, slotSize - 20, slotSize - 20, 4, 4);
                    }

                    // Item name
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    String name = item.getName();
                    if (name.length() > 8) name = name.substring(0, 8) + ".";
                    g.drawString(name, itemX + 2, itemY + slotSize - 2);
                }
            }

            int rows = (itemsInTier.size() + itemsPerRow - 1) / itemsPerRow;
            currentY += rows * (slotSize + spacing) + 20;
        }
    }

    // Helper methods for modern styling
    private void drawSectionLabel(Graphics2D g, String label, int x, int y) {
        g.setColor(new Color(100, 200, 255));
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g.drawString(label, x, y);
    }

    private void drawOptionCard(Graphics2D g, Rectangle rect, String label, String value, Color accentColor, boolean isHovered) {
        // Card background
        g.setColor(new Color(40, 40, 50));
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);
        
        // Border
        g.setColor(isHovered ? accentColor : new Color(70, 70, 80));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);
        g.setStroke(new BasicStroke(1));

        // Label
        g.setColor(new Color(150, 150, 170));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.drawString(label, rect.x + 15, rect.y + 25);

        // Value
        g.setColor(accentColor);
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g.drawString(value, rect.x + 15, rect.y + 48);
    }

    private void drawPlaceholderGrid(Graphics2D g, Rectangle rect, String selected) {
        g.setColor(new Color(40, 40, 50));
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);
        g.setColor(new Color(70, 70, 80));
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);

        g.setColor(new Color(150, 150, 170));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g.drawString(selected, rect.x + 15, rect.y + rect.height / 2);
    }

    /**
     * Handle keyboard input - E key to open style selector while in clothing selection mode
     */
    public void handleKeyPress(int keyCode) {
        // U key for debug output
        if (keyCode == java.awt.event.KeyEvent.VK_U) {
            printEquippedDebug();
            return;
        }

        if (keyCode == java.awt.event.KeyEvent.VK_E && clothingSelectionMode && !styleSelectionMode && selectedClothingForStyling == null) {
            // E key pressed while in clothing selection mode
            // Open style selector for the currently equipped clothing of this type
            if (selectedClothingType != null) {
                String equippedId = switch (selectedClothingType) {
                    case TOP -> tempAppearance.getEquippedTopId();
                    case BOTTOM -> tempAppearance.getEquippedBottomId();
                    case ACCESSORY -> tempAppearance.getEquippedAccessoryId();
                    default -> null;
                };
                
                if (equippedId != null && !equippedId.isEmpty()) {
                    // Find the clothing item by name
                    List<ClothingItem> allItems = wardrobe.getUnlockedByType(selectedClothingType);
                    for (ClothingItem item : allItems) {
                        if (item.getName().equalsIgnoreCase(equippedId)) {
                            openStyleSelector(item);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Open style selection overlay for a specific clothing item
     */
    public void openStyleSelector(ClothingItem clothing) {
        if (clothing == null || !(clothing instanceof combat.clothing.ClothingItem)) {
            return;
        }
        selectedClothingForStyling = clothing;

        // Load the equipped style from SaveData so the UI shows correct selection
        SaveData saveData = SaveManager.load();
        String equippedStyle = saveData.getEquippedClothingStyles().get(clothing.getName().toLowerCase());
        if (equippedStyle != null) {
            selectedClothingForStyling.setSelectedStyle(equippedStyle);
        }

        styleSelectionMode = true;
    }

    /**
     * Close style selection overlay
     */
    public void closeStyleSelector() {
        styleSelectionMode = false;
        selectedClothingForStyling = null;
    }

    /**
     * Print debug info about equipped clothing and styles
     */
    private void printEquippedDebug() {
        System.out.println("=== EQUIPPED CLOTHING DEBUG ===");
        SaveData data = SaveManager.load();
        Map<String, String> equippedStyles = data.getEquippedClothingStyles();

        String topId = tempAppearance.getEquippedTopId();
        if (topId != null && !topId.isEmpty()) {
            String style = equippedStyles.getOrDefault(topId, "default");
            System.out.println("Top: " + topId + " (Style: " + style + ")");
        } else {
            System.out.println("Top: None");
        }

        String bottomId = tempAppearance.getEquippedBottomId();
        if (bottomId != null && !bottomId.isEmpty()) {
            String style = equippedStyles.getOrDefault(bottomId, "default");
            System.out.println("Bottom: " + bottomId + " (Style: " + style + ")");
        } else {
            System.out.println("Bottom: None");
        }

        String accessoryId = tempAppearance.getEquippedAccessoryId();
        if (accessoryId != null && !accessoryId.isEmpty()) {
            String style = equippedStyles.getOrDefault(accessoryId, "default");
            System.out.println("Accessory: " + accessoryId + " (Style: " + style + ")");
        } else {
            System.out.println("Accessory: None");
        }
        System.out.println("===============================");
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * Build the full style list for a clothing piece:
     * default style + lootbox styles + non-lootbox unlockable styles.
     * Ordering is stable and deduplicated for consistent UI.
     */
    private List<String> getAllStylesForSelection(ClothingItem item) {
        LinkedHashSet<String> ordered = new LinkedHashSet<>();
        String defaultStyle = item.getDefaultStyle();
        if (defaultStyle != null && !defaultStyle.isEmpty()) {
            ordered.add(defaultStyle);
        }
        ordered.addAll(item.getStyleOptions());
        ordered.addAll(item.getUnlockableStyles());
        return new ArrayList<>(ordered);
    }

    private Set<String> getUnlockedStylesForItem(SaveData saveData, ClothingItem item) {
        Set<String> result = new HashSet<>();
        if (saveData.getUnlockedClothingStyles().containsKey(item.getName())) {
            result.addAll(saveData.getUnlockedClothingStyles().get(item.getName()));
        }
        if (saveData.getUnlockedClothingStyles().containsKey(item.getName().toLowerCase())) {
            result.addAll(saveData.getUnlockedClothingStyles().get(item.getName().toLowerCase()));
        }
        return result;
    }

    /**
     * Handle style selection clicks
     */
    private void handleStyleSelectionClick(int x, int y) {
        if (selectedClothingForStyling == null) {
            closeStyleSelector();
            return;
        }

        SaveData saveData = SaveManager.load();
        List<String> styles = getAllStylesForSelection(selectedClothingForStyling);
        Set<String> unlockedStyles = getUnlockedStylesForItem(saveData, selectedClothingForStyling);
        String defaultStyle = selectedClothingForStyling.getDefaultStyle();

        int startX = styleSelectionArea.x + 30;
        int startY = styleSelectionArea.y + 90;
        int slotSize = 70;
        int spacing = 12;
        int stylesPerRow = 6;

        for (int i = 0; i < styles.size(); i++) {
            String style = styles.get(i);
            int col = i % stylesPerRow;
            int row = i / stylesPerRow;
            int styleX = startX + col * (slotSize + spacing);
            int styleY = startY + row * (slotSize + spacing);

            if (x >= styleX && x <= styleX + slotSize && y >= styleY && y <= styleY + slotSize) {
                // Clicked on this style
                boolean isDefault = style.equals(defaultStyle);
                boolean isUnlocked = isDefault || unlockedStyles.contains(style);

                System.out.println("Clicked style: " + style + " | isDefault: " + isDefault + " | isUnlocked: " + isUnlocked + " | unlockedStyles: " + unlockedStyles);

                if (isUnlocked) {
                    // Style is unlocked (or is default), equip it
                    selectedClothingForStyling.setSelectedStyle(style);

                    // Save the equipped style
                    Map<String, String> equippedStyles = saveData.getEquippedClothingStyles();
                    equippedStyles.put(selectedClothingForStyling.getName().toLowerCase(), style);
                    SaveManager.save(saveData);

                    System.out.println("Selected style: " + style + " for " + selectedClothingForStyling.getName());
                    // Do NOT close the style selector here; let the user close it explicitly
                } else {
                    System.out.println("Style " + style + " is locked!");
                }
                return;
            }
        }
    }

    /**
     * Draw the style selection overlay
     */
    private void drawStyleSelection(Graphics2D g) {
        if (selectedClothingForStyling == null || !styleSelectionMode) {
            return;
        }

        SaveData saveData = SaveManager.load();
        
        int pickerX = styleSelectionArea.x;
        int pickerY = styleSelectionArea.y;
        int pickerW = styleSelectionArea.width;
        int pickerH = styleSelectionArea.height;

        // Background
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRoundRect(pickerX, pickerY, pickerW, pickerH, 16, 16);

        // Gradient border
        GradientPaint borderGradient = new GradientPaint(
                pickerX, pickerY, new Color(100, 200, 255),
                pickerX + pickerW, pickerY + pickerH, new Color(147, 112, 219)
        );
        g.setPaint(borderGradient);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(pickerX, pickerY, pickerW, pickerH, 16, 16);
        g.setStroke(new BasicStroke(1));
        g.setPaint(null);

        // Title
        g.setColor(new Color(100, 200, 255));
        g.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g.drawString("Select Style", pickerX + 30, pickerY + 40);

        g.setColor(new Color(150, 150, 170));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g.drawString("Locked styles cannot be selected yet", pickerX + 30, pickerY + 65);

        Set<String> unlockedStyles = getUnlockedStylesForItem(saveData, selectedClothingForStyling);
        String defaultStyle = selectedClothingForStyling.getDefaultStyle();
        List<String> allStyles = getAllStylesForSelection(selectedClothingForStyling);

        int startX = pickerX + 30;
        int startY = pickerY + 90;
        int slotSize = 70;
        int spacing = 12;
        int stylesPerRow = 6;

        for (int i = 0; i < allStyles.size(); i++) {
            String style = allStyles.get(i);
            int col = i % stylesPerRow;
            int row = i / stylesPerRow;
            int styleX = startX + col * (slotSize + spacing);
            int styleY = startY + row * (slotSize + spacing);

            boolean isDefault = style.equals(defaultStyle);
            boolean isUnlocked = isDefault || unlockedStyles.contains(style);
            boolean isSelected = style.equals(selectedClothingForStyling.getSelectedStyle());

            // Slot background
            if (isSelected && isUnlocked) {
                GradientPaint grad = new GradientPaint(styleX, styleY, new Color(100, 150, 200), styleX, styleY + slotSize, new Color(50, 100, 150));
                g.setPaint(grad);
            } else {
                GradientPaint grad = new GradientPaint(styleX, styleY, new Color(50, 50, 60), styleX, styleY + slotSize, new Color(40, 40, 50));
                g.setPaint(grad);
            }
            g.fillRoundRect(styleX, styleY, slotSize, slotSize, 8, 8);
            g.setPaint(null);

            // Border
            if (isDefault) {
                // Green outline for default style
                g.setColor(new Color(50, 200, 50));
                g.setStroke(new BasicStroke(3));
            } else if (isSelected && isUnlocked) {
                g.setColor(new Color(100, 200, 255));
                g.setStroke(new BasicStroke(3));
            } else {
                g.setColor(new Color(70, 70, 80));
                g.setStroke(new BasicStroke(2));
            }
            g.drawRoundRect(styleX, styleY, slotSize, slotSize, 8, 8);
            g.setStroke(new BasicStroke(1));

            // Style name
            g.setColor(isUnlocked ? Color.WHITE : new Color(100, 100, 100));
            g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g.drawString(style, styleX + 5, styleY + slotSize - 5);

            // Lock icon if not unlocked
            if (!isUnlocked) {
                g.setColor(new Color(200, 100, 100, 150));
                g.fillRoundRect(styleX + 20, styleY + 20, 30, 30, 4, 4);
                g.setColor(new Color(255, 150, 150));
                g.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g.drawString("L", styleX + 30, styleY + 42);
            }
        }

        // Close button for style selector
        g.setColor(new Color(255, 100, 100));
        g.fillRoundRect(styleCloseBtn.x, styleCloseBtn.y, styleCloseBtn.width, styleCloseBtn.height, 12, 12);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g.drawString("X", styleCloseBtn.x + 15, styleCloseBtn.y + 20);
    }
}
