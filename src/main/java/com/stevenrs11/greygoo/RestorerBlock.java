package com.stevenrs11.greygoo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A simplified "rainbow" goo that attempts to restore terrain by
 * replacing nearby goo blocks with stone. If no goo remains
 * adjacent it destroys itself.
 */
public class RestorerBlock extends Block {
    public RestorerBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE).randomTicks());
    }

    private void restore(ServerLevel level, BlockPos pos) {
        boolean found = false;
        for (Direction dir : Direction.values()) {
            BlockPos target = pos.relative(dir);
            BlockState targetState = level.getBlockState(target);
            if (GreyGooMod.isGoo(targetState.getBlock())) {
                level.setBlockAndUpdate(target, Blocks.STONE.defaultBlockState());
                if (level.random.nextBoolean()) {
                    level.setBlockAndUpdate(target, defaultBlockState());
                }
                found = true;
            }
        }
        if (!found) {
            level.destroyBlock(pos, false);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        restore(level, pos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            restore((ServerLevel) level, pos);
        }
        return InteractionResult.SUCCESS;
    }
}
