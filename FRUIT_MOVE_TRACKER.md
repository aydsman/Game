# Fruit Move Tracker

This file tracks finalized fruit move plans.  
Update this file every time a fruit is finished.

## Important Standards

- Organize fruits by tier.
- Keep move names final once accepted.
- Each move entry should include purpose and intended combat role.
- Each fruit should include balance guardrails to prevent overpowered kits.
- Keep tier strength progression obvious: Tier 3 > Tier 2, Tier 4 > Tier 3, Tier 5 > Tier 4.
- Reuse shared move behavior templates when powers have similar mechanics, then tune stats/assets per power.

---

## Tier 2 Fruits

### Sand Fruit (Crocodile-inspired) - Status: Finalized for now

**Identity**
- Mid-range control and attrition.
- Wins by forcing enemies into bad positions, then finishing them.

**Moves**
- **Move 1 - Sand Slice:** Quick sand slash used for poke and combo starts.
- **Move 2 - Desert Prison:** Control zone that traps/slows enemies to create setup windows.
- **Move 3 - Sand Coffin:** High-impact finisher that is strongest after control setup.

**Balance Guardrails**
- Move 2 should pressure movement, not hard-lock players for too long.
- Move 3 should have clear commitment (startup or recovery) so misses are punishable.
- Sand Fruit should excel at control, not have top-tier burst and top-tier mobility at the same time.

**Combo Flow**
- Use Move 1 for pressure -> Move 2 to hold space -> Move 3 to cash out damage.

---

### String Fruit (Doflamingo-inspired) - Status: Finalized for now

**Identity**
- Mid-range precision controller focused on poke, control, and execution.
- Wins by forcing movement mistakes, then converting into a lethal finisher.

**Moves**
- **Move 1 - Overheat:** Forward string thrust that damages enemies in a thin line in front of the player. Duration: `220ms`, Cooldown: `700ms`, Damage: `30`.
- **Move 2 - Parasite:** Latches up to 6 thin strings onto unique enemies within a `350px` radius and stuns each one for `5000ms`. If fewer enemies are in range, fires that many strings only. Never targets the same enemy twice. Duration: `800ms`, Cooldown: `12000ms`, Damage per target: `18`.
- **Move 3 - God Thread:** Fires 16 strings at the nearest enemy within a `500px` radius, one string every `100ms`. Each hit also applies a `500ms` stun. Re-picks the nearest target on each shot. Visual matches Parasite (purple strings + target dots). Duration: `2000ms`, Cooldown: `12000ms`, Damage per string: `24` (max `384`).

**Balance Guardrails**
- Parasite is a high-impact disable (5 sec stun) so the cooldown is intentionally long.
- God Thread requires proximity (150px), so missing range positioning punishes bad usage.
- String Fruit should lean into precision control and execution, not high mobility.

**Combo Flow**
- Use Move 1 to poke -> Move 2 to force position/control -> Move 3 to finish.

---

## Tier 3 Fruits

### Rubber Fruit (Pre-Gear 4 Luffy-inspired) - Status: Finalized for now

**Identity**
- Fast close-range pressure fruit with strong tempo control and a heavy finisher.
- Plays around speed, repositioning, and explosive punch conversion.

**Moves**
- **Move 1 - Jet Gatling:** Punch barrage with a forward rectangle hitbox; damages anything in front of the player every 130ms. Duration: `3000ms`, Cooldown: `7000ms`.
- **Move 2 - Shave:** Instant teleport-style dash to the mouse position. Duration: `250ms`, Cooldown: `5000ms`.
- **Move 3 - Elephant Gun:** Wind-up phase (fist grows) followed by a single forward punch that damages enemies in a radius. Duration: `1400ms`, Cooldown: `9000ms`.

**Balance Guardrails**
- Move 2 should give mobility value, not long invulnerability.
- Move 3 should be high commitment with punishable recovery on miss.
- Rubber Fruit should out-tempo Tier 2 fruits while keeping clear counterplay.

**Tier Strength Note**
- Stronger than Tier 2 by having higher pace control and better engage/disengage options.
- All rubber moves currently use temporary fallback visuals until final assets are added.

**Combo Flow**
- Move 1 pressure -> Move 2 reposition or chase -> Move 3 finish.

---

## Tier 5 Fruits

### Nika Fruit (Gear 5-inspired) - Status: Finalized for now

**Identity**
- High-mobility brawler/trickster with cartoon physics and explosive finish potential.
- Every Nika move temporarily puts the user into Gear 5 (white attire) until the move ends.

**Moves**
- **Move 1 - Dawn Gatling:** Fast elastic punch barrage for core pressure and combo starts.
- **Move 2 - Toon Dash:** Cartoon run mobility tool for escape and repositioning.
- **Move 3 - Rubber Realm:** Ground punch that rubberizes an area and slows enemies in radius.
- **Move 4 - Thunder Spear:** Holds and launches a lightning bolt as a high-impact ranged strike.
- **Move 5 - Bajrang Gun:** Massive capstone finisher inspired by the Wano final blow.

**Balance Guardrails**
- Move 5 should be highest commitment and highest reward.
- Move 4 needs visible wind-up so opponents can react.
- Move 2 should provide safety but not full invincibility for too long.
- The kit should stay combo-driven, not rely on one-button wins.

**Combo Flow**
- Move 1 for pressure -> Move 2 reposition -> Move 3 slow setup -> Move 4 confirm pressure -> Move 5 finish.
