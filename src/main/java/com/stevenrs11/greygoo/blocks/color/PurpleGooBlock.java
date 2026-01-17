package com.stevenrs11.greygoo.blocks.color;

import com.stevenrs11.greygoo.GreyGooMod;
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
 * Purple Goo - matches BlockGreyGoo.java from original mod.
 *
 * Original behavior:
 * - setTickRandomly(true) - uses random ticks
 * - Spreads in all 6 directions to any edible block
 * - Has decay() logic - when surrounded by air, removes column below
 * - metadata 2 = inactive/starved (stops spreading but stays in place)
 * - Converts to cleaner if adjacent to cleaner
 *
 * NOTE: In original code, this was confusingly named "BlockGreyGoo.java"
 * but the in-game block is called "Purple Goo".
 */
public class PurpleGooBlock extends RandomTickGooBlock {

    public static final BooleanProperty INACTIVE = BooleanProperty.create("inactive");

    public PurpleGooBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(INACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(INACTIVE);
    }

    @Override
    public GooType getGooType() {
        return GooType.PURPLE_GOO;
    }

    @Override
    protected void doSpread(ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState currentState = level.getBlockState(pos);

        // Check if inactive (starved) - skip spreading if so
        if (currentState.getValue(INACTIVE)) {
            return;
        }

        boolean hasFood = false;

        // Check for cleaner first (original behavior)
        for (Direction dir : Direction.values()) {
            BlockPos target = pos.relative(dir);
            BlockState targetState = level.getBlockState(target);

            if (targetState.is(GreyGooMod.CLEANER_BLOCK.get())) {
                level.setBlockAndUpdate(pos, GreyGooMod.CLEANER_BLOCK.get().defaultBlockState());
                return;
            }
        }

        // Spread to all edible neighbors (6 directions)
        for (Direction dir : Direction.values()) {
            BlockPos target = pos.relative(dir);
            BlockState targetState = level.getBlockState(target);

            // Skip air
            if (targetState.isAir()) {
                continue;
            }

            // Skip self
            if (targetState.is(this)) {
                continue;
            }

            // Skip protected blocks
            if (ProtectedBlocks.isProtected(targetState)) {
                continue;
            }

            // Check goo interactions
            GooInteraction interaction = getInteractionWith(level, target);
            if (interaction == GooInteraction.IGNORE) {
                continue;
            }

            // Spread to target
            level.setBlockAndUpdate(target, defaultBlockState());
            hasFood = true;
            recordSpread();
        }

        // If no food found, become inactive
        if (!hasFood) {
            onStarve(level, pos, currentState);
        }

        // Decay logic - if surrounded by air on 5 sides (not below), remove column
        decay(level, pos);
    }

    /**
     * Original decay() behavior:
     * If surrounded by air on all horizontal sides and above,
     * remove the entire column of purple goo below.
     */
    private void decay(ServerLevel level, BlockPos pos) {
        // Check if surrounded by air on 5 sides (all except below)
        boolean airAbove = level.isEmptyBlock(pos.above());
        boolean airEast = level.isEmptyBlock(pos.east());
        boolean airWest = level.isEmptyBlock(pos.west());
        boolean airNorth = level.isEmptyBlock(pos.north());
        boolean airSouth = level.isEmptyBlock(pos.south());

        if (airAbove && airEast && airWest && airNorth && airSouth) {
            // Find how far down the column of air/goo goes
            int depth = 0;
            BlockPos checkPos = pos;

            while (depth < 100) {
                checkPos = checkPos.below();
                BlockState belowState = level.getBlockState(checkPos);

                // Check if still surrounded by air at this level
                boolean stillSurrounded = level.isEmptyBlock(checkPos.east()) &&
                                          level.isEmptyBlock(checkPos.west()) &&
                                          level.isEmptyBlock(checkPos.north()) &&
                                          level.isEmptyBlock(checkPos.south());

                if (!stillSurrounded) {
                    break;
                }
                depth++;
            }

            // Remove the column including self
            if (depth > 0) {
                for (int d = 0; d <= depth; d++) {
                    BlockPos removePos = pos.below(d);
                    BlockState removeState = level.getBlockState(removePos);
                    if (removeState.is(this) || removeState.isAir()) {
                        level.removeBlock(removePos, false);
                    }
                }
                level.removeBlock(pos, false);
            } else if (airAbove) {
                // Original: just remove self if air above but not a column
                level.removeBlock(pos, false);
            }
        } else if (airAbove) {
            // Just air above - remove self
            level.removeBlock(pos, false);
        }
    }

    @Override
    protected void onStarve(ServerLevel level, BlockPos pos, BlockState state) {
        // Purple goo becomes inactive when it has no food
        // It stays as purple goo but stops trying to spread (performance optimization)
        level.setBlockAndUpdate(pos, state.setValue(INACTIVE, true));
    }
}
