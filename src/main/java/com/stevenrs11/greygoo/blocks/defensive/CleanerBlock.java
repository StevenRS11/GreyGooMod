package com.stevenrs11.greygoo.blocks.defensive;

import com.stevenrs11.greygoo.GreyGooMod;
import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.core.RandomTickGooBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Cleaner Block (Red Goo) - removes other goo in a radius.
 * Spreads into adjacent goo blocks, converting them to cleaner.
 * Uses Manhattan distance radius of 2 (original behavior).
 * Dies when no goo is found to clean.
 */
public class CleanerBlock extends RandomTickGooBlock {

    private static final int RADIUS = 2;

    public CleanerBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
    }

    @Override
    public GooType getGooType() {
        return GooType.CLEANER;
    }

    @Override
    protected void doSpread(ServerLevel level, BlockPos pos, RandomSource random) {
        boolean foundGoo = false;

        // Search in Manhattan distance radius
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    // Skip self
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }

                    // Manhattan distance check
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) > RADIUS) {
                        continue;
                    }

                    BlockPos target = pos.offset(dx, dy, dz);
                    BlockState targetState = level.getBlockState(target);

                    // Skip other cleaners
                    if (targetState.is(this)) {
                        continue;
                    }

                    // Convert any goo to cleaner
                    if (GooType.isGoo(targetState.getBlock())) {
                        level.setBlockAndUpdate(target, defaultBlockState());
                        foundGoo = true;
                        recordSpread();
                    }
                }
            }
        }

        if (!foundGoo) {
            onStarve(level, pos, level.getBlockState(pos));
        }
    }

    @Override
    protected void onStarve(ServerLevel level, BlockPos pos, BlockState state) {
        // Cleaner dies (converts to air) when no goo is found
        level.destroyBlock(pos, false);
    }
}
