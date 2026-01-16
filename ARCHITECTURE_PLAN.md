# Grey Goo Mod - Modernized Architecture Plan

## Executive Summary

This document outlines the architecture for reimplementing the Grey Goo mod for Minecraft 1.20.1 with modern coding practices, reduced code duplication, and improved modularity.

### Design Decisions (Confirmed)

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Behavior authenticity | **Exact original reproduction** | Match original mod behavior precisely |
| Visual effects | Keep original minimal | Focus on behavior first, visuals later |
| Color variant activation | Per-block | More intuitive than global flags |
| Spread limiter | Per-goo-type | Maximum configurability |
| Destroyer entities | Blocks only, no golems | Simplify scope |
| Starvation behavior | Preserve exact original | Gameplay-critical (GreyGoo dies, Destroyer→Cancer2) |
| Protected blocks | Hardcoded + config + tags | Maximum flexibility |
| Cleaner immunity | Match original exceptions | Wall/Inert special cases |
| Rapid depth limits | Per-type decision | Match original per block |
| Goo-vs-goo combat | First-to-tick wins | Creates competitive dynamics |
| Wall/Inert spreading | Require foundation | Match original foundation mechanics |
| Interaction registry | Config-driven JSON | Modpack-friendly, hot-reloadable |

---

## IMPORTANT: Authenticity Policy

**Default Rule:** All goo blocks must reproduce the **exact behavior** from the original OldGreyGoo mod unless explicitly specified otherwise.

**Exceptions (Keep Current Behavior):**
| Block | Reason |
|-------|--------|
| RedyellowBlock | User prefers current 1.20.1 implementation over original |
| (Other yellow variants) | Same - current behavior preferred |

When implementing new blocks, always refer to the original source in `C:\Users\steve\IdeaProjects\OldGreyGoo` and match behavior precisely including:
- Exact search radius and patterns
- Exact timing delays
- Exact interaction rules
- Exact starvation/transformation behavior
- Exact mutation chances (e.g., 1/10000 for GreyEater → Cancer)

---

## Part 1: Block Type Hierarchy

### Current Problem
Each goo block duplicates the same patterns:
- Cleaner detection logic
- Spread limiter checks (missing entirely)
- Protected block checks
- Server-side guards

### Proposed Class Hierarchy

```
Block (Minecraft)
    │
    └── AbstractGooBlock (base class)
            │
            ├── RandomTickGooBlock (spreads via randomTick)
            │       ├── GreyGooBlock
            │       ├── AirEaterBlock
            │       ├── WaterEaterBlock
            │       ├── MinerGooBlock
            │       ├── CleanerBlock
            │       ├── WallBlock (foundation-required)
            │       ├── GravityGooBlock
            │       ├── BlackDestroyerBlock
            │       ├── CancerBlock
            │       ├── GreyEaterBlock
            │       └── TGDBlock
            │
            ├── ScheduledTickGooBlock (spreads via scheduleTick)
            │       ├── RapidEaterBlock
            │       ├── RapidWaterEaterBlock (depth-limited)
            │       ├── RapidMinerBlock
            │       ├── FreezerBlock
            │       └── RestorerBlock
            │
            ├── ActivatedGooBlock (per-block activation, scheduled tick)
            │       ├── OrangeRedBlock (foundation-required)
            │       ├── OrangeWhiteBlock (foundation-required)
            │       └── OrangePurpleBlock (foundation-required, removes behind)
            │
            └── InertGooBlock (no spreading)
                    ├── InertBlock
                    ├── Cancer2Block
                    └── TGDInertBlock
```

### AbstractGooBlock - Base Class Design

```java
public abstract class AbstractGooBlock extends Block {

    // Identity
    public abstract GooType getGooType();

    // Shared behavior - uses GooInteractionRegistry
    protected final GooInteraction getInteractionWith(ServerLevel level, BlockPos otherPos) {
        Block other = level.getBlockState(otherPos).getBlock();
        return GooInteractionRegistry.get().getInteraction(getGooType(), other);
    }

    // Handle interaction result
    protected final boolean handleInteraction(ServerLevel level, BlockPos selfPos,
                                               BlockPos otherPos, GooInteraction interaction) {
        switch (interaction) {
            case CONVERT_SELF -> {
                Block other = level.getBlockState(otherPos).getBlock();
                level.setBlockAndUpdate(selfPos, other.defaultBlockState());
                return true;  // Stop processing
            }
            case CONVERT_OTHER -> {
                level.setBlockAndUpdate(otherPos, this.defaultBlockState());
                return false;  // Continue (found food)
            }
            case IGNORE -> { return false; }  // Skip, continue searching
            case REQUIRE_ADJACENT -> { /* special handling */ }
            default -> { return false; }
        }
        return false;
    }

    // Spread limiter integration
    protected final boolean canSpread(ServerLevel level, RandomSource random) {
        return SpreadLimiterManager.get().canSpread(getGooType(), random);
    }

    protected final void recordSpread() {
        SpreadLimiterManager.get().recordSpread(getGooType());
    }

    // Protected block check (uses tags + config + hardcoded)
    protected final boolean isProtectedBlock(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        // 1. Check hardcoded list (bedrock, chests)
        if (ProtectedBlocks.HARDCODED.contains(state.getBlock())) return true;
        // 2. Check tag
        if (state.is(GreyGooTags.Blocks.GOO_PROTECTED)) return true;
        // 3. Check config list
        if (ProtectedBlocks.fromConfig().contains(state.getBlock())) return true;
        return false;
    }

    // Starvation handling - override in subclasses
    protected abstract void onStarve(ServerLevel level, BlockPos pos, BlockState state);

    // Template method - implement in subclasses
    protected abstract void doSpread(ServerLevel level, BlockPos pos, RandomSource random);
}
```

### RandomTickGooBlock

```java
public abstract class RandomTickGooBlock extends AbstractGooBlock {

    public RandomTickGooBlock(Properties props) {
        super(props.randomTicks());
    }

    @Override
    public final void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canSpread(level, random)) return;

        // Check all neighbors for interactions FIRST
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.relative(dir);
            GooInteraction interaction = getInteractionWith(level, neighbor);

            // CONVERT_SELF is highest priority (cleaner infection)
            if (interaction == GooInteraction.CONVERT_SELF) {
                handleInteraction(level, pos, neighbor, interaction);
                return;  // We're gone, stop
            }
        }

        // Delegate spreading logic to subclass
        doSpread(level, pos, random);
        recordSpread();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                  Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            doSpread((ServerLevel) level, pos, level.getRandom());
        }
        return InteractionResult.SUCCESS;
    }
}
```

### ScheduledTickGooBlock

```java
public abstract class ScheduledTickGooBlock extends AbstractGooBlock {

    protected abstract int getMinDelay();
    protected abstract int getDelayVariance();

    // Optional depth limiting (for RapidWaterEater style)
    protected int getMaxDepth() { return -1; }  // -1 = no limit

    public ScheduledTickGooBlock(Properties props) {
        super(props);  // No randomTicks()
    }

    @Override
    public final void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canSpread(level, random)) {
            scheduleNextTick(level, pos);  // Try again later
            return;
        }

        // Check cleaner infection first
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.relative(dir);
            GooInteraction interaction = getInteractionWith(level, neighbor);
            if (interaction == GooInteraction.CONVERT_SELF) {
                handleInteraction(level, pos, neighbor, interaction);
                return;
            }
        }

        doSpread(level, pos, random);
        recordSpread();
    }

    protected void scheduleNextTick(Level level, BlockPos pos) {
        int delay = getMinDelay() + level.getRandom().nextInt(getDelayVariance() + 1);
        level.scheduleTick(pos, this, delay);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
            scheduleNextTick(level, pos);
        }
    }
}
```

### ActivatedGooBlock (Color Variants with Per-Block Activation)

```java
public abstract class ActivatedGooBlock extends ScheduledTickGooBlock {

    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

    public ActivatedGooBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(ACTIVATED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    @Override
    public final void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Only spread if activated
        if (!state.getValue(ACTIVATED)) return;
        super.tick(state, level, pos, random);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                  Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && !state.getValue(ACTIVATED)) {
            // Activate on right-click
            level.setBlockAndUpdate(pos, state.setValue(ACTIVATED, true));
            scheduleNextTick(level, pos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    // Foundation requirement - must have adjacent same-type or Inert
    protected boolean hasFoundation(ServerLevel level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
            if (neighbor == this || neighbor == GreyGooMod.INERT_BLOCK.get()) {
                return true;
            }
        }
        return false;
    }
}
```

---

## Part 2: Spread Limiter System (Per-Goo-Type)

### Design

Since we chose per-goo-type limits, each `GooType` has its own configurable rate limit.

```java
public class SpreadLimiterManager {
    private static SpreadLimiterManager INSTANCE;

    // Per-goo-type counters (reset each server tick)
    private final Map<GooType, AtomicInteger> counters = new EnumMap<>(GooType.class);

    // Loaded from config
    private final Map<GooType, LimiterConfig> configs;

    public static class LimiterConfig {
        public int maxPerTick = 100;      // Hard cap
        public int softCapStart = 50;     // When throttling begins
        public float throttleScale = 0.8f; // How aggressive throttling is
    }

    // Global limits (in addition to per-type)
    private int globalSoftCap = 300;
    private int globalHardCap = 600;
    private final AtomicInteger globalCounter = new AtomicInteger(0);

    public boolean canSpread(GooType type, RandomSource random) {
        // 1. Check global hard cap
        int globalCount = globalCounter.get();
        if (globalCount >= globalHardCap) return false;

        // 2. Global soft cap throttling
        if (globalCount >= globalSoftCap) {
            float ratio = (float)(globalCount - globalSoftCap) / (globalHardCap - globalSoftCap);
            if (random.nextFloat() >= (1.0f - ratio * 0.5f)) return false;
        }

        // 3. Per-type hard cap
        LimiterConfig config = configs.get(type);
        int typeCount = counters.get(type).get();
        if (typeCount >= config.maxPerTick) return false;

        // 4. Per-type soft cap throttling
        if (typeCount >= config.softCapStart) {
            float ratio = (float)(typeCount - config.softCapStart) /
                         (config.maxPerTick - config.softCapStart);
            if (random.nextFloat() >= (1.0f - ratio * config.throttleScale)) return false;
        }

        return true;
    }

    public void recordSpread(GooType type) {
        counters.get(type).incrementAndGet();
        globalCounter.incrementAndGet();
    }

    // Called each server tick via Forge event
    public void resetCounters() {
        counters.values().forEach(c -> c.set(0));
        globalCounter.set(0);
    }
}
```

### Default Limits Per Goo Type

```java
public enum GooType {
    // Consumer goos
    GREY_GOO("grey_goo", 100, 50),
    AIR_EATER("air_eater", 100, 50),
    WATER_EATER("water_eater", 100, 50),
    MINER_GOO("miner_goo", 100, 50),
    GREY_EATER("grey_eater", 100, 50),

    // Rapid variants (higher limits due to self-limiting depth)
    RAPID_EATER("rapid_eater", 150, 75),
    RAPID_WATER_EATER("rapid_water_eater", 150, 75),
    RAPID_MINER("rapid_miner", 150, 75),

    // Defensive (lower priority, fewer spreads needed)
    CLEANER("cleaner", 80, 40),
    WALL("wall", 60, 30),
    INERT("inert", 0, 0),  // Never spreads
    RESTORER("restorer", 100, 60),
    FREEZER("freezer", 50, 25),

    // Destroyers (separate limits, aggressive)
    BLACK_DESTROYER("black_destroyer", 70, 35),
    CANCER("cancer", 70, 35),
    CANCER2("cancer2", 0, 0),  // Inert
    TGD("tgd", 50, 25),
    TGD_INERT("tgd_inert", 0, 0),  // Inert

    // Gravity (special limiter for falling)
    GRAVITY_GOO("gravity_goo", 25, 15),

    // Color variants
    ORANGE_RED("orange_red", 80, 40),
    ORANGE_WHITE("orange_white", 80, 40),
    ORANGE_PURPLE("orange_purple", 80, 40);

    public final String id;
    public final int defaultMax;
    public final int defaultSoftCap;
}
```

---

## Part 3: Goo Interaction System (JSON Config)

### GooInteraction Enum

```java
public enum GooInteraction {
    IGNORE,           // Do nothing with this neighbor
    CONVERT_SELF,     // This goo becomes the other (e.g., cleaner infection)
    CONVERT_OTHER,    // Convert neighbor to this goo type
    SPREAD_INTO,      // Normal spreading (convert non-goo to goo)
    REQUIRE_FOUNDATION, // Can only spread if neighbor is same type or Inert
    MUTUAL_IGNORE,    // Both goos ignore each other (defensive blocks)
    STARVE_CHECK      // Mark as "has no food" - triggers starvation if all neighbors return this
}
```

### JSON Configuration File

Located at: `config/greygoo/interactions.json`

```json
{
    "_comment": "Goo interaction matrix. Format: 'self_type': { 'other_type': 'INTERACTION' }",
    "_default": "SPREAD_INTO for non-goo blocks, IGNORE for same type",

    "cleaner": {
        "_description": "Cleaner spreads into ALL goos, converting them",
        "grey_goo": "CONVERT_OTHER",
        "air_eater": "CONVERT_OTHER",
        "water_eater": "CONVERT_OTHER",
        "miner_goo": "CONVERT_OTHER",
        "black_destroyer": "CONVERT_OTHER",
        "cancer": "CONVERT_OTHER",
        "tgd": "CONVERT_OTHER",
        "gravity_goo": "CONVERT_OTHER",
        "restorer": "CONVERT_OTHER",
        "rapid_eater": "CONVERT_OTHER",
        "rapid_water_eater": "CONVERT_OTHER",
        "orange_red": "CONVERT_OTHER",
        "orange_white": "CONVERT_OTHER",
        "orange_purple": "CONVERT_OTHER",
        "wall": "CONVERT_OTHER",
        "inert": "IGNORE",
        "cleaner": "IGNORE"
    },

    "grey_goo": {
        "_description": "Basic consumer - infected by cleaner, ignores defensive blocks",
        "cleaner": "CONVERT_SELF",
        "wall": "IGNORE",
        "inert": "IGNORE",
        "grey_goo": "IGNORE"
    },

    "air_eater": {
        "cleaner": "CONVERT_SELF",
        "wall": "IGNORE",
        "inert": "IGNORE"
    },

    "water_eater": {
        "cleaner": "CONVERT_SELF",
        "wall": "IGNORE",
        "inert": "IGNORE"
    },

    "black_destroyer": {
        "_description": "Aggressive - converts most goos, immune to some",
        "cleaner": "CONVERT_SELF",
        "grey_goo": "CONVERT_OTHER",
        "air_eater": "CONVERT_OTHER",
        "water_eater": "CONVERT_OTHER",
        "miner_goo": "CONVERT_OTHER",
        "cancer": "IGNORE",
        "cancer2": "IGNORE",
        "tgd": "IGNORE",
        "tgd_inert": "IGNORE",
        "wall": "IGNORE",
        "inert": "IGNORE",
        "freezer": "IGNORE"
    },

    "tgd": {
        "_description": "Tower builder - special exclusions",
        "cleaner": "CONVERT_SELF",
        "grey_goo": "CONVERT_OTHER",
        "orange_red": "IGNORE",
        "black_destroyer": "IGNORE",
        "wall": "IGNORE",
        "inert": "IGNORE",
        "freezer": "IGNORE",
        "grey_eater": "IGNORE",
        "tgd_inert": "IGNORE"
    },

    "wall": {
        "_description": "Defensive - requires foundation to spread",
        "cleaner": "CONVERT_SELF",
        "wall": "REQUIRE_FOUNDATION",
        "inert": "REQUIRE_FOUNDATION",
        "_default": "IGNORE"
    },

    "inert": {
        "_description": "Completely passive - never spreads",
        "_default": "IGNORE"
    },

    "restorer": {
        "_description": "Restoration goo - converts other goos back to original terrain",
        "cleaner": "CONVERT_SELF",
        "grey_goo": "CONVERT_OTHER",
        "black_destroyer": "CONVERT_OTHER",
        "tgd": "CONVERT_OTHER",
        "wall": "IGNORE",
        "inert": "IGNORE"
    },

    "orange_red": {
        "_description": "Color variant - needs foundation, per-block activation",
        "cleaner": "CONVERT_SELF",
        "orange_red": "REQUIRE_FOUNDATION",
        "inert": "REQUIRE_FOUNDATION",
        "_default": "IGNORE"
    },

    "orange_white": {
        "cleaner": "CONVERT_SELF",
        "orange_white": "REQUIRE_FOUNDATION",
        "inert": "REQUIRE_FOUNDATION",
        "_default": "IGNORE"
    },

    "orange_purple": {
        "_description": "Deletes blocks behind as it spreads",
        "cleaner": "CONVERT_SELF",
        "orange_purple": "REQUIRE_FOUNDATION",
        "inert": "REQUIRE_FOUNDATION",
        "_default": "IGNORE"
    }
}
```

### GooInteractionRegistry Implementation

```java
public class GooInteractionRegistry {
    private static GooInteractionRegistry INSTANCE;

    private final Map<GooType, Map<GooType, GooInteraction>> gooToGoo = new EnumMap<>(GooType.class);
    private final Map<GooType, GooInteraction> defaults = new EnumMap<>(GooType.class);

    public static void loadFromConfig(Path configPath) {
        // Parse JSON, populate maps
        // Use Gson or similar
    }

    public GooInteraction getInteraction(GooType self, Block other) {
        // Check if other is a goo block
        GooType otherType = GooType.fromBlock(other);

        if (otherType != null) {
            // Goo-to-goo interaction
            Map<GooType, GooInteraction> selfInteractions = gooToGoo.get(self);
            if (selfInteractions != null && selfInteractions.containsKey(otherType)) {
                return selfInteractions.get(otherType);
            }
            // Check for _default
            return defaults.getOrDefault(self, GooInteraction.IGNORE);
        }

        // Non-goo block - standard spreading rules apply
        return GooInteraction.SPREAD_INTO;
    }

    // Hot-reload support
    public void reload() {
        loadFromConfig(getConfigPath());
    }
}
```

---

## Part 4: Starvation Behavior (Preserve Original)

### Starvation Rules by Goo Type

| Goo Type | On Starvation | Reason |
|----------|---------------|--------|
| GreyGoo | **Dies** (destroyBlock) | Gameplay-critical |
| AirEater | Dies | Matches original |
| WaterEater | Dies | Matches original |
| MinerGoo | Dies | Matches original |
| BlackDestroyer | **Transforms → Cancer2** | Gameplay-critical |
| Cancer | Dies | Matches original |
| TGD | **Transforms → TGDInert** | Gameplay-critical |
| GravityGoo | Dies | Matches original |
| GreyEater | Dies | Matches original |
| Cleaner | Dies (converts to air) | Original behavior |
| Wall | Persists | Defensive block |
| Inert | Persists | Foundation block |
| Restorer | Dies | Matches original |
| RapidEater | Dies | Matches original |
| RapidWaterEater | Dies | Matches original |
| ColorVariants | Persists | Defensive nature |

### Implementation

```java
// In GreyGooBlock
@Override
protected void onStarve(ServerLevel level, BlockPos pos, BlockState state) {
    level.destroyBlock(pos, false);  // Die
}

// In BlackDestroyerBlock
@Override
protected void onStarve(ServerLevel level, BlockPos pos, BlockState state) {
    // Transform to Cancer2 (inert form)
    level.setBlockAndUpdate(pos, GreyGooMod.CANCER2_BLOCK.get().defaultBlockState());
}

// In TGDBlock
@Override
protected void onStarve(ServerLevel level, BlockPos pos, BlockState state) {
    // Transform to TGDInert
    level.setBlockAndUpdate(pos, GreyGooMod.TGD_INERT_BLOCK.get().defaultBlockState());
}

// In WallBlock
@Override
protected void onStarve(ServerLevel level, BlockPos pos, BlockState state) {
    // Do nothing - walls persist
}
```

---

## Part 5: Protected Blocks System

### Three-Layer Protection

```java
public class ProtectedBlocks {

    // Layer 1: Hardcoded (never configurable)
    public static final Set<Block> HARDCODED = Set.of(
        Blocks.BEDROCK,
        Blocks.CHEST,
        Blocks.ENDER_CHEST,
        Blocks.TRAPPED_CHEST,
        Blocks.SHULKER_BOX  // All shulker variants
    );

    // Layer 2: Tag-based (greygoo:protected)
    // Defined in data/greygoo/tags/blocks/protected.json
    public static final TagKey<Block> TAG = TagKey.create(
        Registries.BLOCK,
        new ResourceLocation("greygoo", "protected")
    );

    // Layer 3: Config file additions
    private static Set<Block> configBlocks = Set.of();

    public static void loadFromConfig(List<String> blockIds) {
        configBlocks = blockIds.stream()
            .map(ResourceLocation::new)
            .map(ForgeRegistries.BLOCKS::getValue)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    public static boolean isProtected(BlockState state) {
        Block block = state.getBlock();
        return HARDCODED.contains(block)
            || state.is(TAG)
            || configBlocks.contains(block);
    }
}
```

### Default Protected Tag

`data/greygoo/tags/blocks/protected.json`:
```json
{
    "replace": false,
    "values": [
        "#minecraft:shulker_boxes",
        "minecraft:spawner",
        "minecraft:end_portal_frame",
        "minecraft:end_portal",
        "minecraft:nether_portal"
    ]
}
```

---

## Part 6: Depth-Limited Rapid Variants

### RapidWaterEaterBlock (Original Behavior - Depth Limited)

```java
public class RapidWaterEaterBlock extends ScheduledTickGooBlock {
    public static final IntegerProperty DEPTH = IntegerProperty.create("depth", 0, 50);

    @Override
    protected int getMaxDepth() { return 50; }  // Original limit

    @Override
    protected int getMinDelay() { return 25; }

    @Override
    protected int getDelayVariance() { return 4; }

    @Override
    protected void doSpread(ServerLevel level, BlockPos pos, RandomSource random) {
        int currentDepth = level.getBlockState(pos).getValue(DEPTH);

        // Check depth limit
        if (currentDepth >= getMaxDepth()) {
            level.removeBlock(pos, false);
            return;
        }

        boolean foundFood = false;

        // Search 3x3x3 cube (original behavior)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;

                    BlockPos target = pos.offset(dx, dy, dz);
                    BlockState targetState = level.getBlockState(target);

                    // Only eat water/lava
                    if (targetState.getFluidState().is(FluidTags.WATER) ||
                        targetState.getFluidState().is(FluidTags.LAVA)) {

                        // Spread with incremented depth
                        level.setBlockAndUpdate(target,
                            defaultBlockState().setValue(DEPTH, currentDepth + 1));
                        foundFood = true;
                    }
                }
            }
        }

        // Self-destruct after spreading
        level.removeBlock(pos, false);

        if (!foundFood) {
            onStarve(level, pos, level.getBlockState(pos));
        }
    }
}
```

### RapidEaterBlock (No Depth Limit - Original Behavior)

```java
public class RapidEaterBlock extends ScheduledTickGooBlock {

    @Override
    protected int getMaxDepth() { return -1; }  // No limit

    @Override
    protected void doSpread(ServerLevel level, BlockPos pos, RandomSource random) {
        // Spreads indefinitely until no food or limiter kicks in
        // ... similar logic without depth tracking
    }
}
```

---

## Part 7: Complete Goo Inventory

### Priority 1 - Core (Existing, Need Refactor)
| Block | Type | Depth Limit | Status |
|-------|------|-------------|--------|
| GreyGooBlock | RandomTick | - | Refactor to base class |
| CleanerBlock | RandomTick | - | Refactor to base class |
| AirEaterBlock | RandomTick | - | Refactor to base class |
| WaterEaterBlock | RandomTick | - | Refactor to base class |
| RapidWaterEaterBlock | Scheduled | 50 | Refactor to base class |
| GravityGooBlock | RandomTick | - | Refactor to base class |
| WallBlock | RandomTick | - | Add foundation req |
| RestorerBlock | Scheduled | - | Keep dimension system |

### Priority 2 - Missing Core
| Block | Type | Depth Limit | Notes |
|-------|------|-------------|-------|
| MinerGooBlock | RandomTick | - | Whitelist ores/minerals |
| RapidEaterBlock | Scheduled | None | Fast general consumer |
| RapidMinerBlock | Scheduled | None | Fast ore mining |
| InertBlock | Inert | - | Foundation, no spread |

### Priority 3 - Destroyer Series (No Entities)
| Block | Type | Notes |
|-------|------|-------|
| BlackDestroyerBlock | RandomTick | Aggressive, portal particles |
| CancerBlock | RandomTick | Mutation from GreyEater |
| Cancer2Block | Inert | Inert destroyer state |
| TGDBlock | RandomTick | Tower builder (no golem) |
| TGDInertBlock | Inert | Inert TGD state |

### Priority 4 - Special
| Block | Type | Notes |
|-------|------|-------|
| GreyEaterBlock | RandomTick | Color changes by consumed block |
| FreezerBlock | Scheduled | Freezes cleaners |
| BubbleBlock | RandomTick | White-green variant |

### Priority 5 - Color Variants (Per-Block Activation)
| Block | Type | Notes |
|-------|------|-------|
| OrangeRedBlock | Activated | Unbreakable, foundation req |
| OrangeWhiteBlock | Activated | Ladder behavior |
| OrangePurpleBlock | Activated | Deletes behind |

---

## Part 8: File Structure

```
src/main/java/com/stevenrs11/greygoo/
├── GreyGooMod.java                 # Main mod class, registry
├── GreyGooCreativeTabs.java        # Creative tab
│
├── core/
│   ├── AbstractGooBlock.java       # Base class with all shared logic
│   ├── RandomTickGooBlock.java     # Random tick spreading
│   ├── ScheduledTickGooBlock.java  # Scheduled tick spreading
│   ├── ActivatedGooBlock.java      # Per-block activation (color variants)
│   ├── InertGooBlock.java          # Non-spreading base
│   └── GooType.java                # Enum of all goo types
│
├── spread/
│   ├── SpreadLimiterManager.java   # Per-goo-type rate limiting
│   └── SpreadHelper.java           # Utility for finding blocks in radius
│
├── interaction/
│   ├── GooInteraction.java         # Interaction types enum
│   ├── GooInteractionRegistry.java # JSON-loaded interaction matrix
│   └── ProtectedBlocks.java        # Three-layer protection system
│
├── blocks/
│   ├── consumer/
│   │   ├── GreyGooBlock.java
│   │   ├── AirEaterBlock.java
│   │   ├── WaterEaterBlock.java
│   │   ├── MinerGooBlock.java
│   │   └── GreyEaterBlock.java
│   │
│   ├── rapid/
│   │   ├── RapidEaterBlock.java
│   │   ├── RapidWaterEaterBlock.java
│   │   └── RapidMinerBlock.java
│   │
│   ├── destroyer/
│   │   ├── BlackDestroyerBlock.java
│   │   ├── CancerBlock.java
│   │   ├── Cancer2Block.java
│   │   ├── TGDBlock.java
│   │   └── TGDInertBlock.java
│   │
│   ├── defensive/
│   │   ├── CleanerBlock.java
│   │   ├── WallBlock.java
│   │   ├── InertBlock.java
│   │   ├── FreezerBlock.java
│   │   └── RestorerBlock.java
│   │
│   ├── special/
│   │   ├── GravityGooBlock.java
│   │   └── BubbleBlock.java
│   │
│   └── color/
│       ├── OrangeRedBlock.java
│       ├── OrangeWhiteBlock.java
│       └── OrangePurpleBlock.java
│
├── config/
│   ├── GreyGooConfig.java          # Forge config integration
│   └── ConfigLoader.java           # JSON config loading
│
├── event/
│   └── ServerTickHandler.java      # Resets spread limiters each tick
│
├── tags/
│   └── GreyGooTags.java            # Tag key definitions
│
└── dimension/                       # Existing dimension system
    ├── DynamicDimensionManager.java
    └── PristineChunkGenerator.java
```

---

## Part 9: Implementation Roadmap

### Phase 1: Core Infrastructure
1. Create `core/` package with base classes
2. Implement `GooType` enum with all types
3. Implement `SpreadLimiterManager` with per-goo-type limits
4. Implement `GooInteractionRegistry` with JSON loading
5. Implement `ProtectedBlocks` with three-layer system
6. Add `ServerTickHandler` for limiter reset

### Phase 2: Refactor Existing Blocks
1. Migrate GreyGooBlock to RandomTickGooBlock
2. Migrate CleanerBlock to RandomTickGooBlock
3. Migrate AirEaterBlock, WaterEaterBlock
4. Migrate RapidWaterEaterBlock to ScheduledTickGooBlock
5. Update WallBlock with foundation requirement
6. Verify all interactions match original

### Phase 3: Add Missing Core Blocks
1. MinerGooBlock (whitelist-based)
2. RapidEaterBlock (no depth limit)
3. RapidMinerBlock
4. InertBlock (foundation)

### Phase 4: Destroyer Series
1. BlackDestroyerBlock with Cancer2 transformation
2. CancerBlock
3. Cancer2Block (inert)
4. TGDBlock with tower building (no golem)
5. TGDInertBlock

### Phase 5: Special & Color Variants
1. GreyEaterBlock with color metadata
2. FreezerBlock
3. OrangeRedBlock with per-block activation
4. OrangeWhiteBlock, OrangePurpleBlock

### Phase 6: Config & Polish
1. Create default interactions.json
2. Create default config values
3. Test interaction matrix thoroughly
4. Verify first-to-tick competitive behavior

---

## Appendix A: Goo-vs-Goo Competition

Since we chose "first-to-tick wins" for goo competition:

```java
// When a goo tries to spread into another goo (neither has special interaction)
// The spreading goo converts the target

// In doSpread():
GooInteraction interaction = getInteractionWith(level, targetPos);
if (interaction == GooInteraction.SPREAD_INTO) {
    // This includes other goos that aren't special-cased
    level.setBlockAndUpdate(targetPos, this.defaultBlockState());
    foundFood = true;
}
```

This means if GreyGoo and AirEater are adjacent:
- Whichever ticks first will convert the other
- Creates natural competition between goo types
- Cleaner still "wins" because all goos have CONVERT_SELF for cleaner

---

## Appendix B: Original Behaviors to Verify

**Blocks requiring EXACT original behavior reproduction:**

| Behavior | Original Code | Must Match? |
|----------|--------------|-------------|
| Cleaner radius 2 (Manhattan) | SpreadHelper with radius=2 | **Yes** |
| BlockBlack portal particles | randomDisplayTick | **Yes** |
| TGD tower height limits | bloomheight to 240 | **Yes** |
| GreyEater 1/10000 cancer chance | random check | **Yes** |
| WaterEater 3x3x3 search | Triple nested loops | **Yes** |
| Light levels per block | setLightLevel() | Nice to have |
| RapidWaterEater depth=50 limit | STAGE property | **Yes** |
| BlockWall foundation requirement | Adjacent Wall/Inert check | **Yes** |
| Destroyer → Cancer2 on starve | Transformation logic | **Yes** |
| TGD → TGDInert on starve | Transformation logic | **Yes** |

**Blocks with CUSTOM behavior (do NOT match original):**

| Block | Notes |
|-------|-------|
| RedyellowBlock | Keep current 1.20.1 implementation - user prefers it |
| Other yellow variants | Same - current behavior is acceptable |

---

## Appendix C: Configuration Files

### config/greygoo-common.toml (Forge Config)
```toml
[spread_limits]
    # Global limits across all goo types
    globalSoftCap = 300
    globalHardCap = 600

[spread_limits.grey_goo]
    maxPerTick = 100
    softCapStart = 50
    throttleScale = 0.8

# ... similar for each goo type

[protected_blocks]
    # Additional protected blocks (beyond hardcoded and tags)
    additional = ["minecraft:dragon_egg", "minecraft:beacon"]
```

### config/greygoo/interactions.json
(See Part 3 for full example)
