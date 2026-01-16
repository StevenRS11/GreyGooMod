package com.stevenrs11.greygoo.blocks.consumer;

import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.core.RandomTickGooBlock;
import com.stevenrs11.greygoo.interaction.GooInteraction;
import com.stevenrs11.greygoo.interaction.ProtectedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The classic Grey Goo - consumes all non-protected blocks.
 * Spreads via random ticks, dies when no food is found.
 */
public class GreyGooBlock extends RandomTickGooBlock {

    public GreyGooBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
    }

    @Override
    public GooType getGooType() {
        return GooType.GREY_GOO;
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
                // Already handled in base class, but double-check
                handleInteraction(level, pos, target, interaction);
                return;
            }

            if (interaction == GooInteraction.IGNORE) {
                continue;
            }

            // Eat non-air blocks
            if (!targetState.isAir()) {
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
        // Grey goo becomes inert (green goo) when it has no food
        // This matches the original behavior where grey goo doesn't die, just stops spreading
        net.minecraft.world.level.block.Block inertBlock = GooType.INERT.getBlock();
        if (inertBlock != null) {
            level.setBlockAndUpdate(pos, inertBlock.defaultBlockState());
        }
        // If inert block not available, just stay as grey goo (no destruction)
    }
}
