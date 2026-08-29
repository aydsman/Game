package entity.npc;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

public class NPC {
    protected int x;
    protected int y;
    protected String name;
    protected List<DialogBlock> dialogBlocks;
    protected Color color = Color.CYAN;
    protected boolean inRange = false;
    protected boolean showingDialog = false;
    protected int dialogIndex = 0;
    protected int choiceIndex = 0;
    protected String screenName = null;
    protected int interactionRadius = 100;

    public NPC(int x, int y, String name, List<DialogBlock> dialogBlocks) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.dialogBlocks = dialogBlocks;
    }

    public void setColor(Color color) { this.color = color; }
    public String getScreenName() { return screenName; }

    public boolean isInRange(int px, int py) {
        double dx = px - (x + 25);
        double dy = py - (y + 25);
        return Math.sqrt(dx * dx + dy * dy) <= interactionRadius;
    }

    public void setInRange(boolean inRange) { this.inRange = inRange; }

    public void startDialog() {
        dialogIndex = 0;
        choiceIndex = 0;
        showingDialog = true;
    }

    public boolean isShowingDialog() { return showingDialog; }

    public String advanceDialog() {
        if (dialogBlocks == null || dialogBlocks.isEmpty()) {
            showingDialog = false;
            return null;
        }
        DialogBlock block = dialogBlocks.get(dialogIndex);
        if (block.hasChoices()) {
            DialogChoice choice = block.getChoices().get(choiceIndex);
            String action = choice.getAction();
            if ("next".equals(action)) {
                dialogIndex = Math.min(dialogIndex + 1, dialogBlocks.size() - 1);
                if (dialogIndex >= dialogBlocks.size() - 1 && choiceIndex == block.getChoices().size() - 1) {
                    showingDialog = false;
                }
            } else if (action != null && action.startsWith("screen:")) {
                showingDialog = false;
                return action;
            }
            return action;
        }
        dialogIndex++;
        if (dialogIndex >= dialogBlocks.size()) {
            showingDialog = false;
        }
        return null;
    }

    public void cycleChoiceUp() {
        if (dialogBlocks == null || dialogIndex >= dialogBlocks.size()) return;
        DialogBlock block = dialogBlocks.get(dialogIndex);
        if (!block.hasChoices()) return;
        choiceIndex = (choiceIndex - 1 + block.getChoices().size()) % block.getChoices().size();
    }

    public void cycleChoiceDown() {
        if (dialogBlocks == null || dialogIndex >= dialogBlocks.size()) return;
        DialogBlock block = dialogBlocks.get(dialogIndex);
        if (!block.hasChoices()) return;
        choiceIndex = (choiceIndex + 1) % block.getChoices().size();
    }

    public void draw(Graphics2D g, int cameraX, int cameraY) {
        g.setColor(color);
        g.fillRect(x - cameraX, y - cameraY, 50, 50);
        if (inRange) {
            g.setColor(Color.WHITE);
            g.drawString(name, x - cameraX, y - cameraY - 5);
        }
    }

    public void drawDialog(Graphics2D g, int cameraX, int cameraY) {
        if (!showingDialog || dialogBlocks == null || dialogIndex >= dialogBlocks.size()) return;
        DialogBlock block = dialogBlocks.get(dialogIndex);
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(200, 600, 1000, 200);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString(block.getMessage(), 220, 640);
        if (block.hasChoices()) {
            int y = 680;
            for (int i = 0; i < block.getChoices().size(); i++) {
                DialogChoice c = block.getChoices().get(i);
                String prefix = (i == choiceIndex) ? "> " : "  ";
                g.drawString(prefix + c.getText(), 240, y);
                y += 24;
            }
        } else {
            g.drawString("[E/Space] Continue", 220, 720);
        }
    }
}
