package ui;

import entity.Player;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

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

        // Draw XP bar
        int xpBarY = barY + barHeight + 15;
        double xpPercent = player.getCurrentXP() / player.getMaxXP();
        g.setColor(Color.GRAY);
        g.fillRect(barX, xpBarY, barWidth, barHeight);
        g.setColor(Color.YELLOW);
        g.fillRect(barX, xpBarY, (int) (barWidth * xpPercent), barHeight);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Level " + player.getLevel() + " (" + (int) player.getCurrentXP() + " / " + (int) player.getMaxXP() + ")", barX, xpBarY - 2);

        // Display weapon name below HP
        if (player.getHeldWeapon() != null) {
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Weapon: " + player.getHeldWeapon().getName(), 10, 90);
            if (player.getHeldWeapon().isReloading()) {
                g.drawString("Ammo: Reloading", 10, 115);
            } else {
                g.drawString("Ammo: " + player.getHeldWeapon().getCurrentAmmo() + " / " + player.getHeldWeapon().getMagazineSize(), 10, 115);
            }
        } else {
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Weapon: None", 10, 90);
        }

        // Draw hotbar
        inventoryUI.draw(g, player.getHotbar(), screenWidth, screenHeight);
    }

    public InventoryUI getInventoryUI() {
        return inventoryUI;
    }
}
