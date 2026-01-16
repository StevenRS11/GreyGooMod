package com.stevenrs11.greygoo.blocks.rapid;

import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.core.ScheduledTickGooBlock;
import com.stevenrs11.greygoo.interaction.GooInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * Rapid Water Eater - fast water/lava consumer with depth limiting.
 * Spreads via scheduled ticks in a 3x3x3 cube.
 * Uses STAGE property to track depth (0-50), dies at max depth.
 * Original timing: random.nextInt(25) + random.nextInt(4)
 */
public class RapidWaterEaterBlock extends ScheduledTickGooBlock {

    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 50);
    private static final int MAX_DEPTH = 50;

    public RapidWaterEaterBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public GooType getGooType() {
        return GooType.RAPID_WATER_EATER;
    }

    @Override
    protected int getMinDelay() {
        return 25;
    }

    @Override
    protected int getDelayVariance() {
        return 4;
    }

    @Override
    protected int getMaxDepth() {
        return MAX_DEPTH;
    }

    @Override
    protected void doSpread(ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState currentState = level.getBlockState(pos);
        int stage = currentState.getValue(STAGE);

        // Check depth limit
        if (stage >= MAX_DEPTH) {
            level.removeBlock(pos, false);
            return;
        }

        boolean foundFood = false;

        // Search 3x3x3 cube (original behavior)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }

                    BlockPos target = pos.offset(dx, dy, dz);
                    BlockState targetState = level.getBlockState(target);

                    // Check goo interactions
                    GooInteraction interaction = getInteractionWith(level, target);
                    if (interaction == GooInteraction.CONVERT_SELF) {
                        handleInteraction(level, pos, target, interaction);
                        return;
                    }

                    // Only eat water or lava
                    if (targetState.getFluidState().is(FluidTags.WATER) ||
                        targetState.getFluidState().is(FluidTags.LAVA)) {
                        // Spread with incremented depth
                        level.setBlockAndUpdate(target, defaultBlockState().setValue(STAGE, stage + 1));
                        // Schedule tick for the new block
                        int delay = getMinDelay() + random.nextInt(getDelayVariance() + 1);
                        level.scheduleTick(target, this, delay);
                        foundFood = true;
                        recordSpread();
                    }
                }
            }
        }

        // Self-destruct after spreading (original behavior)
        level.removeBlock(pos, false);

        if (!foundFood) {
            onStarve(level, pos, currentState);
        }
    }

    @Override
    protected void onStarve(ServerLevel level, BlockPos pos, BlockState state) {
        // Already removed in doSpread, nothing extra to do
    }

    @Override
    public void onPlace(BlockState state, net.minecraft.world.level.Level level, BlockPos pos,
                        BlockState oldState, boolean isMoving) {
        // Only schedule if stage > 0 (spread from another block)
        // Stage 0 blocks wait for manual activation or don't auto-spread
        if (!level.isClientSide && state.getValue(STAGE) > 0) {
            scheduleNextTick(level, pos);
        }
    }
}
