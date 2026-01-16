package com.stevenrs11.greygoo.tags;

import com.stevenrs11.greygoo.GreyGooMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**
 * Tag keys for the Grey Goo mod.
 */
public class GreyGooTags {

    public static class Blocks {
        /**
         * Blocks in this tag are protected from goo consumption.
         * Modpacks can add blocks to this tag via datapacks.
         *
         * Location: data/greygoo/tags/blocks/protected.json
         */
        public static final TagKey<Block> GOO_PROTECTED = tag("protected");

        /**
         * Blocks that count as "edible" for specific goo types.
         * Can be used to expand what certain goos can eat.
         */
        public static final TagKey<Block> MINER_TARGETS = tag("miner_targets");

        /**
         * All goo blocks. Useful for other mods to detect goo.
         */
        public static final TagKey<Block> GOO_BLOCKS = tag("goo_blocks");

        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation(GreyGooMod.MODID, name));
        }
    }
}
