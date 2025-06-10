package com.stevenrs11.greygoo;

import java.util.Set;

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

public class WallBlock extends Block {
    private static final Set<Block> NEVER_EAT = Set.of(
            Blocks.BEDROCK,
            Blocks.CHEST,
            Blocks.ENDER_CHEST
    );

    public WallBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE).randomTicks());
    }

    private void grow(ServerLevel level, BlockPos pos) {
        boolean hasFood = false;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) >= 2) {
                        continue;
                    }
                    BlockPos opposite = pos.offset(-dx, -dy, -dz);
                    BlockState oppositeState = level.getBlockState(opposite);
                    if (!oppositeState.is(this)) {
                        continue;
                    }

                    BlockPos target = pos.offset(dx, dy, dz);
                    BlockState targetState = level.getBlockState(target);
                    Block targetBlock = targetState.getBlock();
                    if (targetBlock == GreyGooMod.CLEANER_BLOCK.get()) {
                        level.setBlockAndUpdate(pos, GreyGooMod.CLEANER_BLOCK.get().defaultBlockState());
                        return;
                    }
                    if (targetBlock == this) {
                        continue;
                    }
                    if (!NEVER_EAT.contains(targetBlock)) {
                        level.setBlockAndUpdate(target, defaultBlockState());
                        hasFood = true;
                    }
                }
            }
        }
        if (!hasFood) {
            level.destroyBlock(pos, false);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        grow(level, pos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            grow((ServerLevel) level, pos);
        }
        return InteractionResult.SUCCESS;
    }
}
