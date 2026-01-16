package com.stevenrs11.greygoo.blocks.consumer;

import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.core.RandomTickGooBlock;
import com.stevenrs11.greygoo.interaction.GooInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Air Eater - only consumes air blocks in lit areas.
 * Original behavior: requires light level > 7 (sky light).
 * Spreads via random ticks, dies in darkness or when surrounded.
 */
public class AirEaterBlock extends RandomTickGooBlock {

    private static final int MIN_LIGHT_LEVEL = 7;

    public AirEaterBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
    }

    @Override
    public GooType getGooType() {
        return GooType.AIR_EATER;
    }

    @Override
    protected void doSpread(ServerLevel level, BlockPos pos, RandomSource random) {
        boolean hasFood = false;

        for (Direction dir : Direction.values()) {
            BlockPos target = pos.relative(dir);
            BlockState targetState = level.getBlockState(target);

            // Check goo interactions first
            GooInteraction interaction = getInteractionWith(level, target);

            if (interaction == GooInteraction.CONVERT_SELF) {
                handleInteraction(level, pos, target, interaction);
                return;
            }

            if (interaction == GooInteraction.IGNORE) {
                continue;
            }

            // Only eat air blocks with sufficient light
            if (targetState.isAir() && level.getBrightness(LightLayer.SKY, target) > MIN_LIGHT_LEVEL) {
                level.setBlockAndUpdate(target, defaultBlockState());
                hasFood = true;
                recordSpread();
            }
        }

        if (!hasFood) {
            onStarve(level, pos, level.getBlockState(pos));
        }
    }

    @Override
    protected void onStarve(ServerLevel level, BlockPos pos, BlockState state) {
        // Air eater dies when it has no food
        level.destroyBlock(pos, false);
    }
}
