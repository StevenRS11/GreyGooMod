package com.stevenrs11.greygoo.interaction;

import com.stevenrs11.greygoo.GreyGooMod;
import com.stevenrs11.greygoo.tags.GreyGooTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Three-layer protection system for blocks that goo cannot consume.
 *
 * Layer 1: Hardcoded blocks (always protected)
 * Layer 2: Tag-based protection (greygoo:protected tag)
 * Layer 3: Config file additions
 */
public class ProtectedBlocks {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProtectedBlocks.class);

    /**
     * Layer 1: Hardcoded protected blocks.
     * These can NEVER be consumed by any goo type.
     */
    public static final Set<Block> HARDCODED = Set.of(
        Blocks.BEDROCK,
        Blocks.CHEST,
        Blocks.ENDER_CHEST,
        Blocks.TRAPPED_CHEST,
        // All shulker boxes
        Blocks.SHULKER_BOX,
        Blocks.WHITE_SHULKER_BOX,
        Blocks.ORANGE_SHULKER_BOX,
        Blocks.MAGENTA_SHULKER_BOX,
        Blocks.LIGHT_BLUE_SHULKER_BOX,
        Blocks.YELLOW_SHULKER_BOX,
        Blocks.LIME_SHULKER_BOX,
        Blocks.PINK_SHULKER_BOX,
        Blocks.GRAY_SHULKER_BOX,
        Blocks.LIGHT_GRAY_SHULKER_BOX,
        Blocks.CYAN_SHULKER_BOX,
        Blocks.PURPLE_SHULKER_BOX,
        Blocks.BLUE_SHULKER_BOX,
        Blocks.BROWN_SHULKER_BOX,
        Blocks.GREEN_SHULKER_BOX,
        Blocks.RED_SHULKER_BOX,
        Blocks.BLACK_SHULKER_BOX,
        // Portal blocks
        Blocks.END_PORTAL,
        Blocks.END_PORTAL_FRAME,
        Blocks.END_GATEWAY,
        Blocks.NETHER_PORTAL
    );

    /**
     * Layer 3: Config-based protected blocks.
     * Loaded from config file.
     */
    private static Set<Block> configBlocks = new HashSet<>();

    /**
     * Check if a block state is protected from goo consumption.
     */
    public static boolean isProtected(BlockState state) {
        Block block = state.getBlock();

        // Layer 1: Hardcoded
        if (HARDCODED.contains(block)) {
            return true;
        }

        // Layer 2: Tag-based
        if (state.is(GreyGooTags.Blocks.GOO_PROTECTED)) {
            return true;
        }

        // Layer 3: Config-based
        if (configBlocks.contains(block)) {
            return true;
        }

        return false;
    }

    /**
     * Check if a block is protected (convenience method).
     */
    public static boolean isProtected(Block block) {
        if (HARDCODED.contains(block)) {
            return true;
        }
        if (configBlocks.contains(block)) {
            return true;
        }
        // Can't check tag without state
        return false;
    }

    /**
     * Load protected blocks from config.
     *
     * @param blockIds List of block IDs (e.g., "minecraft:dragon_egg")
     */
    public static void loadFromConfig(List<String> blockIds) {
        configBlocks = blockIds.stream()
            .map(id -> {
                try {
                    ResourceLocation loc = new ResourceLocation(id);
                    Block block = ForgeRegistries.BLOCKS.getValue(loc);
                    if (block == null || block == Blocks.AIR) {
                        LOGGER.warn("Unknown block in protected config: {}", id);
                        return null;
                    }
                    return block;
                } catch (Exception e) {
                    LOGGER.warn("Invalid block ID in protected config: {}", id);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        LOGGER.info("Loaded {} protected blocks from config", configBlocks.size());
    }

    /**
     * Add a block to the config-based protection list.
     */
    public static void addProtected(Block block) {
        configBlocks.add(block);
    }

    /**
     * Remove a block from the config-based protection list.
     * Note: Cannot remove hardcoded or tag-based protection.
     */
    public static void removeProtected(Block block) {
        configBlocks.remove(block);
    }

    /**
     * Get all config-based protected blocks.
     */
    public static Set<Block> getConfigProtected() {
        return new HashSet<>(configBlocks);
    }

    /**
     * Clear config-based protected blocks (for reloading).
     */
    public static void clearConfig() {
        configBlocks.clear();
    }
}
