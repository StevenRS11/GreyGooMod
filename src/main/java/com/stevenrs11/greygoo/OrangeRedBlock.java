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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class OrangeRedBlock extends Block {
    private static final Set<Block> NEVER_EAT = Set.of(
            Blocks.BEDROCK,
            Blocks.CHEST,
            Blocks.ENDER_CHEST
    );

    public OrangeRedBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE).noRandomTicks());
    }

    private void spread(ServerLevel level, BlockPos pos) {
        boolean found = false;
        for (Direction dir : Direction.values()) {
            BlockPos behind = pos.relative(dir.getOpposite());
            if (!level.getBlockState(behind).is(this)) {
                continue;
            }
            BlockPos target = pos.relative(dir);
            BlockState targetState = level.getBlockState(target);
            Block block = targetState.getBlock();
            if (block == GreyGooMod.CLEANER_BLOCK.get()) {
                level.setBlockAndUpdate(pos, GreyGooMod.CLEANER_BLOCK.get().defaultBlockState());
                return;
            }
            if (!targetState.isAir()
                    && !NEVER_EAT.contains(block)
                    && !targetState.is(this)
                    && !targetState.getFluidState().is(FluidTags.WATER)
                    && !targetState.getFluidState().is(FluidTags.LAVA)) {
                level.setBlockAndUpdate(target, defaultBlockState());
                level.scheduleTick(target, this, level.getRandom().nextInt(3) + 1);
                found = true;
            }
        }
        if (!found) {
            level.removeBlock(pos, false);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        spread(level, pos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && !player.isCrouching()) {
            spread((ServerLevel) level, pos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 1);
        }
    }
}
