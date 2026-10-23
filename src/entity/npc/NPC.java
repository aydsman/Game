package entity.npc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;
import entity.npc.DialogBlock;
import entity.npc.DialogChoice;

public class NPC {
    private int x;
    private int y;
    private int width;
    private int height;
    private String name;
    private List<DialogBlock> dialogBlocks;
    private int interactionRadius;
    private Color color;

    private String screenName = null;

    private boolean isInDialog;
    private int currentBlockIndex;
    private int selectedChoice;
    private boolean isShowingDialog;
    private boolean isInRange;

    // Dialog box settings
    private static final int DIALOG_PADDING = 15;
    private static final int DIALOG_MARGIN = 10;
    private static final int DIALOG_MAX_WIDTH = 300;
    private static final int DIALOG_LINE_SPACING = 5;

    public NPC(int x, int y, String name, List<DialogBlock> dialogBlocks) {
        this.x = x;
        this.y = y;
        this.width = 40;
        this.height = 40;
        this.name = name;
        this.dialogBlocks = new ArrayList<>(dialogBlocks);
        this.interactionRadius = 80;
        this.color = Color.YELLOW;
        this.isInDialog = false;
        this.currentBlockIndex = 0;
        this.selectedChoice = 0;
        this.isShowingDialog = false;
        this.isInRange = false;
    }

    public void draw(Graphics2D g, int cameraX, int cameraY) {
        int screenX = x - cameraX;
        int screenY = y - cameraY;

        // Draw NPC body (rectangle)
        g.setColor(color);
        g.fillRect(screenX, screenY, width, height);
        g.setColor(Color.WHITE);
        g.drawRect(screenX, screenY, width, height);

        // Draw "E" indicator when player is nearby (in range and not in dialog)
        if (isInRange && !isShowingDialog) {
            g.setColor(Color.WHITE);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
            String eText = "[E] " + name;
            FontMetrics fm = g.getFontMetrics();
            int eWidth = fm.stringWidth(eText);
            g.drawString(eText, screenX + (width - eWidth) / 2, screenY - 5);
        }
    }

    public void drawDialog(Graphics2D g, int cameraX, int cameraY) {
        if (!isShowingDialog || currentBlockIndex >= dialogBlocks.size()) {
            return;
        }

        int screenX = x - cameraX;
        int screenY = y - cameraY;
        DialogBlock currentBlock = dialogBlocks.get(currentBlockIndex);

        // Set font for dialog
        g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        FontMetrics fm = g.getFontMetrics();

        // Calculate dialog box dimensions
        int maxLineWidth = 0;
        List<String> lines = wrapText(currentBlock.getMessage(), DIALOG_MAX_WIDTH, fm);
        for (String line : lines) {
            int lineWidth = fm.stringWidth(line);
            if (lineWidth > maxLineWidth) {
                maxLineWidth = lineWidth;
            }
        }

        int textWidth = Math.min(maxLineWidth, DIALOG_MAX_WIDTH);
        int textHeight = (lines.size() * fm.getHeight()) + ((lines.size() - 1) * DIALOG_LINE_SPACING);
        int nameWidth = fm.stringWidth(name);

        // Calculate box dimensions
        int boxWidth = Math.max(textWidth, nameWidth) + (DIALOG_PADDING * 2);
        int boxHeight = textHeight + (DIALOG_PADDING * 2) + fm.getHeight() + 5; // extra space for name

        // Position dialog box above NPC
        int boxX = screenX + (width / 2) - (boxWidth / 2);
        int boxY = screenY - boxHeight - DIALOG_MARGIN;

        // Draw translucent background
        g.setColor(new Color(30, 30, 50, 220));
        g.fillRect(boxX, boxY, boxWidth, boxHeight);
        g.setColor(new Color(100, 100, 150, 255));
        g.drawRect(boxX, boxY, boxWidth, boxHeight);

        // Draw NPC name
        g.setColor(new Color(200, 200, 255));
        int nameX = boxX + (boxWidth - nameWidth) / 2;
        int nameY = boxY + DIALOG_PADDING + fm.getAscent();
        g.drawString(name, nameX, nameY);

        // Draw dialog message (white text)
        g.setColor(Color.WHITE);
        int textY = nameY + 5 + fm.getHeight();
        for (String line : lines) {
            g.drawString(line, boxX + DIALOG_PADDING, textY);
            textY += fm.getHeight() + DIALOG_LINE_SPACING;
        }

        // Draw dialog choices if available
        List<DialogChoice> choices = currentBlock.getChoices();
        if (choices != null && !choices.isEmpty()) {
            int choiceY = textY + DIALOG_PADDING;
            for (int i = 0; i < choices.size(); i++) {
                DialogChoice choice = choices.get(i);
                g.setColor(i == selectedChoice ? Color.YELLOW : Color.WHITE);
                g.drawString(choice.getText(), boxX + DIALOG_PADDING, choiceY);
                choiceY += fm.getHeight() + DIALOG_LINE_SPACING;
            }
        }
    }

    private List<String> wrapText(String text, int maxWidth, FontMetrics fm) {
        List<String> lines = new ArrayList<>();
        if (fm.stringWidth(text) <= maxWidth) {
            lines.add(text);
            return lines;
        }

        StringBuilder currentLine = new StringBuilder();
        String[] words = text.split(" ");

        for (String word : words) {
            int testWidth = fm.stringWidth(currentLine.toString() + " " + word);
            if (currentLine.length() == 0) {
                currentLine.append(word);
            } else if (testWidth <= maxWidth) {
                currentLine.append(" ").append(word);
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    public void startDialog() {
        isInDialog = true;
        isShowingDialog = true;
        currentBlockIndex = 0;
        selectedChoice = 0;
    }

    public void cycleChoiceUp() {
        if (isShowingDialog && currentBlockIndex < dialogBlocks.size()) {
            DialogBlock currentBlock = dialogBlocks.get(currentBlockIndex);
            if (currentBlock.hasChoices()) {
                selectedChoice = (selectedChoice - 1 + currentBlock.getChoices().size()) % currentBlock.getChoices().size();
            }
        }
    }

    public void cycleChoiceDown() {
        if (isShowingDialog && currentBlockIndex < dialogBlocks.size()) {
            DialogBlock currentBlock = dialogBlocks.get(currentBlockIndex);
            if (currentBlock.hasChoices()) {
                selectedChoice = (selectedChoice + 1) % currentBlock.getChoices().size();
            }
        }
    }

    public String advanceDialog() {
        if (!isShowingDialog || currentBlockIndex >= dialogBlocks.size()) {
            return null;
        }

        DialogBlock currentBlock = dialogBlocks.get(currentBlockIndex);
        List<DialogChoice> choices = currentBlock.getChoices();
        String action = "next"; // default
        if (choices != null && !choices.isEmpty()) {
            // Blocks can have differing choice counts, so keep the cursor in range.
            selectedChoice = Math.min(selectedChoice, choices.size() - 1);
            DialogChoice selected = choices.get(selectedChoice);
            action = selected.getAction();
        }

        if (action.equals("next")) {
            currentBlockIndex++;
            selectedChoice = 0;
            if (currentBlockIndex >= dialogBlocks.size()) {
                endDialog();
                return null;
            }
        } else if (action.startsWith("block:")) {
            currentBlockIndex = Integer.parseInt(action.substring(6));
            selectedChoice = 0;
            if (currentBlockIndex >= dialogBlocks.size()) {
                endDialog();
                return null;
            }
        } else {
            // For screen actions, return the action
            currentBlockIndex++;
            if (currentBlockIndex >= dialogBlocks.size()) {
                endDialog();
            }
            return action;
        }
        return null;
    }

    public void endDialog() {
        isInDialog = false;
        isShowingDialog = false;
        currentBlockIndex = 0;
    }

    public boolean isInRange(int playerX, int playerY) {
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        double distance = Math.sqrt(Math.pow(playerX - centerX, 2) + Math.pow(playerY - centerY, 2));
        return distance <= interactionRadius;
    }

    public boolean getIsInRange() {
        return isInRange;
    }

    public void setInRange(boolean inRange) {
        this.isInRange = inRange;
    }

    public boolean isShowingDialog() {
        return isShowingDialog;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public String getName() { return name; }

    public void setColor(Color color) { this.color = color; }
    public String getScreenName() { return screenName; }
    public void setScreenName(String screenName) { this.screenName = screenName; }
}
