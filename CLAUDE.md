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

## Block Types Reference

**Core Goo Blocks:**
- `GreyGooBlock` - Consumes all non-protected blocks (excludes bedrock, chests, ender chests)
- `CleanerBlock` - Removes goo blocks in radius 2 (Manhattan distance)
- `AirEaterBlock` - Consumes air blocks only
- `WaterEaterBlock` - Consumes water and lava fluids
- `RapidWaterEaterBlock` - Fast water/lava consumption variant
- `GravityGooBlock` - Goo variant with gravity physics
- `RedyellowBlock` - Special variant (check implementation for details)
- `WallBlock` - Defensive block that stops goo spread

**Protected Blocks:**
GreyGooBlock never consumes: bedrock, chest, ender_chest. Add more to the `NEVER_EAT` set as needed.

## Code Style Notes

- Package: `com.stevenrs11.greygoo`
- Mod ID: `greygoo`
- Use Java (not Kotlin)
- Follow Forge 1.20.1 conventions (`@Mod`, `DeferredRegister`, event bus subscription)
- All goo blocks should implement both random tick spreading and manual click-to-spread
- Use `BlockBehaviour.Properties.copy(Blocks.STONE).randomTicks()` for goo block properties
- Server-side logic only - check `!level.isClientSide` before modifications

## Important Constraints

- Binary files (textures, JARs) must not be committed to the repository
- Resource pack formats must follow vanilla 1.20.1 specifications
- Use Mojang mappings (no SRG/obfuscated names in code)
- Ensure Java 17 compatibility
