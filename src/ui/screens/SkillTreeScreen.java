package ui.screens;

import save.SaveData;
import save.SaveManager;
import skilltree.SkillEffects;
import skilltree.SkillNode;
import skilltree.SkillPillar;
import skilltree.SkillTreeData;
import skilltree.SkillTreeState;
import ui.GamePanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

public class SkillTreeScreen {
    private final GamePanel gamePanel;
    private final Map<String, Rectangle> nodeBounds = new HashMap<>();
    private final Map<SkillPillar, Point> pillarRoots = new HashMap<>();

    private String selectedNodeId = null;
    private Rectangle unlockBtn = new Rectangle(0, 0, 0, 0);
    private Rectangle backBtn = new Rectangle(30, 20, 120, 46);
    private Rectangle respecBtn = new Rectangle(610, 830, 380, 48);
    private boolean respecArmed = false;
    private int hoverX;
    private int hoverY;

    private static final int ROOT_Y = 765;
    private static final int TIER_STEP_Y = 110;
    private static final int BRANCH_STEP_X = 55;
    private static final int NODE_SIZE = 54;
    private static final int ROOT_TRUNK_HEIGHT = 125;

    public SkillTreeScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        pillarRoots.put(SkillPillar.COMBAT, new Point(270, ROOT_Y));
        pillarRoots.put(SkillPillar.VITALITY, new Point(800, ROOT_Y));
        pillarRoots.put(SkillPillar.ABUNDANCE, new Point(1330, ROOT_Y));
    }

    private SaveData saveData() {
        return SaveManager.load();
    }

    private SkillTreeState state() {
        return new SkillTreeState(saveData());
    }

    public void handleMouseMove(int x, int y) {
        this.hoverX = x;
        this.hoverY = y;
    }

    public void handleClick(int x, int y) {
        if (backBtn.contains(x, y)) {
            gamePanel.switchScreen("menu");
            return;
        }
        SkillTreeState state = state();

        if (respecBtn.contains(x, y)) {
            if (!respecArmed) {
                respecArmed = true;
                return;
            }
            state.fullRespec();
            SaveManager.save(saveData());
            SkillEffects.invalidate();
            selectedNodeId = null;
            respecArmed = false;
            return;
        }

        if (unlockBtn.contains(x, y) && selectedNodeId != null) {
            SkillNode selected = SkillTreeData.getById(selectedNodeId);
            if (selected != null && state.tryUnlock(selected)) {
                SaveManager.save(saveData());
                SkillEffects.invalidate();
                respecArmed = false;
            }
            return;
        }

        String clickedNode = findNodeAt(x, y);
        if (clickedNode != null) {
            selectedNodeId = clickedNode;
            respecArmed = false;
            return;
        }

        // Clicked away from nodes/actions: close the floating detail card.
        selectedNodeId = null;
        respecArmed = false;
    }

    private String findNodeAt(int x, int y) {
        for (Map.Entry<String, Rectangle> e : nodeBounds.entrySet()) {
            if (e.getValue().contains(x, y)) {
                return e.getKey();
            }
        }
        return null;
    }

    public void draw(Graphics2D g, int screenWidth, int screenHeight) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint bg = new GradientPaint(0, 0, new Color(18, 20, 28), 0, screenHeight, new Color(10, 12, 18));
        g.setPaint(bg);
        g.fillRect(0, 0, screenWidth, screenHeight);
        drawGrid(g, screenWidth, screenHeight);
        drawHeader(g, screenWidth);
        drawRoots(g);

        nodeBounds.clear();
        drawActiveTreeLines(g);
        drawNodes(g);
        drawRespecBar(g);

        if (selectedNodeId != null) {
            drawFloatingCard(g, state(), SkillTreeData.getById(selectedNodeId));
        } else {
            unlockBtn = new Rectangle(0, 0, 0, 0);
        }
    }

    private void drawGrid(Graphics2D g, int w, int h) {
        g.setColor(new Color(255, 255, 255, 10));
        for (int x = 0; x < w; x += 40) g.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 40) g.drawLine(0, y, w, y);
    }

    private void drawHeader(Graphics2D g, int w) {
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, 0, w, 80);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 30));
        g.drawString("Skill Tree", 180, 50);

        int points = saveData().getSkillPoints();
        g.setColor(new Color(120, 220, 255));
        g.setFont(new Font("Segoe UI", Font.BOLD, 22));
        String sp = "Skill Points: " + points;
        FontMetrics fm = g.getFontMetrics();
        g.drawString(sp, w - fm.stringWidth(sp) - 40, 47);

        drawButton(g, backBtn, "Back", new Color(90, 100, 120), new Color(130, 150, 180), backBtn.contains(hoverX, hoverY));
    }

    private void drawRoots(Graphics2D g) {
        for (SkillPillar pillar : SkillPillar.values()) {
            Point root = pillarRoots.get(pillar);
            Color c = pillar.getBaseColor();
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 140));
            g.fillRoundRect(root.x - 10, root.y - ROOT_TRUNK_HEIGHT, 20, ROOT_TRUNK_HEIGHT, 12, 12);

            g.setColor(c);
            g.fillRoundRect(root.x - 16, root.y - 16, 32, 32, 8, 8);
            g.setColor(new Color(255, 255, 255, 160));
            g.drawRoundRect(root.x - 16, root.y - 16, 32, 32, 8, 8);

            g.setColor(pillar.getBaseColor());
            g.setFont(new Font("Segoe UI", Font.BOLD, 20));
            String name = pillar.getDisplayName();
            int tw = g.getFontMetrics().stringWidth(name);
            g.drawString(name, root.x - tw / 2, root.y + 38);
        }
    }

    private Point nodeCenter(SkillNode node) {
        Point root = pillarRoots.get(node.getPillar());
        int x = root.x + node.getBranchOffset() * BRANCH_STEP_X;
        int y = root.y - node.getTier() * TIER_STEP_Y;
        return new Point(x, y);
    }

    private void drawActiveTreeLines(Graphics2D g) {
        SkillTreeState state = state();
        for (SkillNode node : SkillTreeData.allNodes()) {
            if (!state.isUnlocked(node.getId())) {
                continue;
            }
            Point to = nodeCenter(node);
            if (node.getParents().isEmpty()) {
                Point from = pillarRoots.get(node.getPillar());
                drawNeonLine(g, from, to, node.getPillar().getGlowColor());
            } else {
                for (String parentId : node.getParents()) {
                    SkillNode parent = SkillTreeData.getById(parentId);
                    if (parent != null && state.isUnlocked(parentId)) {
                        drawNeonLine(g, nodeCenter(parent), to, node.getPillar().getGlowColor());
                    }
                }
            }
        }
    }

    private void drawNeonLine(Graphics2D g, Point from, Point to, Color color) {
        g.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60));
        g.drawLine(from.x, from.y, to.x, to.y);
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(color);
        g.drawLine(from.x, from.y, to.x, to.y);
        g.setStroke(new BasicStroke(1f));
    }

    private void drawNodes(Graphics2D g) {
        SkillTreeState state = state();
        for (SkillNode node : SkillTreeData.allNodes()) {
            Point c = nodeCenter(node);
            Rectangle r = new Rectangle(c.x - NODE_SIZE / 2, c.y - NODE_SIZE / 2, NODE_SIZE, NODE_SIZE);
            nodeBounds.put(node.getId(), r);

            boolean unlocked = state.isUnlocked(node.getId());
            boolean available = state.canUnlock(node);
            boolean selected = node.getId().equals(selectedNodeId);

            Color base = node.getPillar().getBaseColor();
            Color fill;
            if (unlocked) fill = new Color(base.getRed(), base.getGreen(), base.getBlue(), 210);
            else if (available) fill = new Color(base.getRed(), base.getGreen(), base.getBlue(), 120);
            else fill = new Color(70, 70, 80, 180);

            g.setColor(fill);
            g.fillRoundRect(r.x, r.y, r.width, r.height, 8, 8);
            g.setColor(selected ? Color.WHITE : (unlocked ? node.getPillar().getGlowColor() : new Color(120, 120, 130)));
            g.setStroke(new BasicStroke(selected ? 3f : 2f));
            g.drawRoundRect(r.x, r.y, r.width, r.height, 8, 8);
            g.setStroke(new BasicStroke(1f));

            g.setColor(Color.WHITE);
            g.setFont(new Font("Segoe UI", Font.BOLD, 12));
            String tierLabel = "T" + node.getTier();
            g.drawString(tierLabel, r.x + 5, r.y + 15);

            g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            String cost = node.getCost() + " SP";
            int cw = g.getFontMetrics().stringWidth(cost);
            g.drawString(cost, r.x + r.width - cw - 4, r.y + r.height - 6);
        }
    }

    private void drawRespecBar(Graphics2D g) {
        Color base = respecArmed ? new Color(220, 80, 80) : new Color(120, 90, 90);
        Color glow = respecArmed ? new Color(255, 140, 140) : new Color(170, 120, 120);
        drawButton(g, respecBtn, respecArmed ? "Click again to confirm FREE respec" : "Respec (FREE)", base, glow, respecBtn.contains(hoverX, hoverY));
    }

    private void drawFloatingCard(Graphics2D g, SkillTreeState state, SkillNode node) {
        if (node == null) {
            return;
        }
        Point c = nodeCenter(node);
        int cardW = 280;
        int cardH = 190;
        int x = c.x + 45;
        int y = c.y - 95;
        if (x + cardW > 1570) x = c.x - cardW - 45;
        if (y < 90) y = 90;
        if (y + cardH > 810) y = 810 - cardH;

        g.setColor(new Color(12, 14, 20, 235));
        g.fillRoundRect(x, y, cardW, cardH, 12, 12);
        g.setColor(node.getPillar().getGlowColor());
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, cardW, cardH, 12, 12);
        g.setStroke(new BasicStroke(1f));

        g.setColor(node.getPillar().getBaseColor());
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g.drawString(node.getTitle(), x + 14, y + 30);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g.drawString("Tier " + node.getTier() + "  |  Cost: " + node.getCost() + " SP", x + 14, y + 52);
        g.drawString(node.getDescription(), x + 14, y + 74);
        g.drawString("Effect: " + effectText(node), x + 14, y + 96);
        g.drawString("Status: " + statusText(state, node), x + 14, y + 118);

        unlockBtn = new Rectangle(x + 14, y + 140, cardW - 28, 36);
        boolean canUnlock = state.canUnlock(node);
        boolean unlocked = state.isUnlocked(node.getId());
        String label = unlocked ? "Unlocked" : (canUnlock ? "Unlock" : "Locked");
        Color b = unlocked ? new Color(80, 140, 90) : (canUnlock ? node.getPillar().getBaseColor() : new Color(90, 90, 96));
        Color h = unlocked ? new Color(120, 170, 120) : (canUnlock ? node.getPillar().getGlowColor() : new Color(120, 120, 130));
        drawButton(g, unlockBtn, label, b, h, unlockBtn.contains(hoverX, hoverY) && !unlocked);
    }

    private String statusText(SkillTreeState state, SkillNode node) {
        if (state.isUnlocked(node.getId())) return "Unlocked";
        if (state.canUnlock(node)) return "Available";
        if (state.getSkillPoints() < node.getCost()) return "Need more skill points";
        return "Parent skill required";
    }

    private String effectText(SkillNode node) {
        return "+" + (int) node.getEffectValue() + "% " + switch (node.getEffectType()) {
            case DAMAGE_MULTIPLIER_PCT -> "damage";
            case RELOAD_SPEED_PCT -> "reload speed";
            case CRIT_CHANCE_PCT -> "crit chance";
            case MAX_HP_PCT -> "max HP";
            case MOVE_SPEED_PCT -> "move speed";
            case HEALING_RECEIVED_PCT -> "healing received";
            case CASH_GAIN_PCT -> "cash gain";
            case GEM_GAIN_PCT -> "gem gain";
            case LOOT_QUALITY_PCT -> "loot quality";
        };
    }

    private void drawButton(Graphics2D g, Rectangle r, String text, Color base, Color hover, boolean isHovered) {
        Color top = isHovered ? hover : base;
        GradientPaint gp = new GradientPaint(r.x, r.y, top, r.x, r.y + r.height, top.darker());
        g.setPaint(gp);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        g.setPaint(null);
        g.setColor(new Color(220, 220, 230));
        g.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
        int ty = r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(text, tx, ty);
    }
}
