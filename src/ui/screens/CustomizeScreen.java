package ui.screens;

import ui.GamePanel;
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

public class CustomizeScreen {

    private GamePanel gamePanel;
    private CharacterAppearance tempAppearance;
    private CharacterRenderer renderer;
    private Wardrobe wardrobe;
    private boolean bodyMode = false; // false = main clothing screen, true = body customization

    private boolean clothingSelectionMode = false;
    private ClothingType selectedClothingType = null;
    private Rectangle clothingSelectionArea = new Rectangle(50, 150, 700, 600);

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
        this.wardrobe = new Wardrobe(data.getUnlockedClothingIds());
        if (wardrobe.getUnlockedClothingIds().isEmpty()) {
            wardrobe.unlockDefaults();
        }
    }

    public void handleClick(int x, int y) {
        if (clothingSelectionMode) {
            // Handle clothing selection clicks
            if (clothingSelectionArea.contains(x, y)) {
                handleClothingSelectionClick(x, y);
            } else {
                // Click outside, close selection
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
         List<ClothingItem> items = wardrobe.getUnlockedByType(selectedClothingType);

         // Safety check for empty wardrobe
         if (items.isEmpty()) {
             return;
         }

         int cols = 4; // Match the drawClothingSelection cols
         int rows = (int) Math.ceil(items.size() / (double) cols);
         int itemWidth = clothingSelectionArea.width / cols;
         int itemHeight = clothingSelectionArea.height / rows;
         int padding = 15;
         int cardSize = 100; // Match the drawClothingSelection cardSize

        for (int i = 0; i < items.size(); i++) {
            int itemX = clothingSelectionArea.x + (i % cols) * itemWidth + padding;
            int itemY = clothingSelectionArea.y + (i / cols) * itemHeight + padding + 60;

            if (x >= itemX && x <= itemX + cardSize && y >= itemY && y <= itemY + cardSize) {
                ClothingItem selectedItem = items.get(i);
                switch (selectedClothingType) {
                    case TOP -> tempAppearance.setEquippedTopId(selectedItem.getName().toLowerCase());
                    case BOTTOM -> tempAppearance.setEquippedBottomId(selectedItem.getName().toLowerCase());
                    case ACCESSORY -> tempAppearance.setEquippedAccessoryId(selectedItem.getName().toLowerCase());
                }
                // Don't close selection mode - allow user to try different items
                return;
            }
        }
    }

     private void drawClothingSelection(Graphics2D g) {
         if (selectedClothingType == null) return;

         // Modern modal background with blur effect
         g.setColor(new Color(0, 0, 0, 200));
         g.fill(clothingSelectionArea);

         // Gradient border
         GradientPaint borderGradient = new GradientPaint(
             clothingSelectionArea.x, clothingSelectionArea.y, new Color(100, 200, 255),
             clothingSelectionArea.x + clothingSelectionArea.width, clothingSelectionArea.y + clothingSelectionArea.height, new Color(147, 112, 219)
         );
         g.setPaint(borderGradient);
         g.setStroke(new BasicStroke(3));
         g.drawRoundRect(clothingSelectionArea.x, clothingSelectionArea.y, clothingSelectionArea.width, clothingSelectionArea.height, 16, 16);
         g.setStroke(new BasicStroke(1));
         g.setPaint(null);

         // Title with styling
         g.setColor(new Color(100, 200, 255));
         g.setFont(new Font("Segoe UI", Font.BOLD, 28));
         g.drawString("Select " + selectedClothingType.toString().toLowerCase(), clothingSelectionArea.x + 30, clothingSelectionArea.y + 40);

         // Subtitle
         g.setColor(new Color(150, 150, 170));
         g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
         g.drawString("Click an item to equip it", clothingSelectionArea.x + 30, clothingSelectionArea.y + 65);

         List<ClothingItem> items = wardrobe.getUnlockedByType(selectedClothingType);

         // Handle empty wardrobe with modern styling
         if (items.isEmpty()) {
             g.setColor(new Color(150, 150, 170));
             g.setFont(new Font("Segoe UI", Font.PLAIN, 18));
             g.drawString("No items unlocked yet!", clothingSelectionArea.x + 30, clothingSelectionArea.y + 120);
             g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
             g.drawString("Play the game to unlock more clothing", clothingSelectionArea.x + 30, clothingSelectionArea.y + 145);
             return;
         }

         int cols = 4; // More columns for smaller icons
         int rows = (int) Math.ceil(items.size() / (double) cols);
         int itemWidth = clothingSelectionArea.width / cols;
         int itemHeight = clothingSelectionArea.height / rows;
         int padding = 15;
         int cardSize = 100; // Fixed smaller square size

        for (int i = 0; i < items.size(); i++) {
            ClothingItem item = items.get(i);
            int itemX = clothingSelectionArea.x + (i % cols) * itemWidth + padding;
            int itemY = clothingSelectionArea.y + (i / cols) * itemHeight + padding + 60;
            int cardWidth = cardSize;
            int cardHeight = cardSize;

            // Draw item card
            g.setColor(new Color(50, 50, 60));
            g.fillRoundRect(itemX, itemY, cardWidth, cardHeight, 12, 12);
            g.setColor(new Color(80, 80, 90));
            g.drawRoundRect(itemX, itemY, cardWidth, cardHeight, 12, 12);

            // Draw item icon or name
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
                // Draw icon with shadow
                g.setColor(new Color(0, 0, 0, 30));
                g.fillRect(itemX + 20, itemY + 20, cardWidth - 40, cardHeight - 60);
                g.drawImage(icon, itemX + 20, itemY + 20, cardWidth - 40, cardHeight - 60, null);
            } else {
                // Fallback colored rectangle
                g.setColor(new Color(60, 60, 70));
                g.fillRect(itemX + 20, itemY + 20, cardWidth - 40, cardHeight - 60);
            }

            // Item name
            g.setColor(Color.WHITE);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            String name = item.getName();
            if (name.length() > 12) name = name.substring(0, 12) + "...";
            g.drawString(name, itemX + cardWidth / 2 - g.getFontMetrics().stringWidth(name) / 2, itemY + cardHeight - 15);
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
}
