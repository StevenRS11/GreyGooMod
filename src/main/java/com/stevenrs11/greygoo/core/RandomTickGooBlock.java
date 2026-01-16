package com.stevenrs11.greygoo.core;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Base class for goo blocks that spread via Minecraft's random tick system.
 *
 * Random tick spreading is slower and less predictable, suitable for:
 * - GreyGoo, AirEater, WaterEater, MinerGoo
 * - CleanerBlock, WallBlock
 * - BlackDestroyer, Cancer, TGD
 * - GravityGoo
 */
public abstract class RandomTickGooBlock extends AbstractGooBlock {

    public RandomTickGooBlock(Properties properties) {
        super(properties.randomTicks());
    }

    @Override
    public final void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Check spread limiter first
        if (!canSpread(level, random)) {
            return;
        }

        // Check for cleaner infection (highest priority)
        if (checkCleanerInfection(level, pos)) {
            return;  // We were converted, stop
        }

        // Delegate spreading to subclass
        doSpread(level, pos, random);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                  Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            // Manual spread on right-click (ignores limiter for testing)
            doSpread((ServerLevel) level, pos, level.getRandom());
        }
        return InteractionResult.SUCCESS;
    }
}
