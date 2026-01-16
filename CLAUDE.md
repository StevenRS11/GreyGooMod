# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is the Grey Goo Mod for Minecraft 1.6.4, a Forge-based mod introducing self-replicating "goo" blocks with various behaviors and properties. The mod features approximately 25 different goo types, processing machines, custom dimensions, and entities.

**Minecraft Version:** 1.6.4
**Mod Loader:** Forge (legacy FML API)
**Package Structure:** `StevenGreyGoo.mod_GreyGoo` (main) and `StevenGreyGoo.mod_GreyGooClient` (client-only)

## Architecture

### Core Systems

**Goo Spread Mechanism**
- `SpreadHelper` - Generic spatial search system for finding blocks within a radius
  - Supports Manhattan distance (non-diagonal) and Chebyshev distance (diagonal) modes
  - Can search for blocks matching a list OR not matching a list
  - Used by all spreading goo blocks for neighbor detection and conversion
- `SpreadLimiter` - Throttles goo propagation to prevent server lag
- Individual goo blocks implement spreading logic in their `updateTick()` method

**Block Types**
- **Spreading Goo** (BlockGreyGoo, BlockCancer, BlockTGD, etc.) - Self-replicating blocks that convert adjacent blocks
- **Eating Goo** (BlockWaterEater, BlockAirEater, BlockGreyEater, etc.) - Consume specific block types
- **Utility Goo** (BlockWall, BlockInert, BlockCleaner, etc.) - Provide barriers, cleanup, or restoration
- **Special Goo** (BlockGravityGoo, BlockElevatorGoo, BlockBubble, etc.) - Unique behaviors like gravity manipulation
- **Processing Machines** (Assembler, Compiler, Programmer, Homogenizer) - Crafting stations with TileEntities

**Processing System**
Each machine has 4 components:
1. Block class (e.g., `Assembler`) - Defines block behavior and TileEntity creation
2. TileEntity class (e.g., `TileEntityAssembler`) - Server-side inventory and processing logic
3. Container class (e.g., `ContainerAssembler`) - Shared inventory interface
4. Gui class (e.g., `GuiAssembler`) - Client-side rendering

Recipe systems use string concatenation of item IDs for lookups (see `AssemblerRecipes`, `ProgrammerRecipes`, etc.)

**Dimension System**
- `GooDimensionHelper` - Manages custom dimension registration
- `GooWorldProvider` - Defines dimension properties (sky color, lighting, etc.)
- `GooChunkProvider` - Generates terrain for the goo dimension
- `GooPortal` / `TileEntityGooPortal` - Portal block and teleportation logic
- `GooTeleporter` - Handles player/entity teleportation between dimensions

**Entity System**
- `EntityTGDGolem` - Hostile mob with replication behavior (uses `EntityAIReplicate`)
- `EntityFallingGravityGoo` - Falling block entity for gravity goo
- Custom renderers in `mod_GreyGooClient` package

### Package Organization

```
mod_GreyGoo/          # Server + client code
  ├── Block*.java     # Block definitions (~30 files)
  ├── Item*.java      # Item definitions (Matrices, Modifiers, tools)
  ├── TileEntity*.java # Processing machine logic
  ├── Container*.java  # Inventory interfaces
  ├── Gui*.java       # GUI definitions (delegate to client package)
  ├── *Recipes.java   # Recipe registries
  ├── SpreadHelper.java
  ├── GooDimensionHelper.java
  ├── Entity*.java    # Custom entities
  └── mod_GreyGoo.java # Main mod class

mod_GreyGooClient/    # Client-only rendering
  ├── ClientProxy.java
  ├── Render*.java    # Entity renderers
  └── ClientTickHandler.java

gui/                  # GUI texture assets
mob/                  # Mob texture assets
```

### Key Files

**mod_GreyGoo.java** (1270 lines)
- Main mod initialization (`@PreInit`, `@Init`, `@PostInit`)
- Block/item registration and configuration
- Static references to all blocks and items
- Global lists: `allGooBlocks`, `Modifiers`, `Matrices`, `mineTheseOnly`, `gooNeverEatThese`
- Config loading from Forge configuration files

**SpreadHelper.java** (318 lines)
- Reusable block search utility used by all spreading goo
- Key methods: `findBlocks()`, `addID()`, `setBase()`, `setNegOrPosSearch()`
- Flags: `onlyCheckMaxRadius`, `checkCubeOutline`, `doDiagonals`

**CommonProxy.java** / **ClientProxy.java**
- Side-specific code abstraction (server vs client)
- Texture loading, renderer registration
- NBT data persistence for world-specific settings

## Development Notes

### Legacy Minecraft Version Considerations

This mod uses **Minecraft 1.6.4 APIs** which differ significantly from modern versions:

- Uses `setBlockWithNotify()` instead of modern `setBlockState()`
- Block IDs are integers, not ResourceLocations
- Texture system uses sprite sheets (`/GooBlockTextures.png`, `/GooItemTextures.png`) and index numbers
- FML annotations: `@Mod`, `@Init`, `@PreInit`, `@PostInit` (not `@EventBusSubscriber`)
- Network handling via `@NetworkMod` with packet handlers
- `ModLoader` and `MinecraftForge` coexist as separate systems

### Common Patterns

**Adding a New Goo Block:**
1. Create block class extending `Block` with `updateTick()` for spreading logic
2. Register in `mod_GreyGoo.java` `@Init` method
3. Add ID to appropriate lists (`allGooBlocks`, `gooNeverEatThese`, etc.)
4. Define texture index in constructor
5. Add language entry (if needed)

**Goo Spreading Pattern:**
```java
SpreadHelper helper = new SpreadHelper(world, x, y, z, radius, diagonals, findPositive);
helper.addID(blockID);
List<CoordHolder> coords = helper.findBlocks();
for (CoordHolder coord : coords) {
    world.setBlockWithNotify(coord.xCoord, coord.yCoord, coord.zCoord, newBlockID);
}
```

**Adding Processing Machine:**
1. Create Block, TileEntity, Container, Gui classes (use existing as template)
2. Create Recipes class with map-based recipe storage
3. Register in `mod_GreyGoo.java` and `GuiHandler.java`
4. Add GUI texture to `gui/` directory

### Configuration System

Block and item IDs are configurable via Forge config files. Default IDs:
- Blocks: 168-202 range
- Items: 401-431 range

Config is loaded in `@PreInit` using `Configuration` API.

### Data Persistence

World-specific settings saved to `GGMData.dat` via NBT:
- `TGDbloom` - Whether TGD can bloom/spread
- `GooActive` - Master on/off switch for goo spreading
- `FreezerTexture` - Dynamic texture selection

Handled in `CommonProxy.writeNBTToFile()` and `readNBTFromFile()`.

### Texture System

Textures use pre-1.7 sprite sheet system:
- `GooBlockTextures.png` - 16x16 block textures in grid
- `GooItemTextures.png` - 16x16 item textures in grid
- Blocks reference texture by index number in constructor
- Loaded via `MinecraftForgeClient.preloadTexture()` in `ClientProxy`

### Rendering

Custom rendering for special blocks:
- `RenderOrangeWhiteGoo` - Custom block renderer (uses `OrangeWhiteRenderID`)
- `RenderFallingGravityGoo` - Entity renderer for falling goo
- `RenderTGDGolem` - Entity renderer using `ModelTGDGolem`

Registered in `ClientProxy.registerRenderers()` via `RenderingRegistry`.

## Important Constraints

1. **Thread Safety**: All world modification must happen server-side (`!world.isRemote`)
2. **Spread Rate Limiting**: Always use `mod_GreyGoo.instance.spreadLimiter.spreadLimiter()` before spreading
3. **Protected Blocks**: Never convert blocks in `gooNeverEatThese` list (includes EMPArray, portals)
4. **Legacy ID System**: Block/item IDs are integers and must not conflict with vanilla or other mods
5. **Side Handling**: Client-only code must stay in `mod_GreyGooClient` package and extend `CommonProxy`

## Notes on Code Style

This is legacy code from Minecraft 1.6.4 era (circa 2013) and contains:
- Obfuscated/placeholder item names (intentional for gameplay mystery)
- Some empty or stub implementations (e.g., `BlockConverterGoo.java`, `BlockPopcornGoo.java`)
- Mix of camelCase and inconsistent naming
- Limited JavaDoc comments
- Recipe systems use string concatenation instead of modern tuple keys

When modifying, maintain consistency with existing patterns rather than modernizing.
