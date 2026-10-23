package entity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import combat.CombatTuning;
import combat.DamageRoll;
import combat.Hitstop;
import combat.Projectile;
import combat.Melee;
import combat.Item;
import combat.armor.WornArmor;
import combat.consumables.Consumable;
import combat.defense.DamageOutcome;
import combat.defense.DodgeState;
import combat.defense.InvulnerabilityWindow;
import progression.PerkService;
import util.CombatFeedback;
import util.KeyHandler;
import util.MouseHandler;
import currency.CurrencyManager;
import java.awt.Rectangle;
import mastery.MasteryService;
import player.CharacterAppearance;
import player.CharacterRenderer;
import player.CharacterRenderer.Direction;
import skilltree.SkillEffects;

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
    private double baseDamage; // Base damage multiplier before charm effects
    private Map<Consumable.ConsumableEffectType, ActiveConsumableEffect> activeEffects = new HashMap<>();
    private CurrencyManager currencyManager = new CurrencyManager();
    private util.CombatFeedback feedback;
    // Latest arena context, kept so knockback can respect walls without threading it through combat calls.
    private List<Rectangle> arenaObstacles;
    private int arenaWidth;
    private int arenaHeight;

    // Persistent player stats (loaded from save)
    private int playerLevel = 1;
    private int playerXP = 0;

    private static final int CHARACTER_RENDER_SIZE = 128;
    private static final long WALK_FRAME_MS = 170;
    private final CharacterRenderer characterRenderer = new CharacterRenderer();
    private boolean characterMoving;
    private int characterWalkFrame;
    private long characterWalkFrameChangedMs;

    private final CombatTuning tuning = CombatTuning.active();
    private final DodgeState dodge = new DodgeState(tuning);
    /**
     * Invulnerability granted after a hit lands. Without it, overlapping enemies each deal full
     * damage on the same frame and a crowd deletes the player faster than any of them can be read.
     */
    private final InvulnerabilityWindow hurtInvulnerability =
            new InvulnerabilityWindow(tuning.getHurtInvulnerabilityMs());
    private boolean dodgeKeyHeldLastFrame = false;
    private Hitstop hitstop;
    private double wornArmorMitigation = 0.0;
    /** Incoming damage cannot be reduced below this fraction of its original amount. */
    private static final double MIN_INCOMING_DAMAGE_FRACTION = 0.25;

    public Player(int x, int y) {
        super(x, y);
        gameStartTime = System.currentTimeMillis();
        lastShotTime = gameStartTime;
        playerSpawn();
    }

    // player spawn
    public void playerSpawn() {
        // Load persistent stats from save data
        loadPersistentStats();
        refreshCharacterAppearance();

        // Base combat stats (level does not modify these; charms/consumables can)
        baseHp = 200;
        baseMaxHp = 200;
        hp = baseHp;
        maxHp = (int) baseMaxHp;
        baseSpeed = 3.5;
        speed = baseSpeed;
        baseDamage = 1.0;
        damage = baseDamage;
        dead = false;
        characterMoving = false;
        characterWalkFrame = 0;
        characterWalkFrameChangedMs = System.currentTimeMillis();

        // A fresh run must not inherit a spent dodge cooldown or leftover invulnerability.
        dodge.reset();
        hurtInvulnerability.reset();
        dodgeKeyHeldLastFrame = false;
        clearStun();
        refreshDefensiveBonuses();

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

        // Fold in skill tree bonuses (charms are applied again as they get equipped)
        recalculateCharmEffects();

        System.out.println("Player spawned - Level: " + playerLevel + ", HP: " + (int)maxHp + ", Speed: " + String.format("%.1f", speed));
    }

    /** Reloads the saved body and palette without rebuilding the player or changing combat state. */
    public void refreshCharacterAppearance() {
        save.SaveData saveData = save.SaveManager.load();
        CharacterAppearance appearance = saveData != null ? saveData.getAppearance() : null;
        if (appearance == null) {
            appearance = new CharacterAppearance();
        }
        characterRenderer.loadAssets(
                appearance,
                CharacterRenderer.getDefaultSkinPalettes(),
                saveData != null ? saveData.getEquippedClothingStyles() : java.util.Map.of());
    }

    // Load persistent stats from save data
    private void loadPersistentStats() {
        save.SaveData saveData = save.SaveManager.load();
        if (saveData != null) {
            playerLevel = saveData.getPlayerLevel();
            playerXP = saveData.getPlayerXP();
            System.out.println("Loaded player stats - Level: " + playerLevel + ", XP: " + playerXP);
        } else {
            playerLevel = 1;
            playerXP = 0;
        }
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
            } else {
                System.err.println("WARNING: Failed to create weapon '" + weaponName + "' (T" + weaponTier + "). Using default Pistol1.");
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
        // Normalize weapon name first to handle display names
        String normalizedName = normalizeWeaponName(weaponName);
        
        // Try the direct switch cases for known weapons
        Item weapon = switch (normalizedName) {
            case "Pistol1" -> new combat.ranged.pistols.Pistol1(tier);
            case "Rifle1" -> new combat.ranged.rifles.Rifle1(tier);
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
                Class<?> itemClass = findWeaponClass(normalizedName);
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
                System.err.println("Failed to create weapon: " + weaponName + " (normalized: " + normalizedName + ", tier " + tier + ") - " + e.getMessage());
            }
        }
        return weapon;
    }
    
    private String normalizeWeaponName(String weaponName) {
        // Convert display names to class names for consistency
        return switch (weaponName) {
            case "Glock", "Pistol1", "Pistol" -> "Pistol1";
            case "AK-47", "Rifle1", "Rifle" -> "Rifle1";
            case "Shotgun1", "Shotgun", "Pump - Action", "Pump-Action" -> "Shotgun1";
            case "SMG1", "SMG", "P90" -> "SMG1";
            case "Sniper1", "Sniper" -> "Sniper1";
            case "Sword1", "Sword" -> "Sword1";
            case "Hammer1", "Hammer" -> "Hammer1";
            case "Dagger1", "Dagger" -> "Dagger1";
            case "Mace1", "Mace" -> "Mace1";
            case "Scythe1", "Scythe" -> "Scythe1";
            default -> weaponName; // Already normalized or unknown
        };
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
            case "DamageCharm1" -> new combat.charms.DamageCharm1();
            case "DamageCharm2" -> new combat.charms.DamageCharm2();
            case "DamageCharm3" -> new combat.charms.DamageCharm3();
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
            case "Overgrowth" -> new combat.powers.Overgrowth();
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
                    try {
                        Class<?> powerClass = Class.forName("combat.powers.fruits." + powerName);
                        yield (combat.powers.Power) powerClass.getConstructor().newInstance();
                    } catch (Exception ex) {
                        System.err.println("Failed to create power: " + powerName);
                        yield null;
                    }
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

    // Recalculate HP, speed, and damage from each equipped charm plus skill tree (additive stacking)
    public void recalculateCharmEffects() {
        double totalHpBoost = 0;
        double totalSpeedBoost = 0;
        double totalDamageBoost = 0;
        for (int i = 0; i < inventory.getMaxCharms(); i++) {
            combat.charms.Charm charm = inventory.getCharm(i);
            if (charm != null) {
                totalHpBoost += charm.getMaxHpBonusFraction();
                totalSpeedBoost += charm.getSpeedBonusFraction();
                totalDamageBoost += charm.getDamageBonusFraction();
            }
        }
        totalHpBoost += SkillEffects.maxHpBonusFraction();
        totalSpeedBoost += SkillEffects.moveSpeedBonusFraction();
        totalDamageBoost += SkillEffects.damageBonusFraction();

        double hpFraction = maxHp > 0 ? (hp / maxHp) : 1.0;
        setMaxHp(baseMaxHp + (baseMaxHp * totalHpBoost));
        setHp(Math.min(maxHp, maxHp * hpFraction));
        speed = baseSpeed + (baseSpeed * totalSpeedBoost);
        damage = baseDamage + (baseDamage * totalDamageBoost);
    }

    @Override
    public void heal(double amount) {
        super.heal(amount * SkillEffects.healingMultiplier());
    }

    public void resetMouseClicks(MouseHandler mouse) {
        mouse.leftClicked = false;
        mouse.rightClicked = false;
        mouse.leftPressed = false;
        mouse.rightPressed = false;
    }

    public void update(KeyHandler key, MouseHandler mouse, int arenaWidth, int arenaHeight) {
        update(key, mouse, arenaWidth, arenaHeight, null);
    }

    /**
     * @param arenaObstacles solid rectangles in world space; may be null or empty
     */
    public void update(KeyHandler key, MouseHandler mouse, int arenaWidth, int arenaHeight, List<Rectangle> arenaObstacles) {
        this.arenaObstacles = arenaObstacles;
        this.arenaWidth = arenaWidth;
        this.arenaHeight = arenaHeight;
        long now = System.currentTimeMillis();

        handleDodgeInput(key, now);

        int oldX = x;
        int oldY = y;
        int newX = oldX;
        int newY = oldY;

        // A dodge in flight owns movement: the direction is committed at the start of the dash, so
        // steering out of it would erase the commitment the dodge is supposed to represent.
        int[] dodgeStep = dodge.sampleDisplacement(now);
        newX += dodgeStep[0];
        newY += dodgeStep[1];

        if (!dodge.isDashing(now) && key != null) {
            int moveStep = (int) Math.round(getTotalSpeed());
            if (key.upPressed)    newY -= moveStep;
            if (key.downPressed)  newY += moveStep;
            if (key.leftPressed)  newX -= moveStep;
            if (key.rightPressed) newX += moveStep;
        }

        // Dodge movement is collision-resolved like any other movement, so it cannot cross a wall.
        int[] resolved = world.arena.ArenaCollision.resolveMovement(
                oldX, oldY, newX, newY, w, l, arenaObstacles, arenaWidth, arenaHeight);
        x = resolved[0];
        y = resolved[1];
        updateCharacterAnimation(oldX, oldY, now);

        // Handle reload
        if (key != null && key.rPressed && heldWeapon instanceof combat.Ranged) {
            ((combat.Ranged) heldWeapon).reload();
        }

        // Update weapon reload state
        if (heldWeapon instanceof combat.Ranged) {
            ((combat.Ranged) heldWeapon).updateReload(SkillEffects.reloadTimeMultiplier());
        }

        // Handle ranged or melee attacks
        long currentTime = now;
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
        updateProjectiles(arenaObstacles, arenaWidth, arenaHeight);

        // check death
        checkDeath();
    }

    public void setCombatFeedback(util.CombatFeedback feedback) {
        this.feedback = feedback;
    }

    /**
     * Starts a dodge on the rising edge of the dodge key.
     *
     * <p>Direction comes from the movement keys, falling back to the aim direction when the player is
     * standing still, so a dodge is never wasted on a stationary player.
     */
    private void handleDodgeInput(KeyHandler key, long nowMs) {
        if (key == null) {
            dodgeKeyHeldLastFrame = false;
            return;
        }
        boolean pressedThisFrame = key.shiftPressed && !dodgeKeyHeldLastFrame;
        dodgeKeyHeldLastFrame = key.shiftPressed;
        if (!pressedThisFrame) {
            return;
        }

        double dirX = 0;
        double dirY = 0;
        if (key.upPressed)    dirY -= 1;
        if (key.downPressed)  dirY += 1;
        if (key.leftPressed)  dirX -= 1;
        if (key.rightPressed) dirX += 1;
        if (dirX == 0 && dirY == 0) {
            dirX = Math.cos(barrelAngle);
            dirY = Math.sin(barrelAngle);
        }
        dodge.tryStart(nowMs, dirX, dirY);
    }

    private void updateCharacterAnimation(int oldX, int oldY, long nowMs) {
        boolean movedThisFrame = x != oldX || y != oldY;
        if (!movedThisFrame) {
            characterWalkFrame = 0;
            characterWalkFrameChangedMs = nowMs;
        } else if (!characterMoving) {
            characterWalkFrame = 0;
            characterWalkFrameChangedMs = nowMs;
        } else if (nowMs - characterWalkFrameChangedMs >= WALK_FRAME_MS) {
            characterWalkFrame = (characterWalkFrame + 1) % 2;
            characterWalkFrameChangedMs = nowMs;
        }
        characterMoving = movedThisFrame;
    }

    private Direction getCharacterDirection() {
        int octant = Math.floorMod(
                (int) Math.round(barrelAngle / (Math.PI / 4.0)), 8);
        return switch (octant) {
            case 0 -> Direction.RIGHT;
            case 1 -> Direction.DOWN_RIGHT;
            case 2 -> Direction.DOWN;
            case 3 -> Direction.DOWN_LEFT;
            case 4 -> Direction.LEFT;
            case 5 -> Direction.UP_LEFT;
            case 6 -> Direction.UP;
            default -> Direction.UP_RIGHT;
        };
    }

    public void setHitstop(Hitstop hitstop) {
        this.hitstop = hitstop;
    }

    public InvulnerabilityWindow getHurtInvulnerability() {
        return hurtInvulnerability;
    }

    /**
     * Re-reads clothing mitigation and perk-driven dodge cooldown. Called on spawn and whenever the
     * persistent level might have changed mid-run.
     */
    public void refreshDefensiveBonuses() {
        save.SaveData saveData = save.SaveManager.load();
        wornArmorMitigation = WornArmor.mitigationFor(saveData == null ? null : saveData.getAppearance());
        dodge.setCooldownScale(PerkService.dodgeCooldownMultiplier(playerLevel));
    }

    @Override
    protected void drawBody(java.awt.Graphics2D g, int cameraX, int cameraY) {
        boolean holdingGun = heldWeapon instanceof combat.Ranged;
        Direction bodyDirection = getCharacterDirection();
        java.awt.image.BufferedImage frame = holdingGun
                ? characterRenderer.getGunBodyFrame(bodyDirection, characterMoving, characterWalkFrame)
                : characterRenderer.getFrame(bodyDirection, characterMoving, characterWalkFrame);
        if (frame == null) {
            super.drawBody(g, cameraX, cameraY);
            return;
        }

        if (holdingGun) {
            java.awt.image.BufferedImage rearArm = characterRenderer.getGunArmFrame(
                    CharacterRenderer.getAimDirection(barrelAngle), true);
            drawCharacterLayer(g, rearArm, cameraX, cameraY);
        }
        drawCharacterLayer(g, frame, cameraX, cameraY);
    }

    @Override
    protected void drawForeground(java.awt.Graphics2D g, int cameraX, int cameraY) {
        if (!(heldWeapon instanceof combat.Ranged)) {
            return;
        }
        java.awt.image.BufferedImage frontArm = characterRenderer.getGunArmFrame(
                CharacterRenderer.getAimDirection(barrelAngle), false);
        drawCharacterLayer(g, frontArm, cameraX, cameraY);
    }

    @Override
    protected int getWeaponDrawCenterY() {
        if (!(heldWeapon instanceof combat.Ranged)) {
            return super.getWeaponDrawCenterY();
        }
        int footOffset = (int) Math.round(90.0 / 96.0 * CHARACTER_RENDER_SIZE);
        int shoulderPivotOffset = (int) Math.round(40.0 / 96.0 * CHARACTER_RENDER_SIZE);
        return y + l - footOffset + shoulderPivotOffset;
    }

    @Override
    protected double getRangedWeaponVisualScale() {
        return 0.65 * CHARACTER_RENDER_SIZE / 96.0;
    }

    private void drawCharacterLayer(
            java.awt.Graphics2D g,
            java.awt.image.BufferedImage layer,
            int cameraX,
            int cameraY) {
        if (layer == null) {
            return;
        }
        int drawX = getCenterX() - cameraX - CHARACTER_RENDER_SIZE / 2;
        // Source feet sit at y=90 of 96. Anchor them to the bottom of the combat hitbox.
        int footOffset = (int) Math.round(90.0 / 96.0 * CHARACTER_RENDER_SIZE);
        int drawY = y + l - cameraY - footOffset;

        java.awt.Graphics2D spriteGraphics = (java.awt.Graphics2D) g.create();
        spriteGraphics.setRenderingHint(
                java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        spriteGraphics.drawImage(
                layer, drawX, drawY, CHARACTER_RENDER_SIZE, CHARACTER_RENDER_SIZE, null);
        spriteGraphics.dispose();
    }

    /** Keeps the health bar above the taller character art instead of across its torso. */
    @Override
    public void displayStats(java.awt.Graphics2D g, int cameraX, int cameraY) {
        int barWidth = w;
        int barHeight = 5;
        int barX = x - cameraX - 1;
        int footOffset = (int) Math.round(90.0 / 96.0 * CHARACTER_RENDER_SIZE);
        int barY = y + l - cameraY - footOffset - 8;

        g.setColor(Color.RED);
        g.fillRect(barX, barY, barWidth, barHeight);
        g.setColor(Color.GREEN);
        int currentBarWidth = (int) ((hp / maxHp) * barWidth);
        g.fillRect(barX, barY, currentBarWidth, barHeight);
        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    /**
     * Draws the player, plus a ring while a defensive window is open.
     *
     * <p>Invulnerability the player cannot see is indistinguishable from damage that missed, so the
     * ring is what turns i-frames from a hidden rule into a readable one.
     */
    @Override
    public void draw(java.awt.Graphics2D g, int cameraX, int cameraY) {
        super.draw(g, cameraX, cameraY);

        long now = System.currentTimeMillis();
        if (!isInvulnerable(now)) {
            return;
        }
        boolean dashing = dodge.isDashing(now);
        java.awt.Stroke previousStroke = g.getStroke();
        g.setStroke(new java.awt.BasicStroke(dashing ? 3f : 2f));
        g.setColor(dashing ? new Color(150, 225, 255, 220) : new Color(255, 255, 255, 120));
        int inset = 3;
        g.drawOval(x - cameraX - inset, y - cameraY - inset, w + inset * 2, l + inset * 2);
        g.setStroke(previousStroke);
    }

    public DodgeState getDodgeState() {
        return dodge;
    }

    /** True while any defensive window is open; used by the renderer to show the player is safe. */
    public boolean isInvulnerable(long nowMs) {
        return dodge.isInvulnerable(nowMs) || hurtInvulnerability.isActive(nowMs);
    }

    public boolean isDodging(long nowMs) {
        return dodge.isDashing(nowMs);
    }

    /**
     * Single entry point for every hit the player lands: applies damage, records stats and weapon
     * mastery, spawns feedback, optionally knocks the target back, and pays the kill reward.
     *
     * @param fromHeldWeapon true for bullet / swing damage, false for powers and burn ticks
     * @param knockbackPx    distance to shove the target away from the player, 0 for none
     */
    public void dealDamage(Entity target, double amount, boolean fromHeldWeapon, int knockbackPx) {
        applyOutgoingDamage(target, amount, false, fromHeldWeapon, knockbackPx, 0L);
    }

    /**
     * Lands a rolled hit, carrying the crit flag through to feedback and optionally applying a stun.
     *
     * @param roll        resolved damage and crit state
     * @param knockbackPx distance to shove the target away from the player, 0 for none
     * @param stunMs      stun to apply on hit, 0 for none; the target may shorten it
     */
    public void dealDamage(Entity target, DamageRoll roll, boolean fromHeldWeapon, int knockbackPx, long stunMs) {
        if (roll == null) {
            return;
        }
        applyOutgoingDamage(target, roll.getAmount(), roll.isCritical(), fromHeldWeapon, knockbackPx, stunMs);
    }

    private void applyOutgoingDamage(Entity target,
                                     double amount,
                                     boolean critical,
                                     boolean fromHeldWeapon,
                                     int knockbackPx,
                                     long stunMs) {
        if (target == null || target.isDead() || amount <= 0) {
            return;
        }
        target.takeDamage(amount);
        stats.addDamageDealt(amount);
        if (fromHeldWeapon && heldWeapon != null) {
            MasteryService.grantWeaponDamageXP(heldWeapon.getName(), amount);
        }
        target.flashHit();
        if (feedback != null) {
            feedback.addHit(target.getCenterX(), target.getCenterY(), amount, false, critical);
        }
        requestHitstopForHit(amount, critical);
        if (fromHeldWeapon) {
            applyLifesteal(amount);
        }
        if (!target.isDead()) {
            if (knockbackPx > 0) {
                target.knockBack(getCenterX(), getCenterY(), knockbackPx, arenaObstacles, arenaWidth, arenaHeight);
            }
            if (stunMs > 0) {
                target.applyStun(stunMs);
            }
        }
        if (target.isDead()) {
            awardKill(target, fromHeldWeapon);
        }
    }

    private void requestHitstopForHit(double amount, boolean critical) {
        if (hitstop == null) {
            return;
        }
        long duration = 0;
        if (critical) {
            duration = tuning.getHitstopCritMs();
        } else if (amount >= tuning.getHitstopHeavyDamageThreshold()) {
            duration = tuning.getHitstopHeavyMs();
        }
        if (duration > 0) {
            hitstop.request(System.currentTimeMillis(), duration);
        }
    }

    private void applyLifesteal(double amount) {
        double fraction = PerkService.lifestealFraction(playerLevel);
        if (fraction <= 0 || amount <= 0) {
            return;
        }
        heal(amount * fraction);
    }

    /** Kill payout (kill count, weapon mastery, XP, gold); pays out at most once per victim. */
    public void awardKill(Entity victim, boolean creditHeldWeapon) {
        if (victim == null || !victim.claimKillReward()) {
            return;
        }
        stats.addKill();
        if (creditHeldWeapon && heldWeapon != null) {
            MasteryService.grantWeaponKillXP(heldWeapon.getName(), 1);
        }
        int xp = victim.getXpValue();
        currencyManager.addGold(victim.getGoldValue());
        if (xp > 0) {
            gainXP(xp);
            save.SaveManager.addXP(xp);
            // Sync local level/XP fields immediately so the HUD updates in real-time
            save.SaveData updatedData = save.SaveManager.load();
            playerLevel = updatedData.getPlayerLevel();
            playerXP = updatedData.getPlayerXP();
        }
        if (feedback != null) {
            feedback.addShake(3.0, 110);
        }
    }

    /**
     * Delegates to {@link #applyIncomingDamage(double)} so that every route into the player's health
     * — enemy bullets, enemy melee, slams, future hazards — passes through the same defensive gate.
     */
    @Override
    public void takeDamage(double damageAmount) {
        applyIncomingDamage(damageAmount);
    }

    /**
     * Single entry point for damage aimed at the player. Applies dodge and post-hit invulnerability,
     * records the hit in run stats, and reports what happened.
     *
     * <p>Callers must not record damage-taken stats themselves: a negated hit is not damage taken,
     * and this method already books the ones that land.
     *
     * @return how the hit resolved; see {@link DamageOutcome}
     */
    public DamageOutcome applyIncomingDamage(double amount) {
        if (amount <= 0 || dead) {
            return DamageOutcome.IGNORED;
        }
        long now = System.currentTimeMillis();

        if (dodge.isInvulnerable(now)) {
            if (feedback != null) {
                feedback.addLabel(getCenterX(), y, "DODGE", CombatFeedback.DODGE_LABEL_COLOR);
            }
            return DamageOutcome.NEGATED_DODGE;
        }
        if (hurtInvulnerability.isActive(now)) {
            return DamageOutcome.NEGATED_IFRAMES;
        }

        double mitigated = amount * incomingDamageFraction();
        if (mitigated <= 0) {
            return DamageOutcome.IGNORED;
        }

        hurtInvulnerability.grant(now);
        super.takeDamage(mitigated);
        stats.addDamageTaken(mitigated);
        flashHit();
        if (feedback != null) {
            feedback.addHit(getCenterX(), getCenterY(), mitigated, true);
            feedback.addShake(Math.min(9.0, 2.0 + mitigated * 0.2), 170);
        }
        return DamageOutcome.APPLIED;
    }

    /**
     * Fraction of incoming damage that actually lands after worn armor and defense potions.
     * Never drops below {@link #MIN_INCOMING_DAMAGE_FRACTION}.
     */
    public double incomingDamageFraction() {
        expireConsumableEffects();
        double mitigation = wornArmorMitigation;
        for (ActiveConsumableEffect effect : activeEffects.values()) {
            if (effect.type == Consumable.ConsumableEffectType.DEFENSE_BOOST) {
                if (Double.isFinite(effect.multiplier) && effect.multiplier > 0) {
                    mitigation += Math.min(0.6, effect.multiplier);
                }
            }
        }
        return Math.max(MIN_INCOMING_DAMAGE_FRACTION, 1.0 - Math.max(0.0, mitigation));
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
            // Rolled once per shot rather than per projectile, so a shotgun blast is one crit or none.
            boolean critical = DamageRoll.rollsCritical(getCritChance(), Math.random());
            double damageMultiplier = getTotalDamageMultiplier()
                    * (critical ? SkillEffects.CRIT_DAMAGE_MULTIPLIER : 1.0);
            List<Projectile> bullets =
                    ranged.shoot(getCenterX(), getCenterY(), barrelAngle, damageMultiplier, critical);
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

    public double getBaseSpeed() { return baseSpeed; }

    public double getBaseDamage() { return baseDamage; }

    public void updateProjectiles() {
        updateProjectiles(null, 10000, 10000);
    }

    public void updateProjectiles(List<Rectangle> arenaObstacles, int arenaWidth, int arenaHeight) {
        for (Projectile p : projectiles) {
            p.update();
        }
        int maxX = arenaWidth > 0 ? arenaWidth : 10000;
        int maxY = arenaHeight > 0 ? arenaHeight : 10000;
        projectiles.removeIf(p -> {
            if (p.getX() < 0 || p.getX() > maxX || p.getY() < 0 || p.getY() > maxY) return true;
            return world.arena.ArenaCollision.projectileHitsObstacle(p.getX(), p.getY(), p.getRadius(), arenaObstacles);
        });
    }

    public void checkProjectileCollisions(entity.EnemyManager enemyManager) {
        ArrayList<Projectile> toRemove = new ArrayList<>();
        for (Projectile p : projectiles) {
            // Damage is read at impact, not at fire time, so distance drop-off applies.
            DamageRoll roll = DamageRoll.resolved(p.getEffectiveDamage(), p.isCritical());
            boolean hitSomething = false;
            for (entity.Enemy enemy : enemyManager.getEnemies()) {
                if (enemy.checkCollision(p.getX(), p.getY())) {
                    dealDamage(enemy, roll, true, 0, 0L);
                    stats.addShotHit();
                    toRemove.add(p);
                    hitSomething = true;
                    break; // Bullet hits one enemy then stops
                }
            }
            // Also check collision with active boss
            if (!hitSomething && enemyManager.getActiveBoss() != null) {
                entity.Boss boss = enemyManager.getActiveBoss();
                if (boss.checkCollision(p.getX(), p.getY())) {
                    dealDamage(boss, roll, true, 0, 0L);
                    stats.addShotHit();
                    toRemove.add(p);
                }
            }
        }
        projectiles.removeAll(toRemove);
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

    /** Movement speed including active speed-boost consumables. */
    public double getTotalSpeed() {
        expireConsumableEffects();
        double total = speed;
        for (ActiveConsumableEffect effect : activeEffects.values()) {
            if (effect.type == Consumable.ConsumableEffectType.SPEED_BOOST) {
                total *= effect.multiplier;
            }
        }
        return total;
    }

    /**
     * Rolls the damage for the melee swing in flight, including the combo finisher bonus and the crit
     * roll. Call once per hit so each target rolls its own crit.
     *
     * @return the roll, or a zero roll when no melee weapon is held
     */
    public DamageRoll rollMeleeDamage() {
        if (!(heldWeapon instanceof combat.Melee)) {
            return DamageRoll.flat(0);
        }
        combat.Melee melee = (combat.Melee) heldWeapon;
        double multiplier = getTotalDamageMultiplier() * melee.getActiveSwingDamageMultiplier();
        return DamageRoll.of(
                melee.getDamage(),
                multiplier,
                getCritChance(),
                SkillEffects.CRIT_DAMAGE_MULTIPLIER,
                Math.random());
    }

    /** Skill-tree crit chance plus unlocked perks, clamped the same way as {@link SkillEffects}. */
    public double getCritChance() {
        return Math.min(SkillEffects.MAX_CRIT_CHANCE, SkillEffects.critChance() + PerkService.critChanceBonus(playerLevel));
    }

    public int getEffectiveMeleeDamage() {
        return rollMeleeDamage().getAmount();
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
        return handleShootingMechanics(mouse, key, shouldDisableShooting, preUpdateLogic, arenaWidth, arenaHeight, null);
    }

    public boolean handleShootingMechanics(MouseHandler mouse, KeyHandler key, boolean shouldDisableShooting, Runnable preUpdateLogic, int arenaWidth, int arenaHeight, List<Rectangle> arenaObstacles) {
        // Save original mouse state
        boolean wasLeftPressed = mouse.leftPressed;

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
        update(key, mouse, arenaWidth, arenaHeight, arenaObstacles);

        // Restore leftPressed so drag doesn't break (don't restore leftClicked - it's one-shot)
        mouse.leftPressed = wasLeftPressed;

        return wasLeftPressed;
    }

    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public int getPlayerXP() {
        return playerXP;
    }
}
