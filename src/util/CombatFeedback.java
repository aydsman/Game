package util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Visual combat feedback: hit flashes and floating damage numbers.
 */
public class CombatFeedback {

    private static final long FLASH_MS = 140;
    private static final long NUMBER_MS = 900;

    private long flashEndMs = 0;
    private final List<DamageNumber> damageNumbers = new ArrayList<>();

    public void flashHit() {
        flashEndMs = System.currentTimeMillis() + FLASH_MS;
    }

    public void flashHit(int worldX, int worldY) {
        flashHit();
    }

    public boolean isFlashing() {
        return System.currentTimeMillis() < flashEndMs;
    }

    public void spawnDamageNumber(int worldX, int worldY, int damage) {
        if (damage <= 0) {
            return;
        }
        damageNumbers.add(new DamageNumber(worldX, worldY, damage, System.currentTimeMillis()));
    }

    public void update() {
        long now = System.currentTimeMillis();
        damageNumbers.removeIf(n -> now - n.startMs > NUMBER_MS);
    }

    public void draw(Graphics2D g, int cameraX, int cameraY) {
        long now = System.currentTimeMillis();
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        for (DamageNumber n : damageNumbers) {
            long elapsed = now - n.startMs;
            int drawX = n.x - cameraX;
            int drawY = n.y - cameraY - (int) (elapsed / 10);
            g.setColor(new Color(255, 220, 80));
            g.drawString(String.valueOf(n.damage), drawX, drawY);
        }
    }

    private static final class DamageNumber {
        final int x;
        final int y;
        final int damage;
        final long startMs;

        DamageNumber(int x, int y, int damage, long startMs) {
            this.x = x;
            this.y = y;
            this.damage = damage;
            this.startMs = startMs;
        }
    }
}
