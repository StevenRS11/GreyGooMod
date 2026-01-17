package com.stevenrs11.greygoo.blocks.consumer;

import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.core.RandomTickGooBlock;
import com.stevenrs11.greygoo.interaction.GooInteraction;
import com.stevenrs11.greygoo.interaction.ProtectedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * The classic Grey Goo - consumes all non-protected blocks.
 * Spreads via random ticks.
 * When no food is found, becomes inactive (stops spreading but stays in place).
 * This matches the original behavior where metadata 2 = starved/inactive.
 */
public class GreyGooBlock extends RandomTickGooBlock {

    public static final BooleanProperty INACTIVE = BooleanProperty.create("inactive");

    public GreyGooBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(INACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(INACTIVE);
    }

    @Override
    public GooType getGooType() {
        return GooType.GREY_GOO;
    }

    @Override
    protected void doSpread(ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState currentState = level.getBlockState(pos);

        // Check if inactive (starved) - skip spreading if so
        if (currentState.getValue(INACTIVE)) {
            return;
        }

        boolean hasFood = false;

        for (Direction dir : Direction.values()) {
            BlockPos target = pos.relative(dir);
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

            // Eat non-air blocks
            if (!targetState.isAir()) {
                level.setBlockAndUpdate(target, defaultBlockState());
                hasFood = true;
                recordSpread();
            }
        }

        if (!hasFood) {
            onStarve(level, pos, currentState);
        }
    }

    @Override
    protected void onStarve(ServerLevel level, BlockPos pos, BlockState state) {
        // Grey goo becomes inactive when it has no food
        // It stays as grey goo but stops trying to spread (performance optimization)
        level.setBlockAndUpdate(pos, state.setValue(INACTIVE, true));
    }
}
