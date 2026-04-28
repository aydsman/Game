# SummerGame Class Documentation

**Total Classes:** 58

**Total Lines of Code:** 4,323

## Table of Contents

- [Root Level](#root-level)
  - [Main.java](#mainjava)
- [combat/](#combat)
  - [Item.java](#itemjava-base-class)
  - [ItemRegistry.java](#itemregistryjava)
  - [Melee.java](#meleejava-extends-item)
  - [Ranged.java](#rangedjava-extends-item)
  - [Projectile.java](#projectilejava)
  - [charms/](#charms)
    - [Charm.java](#charmjava-extends-item)
    - [Charm1.java](#charm1java-extends-charm)
  - [summons/](#summons)
    - [Summon.java](#summonjava-extends-item)
    - [Summon1.java](#summon1java-extends-summon)
  - [powers/](#powers)
    - [Power.java](#powerjava-extends-item)
    - [Power1.java](#power1java-extends-power)
  - [consumables/](#consumables)
    - [Consumable.java](#consumablejava-extends-item)
    - [Consumable1.java](#consumable1java-extends-consumable)
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
- [util/](#util)
  - [Camera.java](#camerajava)
  - [KeyHandler.java](#keyhandlerjava-implements-keylistener)
  - [MouseHandler.java](#mousehandlerjava-implements-mouselistener-mousemotionlistener-mousewheellistener)
- [world/](#world)
  - [Arena.java](#arenajava-base-class)
- [world/arenas/](#worldarenas)
  - [ArenaTest.java](#arenatestjava-extends-arena)

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
- `charms` (List<Item>) - Charm items
- `summons` (List<Item>) - Summon items
- `powers` (List<Item>) - Power items
- `consumables` (List<Item>) - Consumable items
- `LOOT_TABLE` (double[][]) - Chest tier probabilities

**Methods:**
- `ItemRegistry()` - Constructor, registers all items
- `getAllItems()` - Returns all items
- `getWeapons()` - Returns weapons list
- `getCharms()` - Returns charms list
- `getSummons()` - Returns summons list
- `getPowers()` - Returns powers list
- `getConsumables()` - Returns consumables list
- `getItemsByTier(int tier)` - Returns items of specific tier
- `getRandomItem()` - Returns random item
- `getRandomItemForChest(int chestTier)` - Returns random item based on chest tier loot table

### Melee.java (extends Item)
**Fields:**
- `attackSpeed` (double) - attacks per second
- `damage` (int) - damage per attack
- `range` (double) - attack range in pixels
- `knockback` (double) - knockback force

**Methods:**
- `Melee()` - Default constructor with base stats
- `Melee(int tier)` - Constructor with tier and multipliers
- `attack()` - Attack logic (placeholder)

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

## combat/charms/

### Charm.java (extends Item)
- `Charm()` - Default constructor
- `Charm(int tier)` - Constructor with tier

### Charm1.java (extends Charm)
- `Charm1()` - Tier I charm
- `Charm1(int tier)` - Constructor with tier

---

## combat/summons/

### Summon.java (extends Item)
- `Summon()` - Default constructor
- `Summon(int tier)` - Constructor with tier

### Summon1.java (extends Summon)
- `Summon1()` - Tier I summon
- `Summon1(int tier)` - Constructor with tier

---

## combat/powers/

### Power.java (extends Item)
- `Power()` - Default constructor
- `Power(int tier)` - Constructor with tier

### Power1.java (extends Power)
- `Power1()` - Tier I power
- `Power1(int tier)` - Constructor with tier

---

## combat/consumables/

### Consumable.java (extends Item)
- `Consumable()` - Default constructor
- `Consumable(int tier)` - Constructor with tier

### Consumable1.java (extends Consumable)
- `Consumable1()` - Tier I consumable
- `Consumable1(int tier)` - Constructor with tier

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
- `hotbar` (ArrayList<Object>) - Player's hotbar items
- `xpMultiplier` (double) - XP multiplier
- `projectiles` (ArrayList<Projectile>) - Player's projectiles
- `lastShotTime` (long) - Time of last shot
- `gameStartTime` (long) - Game start time
- `canShoot` (boolean) - Can shoot flag

**Methods:**
- `Player(int x, int y)` - Constructor
- `playerSpawn()` - Initializes player stats and hotbar
- `resetMouseClicks(MouseHandler mouse)` - Resets mouse clicks
- `update(KeyHandler key, MouseHandler mouse, int arenaWidth, int arenaHeight)` - Updates player
- `aimBarrel(int mouseX, int mouseY)` - Aims barrel at mouse position
- `shoot()` - Shoots weapon
- `updateProjectiles()` - Updates all projectiles
- `checkProjectileCollisions(EnemyManager enemyManager)` - Checks projectile-enemy collisions
- `getProjectiles()` - Returns projectiles list
- `getHotbar()` - Returns hotbar list
- `equipHotbarSlot(int slot)` - Equips item from hotbar slot

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
- `SCREEN_WIDTH` (int) - 800
- `SCREEN_HEIGHT` (int) - 600
- `FPS` (int) - 60
- `keyHandler` (KeyHandler)
- `mouseHandler` (MouseHandler)
- `menuScreen` (MenuScreen)
- `gameScreen` (GameScreen)
- `pauseScreen` (PauseScreen)
- `customizeScreen` (CustomizeScreen)
- `settingsScreen` (SettingsScreen)
- `helpScreen` (HelpScreen)
- `showMenu, showGame, showPause, showCustomize, showSettings, showHelp` (boolean) - Screen states
- `gameThread` (Thread)

**Methods:**
- `GamePanel()` - Constructor, initializes screens and input handlers
- `switchScreen(String screen)` - Switches to specified screen
- `run()` - Game loop
- `update()` - Updates game screen
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
- `lootItems` (List<Item>) - Items in chest
- `selectedItem` (Item) - Currently selected item
- `isOpen` (boolean) - Chest open state

**Methods:**
- `ChestUI()` - Constructor
- `openChest(int chestTier)` - Opens chest with loot based on tier
- `draw(Graphics2D g, int screenWidth, int screenHeight)` - Draws chest UI
- `handleClick(int x, int y)` - Handles clicks
- `getSelectedItem()` - Returns selected item
- `closeChest()` - Closes chest

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
- `arena` (ArenaTest)
- `player` (Player)
- `camera` (Camera)
- `hud` (HUD)
- `enemyManager` (EnemyManager)
- `debugMode` (boolean)

**Methods:**
- `GameScreen()` - Constructor, spawns enemies
- `resetMouseClicks(MouseHandler mouse)` - Resets mouse clicks
- `update(KeyHandler key, MouseHandler mouse, int screenWidth, int screenHeight)` - Updates game state
- `draw(Graphics2D g, int screenWidth, int screenHeight)` - Draws game screen

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
