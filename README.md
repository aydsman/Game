# Abyss

A Java-based 2D action game featuring dungeon crawling, arena combat, and progression systems.

## Project Stats

- **Total Classes:** 96
- **Total Lines of Code:** 7,832

## Features

### Combat System
- Multiple weapon types (pistols, rifles, shotguns, SMGs, snipers)
- Melee weapons (swords, hammers, daggers, maces, scythes)
- Weapon rarity tiers (I-V)
- Fire rate, damage, accuracy, and reload mechanics
- Projectile-based combat
- Accuracy angle spread for ranged weapons
- Shotgun pellet system (multiple pellets per shot)

### Entity System
- Player with leveling and XP progression
- Enemy variants with different behaviors
- Boss enemies with enhanced stats
- Enemy manager for spawning and tracking

### Dungeon System
- Procedurally generated dungeons with 5 levels
- Room types: Spawn, Enemy, Loot, Mini-boss, Boss
- Hallway connections with collision detection
- Smooth camera transitions
- Room coloring by type
- Graph test visualization for debugging

### UI System
- Main menu with navigation
- Game screen with HUD (HP, XP, hotbar)
- Pause, Settings, Help, and Customize screens
- Inventory system with hotbar slots
- Item Gallery screen with tier cycling (scroll wheel)
- Chest UI for loot display

### Progression
- XP-based leveling system
- Stat multipliers (damage, speed, HP)
- Weapon tiers with scaling stats
- Item registry with loot tables for chests
- Multiple item types: Weapons, Charms, Summons, Powers, Consumables

## Controls

- **WASD / Arrow Keys:** Movement
- **Mouse:** Aim
- **Left Click:** Shoot
- **R:** Reload
- **1-5:** Switch hotbar slot
- **Mouse Wheel:** Cycle hotbar
- **L (Dungeon):** Advance to next level
- **O:** Toggle debug mode

## Project Structure

```
src/
├── Main.java              # Entry point
├── combat/                # Combat items
│   ├── Item.java          # Base class
│   ├── ItemRegistry.java  # Item registry with loot tables
│   ├── Ranged.java        # Ranged weapons
│   ├── Melee.java         # Melee weapons
│   ├── Projectile.java    # Projectiles
│   ├── ranged/            # Ranged weapon types
│   │   ├── pistols/
│   │   ├── rifles/
│   │   ├── shotguns/
│   │   ├── smgs/
│   │   └── snipers/
│   ├── melee/             # Melee weapon types
│   │   ├── swords/
│   │   ├── hammers/
│   │   ├── daggers/
│   │   ├── maces/
│   │   └── scythes/
│   ├── combat.charms/            # Charm items
│   ├── summons/           # Summon items
│   ├── combat.powers/            # Power items
│   └── combat.consumables/       # Consumable items
├── entity/                # Game entities
│   ├── Entity.java        # Base class
│   ├── Player.java
│   ├── Enemy.java
│   ├── Boss.java
│   ├── EnemyManager.java
│   ├── enemies/           # Enemy variants
│   └── boss/              # Boss variants
├── inventory/             # Inventory system
│   └── Inventory.java
├── progression/           # Progression system
│   └── XP.java
├── ui/                    # User interface
│   ├── Game.java
│   ├── GamePanel.java
│   ├── HUD.java
│   ├── InventoryUI.java
│   ├── ChestUI.java
│   └── screens/           # Game screens
│       ├── MenuScreen.java
│       ├── GameScreen.java
│       ├── PauseScreen.java
│       ├── CustomizeScreen.java
│       ├── SettingsScreen.java
│       ├── HelpScreen.java
│       ├── ItemGalleryScreen.java
│       └── GraphTestScreen.java
├── util/                  # Utilities
│   ├── Camera.java
│   ├── KeyHandler.java
│   └── MouseHandler.java
└── world/                 # World management
    ├── Arena.java         # Base arena class
    ├── arenas/            # Arena implementations
    │   └── ArenaTest.java
    ├── DungeonArena.java  # Dungeon arena
    └── dungeon/           # Dungeon generation
        ├── Room.java
        ├── DungeonGenerator.java
        └── Hallway.java
```

## Running the Game

1. Ensure you have Java 17 or higher installed
2. Compile the project: `javac src/**/*.java`
3. Run: `java Main`

## Development

### Dungeon Generation
- Graph-based approach using MST and Delaunay triangulation
- Room connections from spawn with probability-based branching
- Dead ends and loops for varied paths
- Scaling factor for gameplay (10x) vs graph test (1x)

### Collision Detection
- Player constrained to rooms/hallways
- Can only exit through hallway openings
- 75% overlap requirement for transitions
- Independent axis constraining for smooth movement

### Camera System
- Smooth camera transitions with lerp (0.1 speed)
- Centers on rooms in dungeons
- Follows player in arena
- Target-based positioning for accurate aiming

## Future Plans

- Hub world with NPCs and portals
- Tower defense mode
- Skill tree system
- Save/load system
- Co-op multiplayer
- More weapon types and attachments
- Environmental hazards and secrets

## Documentation

- [CLASS_DOCUMENTATION.md](CLASS_DOCUMENTATION.md) - Detailed class documentation
- [NOTES.md](NOTES.md) - Game design notes and development roadmap
