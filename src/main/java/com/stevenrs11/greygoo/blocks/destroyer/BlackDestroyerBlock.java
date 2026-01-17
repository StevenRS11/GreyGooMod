package com.stevenrs11.greygoo.blocks.destroyer;

import com.stevenrs11.greygoo.GreyGooMod;
import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.core.RandomTickGooBlock;
import com.stevenrs11.greygoo.interaction.ProtectedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Black Destroyer - aggressive destroyer that converts to Cancer2 when starving.
 *
 * Original behavior (BlockBlack.java):
 * - Uses random ticks
 * - Spreads in 5x5x5 cube (-2 to +2)
 * - 1/60 chance to spread per target, with density control
 * - Won't eat: Cancer, Cancer2, TGD, TGDInert, Cleaner, Wall, Inert, Freezer, chest, protected
 * - Converts to Cleaner if adjacent
 * - Transforms to Cancer2 when no food in 7x7x7 area
 * - Becomes inactive (metadata 2) when no food found this tick
 * - Portal particles on display tick
 * - Entity walking triggers spread
 */
public class BlackDestroyerBlock extends RandomTickGooBlock {

    public static final BooleanProperty INACTIVE = BooleanProperty.create("inactive");

    public BlackDestroyerBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(INACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(INACTIVE);
    }

    @Override
    public GooType getGooType() {
        return GooType.BLACK_DESTROYER;
    }

    @Override
    protected void doSpread(ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState currentState = level.getBlockState(pos);

        // Check if inactive - skip spreading
        if (currentState.getValue(INACTIVE)) {
            return;
        }

        // Check for food in larger area (7x7x7) - if none, transform to Cancer2
        boolean hasFoodInArea = checkForFoodInArea(level, pos, 3);
        if (!hasFoodInArea) {
            level.setBlockAndUpdate(pos, GreyGooMod.CANCER2_BLOCK.get().defaultBlockState());
            return;
        }

        boolean hasFood = false;
        int numberOfBlack = 0;

        // Count nearby black destroyers for density control
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos checkPos = pos.offset(dx, dy, dz);
                    if (level.getBlockState(checkPos).is(this)) {
                        numberOfBlack++;
                    }
                }
            }
        }

        // Spread in 5x5x5 cube
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;

                    BlockPos target = pos.offset(dx, dy, dz);
                    BlockState targetState = level.getBlockState(target);

                    // Check for cleaner - convert self
                    if (targetState.is(GreyGooMod.CLEANER_BLOCK.get())) {
                        level.setBlockAndUpdate(pos, GreyGooMod.CLEANER_BLOCK.get().defaultBlockState());
                        return;
                    }

                    // Skip blocks we can't eat
                    if (!canEat(targetState)) {
                        continue;
                    }

                    // 1/60 chance to spread, with density control
                    // Don't spread if too dense (>15 nearby) and too close to center
                    // Don't spread if >100 nearby
                    if (random.nextInt(60) == 0) {
                        int distance = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                        if (numberOfBlack > 15 && distance < 3) {
                            continue;
                        }
                        if (numberOfBlack >= 100) {
                            continue;
                        }

                        level.setBlockAndUpdate(target, defaultBlockState());
                        hasFood = true;
                        recordSpread();
                    }
                }
            }
        }

        if (!hasFood) {
            onStarve(level, pos, currentState);
        }
    }

    /**
     * Check if there's any edible food in the given radius.
     */
    private boolean checkForFoodInArea(ServerLevel level, BlockPos pos, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos checkPos = pos.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(checkPos);
                    if (!state.isAir() && canEat(state)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check if this block can be eaten by the destroyer.
     */
    private boolean canEat(BlockState state) {
        if (state.isAir()) return false;

        Block block = state.getBlock();

        // Can't eat other destroyers
        if (block == this) return false;
        if (block == GreyGooMod.CANCER_BLOCK.get()) return false;
        if (block == GreyGooMod.CANCER2_BLOCK.get()) return false;
        if (block == GreyGooMod.TGD_BLOCK.get()) return false;
        if (block == GreyGooMod.TGD_INERT_BLOCK.get()) return false;

        // Can't eat defensive blocks
        if (block == GreyGooMod.CLEANER_BLOCK.get()) return false;
        if (block == GreyGooMod.WALL_BLOCK.get()) return false;
        if (block == GreyGooMod.INERT_BLOCK.get()) return false;

        // Can't eat protected blocks
        if (ProtectedBlocks.isProtected(state)) return false;

        return true;
    }

    @Override
    protected void onStarve(ServerLevel level, BlockPos pos, BlockState state) {
        // Become inactive (stop spreading but stay)
        level.setBlockAndUpdate(pos, state.setValue(INACTIVE, true));
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide) {
            doSpread((ServerLevel) level, pos, level.getRandom());
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // Portal particles (original behavior)
        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + random.nextFloat();
            double y = pos.getY() + random.nextFloat();
            double z = pos.getZ() + random.nextFloat();
            double dx = (random.nextFloat() - 0.5) * 0.5;
            double dy = (random.nextFloat() - 0.5) * 0.5;
            double dz = (random.nextFloat() - 0.5) * 0.5;

            level.addParticle(ParticleTypes.PORTAL, x, y, z, dx, dy, dz);
        }
    }
}
