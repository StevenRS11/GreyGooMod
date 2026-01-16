package com.stevenrs11.greygoo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

/**
 * Manages dynamic dimension creation at runtime.
 *
 * Based on code by Commoble and adapted from RFTools Dimensions by McJtyMods.
 * Used under MIT License.
 *
 * This allows creating dimensions during gameplay that will persist across server restarts.
 * Requires Access Transformers to expose MinecraftServer fields.
 */
public class DynamicDimensionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDimensionManager.class);

    /**
     * Retrieves the level for a given level key, registering and creating one if it doesn't already exist.
     *
     * Levels created this way will be saved to the save folder's dimension registry
     * and will be automatically loaded the next time the server starts.
     *
     * @param server The MinecraftServer instance
     * @param levelKey The ResourceKey for the world
     * @param dimensionFactory Function to create the LevelStem if it doesn't exist
     * @return A ServerLevel for the given key
     */
    public static ServerLevel getOrCreateLevel(MinecraftServer server, ResourceKey<Level> levelKey,
                                               BiFunction<MinecraftServer, ResourceKey<LevelStem>, LevelStem> dimensionFactory) {
        // Get the world map (this is made public via Access Transformer)
        @SuppressWarnings("deprecation")
        Map<ResourceKey<Level>, ServerLevel> map = server.forgeGetWorldMap();

        // If the level already exists, return it
        ServerLevel existingLevel = map.get(levelKey);
        if (existingLevel != null) {
            LOGGER.info("Level {} already exists, returning existing instance", levelKey.location());
            return existingLevel;
        }

        LOGGER.info("Creating new dynamic dimension: {}", levelKey.location());
        return createAndRegisterWorldAndDimension(server, map, levelKey, dimensionFactory);
    }

    /**
     * Creates and registers a new world and dimension.
     * This is the core logic adapted from RFTools Dimensions.
     */
    @SuppressWarnings("deprecation")
    private static ServerLevel createAndRegisterWorldAndDimension(MinecraftServer server,
                                                                  Map<ResourceKey<Level>, ServerLevel> map,
                                                                  ResourceKey<Level> worldKey,
                                                                  BiFunction<MinecraftServer, ResourceKey<LevelStem>, LevelStem> dimensionFactory) {
        // Get everything we need to create the dimension and the level
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);

        // Dimension keys have a 1:1 relationship with level keys, they have the same IDs
        ResourceKey<LevelStem> dimensionKey = ResourceKey.create(Registries.LEVEL_STEM, worldKey.location());
        LevelStem dimension = dimensionFactory.apply(server, dimensionKey);

        // Access the three fields exposed via Access Transformers
        // The int in create() is radius of chunks to watch, 11 is what the server uses
        ChunkProgressListener chunkProgressListener = server.progressListenerFactory.create(11);
        Executor executor = server.executor;
        LevelStorageSource.LevelStorageAccess anvilConverter = server.storageSource;

        WorldData worldData = server.getWorldData();
        WorldOptions worldGenSettings = worldData.worldGenOptions();
        DerivedLevelData derivedLevelData = new DerivedLevelData(worldData, worldData.overworldData());

        // Register the dimension to the registry
        // This is the same order server init creates levels
        LayeredRegistryAccess<RegistryLayer> registries = server.registries();
        RegistryAccess.ImmutableRegistryAccess composite = (RegistryAccess.ImmutableRegistryAccess) registries.compositeAccess();

        // Clone the existing dimension registry and add our new dimension
        Map<ResourceKey<? extends Registry<?>>, Registry<?>> regmap = new HashMap<>(composite.registries);
        ResourceKey<? extends Registry<?>> key = ResourceKey.create(
            ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("root")),
            ResourceLocation.withDefaultNamespace("dimension")
        );

        MappedRegistry<LevelStem> oldRegistry = (MappedRegistry<LevelStem>) regmap.get(key);
        Lifecycle oldLifecycle = oldRegistry.registryLifecycle();

        MappedRegistry<LevelStem> newRegistry = new MappedRegistry<>(Registries.LEVEL_STEM, oldLifecycle, false);

        // Copy all existing dimensions except if there's a conflict
        for (var entry : oldRegistry.entrySet()) {
            ResourceKey<LevelStem> oldKey = entry.getKey();
            ResourceKey<Level> oldLevelKey = ResourceKey.create(Registries.DIMENSION, oldKey.location());
            LevelStem dim = entry.getValue();
            if (dim != null && !oldLevelKey.equals(worldKey)) {
                Registry.register(newRegistry, oldKey, dim);
            }
        }

        // Add our new dimension
        Registry.register(newRegistry, dimensionKey, dimension);
        regmap.replace(key, newRegistry);

        @SuppressWarnings("unchecked")
        Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> newmap =
            (Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>>) regmap;
        composite.registries = newmap;

        // Create the ServerLevel instance
        ServerLevel newWorld = new ServerLevel(
            server,
            executor,
            anvilConverter,
            derivedLevelData,
            worldKey,
            dimension,
            chunkProgressListener,
            false, // isDebug
            net.minecraft.world.level.biome.BiomeManager.obfuscateSeed(worldGenSettings.seed()),
            ImmutableList.of(), // specialSpawners (empty for custom dimensions)
            false, // tickTime (false for non-overworld)
            null // RandomSequences (1.20.1 parameter)
        );

        // Add world border listener to sync with overworld border
        overworld.getWorldBorder().addListener(
            new BorderChangeListener.DelegateBorderChangeListener(newWorld.getWorldBorder())
        );

        // Register level to the server's world map
        map.put(worldKey, newWorld);

        // Update forge's world cache so the new level can be ticked
        server.markWorldsDirty();

        // Fire world load event
        MinecraftForge.EVENT_BUS.post(new LevelEvent.Load(newWorld));

        LOGGER.info("Successfully created and registered dimension: {}", worldKey.location());

        return newWorld;
    }
}
