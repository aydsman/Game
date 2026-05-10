# Abyss

A Java-based 2D action game featuring dungeon crawling, arena combat, wave survival, and RPG progression systems.

## Project Stats

- **Total Classes:** 105+
- **Total Lines of Code:** 16719
- **Language:** Java 17+

## Table of Contents

- [Features](#features)
- [Controls](#controls)
- [Screens](#screens)
- [Project Structure](#project-structure)
- [Systems](#systems)
- [Running the Game](#running-the-game)
- [Documentation](#documentation)

## Features

### Combat System

#### Ranged Weapons

| Weapon | Type | Fire Mode | Description |
| --- | --- | --- | --- |
| Glock | Pistol | Semi-auto | Fast fire rate, low damage, high accuracy |
| AK-47 | Rifle | Automatic | Medium fire rate, medium damage, medium accuracy |
| Pump-Action | Shotgun | Semi-auto | Multiple pellets, high damage, low accuracy |
| P90 | SMG | Automatic | Very fast fire rate, low damage, medium accuracy |
| Sniper1 | Sniper | Semi-auto | Very slow fire rate, very high damage, very high accuracy |

#### Melee Weapons

All melee weapons feature:
- Swing animations with arc-based collision detection
- Knockback effects
- Configurable swing angle, speed, and attack delay
- Automatic or manual swing modes

| Weapon | Attack Speed | Damage | Range | Special |
| --- | --- | --- | --- | --- |
| Sword | Balanced | Medium | Medium | 90° swing arc |
| Hammer | Slow | High | Medium | High knockback |
| Dagger | Fast | Low | Low | Quick strikes |
| Mace | Slow | High | Medium | Heavy impact |
| Scythe | Medium | Medium | High | Wide arc |

#### Power System

5 elemental powers with 3 moves each (activated with keys 1-3):
- **Fire** - Offensive fire magic
- **Light** - Healing and protection
- **Earth** - Barriers and crushing attacks
- **Lightning** - Speed and chain attacks
- **Water** - Control and freezing

### Entity System

- **Player**: Leveling, XP progression, stats tracking (kills, damage, accuracy)
- **Enemies**: 5 variants with different behaviors and detection radii
- **Bosses**: Enhanced stats with unique behaviors
- **Mini-bosses**: Mid-tier challenging enemies

### Wave System

- 10 waves of increasing difficulty
- Configurable spawn intervals and enemy types per wave
- Grace period between waves
- Spawn protection radius around player

### Dungeon System

- 5 procedurally generated levels
- Room types: Spawn (White), Enemy (Red), Loot (Yellow), Mini-boss (Pink), Boss (Purple)
- Graph-based generation using MST and Delaunay triangulation
- Hallway connections with collision detection
- Room count scales with level (13-20 rooms)
- Loot room distribution: 1 guaranteed, 66% chance for 2nd, 33% chance for 3rd

### Inventory System

- **Hotbar**: 5 slots for weapons
- **Charms**: 3 slots for passive bonuses
- **Power**: 1 slot for elemental abilities
- **Summon**: 1 slot for summon abilities
- Drag-and-drop item management
- Chest looting with tier-based item generation

### UI System

- Item icons with aspect ratio preservation
- Tier cycling in Item Gallery (mouse wheel)
- Debug overlays (toggle with O key)
- Stats panel (toggle with U key)

### Character Customization System

Full character creator with layered sprite rendering and runtime recoloring. All assets are original pixel art at 64x64 or 128x128 per frame.

#### Body & Face

- **Body type**: Male or female (separate sprite sheets, shared head shape)
- **Skin color**: Palette of skin tones, each with 3 shades (highlight, base, shadow) recolored at runtime
- **Eye shape**: Multiple styles (round, narrow, wide etc), open and closed versions for blinking
- **Eye color**: Recolorable palette
- **Mouth**: Swappable expressions (neutral, smile, open, sad, surprised)
- **Nose**: Baked into body layer
- **Face markings**: Optional layer for scars, tattoos, freckles etc
- **Body markings**: Optional layer for body tattoos and scars, hidden naturally under clothing

#### Hair & Accessories

- **Hair styles**: Multiple styles, arrow-cycled in customize screen
- **Hair color**: Recolorable palette with 3 shades
- **Accessories**: Hat, glasses, etc — shared between body types since head shape is identical

#### Clothing

Clothing extends `Item` and has tiers just like weapons and charms, but is purely cosmetic — it never enters the hotbar or affects stats. Owning a clothing item and wearing it are two separate things:
- **Owned** — stored in your wardrobe collection, persists via save system
- **Equipped** — stored in `CharacterAppearance`, shown on character

| Slot | Default Unlocked | Source |
| --- | --- | --- |
| Top | T-shirt | Starting item |
| Bottom | Basic pants | Starting item |
| Accessory | None | Loot boxes, shop |

Additional clothing colors and entirely new clothing items are unlocked through loot boxes and the shop. Each clothing piece has its own tier (I-V) matching the existing item tier system.

#### Sprite Layer Stack (draw order)

```
1. Body base          (skin recolorable, nose baked in)
2. Body markings      (tattoos, scars — hidden under clothing)
3. Eyes               (shape + color recolorable, open/closed swap)
4. Mouth              (swappable expression)
5. Face markings      (scars, freckles, face tattoos)
6. Hair               (style + color recolorable)
7. Top                (shirt, hoodie etc — color recolorable)
8. Bottom             (pants, shorts etc — color recolorable)
9. Accessory          (hat, glasses etc)
```

#### Animation System

Each layer shares the same sprite sheet layout. Animations are organized per file (one PNG per animation per direction):

```
walk_down.png   walk_right.png   walk_up.png
idle_down.png   idle_right.png   idle_up.png
```

Left-facing is generated at runtime by horizontally flipping the right-facing sheet — no extra assets needed. Directional facing is determined by mouse angle relative to the player each frame.

Recoloring (skin, hair, clothing) is computed once when the player saves their appearance and cached as `BufferedImage`s — no per-frame performance cost.

#### Asset Folder Structure

```
assets/player/
├── male/
│   ├── body/
│   ├── tops/
│   └── bottoms/
├── female/
│   ├── body/
│   ├── tops/
│   └── bottoms/
└── shared/
    ├── eyes/
    ├── mouth/
    ├── hair/
    ├── face_markings/
    ├── body_markings/
    └── accessories/
```

#### New Player Flow

```
First launch → CustomizeScreen (character creator)
→ Select body type (male / female)
→ Customize skin, hair, eyes, mouth, markings
→ Start with t-shirt + basic pants
→ Confirm → MenuScreen
→ "Customize" button always available from menu to return
```

## Controls

### Movement & Combat

| Key | Action |
| --- | --- |
| WASD / Arrow Keys | Movement |
| Mouse | Aim |
| Left Click | Shoot / Melee attack |
| R | Reload |
| 1-5 | Use Power moves (when power equipped) |
| Space | Interact (chests) |

### Hotbar

| Input | Action |
| --- | --- |
| Mouse Wheel | Cycle hotbar slots |
| Tab | Open/Close inventory |

### Debug & System

| Key | Action |
| --- | --- |
| O | Toggle debug mode |
| U | Toggle stats panel |
| K | Kill all enemies (debug) |
| F | Toggle fullscreen |
| Esc / P | Pause |

### Dungeon

| Key | Action |
| --- | --- |
| L | Advance to next level |
| E | Interact |

## Screens

1. **MenuScreen** - Main menu with Arena, Dungeon, Loadout, Customize, Settings, Help buttons
2. **GameScreen** - Main gameplay with arena combat and wave management
3. **DungeonArenaScreen** - Procedurally generated dungeon crawling
4. **LoadoutScreen** - Configure starting equipment (weapon, charm, power, summon, consumable)
5. **ItemGalleryScreen** - Browse all items with tier cycling and detailed stats
6. **GraphTestScreen** - Visualize dungeon generation algorithm
7. **InventoryScreen** - Full inventory management
8. **PauseScreen** - Pause overlay
9. **SettingsScreen** - Game settings
10. **HelpScreen** - Controls and help
11. **CustomizeScreen** - Full character creator (body type, face, hair, clothing, markings)

## Project Structure

    src/
    ├── Main.java                    # Entry point
    ├── combat/                      # Combat and items
    │   ├── Item.java                # Base item class (tier, rarity, iconPath)
    │   ├── ItemRegistry.java        # Item registration and loot tables
    │   ├── Ranged.java              # Ranged weapon base (fireRate, damage, accuracy, reload)
    │   ├── Melee.java               # Melee weapon base (swing arc, knockback, attackSpeed)
    │   ├── Projectile.java          # Bullet/projectile physics
    │   ├── Inventory.java           # Player inventory (charms, powers, summons)
    │   ├── ranged/                  # Ranged weapon implementations
    │   │   ├── pistols/Pistol1.java (Glock)
    │   │   ├── rifles/Rifle1.java (AK-47)
    │   │   ├── shotguns/Shotgun1.java (Pump-Action)
    │   │   ├── smgs/SMG1.java (P90)
    │   │   └── snipers/Sniper1.java
    │   ├── melee/                   # Melee weapon implementations
    │   │   ├── swords/Sword1.java
    │   │   ├── hammers/Hammer1.java
    │   │   ├── daggers/Dagger1.java
    │   │   ├── maces/Mace1.java
    │   │   └── scythes/Scythe1.java
    │   ├── charms/                  # Charm items (passive bonuses)
    │   │   ├── Charm.java
    │   │   └── Charm1.java
    │   ├── powers/                  # Elemental powers
    │   │   ├── Power.java           # Base power class (moves array)
    │   │   ├── Move.java            # Individual move (name, slot, unlocked)
    │   │   ├── Fire.java
    │   │   ├── Light.java
    │   │   ├── Earth.java
    │   │   ├── Lightning.java
    │   │   └── Water.java
    │   ├── summons/                 # Summon items
    │   │   ├── Summon.java
    │   │   └── Summon1.java
    │   ├── consumables/             # Consumable items
    │   │   ├── Consumable.java
    │   │   ├── Consumable1.java      # Small health potion
    │   │   ├── Consumable2.java      # Medium health potion
    │   │   ├── Consumable3.java      # Large health potion
    │   │   ├── DamagePotion.java     # Small damage buff (10s)
    │   │   ├── DamagePotion2.java    # Medium damage buff (15s)
    │   │   └── DamagePotion3.java    # Large damage buff (20s)
    │   └── clothing/                # Clothing items (extends Item, cosmetic only)
    │       ├── ClothingItem.java     # Base clothing class (type, assetPath, defaultColor)
    │       ├── tops/
    │       │   └── TShirt.java       # Default starting top
    │       ├── bottoms/
    │       │   └── BasicPants.java   # Default starting bottom
    │       └── accessories/
    ├── entity/                      # Game entities
    │   ├── Entity.java              # Base entity (position, HP, damage, speed)
    │   ├── Player.java              # Player with hotbar, projectiles, stats
    │   ├── PlayerStats.java         # Track kills, damage, accuracy
    │   ├── Enemy.java               # Base enemy with AI and detection
    │   ├── EnemyManager.java        # Spawn and manage enemies
    │   ├── Boss.java                # Boss base class
    │   ├── enemies/                 # Enemy variants (Enemy1-5)
    │   ├── boss/Boss1.java
    │   └── miniboss/Miniboss1.java
    ├── player/                      # Player appearance
    │   └── CharacterAppearance.java # Stores all customization choices (saved)
    ├── progression/
    │   └── XP.java                  # XP and leveling system
    ├── ui/                          # User interface
    │   ├── Game.java                # JFrame setup
    │   ├── GamePanel.java           # Main game loop and screen switching
    │   ├── HUD.java                 # Heads-up display (HP, XP, hotbar)
    │   ├── InventoryUI.java         # Hotbar rendering
    │   ├── ChestUI.java             # Chest looting interface
    │   ├── InventoryScreen.java     # Full inventory screen
    │   └── screens/                 # Game screens
    │       ├── MenuScreen.java
    │       ├── GameScreen.java      # Main gameplay (982 lines)
    │       ├── DungeonArenaScreen.java
    │       ├── LoadoutScreen.java   # Starting loadout configuration
    │       ├── ItemGalleryScreen.java
    │       ├── GraphTestScreen.java
    │       ├── PauseScreen.java
    │       ├── SettingsScreen.java
    │       ├── HelpScreen.java
    │       └── CustomizeScreen.java # Full character creator
    ├── util/                        # Utilities
    │   ├── Camera.java              # Smooth camera following
    │   ├── KeyHandler.java          # Keyboard input (WASD, 1-5, R, O, etc.)
    │   └── MouseHandler.java        # Mouse input (aim, click, scroll)
    └── world/                       # World management
        ├── Arena.java               # Base arena class
        ├── DungeonArena.java        # Dungeon arena implementation
        ├── GameMap.java             # Map system
        ├── Tile.java                # Map tiles
        ├── arena/
        │   ├── Arena.java
        │   ├── WaveManager.java     # Wave spawning logic
        │   └── arenas/
        │       ├── ArenaTest.java
        │       ├── Arena1.java
        │       ├── Arena2.java
        │       └── Arena3.java
        ├── chests/
        │   ├── Chest.java           # Loot chests
        │   └── ArenaChest.java
        └── dungeon/                 # Dungeon generation
            ├── Room.java            # Room data (type, position, connections)
            ├── Hallway.java         # Hallway connection logic
            └── DungeonGenerator.java # Procedural generation (MST, Delaunay)

## Systems

### Weapon Tier System

Weapons have 5 tiers (I-V) with scaling stats:
- Tier I: Base stats
- Tier II: 1.2x multipliers
- Tier III: 1.5x multipliers
- Tier IV: 1.8x multipliers
- Tier V: 2.2x multipliers

### Loot Tables

Chests use probability-based loot tables:
- Higher tier chests = better item chances
- Items categorized: Weapons, Charms, Powers, Summons, Consumables, Clothing

### Clothing Tier System

Clothing shares the same 5-tier rarity system as other items. Tier affects visual quality and rarity of obtaining the item — not gameplay stats. Clothing is obtained via loot boxes and the shop.

### Power Move System

- Powers have 1-4 moves in assigned slots
- Moves displayed in bottom-right UI when power equipped
- Keys 1-3 activate corresponding moves
- All moves currently unlocked (no cooldowns yet)

### Collision Systems

- **Ranged**: Projectile-enemy collision with accuracy spread
- **Melee**: Arc-based collision within swing range and angle
- **Dungeon**: Room/hallway boundary collision with opening detection
- **Camera**: Smooth lerp (0.1 speed) following player

### Character Rendering System

- Player faces direction determined by mouse angle each frame
- 4 directions: down, left, right, up
- Left direction generated by flipping right-facing sprites horizontally at runtime
- Each body part is a separate `BufferedImage` layer drawn in order each frame
- Recoloring (skin, hair, clothing) computed once on appearance save, cached for gameplay
- Blink timer cycles eyes between open/closed states automatically

## Meta-Progression System

### Collection & Loadout

- **Persistent Collection**: Items unlocked permanently persist between runs
- **Starting Loadout**: Configure what you begin each run with
    * **Weapon Slot**: Choose 1 starting weapon from collection (with tier selection)
    * **Charm Slots**: Start with 1 slot (expandable to 3 via skill tree)
    * **Power Slot**: Choose 1 starting power
    * **Summon Slot**: Choose 1 starting summon
    * **Consumable Slots**: Optional starting consumables (unlockable)
- **Loadout UI**: Grid-based weapon picker with tier selection (T1-T5 color-coded)
- **Debug Panel**: Press U in Loadout screen to view unlocked items

### Wardrobe Collection

- Clothing items owned persist between runs via save system
- Separate from equipped appearance — owning an item and wearing it are independent
- Managed in `CustomizeScreen`, accessible from main menu at any time

### Item Sources

- Loot boxes (Common, Rare, Epic, Legendary)
- Mission/quest rewards
- Dungeon/arena completion
- Shop purchase with earned currency

### Meta-Upgrades (Skill Tree)

- +1 Starting Charm Slot (max 3)
- +1 Starting Consumable Slot
- Better loot box drop rates
- Starting currency bonuses

## Running the Game

### Requirements

- Java 17 or higher
- Minimum 4GB RAM
- Display: 1600x900 recommended

### Compile & Run

    # Compile all Java files
    javac -d out src/**/*.java

    # Run the game
    java -cp out Main

### IDE Setup (IntelliJ IDEA)

1. Open project folder
2. Mark `src/` as Sources Root
3. Set SDK to Java 17+
4. Run `Main.java`

## Documentation

- **[CLASS_DOCUMENTATION.md](https://github.com/aydsman/Game/blob/master/CLASS_DOCUMENTATION.md)** - Detailed class and method documentation
- **[NOTES.md](https://github.com/aydsman/Game/blob/master/NOTES.md)** - Game design notes, roadmap, and feature status
- **[STORY.md](https://github.com/aydsman/Game/blob/master/STORY.md)** - Game lore and narrative

---

*Last updated: May 2026*