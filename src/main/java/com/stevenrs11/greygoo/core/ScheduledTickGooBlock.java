package com.stevenrs11.greygoo.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Base class for goo blocks that spread via self-scheduled ticks.
 *
 * Scheduled tick spreading is faster and more controlled, suitable for:
 * - RapidEater, RapidWaterEater, RapidMiner (immediate spreading)
 * - FreezerBlock
 * - Color variants (OrangeRed, OrangeWhite, OrangePurple) with activation
 *
 * Subclasses define timing via getMinDelay() and getDelayVariance().
 *
 * Optional features (enabled via hook methods):
 * - Activation requirement: override requiresActivation() to return true
 * - Foundation requirement: override hasFoundationRequirement() to return true
 */
public abstract class ScheduledTickGooBlock extends AbstractGooBlock {

    /**
     * Block state property for activation. Only used when requiresActivation() returns true.
     * Subclasses must add this to their state definition if they require activation.
     */
    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

    public ScheduledTickGooBlock(Properties properties) {
        super(properties);  // No randomTicks() - we schedule our own
    }

    // ==================== Abstract Methods (must implement) ====================

    /**
     * Minimum delay in ticks before next spread attempt.
     */
    protected abstract int getMinDelay();

    /**
     * Random variance added to min delay.
     * Actual delay = minDelay + random(0, variance)
     */
    protected abstract int getDelayVariance();

    // ==================== Optional Hook Methods (override as needed) ====================

    /**
     * Whether this goo requires activation before spreading.
     * If true, the block starts inactive and must be right-clicked to activate.
     * Subclass must also add ACTIVATED to its state definition.
     */
    protected boolean requiresActivation() {
        return false;
    }

    /**
     * Whether this goo requires an adjacent foundation block to spread.
     * Used by Wall and color variants.
     */
    protected boolean hasFoundationRequirement() {
        return false;
    }

    /**
     * Check if a block counts as foundation for spreading.
     * Override to customize which blocks count as foundation.
     * Default: same block type or Inert.
     */
    protected boolean isFoundationBlock(Block block) {
        if (block == this) return true;
        GooType type = GooType.fromBlock(block);
        return type == GooType.INERT;
    }

    /**
     * Optional maximum spread depth (-1 for no limit).
     * Used by RapidWaterEater-style blocks with depth tracking.
     */
    protected int getMaxDepth() {
        return -1;
    }

    // ==================== Core Tick Logic ====================

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Check activation requirement
        if (requiresActivation() && !state.getValue(ACTIVATED)) {
            return;  // Not activated, do nothing
        }

        // Check foundation requirement
        if (hasFoundationRequirement() && !hasFoundation(level, pos)) {
            return;  // No foundation, stay dormant
        }

        // Check spread limiter
        if (!canSpread(level, random)) {
            // Re-schedule for later attempt
            scheduleNextTick(level, pos);
            return;
        }

        // Check for cleaner infection (highest priority)
        if (checkCleanerInfection(level, pos)) {
            return;  // We were converted, stop
        }

        // Delegate spreading to subclass
        doSpread(level, pos, random);
    }

    /**
     * Check if this block has a valid foundation (adjacent foundation block).
     */
    protected boolean hasFoundation(ServerLevel level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
            if (isFoundationBlock(neighbor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Schedule the next tick for this block.
     */
    protected void scheduleNextTick(Level level, BlockPos pos) {
        int delay = getMinDelay() + level.getRandom().nextInt(getDelayVariance() + 1);
        level.scheduleTick(pos, this, delay);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
            // For activation-required blocks, only schedule if already activated
            // (e.g., when spread from another activated block)
            if (requiresActivation()) {
                if (state.getValue(ACTIVATED)) {
                    scheduleNextTick(level, pos);
                }
            } else {
                // Non-activation blocks schedule immediately
                scheduleNextTick(level, pos);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                  Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            if (requiresActivation()) {
                if (!state.getValue(ACTIVATED)) {
                    // Activate on first right-click
                    level.setBlockAndUpdate(pos, state.setValue(ACTIVATED, true));
                    scheduleNextTick(level, pos);
                    return InteractionResult.SUCCESS;
                }
            }
            // Manual spread on right-click (for all scheduled tick goos)
            doSpread((ServerLevel) level, pos, level.getRandom());
        }
        return InteractionResult.SUCCESS;
    }
}
