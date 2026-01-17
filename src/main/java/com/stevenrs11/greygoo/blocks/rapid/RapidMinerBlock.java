package com.stevenrs11.greygoo.blocks.rapid;

import com.stevenrs11.greygoo.core.AbstractRapidGooBlock;
import com.stevenrs11.greygoo.core.GooType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

/**
 * Rapid Miner - fast stone/earth consumer that spreads in waves.
 *
 * Original behavior (BlockRapidMiner.java):
 * - Spreads via scheduled ticks in a 3x3x3 cube
 * - Session-based spread limiting (replaces broken metadata depth check)
 * - Timing: random.nextInt(5) + random.nextInt(18) = 0-21 ticks (faster than rapid eater)
 * - Self-destructs after spreading
 * - Right-click creates session and schedules neighbors with short delays
 * - Converts to cleaner if adjacent to cleaner block
 * - Only consumes mineable blocks (stone, gravel, sand, etc.)
 */
public class RapidMinerBlock extends AbstractRapidGooBlock {

    // Blocks that rapid miner can eat (same as MinerGooBlock)
    private static final Set<Block> MINEABLE = Set.of(
        Blocks.GRAVEL,
        Blocks.STONE,
        Blocks.SAND,
        Blocks.RED_SAND,
        Blocks.SANDSTONE,
        Blocks.RED_SANDSTONE,
        Blocks.NETHERRACK,
        Blocks.SOUL_SAND,
        Blocks.SOUL_SOIL,
        Blocks.CLAY,
        Blocks.COBBLESTONE,
        Blocks.DEEPSLATE,
        Blocks.COBBLED_DEEPSLATE,
        Blocks.TUFF,
        Blocks.GRANITE,
        Blocks.DIORITE,
        Blocks.ANDESITE,
        Blocks.CALCITE,
        Blocks.SMOOTH_BASALT,
        Blocks.BASALT
    );

    public RapidMinerBlock() {
        super();
    }

    @Override
    public GooType getGooType() {
        return GooType.RAPID_MINER;
    }

    @Override
    protected boolean canConsume(BlockState targetState) {
        Block block = targetState.getBlock();
        if (MINEABLE.contains(block)) {
            return true;
        }
        return targetState.is(BlockTags.BASE_STONE_OVERWORLD) ||
               targetState.is(BlockTags.BASE_STONE_NETHER);
    }

    @Override
    protected int getSpreadDelay(RandomSource random) {
        // Original timing: random.nextInt(5) + random.nextInt(18) = 0-21 ticks
        // Faster than rapid eater's 0-27 ticks
        return random.nextInt(5) + random.nextInt(18);
    }
}
