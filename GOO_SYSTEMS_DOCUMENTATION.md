# Grey Goo Mod - System Behavior Documentation

This document describes the exact behavior of two complex systems from the Grey Goo mod, intended to enable faithful reimplementation in modern Minecraft versions.

---

## Table of Contents
1. [Rate Limiting System (SpreadLimiter)](#rate-limiting-system)
2. [System 1: Rainbow Goo / Restorer Block](#system-1-rainbow-goo--restorer-block)
3. [System 2: The Destroyer Chain](#system-2-the-destroyer-chain)
   - [Grey Goo (Starting Point)](#grey-goo-starting-point)
   - [Cancer](#cancer)
   - [Black Goo (Darkness)](#black-goo-darkness)
   - [Cancer 2 (Plague)](#cancer-2-plague)
   - [The Great Destroyer (TGD)](#the-great-destroyer-tgd)
   - [TGD Inert](#tgd-inert)
   - [TGD Golem Entity](#tgd-golem-entity)

---

## Rate Limiting System

All goo blocks use a rate-limiting system to prevent server lag. This is critical to understand before implementing any goo behavior.

### How It Works
- Each goo category has its own spread counter that tracks spreads per tick
- When a block wants to spread, it first checks `spreadLimiter(false)` - this returns true/false based on probability
- After successfully spreading, the block calls `spreadLimiter(true)` to increment the counter
- Counters are reset each game tick by `CommonTickHandler`

### Spread Categories and Limits
| Category | Max Per Tick | Config Scale Variable |
|----------|--------------|----------------------|
| General (Grey Goo, etc.) | 100 | `GeneralSpreadScale` |
| Destroyer series | 70 | `DestroyerSpreadScale` |
| TGD | 50 | `TGDSpreadScale` |
| Restorer | 100 | `RestorerSpreadScale` |

### Probability Formula
The spread limiter uses a soft cap with diminishing probability:
```
base = floor((currentSpread - maxSpread) / maxSpread)
allowed = random(0-99) > (base + configScale)
```
When `configScale = 0` and spreads are under the limit, spreading is almost always allowed. As spreads exceed the limit, probability decreases. Config scale (0-100) allows further throttling.

---

## System 1: Rainbow Goo / Restorer Block

**Purpose:** Restores terrain to its original world-generated state by comparing current blocks against freshly generated chunk data.

### Prerequisites
- Requires a "Goo Portal" to be registered in the world (checked via `worldsWithPortal` hashmap keyed by dimension ID)
- Without a nearby portal, displays message "No nearby Goo Portal Core detected" and does nothing

### Core Mechanism: Fresh Chunk Generation
The Restorer works by generating a "fresh" chunk using the world's chunk generator, then comparing blocks:

1. **For Overworld (dimension 0):** Looks up the corresponding block in a custom "backup dimension" (`mod_GreyGoo.dimensionID`)
2. **For the backup dimension:** Looks up the corresponding block in the Overworld
3. **For other dimensions:** Generates a fresh chunk on-demand using `WorldProvider.createChunkGenerator()`, caches it

The system stores `blockMetaData` alongside block IDs to preserve block variants.

### Block Metadata States
| Metadata | Meaning |
|----------|---------|
| 0 | Active, searching for blocks to restore |
| 1 | Ready to self-replace (all neighbors are correct or protected) |
| 2 | Finished restoring, ready for final self-replacement |

### Spreading Behavior (restore method)

**Step 1: Self-Replacement Check**
- If metadata == 1 AND block is still restorer:
  - Get the "backup" block ID and metadata for this position
  - If backup block is in `NeverRestoreThese` list: replace self with air
  - Otherwise: replace self with the backup block (including metadata)
- If metadata == 2: directly replace self with backup block

**Step 2: Neighbor Scanning**
Scans a 3x3x3 cube centered on the block (offsets -1 to +1 on each axis):
- Only processes blocks at Manhattan distance == 1 (face-adjacent, not diagonal)
- For each neighbor position:
  - Get the backup world's block ID for that position
  - Get the current world's block ID for that position
  - Skip if either block is in `NeverRestoreThese`
  - If current != backup (block needs restoration):
    - 50% chance (random boolean): Place a new Restorer block at that position with metadata 0, schedule update with random delay (10-35 ticks)
    - 50% chance: Skip this tick (will try again later)
    - Set flag that work is not complete

**Step 3: Metadata Update**
- If all neighbors were either correct OR in the protected list: set metadata to 1
- If some neighbors still need work but weren't processed: set metadata to 2
- Schedule self for another update (10-35 ticks random delay)

### Tick Update Behavior
- Only runs server-side (`!world.isRemote`)
- Checks `isGooActive` for this block type
- Checks `Restorerspreadlimiter(false)` - if denied, reschedules with longer delay (25-75 ticks)
- If allowed, calls `restore()` method

### NeverRestoreThese List (Protected Blocks)
These blocks are never overwritten by restoration AND the restorer won't spread into positions where the backup contains these:
- Rainbow Goo / Restorer itself
- Green Goo (Inert)
- EMP Array blocks
- Valuable ores: coal, iron, diamond, emerald, gold, lapis, redstone, glowstone
- Valuable blocks: emerald block, diamond block, iron block, gold block, lapis block
- Liquids: water (still/flowing), lava (still/flowing)
- Containers: chest, ender chest
- Special blocks: doors (wood/iron), vines, enchanting table, bookshelf

### Manual Activation
Right-clicking (without sneaking) immediately triggers one `restore()` cycle.

---

## System 2: The Destroyer Chain

This is a progression system where goo evolves through increasingly dangerous forms, ultimately spawning The Great Destroyer which can devastate worlds.

### Chain Overview
```
Grey Goo (BlockGreyEater, "GreyGoo")
    │
    │ [1/3000 on placement OR 1/10000 per tick]
    ▼
Cancer (Tumor)
    │
    │ [when no food in 7x7x7, toggle delay]
    ▼
Black Goo (Darkness)
    │
    │ [when no food in 7x7x7, immediate]
    ▼
Cancer 2 (Plague)
    │
    │ [when no food in 9x9x9, toggle delay]
    ▼
The Great Destroyer (TGD)
    │
    ├──[grows upward as pillars, leaves behind:]──► TGD Inert
    │                                                   │
    │                                                   │ [golem pattern forms]
    │                                                   ▼
    └──[at bloom height, 1/200 chance]──────────► TGD Golem (entity)
```

### Key Concept: "No Food" Detection
Multiple blocks in this chain check if there's "food" (convertible blocks) nearby. When surrounded only by other goo or void, they transform into the next stage.

### Key Concept: Random Mutation
The chain doesn't start from starvation - it starts from **random mutation**. Grey Goo has a small but cumulative chance to spontaneously become Cancer. The more Grey Goo spreads, the more likely a mutation occurs somewhere.

---

### IMPORTANT: Block Naming Confusion

The code has confusing internal names vs display names:
- `BlockGreyGoo` (class) → **"PurpleGoo"** (in-game name)
- `BlockGreyEater` (class) → **"GreyGoo"** (in-game name) ← THIS IS THE ONE THAT STARTS THE CHAIN

The actual "Grey Goo" that players know and that starts the Destroyer chain is `BlockGreyEater`.

---

### Grey Goo (BlockGreyEater) - THE STARTING POINT

**Display Name:** "GreyGoo"
**Light Level:** 0.4
**This is the block that initiates the Destroyer chain.**

#### Cancer Transformation (How the Chain Starts)

**On Block Placement (`onBlockAdded`):**
- 1/3000 chance to immediately transform into Cancer (Tumor)
- `rand.nextInt(3000) == 9`

**On Each Random Tick (`updateTick`):**
- 1/10000 chance to transform into Cancer (Tumor)
- `random.nextInt(10000) == 9`
- If this roll succeeds, skips normal spreading behavior that tick

This means every Grey Goo block is a ticking time bomb - the more Grey Goo spreads, the higher the cumulative chance that one of them mutates into Cancer.

#### Spreading Behavior (assimilate method)

Scans 3x3x3 area (-1 to +1 on each axis), face-adjacent only (Manhattan distance 1):

1. **Cleaner check:** If adjacent to Red Goo (Cleaner), converts self to Cleaner
2. **Cancer absorption:** If adjacent to Cancer, converts self to Cancer (Cancer spreads through Grey Goo)
3. **Normal spread:** For valid targets (not air, not in `gooNeverEatThese`, not self):
   - Converts target to Grey Goo
   - Sets metadata based on what was consumed (determines drop color):

| Consumed Block | Metadata | Drop Item |
|---------------|----------|-----------|
| Water | 5 | Blue Modifier |
| Gravel/Sand | 3 | Yellow Modifier |
| Iron/Coal Ore | 7 | Brown Modifier |
| Leaves/Wood | 4 | Purple Modifier |
| Surface block (air above, 1/5 chance) | 8 | White Modifier |
| Default | 0 | Grey Modifier |

#### Color System
Grey Goo uses metadata to track what it "ate" and renders with tinted colors:
- Meta 1: Light green (0xDCFADC)
- Meta 2: Light orange (0xFFDCB4)
- Meta 3: Light yellow (0xFAFADC)
- Meta 4: Light purple (0xFADCFA)
- Meta 5: Light blue (0xDCDCFA)
- Meta 6: Light red (0xFADCDC)
- Meta 7: Light brown (0xECD8C6)
- Meta 8: Light grey (0xE0E0E0)

#### Random Metadata Assignment on Placement
Additional random metadata assignments on placement for variety:
- 1/100 chance each: metadata 1, 2, 6, or 8
- 1/500 chance each: metadata 3, 4, 5, or 7

#### Special Interactions
- **Entity walking:** Triggers assimilate on self AND all 6 adjacent blocks
- **Right-click:** Same as walking - triggers 7 assimilate calls
- **Always drops 1 modifier item** (color based on metadata)

---

### Purple Goo (BlockGreyGoo) - Secondary Spreader

**Display Name:** "PurpleGoo"
**Light Level:** 0.625
**Note:** This is a DIFFERENT block from "Grey Goo" despite the class name.

**Spreading (assimilate method):**
1. First checks for Red Goo (Cleaner) adjacent - if found, converts self to Cleaner
2. Otherwise, searches radius 1 (Manhattan distance) for blocks NOT in:
   - `gooNeverEatThese` list
   - Self (Purple Goo)
   - Air (block ID 0)
3. Converts ALL found blocks to Purple Goo simultaneously
4. Increments spread counter for each conversion

**Decay Behavior:**
Purple Goo has a unique "column collapse" decay:
- If the block has air on all 4 horizontal sides AND above:
  - Scans downward (up to 100 blocks) until finding something solid
  - Removes the entire vertical column of Purple Goo
- If just air above: removes self

**Dormancy:**
- If no food found, sets metadata to 2 (dormant)
- Dormant blocks (metadata 2) skip the assimilate check entirely

**Note:** Purple Goo does NOT transform into Cancer - it just goes dormant. Only the actual "Grey Goo" (BlockGreyEater) transforms into Cancer.

---

### Cancer

**Display Name:** "Tumor"
**Light Level:** 0.625

**Food Detection (7x7x7 cube):**
Scans -3 to +3 on all axes. A block is "food" if it is NOT:
- Air (0)
- Cancer
- TGD Inert
- TGD
- Cancer 2
- Black Goo (Darkness)

If ANY non-goo block exists in this 7x7x7 area, `flag1 = true` (has food).

**Spreading Behavior:**
Only spreads if `flag1` is true AND metadata != 2:

1. Scans -3 to +3 on all axes
2. For positions at Manhattan distance == 1 (face-adjacent):
   - Skips: Black Goo, Green Goo (Inert), Cancer 2
   - Before converting, does a SECONDARY check in a 5x5x5 area around the target position
   - Counts existing Cancer blocks in that area
   - Only converts if count < 3 (prevents over-dense clusters)
3. For positions at Manhattan distance < 5 (wider area):
   - Destroys (sets to air) any block that isn't: Black Goo, Cancer, Orange Goo (Wall), Cleaner, Cancer 2, TGD, TGD Inert, Green Goo (Inert), or in `gooNeverEatThese`

**Transformation to Black Goo:**
When `flag1 = false` (no food in 7x7x7):
- Uses a toggle (`hasTicked`) that alternates each call
- Only transforms on every OTHER tick when no food detected
- Transforms self into Black Goo (Darkness)

**Dormancy:**
If no blocks were converted this tick, sets metadata to 2.

---

### Black Goo (Darkness)

**Display Name:** "Darkness"
**Light Level:** 0.0 (completely dark)
**Has TileEntity:** Yes (TileEntityBlack)

**Food Detection (7x7x7 cube):**
Same scan as Cancer. Sets `flag = true` if anything exists besides:
- Air, Black Goo, Cancer 2, TGD, TGD Inert

**Spreading Behavior (5x5x5 area):**
Scans -2 to +2 on all axes:
1. First checks for Cleaner - if found, converts self to Cleaner (cleanup)
2. Counts existing Black Goo blocks in the area (`numberofdark`)
3. For valid targets (not air, not protected goo types, not in `gooNeverEatThese`):
   - Only spreads with 1/60 probability (`rand.nextInt(60) == 1`)
   - Additional density check: skips if `numberofdark > 15` AND position is close to center
   - Hard cap: skips if `numberofdark >= 100`

**Transformation to Cancer 2:**
When `flag = false` (no food in 7x7x7):
- Immediately transforms self into Cancer 2 (Plague)
- No toggle delay like Cancer

**Special Interactions:**
- Entity walking on it triggers assimilate
- Spawns portal particles

---

### Cancer 2 (Plague)

**Display Name:** "Plague"
**Light Level:** 0.225
**Has TileEntity:** Yes (TileEntityCancer2)

**Food Detection (9x9x9 cube):**
Scans -4 to +4 on all axes (note: code uses -3 to +4 with reset to -4). Sets `flag1 = true` if anything exists besides:
- Air, Cancer, Cancer 2

**Spreading Behavior (3x3x3 area):**
Scans -1 to +1 on all axes:
1. Checks for Cleaner - if found, converts self to Cleaner
2. Valid targets must be:
   - Not air
   - Not: Cleaner, Black Goo, Cancer, Orange Goo (Wall), Green Goo (Inert), Freezer (Green-Red), TGD, TGD Inert, self, chest
   - Not in `gooNeverEatThese`
   - **Must have air above** (only spreads on surfaces)
3. If player within 10 blocks: spreads guaranteed
4. If no player nearby: only 1/3 chance to spread

**Transformation to TGD:**
When `flag1 = false` (no food in 9x9x9):
- Uses toggle (`hasTicked`) - only transforms every OTHER tick
- Transforms self into The Great Destroyer

**Dormancy:**
If no food found AND no player within 20 blocks:
- Sets metadata to 2
- Removes tile entity

**Special Interactions:**
- Entities walking on it get Poison II and Wither II for 50 ticks (2.5 seconds)
- Spawns portal particles

---

### The Great Destroyer (TGD)

**Display Name:** "TheGreatDestroyer"
**Light Level:** 0.825
**Hardness:** 1.0 (harder than other goo at 0.0)
**Tick Rate:** 5

This is the most complex block. It has two distinct behavior modes based on Y-level.

**Global Constants:**
- `bloomheight`: Configurable, default 146 (just above sea level in old MC)
- `TGDbloom`: Boolean flag, saved to world data, tracks if TGD has ever "bloomed"

#### Mode 1: Ascending Phase (Y < bloomheight OR Y > bloomheight+13 AND Y < 240)

**Spreading Pattern - Vertical Pillar Growth:**
Scans -3 to +3 on all axes, but uses `Math.abs(i1)` for Y (only checks upward):
1. Checks for Cleaner - converts self to Cleaner if found
2. Checks for TGD Inert - converts self to TGD Inert if found
3. Skips positions with: Orange-Red Goo, Black Goo, Orange Goo (Wall), Green Goo (Inert), Freezer, Grey Goo Eater, TGD Inert, chest
4. Only processes face-adjacent positions (Manhattan distance 1)

**Growth Algorithm:**
For each valid position, with 50/50 branch:
- **Even iteration (`l % 2 == 0`):**
  - Places TGD directly above current position
  - 1/6 chance: also places TGD at horizontal offset
  - Places TGD 2 blocks above
  - 1/8 chance: schedules the upper block for faster update
- **Odd iteration:**
  - Places TGD above
  - 1/6 chance: places TGD at opposite horizontal offset
  - Places TGD 1 block above
  - 1/10 chance: schedules for faster update

**After spreading:** Converts SELF to TGD Inert (becomes structural support)

#### Mode 2: Bloom Phase (Y == bloomheight OR Y == 240)

**First Bloom Event:**
- If `TGDbloom` is false: sets it to true, saves to world NBT
- This is a one-time world event that marks "The Great Destroyer has bloomed"

**Golem Spawning:**
- 1/200 chance per tick
- Only if `totalnumberofTGDgolems < maxnumberofTGDgolems` (default max: 80)
- Spawns EntityTGDGolem 4 blocks below the TGD block

**Horizontal Spread at Bloom Height:**
Iterates 3 times, each time:
- Generates random offset 0-5 blocks
- Places TGD in all 4 cardinal directions at that offset
- Skips positions containing Cleaner or TGD Inert
- 1/12 chance: schedules the new block for faster update (0-8 ticks)
- Converts SELF to TGD Inert

#### Decay Behavior
Checks a 9x9 horizontal area at Manhattan distance 4:
- Counts TGD blocks at the perimeter
- Counts air blocks at the perimeter
- If TGD count > 0: converts nearby TGD and block below to TGD Inert
- This creates the "tree trunk" effect as the canopy spreads

---

### TGD Inert

**Display Name:** "Inert Destroyer"
**Light Level:** 0.225
**Does NOT tick randomly** - only updates when scheduled

**Golem Formation Check:**
When placed, schedules update for 500 ticks later. On update, checks for this specific pattern:
```
     [X]      <- checking block (Y)
     [I]      <- Y-1
     [I]      <- Y-2
     [I]      <- Y-3
  [I][I][I]   <- Y-1, extending on X or Z axis
```

Pattern requirements:
- 4 TGD Inert blocks in a vertical column (Y to Y-3)
- 2 TGD Inert blocks extending horizontally from Y-1 (either X-axis OR Z-axis, both sides)

**If pattern matched:**
- Removes all 7-8 blocks involved in the pattern (sets to air)
- Spawns EntityTGDGolem at the position (if under golem cap)

---

### TGD Golem Entity

**Spawning Conditions:**
- Global cap: `maxnumberofTGDgolems` (default 80)
- Current count tracked in `totalnumberofTGDgolems`

**Spawn Sources:**
1. TGD block at bloom height (1/200 chance per tick)
2. TGD Inert blocks forming the golem pattern (500 ticks after pattern completion)

---

## Block Relationships Summary

### Blocks That Stop/Counter The Destroyer Chain
| Block | Effect |
|-------|--------|
| Red Goo (Cleaner) | Converts adjacent destroyer goo to Cleaner - primary counter |
| Orange Goo (Wall) | Immune to conversion, acts as barrier |
| Green Goo (Inert) | Immune to conversion |
| Freezer (Green-Red) | Immune to Cancer 2 and TGD |
| Grey Goo Eater | Stops TGD specifically |
| Orange-Red Goo | Stops TGD |

### Immunity List (gooNeverEatThese)
Referenced by multiple goo types. Contains:
- EMP Array blocks
- All goo block IDs (except targets)
- Portal blocks
- Other protected blocks

### Spread Limiter Usage
| Block | Limiter Type |
|-------|--------------|
| Grey Goo | `spreadLimiter` (general) |
| Cancer | `Destroyerspreadlimiter` |
| Black Goo | `Destroyerspreadlimiter` |
| Cancer 2 | `Destroyerspreadlimiter` |
| TGD | `TGDspreadlimiter` |
| Restorer | `Restorerspreadlimiter` |

---

## Implementation Notes for Modern Minecraft

### Key Differences to Account For
1. **Block IDs vs Registry Names**: Old code uses integer block IDs everywhere. Modern MC uses ResourceLocations.
2. **setBlockWithNotify vs setBlockState**: Replace `world.setBlockWithNotify(x,y,z,id)` with modern block state system.
3. **Metadata vs BlockStates**: Old metadata (0-15) should become block state properties.
4. **TileEntity registration**: Modern MC uses capability system and different TE registration.
5. **Dimension system**: `DimensionManager` is completely different in modern MC.
6. **Chunk generation**: The fresh chunk generation for Restorer needs modern chunk generator API.
7. **Random tick system**: `setTickRandomly(true)` maps to `randomTick` in modern block properties.
8. **Entity spawning**: `world.spawnEntityInWorld` → modern entity spawning API.

### Suggested BlockState Properties
```
RestoreGoo: dormancy (0, 1, 2)
Cancer: dormant (boolean)
Cancer2: dormant (boolean)
BlackGoo: dormant (boolean)
```

### Preserving Behavior Quirks
- The "hasTicked toggle" in Cancer/Cancer2/Black is intentional - it makes transformation take 2 ticks minimum
- TGD's `Math.abs(i1)` for Y-offset means it only grows UP, never down
- Cancer's density check (count < 3) prevents solid walls of cancer
- Black Goo's 1/60 spread chance makes it very slow-spreading compared to others
- Cancer 2's "must have air above" restriction keeps it surface-bound
