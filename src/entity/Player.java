package entity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import combat.Projectile;
import combat.Melee;
import combat.Item;
import combat.consumables.Consumable;
import combat.ranged.pistols.Pistol1;
import combat.ranged.rifles.Rifle1;
import util.KeyHandler;
import util.MouseHandler;
import currency.CurrencyManager;

public class Player extends Entity {

    // Active consumable effect tracking
    public static class ActiveConsumableEffect {
        public Consumable.ConsumableEffectType type;
        public double multiplier;
        public long endTimeMs;
        public String name;

        public ActiveConsumableEffect(Consumable.ConsumableEffectType type, double multiplier, long durationMs, String name) {
            this.type = type;
            this.multiplier = multiplier;
            this.endTimeMs = System.currentTimeMillis() + durationMs;
            this.name = name;
        }

        public long getRemainingMs() {
            return Math.max(0, endTimeMs - System.currentTimeMillis());
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= endTimeMs;
        }
    }

    ArrayList<Object> hotbar = new ArrayList<>();
    double xpMultiplier = 1.0; // multiplier for xp
    private ArrayList<Projectile> projectiles = new ArrayList<>();
    private long lastShotTime = 0;
    private long gameStartTime;
    private PlayerStats stats = new PlayerStats();
    private combat.Inventory inventory = new combat.Inventory();
    private double baseHp; // Base HP before charm effects
    private double baseMaxHp; // Base max HP before charm effects
    private double baseSpeed; // Base speed before charm effects
    private Map<Consumable.ConsumableEffectType, ActiveConsumableEffect> activeEffects = new HashMap<>();
    private CurrencyManager currencyManager = new CurrencyManager();

    public Player(int x, int y) {
        super(x, y);
        gameStartTime = System.currentTimeMillis();
        lastShotTime = gameStartTime;
        playerSpawn();
    }

    // player spawn
    public void playerSpawn() {
        // health
        hp = 200;
        maxHp = 200;
        baseHp = 200;
        baseMaxHp = 200;
        dead = false;
        // multipliers
        damage = 1.0;
        speed = 7.0;
        baseSpeed = 7.0;
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
        // Set starting item in slot 0 only
        hotbar.set(0, new combat.ranged.pistols.Pistol1(1));

        // Equip first hotbar slot by default
        equipHotbarSlot(0);

        // Set player reference in inventory for charm effects
        inventory.setPlayer(this);

    }

    // Apply starting loadout from LoadoutScreen
    public void applyLoadout(String weaponName, int weaponTier, String charmName, int charmTier, String powerName, int powerTier, String summonName, int summonTier, String consumableName, int consumableTier) {
        // Apply starting weapon
        if (!weaponName.equals("None") && !weaponName.isEmpty()) {
            Item startingWeapon = createWeaponFromLoadout(weaponName, weaponTier);
            if (startingWeapon != null) {
                hotbar.set(0, startingWeapon);
                equipHotbarSlot(0);
                System.out.println("Spawned with weapon: " + weaponName + " (T" + weaponTier + ")");
            }
        }

        // Apply starting charm (if unlocked and slot available)
        if (!charmName.equals("None") && !charmName.isEmpty()) {
            combat.charms.Charm startingCharm = createCharmFromLoadout(charmName);
            if (startingCharm != null) {
                startingCharm.setTier(charmTier);
                inventory.equipCharm(0, startingCharm); // Equip in first charm slot
                System.out.println("Spawned with charm: " + charmName + " (T" + charmTier + ")");
            }
        }

        // Apply starting power
        if (!powerName.equals("None") && !powerName.isEmpty()) {
            combat.powers.Power startingPower = createPowerFromLoadout(powerName);
            if (startingPower != null) {
                startingPower.setTier(powerTier);
                inventory.equipPower(startingPower);
                System.out.println("Spawned with power: " + powerName + " (T" + powerTier + ")");
            }
        }

        // Apply starting summon
        if (!summonName.equals("None") && !summonName.isEmpty()) {
            combat.summons.Summon startingSummon = createSummonFromLoadout(summonName);
            if (startingSummon != null) {
                startingSummon.setTier(summonTier);
                inventory.equipSummon(startingSummon);
                System.out.println("Spawned with summon: " + summonName + " (T" + summonTier + ")");
            }
        }

        // Apply starting consumable (add to hotbar if slot available)
        if (!consumableName.equals("None") && !consumableName.isEmpty()) {
            combat.consumables.Consumable startingConsumable = createConsumableFromLoadout(consumableName);
            if (startingConsumable != null) {
                startingConsumable.setTier(consumableTier);
                // Add to hotbar slot 4 (reserved for consumables)
                if (hotbar.size() > 4) {
                    hotbar.set(4, startingConsumable);
                }
                System.out.println("Spawned with consumable: " + consumableName + " (T" + consumableTier + ")");
            }
        }
    }

    private Item createWeaponFromLoadout(String weaponName, int tier) {
        // First try the direct switch cases for known aliases
        Item weapon = switch (weaponName) {
            case "Glock", "Pistol1" -> new combat.ranged.pistols.Pistol1(tier);
            case "AK-47", "Rifle1" -> new combat.ranged.rifles.Rifle1(tier);
            case "Shotgun1" -> new combat.ranged.shotguns.Shotgun1(tier);
            case "SMG1" -> new combat.ranged.smgs.SMG1(tier);
            case "Sniper1" -> new combat.ranged.snipers.Sniper1(tier);
            case "Sword1" -> new combat.melee.swords.Sword1(tier);
            case "Hammer1" -> new combat.melee.hammers.Hammer1(tier);
            case "Dagger1" -> new combat.melee.daggers.Dagger1(tier);
            case "Mace1" -> new combat.melee.maces.Mace1(tier);
            case "Scythe1" -> new combat.melee.scythes.Scythe1(tier);
            default -> null;
        };
        
        // If not found in switch, try creating dynamically using reflection
        if (weapon == null) {
            try {
                Class<?> itemClass = findWeaponClass(weaponName);
                if (itemClass != null) {
                    // Try constructor with tier parameter
                    try {
                        return (Item) itemClass.getConstructor(int.class).newInstance(tier);
                    } catch (NoSuchMethodException e) {
                        // Try no-arg constructor
                        return (Item) itemClass.getConstructor().newInstance();
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to create weapon: " + weaponName + " (tier " + tier + ")");
            }
        }
        return weapon;
    }
    
    private Class<?> findWeaponClass(String weaponName) {
        String[] packages = {
            "combat.ranged.pistols.",
            "combat.ranged.rifles.",
            "combat.ranged.shotguns.",
            "combat.ranged.smgs.",
            "combat.ranged.snipers.",
            "combat.melee.swords.",
            "combat.melee.hammers.",
            "combat.melee.daggers.",
            "combat.melee.maces.",
            "combat.melee.scythes."
        };
        
        for (String pkg : packages) {
            try {
                return Class.forName(pkg + weaponName);
            } catch (ClassNotFoundException e) {
                // Continue searching
            }
        }
        return null;
    }

    private combat.charms.Charm createCharmFromLoadout(String charmName) {
        return switch (charmName) {
            case "Charm1", "SimpleCharm" -> new combat.charms.Charm1();
            case "SpeedCharm" -> new combat.charms.SpeedCharm();
            default -> {
                // Try dynamic creation
                try {
                    Class<?> charmClass = Class.forName("combat.charms." + charmName);
                    yield (combat.charms.Charm) charmClass.getConstructor().newInstance();
                } catch (Exception e) {
                    System.err.println("Failed to create charm: " + charmName);
                    yield null;
                }
            }
        };
    }

    private combat.powers.Power createPowerFromLoadout(String powerName) {
        return switch (powerName) {
            case "Fire" -> new combat.powers.Fire();
            case "Earth" -> new combat.powers.Earth();
            case "Light" -> new combat.powers.Light();
            case "Lightning" -> new combat.powers.Lightning();
            case "Water" -> new combat.powers.Water();
            case "Magma" -> new combat.powers.Magma();
            case "FireV2" -> new combat.powers.FireV2();
            case "EarthV2" -> new combat.powers.EarthV2();
            case "LightningV2" -> new combat.powers.LightningV2();
            case "MagmaV2" -> new combat.powers.MagmaV2();
            case "WaterV2" -> new combat.powers.WaterV2();
            case "Infinity" -> new combat.powers.Infinity();
            case "KingOfCurses" -> new combat.powers.KingOfCurses();
            case "RinneSharingan" -> new combat.powers.RinneSharingan();
            default -> {
                // Try dynamic creation
                try {
                    Class<?> powerClass = Class.forName("combat.powers." + powerName);
                    yield (combat.powers.Power) powerClass.getConstructor().newInstance();
                } catch (Exception e) {
                    System.err.println("Failed to create power: " + powerName);
                    yield null;
                }
            }
        };
    }

    private combat.summons.Summon createSummonFromLoadout(String summonName) {
        return switch (summonName) {
            case "Summon1" -> new combat.summons.Summon1();
            case "Summon2" -> new combat.summons.Summon2();
            case "Summon3" -> new combat.summons.Summon3();
            case "Summon4" -> new combat.summons.Summon4();
            case "Summon5" -> new combat.summons.Summon5();
            default -> {
                // Try dynamic creation
                try {
                    Class<?> summonClass = Class.forName("combat.summons." + summonName);
                    yield (combat.summons.Summon) summonClass.getConstructor().newInstance();
                } catch (Exception e) {
                    System.err.println("Failed to create summon: " + summonName);
                    yield null;
                }
            }
        };
    }

    private combat.consumables.Consumable createConsumableFromLoadout(String consumableName) {
        return switch (consumableName) {
            case "Consumable1" -> new combat.consumables.Consumable1();
            case "Consumable2" -> new combat.consumables.Consumable2();
            case "Consumable3" -> new combat.consumables.Consumable3();
            case "DamagePotion" -> new combat.consumables.DamagePotion();
            case "DamagePotion2" -> new combat.consumables.DamagePotion2();
            case "DamagePotion3" -> new combat.consumables.DamagePotion3();
            default -> {
                // Try dynamic creation
                try {
                    Class<?> consumableClass = Class.forName("combat.consumables." + consumableName);
                    yield (combat.consumables.Consumable) consumableClass.getConstructor().newInstance();
                } catch (Exception e) {
                    System.err.println("Failed to create consumable: " + consumableName);
                    yield null;
                }
            }
        };
    }

    // Recalculate HP and Speed based on equipped charms (additive stacking)
    public void recalculateCharmEffects() {
        int charmCount = 0;
        int speedCharmCount = 0;
        for (int i = 0; i < inventory.getMaxCharms(); i++) {
            combat.charms.Charm charm = inventory.getCharm(i);
            if (charm != null) {
                charmCount++;
                if (charm instanceof combat.charms.SpeedCharm) {
                    speedCharmCount++;
                }
            }
        }
        // Each charm adds 10% of base HP
        double totalHpBoost = charmCount * 0.1;
        setHp((int)(baseHp + (baseHp * totalHpBoost)));
        setMaxHp((int)(baseMaxHp + (baseMaxHp * totalHpBoost)));

        // Each SpeedCharm adds 10% of base speed
        double totalSpeedBoost = speedCharmCount * 0.10;
        speed = baseSpeed + (baseSpeed * totalSpeedBoost);
    }

    public void resetMouseClicks(MouseHandler mouse) {
        mouse.leftClicked = false;
        mouse.rightClicked = false;
        mouse.leftPressed = false;
        mouse.rightPressed = false;
    }

    public void update(KeyHandler key, MouseHandler mouse, int arenaWidth, int arenaHeight) {
        if (key != null) {
            if (key.upPressed)    y -= (int) speed;
            if (key.downPressed)  y += (int) speed;
            if (key.leftPressed)  x -= (int) speed;
            if (key.rightPressed) x += (int) speed;
        }

        // clamp player to arena bounds
        x = Math.max(0, Math.min(x, arenaWidth - w));
        y = Math.max(0, Math.min(y, arenaHeight - l));

        // Handle reload
        if (key != null && key.rPressed && heldWeapon instanceof combat.Ranged) {
            ((combat.Ranged) heldWeapon).reload();
        }

        // Update weapon reload state
        if (heldWeapon instanceof combat.Ranged) {
            ((combat.Ranged) heldWeapon).updateReload();
        }

        // Handle ranged or melee attacks
        long currentTime = System.currentTimeMillis();
        if (heldWeapon != null) {
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
            double damageMultiplier = getTotalDamageMultiplier();
            List<Projectile> bullets = ranged.shoot(getCenterX(), getCenterY(), barrelAngle, damageMultiplier);
            if (bullets != null) {
                projectiles.addAll(bullets);
                stats.addShotFired();
            }
        }
    }

    public PlayerStats getStats() {
        return stats;
    }

    public double getBaseHp() { return baseHp; }
    public double getBaseMaxHp() { return baseMaxHp; }

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
        // Gain XP and Gold from killed enemies
        for (entity.Enemy enemy : killedEnemies) {
            int xp = enemy.getXpValue();
            gainXP(xp);
            currencyManager.addGold(enemy.getGoldValue());
            // Save XP to persistent storage
            save.SaveManager.addXP(xp);
        }
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public ArrayList<Object> getHotbar() {
        return hotbar;
    }

    public combat.Inventory getInventory() {
        return inventory;
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

    // Active consumable effects management
    public void addConsumableEffect(Consumable consumable) {
        if (consumable.getEffectType() == Consumable.ConsumableEffectType.NONE || consumable.getDurationMs() <= 0) {
            return;
        }
        ActiveConsumableEffect effect = new ActiveConsumableEffect(
            consumable.getEffectType(),
            consumable.getEffectMultiplier(),
            consumable.getDurationMs(),
            consumable.getName()
        );
        activeEffects.put(consumable.getEffectType(), effect);
    }

    public Map<Consumable.ConsumableEffectType, ActiveConsumableEffect> getActiveEffects() {
        return activeEffects;
    }

    public void expireConsumableEffects() {
        activeEffects.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public double getTotalDamageMultiplier() {
        expireConsumableEffects();
        double totalMultiplier = damage; // base player damage multiplier
        for (ActiveConsumableEffect effect : activeEffects.values()) {
            if (effect.type == Consumable.ConsumableEffectType.DAMAGE_BOOST) {
                totalMultiplier *= effect.multiplier;
            }
        }
        return totalMultiplier;
    }

    public int getEffectiveMeleeDamage() {
        if (heldWeapon instanceof combat.Melee) {
            combat.Melee melee = (combat.Melee) heldWeapon;
            double multiplier = getTotalDamageMultiplier();
            return (int) (melee.getDamage() * multiplier);
        }
        return 0;
    }

    /**
     * Handles shooting mechanics with conditional disabling and optional pre-update logic.
     * @param mouse The mouse handler
     * @param key The key handler for movement
     * @param shouldDisableShooting Whether shooting should be disabled
     * @param preUpdateLogic Optional logic to run before update() (can be null)
     * @param arenaWidth Arena width
     * @param arenaHeight Arena height
     * @return The original leftPressed state (for restoration)
     */
    public boolean handleShootingMechanics(MouseHandler mouse, KeyHandler key, boolean shouldDisableShooting, Runnable preUpdateLogic, int arenaWidth, int arenaHeight) {
        // Save original mouse state
        boolean wasLeftPressed = mouse.leftPressed;
        boolean wasLeftClicked = mouse.leftClicked;

        // Disable shooting if conditions met
        if (shouldDisableShooting) {
            mouse.leftClicked = false;
            mouse.leftPressed = false;
        }

        // Execute any pre-update logic (like consumable handling)
        if (preUpdateLogic != null) {
            preUpdateLogic.run();
        }

        // Update player (handles movement, shooting, projectiles, etc.)
        update(key, mouse, arenaWidth, arenaHeight);

        // Restore leftPressed so drag doesn't break (don't restore leftClicked - it's one-shot)
        mouse.leftPressed = wasLeftPressed;

        return wasLeftPressed;
    }

    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }
}
