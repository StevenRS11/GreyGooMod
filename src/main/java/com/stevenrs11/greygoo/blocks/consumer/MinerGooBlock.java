package com.stevenrs11.greygoo.blocks.consumer;

import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.core.RandomTickGooBlock;
import com.stevenrs11.greygoo.interaction.GooInteraction;
import com.stevenrs11.greygoo.interaction.ProtectedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

/**
 * Miner Goo - consumes stone and earth blocks only.
 * Original behavior: eats gravel, stone, sand, sandstone, netherrack, soul sand, clay.
 * Becomes inert when no food is found.
 */
public class MinerGooBlock extends RandomTickGooBlock {

    // Blocks that miner goo can eat (matching original behavior)
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
        // Additional stone variants for 1.20.1
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

    public MinerGooBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
    }

    @Override
    public GooType getGooType() {
        return GooType.MINER_GOO;
    }

    /**
     * Check if a block is mineable by this goo.
     */
    private boolean isMineable(BlockState state) {
        Block block = state.getBlock();
        // Check direct matches first
        if (MINEABLE.contains(block)) {
            return true;
        }
        // Also check stone tag for any stone-like blocks we might have missed
        return state.is(BlockTags.BASE_STONE_OVERWORLD) ||
               state.is(BlockTags.BASE_STONE_NETHER);
    }

    @Override
    protected void doSpread(ServerLevel level, BlockPos pos, RandomSource random) {
        boolean hasFood = false;

        for (Direction dir : Direction.values()) {
            BlockPos target = pos.relative(dir);
            BlockState targetState = level.getBlockState(target);

            // Skip protected blocks
            if (ProtectedBlocks.isProtected(targetState)) {
                continue;
            }

            // Check goo interactions
            GooInteraction interaction = getInteractionWith(level, target);

            if (interaction == GooInteraction.CONVERT_SELF) {
                handleInteraction(level, pos, target, interaction);
                return;
            }

            if (interaction == GooInteraction.IGNORE) {
                continue;
            }

            // Only eat mineable blocks (stone/earth)
            if (isMineable(targetState)) {
                level.setBlockAndUpdate(target, defaultBlockState());
                hasFood = true;
                recordSpread();
            }
        }

        if (!hasFood) {
            onStarve(level, pos, level.getBlockState(pos));
        }
    }

    @Override
    protected void onStarve(ServerLevel level, BlockPos pos, BlockState state) {
        // Miner goo becomes inert when it has no food (same as grey goo)
        Block inertBlock = GooType.INERT.getBlock();
        if (inertBlock != null) {
            level.setBlockAndUpdate(pos, inertBlock.defaultBlockState());
        }
    }
}
