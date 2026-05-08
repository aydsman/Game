# Abyss Class Documentation

**Total Classes:** 112

**Total Lines of Code:** 9,000+

## Table of Contents

- [Root Level](#root-level)
  - [Main.java](#mainjava)
- [combat/](#combat)
  - [Item.java](#itemjava-base-class)
  - [ItemRegistry.java](#itemregistryjava)
  - [Melee.java](#meleejava-extends-item)
  - [Ranged.java](#rangedjava-extends-item)
  - [Projectile.java](#projectilejava)
  - [combat.charms/](#charms)
    - [Charm.java](#charmjava-extends-item)
    - [Charm1.java](#charm1java-extends-charm)
    - [SpeedCharm.java](#speedcharmjava-extends-charm)
  - [summons/](#summons)
    - [Summon.java](#summonjava-extends-item)
    - [Summon1.java](#summon1java-extends-summon)
    - [Summon2.java](#summon2java-extends-summon)
  - [combat.powers/](#powers)
    - [Power.java](#powerjava-extends-item)
    - [Earth.java](#earthjava-extends-power)
    - [EarthV2.java](#earthv2java-extends-power)
    - [Fire.java](#firejava-extends-power)
    - [FireV2.java](#firev2java-extends-power)
    - [Infinity.java](#infinityjava-extends-power)
    - [KingOfCurses.java](#kingofcursesjava-extends-power)
    - [Light.java](#lightjava-extends-power)
    - [Lightning.java](#lightningjava-extends-power)
    - [LightningV2.java](#lightningv2java-extends-power)
    - [Magma.java](#magmajava-extends-power)
    - [MagmaV2.java](#magmav2java-extends-power)
    - [Move.java](#movejava-extends-power)
    - [RinneSharingan.java](#rinne sharinganjava-extends-power)
    - [Water.java](#waterjava-extends-power)
  - [combat.consumables/](#consumables)
    - [Consumable.java](#consumablejava-extends-item)
    - [Consumable1.java](#consumable1java-extends-consumable)
    - [Consumable2.java](#consumable2java-extends-consumable)
    - [Consumable3.java](#consumable3java-extends-consumable)
    - [DamagePotion.java](#damagepotionjava-extends-consumable)
    - [DamagePotion2.java](#damagepotion2java-extends-consumable)
    - [DamagePotion3.java](#damagepotion3java-extends-consumable)
- [combat/melee/](#combatmelee)
  - [daggers/](#daggers)
    - [Dagger.java](#daggerjava-extends-melee)
    - [Dagger1.java](#dagger1java-extends-dagger)
  - [hammers/](#hammers)
    - [Hammer.java](#hammerjava-extends-melee)
    - [Hammer1.java](#hammer1java-extends-hammer)
  - [maces/](#maces)
    - [Mace.java](#macejava-extends-melee)
    - [Mace1.java](#mace1java-extends-mace)
  - [scythes/](#scythes)
    - [Scythe.java](#scythejava-extends-melee)
    - [Scythe1.java](#scythe1java-extends-scythe)
  - [swords/](#swords)
    - [Sword.java](#swordjava-extends-melee)
    - [Sword1.java](#sword1java-extends-sword)
- [combat/ranged/](#combatranged)
  - [pistols/](#pistols)
    - [Pistol.java](#pistoljava-extends-ranged)
    - [Pistol1.java](#pistol1java-extends-pistol)
  - [rifles/](#rifles)
    - [Rifle.java](#riflejava-extends-ranged)
    - [Rifle1.java](#rifle1java-extends-rifle)
  - [shotguns/](#shotguns)
    - [Shotgun.java](#shotgunjava-extends-ranged)
    - [Shotgun1.java](#shotgun1java-extends-shotgun)
  - [smgs/](#smgs)
    - [SMG.java](#smgjava-extends-ranged)
    - [SMG1.java](#smg1java-extends-smg)
  - [snipers/](#snipers)
    - [Sniper.java](#sniperjava-extends-ranged)
    - [Sniper1.java](#sniper1java-extends-sniper)
- [entity/](#entity)
  - [Entity.java](#entityjava-base-class)
  - [Player.java](#playerjava-extends-entity)
  - [Enemy.java](#enemyjava-extends-entity)
  - [EnemyManager.java](#enemymanagerjava)
  - [Boss.java](#bossjava-extends-entity)
- [entity/enemies/](#entityenemies)
  - [Enemy1.java](#enemy1java-extends-enemy)
  - [Enemy2.java](#enemy2java-extends-enemy)
  - [Enemy3.java](#enemy3java-extends-enemy)
  - [Enemy4.java](#enemy4java-extends-enemy)
  - [Enemy5.java](#enemy5java-extends-enemy)
- [entity/boss/](#entityboss)
  - [Boss1.java](#boss1java-extends-boss)
- [inventory/](#inventory)
  - [Inventory.java](#inventoryjava)
  - [Charm.java](#inventorycharmjava) (combat/inventory/)
  - [Charm2.java](#charm2java)
  - [Charm3.java](#charm3java)
  - [Power.java](#inventorypowerjava)
  - [Power2.java](#power2java)
  - [Summon.java](#inventorysummonjava)
  - [Summon2.java](#summon2java)
- [progression/](#progression)
  - [XP.java](#xpjava)
- [ui/](#ui)
  - [Game.java](#gamejava)
  - [GamePanel.java](#gamepaneljava-extends-jpanel-implements-runnable)
  - [HUD.java](#hudjava)
  - [InventoryUI.java](#inventoryuijava)
- [ui/screens/](#uiscreens)
  - [MenuScreen.java](#menuscreenjava)
  - [GameScreen.java](#gamescreenjava)
  - [PauseScreen.java](#pausescreenjava)
  - [CustomizeScreen.java](#customizescreenjava)
  - [SettingsScreen.java](#settingsscreenjava)
  - [HelpScreen.java](#helpscreenjava)
  - [ItemGalleryScreen.java](#itemgalleryscreenjava)
  - [GraphTestScreen.java](#graphtestscreenjava)
  - [LoadoutScreen.java](#loadoutscreenjava)
  - [ShopScreen.java](#shopscreenjava)
- [util/](#util)
  - [Camera.java](#camerajava)
  - [KeyHandler.java](#keyhandlerjava-implements-keylistener)
  - [MouseHandler.java](#mousehandlerjava-implements-mouselistener-mousemotionlistener-mousewheellistener)
- [world/](#world)
  - [Arena.java](#arenajava-base-class)
- [world/arenas/](#worldarenas)
  - [ArenaTest.java](#arenatestjava-extends-arena)
- [world/dungeon/](#worlddungeon)
  - [DungeonArenaScreen.java](#dungeonarenascreenjava)

---

## Root Level

### Main.java
- `main(String[] args)` - Entry point, creates new Game instance

---

## combat/

### Item.java (Base class)
**Fields:**
- `name` (String)
- `tier` (int) - 1-5 (I-V)
- `rarity` (String) - Tier I, Tier II, Tier III, Tier IV, Tier V
- `description` (String)

**Methods:**
- `Item()` - Default constructor, sets tier=1, rarity="Tier I"
- `Item(int tier)` - Constructor with tier, sets rarity from tier
- `getName()` - Returns name
- `getTier()` - Returns tier
- `getRarity()` - Returns rarity
- `getDescription()` - Returns description
- `setName(String name)` - Sets name
- `setDescription(String description)` - Sets description

### ItemRegistry.java
**Fields:**
- `allItems` (List<Item>) - All registered items
- `weapons` (List<Item>) - Weapon items
- `combat.charms` (List<Item>) - Charm items
- `summons` (List<Item>) - Summon items
- `combat.powers` (List<Item>) - Power items
- `combat.consumables` (List<Item>) - Consumable items
- `LOOT_TABLE` (double[][]) - Chest tier probabilities

**Methods:**
- `ItemRegistry()` - Constructor, registers all items
- `getAllItems()` - Returns all items
- `getWeapons()` - Returns weapons list
- `getCharms()` - Returns combat.charms list
- `getSummons()` - Returns summons list
- `getPowers()` - Returns combat.powers list
- `getConsumables()` - Returns combat.consumables list
- `getItemsByTier(int tier)` - Returns items of specific tier
- `getRandomItem()` - Returns random item
- `getRandomItemForChest(int chestTier)` - Returns random item based on chest tier loot table

### Melee.java (extends Item)
**Fields:**
- `attackSpeed` (double) - attacks per second
- `damage` (int) - damage per attack
- `range` (double) - attack range in pixels
- `knockback` (double) - knockback force
- `swingAngle` (double) - total arc width in degrees (default 90)
- `swingSpeed` (double) - animation speed per frame (default 0.15)
- `attackDelay` (double) - seconds between attacks (default 0.5)
- `automatic` (boolean) - hold to auto-swing
- `isSwinging` (boolean) - active swing state
- `swingProgress` (double) - 0.0 to 1.0 progress
- `swingAngleStart/End` (double) - arc boundaries in radians
- `swingCenterAngle` (double) - direction of swing
- `lastAttackTime` (long) - timestamp of last attack
- `hitEntitiesThisSwing` (List<Entity>) - tracks entities hit in current swing

**Methods:**
- `Melee()` - Default constructor with base stats
- `Melee(int tier)` - Constructor with tier and multipliers
- `attack()` - Attack logic (placeholder)
- `canAttack()` - Returns true if not swinging and attack delay elapsed
- `startSwing(double centerAngleRadians)` - Initiates swing toward angle
- `updateSwing()` - Updates swing progress each frame
- `endSwing()` - Completes swing and resets state
- `getCurrentSwingAngle()` - Returns current interpolated swing angle
- `isInSwingArc(double px, double py, int playerX, int playerY)` - Checks if point is within swing arc and range
- `normalizeAngle(double angle)` - Normalizes angle to [0, 2PI]
- Getters: `isSwinging()`, `getSwingProgress()`, `getSwingAngle()`, `getRange()`, `isAutomatic()`, `getAttackDelay()`, `getHitEntitiesThisSwing()`, `getDamage()`, `getAttackSpeed()`, `getKnockback()`
- `setSwingProperties(angle, speed, delay, automatic)` - Configures swing behavior
- `clone()` - Deep clone with reset swing state

### Ranged.java (extends Item)
**Fields:**
- `fireRate` (double) - seconds between shots
- `damage` (int) - damage per shot
- `accuracy` (double) - 0.0 to 1.0 (100%)
- `accuracyAngle` (double) - degrees of spread for accuracy
- `magazineSize` (int) - bullets per magazine
- `reloadTime` (double) - seconds to reload
- `currentAmmo` (int) - current ammo in magazine
- `automatic` (boolean) - true for automatic, false for semi-automatic
- `barrelColor` (String) - color of the barrel
- `projectileColor` (String) - color of the projectile
- `barrelLength` (int) - length of the barrel
- `barrelHeight` (int) - height of the barrel
- `isReloading` (boolean) - reload state
- `reloadStartTime` (long) - when reload started

**Methods:**
- `Ranged()` - Default constructor with base stats
- `Ranged(int tier)` - Constructor with tier and multipliers
- `getBarrelTip(int centerX, int centerY, double barrelAngle)` - Returns barrel tip position
- `shoot(int centerX, int centerY, double barrelAngle)` - Creates and returns List<Projectile>
- `reload()` - Starts reload
- `updateReload()` - Updates reload state
- `getBarrelColor()` - Returns barrel color
- `getBarrelLength()` - Returns barrel length
- `getBarrelHeight()` - Returns barrel height
- `getName()` - Returns name
- `getFireRate()` - Returns fire rate
- `getCurrentAmmo()` - Returns current ammo
- `getMagazineSize()` - Returns magazine size
- `isAutomatic()` - Returns automatic status
- `isReloading()` - Returns reloading status
- `getAccuracyAngle()` - Returns accuracy angle

### Projectile.java
**Fields:**
- `x` (int)
- `y` (int)
- `color` (Color)
- `speed` (double)
- `angle` (double)
- `damage` (int)
- `radius` (int)

**Methods:**
- `Projectile(int x, int y, Color color, double speed)` - Constructor
- `Projectile(int x, int y, Color color, double speed, double angle, int damage)` - Full constructor
- `Projectile(int x, int y, Color color, double speed, double angle, int damage, int radius)` - Constructor with radius
- `update()` - Updates position based on speed and angle
- `getX()` - Returns x
- `getY()` - Returns y
- `getColor()` - Returns color
- `getSpeed()` - Returns speed
- `getAngle()` - Returns angle
- `getDamage()` - Returns damage
- `setAngle(double angle)` - Sets angle

---

## combat/combat.charms/

### Charm.java (extends Item)
- `Charm()` - Default constructor
- `Charm(int tier)` - Constructor with tier

### Charm1.java (extends Charm)
- `Charm1()` - Tier I charm
- `Charm1(int tier)` - Constructor with tier

### SpeedCharm.java (extends Charm)
- `SpeedCharm()` - Speed boost charm (+10% movement speed)
- `SpeedCharm(int tier)` - Constructor with tier
- `getSpeedBonus()` - Returns speed percentage bonus

---

## combat/summons/

### Summon.java (extends Item)
- `Summon()` - Default constructor
- `Summon(int tier)` - Constructor with tier

### Summon1.java (extends Summon)
- `Summon1()` - Tier I summon
- `Summon1(int tier)` - Constructor with tier

### Summon2.java (extends Summon)
- `Summon2()` - Tier II summon
- `Summon2(int tier)` - Constructor with tier

---

## combat/combat.powers/

### Power.java (extends Item)
- `Power()` - Default constructor
- `Power(int tier)` - Constructor with tier

### Earth.java (extends Power)
- `Earth()` - Earth elemental power
- `Earth(int tier)` - Constructor with tier

### EarthV2.java (extends Power)
- `EarthV2()` - Enhanced earth power
- `EarthV2(int tier)` - Constructor with tier

### Fire.java (extends Power)
- `Fire()` - Fire elemental power
- `Fire(int tier)` - Constructor with tier

### FireV2.java (extends Power)
- `FireV2()` - Enhanced fire power
- `FireV2(int tier)` - Constructor with tier

### Infinity.java (extends Power)
- `Infinity()` - Infinity power (unlimited ammo mode)
- `Infinity(int tier)` - Constructor with tier

### KingOfCurses.java (extends Power)
- `KingOfCurses()` - King of Curses power (powerful curse abilities)
- `KingOfCurses(int tier)` - Constructor with tier

### Light.java (extends Power)
- `Light()` - Light elemental power
- `Light(int tier)` - Constructor with tier

### Lightning.java (extends Power)
- `Lightning()` - Lightning elemental power
- `Lightning(int tier)` - Constructor with tier

### LightningV2.java (extends Power)
- `LightningV2()` - Enhanced lightning power
- `LightningV2(int tier)` - Constructor with tier

### Magma.java (extends Power)
- `Magma()` - Magma elemental power (fire + earth)
- `Magma(int tier)` - Constructor with tier

### MagmaV2.java (extends Power)
- `MagmaV2()` - Enhanced magma power
- `MagmaV2(int tier)` - Constructor with tier

### Move.java (extends Power)
- `Move()` - Movement enhancement power
- `Move(int tier)` - Constructor with tier

### RinneSharingan.java (extends Power)
- `RinneSharingan()` - Rinne Sharingan power (special abilities)
- `RinneSharingan(int tier)` - Constructor with tier

### Water.java (extends Power)
- `Water()` - Water elemental power
- `Water(int tier)` - Constructor with tier

---

## combat/combat.consumables/

### Consumable.java (extends Item)
- `Consumable()` - Default constructor
- `Consumable(int tier)` - Constructor with tier

### Consumable1.java (extends Consumable)
- `Consumable1()` - Tier I consumable (Small health potion)
- `Consumable1(int tier)` - Constructor with tier

### Consumable2.java (extends Consumable)
- `Consumable2()` - Tier II consumable (Medium health potion)
- `Consumable2(int tier)` - Constructor with tier

### Consumable3.java (extends Consumable)
- `Consumable3()` - Tier III consumable (Large health potion)
- `Consumable3(int tier)` - Constructor with tier

### DamagePotion.java (extends Consumable)
- `DamagePotion()` - Tier I damage buff potion (10s duration, 1.2x multiplier)
- `DamagePotion(int tier)` - Constructor with tier
- `getEffect()` - Returns "damage_boost"
- `getEffectDuration()` - Returns 10 seconds
- `getEffectMultiplier()` - Returns 1.2 multiplier

### DamagePotion2.java (extends Consumable)
- `DamagePotion2()` - Tier II damage buff potion (15s duration, 1.4x multiplier)
- `DamagePotion2(int tier)` - Constructor with tier
- `getEffect()` - Returns "damage_boost"
- `getEffectDuration()` - Returns 15 seconds
- `getEffectMultiplier()` - Returns 1.4 multiplier

### DamagePotion3.java (extends Consumable)
- `DamagePotion3()` - Tier III damage buff potion (20s duration, 1.6x multiplier)
- `DamagePotion3(int tier)` - Constructor with tier
- `getEffect()` - Returns "damage_boost"
- `getEffectDuration()` - Returns 20 seconds
- `getEffectMultiplier()` - Returns 1.6 multiplier

---

## combat/melee/

### daggers/
#### Dagger.java (extends Melee)
- `Dagger()` - High attack speed, low damage, low range
- `Dagger(int tier)` - Constructor with tier

#### Dagger1.java (extends Dagger)
- `Dagger1()` - Tier I dagger

### hammers/
#### Hammer.java (extends Melee)
- `Hammer()` - Low attack speed, high damage, high knockback
- `Hammer(int tier)` - Constructor with tier

#### Hammer1.java (extends Hammer)
- `Hammer1()` - Tier I hammer

### maces/
#### Mace.java (extends Melee)
- `Mace()` - Low attack speed, high damage, medium range
- `Mace(int tier)` - Constructor with tier

#### Mace1.java (extends Mace)
- `Mace1()` - Tier I mace

### scythes/
#### Scythe.java (extends Melee)
- `Scythe()` - Medium attack speed, medium damage, high range
- `Scythe(int tier)` - Constructor with tier

#### Scythe1.java (extends Scythe)
- `Scythe1()` - Tier I scythe

### swords/
#### Sword.java (extends Melee)
- `Sword()` - Balanced attack speed, medium damage, medium range
- `Sword(int tier)` - Constructor with tier

#### Sword1.java (extends Sword)
- `Sword1()` - Tier I sword

---

## combat/ranged/

### pistols/
#### Pistol.java (extends Ranged)
- `Pistol()` - Fast fire rate, low damage, high accuracy, semi-automatic
- `Pistol(int tier)` - Constructor with tier

#### Pistol1.java (extends Pistol)
- `Pistol1()` - Tier I pistol

### rifles/
#### Rifle.java (extends Ranged)
- `Rifle()` - Medium fire rate, medium damage, medium accuracy, automatic
- `Rifle(int tier)` - Constructor with tier

#### Rifle1.java (extends Rifle)
- `Rifle1()` - Tier I rifle

### shotguns/
#### Shotgun.java (extends Ranged)
- `Shotgun()` - Slow fire rate, high damage, low accuracy, semi-automatic, multiple pellets
- `Shotgun(int tier)` - Constructor with tier

#### Shotgun1.java (extends Shotgun)
- `Shotgun1()` - Tier I shotgun

### smgs/
#### SMG.java (extends Ranged)
- `SMG()` - Very fast fire rate, low damage, medium accuracy, automatic
- `SMG(int tier)` - Constructor with tier

#### SMG1.java (extends SMG)
- `SMG1()` - Tier I SMG

### snipers/
#### Sniper.java (extends Ranged)
- `Sniper()` - Very slow fire rate, very high damage, very high accuracy, semi-automatic
- `Sniper(int tier)` - Constructor with tier

#### Sniper1.java (extends Sniper)
- `Sniper1()` - Tier I sniper

---

## entity/

### Entity.java (Base class)
**Fields:**
- `x, y` (int) - position
- `w, l` (int) - size (default 30x30)
- `hp, maxHp` (double) - health
- `damage` (double) - damage multiplier
- `speed` (double) - speed multiplier
- `ranged` (boolean) - whether entity uses ranged attacks
- `barrelAngle` (double) - angle barrel is pointing (radians)
- `color` (Color) - entity color
- `heldWeapon` (Ranged) - weapon entity is holding
- `dead` (boolean) - death status
- `level` (int) - entity level
- `currentXP` (double) - current experience points
- `maxXP` (double) - XP needed for next level

**Methods:**
- `Entity(int x, int y)` - Constructor
- `getX()` - Returns x
- `getY()` - Returns y
- `getW()` - Returns width
- `getL()` - Returns length
- `getHp()` - Returns hp
- `getMaxHp()` - Returns max hp
- `getCenterX()` - Returns center x
- `getCenterY()` - Returns center y
- `isDead()` - Returns dead status
- `getLevel()` - Returns level
- `getCurrentXP()` - Returns current XP
- `getMaxXP()` - Returns max XP
- `checkCollision(int px, int py)` - Checks if point collides with entity
- `checkDeath()` - Checks if hp <= 0, sets dead=true
- `gainXP(double amount)` - Adds XP, levels up if threshold reached
- `levelUp()` - Increases level, increases XP requirement
- `setWeapon(Ranged weapon)` - Sets held weapon
- `getHeldWeapon()` - Returns held weapon
- `selectWeapon(String weaponType, int weaponId)` - Selects weapon by type and ID
- `draw(Graphics2D g, int cameraX, int cameraY)` - Draws entity
- `displayStats(Graphics2D g, int cameraX, int cameraY)` - Draws HP bar

### Player.java (extends Entity)
**Fields:**
- `hotbar` (ArrayList<Object>) - Player's 5-slot hotbar (slots 0-4, null for empty)
- `xpMultiplier` (double) - XP multiplier
- `projectiles` (ArrayList<Projectile>) - Player's projectiles
- `lastShotTime` (long) - Time of last shot
- `gameStartTime` (long) - Game start time
- `canShoot` (boolean) - Can shoot flag
- `stats` (PlayerStats) - Player statistics tracking
- `cameraX, cameraY` (int) - Camera offset for mouse calculations
- `inventory` (Inventory) - Player inventory system
- `activeConsumableEffects` (List<ActiveEffect>) - Active consumable buffs/debuffs

**Methods:**
- `Player(int x, int y)` - Constructor
- `playerSpawn()` - Initializes player with 5-slot hotbar
- `resetMouseClicks(MouseHandler mouse)` - Resets mouse clicks
- `update(KeyHandler key, MouseHandler mouse, int arenaWidth, int arenaHeight)` - Updates player movement, attacks, reloads
- `handleMeleeAttack(MouseHandler mouse, Melee melee)` - Handles melee swing attacks
- `handleRangedAttack(MouseHandler mouse, long currentTime)` - Handles ranged shooting with fire rate
- `setCameraOffset(int x, int y)` - Sets camera offset for mouse aiming
- `aimBarrel(int mouseX, int mouseY)` - Aims barrel at mouse position
- `shoot()` - Shoots weapon
- `updateProjectiles()` - Updates all projectiles
- `checkProjectileCollisions(EnemyManager enemyManager)` - Checks projectile-enemy collisions and boss hits
- `getProjectiles()` - Returns projectiles list
- `getHotbar()` - Returns hotbar list (5 fixed slots)
- `equipHotbarSlot(int slot)` - Equips item from hotbar slot (supports Ranged and Melee)
- `getStats()` - Returns PlayerStats instance
- `applyLoadout(String weaponName, int weaponTier, String charmName, String powerName, String summonName, String consumableName)` - Applies starting loadout configuration
- `createWeaponFromLoadout(String weaponName, int tier)` - Creates weapon instance for loadout
- `createCharmFromLoadout(String charmName)` - Creates charm instance for loadout
- `useConsumable(int slot)` - Uses consumable from inventory slot
- `addConsumableEffect(Consumable consumable)` - Adds active consumable effect
- `updateConsumableEffects()` - Updates and expires consumable effects
- `getEffectiveDamageMultiplier()` - Returns damage multiplier with consumable buffs
- `getInventory()` - Returns inventory instance

### PlayerStats.java
**Fields:**
- `kills` (int) - Total enemies killed
- `deaths` (int) - Total deaths
- `damageDealt` (double) - Total damage dealt
- `damageTaken` (double) - Total damage taken
- `shotsFired` (int) - Total shots fired
- `shotsHit` (int) - Total shots hit

**Methods:**
- `addKill()` - Increments kill count
- `addDeath()` - Increments death count
- `addDamageDealt(double damage)` - Adds to damage dealt
- `addDamageTaken(double damage)` - Adds to damage taken
- `addShotFired()` - Increments shots fired
- `addShotHit()` - Increments shots hit
- `getKills()` - Returns kills
- `getDeaths()` - Returns deaths
- `getDamageDealt()` - Returns damage dealt
- `getDamageTaken()` - Returns damage taken
- `getShotsFired()` - Returns shots fired
- `getShotsHit()` - Returns shots hit
- `getAccuracy()` - Returns hit percentage
- `reset()` - Resets all stats to 0

### Enemy.java (extends Entity)
**Fields:**
- `projectiles` (ArrayList<Projectile>) - Enemy's projectiles
- `lastShotTime` (long) - Time of last shot
- `detectionRadius` (int) - Detection radius in pixels
- `debugMode` (boolean) - Debug mode flag
- `xpValue` (int) - XP given when killed

**Methods:**
- `Enemy(int x, int y)` - Constructor
- `move(Player player, int arenaWidth, int arenaHeight)` - Moves toward player
- `aimBarrel(Player player)` - Aims barrel at player
- `isPlayerInRange(Player player)` - Checks if player is in detection radius
- `getDetectionRadius()` - Returns detection radius
- `setDetectionRadius(int radius)` - Sets detection radius
- `setDebugMode(boolean debugMode)` - Sets debug mode
- `draw(Graphics2D g, int cameraX, int cameraY)` - Draws enemy with debug circle
- `shoot()` - Shoots at player
- `updateProjectiles()` - Updates projectiles
- `updateWeapon()` - Updates weapon reload
- `getProjectiles()` - Returns projectiles list

### EnemyManager.java
**Fields:**
- `enemies` (ArrayList<Enemy>) - List of enemies

**Methods:**
- `spawnEnemy(int x, int y, int id)` - Spawns enemy by ID
- `update(Player player, int arenaWidth, int arenaHeight)` - Updates all enemies
- `setDebugMode(boolean debugMode)` - Sets debug mode for all enemies
- `checkEnemyProjectileCollisions(Player player)` - Checks enemy projectile collisions with player
- `drawEnemyProjectiles(Graphics2D g, int cameraX, int cameraY)` - Draws enemy projectiles
- `draw(Graphics2D g, int cameraX, int cameraY)` - Draws all enemies
- `getEnemies()` - Returns enemies list
- `removeEnemy(Enemy enemy)` - Removes specific enemy
- `clear()` - Clears all enemies

### Boss.java (extends Entity)
**Fields:** (inherited from Entity)

**Methods:**
- `Boss(int x, int y)` - Constructor with high HP and damage

---

## entity/enemies/

### Enemy1.java (extends Enemy)
- `Enemy1(int x, int y)` - Blue enemy with pistol

### Enemy2.java (extends Enemy)
- `Enemy2(int x, int y)` - Enemy variant 2

### Enemy3.java (extends Enemy)
- `Enemy3(int x, int y)` - Enemy variant 3

### Enemy4.java (extends Enemy)
- `Enemy4(int x, int y)` - Enemy variant 4

### Enemy5.java (extends Enemy)
- `Enemy5(int x, int y)` - Enemy variant 5

---

## entity/boss/

### Boss1.java (extends Boss)
- `Boss1(int x, int y)` - Boss with damage=3.0

---

## inventory/

### Inventory.java
**Fields:**
- `hotbar` (Item[]) - 5 slots for quick access
- `selectedSlot` (int) - Currently selected hotbar slot (0-4)

**Methods:**
- `Inventory()` - Constructor with 5-slot hotbar
- `setItem(int slot, Item item)` - Sets item in slot
- `getItem(int slot)` - Gets item from slot
- `getSelectedItem()` - Gets selected slot item
- `setSelectedSlot(int slot)` - Sets selected slot
- `getSelectedSlot()` - Returns selected slot
- `getHotbarSize()` - Returns hotbar size (5)
- `scrollHotbar(int direction)` - Scrolls hotbar with loop-around
- `equipCharm(int index, Charm charm)` - Equips charm at index
- `getCharm(int index)` - Returns charm at index
- `removeCharm(int index)` - Removes charm at index
- `setPlayer(Player player)` - Sets player reference
- `applyCharmEffects()` - Recalculates and applies all charm effects

### combat/inventory/Charm.java
- `Charm()` - Constructor for inventory charm
- `Charm(int tier)` - Constructor with tier
- `applyEffect(Player player)` - Applies charm effect to player

### Charm2.java
- `Charm2()` - Tier II inventory charm
- `Charm2(int tier)` - Constructor with tier

### Charm3.java
- `Charm3()` - Tier III inventory charm
- `Charm3(int tier)` - Constructor with tier

### Power.java (combat/inventory/)
- `Power()` - Constructor for inventory power
- `Power(int tier)` - Constructor with tier
- `activate()` - Activates power effect

### Power2.java
- `Power2()` - Tier II inventory power
- `Power2(int tier)` - Constructor with tier

### Summon.java (combat/inventory/)
- `Summon()` - Constructor for inventory summon
- `Summon(int tier)` - Constructor with tier
- `summon()` - Summons entity

### Summon2.java
- `Summon2()` - Tier II inventory summon
- `Summon2(int tier)` - Constructor with tier

### LootBox.java (combat/lootboxes/)
- `LootBox()` - Constructor
- `open()` - Opens loot box and returns random items
- `getTier()` - Returns loot box tier
- `setTier(int tier)` - Sets loot box tier

---

## progression/

### XP.java
**Fields:**
- `level` (int)
- `currentXP` (int)
- `xpToNextLevel` (int)
- `totalXP` (int)
- `xpMultiplier` (double)

**Methods:**
- `XP(double xpMultiplier)` - Constructor
- `addXP(int amount)` - Adds XP with multiplier
- `setXpMultiplier(double multiplier)` - Sets XP multiplier
- `removeXP(int amount)` - Removes XP
- `resetXP()` - Resets XP to level 1
- `getLevel()` - Returns level
- `getCurrentXP()` - Returns current XP
- `getXpToNextLevel()` - Returns XP to next level
- `getTotalXP()` - Returns total XP
- `getProgressToNextLevel()` - Returns progress percentage

---

## ui/

### Game.java
**Methods:**
- `Game()` - Creates JFrame with GamePanel

### GamePanel.java (extends JPanel, implements Runnable)
**Fields:**
- `SCREEN_WIDTH` (int) - 1600
- `SCREEN_HEIGHT` (int) - 900
- `FPS` (int) - 60
- `keyHandler` (KeyHandler)
- `mouseHandler` (MouseHandler)
- `menuScreen` (MenuScreen)
- `gameScreen` (GameScreen)
- `pauseScreen` (PauseScreen)
- `customizeScreen` (CustomizeScreen)
- `settingsScreen` (SettingsScreen)
- `helpScreen` (HelpScreen)
- `graphTestScreen` (GraphTestScreen)
- `itemGalleryScreen` (ItemGalleryScreen)
- `dungeonArenaScreen` (DungeonArenaScreen)
- `loadoutScreen` (LoadoutScreen)
- `shopScreen` (ShopScreen)
- `showMenu, showGame, showPause, showCustomize, showSettings, showHelp, showGraphTest, showItems, showDungeonArena, showLoadout, showShop` (boolean) - Screen states
- `gameThread` (Thread)

**Methods:**
- `GamePanel()` - Constructor, initializes screens and input handlers
- `switchScreen(String screen)` - Switches to specified screen (applies loadout when starting game)
- `run()` - Game loop
- `update()` - Updates game screen (handles U key for Loadout debug, level keys for GraphTest)
- `paintComponent(Graphics g)` - Draws current screen

### HUD.java
**Fields:**
- `inventoryUI` (InventoryUI)

**Methods:**
- `HUD()` - Constructor
- `draw(Graphics2D g, Player player, int screenWidth, int screenHeight)` - Draws HUD (HP, XP, weapon, hotbar)
- `getInventoryUI()` - Returns InventoryUI instance

### InventoryUI.java
**Fields:**
- `slotSize` (int) - 50 pixels
- `slotSpacing` (int) - 5 pixels
- `selectedSlot` (int) - Currently selected slot (0-4)

**Methods:**
- `draw(Graphics2D g, ArrayList<Object> hotbar, int screenWidth, int screenHeight)` - Draws hotbar UI
- `setSelectedSlot(int slot)` - Sets selected slot
- `getSelectedSlot()` - Returns selected slot

### ChestUI.java
**Fields:**
- `slotSize` (int) - 50 pixels
- `slotSpacing` (int) - 5 pixels
- `hoveredSlot` (int) - Currently hovered slot index (-1 if none)

**Methods:**
- `ChestUI()` - Constructor
- `draw(Graphics2D g, Chest chest, int chestX, int chestY, int screenWidth, int screenHeight)` - Draws chest UI with slot highlighting
- `setHoveredSlot(int slot)` - Sets hovered slot
- `getHoveredSlot()` - Returns hovered slot
- `getSlotAtPosition(mouseX, mouseY, chest, chestX, chestY)` - Returns slot at position (uses center point)
- `getSlotAtPosition(itemX, itemY, itemW, itemH, chest, chestX, chestY)` - Returns slot with item overlap detection
- `getSlotSize()` - Returns slot size
- `getSlotSpacing()` - Returns slot spacing

---

## ui/screens/

### MenuScreen.java
**Fields:**
- `gamePanel` (GamePanel)
- `playBtn, customizeBtn, settingsBtn, helpBtn` (Rectangle) - Button rectangles

**Methods:**
- `MenuScreen(GamePanel gamePanel)` - Constructor
- `handleClick(int x, int y)` - Handles button clicks
- `draw(Graphics2D g)` - Draws menu screen
- `drawButton(Graphics2D g, Rectangle btn, String label)` - Draws a button

### GameScreen.java
**Fields:**
- `arena` (ArenaTest) - Current arena
- `player` (Player) - Player entity
- `camera` (Camera) - Camera following player
- `hud` (HUD) - Heads-up display
- `chestUI` (ChestUI) - Chest UI renderer
- `enemyManager` (EnemyManager) - Manages all enemies
- `waveManager` (WaveManager) - Handles enemy waves
- `chests` (ArrayList<Chest>) - List of chests in arena
- `activeChest` (Chest) - Currently open chest
- `draggedItem` (Item) - Item being dragged
- `dragSource` (int) - Source of drag (-1=inventory, 0=chest)
- `dragSourceSlot` (int) - Source slot index
- `debugMode` (boolean) - Debug info display
- `wavesEnabled` (boolean) - Wave spawning enabled
- `statsPanelVisible` (boolean) - Stats panel toggle
- `lastMouseX/Y` (int) - Mouse position for dragging

**Methods:**
- `GameScreen()` - Constructor, initializes wave manager
- `resetMouseClicks(MouseHandler mouse)` - Resets mouse clicks
- `update(...)` - Updates game state (input, player, enemies, waves, drag-drop)
- `draw(...)` - Renders game (arena, entities, projectiles, UI, dragged item)
- `findClosestChestInRange()` - Finds nearest chest within interaction range
- `handleDragAndDrop(...)` - Manages item dragging with slot swapping between inventory and chests
- `getInventorySlotAtPosition(...)` - Overlap-based slot detection for items
- `getInventorySlotAtPositionSimple(...)` - Point-based slot detection for mouse
- `checkMeleeCollisions(...)` - Checks melee hits on enemies
- `checkMeleeBossCollision(...)` - Checks melee hits on boss

### PauseScreen.java
**Methods:**
- `draw(Graphics2D g)` - Draws pause screen (empty)

### CustomizeScreen.java
**Methods:**
- `CustomizeScreen(GamePanel gamePanel)` - Constructor
- `handleClick(int x, int y)` - Handles clicks
- `draw(Graphics2D g)` - Draws customize screen

### SettingsScreen.java
**Methods:**
- `SettingsScreen(GamePanel gamePanel)` - Constructor
- `handleClick(int x, int y)` - Handles clicks
- `draw(Graphics2D g)` - Draws settings screen

### HelpScreen.java
**Methods:**
- `HelpScreen(GamePanel gamePanel)` - Constructor
- `handleClick(int x, int y)` - Handles clicks
- `draw(Graphics2D g)` - Draws help screen

### ItemGalleryScreen.java
**Fields:**
- `gamePanel` (GamePanel)
- `itemRegistry` (ItemRegistry)
- `backBtn` (Rectangle)
- `selectedItem` (Item)
- `itemTypes` (String[]) - Array of item type names
- `typeButtons` (Rectangle[]) - Type selector buttons
- `selectedType` (int) - Currently selected item type

**Methods:**
- `ItemGalleryScreen(GamePanel gamePanel)` - Constructor
- `handleMouseScroll(int scrollDirection)` - Handles mouse scroll for tier cycling
- `cycleTier(int direction)` - Cycles item tier (1-5)
- `createItemWithTier(String itemName, int tier)` - Creates item instance with specific tier
- `draw(Graphics2D g)` - Draws item gallery screen
- `drawItemGrid(Graphics2D g)` - Draws item grid
- `getItemsForSelectedType()` - Returns items for selected type
- `drawItemDetails(Graphics2D g)` - Draws selected item details panel
- `getItemTypeName(Item item)` - Returns item type name
- `handleClick(int x, int y)` - Handles clicks

### GraphTestScreen.java
**Methods:**
- `GraphTestScreen(GamePanel gamePanel)` - Constructor
- `handleClick(int x, int y)` - Handles clicks
- `draw(Graphics2D g)` - Draws graph test screen
- `regenerateLevel()` - Regenerates dungeon level
- `setLevel(int level)` - Sets dungeon level (1-5)

### LoadoutScreen.java
**Fields:**
- `gamePanel` (GamePanel)
- `backBtn` (Rectangle) - Back button
- `weaponSlot, charmSlot1-3, powerSlot, summonSlot, consumableSlot` (Rectangle) - Loadout slots
- `unlockedWeapons` (Map<String, List<Integer>>) - Weapon name -> unlocked tiers
- `selectedWeapon` (String) - Currently selected weapon
- `selectedWeaponTier` (int) - Selected weapon tier (1-5)
- `selectedCharm, selectedPower, selectedSummon, selectedConsumable` (String) - Other selections
- `unlockedCharmSlots, unlockedConsumableSlots` (int) - Unlocked slot counts (meta-progression)
- `showUnlockedItemsDebug` (boolean) - Debug panel toggle (U key)
- `openPicker` (String) - Currently open picker type

**Methods:**
- `LoadoutScreen(GamePanel gamePanel)` - Constructor
- `toggleDebugPanel()` - Toggles debug panel visibility
- `handleClick(int x, int y)` - Handles slot clicks and picker selection
- `handleWeaponPickerClick(int x, int y)` - Handles weapon grid picker clicks
- `draw(Graphics2D g, int width, int height)` - Renders loadout screen
- `drawWeaponPickerGrid(Graphics2D g, int pickerX, int pickerY)` - Draws gallery-style weapon picker organized by tier
- `drawUnlockedItemsPanel(Graphics2D g, int x, int y)` - Draws debug unlocked items panel
- `getSelectedWeapon()` - Returns selected weapon name
- `getSelectedWeaponTier()` - Returns selected weapon tier
- `getSelectedCharm()` - Returns selected charm
- `getSelectedPower()` - Returns selected power
- `getSelectedSummon()` - Returns selected summon
- `getSelectedConsumable()` - Returns selected consumable
- `createWeapon(String weaponName, int tier)` - Creates weapon instance
- `loadIcon(String path)` - Caches and returns weapon icons

### ShopScreen.java
**Fields:**
- `gamePanel` (GamePanel)
- `backBtn` (Rectangle) - Back button

**Methods:**
- `ShopScreen(GamePanel gamePanel)` - Constructor
- `handleClick(int x, int y)` - Handles button clicks
- `draw(Graphics2D g, int screenWidth, int screenHeight)` - Renders shop screen

---

## util/

### Camera.java
**Fields:**
- `x, y` (int) - Camera position

**Methods:**
- `follow(int targetX, int targetY, int targetW, int targetH, int screenWidth, int screenHeight, int arenaWidth, int arenaHeight)` - Follows target with clamping

### KeyHandler.java (implements KeyListener)
**Fields:**
- `upPressed, downPressed, leftPressed, rightPressed` (boolean)
- `rPressed` (boolean) - Reload key
- `oPressed` (boolean) - Debug mode key

**Methods:**
- `keyPressed(KeyEvent e)` - Handles key press
- `keyReleased(KeyEvent e)` - Handles key release
- `keyTyped(KeyEvent e)` - Empty

### MouseHandler.java (implements MouseListener, MouseMotionListener, MouseWheelListener)
**Fields:**
- `mouseX, mouseY` (int) - Mouse position
- `leftPressed` (boolean) - Left mouse button held
- `leftClicked` (boolean) - Left mouse button clicked
- `scrollDirection` (int) - Scroll direction (-1 up, 1 down, 0 none)

**Methods:**
- `mouseMoved(MouseEvent e)` - Updates mouse position
- `mouseDragged(MouseEvent e)` - Updates mouse position during drag
- `mouseClicked(MouseEvent e)` - Empty
- `mousePressed(MouseEvent e)` - Handles mouse press
- `mouseReleased(MouseEvent e)` - Handles mouse release
- `mouseEntered(MouseEvent e)` - Empty
- `mouseExited(MouseEvent e)` - Empty
- `mouseWheelMoved(MouseWheelEvent e)` - Handles mouse wheel scroll
- `resetScroll()` - Resets scroll direction to 0

---

## world/

### Arena.java (Base class)
**Fields:**
- `width` (int)
- `height` (int)

**Methods:**
- `Arena(int width, int height)` - Constructor
- `getWidth()` - Returns width
- `getHeight()` - Returns height
- `draw(Graphics2D g, int screenWidth, int screenHeight, int cameraX, int cameraY)` - Draws arena

---

## world/arenas/

### ArenaTest.java (extends Arena)
**Methods:**
- `ArenaTest()` - Constructor with 2000x2000 arena

---

## world/dungeon/

### DungeonArenaScreen.java
**Fields:**
- `arena` (Arena) - Current dungeon arena
- `player` (Player) - Player entity
- `camera` (Camera) - Camera following player
- `hud` (HUD) - Heads-up display
- `dungeon` (Graph) - Procedurally generated dungeon graph
- `currentLevel` (int) - Current dungeon level (1-5)

**Methods:**
- `DungeonArenaScreen()` - Constructor, initializes dungeon
- `resetMouseClicks(MouseHandler mouse)` - Resets mouse clicks
- `regenerateLevel()` - Regenerates dungeon layout
- `setLevel(int level)` - Sets dungeon level difficulty
- `update(KeyHandler key, MouseHandler mouse, int screenWidth, int screenHeight)` - Updates game state
- `draw(Graphics2D g, int screenWidth, int screenHeight)` - Renders dungeon with fog of war
