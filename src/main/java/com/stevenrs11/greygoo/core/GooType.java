package com.stevenrs11.greygoo.core;

import net.minecraft.world.level.block.Block;
import com.stevenrs11.greygoo.GreyGooMod;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Enum of all goo types in the mod.
 * Each type has configurable spread limits and can be mapped to/from blocks.
 */
public enum GooType {
    // Consumer goos
    GREY_GOO("grey_goo", 100, 50, 0.8f),
    AIR_EATER("air_eater", 100, 50, 0.8f),
    WATER_EATER("water_eater", 100, 50, 0.8f),
    MINER_GOO("miner_goo", 100, 50, 0.8f),
    GREY_EATER("grey_eater", 100, 50, 0.8f),

    // Rapid variants (higher limits due to self-limiting depth)
    RAPID_EATER("rapid_eater", 150, 75, 0.8f),
    RAPID_WATER_EATER("rapid_water_eater", 150, 75, 0.8f),
    RAPID_MINER("rapid_miner", 150, 75, 0.8f),

    // Defensive
    CLEANER("cleaner", 80, 40, 0.8f),
    WALL("wall", 60, 30, 0.8f),
    INERT("inert", 0, 0, 0.0f),  // Never spreads
    RESTORER("restorer", 100, 60, 0.8f),
    FREEZER("freezer", 50, 25, 0.8f),

    // Destroyers
    BLACK_DESTROYER("black_destroyer", 70, 35, 0.9f),
    CANCER("cancer", 70, 35, 0.9f),
    CANCER2("cancer2", 0, 0, 0.0f),  // Inert
    TGD("tgd", 50, 25, 0.9f),
    TGD_INERT("tgd_inert", 0, 0, 0.0f),  // Inert

    // Gravity (special limiter for falling)
    GRAVITY_GOO("gravity_goo", 25, 15, 0.7f),

    // Color variants
    ORANGE_RED("orange_red", 80, 40, 0.8f),
    ORANGE_WHITE("orange_white", 80, 40, 0.8f),
    ORANGE_PURPLE("orange_purple", 80, 40, 0.8f),

    // Special (kept from current implementation)
    REDYELLOW("redyellow", 80, 40, 0.8f),
    BUBBLE("bubble", 100, 50, 0.8f);

    public final String id;
    public final int defaultMaxPerTick;
    public final int defaultSoftCap;
    public final float defaultThrottleScale;

    // Block mapping (populated during mod init)
    private static final Map<Block, GooType> BLOCK_TO_TYPE = new HashMap<>();
    private Supplier<Block> blockSupplier;

    GooType(String id, int defaultMaxPerTick, int defaultSoftCap, float defaultThrottleScale) {
        this.id = id;
        this.defaultMaxPerTick = defaultMaxPerTick;
        this.defaultSoftCap = defaultSoftCap;
        this.defaultThrottleScale = defaultThrottleScale;
    }

    /**
     * Register the block supplier for this goo type.
     * Called during mod initialization.
     */
    public void registerBlock(Supplier<Block> supplier) {
        this.blockSupplier = supplier;
    }

    /**
     * Get the block for this goo type.
     */
    @Nullable
    public Block getBlock() {
        return blockSupplier != null ? blockSupplier.get() : null;
    }

    /**
     * Initialize the block-to-type mapping.
     * Call after all blocks are registered.
     */
    public static void initBlockMapping() {
        BLOCK_TO_TYPE.clear();
        for (GooType type : values()) {
            Block block = type.getBlock();
            if (block != null) {
                BLOCK_TO_TYPE.put(block, type);
            }
        }
    }

    /**
     * Get the GooType for a given block, or null if not a goo block.
     */
    @Nullable
    public static GooType fromBlock(Block block) {
        return BLOCK_TO_TYPE.get(block);
    }

    /**
     * Check if a block is any type of goo.
     */
    public static boolean isGoo(Block block) {
        return BLOCK_TO_TYPE.containsKey(block);
    }

    /**
     * Get a GooType by its string ID.
     */
    @Nullable
    public static GooType fromId(String id) {
        for (GooType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}
