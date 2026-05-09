package ui;

import entity.Player;
import combat.consumables.Consumable;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Map;

public class HUD {

    private InventoryUI inventoryUI;

    public HUD() {
        inventoryUI = new InventoryUI();
    }

    public void draw(Graphics2D g, Player player, int screenWidth, int screenHeight) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("HP: " + (int) player.getHp() + " / " + (int) player.getMaxHp(), 10, 30);

        // Draw HP bar
        int barWidth = 200;
        int barHeight = 10;
        int barX = 10;
        int barY = 40;
        double hpPercent = player.getHp() / player.getMaxHp();
        g.setColor(Color.RED);
        g.fillRect(barX, barY, barWidth, barHeight);
        g.setColor(Color.GREEN);
        g.fillRect(barX, barY, (int) (barWidth * hpPercent), barHeight);

        // Draw XP bar (using persistent player level from save)
        int xpBarY = barY + barHeight + 15;
        int playerLevel = player.getPlayerLevel();
        int playerXP = player.getPlayerXP();
        int xpNeeded = playerLevel * 100;
        double xpPercent = (double) playerXP / xpNeeded;
        g.setColor(Color.GRAY);
        g.fillRect(barX, xpBarY, barWidth, barHeight);
        g.setColor(Color.YELLOW);
        g.fillRect(barX, xpBarY, (int) (barWidth * xpPercent), barHeight);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Level " + playerLevel + " (" + playerXP + " / " + xpNeeded + " XP)", barX, xpBarY - 2);

        // Display weapon name below HP
        if (player.getHeldWeapon() != null) {
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Weapon: " + player.getHeldWeapon().getName(), 10, 90);

            // Only show ammo for ranged weapons
            if (player.getHeldWeapon() instanceof combat.Ranged) {
                combat.Ranged ranged = (combat.Ranged) player.getHeldWeapon();
                if (ranged.isReloading()) {
                    g.drawString("Ammo: Reloading", 10, 115);
                } else {
                    g.drawString("Ammo: " + ranged.getCurrentAmmo() + " / " + ranged.getMagazineSize(), 10, 115);
                }
            }
        } else {
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Weapon: None", 10, 90);
        }

        // Draw active consumable effects (top-left area, below HP/XP bars)
        drawActiveConsumableEffects(g, player, 10, 130);

        // Draw hotbar
        inventoryUI.draw(g, player.getHotbar(), screenWidth, screenHeight);
    }

    private void drawActiveConsumableEffects(Graphics2D g, Player player, int startX, int startY) {
        Map<Consumable.ConsumableEffectType, Player.ActiveConsumableEffect> effects = player.getActiveEffects();
        if (effects.isEmpty()) return;

        int iconSize = 32;
        int spacing = 8;
        int currentX = startX;
        int currentY = startY;

        g.setFont(new Font("Arial", Font.BOLD, 14));

        for (Player.ActiveConsumableEffect effect : effects.values()) {
            if (effect.isExpired()) continue;

            // Draw icon background
            Color iconColor = getEffectColor(effect.type);
            g.setColor(iconColor);
            g.fillRect(currentX, currentY, iconSize, iconSize);

            // Draw icon border
            g.setColor(Color.WHITE);
            g.drawRect(currentX, currentY, iconSize, iconSize);

            // Draw timer text next to icon
            long remainingSeconds = (effect.getRemainingMs() + 999) / 1000; // Round up
            String timerText = remainingSeconds + "s";

            g.setColor(Color.WHITE);
            g.drawString(timerText, currentX + iconSize + 5, currentY + iconSize / 2 + 5);

            // Draw effect name below
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            String effectName = getEffectDisplayName(effect.type);
            g.drawString(effectName, currentX, currentY + iconSize + 12);
            g.setFont(new Font("Arial", Font.BOLD, 14));

            // Move to next position (horizontal layout)
            currentX += iconSize + 50;

            // Wrap to next row if too far right (but stay in top-left area)
            if (currentX > 250) {
                currentX = startX;
                currentY += iconSize + 25;
            }
        }
    }

    private Color getEffectColor(Consumable.ConsumableEffectType type) {
        switch (type) {
            case DAMAGE_BOOST:
                return new Color(255, 50, 50); // Red
            case SPEED_BOOST:
                return new Color(50, 150, 255); // Blue
            case DEFENSE_BOOST:
                return new Color(100, 200, 100); // Green
            default:
                return new Color(200, 200, 100); // Yellow
        }
    }

    private String getEffectDisplayName(Consumable.ConsumableEffectType type) {
        switch (type) {
            case DAMAGE_BOOST:
                return "DMG x2";
            case SPEED_BOOST:
                return "SPD+";
            case DEFENSE_BOOST:
                return "DEF+";
            default:
                return "BUFF";
        }
    }

    public InventoryUI getInventoryUI() {
        return inventoryUI;
    }
}
