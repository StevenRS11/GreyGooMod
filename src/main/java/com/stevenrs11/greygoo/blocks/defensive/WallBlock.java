package com.stevenrs11.greygoo.blocks.defensive;

import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.core.RandomTickGooBlock;
import com.stevenrs11.greygoo.interaction.GooInteraction;
import com.stevenrs11.greygoo.interaction.ProtectedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Wall Block - defensive goo that spreads with a foundation requirement.
 * Original behavior: can only spread in a direction if there's a Wall block
 * on the opposite side (creates a "growing wall" effect).
 * Dies when no valid spread targets are found.
 */
public class WallBlock extends RandomTickGooBlock {

    public WallBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
    }

    @Override
    public GooType getGooType() {
        return GooType.WALL;
    }

    @Override
    protected void doSpread(ServerLevel level, BlockPos pos, RandomSource random) {
        boolean hasFood = false;

        // Original behavior: check 6 adjacent directions
        // For each direction, only spread if there's a wall on the opposite side
        for (Direction dir : Direction.values()) {
            BlockPos target = pos.relative(dir);
            BlockPos opposite = pos.relative(dir.getOpposite());

            // Check if opposite side has a wall (foundation requirement)
            BlockState oppositeState = level.getBlockState(opposite);
            if (!oppositeState.is(this)) {
                continue;  // No foundation on opposite side
            }

            BlockState targetState = level.getBlockState(target);
            Block targetBlock = targetState.getBlock();

            // Check goo interactions
            GooInteraction interaction = getInteractionWith(level, target);
            if (interaction == GooInteraction.CONVERT_SELF) {
                handleInteraction(level, pos, target, interaction);
                return;
            }

            // Skip if target is already a wall
            if (targetBlock == this) {
                continue;
            }

            // Skip protected blocks
            if (ProtectedBlocks.isProtected(targetState)) {
                continue;
            }

            // Spread into the target
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
        // Wall dies when it has no valid spread targets
        level.destroyBlock(pos, false);
    }
}
