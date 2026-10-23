# Power Move Tracker

This file tracks finalized move plans for regular (non-fruit) powers.  
Update this file every time a power is finished.

For fruit powers, see [FRUIT_MOVE_TRACKER.md](FRUIT_MOVE_TRACKER.md).

## Important Standards

- Organize powers by tier.
- Keep move names final once accepted.
- Each move entry should include purpose and intended combat role.
- Each power should include balance guardrails to prevent overpowered kits.
- Keep tier strength progression obvious: Tier 2 > Tier 1, Tier 3 > Tier 2, Tier 4 > Tier 3, Tier 5 > Tier 4.
- Reuse shared move behavior templates when powers have similar mechanics, then tune stats/assets per power.

---

## Tier 1 Powers

### Earth - Status: Finalized for now

**Identity**
- Close-to-mid range bruiser / zone controller.
- Wins by holding space around cast points, not by chasing.
- Tier 1 baseline kit: simple, planted, and punishable when finishers miss.

**Moves**
- **Move 1 - Rock Barrage:** Fires 3 rocks toward the mouse, one every `80ms` along the same aim angle. Each rock impacts at `190px`, `205px`, and `220px` forward and deals `10` damage in a `35px` radius. Duration: `280ms`, Cooldown: `900ms`, Max damage: `30`.
- **Move 2 - Spike Ring:** On cast, anchors a spike ring at the player's position when the move was clicked. The ring stays fixed at that world location (does not follow the player). Enemies within `120px` of the anchor take `18` damage once and are slowed by `30%` for `2500ms`. Duration: `2500ms` (ring visual persists at anchor), Cooldown: `7500ms`.
- **Move 3 - Boulder Barrage:** Launches 2 large boulders toward the mouse direction, `350ms` apart. Each travels up to `320px` and detonates on the first enemy hit or at max range for `38` damage in a `60px` radius. Duration: `900ms`, Cooldown: `9500ms`.

**Balance Guardrails**
- No mobility — Earth should feel planted and defensive.
- Spike Ring is anchored at cast position, so walking away leaves the zone behind.
- Spike Ring slows but does not stun at Tier 1.
- Boulder Barrage is aim-dependent with long cooldown; missing both boulders should hurt.

**Combo Flow**
- Rock Barrage poke -> Spike Ring hold space at cast point -> Boulder Barrage finish on slowed enemies.

---

### Fire - Status: Finalized for now

**Identity**
- Mid-range burner / pressure DPS.
- Wins by landing skillshots, dashing to reposition, and cashing out with a heavy fireball.
- All moves apply fire tick damage after their initial hit effect.

**Moves**
- **Move 1 - Flame Burst:** Fires 6 fire bullets toward the mouse over `400ms` (one every `60ms`) with a `12°` spread. Each bullet travels up to `400px`, deals `8` damage on impact in a `22px` radius, and applies fire tick. Duration: `400ms`, Cooldown: `1400ms`.
- **Move 2 - Trail of Fire:** Dashes `130px` toward the mouse and leaves 5 fire patches along the trail behind the dash path. Each patch is `40px` radius, lasts `2500ms` at fixed world positions, and applies fire tick to enemies inside every `500ms`. Duration: `2500ms`, Cooldown: `6500ms`.
- **Move 3 - Grand Fireball:** `350ms` wind-up, then launches one large fireball up to `420px`. Detonates for `46` damage in an `80px` radius and applies fire tick. Duration: `900ms`, Cooldown: `9000ms`.

**Fire Tick (all moves)**
- `4` damage every `500ms` for `2000ms` after a target is burned.

**Balance Guardrails**
- Flame Burst rewards aim with spread salvo pressure, not single-target burst.
- Trail of Fire gives mobility but patches stay behind on the ground — counterplay by leaving the fire.
- Grand Fireball has visible wind-up and long cooldown; missing it should hurt.
- Fire should out-damage Earth in raw burn but lack Earth's hard slow/control.

**Combo Flow**
- Flame Burst poke + burn -> Trail of Fire dash through enemies leaving fire -> Grand Fireball finish on burning targets.

---

### Light - Status: Not started

---

### Lightning - Status: Not started

---

### Shadow - Status: Not started

---

### Water - Status: Not started

---

## Tier 2 Powers

### Crystal - Status: Not started

---

### Limitless - Status: Not started

---

### Magma - Status: Not started

---

### Metal - Status: Not started

---

### Overgrowth - Status: Not started

---

### Storm - Status: Not started

---

### Vessel - Status: Not started

---

## Tier 3 Powers

### Eclipse - Status: Not started

---

### Haki - Status: Not started

---

### Plasma - Status: Not started

---

### Sharingan - Status: Not started

---

## Tier 4 Powers

### EarthV2 - Status: Not started

---

### FireV2 - Status: Not started

---

### LightningV2 - Status: Not started

---

### MagmaV2 - Status: Not started

---

### Nightbloom - Status: Not started

---

### Rinnegan - Status: Not started

---

### WaterV2 - Status: Not started

---

## Tier 5 Powers

### Cataclysm - Status: Not started

---

### Infinity - Status: Not started

---

### King of Curses - Status: Not started

---

### Rinne Sharingan - Status: Not started

---

## Finished Power Template

When a power is finalized, replace its section with this structure:

### Power Name - Status: Finalized for now

**Identity**
- 

**Moves**
- **Move 1 - Name:** 
- **Move 2 - Name:** 
- **Move 3 - Name:** 

**Balance Guardrails**
- 

**Combo Flow**
- 

**Tier Strength Note** _(optional, for upgrades and fusion lines)_
- 
