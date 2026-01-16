package com.stevenrs11.greygoo.blocks.rapid;

import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.core.ScheduledTickGooBlock;
import com.stevenrs11.greygoo.interaction.GooInteraction;
import com.stevenrs11.greygoo.interaction.ProtectedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.Set;

/**
 * Rapid Miner - fast stone/earth consumer with depth limiting.
 * Spreads via scheduled ticks in a 3x3x3 cube.
 * Uses STAGE property to track depth (0-50), dies at max depth.
 * Only consumes mineable blocks (stone, gravel, sand, etc).
 * Original timing: random.nextInt(5) + random.nextInt(18) (5-22 ticks)
 */
public class RapidMinerBlock extends ScheduledTickGooBlock {

    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 50);
    private static final int MAX_DEPTH = 50;

    // Blocks that rapid miner can eat (same as MinerGooBlock)
    private static final Set<Block> MINEABLE = Set.of(
        Blocks.GRAVEL,
        Blocks.STONE,
        Blocks.SAND,
        Blocks.RED_SAND,
        Blocks.SANDSTONE,
        Blocks.RED_SANDSTONE,
        Blocks.NETHERRACK,
        Blocks.SOUL_SAND,
        Blocks.SOUL_SOIL,
        Blocks.CLAY,
        Blocks.COBBLESTONE,
        Blocks.DEEPSLATE,
        Blocks.COBBLED_DEEPSLATE,
        Blocks.TUFF,
        Blocks.GRANITE,
        Blocks.DIORITE,
        Blocks.ANDESITE,
        Blocks.CALCITE,
        Blocks.SMOOTH_BASALT,
        Blocks.BASALT
    );

    public RapidMinerBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public GooType getGooType() {
        return GooType.RAPID_MINER;
    }

    @Override
    protected int getMinDelay() {
        return 5;  // Faster than RapidEater
    }

    @Override
    protected int getDelayVariance() {
        return 18;  // Original: random.nextInt(5) + random.nextInt(18)
    }

    @Override
    protected int getMaxDepth() {
        return MAX_DEPTH;
    }

    /**
     * Check if a block is mineable by this goo.
     */
    private boolean isMineable(BlockState state) {
        Block block = state.getBlock();
        if (MINEABLE.contains(block)) {
            return true;
        }
        return state.is(BlockTags.BASE_STONE_OVERWORLD) ||
               state.is(BlockTags.BASE_STONE_NETHER);
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

                    // Skip protected blocks
                    if (ProtectedBlocks.isProtected(targetState)) {
                        continue;
                    }

                    // Check goo interactions
                    GooInteraction interaction = getInteractionWith(level, target);
                    if (interaction == GooInteraction.CONVERT_SELF) {
                        handleInteraction(level, pos, target, interaction);
                        return;
                    }

                    if (interaction == GooInteraction.IGNORE) {
                        continue;
                    }

                    // Only eat mineable blocks (stone/earth)
                    if (isMineable(targetState)) {
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
        // Stage 0 blocks wait for manual activation
        if (!level.isClientSide && state.getValue(STAGE) > 0) {
            scheduleNextTick(level, pos);
        }
    }
}
