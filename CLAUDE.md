# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a modernized port of the classic Grey Goo mod for Minecraft 1.20.1 using Forge 47.4.1. The mod implements self-replicating "goo" blocks that spread by consuming adjacent blocks, along with specialized variants (water eaters, air eaters, gravity goo, etc.) and cleaner blocks that remove goo.

**Target Environment:**
- Minecraft: 1.20.1
- Forge: 47.4.1 (last stable Forge for 1.20.x)
- Java: 17 (JDK 17 required)
- Mappings: Mojang official mappings

## Build Commands

**Setup project and generate IntelliJ run configurations:**
```bash
./gradlew genIntellijRuns --refresh-dependencies
```

**Build the mod JAR:**
```bash
./gradlew build
```
Output: `build/libs/greygoo-1.0.jar`

**Run client in development mode:**
```bash
./gradlew runClient
```

**Note:** The Gradle wrapper JAR is not in the repository. Run `gradle wrapper` once with a local Gradle installation to download it. Never commit binaries to the repository.

## Architecture

### Registry Pattern
All blocks and items are registered using Forge's `DeferredRegister` pattern in `GreyGooMod.java`. The main mod class contains:
- `BLOCKS` - DeferredRegister for all block types
- `ITEMS` - DeferredRegister for corresponding BlockItems
- `isGoo(Block)` - Static utility to check if a block is any goo variant

When adding new goo blocks:
1. Register the block in the `BLOCKS` DeferredRegister
2. Register the corresponding BlockItem in the `ITEMS` DeferredRegister
3. Add the block to `GreyGooCreativeTabs.java` displayItems lambda
4. Add the block to `isGoo()` method if it should be treated as goo
5. Create textures in `src/main/resources/assets/greygoo/textures/block/` and `item/`

### Block Behavior Pattern
All goo blocks follow a common pattern:
- Extend `Block` with `randomTicks()` enabled in properties
- Implement `randomTick()` for automatic spreading
- Override `use()` to trigger manual spreading on right-click
- Implement a private `spread()` or similar method containing the core logic
- Check for adjacent `CLEANER_BLOCK` - if found, convert self to cleaner (cleaner "infects" goo)
- Remove self if no valid targets found (goo starves)

**Example spreading logic (GreyGooBlock.java:30-48):**
- Iterate through 6 directions (Direction.values())
- Check if adjacent block is cleaner → convert self and return
- Check if adjacent block is edible → convert it to goo
- If no food found → destroy self

### Creative Tab
`GreyGooCreativeTabs.java` defines a custom creative mode tab for all mod items. When adding new blocks, manually add them to the `displayItems` lambda.

### Textures
Textures are NOT stored in the repository. Use `tools/split_textures.py` to generate individual 16x16 PNGs from the provided texture atlases:
```bash
python tools/split_textures.py GooBlockTextures.png GooItemTextures.png
```

Place generated textures in:
- `src/main/resources/assets/greygoo/textures/block/`
- `src/main/resources/assets/greygoo/textures/item/`

## Original Mod Naming Convention

**IMPORTANT:** The original mod has confusing naming between code and in-game names:

| In-Game Name | Original Code File | Behavior |
|--------------|-------------------|----------|
| **Purple Goo** | `BlockGreyGoo.java` | Random ticks, 6-direction spread, decay logic, metadata 2=inactive |
| **Grey Goo/Grey Eater** | `BlockGreyEater.java` | Random ticks, orthogonal only (Manhattan=1), color variants, cancer mutation |
| **OrangePurple** | `BlockOrangePurple.java` | Scheduled ticks, foundation required (inert/self behind), moving front effect |

When implementing or fixing goo behaviors, always reference the ORIGINAL CODE FILE, not the in-game name.

## Block Types Reference

**Core Goo Blocks:**
- `GreyGooBlock` - Grey Eater behavior: orthogonal spread only, color variants based on consumed block
- `PurpleGooBlock` - Purple Goo behavior: 6-direction spread, decay logic, becomes inactive when starved
- `CleanerBlock` - Removes goo blocks in radius 2 (Manhattan distance)
- `AirEaterBlock` - Consumes air blocks only
- `WaterEaterBlock` - Consumes water and lava fluids
- `RapidWaterEaterBlock` - Fast water/lava consumption variant
- `GravityGooBlock` - Goo variant with gravity physics
- `RedyellowBlock` - Special variant (check implementation for details)
- `WallBlock` - Defensive block that stops goo spread

**Protected Blocks:**
GreyGooBlock never consumes: bedrock, chest, ender_chest. Add more to the `NEVER_EAT` set as needed.

## Dynamic Dimension System

**See [DIMENSION_SYSTEM.md](DIMENSION_SYSTEM.md) for complete technical documentation.**

The mod uses a sophisticated dynamic dimension creation system to support the Restorer Block. Key components:

### Architecture Summary

1. **Access Transformers** (`src/main/resources/META-INF/accesstransformer.cfg`)
   - Exposes private MinecraftServer fields using SRG names
   - Required: `f_129756_` (progressListenerFactory), `f_129738_` (executor), `f_129744_` (storageSource)
   - Required: `f_206223_` (RegistryAccess.ImmutableRegistryAccess.registries)
   - **CRITICAL**: After modifying AT files, run `./gradlew clean build --refresh-dependencies`

2. **DynamicDimensionManager.java**
   - Creates dimensions at runtime (adapted from RFTools Dimensions)
   - Main method: `getOrCreateLevel(server, levelKey, dimensionFactory)`
   - Handles registry manipulation, ServerLevel construction, world persistence

3. **PristineChunkGenerator.java**
   - Manages the `greygoo:pristine_backup` dimension
   - Lazily creates backup dimension mirroring overworld generation
   - Provides `getPristineBlock(level, pos)` for querying pristine block states

4. **RestorerBlock.java**
   - Uses PristineChunkGenerator to compare current vs pristine blocks
   - 3-state restoration cycle (DEFAULT → READY → COMPLETE)
   - Spreads into modified blocks (including other goo) with 50% probability

### Why This Approach?

- **Seed Synchronization**: Backup dimension automatically uses same seed as overworld (copies ChunkGenerator)
- **Complete Generation**: Structures, features, biomes all handled by vanilla systems
- **Performance**: Direct queries instead of slow on-demand chunk generation
- **Persistence**: Dimension saves with world data, reloads automatically

### Key Technical Details

**LevelStem Copying** - The magic happens here:
```java
LevelStem overworldStem = /* get from registry */;
return new LevelStem(
    overworldStem.type(),        // Same DimensionType
    overworldStem.generator()    // Same ChunkGenerator = same seed!
);
```

**Why Not JSON Dimensions?** JSON dimensions cannot dynamically inherit world seeds per-save in Forge 1.20.1 ([source](https://forums.minecraftforge.net/topic/139721-1201-custom-seed-for-custom-dimensions/)).

**SRG Names in Access Transformers**: Field names work in dev but are obfuscated in production. SRG names (e.g., `f_129756_`) are intermediate mappings that work in both. Find them in `build/fg_cache/.../srg_to_official_1.20.1.tsrg`.

### Future Extensions

This system can be extended for:
- Time-travel dimensions (world snapshots at different timestamps)
- Mirror dimensions (inverted terrain, different structure gen)
- Parallel realities (same seed, modified generation settings)
- Custom ChunkGenerators for completely unique terrain

Refer to DIMENSION_SYSTEM.md sections "Future Extensions" and "Usage Guide" for implementation examples.

## Code Style Notes

- Package: `com.stevenrs11.greygoo`
- Mod ID: `greygoo`
- Use Java (not Kotlin)
- Follow Forge 1.20.1 conventions (`@Mod`, `DeferredRegister`, event bus subscription)
- All goo blocks should implement both random tick spreading and manual click-to-spread
- Use `BlockBehaviour.Properties.copy(Blocks.STONE).randomTicks()` for goo block properties
- Server-side logic only - check `!level.isClientSide` before modifications
- **Timing**: Use randomized delays for scheduleTick() to prevent synchronized updates (e.g., `random.nextInt(25) + random.nextInt(10)`)

## Important Constraints

- Binary files (textures, JARs) must not be committed to the repository
- Resource pack formats must follow vanilla 1.20.1 specifications
- Use Mojang mappings (no SRG/obfuscated names in code)
- Ensure Java 17 compatibility
