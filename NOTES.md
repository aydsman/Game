# Game Design Notes

## Project Stats

- **Total Classes:** 96
- **Total Lines of Code:** 7,832

## Legend
- ✅ = Completed
- ❌ = Not started
- ✏️ = Work in progress

## Vision
Progression-based action game with open world exploration, dungeon crawling, tower defense, and arena combat.

---

## Hub World (Starting Point)

### Layout
- Central spawn area with safe zone
- NPCs for interactions:
  - Shop (buy items/weapons)
  - Quest giver
  - Skill tree trainer
  - Blacksmith (weapon upgrades)
- Portals/doors to game modes:
  - Dungeon Portal
  - Tower Defense Gate
  - Arena Entrance
  - Future: Map exits to other regions

### Hub Features
- ✅ Storage chest for items with drag-and-drop swapping between inventory and chests
- ❌ Charm crafting station
- ❌ Leaderboard/stat display

---

## Progression System

### Leveling
- ✅ XP from killing enemies, completing dungeons/arenas
- ❌ Level up grants skill points
- ❌ Stat points to allocate (HP, damage, speed, etc.)

### Gear Progression
- ✅ Weapons drop from enemies/chests
- ✅ Rarity tiers: Tier I → Tier II → Tier III → Tier IV → Tier V
- ✅ Each tier has better base stats
- ❌ Weapons can be upgraded at blacksmith
- ✅ Item class hierarchy: Item (base) → Ranged, Melee, Charm, Summon, Power, Consumable
- ✅ ItemRegistry with loot tables for chest generation
- ✅ Item Gallery screen to view all items by type and tier

### Skill Tree Structure
Three main branches:

**Combat:**
- ❌ Damage
- ❌ Fire rate
- ❌ Reload speed
- ❌ Crit chance

**Survival:**
- ❌ HP
- ❌ Defense
- ❌ Health regen
- ❌ Dodge

**Utility:**
- ❌ Movement speed
- ❌ Loot chance
- ❌ Charm slots

- ❌ Unlock nodes sequentially
- ❌ Can respec at hub

### Perk System
- ❌ Choose 1 perk every 5 levels
- ❌ Perks are unique bonuses (e.g., "Headshots deal 2x damage")
- ❌ Can swap perks at hub for cost

---

## Dungeon System

### Level Structure
- 5 levels total
- Level 1: 13-16 rooms (excluding spawn)
- Level 2: 14-17 rooms
- Level 3: 15-18 rooms
- Level 4: 15-18 rooms
- Level 5: 16-20 rooms (includes boss room)

### Room Types & Colors
- **Spawn room**: White (doesn't count toward room total)
- **Enemy room**: Red
- **Loot room**: Yellow
- **Mini-boss room**: Pink (33% chance in levels 2-4)
- **Boss room**: Purple (level 5 only)

### Loot Room Distribution
- 1 loot room guaranteed per level
- 66% chance for 2nd loot room
- 33% chance for 3rd loot room
- Higher chance at dead ends, but can appear elsewhere (not too close to spawn)

### Generation Algorithm
- Graph-based approach using MST and Delaunay triangulation
- Room connections from player spawn:
  - Always at least 1 connection
  - 75% chance for 2nd connection
  - 50% chance for 3rd connection
  - 25% chance for 4th connection (max)
- Direction-based branching (N, S, E, W) - not just straight lines
- Dead ends scattered throughout
- Occasional loops for alternate paths
- Boss room placed furthest from spawn

### Shop System
- Separate shop screen appears after beating each level
- Between levels, player visits shop before proceeding

### GraphTestScreen Visualization
- Hallways drawn as lines between rooms
- Room ID displayed at top right for debugging
- Miniature version of arena for testing generation

### Class Structure
```
world/
├── DungeonArena.java (extends Arena)
└── dungeon/
    ├── Room.java
    ├── DungeonGenerator.java
    └── Hallway.java
```

### Dungeon Progression
- ✅ Difficulty tiers (1-5) - Press L to advance levels
- ✅ Room generation with scaling (10x for gameplay, 1x for graph test)
- ✅ Hallway connections with proper collision detection
- ✅ Room types with colors (Spawn=White, Enemy=Red, Loot=Yellow, Mini-boss=Pink, Boss=Purple)
- ✅ Smooth camera transitions between rooms/hallways
- ✅ Player collision detection (can only exit through hallway openings)
- ✅ Level indicator display (shows "Level X/5")
- ❌ Higher tiers = better loot, harder enemies
- ❌ Complete dungeon to get loot chest
- ❌ Boss at end drops guaranteed good item

---

## Tower Defense Integration

### Gameplay
- ❌ Defend a central point (crystal/nexus)
- ❌ Waves of enemies approach from set paths
- ❌ Player fights + can place towers
- ❌ Towers cost resources (gold from kills)

### Tower Types
- ❌ Arrow tower (fast, low damage)
- ❌ Cannon tower (slow, AoE damage)
- ❌ Ice tower (slows enemies)
- ❌ Laser tower (high damage, single target)

### Progression
- ❌ Survive X waves to win
- ❌ Better rewards for higher difficulty
- ❌ Tower upgrades between waves

---

## Arena System

### Arena Modes
- ❌ Wave Survival (infinite waves, see how far you go)
- ❌ Time Attack (kill X enemies as fast as possible)
- ❌ Boss Rush (fight bosses back-to-back)
- ❌ Challenge Mode (special modifiers like "enemies have 2x HP")

### Arena Rewards
- ❌ Currency based on performance
- ❌ Unique arena-only weapons
- ❌ Leaderboard rankings

---

## Save/Load System

### What to Save
- ❌ Player level, XP, stat allocation
- ❌ Skill tree unlocks
- ❌ Equipped gear
- ❌ Inventory items
- ❌ Currency
- ❌ Hub progress (unlocked areas)
- ❌ Best arena scores

### File Format
- ❌ JSON for easy editing/debugging
- ❌ Auto-save after major events
- ❌ Manual save slots (3 slots)

---

## Development Priority Order

1. **Hub World** - Basic layout, portals, NPCs
2. **Progression** - Leveling, XP, stat allocation
3. **Skill Tree** - UI and node system
4. **Dungeon Generation** - Basic room algorithm
5. **Arena Modes** - Wave survival first
6. **Tower Defense** - Basic towers + waves
7. **Perk System** - After core systems work
8. **Save/Load** - Once progression is solid

---

## Additional Features (Future)

### Items & Loot
- ❌ Chests with random loot (weapons, ammo, health, combat.charms)
- ❌ Different chest rarities (common, rare, legendary)
- ❌ Keys required to open certain chests
- ❌ Armor pieces (helmet, chestplate, legs) with defense stats
- ❌ Charms that provide passive bonuses (speed, damage, health regen)
- ❌ Consumable items (health potions, ammo boxes, temporary buffs)
- ❌ Currency system (coins/gold dropped by enemies)

### Combat & Weapons
- ✅ Melee weapons (swords, hammers, daggers, maces, scythes) with swing animations, arc collision, and knockback
- ❌ Weapon attachments (scopes, silencers, extended magazines)
- ❌ Critical hit system with visual effects
- ❌ Status effects (burn, poison, slow, stun)
- ❌ Grenades/throwables with different effects
- ❌ Special abilities/ultimates with cooldowns

### Enemies & Progression
- ✅ Boss enemies (Boss1) and Mini-bosses (Miniboss1) - basic implementation
- ✅ Enemy types (Enemy1-5 variants exist)
- ❌ Enemy factions that fight each other
- ❌ Enemy scaling based on player level
- ❌ Mini-bosses that drop special loot

### World & Exploration
- ✅ Arena system (basic ArenaTest arena exists)
- ❌ Multiple biomes/arenas with different themes
- ❌ Environmental hazards (spikes, lava, traps)
- ❌ Destructible environment elements
- ❌ Secret areas hidden behind walls
- ❌ Teleporters/fast travel between areas
- ❌ Day/night cycle affecting enemy behavior

### Systems & Features
- ✅ Camera system (follows player)
- ✅ Input handling (keyboard, mouse)
- ✅ UI screens (Menu, Game, Pause, Settings, Help, Customize)
- ❌ Achievements/trophies
- ❌ Difficulty settings
- ❌ Co-op multiplayer
- ❌ Crafting system to combine items
