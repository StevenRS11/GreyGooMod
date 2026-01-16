package com.stevenrs11.greygoo.blocks.consumer;

import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.core.RandomTickGooBlock;
import com.stevenrs11.greygoo.interaction.GooInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Water Eater - consumes water and lava.
 * Spreads via random ticks into adjacent fluid blocks.
 * Dies when no fluids are found.
 */
public class WaterEaterBlock extends RandomTickGooBlock {

    public WaterEaterBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
    }

    @Override
    public GooType getGooType() {
        return GooType.WATER_EATER;
    }

    @Override
    protected void doSpread(ServerLevel level, BlockPos pos, RandomSource random) {
        boolean hasFood = false;

        for (Direction dir : Direction.values()) {
            BlockPos target = pos.relative(dir);
            BlockState targetState = level.getBlockState(target);

            // Check goo interactions first
            GooInteraction interaction = getInteractionWith(level, target);

            if (interaction == GooInteraction.CONVERT_SELF) {
                handleInteraction(level, pos, target, interaction);
                return;
            }

            if (interaction == GooInteraction.IGNORE) {
                continue;
            }

            // Only eat water or lava
            if (targetState.getFluidState().is(FluidTags.WATER) ||
                targetState.getFluidState().is(FluidTags.LAVA)) {
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
        // Water eater dies when it has no food
        level.destroyBlock(pos, false);
    }
}
