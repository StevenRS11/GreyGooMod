package com.stevenrs11.greygoo.core;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base class for inert goo blocks that never spread.
 *
 * Used by:
 * - InertBlock (green goo - foundation for color variants)
 * - Cancer2Block (inert destroyer state)
 * - TGDInertBlock (inert TGD state)
 */
public abstract class InertGooBlock extends AbstractGooBlock {

    public InertGooBlock(Properties properties) {
        super(properties);  // No randomTicks - never spreads
    }

    @Override
    protected final void doSpread(ServerLevel level, BlockPos pos, RandomSource random) {
        // Inert blocks never spread
    }

    @Override
    protected final void onStarve(ServerLevel level, BlockPos pos, BlockState state) {
        // Inert blocks never starve - they persist forever
    }
}
