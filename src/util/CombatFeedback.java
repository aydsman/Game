package util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Short-lived combat visuals shared by every damage source: floating damage numbers,
 * impact sparks and screen shake. Positions are world space; {@link #draw} applies the camera.
 */
public class CombatFeedback {

    private static final long POPUP_LIFETIME_MS = 650;
    private static final long CRIT_POPUP_LIFETIME_MS = 800;
    private static final long SPARK_LIFETIME_MS = 200;
    private static final long CRIT_SPARK_LIFETIME_MS = 320;
    private static final int MAX_POPUPS = 48;
    private static final int MAX_SPARKS = 140;
    private static final int SPARKS_PER_HIT = 4;
    private static final int SPARKS_PER_CRIT = 10;
    private static final int BIG_HIT_THRESHOLD = 40;
    private static final double POPUP_RISE_PX_PER_MS = 0.035;

    private static final Color HIT_COLOR = new Color(255, 240, 190);
    private static final Color BIG_HIT_COLOR = new Color(255, 160, 60);
    private static final Color PLAYER_HURT_COLOR = new Color(255, 95, 95);
    private static final Color CRIT_COLOR = new Color(255, 214, 64);
    private static final Color CRIT_OUTLINE_COLOR = new Color(120, 40, 0);
    /** Label shown when a hit is cancelled by dodge invulnerability. */
    public static final Color DODGE_LABEL_COLOR = new Color(140, 220, 255);

    private final List<Popup> popups = new ArrayList<>();
    private final List<Spark> sparks = new ArrayList<>();
    private final Random random = new Random();

    private double shakeMagnitude = 0;
    private long shakeEndMs = 0;
    private long shakeStartMs = 0;
    private int shakeOffsetX = 0;
    private int shakeOffsetY = 0;

    /**
     * Registers a hit: a rising damage number plus a spark burst.
     *
     * @param targetIsPlayer colours the number as incoming damage instead of outgoing
     */
    public void addHit(int worldX, int worldY, double amount, boolean targetIsPlayer) {
        addHit(worldX, worldY, amount, targetIsPlayer, false);
    }

    /**
     * Registers a hit, optionally as a critical one.
     *
     * <p>Crits get their own colour, a larger outlined number and a wider, longer-lived spark burst,
     * so a crit is distinguishable from an ordinary large hit at a glance.
     *
     * @param targetIsPlayer colours the number as incoming damage instead of outgoing
     * @param critical       draw this hit as a critical hit
     */
    public void addHit(int worldX, int worldY, double amount, boolean targetIsPlayer, boolean critical) {
        int rounded = (int) Math.round(amount);
        if (rounded <= 0) {
            return;
        }
        long now = System.currentTimeMillis();
        boolean big = rounded >= BIG_HIT_THRESHOLD;
        boolean crit = critical && !targetIsPlayer;

        Popup popup = new Popup();
        popup.x = worldX + random.nextInt(19) - 9;
        popup.y = worldY - (crit ? 18 : 12);
        popup.text = crit ? rounded + "!" : String.valueOf(rounded);
        popup.color = targetIsPlayer ? PLAYER_HURT_COLOR : (crit ? CRIT_COLOR : (big ? BIG_HIT_COLOR : HIT_COLOR));
        popup.fontSize = crit ? 30 : (targetIsPlayer ? 20 : (big ? 22 : 16));
        popup.lifetimeMs = crit ? CRIT_POPUP_LIFETIME_MS : POPUP_LIFETIME_MS;
        popup.outlineColor = crit ? CRIT_OUTLINE_COLOR : null;
        popup.bornMs = now;
        add(popups, popup, MAX_POPUPS);

        Color sparkColor = targetIsPlayer ? PLAYER_HURT_COLOR : (crit ? CRIT_COLOR : (big ? BIG_HIT_COLOR : HIT_COLOR));
        int sparkCount = crit ? SPARKS_PER_CRIT : SPARKS_PER_HIT;
        for (int i = 0; i < sparkCount; i++) {
            Spark spark = new Spark();
            spark.x = worldX;
            spark.y = worldY;
            spark.angle = random.nextDouble() * Math.PI * 2;
            spark.length = crit ? 16 + random.nextInt(20) : 8 + random.nextInt(big ? 16 : 8);
            spark.color = sparkColor;
            spark.lifetimeMs = crit ? CRIT_SPARK_LIFETIME_MS : SPARK_LIFETIME_MS;
            spark.bornMs = now;
            add(sparks, spark, MAX_SPARKS);
        }
    }

    /**
     * Registers a short text marker at a world position, for events that are not damage — a dodged
     * hit, an immunity, a status landing. Without one, a negated hit looks like a missed hit.
     *
     * @param text  short label; ignored when null or blank
     * @param color label colour; falls back to white when null
     */
    public void addLabel(int worldX, int worldY, String text, Color color) {
        if (text == null || text.isBlank()) {
            return;
        }
        Popup popup = new Popup();
        popup.x = worldX;
        popup.y = worldY - 12;
        popup.text = text;
        popup.color = color == null ? Color.WHITE : color;
        popup.fontSize = 16;
        popup.lifetimeMs = POPUP_LIFETIME_MS;
        popup.bornMs = System.currentTimeMillis();
        add(popups, popup, MAX_POPUPS);
    }

    /** Requests a shake; a stronger request overrides a weaker one still in progress. */
    public void addShake(double magnitude, long durationMs) {
        if (magnitude <= 0 || durationMs <= 0) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now < shakeEndMs && magnitude <= shakeMagnitude) {
            return;
        }
        shakeMagnitude = magnitude;
        shakeStartMs = now;
        shakeEndMs = now + durationMs;
    }

    public void update() {
        long now = System.currentTimeMillis();
        popups.removeIf(p -> now - p.bornMs >= p.lifetimeMs);
        sparks.removeIf(s -> now - s.bornMs >= s.lifetimeMs);

        if (now >= shakeEndMs) {
            shakeMagnitude = 0;
            shakeOffsetX = 0;
            shakeOffsetY = 0;
            return;
        }
        double remaining = 1.0 - (now - shakeStartMs) / (double) Math.max(1, shakeEndMs - shakeStartMs);
        double amplitude = shakeMagnitude * remaining;
        shakeOffsetX = (int) Math.round((random.nextDouble() * 2 - 1) * amplitude);
        shakeOffsetY = (int) Math.round((random.nextDouble() * 2 - 1) * amplitude);
    }

    public int getShakeOffsetX() {
        return shakeOffsetX;
    }

    public int getShakeOffsetY() {
        return shakeOffsetY;
    }

    public void draw(Graphics2D g, int cameraX, int cameraY) {
        long now = System.currentTimeMillis();

        java.awt.Stroke oldStroke = g.getStroke();
        g.setStroke(new java.awt.BasicStroke(2f));
        for (Spark spark : sparks) {
            double life = 1.0 - (now - spark.bornMs) / (double) spark.lifetimeMs;
            if (life <= 0) {
                continue;
            }
            int sx = (int) spark.x - cameraX;
            int sy = (int) spark.y - cameraY;
            int ex = sx + (int) Math.round(Math.cos(spark.angle) * spark.length * (1.4 - life));
            int ey = sy + (int) Math.round(Math.sin(spark.angle) * spark.length * (1.4 - life));
            g.setColor(withAlpha(spark.color, life));
            g.drawLine(sx, sy, ex, ey);
        }
        g.setStroke(oldStroke);

        for (Popup popup : popups) {
            long age = now - popup.bornMs;
            double life = 1.0 - age / (double) popup.lifetimeMs;
            if (life <= 0) {
                continue;
            }
            int px = (int) popup.x - cameraX;
            int py = (int) popup.y - cameraY - (int) Math.round(age * POPUP_RISE_PX_PER_MS);
            g.setFont(new Font("Segoe UI", Font.BOLD, popup.fontSize));
            if (popup.outlineColor != null) {
                // Ring the glyphs so a bright crit number stays legible over bright effects.
                g.setColor(withAlpha(popup.outlineColor, life));
                for (int dx = -2; dx <= 2; dx += 2) {
                    for (int dy = -2; dy <= 2; dy += 2) {
                        if (dx != 0 || dy != 0) {
                            g.drawString(popup.text, px + dx, py + dy);
                        }
                    }
                }
            } else {
                g.setColor(withAlpha(Color.BLACK, life * 0.6));
                g.drawString(popup.text, px + 1, py + 1);
            }
            g.setColor(withAlpha(popup.color, life));
            g.drawString(popup.text, px, py);
        }
    }

    public void clear() {
        popups.clear();
        sparks.clear();
        shakeMagnitude = 0;
        shakeEndMs = 0;
        shakeOffsetX = 0;
        shakeOffsetY = 0;
    }

    private static <T> void add(List<T> target, T item, int cap) {
        if (target.size() >= cap) {
            target.remove(0);
        }
        target.add(item);
    }

    private static Color withAlpha(Color base, double fraction) {
        int alpha = (int) Math.round(Math.max(0, Math.min(1, fraction)) * 255);
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);
    }

    private static class Popup {
        double x;
        double y;
        String text;
        Color color;
        /** Non-null draws an outline around the glyphs instead of a drop shadow. */
        Color outlineColor;
        int fontSize;
        long lifetimeMs = POPUP_LIFETIME_MS;
        long bornMs;
    }

    private static class Spark {
        double x;
        double y;
        double angle;
        int length;
        Color color;
        long lifetimeMs = SPARK_LIFETIME_MS;
        long bornMs;
    }
}
