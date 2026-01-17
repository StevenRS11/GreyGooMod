package com.stevenrs11.greygoo.blocks.destroyer;

import com.stevenrs11.greygoo.GreyGooMod;
import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.core.RandomTickGooBlock;
import com.stevenrs11.greygoo.interaction.ProtectedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * TGD Block - The Grey Death. Builds vertical towers up to bloom height.
 *
 * Original behavior (BlockTGD.java):
 * - Uses random ticks
 * - Builds upward in towers until reaching bloom height
 * - At bloom height, spreads horizontally and turns to TGDInert
 * - Won't spread into: Cleaner, TGDInert, OrangeRed, Black, Wall, Inert, Freezer, GreyEater, chest, protected
 * - Converts to Cleaner if adjacent
 * - Converts to TGDInert if adjacent
 * - Converts self to TGDInert after spreading
 * - Has decay logic that converts to TGDInert when surrounded by TGD
 * - Golem spawning disabled per architecture plan
 */
public class TGDBlock extends RandomTickGooBlock {

    // Bloom height - where TGD stops building up and spreads horizontally
    // Original default was around y=200, we'll use 200 for similar behavior
    private static final int BLOOM_HEIGHT = 200;
    private static final int MAX_HEIGHT = 240;

    public TGDBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
    }

    @Override
    public GooType getGooType() {
        return GooType.TGD;
    }

    @Override
    protected void doSpread(ServerLevel level, BlockPos pos, RandomSource random) {
        int y = pos.getY();

        // Below bloom height - build towers upward
        if (y < BLOOM_HEIGHT || (y > BLOOM_HEIGHT + 13 && y < MAX_HEIGHT)) {
            buildTower(level, pos, random);
        }

        // At bloom height or max height - spread horizontally
        if (y == BLOOM_HEIGHT || y == MAX_HEIGHT) {
            spreadHorizontally(level, pos, random);
        }

        // Run decay logic
        decay(level, pos);
    }

    /**
     * Build vertical towers upward.
     */
    private void buildTower(ServerLevel level, BlockPos pos, RandomSource random) {
        // Check for Cleaner or TGDInert in 7x7x7 area
        for (int dx = -3; dx < 4; dx++) {
            for (int dy = -3; dy < 4; dy++) {
                for (int dz = -3; dz < 4; dz++) {
                    BlockPos checkPos = pos.offset(dx, Math.abs(dy), dz);
                    BlockState checkState = level.getBlockState(checkPos);

                    if (checkState.is(GreyGooMod.CLEANER_BLOCK.get())) {
                        level.setBlockAndUpdate(pos, GreyGooMod.CLEANER_BLOCK.get().defaultBlockState());
                        return;
                    }

                    if (checkState.is(GreyGooMod.TGD_INERT_BLOCK.get())) {
                        level.setBlockAndUpdate(pos, GreyGooMod.TGD_INERT_BLOCK.get().defaultBlockState());
                        return;
                    }
                }
            }
        }

        // Build upward - orthogonal spread only (Manhattan == 1)
        for (int dx = -3; dx < 4; dx++) {
            for (int dy = -3; dy < 4; dy++) {
                for (int dz = -3; dz < 4; dz++) {
                    int manhattan = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                    if (manhattan != 1) continue;

                    BlockPos target = pos.offset(dx, Math.abs(dy), dz);
                    BlockState targetState = level.getBlockState(target);

                    // Skip blocks we can't spread into
                    if (!canSpreadInto(targetState)) continue;

                    // Build tower upward
                    int k1 = 0;

                    if (k1 < 3 && dx % 2 == 0) {
                        int l3 = random.nextInt(6);
                        level.setBlockAndUpdate(pos.above(Math.abs(dy)), defaultBlockState());
                        recordSpread();

                        if (random.nextInt(8) == 0) {
                            level.scheduleTick(pos.above(Math.abs(dy)), this, random.nextInt(15));
                        }

                        if (l3 == 1) {
                            level.setBlockAndUpdate(pos.offset(dx, 0, dz), defaultBlockState());
                            recordSpread();
                        }

                        level.setBlockAndUpdate(pos.above(Math.abs(dy) + 2), defaultBlockState());
                        recordSpread();
                    } else {
                        int i4 = random.nextInt(6);
                        level.setBlockAndUpdate(pos.above(Math.abs(dy)), defaultBlockState());
                        recordSpread();

                        if (i4 == 1) {
                            level.setBlockAndUpdate(pos.offset(-dx, 0, -dz), defaultBlockState());
                            recordSpread();
                        }

                        level.setBlockAndUpdate(pos.above(Math.abs(dy) + 1), defaultBlockState());
                        recordSpread();

                        if (random.nextInt(10) == 0) {
                            level.scheduleTick(pos.above(Math.abs(dy) + 1), this, random.nextInt(15));
                        }
                    }

                    if (k1 < 2) {
                        level.setBlockAndUpdate(pos.above(Math.abs(dy) + 2), defaultBlockState());
                        recordSpread();

                        if (random.nextInt(11) == 0) {
                            level.scheduleTick(pos.above(Math.abs(dy) + 2), this, random.nextInt(15));
                        }
                    }

                    // Convert self to TGDInert after spreading
                    level.setBlockAndUpdate(pos, GreyGooMod.TGD_INERT_BLOCK.get().defaultBlockState());
                    return;
                }
            }
        }
    }

    /**
     * Spread horizontally at bloom height.
     */
    private void spreadHorizontally(ServerLevel level, BlockPos pos, RandomSource random) {
        // Note: Golem spawning disabled per architecture plan

        for (int l2 = 1; l2 < 4; l2++) {
            int j3 = random.nextInt(6);

            // Spread in -X direction
            BlockPos targetNegX = pos.offset(-j3, 0, 0);
            BlockState stateNegX = level.getBlockState(targetNegX);
            if (!stateNegX.is(GreyGooMod.CLEANER_BLOCK.get()) &&
                !stateNegX.is(GreyGooMod.TGD_INERT_BLOCK.get())) {
                level.setBlockAndUpdate(targetNegX, defaultBlockState());
                recordSpread();

                if (random.nextInt(12) == 0) {
                    level.scheduleTick(targetNegX, this, random.nextInt(8));
                }
            }

            // Spread in +X direction
            BlockPos targetPosX = pos.offset(j3, 0, 0);
            BlockState statePosX = level.getBlockState(targetPosX);
            if (!statePosX.is(GreyGooMod.CLEANER_BLOCK.get()) &&
                !statePosX.is(GreyGooMod.TGD_INERT_BLOCK.get())) {
                level.setBlockAndUpdate(targetPosX, defaultBlockState());
                recordSpread();

                if (random.nextInt(12) == 0) {
                    level.scheduleTick(targetPosX, this, random.nextInt(8));
                }
            }

            // Spread in -Z direction
            BlockPos targetNegZ = pos.offset(0, 0, -j3);
            BlockState stateNegZ = level.getBlockState(targetNegZ);
            if (!stateNegZ.is(GreyGooMod.CLEANER_BLOCK.get()) &&
                !stateNegZ.is(GreyGooMod.TGD_INERT_BLOCK.get())) {
                level.setBlockAndUpdate(targetNegZ, defaultBlockState());
                recordSpread();

                if (random.nextInt(12) == 0) {
                    level.scheduleTick(targetNegZ, this, random.nextInt(8));
                }
            }

            // Spread in +Z direction
            BlockPos targetPosZ = pos.offset(0, 0, j3);
            BlockState statePosZ = level.getBlockState(targetPosZ);
            if (!statePosZ.is(GreyGooMod.CLEANER_BLOCK.get()) &&
                !statePosZ.is(GreyGooMod.TGD_INERT_BLOCK.get())) {
                level.setBlockAndUpdate(targetPosZ, defaultBlockState());
                recordSpread();

                if (random.nextInt(12) == 0) {
                    level.scheduleTick(targetPosZ, this, random.nextInt(8));
                }
            }

            // Convert self to TGDInert
            level.setBlockAndUpdate(pos, GreyGooMod.TGD_INERT_BLOCK.get().defaultBlockState());
        }
    }

    /**
     * Decay logic - converts to TGDInert when surrounded by TGD.
     */
    private void decay(ServerLevel level, BlockPos pos) {
        int tgdCount = 0;
        int airCount = 0;

        // Check at Manhattan distance 4
        for (int dx = -5; dx < 4; dx++) {
            for (int dz = -5; dz < 4; dz++) {
                int manhattan = Math.abs(dx) + Math.abs(dz);
                if (manhattan != 0 && manhattan == 4) {
                    BlockPos checkPos = pos.offset(dx, 0, dz);
                    BlockState state = level.getBlockState(checkPos);

                    if (state.is(this)) {
                        tgdCount++;
                    }
                    if (state.isAir()) {
                        airCount++;
                    }
                }
            }
        }

        if (tgdCount != 0) {
            // Convert nearby TGD to TGDInert
            for (int dx = -1; dx < 2; dx++) {
                for (int dz = -1; dz < 2; dz++) {
                    BlockPos checkPos = pos.offset(dx, 0, dz);
                    if (level.getBlockState(checkPos).is(this)) {
                        level.setBlockAndUpdate(checkPos, GreyGooMod.TGD_INERT_BLOCK.get().defaultBlockState());
                    }
                }
            }
            BlockPos below = pos.below();
            if (level.getBlockState(below).is(this)) {
                level.setBlockAndUpdate(below, GreyGooMod.TGD_INERT_BLOCK.get().defaultBlockState());
            }
        }
    }

    /**
     * Check if TGD can spread into this block.
     */
    private boolean canSpreadInto(BlockState state) {
        if (state.isAir()) return false;

        Block block = state.getBlock();

        // Can't spread into these
        if (block == GreyGooMod.CLEANER_BLOCK.get()) return false;
        if (block == GreyGooMod.TGD_INERT_BLOCK.get()) return false;
        if (block == GreyGooMod.BLACK_DESTROYER_BLOCK.get()) return false;
        if (block == GreyGooMod.WALL_BLOCK.get()) return false;
        if (block == GreyGooMod.INERT_BLOCK.get()) return false;
        if (block == GreyGooMod.GREY_GOO_BLOCK.get()) return false;  // GreyEater equivalent
        if (block == this) return false;

        // Protected blocks
        if (ProtectedBlocks.isProtected(state)) return false;

        return true;
    }

    @Override
    protected void onStarve(ServerLevel level, BlockPos pos, BlockState state) {
        // TGD doesn't starve in the traditional sense - it converts to TGDInert
        level.setBlockAndUpdate(pos, GreyGooMod.TGD_INERT_BLOCK.get().defaultBlockState());
    }
}
