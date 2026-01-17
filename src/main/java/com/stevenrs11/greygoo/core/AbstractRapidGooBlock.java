package com.stevenrs11.greygoo.core;

import com.stevenrs11.greygoo.GreyGooMod;
import com.stevenrs11.greygoo.spread.RapidSpreadManager;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Base class for rapid spreading goo blocks.
 *
 * Rapid blocks spread via scheduled ticks in waves:
 * - Right-click creates a new spread session and starts spreading
 * - Each block spreads to neighbors, schedules them, then self-destructs
 * - All blocks in a session share the same session ID
 * - Sessions expire after a configurable duration (default 400 ticks = 20 seconds)
 *
 * Subclasses define:
 * - What blocks they can consume (canConsume)
 * - Their timing formula (getSpreadDelay)
 * - Their session duration (getSessionDuration)
 */
public abstract class AbstractRapidGooBlock extends Block {

    /**
     * Session ID property. 0 = no session (won't spread automatically).
     * Non-zero values (1-15) link the block to an active spread session.
     * Limited to 15 concurrent sessions to keep block state count reasonable.
     */
    public static final IntegerProperty SESSION_ID = IntegerProperty.create("session", 0, 15);

    public AbstractRapidGooBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(SESSION_ID, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SESSION_ID);
    }

    // ==================== Abstract Methods (must implement) ====================

    /**
     * Get the goo type for this block.
     */
    public abstract GooType getGooType();

    /**
     * Check if this rapid goo can consume the target block.
     * @param targetState The block state to potentially consume
     * @return true if this block can spread into the target
     */
    protected abstract boolean canConsume(BlockState targetState);

    /**
     * Calculate the spread delay for a new block.
     * Original formulas:
     * - RapidEater: random.nextInt(25) + random.nextInt(4) = 0-27 ticks
     * - RapidMiner: random.nextInt(5) + random.nextInt(18) = 5-22 ticks
     */
    protected abstract int getSpreadDelay(RandomSource random);

    /**
     * Get the session duration in ticks for this block type.
     * Default: 400 ticks (20 seconds)
     */
    protected int getSessionDuration() {
        return 400;
    }

    // ==================== Core Tick Logic ====================

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int sessionId = state.getValue(SESSION_ID);

        // Check if session is still active
        if (!RapidSpreadManager.get().isSessionActive(sessionId, getSessionDuration())) {
            // Session expired or invalid - just remove self without spreading
            level.removeBlock(pos, false);
            return;
        }

        // Spread to neighbors
        spread(level, pos, random, sessionId);

        // Self-destruct after spreading (original behavior)
        level.removeBlock(pos, false);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                  Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            RandomSource random = level.getRandom();

            // Start a new spread session
            int sessionId = RapidSpreadManager.get().startSession();

            // Update this block with the session ID
            level.setBlockAndUpdate(pos, state.setValue(SESSION_ID, sessionId));

            // Immediately spread
            spread(serverLevel, pos, random, sessionId);

            // Schedule neighbors with SHORT delays (0-4 ticks) for rapid chain reaction
            // Original behavior: schedules horizontal neighbors + self
            scheduleNeighborsForActivation(level, pos, random);
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * Schedule neighboring positions for rapid activation after right-click.
     * Subclasses can override to customize which neighbors get scheduled.
     */
    protected void scheduleNeighborsForActivation(Level level, BlockPos pos, RandomSource random) {
        level.scheduleTick(pos, this, 5);
        level.scheduleTick(pos.east(), this, random.nextInt(5));
        level.scheduleTick(pos.west(), this, random.nextInt(5));
        level.scheduleTick(pos.north(), this, random.nextInt(5));
        level.scheduleTick(pos.south(), this, random.nextInt(5));
        level.scheduleTick(pos.below(), this, random.nextInt(5));
    }

    /**
     * Spread to all valid targets in 3x3x3 cube.
     * Each new block inherits the session ID and gets scheduled.
     */
    protected void spread(ServerLevel level, BlockPos pos, RandomSource random, int sessionId) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }

                    BlockPos target = pos.offset(dx, dy, dz);
                    BlockState targetState = level.getBlockState(target);

                    // Check for cleaner - convert self to cleaner (original behavior)
                    if (targetState.is(GreyGooMod.CLEANER_BLOCK.get())) {
                        level.setBlockAndUpdate(pos, GreyGooMod.CLEANER_BLOCK.get().defaultBlockState());
                        return;  // Stop spreading, we've been infected
                    }

                    // Skip if already this block type
                    if (targetState.is(this)) {
                        continue;
                    }

                    // Check if we can consume this block
                    if (canConsume(targetState)) {
                        // Convert block to this rapid goo type with the same session ID
                        level.setBlockAndUpdate(target,
                            defaultBlockState().setValue(SESSION_ID, sessionId));

                        // Schedule with subclass-defined timing
                        int delay = getSpreadDelay(random);
                        level.scheduleTick(target, this, delay);
                    }
                }
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        // Don't auto-schedule on place - spreading is controlled by session system
        // Blocks only spread when:
        // 1. Player right-clicks (creates session, schedules neighbors)
        // 2. A neighbor's spread() method schedules this block
    }
}
