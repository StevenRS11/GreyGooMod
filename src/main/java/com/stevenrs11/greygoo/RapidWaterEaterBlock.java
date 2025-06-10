package com.stevenrs11.greygoo;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class RapidWaterEaterBlock extends Block {
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 50);

    public RapidWaterEaterBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE).noRandomTicks());
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    private void spreadCube(ServerLevel level, BlockPos pos, BlockState state, boolean removeSelf) {
        int stage = state.getValue(STAGE);
        if (stage >= 50) {
            if (removeSelf) {
                level.removeBlock(pos, false);
            }
            return;
        }
        boolean found = false;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }
                    BlockPos target = pos.offset(dx, dy, dz);
                    BlockState targetState = level.getBlockState(target);
                    if (targetState.is(GreyGooMod.CLEANER_BLOCK.get())) {
                        level.setBlockAndUpdate(pos, GreyGooMod.CLEANER_BLOCK.get().defaultBlockState());
                        return;
                    }
                    if (targetState.getFluidState().is(FluidTags.WATER) || targetState.getFluidState().is(FluidTags.LAVA)) {
                        level.setBlockAndUpdate(target, defaultBlockState().setValue(STAGE, stage + 1));
                        level.scheduleTick(target, this, level.getRandom().nextInt(3));
                        found = true;
                    }
                }
            }
        }
        if (removeSelf) {
            level.removeBlock(pos, false);
        } else if (!found) {
            level.removeBlock(pos, false);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        spreadCube(level, pos, state, true);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && !player.isCrouching()) {
            spreadCube((ServerLevel) level, pos, state.setValue(STAGE, 0), false);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide && state.getValue(STAGE) > 0) {
            level.scheduleTick(pos, this, level.getRandom().nextInt(3));
        }
    }
}
