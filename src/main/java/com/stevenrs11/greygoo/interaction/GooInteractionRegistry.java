package com.stevenrs11.greygoo.interaction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stevenrs11.greygoo.GreyGooMod;
import com.stevenrs11.greygoo.core.GooType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

/**
 * Registry for goo-to-goo interactions.
 * Loaded from JSON config file for modpack customization.
 */
public class GooInteractionRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(GooInteractionRegistry.class);
    private static GooInteractionRegistry INSTANCE;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Interaction matrix: gooToGoo[self][other] = interaction
    private final Map<GooType, Map<GooType, GooInteraction>> gooToGoo = new EnumMap<>(GooType.class);

    // Default interaction for each goo type (when specific interaction not defined)
    private final Map<GooType, GooInteraction> defaults = new EnumMap<>(GooType.class);

    private GooInteractionRegistry() {
        // Initialize with hardcoded defaults
        initializeDefaults();
    }

    public static GooInteractionRegistry get() {
        if (INSTANCE == null) {
            INSTANCE = new GooInteractionRegistry();
        }
        return INSTANCE;
    }

    /**
     * Get the interaction between a goo type and another block.
     */
    public GooInteraction getInteraction(GooType self, Block other) {
        // Check if the other block is a goo
        GooType otherType = GooType.fromBlock(other);

        if (otherType != null) {
            // Goo-to-goo interaction
            Map<GooType, GooInteraction> selfInteractions = gooToGoo.get(self);
            if (selfInteractions != null) {
                GooInteraction interaction = selfInteractions.get(otherType);
                if (interaction != null) {
                    return interaction;
                }
            }
            // Fall back to default for this goo type
            return defaults.getOrDefault(self, GooInteraction.IGNORE);
        }

        // Non-goo block - default to SPREAD_INTO (normal spreading)
        return GooInteraction.SPREAD_INTO;
    }

    /**
     * Set a specific interaction between two goo types.
     */
    public void setInteraction(GooType self, GooType other, GooInteraction interaction) {
        gooToGoo.computeIfAbsent(self, k -> new EnumMap<>(GooType.class))
                .put(other, interaction);
    }

    /**
     * Set the default interaction for a goo type.
     */
    public void setDefault(GooType type, GooInteraction interaction) {
        defaults.put(type, interaction);
    }

    /**
     * Initialize hardcoded default interactions.
     * These can be overridden by JSON config.
     */
    private void initializeDefaults() {
        // Cleaner infects ALL other goos
        for (GooType type : GooType.values()) {
            if (type != GooType.CLEANER) {
                setInteraction(type, GooType.CLEANER, GooInteraction.CONVERT_SELF);
            }
        }

        // Cleaner converts all goos
        for (GooType type : GooType.values()) {
            if (type != GooType.CLEANER && type != GooType.INERT) {
                setInteraction(GooType.CLEANER, type, GooInteraction.CONVERT_OTHER);
            }
        }
        setInteraction(GooType.CLEANER, GooType.CLEANER, GooInteraction.IGNORE);
        setInteraction(GooType.CLEANER, GooType.INERT, GooInteraction.IGNORE);

        // Wall and Inert ignore most things
        setDefault(GooType.WALL, GooInteraction.IGNORE);
        setDefault(GooType.INERT, GooInteraction.IGNORE);

        // Wall requires foundation
        setInteraction(GooType.WALL, GooType.WALL, GooInteraction.REQUIRE_FOUNDATION);
        setInteraction(GooType.WALL, GooType.INERT, GooInteraction.REQUIRE_FOUNDATION);

        // Basic consumers ignore defensive blocks
        for (GooType consumer : new GooType[]{GooType.GREY_GOO, GooType.AIR_EATER,
                GooType.WATER_EATER, GooType.MINER_GOO, GooType.GREY_EATER}) {
            setInteraction(consumer, GooType.WALL, GooInteraction.IGNORE);
            setInteraction(consumer, GooType.INERT, GooInteraction.IGNORE);
            setInteraction(consumer, consumer, GooInteraction.IGNORE);  // Same type ignores self
        }

        // Black Destroyer dominance
        setInteraction(GooType.BLACK_DESTROYER, GooType.GREY_GOO, GooInteraction.CONVERT_OTHER);
        setInteraction(GooType.BLACK_DESTROYER, GooType.AIR_EATER, GooInteraction.CONVERT_OTHER);
        setInteraction(GooType.BLACK_DESTROYER, GooType.WATER_EATER, GooInteraction.CONVERT_OTHER);
        setInteraction(GooType.BLACK_DESTROYER, GooType.MINER_GOO, GooInteraction.CONVERT_OTHER);
        setInteraction(GooType.BLACK_DESTROYER, GooType.CANCER, GooInteraction.IGNORE);
        setInteraction(GooType.BLACK_DESTROYER, GooType.CANCER2, GooInteraction.IGNORE);
        setInteraction(GooType.BLACK_DESTROYER, GooType.TGD, GooInteraction.IGNORE);
        setInteraction(GooType.BLACK_DESTROYER, GooType.TGD_INERT, GooInteraction.IGNORE);
        setInteraction(GooType.BLACK_DESTROYER, GooType.WALL, GooInteraction.IGNORE);
        setInteraction(GooType.BLACK_DESTROYER, GooType.INERT, GooInteraction.IGNORE);
        setInteraction(GooType.BLACK_DESTROYER, GooType.FREEZER, GooInteraction.IGNORE);

        // TGD special exclusions
        setInteraction(GooType.TGD, GooType.GREY_GOO, GooInteraction.CONVERT_OTHER);
        setInteraction(GooType.TGD, GooType.ORANGE_RED, GooInteraction.IGNORE);
        setInteraction(GooType.TGD, GooType.BLACK_DESTROYER, GooInteraction.IGNORE);
        setInteraction(GooType.TGD, GooType.WALL, GooInteraction.IGNORE);
        setInteraction(GooType.TGD, GooType.INERT, GooInteraction.IGNORE);
        setInteraction(GooType.TGD, GooType.FREEZER, GooInteraction.IGNORE);
        setInteraction(GooType.TGD, GooType.GREY_EATER, GooInteraction.IGNORE);
        setInteraction(GooType.TGD, GooType.TGD_INERT, GooInteraction.IGNORE);

        // Restorer converts goos back
        setInteraction(GooType.RESTORER, GooType.GREY_GOO, GooInteraction.CONVERT_OTHER);
        setInteraction(GooType.RESTORER, GooType.BLACK_DESTROYER, GooInteraction.CONVERT_OTHER);
        setInteraction(GooType.RESTORER, GooType.TGD, GooInteraction.CONVERT_OTHER);
        setInteraction(GooType.RESTORER, GooType.WALL, GooInteraction.IGNORE);
        setInteraction(GooType.RESTORER, GooType.INERT, GooInteraction.IGNORE);

        // Color variants require foundation
        for (GooType color : new GooType[]{GooType.ORANGE_RED, GooType.ORANGE_WHITE, GooType.ORANGE_PURPLE}) {
            setDefault(color, GooInteraction.IGNORE);
            setInteraction(color, color, GooInteraction.REQUIRE_FOUNDATION);
            setInteraction(color, GooType.INERT, GooInteraction.REQUIRE_FOUNDATION);
        }

        // Inert types never spread
        setDefault(GooType.CANCER2, GooInteraction.IGNORE);
        setDefault(GooType.TGD_INERT, GooInteraction.IGNORE);
    }

    // ==================== JSON Config Loading ====================

    /**
     * Get the path to the interactions config file.
     */
    public static Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get().resolve("greygoo").resolve("interactions.json");
    }

    /**
     * Load interactions from JSON config file.
     */
    public void loadFromConfig() {
        Path configPath = getConfigPath();

        if (!Files.exists(configPath)) {
            // Create default config
            saveDefaultConfig();
            return;
        }

        try (Reader reader = Files.newBufferedReader(configPath)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            parseConfig(root);
            LOGGER.info("Loaded goo interactions from {}", configPath);
        } catch (IOException e) {
            LOGGER.error("Failed to load interactions config: {}", e.getMessage());
        }
    }

    /**
     * Parse JSON config and populate the registry.
     */
    private void parseConfig(JsonObject root) {
        for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
            String selfId = entry.getKey();

            // Skip comments
            if (selfId.startsWith("_")) continue;

            GooType self = GooType.fromId(selfId);
            if (self == null) {
                LOGGER.warn("Unknown goo type in config: {}", selfId);
                continue;
            }

            if (entry.getValue().isJsonObject()) {
                JsonObject interactions = entry.getValue().getAsJsonObject();
                for (Map.Entry<String, JsonElement> interactionEntry : interactions.entrySet()) {
                    String key = interactionEntry.getKey();

                    // Handle _default
                    if (key.equals("_default")) {
                        GooInteraction defaultInteraction = GooInteraction.fromString(
                            interactionEntry.getValue().getAsString());
                        setDefault(self, defaultInteraction);
                        continue;
                    }

                    // Skip other metadata
                    if (key.startsWith("_")) continue;

                    GooType other = GooType.fromId(key);
                    if (other == null) {
                        LOGGER.warn("Unknown goo type in config: {}", key);
                        continue;
                    }

                    GooInteraction interaction = GooInteraction.fromString(
                        interactionEntry.getValue().getAsString());
                    setInteraction(self, other, interaction);
                }
            }
        }
    }

    /**
     * Save the default config file.
     */
    public void saveDefaultConfig() {
        Path configPath = getConfigPath();
        try {
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                writer.write(getDefaultConfigJson());
            }
            LOGGER.info("Created default interactions config at {}", configPath);
        } catch (IOException e) {
            LOGGER.error("Failed to save default config: {}", e.getMessage());
        }
    }

    /**
     * Generate the default config JSON.
     */
    private String getDefaultConfigJson() {
        return """
            {
                "_comment": "Goo interaction matrix. Format: 'self_type': { 'other_type': 'INTERACTION' }",
                "_interactions": "IGNORE, CONVERT_SELF, CONVERT_OTHER, SPREAD_INTO, REQUIRE_FOUNDATION, MUTUAL_IGNORE, STARVE_CHECK",

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
                }
            }
            """;
    }

    /**
     * Reload the config (for hot-reloading).
     */
    public void reload() {
        // Reset to defaults first
        gooToGoo.clear();
        defaults.clear();
        initializeDefaults();

        // Load from config
        loadFromConfig();
    }
}
