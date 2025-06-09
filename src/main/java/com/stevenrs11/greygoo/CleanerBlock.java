package com.stevenrs11.greygoo;

import net.minecraft.core.BlockPos;
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
    private static final int RADIUS = 2;
    public CleanerBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE).randomTicks());
    }

    private void clean(ServerLevel level, BlockPos pos) {
        boolean found = false;
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) > RADIUS) {
                        continue;
                    }
                    BlockPos target = pos.offset(dx, dy, dz);
                    BlockState targetState = level.getBlockState(target);
                    if (targetState.is(GreyGooMod.CLEANER_BLOCK.get())) {
                        continue;
                    }
                    if (GreyGooMod.isGoo(targetState.getBlock())) {
                        level.setBlockAndUpdate(target, defaultBlockState());
                        found = true;
                    }
                }
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
