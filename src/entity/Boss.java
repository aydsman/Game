package entity;

import java.awt.Color;
import java.awt.Graphics2D;

public class Boss extends Entity {

    public Boss(int x, int y) {
        super(x, y);
        // health
        hp = 1000;
        maxHp = 1000;
        // multipliers
        damage = 10.0;
        speed = 1.0;
        // visuals
        color = Color.BLACK;
    }

    public void move(Player player, int arenaWidth, int arenaHeight) {
        move(player, arenaWidth, arenaHeight, null);
    }

    public void move(Player player, int arenaWidth, int arenaHeight, java.util.List<java.awt.Rectangle> obstacles) {
        int step = Math.max(1, (int) Math.round(speed));
        world.arena.ArenaPathfinding.applyChaseStep(this, player, obstacles, arenaWidth, arenaHeight, step);
    }

    public void drawHealthBar(Graphics2D g, int screenWidth, int screenHeight) {
        int barWidth = 300;
        int barHeight = 20;
        int barX = (screenWidth - barWidth) / 2;
        int barY = 30;

        // Background
        g.setColor(Color.DARK_GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);

        // Health fill
        double healthPercent = (double) hp / maxHp;
        int fillWidth = (int) (barWidth * healthPercent);
        g.setColor(Color.RED);
        g.fillRect(barX, barY, fillWidth, barHeight);

        // Border
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);

        // HP text
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        String hpText = hp + " / " + maxHp;
        java.awt.FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(hpText);
        g.drawString(hpText, barX + (barWidth - textWidth) / 2, barY + 14);
    }
}
