# Dynamic Dimension System - Technical Documentation

## Overview

This document describes the dynamic dimension creation system implemented for the Grey Goo mod's Restorer Block. The system creates a pristine backup dimension that mirrors the overworld's terrain generation, allowing blocks to be compared against their original generated state.

**Author**: Claude Code (Anthropic)
**Date**: 2026-01-15
**Minecraft Version**: 1.20.1 Forge
**Status**: Working and tested

---

## Table of Contents

1. [The Problem](#the-problem)
2. [Solution Evolution](#solution-evolution)
3. [Architecture Overview](#architecture-overview)
4. [Implementation Details](#implementation-details)
5. [Technical Deep Dive](#technical-deep-dive)
6. [Usage Guide](#usage-guide)
7. [Troubleshooting](#troubleshooting)
8. [Future Extensions](#future-extensions)

---

## The Problem

The Restorer Block needs to restore terrain to its original generated state. This requires knowing what blocks "should be" at any given position according to world generation.

### Initial Approaches (Failed)

**Approach 1: Recalculate Block States**
- **Idea**: Dynamically recalculate what blocks should be using noise generators
- **Problem**: Non-deterministic feature placement (trees, structures, ores)
- **Result**: ❌ Inconsistent restoration

**Approach 2: On-Demand Chunk Generation**
- **Idea**: Generate fresh chunks on-the-fly using NoiseBasedChunkGenerator
- **Implementation**: Used `generator.fillFromNoise()` and `createBiomes()`
- **Problem**: Each chunk took 5+ seconds to generate (timeout issues)
- **Result**: ❌ Server froze, unplayable

### Working Solution: Backup Dimension

**Approach 3: Dynamic Dimension with Mirrored Generation**
- **Idea**: Create a parallel dimension that mirrors overworld generation
- **Mechanism**: Copy overworld's LevelStem (DimensionType + ChunkGenerator)
- **Result**: ✅ Same seed, identical generation, MC handles everything

---

## Solution Evolution

### Why Dimensions Instead of Generation?

1. **Seed Synchronization**: Dimensions automatically use the same seed when copying the ChunkGenerator
2. **Structure Support**: Multi-chunk structures (villages, strongholds) generate correctly
3. **Feature Generation**: Trees, ores, caves all handled by vanilla systems
4. **Persistence**: Dimension saves/loads with world data
5. **Performance**: MC's optimized chunk loading vs our slow manual generation

### The JSON Dimension Problem

Initial research suggested using JSON dimension definitions:
```json
{
  "type": "minecraft:overworld",
  "generator": { /* copy overworld settings */ }
}
```

**Critical Issue**: JSON dimensions cannot dynamically inherit the world seed on a per-save basis ([source](https://forums.minecraftforge.net/topic/139721-1201-custom-seed-for-custom-dimensions/)).

> "If you wanted to do that, you would most likely need to remake the entire chunk generator to take in a specific seed."
> — Forge Forum Moderator

This led us to runtime dimension creation.

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                        Restorer Block                       │
│  Needs pristine block state at position (x, y, z)          │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                  PristineChunkGenerator                     │
│  • Lazily creates backup dimension on first use            │
│  • Queries backup dimension for block states               │
│  • Returns pristine BlockState                             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                 DynamicDimensionManager                     │
│  • Creates dimension at runtime                             │
│  • Copies overworld's LevelStem                            │
│  • Registers to server's dimension registry                │
│  • Handles all registration complexity                     │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                   Access Transformers                       │
│  • Exposes MinecraftServer private fields                  │
│  • Exposes RegistryAccess private fields                   │
│  • Required for dimension creation                         │
└─────────────────────────────────────────────────────────────┘
```

### Component Responsibilities

| Component | Responsibility | Key Methods |
|-----------|---------------|-------------|
| **RestorerBlock** | Restoration logic, spreading | `restore()`, `checkAndSpread()` |
| **PristineChunkGenerator** | Dimension management, block queries | `getPristineBlock()`, `initialize()` |
| **DynamicDimensionManager** | Runtime dimension creation | `getOrCreateLevel()` |
| **Access Transformers** | Field access via SRG names | N/A (build-time) |

---

## Implementation Details

### 1. Access Transformers

**File**: `src/main/resources/META-INF/accesstransformer.cfg`

```cfg
# Access Transformers for Grey Goo Mod
# Exposes MinecraftServer and registry fields needed for dynamic dimension creation

# MinecraftServer fields (using SRG names)
public net.minecraft.server.MinecraftServer f_129756_ # progressListenerFactory
public net.minecraft.server.MinecraftServer f_129738_ # executor
public net.minecraft.server.MinecraftServer f_129744_ # storageSource

# RegistryAccess fields for dimension registration (using SRG names)
public-f net.minecraft.core.RegistryAccess$ImmutableRegistryAccess f_206223_ # registries
```

**Why SRG Names?**
- Development environment uses readable names (`progressListenerFactory`)
- Production environment uses obfuscated names (`f_129756_`)
- SRG names are intermediate mappings that work in both environments
- Found by examining `build/fg_cache/.../srg_to_official_1.20.1.tsrg`

**Build Configuration** (`build.gradle`):
```gradle
minecraft {
    mappings channel: 'official', version: '1.20.1'
    accessTransformer = file('src/main/resources/META-INF/accesstransformer.cfg')
    runs { /* ... */ }
}
```

**Critical Note**: After adding/modifying AT files, you MUST:
1. Clean build cache: `./gradlew clean`
2. Rebuild project: `./gradlew build`
3. Refresh IDE project (IntelliJ: Gradle → Reload)

### 2. DynamicDimensionManager

**File**: `src/main/java/com/stevenrs11/greygoo/DynamicDimensionManager.java`

**Adapted From**: RFTools Dimensions by McJtyMods (used under MIT License)
**Original Source**: [Commoble's 1.16.4 Gist](https://gist.github.com/Commoble/7db2ef25f94952a4d2e2b7e3d4be53e0)

#### Key Method: `getOrCreateLevel()`

```java
public static ServerLevel getOrCreateLevel(
    MinecraftServer server,
    ResourceKey<Level> levelKey,
    BiFunction<MinecraftServer, ResourceKey<LevelStem>, LevelStem> dimensionFactory
)
```

**Process**:
1. Check if dimension already exists in `server.forgeGetWorldMap()`
2. If exists, return existing ServerLevel
3. If not, create new dimension:
   - Extract overworld's dimension registry
   - Clone the registry with new dimension added
   - Replace old registry with new one
   - Create ServerLevel instance with required parameters
   - Add world border listener
   - Register to server's world map
   - Mark worlds dirty (triggers save)
   - Fire LevelEvent.Load

#### ServerLevel Constructor Parameters

```java
ServerLevel newWorld = new ServerLevel(
    server,                          // MinecraftServer
    executor,                        // Executor (from AT)
    anvilConverter,                  // LevelStorageSource.LevelStorageAccess (from AT)
    derivedLevelData,                // ServerLevelData
    worldKey,                        // ResourceKey<Level>
    dimension,                       // LevelStem
    chunkProgressListener,           // ChunkProgressListener (from AT)
    false,                           // isDebug
    BiomeManager.obfuscateSeed(seed), // biomeZoomSeed
    ImmutableList.of(),              // specialSpawners (empty for custom dims)
    false,                           // tickTime (false for non-overworld)
    null                             // RandomSequences (1.20.1 param)
);
```

#### Registry Manipulation

The most complex part is modifying the dimension registry:

```java
// Get composite registry access
LayeredRegistryAccess<RegistryLayer> registries = server.registries();
RegistryAccess.ImmutableRegistryAccess composite =
    (RegistryAccess.ImmutableRegistryAccess) registries.compositeAccess();

// Clone registries map (requires AT to access private field)
Map<ResourceKey<? extends Registry<?>>, Registry<?>> regmap =
    new HashMap<>(composite.registries);

// Get the LevelStem registry
ResourceKey<? extends Registry<?>> key = ResourceKey.create(
    ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("root")),
    ResourceLocation.withDefaultNamespace("dimension")
);

MappedRegistry<LevelStem> oldRegistry = (MappedRegistry<LevelStem>) regmap.get(key);
MappedRegistry<LevelStem> newRegistry = new MappedRegistry<>(
    Registries.LEVEL_STEM,
    oldRegistry.registryLifecycle(),
    false
);

// Copy existing dimensions
for (var entry : oldRegistry.entrySet()) {
    Registry.register(newRegistry, entry.getKey(), entry.getValue());
}

// Add our new dimension
Registry.register(newRegistry, dimensionKey, dimension);

// Replace in map and update composite
regmap.replace(key, newRegistry);
composite.registries = (Map) regmap;  // Requires AT
```

### 3. PristineChunkGenerator

**File**: `src/main/java/com/stevenrs11/greygoo/PristineChunkGenerator.java`

#### Lazy Initialization

```java
public BlockState getPristineBlock(ServerLevel level, BlockPos pos) {
    // Lazy initialization - create backup dimension on first use
    if (!initialized) {
        initialize(level);
    }

    // Query backup dimension for pristine block state
    return backupDimension.getBlockState(pos);
}
```

#### Dimension Creation

```java
private void initialize(ServerLevel overworld) {
    // Get overworld's LevelStem
    LevelStem overworldStem = overworld.getServer().registries()
        .compositeAccess()
        .registryOrThrow(Registries.LEVEL_STEM)
        .get(ResourceKey.create(Registries.LEVEL_STEM, Level.OVERWORLD.location()));

    // Create backup dimension with EXACT same settings
    backupDimension = DynamicDimensionManager.getOrCreateLevel(
        overworld.getServer(),
        PRISTINE_BACKUP_KEY,
        (server, dimensionKey) -> new LevelStem(
            overworldStem.type(),        // Same DimensionType
            overworldStem.generator()    // Same ChunkGenerator (includes seed!)
        )
    );

    // Verify seeds match
    if (backupDimension.getSeed() != overworld.getSeed()) {
        LOGGER.warn("Seed mismatch!");
    }
}
```

#### Why This Works

**The Magic**: By copying the overworld's `ChunkGenerator`, we automatically get:
- Same world seed
- Same biome distribution
- Same noise settings
- Same structure placement
- Same feature generation

The `ChunkGenerator` encapsulates ALL world generation logic, so copying it gives us an exact duplicate.

### 4. RestorerBlock Integration

**File**: `src/main/java/com/stevenrs11/greygoo/RestorerBlock.java`

```java
private static final PristineChunkGenerator pristineGenerator =
    new PristineChunkGenerator();

private void checkAndSpread(ServerLevel level, BlockPos pos, RandomSource random) {
    for (Direction dir : Direction.values()) {
        BlockPos targetPos = pos.relative(dir);
        BlockState currentBlock = level.getBlockState(targetPos);

        // Query backup dimension for pristine state
        BlockState pristineBlock = pristineGenerator.getPristineBlock(level, targetPos);

        // Compare and restore if different
        if (!currentBlock.is(pristineBlock.getBlock())) {
            // Restoration logic...
        }
    }
}
```

---

## Technical Deep Dive

### How Seed Inheritance Works

When you copy a `ChunkGenerator`, you're copying the entire generation context:

```java
// NoiseBasedChunkGenerator contains:
class NoiseBasedChunkGenerator {
    private final BiomeSource biomeSource;
    private final Holder<NoiseGeneratorSettings> settings;
    // Seed is NOT stored here! It's in BiomeSource and RandomState
}

// BiomeSource contains the seed:
class MultiNoiseBiomeSource {
    private final long seed;  // World seed!
    // ...
}
```

The `RandomState` (created from the seed at world load) is used during generation:
```java
ChunkAccess chunk = generator.fillFromNoise(
    executor,
    blender,
    randomState,  // Contains seed-based random for this chunk
    structureManager,
    protoChunk
);
```

By reusing the overworld's `ChunkGenerator`, we automatically get the same `BiomeSource` with the same seed, ensuring identical generation.

### Dimension Persistence

Once created, the backup dimension is saved to disk:
```
saves/YourWorld/
├── level.dat
├── DIM1/              # The Nether
├── DIM-1/             # The End
├── greygoo_pristine_backup/  # Our backup dimension!
│   ├── region/
│   ├── data/
│   └── ...
├── dimensions.json    # Lists all registered dimensions
└── ...
```

On world reload, Minecraft automatically:
1. Reads `dimensions.json`
2. Loads all dimension definitions
3. Creates ServerLevel instances
4. Our `greygoo:pristine_backup` dimension just exists!

Our code only needs to:
```java
ServerLevel backup = server.getLevel(PRISTINE_BACKUP_KEY);
if (backup == null) {
    // First time - create it
    backup = DynamicDimensionManager.getOrCreateLevel(...);
}
```

### Why Access Transformers Instead of Reflection?

**Reflection Approach** (brittle):
```java
Field field = MinecraftServer.class.getDeclaredField("executor");
field.setAccessible(true);
Executor executor = (Executor) field.get(server);
```

**Problems**:
- Field name works in dev, fails in production (obfuscation)
- Need SRG names anyway
- Reflection calls every time
- Can break with Java security updates

**Access Transformer Approach** (robust):
```cfg
public net.minecraft.server.MinecraftServer f_129756_
```

**Benefits**:
- Applied at build time (no runtime overhead)
- Works in both dev and production
- Officially supported by Forge
- Fields become truly public (IDE autocomplete works!)

### Chunk Loading Behavior

**Important**: The backup dimension only loads chunks when queried!

```java
BlockState pristine = backupDimension.getBlockState(pos);
```

This call:
1. Calculates chunk position from BlockPos
2. Checks if chunk is loaded
3. If not loaded, generates chunk using ChunkGenerator
4. Caches chunk in memory
5. Returns block state

**Optimization**: Backup dimension chunks are NOT kept loaded unless:
- A player is in that dimension (none ever will be)
- Force-loaded via chunk tickets (we don't do this)

This means minimal memory overhead!

---

## Usage Guide

### Creating Your Own Dynamic Dimension

```java
// 1. Define your dimension key
public static final ResourceKey<Level> MY_DIMENSION_KEY = ResourceKey.create(
    Registries.DIMENSION,
    ResourceLocation.fromNamespaceAndPath("mymod", "my_dimension")
);

// 2. Create the dimension
ServerLevel myDimension = DynamicDimensionManager.getOrCreateLevel(
    server,
    MY_DIMENSION_KEY,
    (srv, dimKey) -> {
        // Option A: Copy overworld
        LevelStem overworldStem = srv.registries()
            .compositeAccess()
            .registryOrThrow(Registries.LEVEL_STEM)
            .get(ResourceKey.create(Registries.LEVEL_STEM, Level.OVERWORLD.location()));
        return overworldStem;

        // Option B: Custom generator
        BiomeSource biomeSource = /* your biome source */;
        Holder<NoiseGeneratorSettings> settings = /* your settings */;
        ChunkGenerator generator = new NoiseBasedChunkGenerator(biomeSource, settings);
        Holder<DimensionType> dimType = /* your dimension type */;
        return new LevelStem(dimType, generator);
    }
);

// 3. Use the dimension
BlockState block = myDimension.getBlockState(pos);
```

### Accessing the Backup Dimension from Anywhere

```java
// From any code with server access:
MinecraftServer server = level.getServer();
ServerLevel backup = server.getLevel(PristineChunkGenerator.PRISTINE_BACKUP_KEY);

if (backup != null) {
    // Dimension exists, query it
    BlockState pristine = backup.getBlockState(somePos);
}
```

### Modifying Generation Settings

If you want a dimension with DIFFERENT generation:

```java
(srv, dimKey) -> {
    // Get base settings
    LevelStem overworldStem = /* get overworld stem */;

    // Modify the generator
    NoiseBasedChunkGenerator oldGen =
        (NoiseBasedChunkGenerator) overworldStem.generator();

    // Create new generator with modifications
    Holder<NoiseGeneratorSettings> newSettings = /* modified settings */;
    NoiseBasedChunkGenerator newGen = new NoiseBasedChunkGenerator(
        oldGen.getBiomeSource(),  // Keep same biomes
        newSettings               // Different terrain
    );

    return new LevelStem(overworldStem.type(), newGen);
}
```

---

## Troubleshooting

### Build Fails After Adding AT

**Symptom**: "field has private access"

**Solution**:
1. Verify AT file path: `src/main/resources/META-INF/accesstransformer.cfg`
2. Check `build.gradle` has: `accessTransformer = file('...')`
3. Run: `./gradlew clean build --refresh-dependencies`
4. Reload IDE project

### Seed Mismatch Warning

**Symptom**: Log shows "Backup dimension seed does not match overworld seed"

**Causes**:
- Used wrong ChunkGenerator (didn't copy overworld's)
- Created new BiomeSource instead of reusing
- Modified generator after copying

**Solution**: Ensure you copy the EXACT LevelStem:
```java
return new LevelStem(
    overworldStem.type(),        // Don't create new!
    overworldStem.generator()    // Don't modify!
);
```

### Dimension Doesn't Persist

**Symptom**: Dimension recreates every world load

**Causes**:
- Not calling `server.markWorldsDirty()` after registration
- Not firing `LevelEvent.Load`
- Dimension registry not being saved

**Solution**: Ensure DynamicDimensionManager calls:
```java
server.markWorldsDirty();
MinecraftForge.EVENT_BUS.post(new LevelEvent.Load(newWorld));
```

### Slow Performance

**Symptom**: Game freezes when querying blocks

**Causes**:
- Querying positions in rapid succession
- Chunk generation happening on main thread
- No caching of results

**Solutions**:
1. Cache BlockState lookups if querying same position multiple times
2. Spread queries over multiple ticks
3. Pre-generate chunks asynchronously if you know you'll need them

### Registry Errors

**Symptom**: "Registry is frozen" or "Cannot register to frozen registry"

**Cause**: Trying to register dimension too late in loading process

**Solution**: Register dimensions during world load (e.g., ServerAboutToStartEvent or when first needed lazily)

---

## Future Extensions

### Potential Use Cases

1. **Time Travel Dimension**
   - Create dimension snapshot at specific time
   - Roll back/forward terrain changes

2. **Mirror Dimension**
   - Inverted terrain (air becomes stone, stone becomes air)
   - Modified BiomeSource for different biome layout

3. **Parallel Reality**
   - Different structure generation (no villages, all villages, etc.)
   - Modified NoiseGeneratorSettings for alien terrain

4. **Void World Copy**
   - Flat dimension at same seed
   - Useful for schematic comparisons

5. **Snapshot System**
   - Multiple backup dimensions at different timestamps
   - Version control for terrain

### Extending DynamicDimensionManager

**Adding Dimension Removal**:
```java
public static void removeDimension(MinecraftServer server, ResourceKey<Level> levelKey) {
    // Eject players
    // Save dimension data
    // Remove from registry
    // Remove from world map
    // Mark dirty
}
```

**Adding Dimension Copy**:
```java
public static void copyChunks(ServerLevel source, ServerLevel dest, ChunkPos pos) {
    // Load source chunk
    // Clone blocks and entities
    // Write to dest chunk
}
```

### Advanced: Custom ChunkGenerator

For complete control, implement your own ChunkGenerator:

```java
public class MirrorChunkGenerator extends NoiseBasedChunkGenerator {
    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(
        Executor executor,
        Blender blender,
        RandomState randomState,
        StructureManager structureManager,
        ChunkAccess chunk
    ) {
        // Generate base terrain
        CompletableFuture<ChunkAccess> base = super.fillFromNoise(...);

        // Modify generated terrain
        return base.thenApply(c -> {
            // Invert blocks, add custom features, etc.
            return c;
        });
    }
}
```

### Performance Optimization Ideas

1. **Chunk Pre-Generation**:
   ```java
   public void preGenerateArea(ServerLevel level, ChunkPos center, int radius) {
       for (int x = -radius; x <= radius; x++) {
           for (int z = -radius; z <= radius; z++) {
               ChunkPos pos = new ChunkPos(center.x + x, center.z + z);
               level.getChunk(pos.x, pos.z); // Forces generation
           }
       }
   }
   ```

2. **Async Block Queries**:
   ```java
   public CompletableFuture<BlockState> getPristineAsync(BlockPos pos) {
       return CompletableFuture.supplyAsync(() ->
           backupDimension.getBlockState(pos)
       );
   }
   ```

3. **Batch Comparisons**:
   ```java
   public Map<BlockPos, Boolean> compareArea(ServerLevel level, BlockPos start, BlockPos end) {
       // Compare entire region in one go
       // More efficient than per-block queries
   }
   ```

---

## References

### Primary Sources

- **RFTools Dimensions**: [GitHub - McJtyMods/RFToolsDimensions](https://github.com/McJtyMods/RFToolsDimensions)
- **Commoble's Dynamic Dimensions Gist**: [1.16.4 Implementation](https://gist.github.com/Commoble/7db2ef25f94952a4d2e2b7e3d4be53e0)
- **DynamicDimensions Library**: [TeamGalacticraft - 1.21+](https://github.com/TeamGalacticraft/DynamicDimensions)

### Documentation

- **Forge Access Transformers**: [Official Docs](https://docs.minecraftforge.net/en/latest/advanced/accesstransformers/)
- **Forge Community Wiki**: [Access Transformers Guide](https://forge.gemwire.uk/wiki/Access_Transformers)
- **MinecraftServer JavaDocs**: [1.20.1 ServerLevel](https://lexxie.dev/forge/1.20.1/net/minecraft/server/level/ServerLevel.html)

### Forum Discussions

- **Seed Synchronization Issue**: [Custom Seeds for Dimensions](https://forums.minecraftforge.net/topic/139721-1201-custom-seed-for-custom-dimensions/)
- **Dynamic Dimension Creation**: [Runtime Dimension Registration](https://forums.minecraftforge.net/topic/111099-dynamic-runtime-custom-dimension-creation-in-mc-118/)

---

## Changelog

### 2026-01-15 - Initial Implementation
- Implemented Access Transformers for MinecraftServer and RegistryAccess
- Created DynamicDimensionManager based on RFTools pattern
- Implemented PristineChunkGenerator with lazy initialization
- Integrated with RestorerBlock for terrain restoration
- Full build and testing successful

### Future
- Document dimension removal process
- Add async chunk generation utilities
- Create helper methods for common dimension modifications

---

## License Notes

- **DynamicDimensionManager**: Adapted from RFTools Dimensions by McJtyMods (MIT License)
- **Original Concept**: Commoble's Dynamic Dimensions Gist (Public Domain)
- **Grey Goo Mod**: Original implementation by StevenRS11

---

**End of Documentation**

For questions or issues, refer to the troubleshooting section or examine the source code with comments.
