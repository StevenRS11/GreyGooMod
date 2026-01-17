package com.stevenrs11.greygoo.blocks.destroyer;

import com.stevenrs11.greygoo.GreyGooMod;
import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.core.RandomTickGooBlock;
import com.stevenrs11.greygoo.interaction.ProtectedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Cancer2 Block - fast spreader when player is nearby, transforms to TGD when starving.
 *
 * Original behavior (BlockCancer2.java):
 * - Uses random ticks
 * - Spreads in 3x3x3 cube (-1 to +1)
 * - Target block must have air above it
 * - Spreads rapidly when player within 10 blocks
 * - Without player nearby, 1/3 chance to spread
 * - Won't spread into: Cleaner, Black, Cancer, Wall, Inert, Freezer, TGD, TGDInert, Cancer2, chest, protected
 * - Transforms to TGD when no food in 7x7x7 area (with hasTicked toggle)
 * - Becomes inactive when no food and no player within 20 blocks
 * - Poison and wither effects on entity walking (50 ticks)
 * - Portal particles on display tick
 */
public class Cancer2Block extends RandomTickGooBlock {

    public static final BooleanProperty INACTIVE = BooleanProperty.create("inactive");

    // Instance toggle for transformation delay
    private boolean hasTicked = false;

    public Cancer2Block() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(INACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(INACTIVE);
    }

    @Override
    public GooType getGooType() {
        return GooType.CANCER2;
    }

    @Override
    protected void doSpread(ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState currentState = level.getBlockState(pos);

        // Check if inactive - skip spreading
        if (currentState.getValue(INACTIVE)) {
            return;
        }

        // Check for food in 7x7x7 area
        boolean hasFoodInArea = checkForFoodInArea(level, pos, 3);

        if (!hasFoodInArea) {
            // Transform to TGD with toggle delay
            hasTicked = !hasTicked;
            if (hasTicked) {
                level.setBlockAndUpdate(pos, GreyGooMod.TGD_BLOCK.get().defaultBlockState());
            }
            return;
        }

        // Check for player proximity
        Player nearestPlayer = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, false);
        boolean playerNearby = nearestPlayer != null;

        boolean hasFood = false;

        // Spread in 3x3x3 cube
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;

                    BlockPos target = pos.offset(dx, dy, dz);
                    BlockState targetState = level.getBlockState(target);

                    // Check for cleaner - convert self
                    if (targetState.is(GreyGooMod.CLEANER_BLOCK.get())) {
                        level.setBlockAndUpdate(pos, GreyGooMod.CLEANER_BLOCK.get().defaultBlockState());
                        return;
                    }

                    // Skip blocks we can't spread into
                    if (!canSpreadInto(level, target, targetState)) {
                        continue;
                    }

                    // Spread based on player proximity
                    if (playerNearby) {
                        // Fast spread when player nearby
                        level.setBlockAndUpdate(target, defaultBlockState());
                        hasFood = true;
                        recordSpread();
                    } else if (random.nextInt(3) == 1) {
                        // 1/3 chance when no player
                        level.setBlockAndUpdate(target, defaultBlockState());
                        hasFood = true;
                        recordSpread();
                    }
                }
            }
        }

        if (!hasFood) {
            // Check for player within 20 blocks for inactive check
            Player farPlayer = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 20, false);
            if (farPlayer == null) {
                onStarve(level, pos, currentState);
            }
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
                    if (!state.isAir() && !isCancerOrCancer2(state) && canSpreadInto(level, checkPos, state)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check if state is Cancer or Cancer2.
     */
    private boolean isCancerOrCancer2(BlockState state) {
        Block block = state.getBlock();
        return block == GreyGooMod.CANCER_BLOCK.get() || block == this;
    }

    /**
     * Check if Cancer2 can spread into this block.
     * Target block must have air above it.
     */
    private boolean canSpreadInto(ServerLevel level, BlockPos target, BlockState state) {
        if (state.isAir()) return false;

        Block block = state.getBlock();

        // Can't spread into these
        if (block == GreyGooMod.CLEANER_BLOCK.get()) return false;
        if (block == GreyGooMod.BLACK_DESTROYER_BLOCK.get()) return false;
        if (block == GreyGooMod.CANCER_BLOCK.get()) return false;
        if (block == GreyGooMod.WALL_BLOCK.get()) return false;
        if (block == GreyGooMod.INERT_BLOCK.get()) return false;
        if (block == GreyGooMod.TGD_BLOCK.get()) return false;
        if (block == GreyGooMod.TGD_INERT_BLOCK.get()) return false;
        if (block == this) return false;

        // Protected blocks
        if (ProtectedBlocks.isProtected(state)) return false;

        // Target must have air above it (original behavior)
        BlockPos above = target.above();
        if (!level.getBlockState(above).isAir()) return false;

        return true;
    }

    @Override
    protected void onStarve(ServerLevel level, BlockPos pos, BlockState state) {
        // Become inactive
        level.setBlockAndUpdate(pos, state.setValue(INACTIVE, true));
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide) {
            // Trigger spread on step
            doSpread((ServerLevel) level, pos, level.getRandom());
        }

        // Apply poison and wither effects to living entities
        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 50, 1));
            living.addEffect(new MobEffectInstance(MobEffects.WITHER, 50, 1));
        }

        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // Portal particles (original behavior)
        for (int i = 0; i < 4; i++) {
            double x = pos.getX() + random.nextFloat();
            double y = pos.getY() + random.nextFloat();
            double z = pos.getZ() + random.nextFloat();
            double dx = (random.nextFloat() - 0.5) * 0.5;
            double dy = (random.nextFloat() - 0.5) * 0.5;
            double dz = (random.nextFloat() - 0.5) * 0.5;

            int i1 = random.nextInt(2) * 2 - 1;

            // Check adjacent Cancer2 blocks for directional particles
            if (level.getBlockState(pos.west()).is(this) || level.getBlockState(pos.east()).is(this)) {
                z = pos.getZ() + 0.5 + 0.25 * i1;
                dz = random.nextFloat() * 2.0f * i1;
            } else {
                x = pos.getX() + 0.5 + 0.25 * i1;
                dx = random.nextFloat() * 2.0f * i1;
            }

            level.addParticle(ParticleTypes.PORTAL, x, y, z, dx, dy, dz);
        }
    }
}
