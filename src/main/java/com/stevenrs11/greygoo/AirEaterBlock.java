package com.stevenrs11.greygoo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AirEaterBlock extends Block {
    public AirEaterBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE).randomTicks());
    }

    private void spread(ServerLevel level, BlockPos pos) {
        boolean hasFood = false;
        for (Direction dir : Direction.values()) {
            BlockPos target = pos.relative(dir);
            BlockState targetState = level.getBlockState(target);
            if (targetState.is(GreyGooMod.CLEANER_BLOCK.get())) {
                level.setBlockAndUpdate(pos, GreyGooMod.CLEANER_BLOCK.get().defaultBlockState());
                return;
            }
            if (targetState.isAir() && level.getBrightness(LightLayer.SKY, target) > 7) {
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
