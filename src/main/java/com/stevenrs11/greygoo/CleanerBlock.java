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

public class CleanerBlock extends Block {
    public CleanerBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE).randomTicks());
    }

    private void clean(ServerLevel level, BlockPos pos) {
        boolean found = false;
        for (Direction dir : Direction.values()) {
            BlockPos target = pos.relative(dir);
            BlockState targetState = level.getBlockState(target);
            if (targetState.is(GreyGooMod.CLEANER_BLOCK.get())) {
                continue;
            }
            if (GreyGooMod.isGoo(targetState.getBlock())) {
                level.setBlockAndUpdate(target, defaultBlockState());
                found = true;
            }
        }
        if (!found) {
            level.destroyBlock(pos, false);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        clean(level, pos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            clean((ServerLevel) level, pos);
        }
        return InteractionResult.SUCCESS;
    }
}
