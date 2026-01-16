# Future Ideas and Enhancements

This document tracks ideas for future implementation between sessions.

---

## Generalized Batch Spread System

**Status**: Concept Phase
**Proposed Date**: 2026-01-15
**Context**: During Restorer Block optimization work

### Concept

Create a generalized spreading system that can be used across all Grey Goo blocks (GreyGooBlock, RestorerBlock, RapidWaterEaterBlock, WallBlock, CleanerBlock).

### Key Features

1. **3D Array of Spread Targets with Per-Coordinate Modifiers**
   - Each block provides a 3D array of potential spread positions
   - **Each coordinate/position in the array has its own unique set of modifiers**
   - Different positions can have completely different behavior (probability, delays, filters, effects)
   - Flexible targeting (adjacent only, cubic area, custom patterns)

2. **Per-Coordinate Modifier Independence**
   - Each position in the 3D array is a fully independent SpreadTarget
   - Position (1, 0, 0) can have probability 0.5, delay 10-20, particle effects
   - Position (0, 1, 0) can have probability 0.8, delay 5-15, block filter for water
   - Position (-1, 0, 0) can have probability 0.3, delay 20-30, sound effects
   - Allows directional bias (spread upward differently than downward)
   - Allows distance-based behavior (adjacent vs diagonal vs distant blocks)

3. **Temporal Independence**
   - Each spread operation should be independent in time
   - Randomized delays per target (maintains current organic behavior)
   - Batch query optimization without sacrificing temporal variation

4. **Probability-Based Spreading**
   - Current: 50% chance for adjacent blocks
   - Future: Custom probability per target position
   - Allows distance-based falloff, directional bias, etc.

### API Design Sketch

```java
public interface ISpreadable {
    /**
     * Get all potential spread targets for this block
     * @return List of spread targets with positions and probabilities
     */
    List<SpreadTarget> getSpreadTargets(Level level, BlockPos pos, BlockState state);

    /**
     * Check if spreading to target is valid
     * @return true if spread can occur
     */
    boolean canSpreadTo(Level level, BlockPos pos, BlockState currentState,
                        BlockPos target, BlockState targetState);

    /**
     * Perform the spread operation
     */
    void performSpread(Level level, BlockPos source, BlockPos target,
                      SpreadTarget spreadInfo);
}

public class SpreadTarget {
    // Required field
    public final BlockPos position;      // Target position

    // Optional modifiers - use builder pattern or defaults
    public final float probability;      // 0.0 to 1.0 (default: 1.0)
    public final int minDelay;           // Min tick delay (default: 0)
    public final int maxDelay;           // Max tick delay (default: 0)

    // Block filtering modifiers
    public final Predicate<BlockState> blockFilter;        // Filter which blocks to spread into
    public final Set<Block> allowedBlocks;                 // Whitelist of allowed blocks
    public final Set<Block> blockedBlocks;                 // Blacklist of blocked blocks
    public final TagKey<Block> allowedTag;                 // Spread only to blocks with this tag
    public final TagKey<Block> blockedTag;                 // Never spread to blocks with this tag

    // Delay modifiers
    public final DelayType delayType;                      // Uniform, triangular, exponential, etc.
    public final BiFunction<RandomSource, SpreadTarget, Integer> customDelayFunction;

    // Probability modifiers
    public final BiFunction<Level, BlockPos, Float> dynamicProbability;  // Context-aware probability
    public final float distanceFalloff;                    // Probability reduction per block distance

    // State modifiers
    public final BlockState targetState;                   // Specific state to place (or null for default)
    public final Map<Property<?>, Object> stateOverrides;  // Override specific properties

    // Condition modifiers
    public final BiPredicate<Level, BlockPos> extraCondition;  // Additional spread condition
    public final boolean requiresLineOfSight;              // Check for obstructions
    public final boolean requiresAir;                      // Only spread to air blocks
    public final boolean requiresSolid;                    // Only spread to solid blocks
    public final boolean requiresReplaceable;              // Only spread to replaceable blocks

    // Effect modifiers
    public final Consumer<Level, BlockPos> onSpreadEffect; // Callback when spread occurs
    public final ParticleOptions particles;                // Particles to spawn
    public final SoundEvent sound;                         // Sound to play
    public final float soundVolume;                        // Sound volume
    public final float soundPitch;                         // Sound pitch

    // Priority/ordering modifiers
    public final int priority;                             // Higher priority spreads first
    public final boolean cancelOthersOnSuccess;            // If this spreads, cancel remaining

    // Metadata
    public final Object metadata;                          // Custom data for specific implementations

    // Builder for flexible construction
    public static class Builder {
        // Allows constructing with only needed modifiers:
        // new SpreadTarget.Builder(pos).probability(0.5f).blockFilter(b -> !b.is(Blocks.BEDROCK)).build()
    }
}

### Modifier System Flexibility

**Critical Design Principle 1**: Modifiers are applied at the coordinate level in the 3D matrix.
- Each position in the 3D array is its own SpreadTarget with its own modifiers
- Position (1, 0, 0) can have completely different modifiers than position (0, 1, 0)
- This allows directional bias, distance-based behavior, and position-specific effects
- The 3D array is NOT just a list of positions - it's a list of (position + unique modifier set)

**Visual Example of Per-Coordinate Modifiers**:
```
Source Block at (0, 0, 0)

3D Spread Matrix:
  North  (0, 0, 1):  {prob: 0.8, delay: 5-10,  filter: air,   particles: flame}
  South  (0, 0, -1): {prob: 0.3, delay: 20-40, filter: stone, particles: smoke}
  East   (1, 0, 0):  {prob: 0.6, delay: 10-20, filter: none,  sound: hiss}
  West   (-1, 0, 0): {prob: 0.5, delay: 15-25, filter: logs,  priority: 10}
  Up     (0, 1, 0):  {prob: 0.9, delay: 0-5,   filter: any,   cancelOthers: true}
  Down   (0, -1, 0): {prob: 0.4, delay: 25-50, filter: solid, targetState: obsidian}

Each coordinate gets its own complete set of modifiers!
```

This is fundamentally different from a system where all targets share the same modifiers. Each entry in the 3D array is independently configured.

**Critical Design Principle 2**: Each modifier is optional. Implementations can use as few or as many as needed.

**Simple Usage** (minimal modifiers):
```java
// Just position and probability - everything else uses defaults
new SpreadTarget.Builder(targetPos)
    .probability(0.5f)
    .build()
```

**Moderate Usage** (a few modifiers):
```java
// Position, probability, delay, and a simple filter
new SpreadTarget.Builder(targetPos)
    .probability(0.7f)
    .minDelay(5)
    .maxDelay(20)
    .blockFilter(state -> !state.is(Blocks.BEDROCK))
    .build()
```

**Complex Usage** (many modifiers):
```java
// Full-featured spread with filtering, effects, conditions, and custom behavior
new SpreadTarget.Builder(targetPos)
    .probability(0.5f)
    .dynamicProbability((level, pos) -> {
        // Probability based on light level
        return level.getBrightness(LightLayer.BLOCK, pos) / 15.0f;
    })
    .minDelay(10)
    .maxDelay(50)
    .delayType(DelayType.TRIANGULAR)
    .blockFilter(state -> state.isAir() || state.getMaterial().isReplaceable())
    .blockedTag(BlockTags.LOGS)  // Never spread to logs
    .requiresLineOfSight(true)
    .targetState(Blocks.STONE.defaultBlockState())
    .onSpreadEffect((level, pos) -> {
        level.addParticle(ParticleTypes.SMOKE, pos.getX(), pos.getY(), pos.getZ(), 0, 0.1, 0);
    })
    .particles(ParticleTypes.FLAME)
    .sound(SoundEvents.FIRE_AMBIENT, 1.0f, 1.0f)
    .priority(10)
    .metadata(Map.of("reason", "complex_spread", "timestamp", System.currentTimeMillis()))
    .build()
```

**Modifier Categories**:

1. **Core Modifiers** (used by most blocks):
   - Position
   - Probability
   - Delay range

2. **Filtering Modifiers** (control where spread can occur):
   - Block filters (predicates)
   - Allowed/blocked block lists
   - Tag-based filtering
   - Condition checks (line of sight, air, solid, replaceable)

3. **Timing Modifiers** (control when spread occurs):
   - Min/max delay
   - Delay distribution type
   - Custom delay functions

4. **State Modifiers** (control what gets placed):
   - Target BlockState
   - Property overrides

5. **Effect Modifiers** (visual/audio feedback):
   - Particles
   - Sounds
   - Custom callbacks

6. **Control Flow Modifiers** (affect spread logic):
   - Priority
   - Cancel-others-on-success
   - Dynamic probability

**Implementation Notes**:
- Use builder pattern for clean construction
- All modifiers optional except position
- Sensible defaults for all fields
- No performance penalty for unused modifiers (only check if non-null)
- Blocks can mix and match modifiers as needed

**Example Modifier Combinations by Block**:

- **RestorerBlock**: Position, probability (0.5), delay range, block filter (no bedrock/barrier)
- **WallBlock**: Position, probability with distance falloff, delay range, blocked tags
- **RapidWaterEaterBlock**: Position, probability (1.0), delay range, block filter (water/lava only)
- **GreyGooBlock**: Position, distance falloff, delay, particles, sound effects
- **CleanerBlock**: Position, priority, cancel-others, visual effects

public class BatchSpreadManager {
    /**
     * Process all spread targets for a block in one batch
     * - Queries pristine states in batch (if needed)
     * - Applies probability checks
     * - Schedules independent ticks with delays
     */
    public static void processBatchSpread(ISpreadable block,
                                         Level level,
                                         BlockPos pos,
                                         BlockState state) {
        List<SpreadTarget> targets = block.getSpreadTargets(level, pos, state);

        // Batch query pristine states if needed (RestorerBlock)
        Map<BlockPos, BlockState> pristineStates = batchQueryPristine(level, targets);

        // Process each target independently
        for (SpreadTarget target : targets) {
            if (random.nextFloat() < target.probability) {
                if (block.canSpreadTo(level, pos, state, target.position,
                                     level.getBlockState(target.position))) {
                    // Spread with randomized delay
                    int delay = random.nextInt(target.maxDelay - target.minDelay) + target.minDelay;
                    level.scheduleTick(target.position, block, delay);
                    block.performSpread(level, pos, target.position, target);
                }
            }
        }
    }
}
```

### Benefits

1. **Performance**: Batch pristine queries for RestorerBlock (single dimension access)
2. **Flexibility**: Easy to create new spread patterns per block type
3. **Maintainability**: Single spread system instead of duplicated logic
4. **Extensibility**: New goo blocks just implement ISpreadable
5. **Temporal Independence**: Each spread still gets its own randomized delay

### Example Usage

**RestorerBlock** (current behavior - uniform modifiers):
```java
@Override
public List<SpreadTarget> getSpreadTargets(Level level, BlockPos pos, BlockState state) {
    List<SpreadTarget> targets = new ArrayList<>();
    // 6 adjacent blocks, 50% probability each
    for (Direction dir : Direction.values()) {
        BlockPos target = pos.relative(dir);
        // Each coordinate gets the same modifiers in this case
        targets.add(new SpreadTarget.Builder(target)
            .probability(0.5f)
            .minDelay(0)
            .maxDelay(34)
            .build());
    }
    return targets;
}
```

**RestorerBlock** (with directional bias - different modifiers per direction):
```java
@Override
public List<SpreadTarget> getSpreadTargets(Level level, BlockPos pos, BlockState state) {
    List<SpreadTarget> targets = new ArrayList<>();

    // Upward spread - higher priority, faster
    targets.add(new SpreadTarget.Builder(pos.above())
        .probability(0.7f)  // 70% chance
        .minDelay(0)
        .maxDelay(20)       // Faster
        .priority(10)
        .build());

    // Horizontal spread - normal
    for (Direction dir : Direction.Plane.HORIZONTAL) {
        targets.add(new SpreadTarget.Builder(pos.relative(dir))
            .probability(0.5f)  // 50% chance
            .minDelay(0)
            .maxDelay(34)
            .priority(5)
            .build());
    }

    // Downward spread - lower priority, slower, with particles
    targets.add(new SpreadTarget.Builder(pos.below())
        .probability(0.3f)  // 30% chance
        .minDelay(10)
        .maxDelay(50)       // Slower
        .priority(1)
        .particles(ParticleTypes.FALLING_DUST)
        .build());

    return targets;
}
```

**WallBlock** (cubic spread with per-coordinate modifiers):
```java
@Override
public List<SpreadTarget> getSpreadTargets(Level level, BlockPos pos, BlockState state) {
    List<SpreadTarget> targets = new ArrayList<>();
    // 3x3x3 cube - each coordinate gets unique modifiers based on position
    for (int dx = -1; dx <= 1; dx++) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dy == 0 && dz == 0) continue;
                BlockPos target = pos.offset(dx, dy, dz);

                // Calculate distance for this specific coordinate
                int distance = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                float probability = 1.0f / distance; // Closer = higher probability

                // Calculate delay for this specific coordinate
                int minDelay = distance * 5;     // Further = longer delay
                int maxDelay = distance * 15;

                // Different particle effects based on direction
                ParticleOptions particles = null;
                if (dy > 0) particles = ParticleTypes.FLAME;        // Upward
                else if (dy < 0) particles = ParticleTypes.SMOKE;   // Downward
                else particles = ParticleTypes.CLOUD;               // Horizontal

                // Each coordinate in the 3D array gets its own unique modifiers
                targets.add(new SpreadTarget.Builder(target)
                    .probability(probability)
                    .minDelay(minDelay)
                    .maxDelay(maxDelay)
                    .particles(particles)
                    .priority(4 - distance)  // Closer = higher priority
                    .build());
            }
        }
    }
    return targets;
}
```

**Advanced Goo Block** (hypothetical - extreme per-coordinate customization):
```java
@Override
public List<SpreadTarget> getSpreadTargets(Level level, BlockPos pos, BlockState state) {
    List<SpreadTarget> targets = new ArrayList<>();

    // North: Fast, high probability, only to stone
    targets.add(new SpreadTarget.Builder(pos.north())
        .probability(0.9f)
        .minDelay(1).maxDelay(5)
        .blockFilter(state -> state.is(Blocks.STONE))
        .particles(ParticleTypes.FLAME)
        .sound(SoundEvents.FIRE_AMBIENT, 1.0f, 1.0f)
        .build());

    // South: Slow, low probability, any replaceable, with line of sight
    targets.add(new SpreadTarget.Builder(pos.south())
        .probability(0.2f)
        .minDelay(20).maxDelay(50)
        .requiresReplaceable(true)
        .requiresLineOfSight(true)
        .particles(ParticleTypes.SMOKE)
        .build());

    // East: Medium speed, tag-based filter, custom callback
    targets.add(new SpreadTarget.Builder(pos.east())
        .probability(0.6f)
        .minDelay(5).maxDelay(15)
        .blockedTag(BlockTags.LOGS)
        .onSpreadEffect((lvl, p) -> lvl.explode(null, p.getX(), p.getY(), p.getZ(), 1.0f, false))
        .build());

    // West: Dynamic probability based on light level
    targets.add(new SpreadTarget.Builder(pos.west())
        .dynamicProbability((lvl, p) -> lvl.getBrightness(LightLayer.BLOCK, p) / 15.0f)
        .minDelay(10).maxDelay(20)
        .build());

    // Up: High priority, cancels others on success
    targets.add(new SpreadTarget.Builder(pos.above())
        .probability(0.5f)
        .minDelay(0).maxDelay(10)
        .priority(100)
        .cancelOthersOnSuccess(true)
        .build());

    // Down: Complex filtering and state overrides
    targets.add(new SpreadTarget.Builder(pos.below())
        .probability(0.4f)
        .blockFilter(state -> state.isAir() || state.getMaterial().isLiquid())
        .targetState(Blocks.OBSIDIAN.defaultBlockState())
        .particles(ParticleTypes.LAVA)
        .sound(SoundEvents.LAVA_POP, 0.5f, 2.0f)
        .build());

    return targets;
}
```

This example shows each of the 6 directions having completely different modifiers - demonstrating the full flexibility of per-coordinate customization.

**RapidWaterEaterBlock** (staged spread):
```java
@Override
public List<SpreadTarget> getSpreadTargets(Level level, BlockPos pos, BlockState state) {
    List<SpreadTarget> targets = new ArrayList<>();
    int stage = state.getValue(STAGE);

    // Only spread to water/lava blocks
    for (int dx = -1; dx <= 1; dx++) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dy == 0 && dz == 0) continue;
                BlockPos target = pos.offset(dx, dy, dz);
                BlockState targetState = level.getBlockState(target);

                if (targetState.getFluidState().is(FluidTags.WATER) ||
                    targetState.getFluidState().is(FluidTags.LAVA)) {
                    targets.add(new SpreadTarget(target, 1.0f, 0, 29)); // Always spread to fluids
                }
            }
        }
    }
    return targets;
}
```

### Implementation Phases

1. **Phase 1**: Design and implement ISpreadable interface and SpreadTarget class
2. **Phase 2**: Implement BatchSpreadManager with batch pristine query support
3. **Phase 3**: Refactor RestorerBlock to use new system (verify behavior matches)
4. **Phase 4**: Refactor other goo blocks to use new system
5. **Phase 5**: Add advanced features (directional bias, custom spread patterns)

### Compatibility Notes

- Must maintain current behavior exactly for RestorerBlock
- Temporal independence critical - don't synchronize spread delays
- Each block still gets independent random ticks
- Batch optimization only for queries, not for scheduling

### Performance Estimate

For RestorerBlock querying 6 adjacent blocks:
- **Current**: 6 individual pristine queries (potential 6 chunk loads)
- **With Batch**: 1 batch query (worst case 6 chunk loads, but likely 1-2 due to spatial locality)
- **Expected Gain**: 20-30% improvement due to reduced dimension access overhead

For WallBlock with 26-block cubic spread:
- **Current**: 26 individual checks
- **With Batch**: 1 batch operation
- **Expected Gain**: More significant, especially if checking pristine states

### Related Files

- RestorerBlock.java - Primary beneficiary due to pristine queries
- GreyGooBlock.java - Cubic spread pattern (3x3x3)
- WallBlock.java - Cubic spread pattern (3x3x3)
- RapidWaterEaterBlock.java - Cubic fluid targeting (3x3x3)
- CleanerBlock.java - Adjacent spread (6 directions)
- PristineChunkGenerator.java - Would need batch query method added

### Design Philosophy: Maximum Flexibility, Minimal Requirement

**Key Insight**: The modifier system must support:
- **Minimal implementations**: Just position + probability
- **Moderate implementations**: A handful of modifiers for specific needs
- **Complex implementations**: Full suite of modifiers for advanced behaviors

**No All-or-Nothing**: Blocks should never be forced to implement modifiers they don't need. The system should gracefully handle anywhere from 1-2 modifiers to dozens of modifiers per spread target.

**Extensibility**: New modifiers can be added to SpreadTarget without breaking existing implementations (use sensible defaults or null checks).

**Examples of Flexibility**:
- RestorerBlock might use 3-4 modifiers (position, probability, delay, basic filter)
- WallBlock might use 5-6 modifiers (position, probability, distance falloff, delay, tag filters)
- A future advanced goo block might use 15+ modifiers (all the filtering, effects, conditions, etc.)

### References

- User's original idea: "we could probably generalize it to all the blocks that spread and pass it a sort of 3d array of potental spread targets, each with a probability associated with it. thats actually a dang good idea and could allow for a lot of flexibiliy."
- User confirmed temporal independence is important: "unless we want we want each spread to be independent, temporally, unless your idea can support that."
- User emphasized this is a separate project: "That will be a seperate thing entirely."
- User clarified modifier flexibility: "each block in the array would have a variety of modifiers, ranging from probability, block filters, variable delays, etc. There is a ton of flexibility there and any system we implement would need to be able to only implement one or two of those, or a great many of them."

---

## Other Future Ideas

### Restorer Block Enhancements

**NBT Data Restoration**:
- Copy tile entity data from backup dimension
- Restore chest contents, sign text, etc.
- Implementation: Query BlockEntity from backup, copy NBT

**Tag-Based Blacklist**:
- Forge tag: `#greygoo:restorer_blacklist`
- Allow players/modpack creators to protect specific blocks
- Implementation: Check tag before spreading/restoring

**Visual Feedback**:
- Particles during restoration
- Sound effects for state transitions
- Different textures per restoration state

### Performance Optimizations

**Async Pristine Queries**:
- Query pristine states off main thread
- Cache results for next tick
- Requires careful thread safety

**Predictive Chunk Loading**:
- Pre-load chunks around active restorers
- Use spatial locality prediction
- Trade memory for latency reduction

**BlockState-Level Cache**:
- Cache individual block states, not just chunks
- Higher hit rate for repeated position queries
- More memory overhead but better granularity

---

**Last Updated**: 2026-01-15
