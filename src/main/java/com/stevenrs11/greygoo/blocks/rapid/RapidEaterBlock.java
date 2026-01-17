package com.stevenrs11.greygoo.blocks.rapid;

import com.stevenrs11.greygoo.core.AbstractRapidGooBlock;
import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.interaction.ProtectedBlocks;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Rapid Eater - fast block consumer that spreads in waves.
 *
 * Original behavior (BlockRapidEater.java):
 * - Spreads via scheduled ticks in a 3x3x3 cube
 * - Session-based spread limiting (replaces broken metadata depth check)
 * - Timing: random.nextInt(25) + random.nextInt(4) = 0-27 ticks
 * - Self-destructs after spreading
 * - Right-click creates session and schedules neighbors with short delays
 * - Converts to cleaner if adjacent to cleaner block
 */
public class RapidEaterBlock extends AbstractRapidGooBlock {

    public RapidEaterBlock() {
        super();
    }

    @Override
    public GooType getGooType() {
        return GooType.RAPID_EATER;
    }

    @Override
    protected boolean canConsume(BlockState targetState) {
        // Skip air
        if (targetState.isAir()) {
            return false;
        }

        // Skip protected blocks
        if (ProtectedBlocks.isProtected(targetState)) {
            return false;
        }

        return true;
    }

    @Override
    protected int getSpreadDelay(RandomSource random) {
        // Original timing: random.nextInt(25) + random.nextInt(4) = 0-27 ticks
        return random.nextInt(25) + random.nextInt(4);
    }
}
