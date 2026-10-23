# Game Design Notes

## Project Stats

- **Total type declarations:** 260 (classes, interfaces, enums, and nested types)
- **Java source files:** 242
- **Total lines of code:** 23,850 (all lines in `src/**/*.java`)

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
- ✅ NPCs and objects open real screens (loadout, shop, crafting, skill tree, locker,
  quest board, profile, arena portal)
- ✅ Crafting station (Artificer) for merging items into higher tiers
- ❌ Leaderboard/stat display

---

## Progression System

### Leveling
- ✅ XP from killing enemies, completing dungeons/arenas
- ✅ Level up grants skill points (more at levels ending in 5 and 0)
- ❌ Stat points to allocate (HP, damage, speed, etc.)

### Gear Progression
- ✅ Weapons drop from enemies/chests
- ✅ Rarity tiers: Tier I → Tier II → Tier III → Tier IV → Tier V
- ✅ Each tier has better base stats
- ✏️ Weapon upgrades happen at the Artificer's crafting station (tier merges), not a
  dedicated blacksmith screen
- ✅ Item class hierarchy: Item (base) → Ranged, Melee, Charm, Summon, Power, Consumable
- ✅ ItemRegistry with loot tables for chest generation
- ✅ Item Gallery screen to view all items by type and tier

### Skill Tree Structure
Three pillars, five tiers each. Unlocked nodes feed `skilltree.SkillEffects`, which is
read by the player, weapons, loot boxes and reward payouts.

**Combat:**
- ✅ Damage
- ✅ Reload speed
- ✅ Crit chance
- ❌ Fire rate

**Vitality:**
- ✅ Max HP
- ✅ Movement speed
- ✅ Healing received
- ✏️ Defense / dodge exist in combat (worn armor, Shift dodge); not yet skill-tree nodes

**Abundance:**
- ✅ Cash gain
- ✅ Gem gain
- ✅ Loot quality (biases loot box tier rolls)
- ❌ Charm slots

- ✅ Unlock nodes sequentially (parents required)
- ✅ Full respec from the Skill Tree screen
- ✅ Reachable from the hub Skill Tree object and the main menu

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
- ✅ Player level and XP
- ✅ Skill points and skill tree unlocks
- ✅ Equipped gear / loadout selections
- ✅ Inventory items and stacks
- ✅ Currency (cash and gems; gold is session-only)
- ✅ Unlocked arenas and last completed arena
- ✅ Claimed quest/reward ids
- ❌ Best arena scores

### File Format
- ✅ JSON at `data/player_save.json`
- ✅ Auto-save plus stat sync when returning to the menu
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

## Meta-Progression System (Collection & Loadout)

### Overview
Outside of game modes, players build a permanent collection of items and configure starting loadouts for runs.

### Collection System
- **Persistent Inventory**: Separate from run-based hotbar - items persist between runs
- **Item Sources**:
  - Loot boxes (Common, Rare, Epic, Legendary rarities)
  - Mission/quest rewards
  - Shop purchase
  - Dungeon/arena completion rewards
- **Categories**: Weapons, Charms, Powers, Summons, Consumables
- **Save System**: JSON-based collection persistence

### Starting Loadout Configuration
Pre-run screen where players choose what they start with:

| Slot | Default | Unlock Status | Max |
|------|---------|---------------|-----|
| Weapon | Pistol1 | Always 1 slot | 1 |
| Charms | None | Starts at 1, expandable to 3 | 3 |
| Power | None | Always 1 slot | 1 |
| Summon | None | Always 1 slot | 1 |
| Consumables | None | Starts at 0, expandable | 2 |

### Skill Tree Expansion (Meta-Upgrades)
- **+1 Starting Charm Slot**: Unlock 2nd then 3rd charm slot permanently
- **+1 Starting Consumable Slot**: Start runs with 1 consumable
- **Better Loot Box Odds**: Increased chance for higher tier drops
- **Starting Currency**: Begin runs with bonus gold

### Loot Box System
- Earn from: beating dungeons, quests, daily login, achievements
- Contains random items based on box rarity
- Duplicate items can be converted to currency or crafting materials

### Shop System
- **Direct Purchase**: Buy specific items with earned currency
- **Loot Boxes**: Cheaper but random
- **Daily Deals**: Discounted items rotating daily

### UI Screens Needed
- **Collection Screen**: Browse all unlocked items by category
- **Loadout Screen**: Configure starting equipment (no moving player)
- **Shop Screen**: Purchase items and loot boxes
- **Loot Box Opening**: Animation for revealing rewards

---

## Additional Features (Future)

### Items & Loot
- ✅ Chests with random loot via ItemRegistry loot tables
- ✅ Loot boxes with tier/type probability rolls (see LootboxCatalog)
- ❌ Keys required to open certain chests
- ❌ Armor pieces (helmet, chestplate, legs) with defense stats
- ✅ Charms that provide passive bonuses (speed, damage, max HP)
- ✅ Consumable items (health, damage and speed potions with timed buffs)
- ✅ Currency system (gold from enemies; cash and gems persist)

### Combat & Weapons
- ✅ Melee weapons (swords, hammers, daggers, maces, scythes) with swing animations, arc collision, and knockback
- ✅ Dodge (Shift) with i-frames; hurt i-frames after a landed hit
- ✅ Crit visuals, hitstop, and per-weapon melee knockback
- ✅ Dagger 3-hit combo; hammer on-hit stun (bosses resist); shotgun damage drop-off
- ✅ Enemy roles (kiter, closer, slammer) and a telegraphed slam on slammers / bosses
- ✅ Summons tick in combat; worn clothing grants light damage reduction
- ✅ Perks unlock at account levels 5 / 10 / 15
- ❌ Weapon attachments (scopes, silencers, extended magazines)
- ✅ Critical hits from the skill tree (2x damage) with distinct popups
- ✏️ Status effects exist on entities (stun, slow, burn); not exposed on all weapons
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
- ✅ Borderless fullscreen launch with scaled 1600x900 rendering
- ✅ Quest objectives tracked from save stats, gating reward claims
- ❌ Achievements/trophies
- ❌ Difficulty settings
- ❌ Co-op multiplayer
- ✅ Crafting system to combine items (tier merges at the Artificer)
