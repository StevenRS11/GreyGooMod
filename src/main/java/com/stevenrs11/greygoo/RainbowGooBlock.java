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
 * Rainbow Goo restores the world to its original state. When it spreads it
 * compares the current block with the block that would have been placed during
 * world generation using a deterministic calculation based on the world seed.
 * If the two differ, the goo occupies the block. When the goo can no longer
 * find any discrepancies nearby it decays back into the block that "should"
 * exist at its position.
 */
public class RainbowGooBlock extends Block {
    public RainbowGooBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE)
                .lightLevel(state -> 15)
                .randomTicks());
    }

    /**
     * Determine what block would have existed at a position using the world's
     * {@link net.minecraft.world.level.chunk.ChunkGenerator}. This mirrors the
     * old BlockRestorer logic by generating a fresh column from the chunk
     * generator with the current world seed. Feature placement is ignored but
     * the base terrain and surface rules are reproduced.
     */
    private BlockState originalBlock(ServerLevel level, BlockPos pos) {
        var generator = level.getChunkSource().getGenerator();
        var randomState = level.getChunkSource().randomState();

        var column = generator.getBaseColumn(pos.getX(), pos.getZ(), level, randomState);

        int localY = pos.getY() - column.getMinY();
        if (localY >= 0 && localY < column.getHeight()) {
            return column.getBlock(localY);
        }
        return Blocks.AIR.defaultBlockState();
    }

    private void operate(ServerLevel level, BlockPos pos, RandomSource random) {
        boolean acted = false;
        for (Direction dir : Direction.values()) {
            BlockPos target = pos.relative(dir);
            BlockState current = level.getBlockState(target);
            BlockState expected = originalBlock(level, target);

            if (GreyGooMod.isGoo(current.getBlock())) {
                // Convert existing goo back to what should be here and possibly
                // spawn new rainbow goo for further cleanup.
                level.setBlockAndUpdate(target, expected);
                if (random.nextBoolean()) {
                    level.setBlockAndUpdate(target, defaultBlockState());
                }
                acted = true;
            } else if (!current.equals(expected)) {
                // This block differs from worldgen, spread into it.
                level.setBlockAndUpdate(target, defaultBlockState());
                acted = true;
            }
        }

        if (!acted) {
            // No nearby discrepancies remain; revert to the block that would
            // have been generated originally.
            level.setBlockAndUpdate(pos, originalBlock(level, pos));
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        operate(level, pos, random);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            operate((ServerLevel) level, pos, level.getRandom());
        }
        return InteractionResult.SUCCESS;
    }
}
