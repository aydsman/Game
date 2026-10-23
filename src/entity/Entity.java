package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import combat.Item;
import world.arena.ArenaPathfinding;

public class Entity {
    protected int x, y; // position
    protected int w = 50, l = 50; // size (default 50x50, override in subclass if needed)
    protected double hp = 100, maxHp = 100; // health
    protected double damage = 1.0; // damage multiplier (default damage 1)
    protected double speed = 1.0; // speed multiplier (default speed 1)
    public boolean ranged = false; // whether this entity uses ranged attacks
    protected double barrelAngle = 0; // angle the barrel is pointing (in radians)
    protected Color color = Color.WHITE; // default color
    protected Item heldWeapon; // weapon the entity is holding (null if nothing)
    protected boolean dead = false; // death status
    /** Arena pathfinding state (enemies / bosses with obstacles). */
    protected final ArenaPathfinding.PathAgentState pathAgentState = new ArenaPathfinding.PathAgentState();

    public ArenaPathfinding.PathAgentState getPathAgentState() {
        return pathAgentState;
    }

    protected int level = 1; // entity level
    protected double currentXP = 0; // current experience points
    protected double maxXP = 100; // XP needed for next level
    protected int xpValue = 0; // XP paid out when this entity is killed
    protected int goldValue = 0; // gold paid out when this entity is killed
    private boolean killRewardClaimed = false;
    protected boolean debugMode = false; // debug mode for showing ranges
    private long hitFlashUntilMs = 0;
    private static final long HIT_FLASH_MS = 90;
    protected long stunnedUntilMs = 0; // wall-clock end time of an active stun
    protected long slowedUntilMs = 0;
    protected double slowSpeedMultiplier = 1.0;
    protected long fireTickUntilMs = 0;
    protected int fireTickDamagePerTick = 0;
    protected long fireTickIntervalMs = 500;
    protected long lastFireTickAppliedMs = 0;
    /** Optional telegraphed slam; null on entities that do not slam. */
    protected entity.attack.TelegraphedSlam slam;

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public int getW() { return w; }
    public int getL() { return l; }
    public double getSpeed() { return speed; }
    public double getHp() { return hp; }
    public double getMaxHp() { return maxHp; }
    public double getDamage() { return damage; }
    public void setHp(double hp) { this.hp = hp; }
    public void setMaxHp(double maxHp) { this.maxHp = maxHp; }
    public int getCenterX() { return x + w / 2; }
    public int getCenterY() { return y + l / 2; }
    public boolean isDead() { return dead; }

    public entity.attack.TelegraphedSlam getSlam() {
        return slam;
    }

    /** Gives this entity a telegraphed slam using the active combat tuning. */
    public void enableSlam() {
        this.slam = new entity.attack.TelegraphedSlam(combat.CombatTuning.active());
    }
    public int getLevel() { return level; }
    public double getCurrentXP() { return currentXP; }
    public double getMaxXP() { return maxXP; }
    public double getBarrelAngle() { return barrelAngle; }

    public boolean checkCollision(int px, int py) {
        return px >= x && px <= x + w && py >= y && py <= y + l;
    }

    public int getXpValue() { return xpValue; }
    public int getGoldValue() { return goldValue; }

    /**
     * Returns true only the first time it is called, so kill payouts stay correct even when
     * several damage sources (bullet, swing, power tick) all see this entity dead on the same frame.
     */
    public boolean claimKillReward() {
        if (killRewardClaimed) {
            return false;
        }
        killRewardClaimed = true;
        return true;
    }

    public void takeDamage(double damageAmount) {
        hp -= damageAmount;
        checkDeath();
    }

    public void flashHit() {
        hitFlashUntilMs = System.currentTimeMillis() + HIT_FLASH_MS;
    }

    public boolean isHitFlashing() {
        return System.currentTimeMillis() < hitFlashUntilMs;
    }

    /** Shoves this entity directly away from a point, stopping at walls and arena edges. */
    public void knockBack(int fromX, int fromY, int distance,
                          java.util.List<java.awt.Rectangle> obstacles, int arenaWidth, int arenaHeight) {
        if (distance <= 0) {
            return;
        }
        double dx = getCenterX() - fromX;
        double dy = getCenterY() - fromY;
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length < 0.001) {
            return;
        }
        int targetX = x + (int) Math.round(dx / length * distance);
        int targetY = y + (int) Math.round(dy / length * distance);
        int boundsW = arenaWidth > w ? arenaWidth : x + w + distance + 1;
        int boundsH = arenaHeight > l ? arenaHeight : y + l + distance + 1;
        int[] resolved = world.arena.ArenaCollision.resolveMovement(
                x, y, targetX, targetY, w, l, obstacles, boundsW, boundsH);
        x = resolved[0];
        y = resolved[1];
    }

    public void checkDeath() {
        if (hp <= 0) {
            hp = 0; // Clamp health to 0
            dead = true;
        }
    }

    public void heal(double amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    public boolean isStunned() {
        return System.currentTimeMillis() < stunnedUntilMs;
    }

    public long getStunRemainingMs() {
        return Math.max(0, stunnedUntilMs - System.currentTimeMillis());
    }

    public void applyStun(long durationMs) {
        if (durationMs <= 0) {
            return;
        }
        long effective = adjustStunDuration(durationMs);
        if (effective <= 0) {
            return;
        }
        long target = System.currentTimeMillis() + effective;
        if (target > stunnedUntilMs) {
            stunnedUntilMs = target;
        }
    }

    /**
     * Hook for stun resistance, applied to every incoming stun.
     *
     * <p>The base entity has none. Subclasses shorten stuns here rather than at the call sites, so a
     * new stun source cannot accidentally bypass a resistance. See {@link Boss}.
     *
     * @param requestedMs stun the attacker asked for; always positive
     * @return stun to actually apply; 0 or less means immune
     */
    protected long adjustStunDuration(long requestedMs) {
        return requestedMs;
    }

    public void clearStun() {
        stunnedUntilMs = 0;
    }

    public boolean isSlowed() {
        return System.currentTimeMillis() < slowedUntilMs;
    }

    public void applySlow(long durationMs, double speedMultiplier) {
        if (durationMs <= 0 || speedMultiplier <= 0 || speedMultiplier >= 1.0) {
            return;
        }
        long target = System.currentTimeMillis() + durationMs;
        if (target > slowedUntilMs) {
            slowedUntilMs = target;
        }
        slowSpeedMultiplier = Math.min(slowSpeedMultiplier, speedMultiplier);
    }

    public double getEffectiveSpeed() {
        if (System.currentTimeMillis() >= slowedUntilMs) {
            slowSpeedMultiplier = 1.0;
            return speed;
        }
        return speed * slowSpeedMultiplier;
    }

    public boolean isBurning() {
        return System.currentTimeMillis() < fireTickUntilMs && fireTickDamagePerTick > 0;
    }

    public void applyFireTick(int damagePerTick, long tickIntervalMs, long durationMs) {
        if (damagePerTick <= 0 || tickIntervalMs <= 0 || durationMs <= 0) {
            return;
        }
        long target = System.currentTimeMillis() + durationMs;
        if (target > fireTickUntilMs) {
            fireTickUntilMs = target;
        }
        fireTickDamagePerTick = Math.max(fireTickDamagePerTick, damagePerTick);
        fireTickIntervalMs = tickIntervalMs;
    }

    /** Returns tick damage if a fire tick is due this frame. */
    public int consumeFireTickIfDue() {
        long now = System.currentTimeMillis();
        if (now >= fireTickUntilMs || fireTickDamagePerTick <= 0) {
            fireTickDamagePerTick = 0;
            return 0;
        }
        if (lastFireTickAppliedMs == 0 || now - lastFireTickAppliedMs >= fireTickIntervalMs) {
            lastFireTickAppliedMs = now;
            return fireTickDamagePerTick;
        }
        return 0;
    }

    public void gainXP(double amount) {
        currentXP += amount;
        if (currentXP >= maxXP) {
            levelUp();
        }
    }

    public void levelUp() {
        level++;
        currentXP = currentXP - maxXP;
        maxXP = maxXP * 1.5; // Increase XP requirement by 50% each level
    }

    // Weapon holding methods
    public void setWeapon(Item weapon) {
        this.heldWeapon = weapon;
        this.ranged = (weapon instanceof combat.Ranged);
    }

    public Item getHeldWeapon() {
        return heldWeapon;
    }

    public void selectWeapon(String weaponType, int weaponId) {
        Item weapon = switch(weaponType.toLowerCase()) {
            case "pistol" -> switch(weaponId) {
                case 1 -> new combat.ranged.pistols.Pistol1();
                default -> new combat.ranged.pistols.Pistol();
            };
            case "rifle" -> switch(weaponId) {
                case 1 -> new combat.ranged.rifles.Rifle1();
                default -> new combat.ranged.rifles.Rifle();
            };
            case "shotgun" -> switch(weaponId) {
                case 1 -> new combat.ranged.shotguns.Shotgun1();
                default -> new combat.ranged.shotguns.Shotgun();
            };
            case "sniper" -> switch(weaponId) {
                case 1 -> new combat.ranged.snipers.Sniper1();
                default -> new combat.ranged.snipers.Sniper();
            };
            case "smg" -> switch(weaponId) {
                case 1 -> new combat.ranged.smgs.SMG1();
                default -> new combat.ranged.smgs.SMG();
            };
            case "sword" -> switch(weaponId) {
                case 1 -> new combat.melee.swords.Sword1();
                default -> new combat.melee.swords.Sword();
            };
            case "hammer" -> switch(weaponId) {
                case 1 -> new combat.melee.hammers.Hammer1();
                default -> new combat.melee.hammers.Hammer();
            };
            case "dagger" -> switch(weaponId) {
                case 1 -> new combat.melee.daggers.Dagger1();
                default -> new combat.melee.daggers.Dagger();
            };
            case "scythe" -> switch(weaponId) {
                case 1 -> new combat.melee.scythes.Scythe1();
                default -> new combat.melee.scythes.Scythe();
            };
            case "mace" -> switch(weaponId) {
                case 1 -> new combat.melee.maces.Mace1();
                default -> new combat.melee.maces.Mace();
            };
            default -> null;
        };
        setWeapon(weapon);
    }

    public void draw(Graphics2D g, int cameraX, int cameraY) {
        drawBody(g, cameraX, cameraY);
        if (ranged) {
            drawBarrel(g, cameraX, cameraY);
        } else if (heldWeapon instanceof combat.Melee) {
            drawMeleeWeapon(g, cameraX, cameraY);
        }
        drawForeground(g, cameraX, cameraY);
        displayStats(g, cameraX, cameraY);
        drawStunIndicator(g, cameraX, cameraY);
    }

    /** Entity silhouette hook; players replace the default rectangle with layered sprites. */
    protected void drawBody(Graphics2D g, int cameraX, int cameraY) {
        g.setColor(isHitFlashing() ? Color.WHITE : color);
        g.fillRect(x - cameraX, y - cameraY, w, l);
    }

    /** Foreground silhouette hook for layers that must appear in front of held equipment. */
    protected void drawForeground(Graphics2D g, int cameraX, int cameraY) {
        // Most entities have no foreground body layer.
    }

    private void drawStunIndicator(Graphics2D g, int cameraX, int cameraY) {
        if (!isStunned()) {
            return;
        }
        int cx = getCenterX() - cameraX;
        int topY = y - cameraY - 22;
        g.setColor(new Color(180, 130, 255, 220));
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 11));
        java.awt.FontMetrics fm = g.getFontMetrics();
        String label = "STUN";
        g.drawString(label, cx - (fm.stringWidth(label) / 2), topY);
        g.setColor(new Color(180, 130, 255, 160));
        g.drawOval(cx - 14, topY + 2, 28, 8);
    }

    private void drawBarrel(Graphics2D g, int cameraX, int cameraY) {
        if (heldWeapon == null) {
            return; // no weapon, don't draw barrel
        }
        
        // Use weapon properties for barrel appearance
        double visualScale = getRangedWeaponVisualScale();
        int barrelLength = Math.max(1, (int) Math.round(heldWeapon.getBarrelLength() * visualScale));
        int barrelHeight = Math.max(1, (int) Math.round(heldWeapon.getBarrelHeight() * visualScale));
        int centerX = getWeaponDrawCenterX() - cameraX;
        int centerY = getWeaponDrawCenterY() - cameraY;

        // save original transform
        java.awt.geom.AffineTransform old = g.getTransform();

        // rotate around center
        g.rotate(barrelAngle, centerX, centerY);

        // draw barrel with weapon-specific color
        switch (heldWeapon.getBarrelColor().toLowerCase()) {
            case "red":
                g.setColor(Color.RED);
                break;
            case "blue":
                g.setColor(Color.BLUE);
                break;
            case "darkgreen":
                g.setColor(new Color(0, 100, 0));
                break;
            case "brown":
                g.setColor(new Color(139, 69, 19));
                break;
            case "black":
            default:
                g.setColor(Color.DARK_GRAY);
                break;
        }
        
        // Draw barrel: length extends right from center dot, height centered on dot
        g.fillRect(centerX, centerY - barrelHeight / 2, barrelLength, barrelHeight);

        // restore transform
        g.setTransform(old);
    }

    /** Visual-only held-weapon pivot; combat hitboxes and projectile origins remain unchanged. */
    protected int getWeaponDrawCenterX() {
        return getCenterX();
    }

    /** Visual-only held-weapon pivot; players can align it with their sprite's hands. */
    protected int getWeaponDrawCenterY() {
        return getCenterY();
    }

    /** Temporary primitive-weapon scale; future directional gun sprites replace this rectangle. */
    protected double getRangedWeaponVisualScale() {
        return 1.0;
    }

    private void drawMeleeWeapon(Graphics2D g, int cameraX, int cameraY) {
        if (heldWeapon == null) {
            return;
        }

        combat.Melee melee = (combat.Melee) heldWeapon;
        int centerX = getCenterX() - cameraX;
        int centerY = getCenterY() - cameraY;
        int weaponLength = 30;
        int weaponHeight = 8;
        int handleOffset = 10; // Distance from center to handle start

        // save original transform
        java.awt.geom.AffineTransform old = g.getTransform();

        // rotate around center
        g.rotate(barrelAngle, centerX, centerY);

        // Draw melee weapon (sword/hammer etc) - offset from center
        g.setColor(Color.GRAY);
        g.fillRect(centerX + handleOffset, centerY - weaponHeight / 2, weaponLength, weaponHeight);

        // Draw handle
        g.setColor(Color.DARK_GRAY);
        g.fillRect(centerX + handleOffset - 5, centerY - weaponHeight / 2 - 2, 5, weaponHeight + 4);

        // restore transform
        g.setTransform(old);

        // Draw range circle in debug mode
        if (debugMode) {
            double range = melee.getRange();
            g.setColor(new Color(255, 255, 0, 100)); // Semi-transparent yellow
            g.drawOval(centerX - (int)range, centerY - (int)range, (int)(range * 2), (int)(range * 2));
        }
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public void displayStats(Graphics2D g, int cameraX, int cameraY) {
        int barWidth = w;
        int barHeight = 5;
        int barX = x - cameraX - 1;
        int barY = y - cameraY - 10;

        // background (red)
        g.setColor(Color.RED);
        g.fillRect(barX, barY, barWidth, barHeight);

        // foreground (green) based on current hp
        g.setColor(Color.GREEN);
        int currentBarWidth = (int) ((hp / maxHp) * barWidth);
        g.fillRect(barX, barY, currentBarWidth, barHeight);

        // border (black)
        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);
    }
}
