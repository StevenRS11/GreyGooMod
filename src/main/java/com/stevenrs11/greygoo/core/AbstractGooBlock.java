package com.stevenrs11.greygoo.core;

import com.stevenrs11.greygoo.interaction.GooInteraction;
import com.stevenrs11.greygoo.interaction.GooInteractionRegistry;
import com.stevenrs11.greygoo.interaction.ProtectedBlocks;
import com.stevenrs11.greygoo.spread.SpreadLimiterManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base class for all goo blocks providing shared functionality:
 * - Goo interaction system integration
 * - Spread limiter checks
 * - Protected block checks
 * - Cleaner infection handling
 */
public abstract class AbstractGooBlock extends Block {

    public AbstractGooBlock(Properties properties) {
        super(properties);
    }

    /**
     * Get the GooType for this block.
     * Must be implemented by all goo blocks.
     */
    public abstract GooType getGooType();

    /**
     * Handle starvation (no valid targets found).
     * Override to customize behavior (die, transform, persist).
     */
    protected abstract void onStarve(ServerLevel level, BlockPos pos, BlockState state);

    /**
     * Perform the actual spreading logic.
     * Called after limiter and cleaner checks pass.
     */
    protected abstract void doSpread(ServerLevel level, BlockPos pos, RandomSource random);

    // ==================== Shared Utility Methods ====================

    /**
     * Get the interaction type between this goo and another block.
     */
    protected final GooInteraction getInteractionWith(ServerLevel level, BlockPos otherPos) {
        Block other = level.getBlockState(otherPos).getBlock();
        return GooInteractionRegistry.get().getInteraction(getGooType(), other);
    }

    /**
     * Handle an interaction result.
     * @return true if this goo was converted/destroyed (stop processing), false otherwise
     */
    protected final boolean handleInteraction(ServerLevel level, BlockPos selfPos,
                                               BlockPos otherPos, GooInteraction interaction) {
        switch (interaction) {
            case CONVERT_SELF -> {
                // This goo becomes the other block (e.g., cleaner infection)
                Block other = level.getBlockState(otherPos).getBlock();
                level.setBlockAndUpdate(selfPos, other.defaultBlockState());
                return true;  // We're gone, stop processing
            }
            case CONVERT_OTHER -> {
                // Convert the other block to this goo
                level.setBlockAndUpdate(otherPos, this.defaultBlockState());
                return false;  // Continue - we found food
            }
            case SPREAD_INTO -> {
                // Normal spreading into non-goo blocks
                level.setBlockAndUpdate(otherPos, this.defaultBlockState());
                return false;  // Continue - we found food
            }
            case IGNORE, MUTUAL_IGNORE, STARVE_CHECK -> {
                // Do nothing
                return false;
            }
            case REQUIRE_FOUNDATION -> {
                // Special handling - check if foundation exists
                // Subclasses should handle this
                return false;
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * Check if spreading is allowed by the limiter system.
     */
    protected final boolean canSpread(ServerLevel level, RandomSource random) {
        return SpreadLimiterManager.get().canSpread(getGooType(), random);
    }

    /**
     * Record that a spread action occurred.
     * Call after successfully spreading.
     */
    protected final void recordSpread() {
        SpreadLimiterManager.get().recordSpread(getGooType());
    }

    /**
     * Check if a block position contains a protected block.
     */
    protected final boolean isProtectedBlock(ServerLevel level, BlockPos pos) {
        return ProtectedBlocks.isProtected(level.getBlockState(pos));
    }

    /**
     * Check if this goo can eat/spread into a target position.
     * Considers protection and interaction rules.
     */
    protected final boolean canSpreadInto(ServerLevel level, BlockPos targetPos) {
        BlockState targetState = level.getBlockState(targetPos);

        // Check if protected
        if (ProtectedBlocks.isProtected(targetState)) {
            return false;
        }

        // Check interaction
        GooInteraction interaction = getInteractionWith(level, targetPos);
        return interaction == GooInteraction.SPREAD_INTO ||
               interaction == GooInteraction.CONVERT_OTHER;
    }

    /**
     * Check all neighbors for CONVERT_SELF interactions (cleaner infection).
     * @return true if this goo was converted, false otherwise
     */
    protected final boolean checkCleanerInfection(ServerLevel level, BlockPos pos) {
        for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
            BlockPos neighbor = pos.relative(dir);
            GooInteraction interaction = getInteractionWith(level, neighbor);
            if (interaction == GooInteraction.CONVERT_SELF) {
                handleInteraction(level, pos, neighbor, interaction);
                return true;
            }
        }
        return false;
    }
}
