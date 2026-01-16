# Restorer Block Implementation Notes

**Date**: 2026-01-15
**Status**: Complete and Working
**Minecraft Version**: 1.20.1 Forge

---

## Overview

This document describes the specific implementation details and fixes applied to the Restorer Block to match the original legacy implementation exactly.

Related Documentation:
- [DIMENSION_SYSTEM.md](DIMENSION_SYSTEM.md) - Technical details of the backup dimension system
- [CLAUDE.md](CLAUDE.md) - General project architecture and conventions

---

## Implementation Timeline

### Phase 1: Initial Implementation (Failed Approaches)

#### Attempt 1: Dynamic Block Recalculation
- **Approach**: Recalculate what blocks should be using noise generators
- **Result**: ❌ Failed - non-deterministic feature placement (trees, structures)

#### Attempt 2: On-Demand Chunk Generation
- **Approach**: Generate fresh chunks using `NoiseBasedChunkGenerator.fillFromNoise()`
- **Implementation**:
  ```java
  ChunkAccess chunk = generator.fillFromNoise(executor, blender, randomState, structureManager, protoChunk);
  ```
- **Result**: ❌ Failed - 5+ second generation time per chunk caused server freezing
- **Error**: `Can't keep up! Running 29986ms or 599 ticks behind`

### Phase 2: Backup Dimension Approach (Success)

#### Research Phase
- Investigated JSON dimension definitions → Cannot dynamically inherit seed
- Examined RFTools Dimensions → Uses reflection for runtime creation
- Found Commoble's 1.16.4 dynamic dimension gist
- Discovered Access Transformers as cleaner alternative to reflection

#### Implementation Phase
1. Created Access Transformer configuration with SRG field names
2. Implemented DynamicDimensionManager (adapted from RFTools)
3. Rewrote PristineChunkGenerator to use backup dimension
4. Updated RestorerBlock to use new pristine lookup system

---

## Restorer Block Mechanics

### State Machine

The Restorer Block has 3 states matching the original exactly:

```
STATE 0 (DEFAULT)  ──flag══>  STATE 1 (READY)  ──restore══>  [Block Replaced]
     │                              │
     │                              │
     └───flag1══>  STATE 2 (COMPLETE)  ──restore══>  [Block Replaced]
```

**State Variables** (from original BlockRestorer.java lines 49-50):
- `flag`: True if all adjacent blocks either match pristine OR all spread attempts succeeded
- `flag1`: True if NO differences found at all

**State Transitions**:
- `STATE 0 → STATE 1`: When `flag==true` (found differences, all spreading succeeded)
- `STATE 0 → STATE 2`: When `flag1==true` (no differences found, ready to decay)
- `STATE 1 → [Restored]`: Block restores itself to pristine state
- `STATE 2 → [Restored]`: Block restores itself to pristine state (same as STATE 1)

### Critical Implementation Details

#### 1. State 1 and 2 Both Restore First

**Original Code** (BlockRestorer.java:66-85):
```java
if(world.getBlockMetadata(i, j, k)==1 && world.getBlockId(i, j, k)==this.blockID) {
    // State 1 - Restore position
    world.setBlockAndMetadataWithNotify(i, j, k, backupBlockID, backupMetadata);
}
else if((world.getBlockMetadata(i, j, k)==2) && world.getBlockId(i, j, k)==this.blockID) {
    // State 2 - Also restore position
    world.setBlockAndMetadataWithNotify(i, j, k, backupBlockID, backupMetadata);
}
```

**Modern Implementation** (RestorerBlock.java:98-107):
```java
if (state == STATE_READY || state == STATE_COMPLETE) {
    BlockState pristineBlock = pristineGenerator.getPristineBlock(level, pos);
    if (pristineBlock != null) {
        level.setBlockAndUpdate(pos, pristineBlock);
        return;  // Block has been replaced, we're done
    }
}
```

**Key Point**: Both STATE 1 and STATE 2 restore the block at their own position FIRST, then the block is gone (replaced with pristine block). The state machine doesn't continue after restoration - the restorer block ceases to exist at that position.

#### 2. Spreading Logic and Flag Calculation

**Original Code** (BlockRestorer.java:107-124):
```java
if (!mod_GreyGoo.instance.NeverRestoreThese.contains(thisWorldBlockID) &&
    !mod_GreyGoo.instance.NeverRestoreThese.contains(backupWorldBlockID) &&
    (Math.abs(l) + Math.abs(i1) + Math.abs(j1) == 1) &&  // Only adjacent (not diagonal)
    thisWorldBlockID != backupWorldBlockID) {

    flag1 = false;  // Found a difference

    if(random.nextBoolean()) {  // 50% chance
        // Spread restorer to this position
        world.setBlockWithNotify(i + l, j + i1, k + j1, blockID);
        world.setBlockMetadata(i + l, j + i1, k + j1, 0);
        world.scheduleBlockUpdate(i + l, j + i1, k + j1, blockID,
            random.nextInt(25) + random.nextInt(10));
    } else {
        flag = false;  // Didn't spread - mark incomplete
    }
}
```

**Modern Implementation** (RestorerBlock.java:125-145):
```java
if (!currentBlock.is(pristineBlock.getBlock())) {
    flag1 = false;  // Found at least one difference

    if (random.nextBoolean()) {  // 50% chance to spread
        // Can spread into anything except bedrock/barrier (ALLOW goo!)
        if (!currentBlock.is(Blocks.BEDROCK) && !currentBlock.is(Blocks.BARRIER)) {
            level.setBlockAndUpdate(targetPos,
                defaultBlockState().setValue(RESTORE_STATE, STATE_DEFAULT));

            int delay = random.nextInt(25) + random.nextInt(10);
            level.scheduleTick(targetPos, this, delay);
        }
    } else {
        flag = false;  // 50% chance we didn't spread
    }
}
```

**Key Change**: Removed `GreyGooMod.isGoo()` check - restorer CAN spread into goo blocks now!

#### 3. Timing Randomization

**Critical Issue**: Without randomized delays, all restorer blocks update simultaneously causing lag spikes.

**Original Timing** (BlockRestorer.java:116, 151, 202):
- Spread delay: `random.nextInt(25) + random.nextInt(10)` (0-34 ticks)
- Self-update delay: `random.nextInt(25) + random.nextInt(10)` (0-34 ticks)
- Right-click delay: `random.nextInt(25) + random.nextInt(10)` (0-34 ticks)

**Why Two Random Calls?**: Creates a triangular distribution instead of uniform:
- `random.nextInt(25)`: 0-24
- `random.nextInt(10)`: 0-9
- Sum: 0-33 with bias toward middle values

This spreads updates more evenly over time.

**Applied To**:
- RestorerBlock: All `scheduleTick()` calls
- RapidWaterEaterBlock: Changed from `nextInt(3)` to `nextInt(25) + nextInt(4)` (matching original line 51)
- WallBlock: Uses randomTick (no manual scheduling needed)

---

## Key Differences From Original

### What Changed

1. **Backup System**:
   - **Original**: Direct world generation copy via dimension
   - **Modern**: Dynamic dimension created at runtime with mirrored LevelStem

2. **Block Comparison**:
   - **Original**: `dimHelper.getBlockIDfromFreshChunk(world, x, y, z)`
   - **Modern**: `pristineGenerator.getPristineBlock(level, pos)`

3. **State Storage**:
   - **Original**: Metadata values (0, 1, 2)
   - **Modern**: BlockState IntegerProperty (0, 1, 2)

4. **Spread Protection**:
   - **Original**: `!mod_GreyGoo.NeverRestoreThese.contains(blockID)`
   - **Modern**: `!currentBlock.is(Blocks.BEDROCK) && !currentBlock.is(Blocks.BARRIER)`
   - **Removed**: Check for `isGoo()` - restorer now fights other goo!

### What Stayed The Same

1. **State Machine Logic**: Exact 3-state cycle with flag/flag1 transitions
2. **Spread Probability**: 50% chance via `random.nextBoolean()`
3. **Timing Delays**: `random.nextInt(25) + random.nextInt(10)`
4. **Adjacent-Only Spread**: Only 6 faces (not diagonal)
5. **Self-Restoration**: States 1 and 2 restore their own position first

---

## Testing Checklist

### Functional Tests

- [x] Restorer block places correctly
- [x] Backup dimension creates on first use
- [x] Seed verification (backup seed matches overworld)
- [x] Block comparison works (detects modifications)
- [x] Spreading works (50% probability observable)
- [x] State transitions (DEFAULT → READY → restore)
- [x] State transitions (DEFAULT → COMPLETE → restore when no work)
- [x] Self-restoration (block replaces itself with pristine)
- [x] Goo overtaking (restorer spreads into goo blocks)
- [x] Right-click activation works
- [x] Scheduled ticks work (updates continue automatically)
- [x] Timing randomization (no synchronized updates)

### Performance Tests

- [ ] Large area restoration (100x100 blocks)
- [ ] Multiple restorers active simultaneously
- [ ] Backup dimension chunk loading performance
- [ ] No server freezing or timeouts
- [ ] TPS remains stable during restoration

### Edge Cases

- [ ] Restorer in non-overworld dimension (should fail gracefully)
- [ ] Restorer near world border
- [ ] Restorer in pristine area (should decay immediately)
- [ ] Restorer vs Grey Goo battle (both spreading)
- [ ] Restorer vs Cleaner interaction
- [ ] World reload persistence (backup dimension reloads)

---

## Known Issues and Limitations

### Current Limitations

1. **No NBT Data Restoration**:
   - Restores block types only, not tile entity data
   - Chests, signs, etc. lose their contents
   - **Future Enhancement**: Copy NBT from backup dimension

2. **No Protected Blocks List**:
   - Currently only protects bedrock and barrier
   - **Future Enhancement**: Forge tag `#greygoo:restorer_blacklist`

3. **Structure Generation Edge Cases**:
   - Multi-chunk structures (villages, strongholds) should generate correctly
   - Not extensively tested yet
   - Backup dimension uses same ChunkGenerator, so should match

4. **Memory Usage**:
   - Backup dimension chunks loaded on-demand
   - Chunks stay loaded while in use
   - No explicit unloading mechanism currently
   - **Note**: Minecraft's chunk management should handle this

### Not Issues (By Design)

1. **Restorer Spreading Into Goo**: This is intentional! Restorer fights destructive goo.
2. **Decay After Restoration**: Restorer removes itself when work is done (not a bug).
3. **Randomized Update Timing**: Prevents lag spikes (performance feature).

---

## Future Enhancements

### Short-Term

1. **NBT Data Copying**:
   ```java
   BlockEntity sourceEntity = backupDimension.getBlockEntity(pos);
   if (sourceEntity != null) {
       CompoundTag nbt = sourceEntity.saveWithFullMetadata();
       // Apply to current dimension
   }
   ```

2. **Tag-Based Blacklist**:
   ```java
   if (currentBlock.is(TagKey.create(Registries.BLOCK,
       new ResourceLocation("greygoo", "restorer_blacklist")))) {
       continue;  // Don't restore
   }
   ```

3. **Visual Feedback**:
   - Particles during restoration
   - Sound effects for state transitions
   - Different textures per state

### Long-Term

1. **Selective Restoration**:
   - Restore only specific block types
   - Exclude natural vs player-placed blocks
   - Time-based restoration (only restore blocks changed after X time)

2. **Restoration History**:
   - Track what was restored
   - Undo restoration
   - Multiple backup snapshots

3. **Performance Optimization**:
   - Async block queries
   - Batch comparisons
   - Chunk pre-generation for known restoration areas

---

## Debugging Tips

### Enable Debug Logging

Change LOGGER calls from `debug()` to `info()` in RestorerBlock.java to see:
- State transitions
- Spread attempts
- Pristine block comparisons
- Timing delays

### Check Backup Dimension

```java
// In-game command or debug code:
ServerLevel backup = server.getLevel(PristineChunkGenerator.PRISTINE_BACKUP_KEY);
if (backup != null) {
    System.out.println("Backup seed: " + backup.getSeed());
    System.out.println("Overworld seed: " + server.overworld().getSeed());
    System.out.println("Seeds match: " + (backup.getSeed() == server.overworld().getSeed()));
}
```

### Verify State Transitions

Add breakpoints or logging at:
- Line 149: `if (flag && level.getBlockState(pos).is(this))`
- Line 154: `else if (flag1 && level.getBlockState(pos).is(this))`
- Line 99: `if (state == STATE_READY || state == STATE_COMPLETE)`

### Performance Profiling

Use `/debug start` and `/debug stop` in-game to generate performance reports.

Look for:
- Slow chunk generation in backup dimension
- Too many scheduled ticks queued
- Memory usage of loaded chunks

---

## References

### Legacy Implementation

- Original file: `C:\Users\steve\IdeaProjects\OldGreyGoo\GreyGooMod\mod_GreyGoo\BlockRestorer.java`
- Key lines:
  - 46-159: Main `restore()` method
  - 107-124: Spreading and flag logic
  - 141-149: State transitions
  - 66-85: State 1/2 restoration

### Modern Implementation

- RestorerBlock.java: Main restoration logic
- PristineChunkGenerator.java: Backup dimension management
- DynamicDimensionManager.java: Runtime dimension creation
- Access transformers: src/main/resources/META-INF/accesstransformer.cfg

---

**End of Implementation Notes**

Last updated: 2026-01-15
