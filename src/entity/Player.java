package entity;

import java.awt.Color;
import java.util.ArrayList;

import combat.Projectile;
import util.KeyHandler;
import util.MouseHandler;

public class Player extends Entity {

    ArrayList<Object> hotbar = new ArrayList<>();
    double xpMultiplier = 1.0; // multiplier for xp
    private ArrayList<Projectile> projectiles = new ArrayList<>();
    private long lastShotTime = 0;
    private long gameStartTime;
    private boolean canShoot = false;

    public Player(int x, int y) {
        super(x, y);
        gameStartTime = System.currentTimeMillis();
        lastShotTime = gameStartTime;
        playerSpawn();
    }

    // player spawn
    public void playerSpawn() {
        // health
        hp = 10;
        maxHp = 10;
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

        // Add starter weapons to hotbar (sequentially fills slots 1-5)
        hotbar.add(new combat.ranged.pistols.Pistol1(1));
        hotbar.add(new combat.ranged.rifles.Rifle1(1));
        hotbar.add(new combat.ranged.shotguns.Shotgun1(1));
        hotbar.add(new combat.melee.swords.Sword1(1));
        hotbar.add(new combat.melee.hammers.Hammer1(1));

        // Equip first hotbar slot by default
        equipHotbarSlot(0);
    }

    public void resetMouseClicks(MouseHandler mouse) {
        mouse.leftClicked = false;
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
        if (key.rPressed && heldWeapon != null) {
            heldWeapon.reload();
        }

        // Update weapon reload state
        if (heldWeapon != null) {
            heldWeapon.updateReload();
        }

        // shoot with fire rate delay
        long currentTime = System.currentTimeMillis();
        if (heldWeapon != null && canShoot && !heldWeapon.isReloading()) {
            long fireRateMs = (long) (heldWeapon.getFireRate() * 1000);
            boolean shouldShoot = false;

            if (heldWeapon.isAutomatic()) {
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
            if (!heldWeapon.isAutomatic() && mouse.leftClicked) {
                mouse.leftClicked = false;
            }
        }

        // Auto-reload when ammo is 0
        if (heldWeapon != null && heldWeapon.getCurrentAmmo() == 0 && !heldWeapon.isReloading()) {
            heldWeapon.reload();
        }

        // update projectiles
        updateProjectiles();

        // check death
        checkDeath();
        if (dead) {
            System.out.println("dead");
        }
    }

    public void aimBarrel(int mouseX, int mouseY) {
        int centerX = getCenterX();
        int centerY = getCenterY();
        barrelAngle = Math.atan2(mouseY - centerY, mouseX - centerX);
    }

    public void shoot() {
        if (heldWeapon != null) {
            Projectile bullet = heldWeapon.shoot(getCenterX(), getCenterY(), barrelAngle);
            if (bullet != null) {
                projectiles.add(bullet);
            }
        }
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
                    enemy.hp -= p.getDamage();
                    enemy.checkDeath();
                    if (enemy.isDead() && !killedEnemies.contains(enemy)) {
                        killedEnemies.add(enemy);
                    }
                    toRemove.add(p);
                    break; // Bullet hits one enemy then continues
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
                heldWeapon = null; // Melee not implemented for holding yet
                ranged = false;
            }
        }
    }
}
