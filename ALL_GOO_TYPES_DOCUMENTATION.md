# Grey Goo Mod - Complete Goo Block Documentation

This document provides detailed behavior documentation for ALL goo block types in the Grey Goo mod.

---

## Table of Contents

### Activation-Required Goo (require right-click to start spreading)
1. [Bubble Goo (White-Green)](#bubble-goo-white-greengoo)
2. [Freezer (Green-Red)](#freezer-green-redgoo)
3. [Orange-Red Goo](#orange-red-goo-orangeredgoo)
4. [Orange-Purple Goo](#orange-purple-goo-orangepurplegoo)
5. [Orange-White Goo](#orange-white-goo-orangewhitegoo)
6. [Rapid Eater (Purple-Red)](#rapid-eater-purpleredgoo)
7. [Rapid Miner (Brown-Red)](#rapid-miner-brown-redgoo)
8. [Rapid Water Eater](#rapid-water-eater)

### Autonomous Goo (spread on random tick without activation)
9. [Grey Goo](#grey-goo-greygoo)
10. [Purple Goo](#purple-goo-purplegoo)
11. [Red Goo (Cleaner)](#red-goo-cleaner-redgoo)
12. [Orange Goo (Wall)](#orange-goo-wall-orangegoo)
13. [Blue Goo (Water Eater)](#blue-goo-water-eater-bluegoo)
14. [White Goo (Air Eater)](#white-goo-air-eater-whitegoo)
15. [Miner Goo](#miner-goo-minergoo)
16. [Yellow Goo (Gravity)](#yellow-goo-gravity-yellow-goo)

### Destroyer Chain
17. [Cancer (Tumor)](#cancer-tumor)
18. [Black Goo (Darkness)](#black-goo-darkness)
19. [Cancer 2 (Plague)](#cancer-2-plague)
20. [The Great Destroyer](#the-great-destroyer-thegreatdestroyer)
21. [TGD Inert](#tgd-inert-inert-destroyer)

### Utility / Non-Spreading
22. [Green Goo (Inert)](#green-goo-inert-greengoo)
23. [Rainbow Goo (Restorer)](#rainbow-goo-restorer-rainbowgoo)
24. [Elevator Goo](#elevator-goo-elevatorgoo)
25. [Substrate](#substrate)
26. [Data Storage](#data-storage)

---

## Common Mechanics

### Global Activation Check
All spreading goo checks `mod_GreyGoo.isGooActive(blockID, world)` before acting. This can be disabled by EMP Array blocks.

### Cleaner Check
Almost all goo types check for adjacent Red Goo (Cleaner) and convert themselves to Cleaner if found. This is the primary counter-measure.

### Dormancy System
Many goo types use metadata value `2` to indicate dormancy (no more food found). Dormant blocks skip spreading logic.

### Manhattan Distance
"Face-adjacent" means Manhattan distance == 1 (sharing a face, not diagonal). Calculated as `Math.abs(x) + Math.abs(y) + Math.abs(z)`.

---

## Activation-Required Goo

These goo types have `setTickRandomly(false)` and require a right-click to begin spreading. Once activated, a global boolean flag enables their spread.

---

### Bubble Goo ("White-GreenGoo")

**Class:** `BlockBubble`
**Light Level:** 0.1
**Activation Flag:** `BubbleisSpreading`

**Spreading Behavior:**
- Only spreads when `BubbleisSpreading` is true
- Scans 5x5x5 area (-2 to +2)
- Converts AIR blocks to Bubble Goo IF:
  - Position is face-adjacent (Manhattan < 2)
  - Light level at target > 9
- Schedules new blocks with random delay (0-14 ticks)

**Decay:**
- Scans 5x5x5 area
- If ANY position has light level < 2: removes self
- This means Bubble Goo dies in darkness

**Drops:** 1/15 chance, randomly either Green Goo (Inert) or White Goo (Air Eater) item

**Summary:** Light-dependent goo that fills air in well-lit areas. Dies in darkness.

---

### Freezer ("Green-RedGoo")

**Class:** `BlockFreezer`
**Light Level:** 0.5
**Activation Flag:** `FreezerisSpreading`
**Hardness:** 0.0
**Rendering:** Transparent (non-opaque)

**Spreading Behavior:**
- Only spreads when `FreezerisSpreading` is true
- Scans 3x3x3 area (-1 to +1)
- Converts blocks in `cleanerList` to Freezer (except self)
- Schedules new blocks with delay 4-28 ticks

**What It Freezes (`cleanerList`):**
All goo types: Rainbow, Cancer, TGD, TGD Inert, Cancer 2, Water Eater, Grey Goo, Purple Goo, Air Eater, Miner, Rapid Miner, Bubble, Wall, Black, Gravity, Freezer, Rapid Water Eater, Orange-Red, Rapid Eater, Orange-Purple, Orange-White

**Drops:** 1/15 chance, randomly either Red Goo (Cleaner) or Miner Goo item

**Summary:** Converts other goo types into itself, effectively "freezing" goo spread. Counter-measure that doesn't destroy but neutralizes.

---

### Orange-Red Goo ("OrangeRedGoo")

**Class:** `BlockOrangeRed`
**Light Level:** 0.5
**Activation Flag:** `OrangeRedIsSpreading`
**Hardness:** UNBREAKABLE

**Spreading Behavior:**
- Only spreads when `OrangeRedIsSpreading` is true
- Scans 3x3x3 area
- For each position at Manhattan < 2:
  - Checks if OPPOSITE position (-l, -i1, -j1) contains Orange-Red OR Green Goo (Inert)
  - If yes, AND target position is not Green Goo or self: converts target
- Schedules new blocks with delay 0-8 ticks (sum of 3 random 0-2 values)

**Key Pattern:** Spreads AWAY from Green Goo anchor points. Green Goo acts as seed/anchor.

**Summary:** Unbreakable barrier goo that spreads outward from Green Goo anchors. Forms defensive walls.

---

### Orange-Purple Goo ("OrangePurpleGoo")

**Class:** `BlockOrangePurple`
**Light Level:** 0.5
**Activation Flag:** `OrangePurpleIsSpreading`
**Hardness:** UNBREAKABLE

**Spreading Behavior:**
- Only spreads when `OrangePurpleIsSpreading` is true
- Scans 3x3x3 area
- For each position at Manhattan < 2:
  - Checks if OPPOSITE position contains Orange-Purple OR Green Goo (Inert)
  - If target is not Green Goo and not self: converts target
  - **ALSO:** Removes block at 2x offset in opposite direction (erosion behind)
- Schedules new blocks with delay 3 ticks

**Key Pattern:** Spreads outward from anchor while ERASING blocks behind it. Creates moving wave effect.

**Summary:** Moving barrier that erases its trail. Creates temporary walls that shift.

---

### Orange-White Goo ("OrangeWhiteGoo")

**Class:** `BlockOrangeWhite`
**Light Level:** 0.5
**Activation Flag:** `OrangeWhiteIsSpreading`
**Hardness:** 0.0
**Special:** Acts as LADDER (climbable)
**Rendering:** Custom render type 65, transparent, narrow collision box (0.31-0.69)

**Spreading Behavior:**
- Only spreads when `OrangeWhiteIsSpreading` is true
- Scans 3x3x3 area
- For each position at Manhattan < 2:
  - Checks if OPPOSITE position contains Orange-White OR Green Goo (Inert)
  - Additional check: position at 2x offset must NOT be self (prevents dense clusters)
  - Converts target if valid
- Schedules new blocks with delay 0-5 ticks

**Drops:** 1/15 chance of either Orange Goo (Wall) or White Goo (Air Eater) item

**Summary:** Climbable goo tendrils that spread from anchors. Creates ladder-like structures.

---

### Rapid Eater ("PurpleRedGoo")

**Class:** `BlockRapidEater`
**Light Level:** 0.5
**Activation Flag:** `RapidEaterIsSpreading`

**Spreading Behavior:**
- Only spreads when `RapidEaterIsSpreading` is true
- Scans 3x3x3 area
- Converts ALL blocks (not air, not in `gooNeverEatThese`)
- Schedules new blocks with delay 4-28 ticks
- **SELF-DESTRUCTS:** After spreading, converts self to air

**Key Behavior:** Explosive consumption wave that erases everything including itself. Leaves nothing behind.

**Drops:** 1/15 chance, randomly either Red Goo (Cleaner) or Purple Goo item

**Summary:** Destructive wave that consumes all blocks and itself. Total erasure.

---

### Rapid Miner ("Brown-RedGoo")

**Class:** `BlockRapidMiner`
**Light Level:** 1.0
**Activation Flag:** `RapidMinerisSpreading`

**Spreading Behavior:**
- Only spreads when `RapidMinerisSpreading` is true
- Scans 3x3x3 area, face-adjacent only
- ONLY converts blocks in `mineTheseOnly` list
- Skips: ores (diamond, gold, iron, coal, lapis, redstone), Orange Wall, Green Inert, Black Goo, bedrock, chest, self
- Schedules new blocks with delay 4-21 ticks
- **SELF-DESTRUCTS:** After spreading, converts self to air

**mineTheseOnly List:**
- Gravel, Stone, Sand, Sandstone, Netherrack, Soul Sand, Clay

**Drops:** 1/15 chance, randomly either Red Goo (Cleaner) or Miner Goo item

**Summary:** Mining wave that consumes stone/dirt types while preserving ores. Self-erasing excavator.

---

### Rapid Water Eater

**Class:** `BlockRapidWaterEater`
**Light Level:** 0.0
**Display Name:** "fsgsfg897yhewf" (placeholder/obfuscated)
**Activation Flag:** `RapidWaterEaterisSpreading`

**Spreading Behavior:**
- Only spreads when `RapidWaterEaterisSpreading` is true
- Scans 3x3x3 area
- Converts water (still/moving) and lava (still/moving)
- Schedules new blocks with delay 4-28 ticks
- **SELF-DESTRUCTS:** After spreading, converts self to air

**Drops:** 1/15 chance, randomly either Red Goo (Cleaner) or Blue Goo (Water Eater) item

**Summary:** Rapid water/lava draining that self-erases. Fast fluid removal.

---

## Autonomous Goo

These goo types have `setTickRandomly(true)` and spread automatically via random ticks.

---

### Grey Goo ("GreyGoo")

**Class:** `BlockGreyEater`
**Light Level:** 0.4

**THIS IS THE MAIN "GREY GOO" BLOCK THAT STARTS THE DESTROYER CHAIN.**

See main documentation for full details. Key points:
- 1/3000 chance on placement to become Cancer
- 1/10000 chance per tick to become Cancer
- Spreads to face-adjacent blocks
- If touches Cancer, becomes Cancer
- Tracks what it consumed via metadata (affects color tint and drops)

---

### Purple Goo ("PurpleGoo")

**Class:** `BlockGreyGoo`
**Light Level:** 0.625

**Spreading Behavior:**
- Uses SpreadHelper with radius 1, no diagonals
- First checks for Cleaner - converts self if found
- Searches for blocks NOT in `gooNeverEatThese`, NOT self, NOT air
- Converts ALL found blocks simultaneously

**Decay - Column Collapse:**
- If air on all 4 sides AND above:
  - Scans downward up to 100 blocks
  - Removes entire vertical column of Purple Goo below
  - Removes self
- If just air above: removes self

**Dormancy:** Metadata 2 when no food found

**Drops:** 1/20 chance

**Summary:** Basic spreading goo with vertical column collapse. Does NOT transform into Cancer (despite class name).

---

### Red Goo / Cleaner ("RedGoo")

**Class:** `BlockCleaner`
**Light Level:** 1.0

**THE PRIMARY COUNTER TO ALL GOO.**

**Cleaning Behavior:**
1. **Detection phase:** Scans 7x7x7 area (-3 to +3) for any block in `cleanerList`
2. **Conversion phase:** Scans 5x5x5 area (-2 to +2), converts `cleanerList` blocks within Manhattan < 3 to Cleaner
3. **Self-cleanup:** If NO goo detected in 7x7x7, removes adjacent Cleaner blocks (prevents infinite spread)
4. **Self-destruct:** Always removes self after cleaning

**cleanerList (blocks it cleans):**
Rainbow Goo, Cancer, TGD, TGD Inert, Cancer 2, Water Eater, Grey Goo (both), Air Eater, Miner, Rapid Miner, Bubble, Orange Wall, Black Goo, Gravity Goo, Freezer, Rapid Water Eater, Orange-Red, Rapid Eater, Orange-Purple, Orange-White

**Special - TGD Golem Damage:**
- Entities walking on Cleaner: if entity is TGD Golem, deals 50 damage

**Drops:** 1/20 chance

**Summary:** Aggressive goo hunter that converts other goo to itself, then self-destructs. Chain reaction cleaner.

---

### Orange Goo / Wall ("OrangeGoo")

**Class:** `BlockWall`
**Light Level:** 0.4
**Hardness:** UNBREAKABLE

**Spreading Behavior:**
- Uses spread limiter (general)
- Scans 3x3x3 area
- For each position at Manhattan < 2:
  - Checks if OPPOSITE position contains Orange Goo OR Green Goo (Inert)
  - Target must not be Green Goo, not in `gooNeverEatThese` (exception: snow)
  - Converts valid targets

**Note:** Snow can be overwritten despite being in protected list

**Dormancy:** Metadata 2 when no food found. Right-click resets metadata to 0.

**Summary:** Unbreakable barrier that spreads from anchor points. Primary defensive wall.

---

### Blue Goo / Water Eater ("BlueGoo")

**Class:** `BlockWaterEater`
**Light Level:** 0.6

**Spreading Behavior:**
- Scans 3x3x3 area, face-adjacent only
- Converts: water (still/moving), lava (still/moving)

**Decay - Column Collapse:**
- Same as Purple Goo: if isolated with air on all sides, collapses downward and removes column
- If just air above: removes self

**Dormancy:** Metadata 2 when no liquids found

**Drops:** 1/20 chance

**Summary:** Liquid-specific eater with column collapse. Drains water and lava.

---

### White Goo / Air Eater ("WhiteGoo")

**Class:** `BlockAirEater`
**Light Level:** 0.1

**Spreading Behavior:**
- Uses SpreadHelper with radius 1, no diagonals
- First checks for Cleaner - converts self if found
- Searches for AIR blocks (block ID 0)
- Only converts air blocks with light level > 6

**Decay:**
- Scans 3x3x3 area
- If ANY adjacent position has light level < 2: removes self

**Dormancy:** Metadata 2 when no valid air found

**Drops:** 1/20 chance

**Summary:** Light-dependent goo that fills air in lit areas. Dies in complete darkness.

---

### Miner Goo ("MinerGoo")

**Class:** `BlockMinerGoo`
**Light Level:** 0.9

**Spreading Behavior:**
- Scans 3x3x3 area, face-adjacent only
- ONLY converts blocks in `mineTheseOnly` list AND NOT in `gooNeverEatThese`
- Explicitly skips: diamond/gold/iron/coal/lapis/redstone ores, Orange Wall, Green Inert, Black Goo, bedrock, chest, self

**mineTheseOnly List:**
- Gravel, Stone, Sand, Sandstone, Netherrack, Soul Sand, Clay

**Dormancy:** Metadata 2 when nothing to mine

**Drops:** 1/20 chance

**Summary:** Selective miner that consumes stone/dirt types while preserving ores. Controlled excavation.

---

### Yellow Goo / Gravity Goo ("Yellow Goo")

**Class:** `BlockGravityGoo`
**Light Level:** 0.5
**Material:** Sand (falls like sand)

**Spreading Behavior:**
- Uses `numberoffallingLimiter` (separate from other spread limiters)
- Scans 3x3x3 area, face-adjacent only
- Converts non-air blocks that are:
  - Not chest, not Gravity Goo, not bedrock
  - Not in `gooNeverEatThese`
  - Not liquids (water/lava - these are fall-through only)

**Falling Behavior:**
- On placement or neighbor change: checks if can fall (air, fire, water, or lava below)
- Spawns `EntityFallingGravityGoo` entity
- Removes self from original position
- Falls through air, fire, water, and lava

**Drops:** 1/20 chance

**Summary:** Gravity-affected spreading goo. Converts blocks then falls, creating cascading destruction.

---

## Destroyer Chain

See main documentation for detailed progression. Summary here:

---

### Cancer ("Tumor")

**Class:** `BlockCancer`
**Light Level:** 0.625

**Food Detection:** 7x7x7 area for non-goo blocks
**Spreading:** 7x7x7 area with density limiting (max 3 nearby Cancer)
**Destruction:** 9x9x9 area destroys non-goo blocks (sets to air)
**Transformation:** When no food, transforms to Black Goo (toggle delay)

---

### Black Goo ("Darkness")

**Class:** `BlockBlack`
**Light Level:** 0.0 (completely dark)
**Has TileEntity:** Yes

**Food Detection:** 7x7x7 area
**Spreading:** 5x5x5 area, 1/60 chance, density capped at 100
**Transformation:** When no food, immediately becomes Cancer 2
**Particles:** Portal particles

---

### Cancer 2 ("Plague")

**Class:** `BlockCancer2`
**Light Level:** 0.225
**Has TileEntity:** Yes

**Food Detection:** 9x9x9 area
**Spreading:** 3x3x3 area, surface-only (needs air above), player proximity affects rate
**Transformation:** When no food, becomes TGD (toggle delay)
**Entity Effect:** Poison II + Wither II for 50 ticks
**Particles:** Portal particles

---

### The Great Destroyer ("TheGreatDestroyer")

**Class:** `BlockTGD`
**Light Level:** 0.825
**Hardness:** 1.0

**Ascending Phase (Y < bloomheight or Y > bloomheight+13):**
- Grows upward as pillars
- Converts self to TGD Inert after spreading

**Bloom Phase (Y == bloomheight or Y == 240):**
- Sets `TGDbloom` flag (one-time world event)
- 1/200 chance to spawn TGD Golem
- Spreads horizontally in all 4 cardinal directions
- Converts self to TGD Inert

**Default bloomheight:** 146

---

### TGD Inert ("Inert Destroyer ")

**Class:** `BlockTGDinert`
**Light Level:** 0.225
**Does NOT random tick** - only scheduled updates

**Golem Formation:**
- 500 ticks after placement, checks for golem pattern:
  - 4 vertical TGD Inert blocks
  - 2 horizontal TGD Inert blocks extending from Y-1
- If pattern found: removes all blocks, spawns TGD Golem

**Drops:** 1/10 chance

---

## Utility / Non-Spreading Goo

---

### Green Goo / Inert ("GreenGoo")

**Class:** `BlockInert`
**Light Level:** 0.1
**Does NOT tick**

**Purpose:**
- Anchor point for Orange-family goo spreading
- Protected from most goo types
- Debug tool: shift-right-click displays:
  - TGD Bloom status
  - Global Goo Active status
  - World type
  - Current dimension
  - EMP Array status

**Drops:** Always 1 (100% drop rate)

**Summary:** Inert anchor block. Does nothing but serves as spreading seed for barrier goo.

---

### Rainbow Goo / Restorer ("RainbowGoo")

**Class:** `BlockRestorer`
**Light Level:** 1.0

See main documentation for full details. Restores terrain to original world generation state using backup dimension or fresh chunk generation.

---

### Elevator Goo ("ElevatorGoo")

**Class:** `BlockElevatorGoo`
**Light Level:** 0.5
**Does NOT tick**

**Behavior:**
- When entity walks on it AND block exists 5 blocks above:
  - Removes block at Y-1 (below)
  - Removes blocks at Y+2 and Y+3
  - Places Elevator Goo at Y+1

**Effect:** Steps upward when walked on, if there's a ceiling. Creates rising platform.

**Summary:** Pressure-activated elevator. Steps up when walked on under a ceiling.

---

### Substrate

**Class:** `BlockSubstrate`
**Light Level:** 0.0
**Display Name:** "fsgsfgyhewf" (placeholder/obfuscated)
**Does NOT tick**

**Behavior:** None. Inert decorative block.

---

### Data Storage

**Class:** `BlockDataStorage`
**Light Level:** (none set)
**Does NOT tick**

**Behavior:** None. Internal/utility block.

---

## Quick Reference Tables

### Goo Types by Danger Level

| Danger | Blocks |
|--------|--------|
| Catastrophic | Grey Goo (starts chain), Cancer, Black Goo, Cancer 2, TGD |
| Destructive | Rapid Eater, Rapid Miner, Gravity Goo |
| Consuming | Purple Goo, Water Eater, Air Eater, Miner Goo |
| Neutral | Orange Wall, Orange-Red, Orange-Purple, Orange-White, Bubble, Freezer |
| Beneficial | Red Goo (Cleaner), Rainbow Goo (Restorer), Green Goo (Inert) |

### Activation Requirements

| Requires Right-Click | Auto-Spreads |
|---------------------|--------------|
| Bubble, Freezer, Orange-Red, Orange-Purple, Orange-White, Rapid Eater, Rapid Miner, Rapid Water Eater | Grey Goo, Purple Goo, Red Goo, Orange Wall, Blue Goo, White Goo, Miner Goo, Gravity Goo, Cancer, Black Goo, Cancer 2, TGD |

### Light Levels

| Light | Blocks |
|-------|--------|
| 1.0 | Rainbow Goo, Red Goo (Cleaner), Rapid Miner |
| 0.825 | TGD |
| 0.625 | Purple Goo, Cancer |
| 0.6 | Blue Goo (Water Eater) |
| 0.5 | Freezer, Orange-Red, Orange-Purple, Orange-White, Rapid Eater, Gravity Goo, Elevator Goo |
| 0.4 | Grey Goo, Orange Wall |
| 0.225 | Cancer 2, TGD Inert |
| 0.1 | Green Goo, White Goo, Bubble |
| 0.0 | Black Goo (Darkness), Substrate, Rapid Water Eater |

### Hardness

| Unbreakable | Normal (0.0) | Harder (1.0) |
|-------------|--------------|--------------|
| Orange Wall, Orange-Red, Orange-Purple | Most goo | TGD |
