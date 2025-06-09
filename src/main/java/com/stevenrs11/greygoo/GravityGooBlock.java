package com.stevenrs11.greygoo;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class GravityGooBlock extends FallingBlock {
    private static final Set<Block> NEVER_EAT = Set.of(
            Blocks.BEDROCK,
            Blocks.CHEST,
            Blocks.ENDER_CHEST
    );

    public GravityGooBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.SAND).randomTicks());
    }

    private void spread(ServerLevel level, BlockPos pos) {
        boolean hasFood = false;
        for (Direction dir : Direction.values()) {
            BlockPos target = pos.relative(dir);
            BlockState targetState = level.getBlockState(target);
            Block block = targetState.getBlock();
            if (block == GreyGooMod.CLEANER_BLOCK.get()) {
                level.setBlockAndUpdate(pos, GreyGooMod.CLEANER_BLOCK.get().defaultBlockState());
                return;
            }
            if (!targetState.isAir()
                    && !NEVER_EAT.contains(block)
                    && !targetState.getFluidState().is(FluidTags.WATER)
                    && !targetState.getFluidState().is(FluidTags.LAVA)) {
                level.setBlockAndUpdate(target, defaultBlockState());
                hasFood = true;
            }
        }
        if (!hasFood) {
            level.destroyBlock(pos, false);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        spread(level, pos);
        super.randomTick(state, level, pos, random);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            spread((ServerLevel) level, pos);
        }
        return InteractionResult.SUCCESS;
    }
}
