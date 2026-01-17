package com.stevenrs11.greygoo.blocks.rapid;

import com.stevenrs11.greygoo.core.AbstractRapidGooBlock;
import com.stevenrs11.greygoo.core.GooType;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Rapid Water Eater - fast water/lava consumer that spreads in waves.
 *
 * Original behavior (BlockRapidWaterEater.java):
 * - Spreads via scheduled ticks in a 3x3x3 cube
 * - Session-based spread limiting (replaces broken metadata depth check)
 * - Timing: random.nextInt(25) + random.nextInt(4) = 0-27 ticks
 * - Self-destructs after spreading
 * - Right-click creates session and schedules neighbors with short delays
 * - Converts to cleaner if adjacent to cleaner block
 * - Only consumes water and lava (still and flowing)
 */
public class RapidWaterEaterBlock extends AbstractRapidGooBlock {

    public RapidWaterEaterBlock() {
        super();
    }

    @Override
    public GooType getGooType() {
        return GooType.RAPID_WATER_EATER;
    }

    @Override
    protected boolean canConsume(BlockState targetState) {
        // Only eat water or lava (still and flowing)
        return targetState.getFluidState().is(FluidTags.WATER) ||
               targetState.getFluidState().is(FluidTags.LAVA);
    }

    @Override
    protected int getSpreadDelay(RandomSource random) {
        // Original timing: random.nextInt(25) + random.nextInt(4) = 0-27 ticks
        return random.nextInt(25) + random.nextInt(4);
    }

    @Override
    protected void scheduleNeighborsForActivation(Level level, BlockPos pos, RandomSource random) {
        // Original water eater doesn't schedule below direction (no pos.below())
        level.scheduleTick(pos, this, 5);
        level.scheduleTick(pos.east(), this, random.nextInt(5));
        level.scheduleTick(pos.west(), this, random.nextInt(5));
        level.scheduleTick(pos.north(), this, random.nextInt(5));
        level.scheduleTick(pos.south(), this, random.nextInt(5));
    }
}
