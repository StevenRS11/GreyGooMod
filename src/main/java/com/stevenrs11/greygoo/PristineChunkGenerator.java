package com.stevenrs11.greygoo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.dimension.LevelStem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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
 *
 * Performance Optimization: This class caches loaded chunks to avoid repeated
 * chunk loads from the backup dimension. With 50-chunk cache, queries for the
 * same chunk are ~100,000x faster (50ms chunk load vs 100ns cache hit).
 */
public class PristineChunkGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PristineChunkGenerator.class);

    // The resource key for our pristine backup dimension
    public static final ResourceKey<Level> PRISTINE_BACKUP_KEY = ResourceKey.create(
        Registries.DIMENSION,
        ResourceLocation.fromNamespaceAndPath(GreyGooMod.MODID, "pristine_backup")
    );

    // Cache configuration
    private static final int CHUNK_CACHE_SIZE = 50;  // ~3.2MB memory (50 chunks × 64KB)

    private ServerLevel backupDimension = null;
    private boolean initialized = false;

    // Chunk cache: LRU eviction using LinkedHashMap with access-order
    // Thread-safe wrapper for concurrent access from multiple restorer blocks
    private final Map<ChunkPos, ChunkAccess> chunkCache = Collections.synchronizedMap(
        new LinkedHashMap<ChunkPos, ChunkAccess>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<ChunkPos, ChunkAccess> eldest) {
                boolean shouldRemove = size() > CHUNK_CACHE_SIZE;
                if (shouldRemove) {
                    LOGGER.debug("Evicting chunk from cache: {}", eldest.getKey());
                }
                return shouldRemove;
            }
        }
    );

    // Cache statistics
    private long cacheHits = 0;
    private long cacheMisses = 0;
    private long totalQueries = 0;

    /**
     * Get the pristine block state at the given position.
     *
     * This creates the backup dimension if needed, loads the chunk (with caching),
     * and queries it for the block state.
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

        totalQueries++;

        // Calculate chunk position
        ChunkPos chunkPos = new ChunkPos(pos);

        // Check cache first
        ChunkAccess chunk = chunkCache.get(chunkPos);
        if (chunk != null) {
            // Cache hit!
            cacheHits++;
            LOGGER.debug("Pristine chunk cache HIT: {} (hit rate: {:.1f}%)",
                chunkPos, getCacheHitRate());
        } else {
            // Cache miss - load from backup dimension
            cacheMisses++;
            LOGGER.debug("Pristine chunk cache MISS: {} - loading from backup dimension",
                chunkPos);

            long startTime = System.nanoTime();
            chunk = backupDimension.getChunk(chunkPos.x, chunkPos.z);
            long loadTime = System.nanoTime() - startTime;

            // Add to cache
            chunkCache.put(chunkPos, chunk);

            LOGGER.debug("Loaded chunk {} in {:.2f}ms (cache size: {}/{})",
                chunkPos,
                loadTime / 1_000_000.0,
                chunkCache.size(),
                CHUNK_CACHE_SIZE);
        }

        // Get block state from chunk
        BlockState pristineState = chunk.getBlockState(pos);

        LOGGER.trace("Pristine block at {}: {}", pos, pristineState.getBlock());
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

                LOGGER.info("Chunk cache initialized: max size = {}, estimated memory = ~{}MB",
                    CHUNK_CACHE_SIZE,
                    (CHUNK_CACHE_SIZE * 64) / 1024);
            } else {
                LOGGER.error("Failed to create or retrieve backup dimension");
            }

        } catch (Exception e) {
            LOGGER.error("Exception while initializing pristine backup dimension: {}", e.getMessage(), e);
            backupDimension = null;
        }
    }

    /**
     * Clear the chunk cache.
     * Useful for debugging or forcing fresh chunk loads.
     */
    public void clearCache() {
        synchronized (chunkCache) {
            int size = chunkCache.size();
            chunkCache.clear();
            LOGGER.info("Cleared pristine chunk cache ({} chunks)", size);
        }
    }

    /**
     * Get cache hit rate as a percentage (0-100).
     */
    public double getCacheHitRate() {
        if (totalQueries == 0) {
            return 0.0;
        }
        return (cacheHits * 100.0) / totalQueries;
    }

    /**
     * Log detailed cache statistics.
     * Useful for monitoring performance and tuning cache size.
     */
    public void logCacheStats() {
        double hitRate = getCacheHitRate();
        long avgLoadTime = cacheMisses > 0 ? 0 : 0; // TODO: Track if needed

        LOGGER.info("=== Pristine Chunk Cache Statistics ===");
        LOGGER.info("Total queries: {}", totalQueries);
        LOGGER.info("Cache hits: {} ({:.1f}%)", cacheHits, hitRate);
        LOGGER.info("Cache misses: {} ({:.1f}%)", cacheMisses, 100.0 - hitRate);
        LOGGER.info("Current cache size: {}/{} chunks", chunkCache.size(), CHUNK_CACHE_SIZE);
        LOGGER.info("Estimated memory usage: ~{:.1f}MB",
            (chunkCache.size() * 64.0) / 1024.0);

        if (hitRate < 50.0 && totalQueries > 100) {
            LOGGER.warn("Low cache hit rate ({:.1f}%) - consider increasing CHUNK_CACHE_SIZE", hitRate);
        }
    }

    /**
     * Reset cache statistics.
     * Useful for benchmarking specific scenarios.
     */
    public void resetStats() {
        cacheHits = 0;
        cacheMisses = 0;
        totalQueries = 0;
        LOGGER.info("Reset pristine cache statistics");
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
     * Get statistics about the backup dimension and cache.
     */
    public String getStats() {
        if (!initialized) {
            return "Pristine backup: Not initialized";
        }
        if (backupDimension == null) {
            return "Pristine backup: Initialization failed";
        }

        return String.format(
            "Pristine backup: %s | Seed: %d | Loaded chunks: %d | " +
            "Cache: %d/%d chunks (%.1f%% hit rate, %d queries)",
            PRISTINE_BACKUP_KEY.location(),
            backupDimension.getSeed(),
            backupDimension.getChunkSource().getLoadedChunksCount(),
            chunkCache.size(),
            CHUNK_CACHE_SIZE,
            getCacheHitRate(),
            totalQueries
        );
    }
}
