package com.stevenrs11.greygoo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.LevelStem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides pristine block states by maintaining a backup dimension that mirrors
 * the overworld's initial generation.
 *
 * This class creates a parallel dimension with the same seed, biomes, and terrain
 * generation as the overworld. The backup dimension remains untouched, allowing
 * the Restorer Block to compare current blocks against their original state.
 *
 * The backup dimension is created lazily on first use and persists across server
 * restarts thanks to Minecraft's dimension system.
 */
public class PristineChunkGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PristineChunkGenerator.class);

    // The resource key for our pristine backup dimension
    public static final ResourceKey<Level> PRISTINE_BACKUP_KEY = ResourceKey.create(
        Registries.DIMENSION,
        ResourceLocation.fromNamespaceAndPath(GreyGooMod.MODID, "pristine_backup")
    );

    private ServerLevel backupDimension = null;
    private boolean initialized = false;

    /**
     * Get the pristine block state at the given position.
     *
     * This creates the backup dimension if needed and queries it for the block state.
     *
     * @param level The server level (should be overworld)
     * @param pos The block position to query
     * @return The pristine block state at this position, or null if unavailable
     */
    public BlockState getPristineBlock(ServerLevel level, BlockPos pos) {
        if (level == null || level.isClientSide) {
            return null;
        }

        // Only works in the overworld
        if (level.dimension() != Level.OVERWORLD) {
            LOGGER.warn("PristineChunkGenerator called in non-overworld dimension: {}", level.dimension().location());
            return null;
        }

        // Lazy initialization - create backup dimension on first use
        if (!initialized) {
            initialize(level);
        }

        // If initialization failed, return null
        if (backupDimension == null) {
            LOGGER.error("Backup dimension is not available");
            return null;
        }

        // Query the backup dimension for the pristine block state
        BlockState pristineState = backupDimension.getBlockState(pos);

        LOGGER.debug("Pristine block at {}: {}", pos, pristineState.getBlock());
        return pristineState;
    }

    /**
     * Initialize the backup dimension by creating it or retrieving it if it already exists.
     *
     * The backup dimension uses the exact same LevelStem (DimensionType + ChunkGenerator)
     * as the overworld, ensuring identical terrain generation.
     */
    private void initialize(ServerLevel overworld) {
        initialized = true;

        try {
            LOGGER.info("Initializing pristine backup dimension...");

            // Get the overworld's LevelStem to copy its generation settings
            LevelStem overworldStem = overworld.getServer().registries().compositeAccess()
                .registryOrThrow(Registries.LEVEL_STEM)
                .get(ResourceKey.create(Registries.LEVEL_STEM, Level.OVERWORLD.location()));

            if (overworldStem == null) {
                LOGGER.error("Could not find overworld LevelStem!");
                return;
            }

            // Create or retrieve the backup dimension
            // This will reuse the existing dimension if it was already created in a previous session
            backupDimension = DynamicDimensionManager.getOrCreateLevel(
                overworld.getServer(),
                PRISTINE_BACKUP_KEY,
                (server, dimensionKey) -> {
                    // Create a LevelStem that exactly mirrors the overworld
                    // Same DimensionType, same ChunkGenerator (which includes the seed)
                    LOGGER.info("Creating new pristine backup dimension with overworld settings");
                    return new LevelStem(
                        overworldStem.type(),        // Same dimension type (time, lighting, etc.)
                        overworldStem.generator()    // Same chunk generator (includes seed, biomes, noise)
                    );
                }
            );

            if (backupDimension != null) {
                LOGGER.info("Pristine backup dimension initialized successfully: {}",
                    PRISTINE_BACKUP_KEY.location());
                LOGGER.info("Backup dimension seed: {}", backupDimension.getSeed());
                LOGGER.info("Overworld seed: {}", overworld.getSeed());

                // Verify seeds match
                if (backupDimension.getSeed() != overworld.getSeed()) {
                    LOGGER.warn("WARNING: Backup dimension seed does not match overworld seed! " +
                        "This should not happen and may cause restoration issues.");
                }
            } else {
                LOGGER.error("Failed to create or retrieve backup dimension");
            }

        } catch (Exception e) {
            LOGGER.error("Exception while initializing pristine backup dimension: {}", e.getMessage(), e);
            backupDimension = null;
        }
    }

    /**
     * Get the backup dimension instance.
     * Returns null if not yet initialized or initialization failed.
     */
    public ServerLevel getBackupDimension() {
        return backupDimension;
    }

    /**
     * Check if the backup dimension is initialized and available.
     */
    public boolean isInitialized() {
        return initialized && backupDimension != null;
    }

    /**
     * Get statistics about the backup dimension.
     */
    public String getStats() {
        if (!initialized) {
            return "Pristine backup: Not initialized";
        }
        if (backupDimension == null) {
            return "Pristine backup: Initialization failed";
        }
        return String.format("Pristine backup: %s (seed: %d, loaded chunks: %d)",
            PRISTINE_BACKUP_KEY.location(),
            backupDimension.getSeed(),
            backupDimension.getChunkSource().getLoadedChunksCount()
        );
    }
}
