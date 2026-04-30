package entity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import combat.Projectile;
import combat.Melee;
import util.KeyHandler;
import util.MouseHandler;

public class Player extends Entity {

    ArrayList<Object> hotbar = new ArrayList<>();
    double xpMultiplier = 1.0; // multiplier for xp
    private ArrayList<Projectile> projectiles = new ArrayList<>();
    private long lastShotTime = 0;
    private long gameStartTime;
    private boolean canShoot = false;
    private PlayerStats stats = new PlayerStats();

    public Player(int x, int y) {
        super(x, y);
        gameStartTime = System.currentTimeMillis();
        lastShotTime = gameStartTime;
        playerSpawn();
    }

    // player spawn
    public void playerSpawn() {
        // health
        hp = 50;
        maxHp = 50;
        dead = false;
        // multipliers
        damage = 1.0;
        speed = 7.0;
        // combat
        ranged = true;
        // visuals
        color = Color.BLUE;
        // weapon
        heldWeapon = null; // starts with no weapon
        //selectWeapon("rifle", 1);

        // Reset stats on spawn (fresh run)
        if (stats != null) {
            stats.reset();
        }

        // Initialize hotbar with 5 fixed slots
        hotbar.clear();
        for (int i = 0; i < 5; i++) {
            hotbar.add(null);
        }
        // Set starting items in slots 0, 1, and 2
        hotbar.set(0, new combat.ranged.pistols.Pistol1(1));
        hotbar.set(1, new combat.melee.swords.Sword1(1));
        hotbar.set(2, new combat.consumables.Consumable1(1));

        // Equip first hotbar slot by default
        equipHotbarSlot(0);
    }

    public void resetMouseClicks(MouseHandler mouse) {
        mouse.leftClicked = false;
        mouse.rightClicked = false;
    }

    public void update(KeyHandler key, MouseHandler mouse, int arenaWidth, int arenaHeight) {
        if (key.upPressed)    y -= (int) speed;
        if (key.downPressed)  y += (int) speed;
        if (key.leftPressed)  x -= (int) speed;
        if (key.rightPressed) x += (int) speed;

        // clamp player to arena bounds
        x = Math.max(0, Math.min(x, arenaWidth - w));
        y = Math.max(0, Math.min(y, arenaHeight - l));

        // Clear stale mouse clicks on first update
        if (!canShoot) {
            mouse.leftClicked = false;
            canShoot = true;
        }

        // Handle reload
        if (key.rPressed && heldWeapon instanceof combat.Ranged) {
            ((combat.Ranged) heldWeapon).reload();
        }

        // Update weapon reload state
        if (heldWeapon instanceof combat.Ranged) {
            ((combat.Ranged) heldWeapon).updateReload();
        }

        // Handle ranged or melee attacks
        long currentTime = System.currentTimeMillis();
        if (heldWeapon != null && canShoot) {
            if (heldWeapon instanceof Melee) {
                // Melee swing attack
                handleMeleeAttack(mouse, (Melee) heldWeapon);
            } else if (heldWeapon instanceof combat.Ranged) {
                // Ranged shooting
                handleRangedAttack(mouse, currentTime);
            }
        }

        // Update active melee swing
        if (heldWeapon instanceof Melee) {
            Melee melee = (Melee) heldWeapon;
            melee.updateSwing();
        }

        // Auto-reload when ammo is 0 (ranged only)
        if (heldWeapon instanceof combat.Ranged) {
            combat.Ranged ranged = (combat.Ranged) heldWeapon;
            if (ranged.getCurrentAmmo() == 0 && !ranged.isReloading()) {
                ranged.reload();
            }
        }

        // update projectiles
        updateProjectiles();

        // check death
        checkDeath();
        if (dead) {
            System.out.println("dead");
        }
    }

    private void handleMeleeAttack(MouseHandler mouse, Melee melee) {
        boolean shouldAttack = false;

        if (melee.isAutomatic()) {
            // Automatic: hold to swing
            shouldAttack = mouse.leftPressed && melee.canAttack();
        } else {
            // Manual: click to swing
            shouldAttack = mouse.leftClicked && melee.canAttack();
        }

        if (shouldAttack) {
            // Start swing toward mouse direction
            int centerX = getCenterX();
            int centerY = getCenterY();
            double angleToMouse = Math.atan2(mouse.mouseY + cameraY - centerY, mouse.mouseX + cameraX - centerX);
            melee.startSwing(angleToMouse);
        }

        // Reset click for non-automatic melee
        if (!melee.isAutomatic() && mouse.leftClicked) {
            mouse.leftClicked = false;
        }
    }

    private int cameraX, cameraY; // Camera offset for mouse calculations

    public void setCameraOffset(int x, int y) {
        this.cameraX = x;
        this.cameraY = y;
    }

    private void handleRangedAttack(MouseHandler mouse, long currentTime) {
        if (!(heldWeapon instanceof combat.Ranged)) return;
        combat.Ranged ranged = (combat.Ranged) heldWeapon;
        long fireRateMs = (long) (ranged.getFireRate() * 1000);
        boolean shouldShoot = false;

        if (ranged.isAutomatic()) {
            // Automatic: hold to shoot
            shouldShoot = mouse.leftPressed && (currentTime - lastShotTime >= fireRateMs);
        } else {
            // Semi-automatic: click to shoot
            shouldShoot = mouse.leftClicked && (currentTime - lastShotTime >= fireRateMs);
        }

        if (shouldShoot) {
            shoot();
            lastShotTime = currentTime;
        }

        // Reset click for semi-auto weapons
        if (!ranged.isAutomatic() && mouse.leftClicked) {
            mouse.leftClicked = false;
        }
    }

    public void aimBarrel(int mouseX, int mouseY) {
        int centerX = getCenterX();
        int centerY = getCenterY();
        barrelAngle = Math.atan2(mouseY - centerY, mouseX - centerX);
    }

    public void shoot() {
        if (heldWeapon instanceof combat.Ranged) {
            combat.Ranged ranged = (combat.Ranged) heldWeapon;
            List<Projectile> bullets = ranged.shoot(getCenterX(), getCenterY(), barrelAngle);
            if (bullets != null) {
                projectiles.addAll(bullets);
                stats.addShotFired();
            }
        }
    }

    public PlayerStats getStats() {
        return stats;
    }

    public void updateProjectiles() {
        for (Projectile p : projectiles) {
            p.update();
        }
        // Remove projectiles that go off screen (simple cleanup)
        // Increased bounds for dungeon arena (larger world)
        projectiles.removeIf(p -> p.getX() < 0 || p.getX() > 10000 || p.getY() < 0 || p.getY() > 10000);
    }

    public void checkProjectileCollisions(entity.EnemyManager enemyManager) {
        ArrayList<Projectile> toRemove = new ArrayList<>();
        ArrayList<entity.Enemy> killedEnemies = new ArrayList<>();
        for (Projectile p : projectiles) {
            for (entity.Enemy enemy : enemyManager.getEnemies()) {
                if (enemy.checkCollision(p.getX(), p.getY())) {
                    enemy.takeDamage(p.getDamage());
                    if (enemy.isDead() && !killedEnemies.contains(enemy)) {
                        killedEnemies.add(enemy);
                        stats.addKill();
                    }
                    stats.addShotHit();
                    stats.addDamageDealt(p.getDamage());
                    toRemove.add(p);
                    break; // Bullet hits one enemy then continues
                }
            }
            // Also check collision with active boss
            if (!toRemove.contains(p) && enemyManager.getActiveBoss() != null) {
                entity.Boss boss = enemyManager.getActiveBoss();
                if (boss.checkCollision(p.getX(), p.getY())) {
                    boss.takeDamage(p.getDamage());
                    stats.addShotHit();
                    stats.addDamageDealt(p.getDamage());
                    toRemove.add(p);
                }
            }
        }
        projectiles.removeAll(toRemove);
        // Gain XP from killed enemies
        for (entity.Enemy enemy : killedEnemies) {
            gainXP(enemy.xpValue);
        }
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public ArrayList<Object> getHotbar() {
        return hotbar;
    }

    public void equipHotbarSlot(int slot) {
        if (slot >= 0 && slot < hotbar.size()) {
            Object item = hotbar.get(slot);
            if (item == null) {
                heldWeapon = null;
                ranged = false;
            } else if (item instanceof combat.Ranged) {
                heldWeapon = (combat.Ranged) item;
                ranged = true;
            } else if (item instanceof combat.Melee) {
                heldWeapon = (combat.Melee) item;
                ranged = false;
            } else if (item instanceof combat.consumables.Consumable) {
                heldWeapon = null;
                ranged = false;
            }
        }
    }
}
