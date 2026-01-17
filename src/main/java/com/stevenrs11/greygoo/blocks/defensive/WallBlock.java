package com.stevenrs11.greygoo.blocks.defensive;

import com.stevenrs11.greygoo.GreyGooMod;
import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.core.RandomTickGooBlock;
import com.stevenrs11.greygoo.interaction.ProtectedBlocks;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Wall Block (Orange Goo) - defensive goo that spreads with a foundation requirement.
 *
 * Original behavior (BlockWall.java):
 * - Uses random ticks
 * - Spreads only to orthogonal neighbors (manhattan < 2, so effectively == 1)
 * - Foundation requirement: opposite direction must have Wall OR Inert block
 * - Can spread into air and any non-protected, non-Inert block
 * - ALWAYS goes inactive after one tick (semi-manual spreading)
 * - Right-click reactivates and triggers one spread
 * - Converts to Cleaner if adjacent to Cleaner
 */
public class WallBlock extends RandomTickGooBlock {

    public static final BooleanProperty INACTIVE = BooleanProperty.create("inactive");

    public WallBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(INACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(INACTIVE);
    }

    @Override
    public GooType getGooType() {
        return GooType.WALL;
    }

    @Override
    protected void doSpread(ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState currentState = level.getBlockState(pos);

        // Check if inactive - skip spreading
        if (currentState.getValue(INACTIVE)) {
            return;
        }

        // Iterate through 3x3x3 area (matching original loop structure)
        for (int dy = -1; dy < 2; dy++) {
            for (int dz = -1; dz < 2; dz++) {
                for (int dx = -1; dx < 2; dx++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;

                    BlockPos target = pos.offset(dx, dy, dz);
                    BlockState targetState = level.getBlockState(target);

                    // Check for cleaner - convert self to cleaner
                    if (targetState.is(GreyGooMod.CLEANER_BLOCK.get())) {
                        level.setBlockAndUpdate(pos, GreyGooMod.CLEANER_BLOCK.get().defaultBlockState());
                        return;
                    }

                    // Foundation check: opposite direction must have Wall OR Inert
                    BlockPos foundation = pos.offset(-dx, -dy, -dz);
                    BlockState foundationState = level.getBlockState(foundation);
                    boolean hasFoundation = foundationState.is(this) ||
                                           foundationState.is(GreyGooMod.INERT_BLOCK.get());

                    if (!hasFoundation) {
                        continue;
                    }

                    // Only spread to orthogonal neighbors (manhattan < 2)
                    int manhattan = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                    if (manhattan >= 2) {
                        continue;
                    }

                    // Can't spread into Inert
                    if (targetState.is(GreyGooMod.INERT_BLOCK.get())) {
                        continue;
                    }

                    // Can't spread into itself (prevents reactivating existing Wall blocks)
                    if (targetState.is(this)) {
                        continue;
                    }

                    // Can't spread into protected blocks (unless it's snow - but we skip that detail)
                    if (ProtectedBlocks.isProtected(targetState)) {
                        continue;
                    }

                    // Spread into target (including air!)
                    level.setBlockAndUpdate(target, defaultBlockState());
                    recordSpread();
                }
            }
        }

        // Original behavior: ALWAYS go inactive after spreading
        // This makes Wall a semi-manual block that requires right-click to reactivate
        onStarve(level, pos, currentState);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                  Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && !player.isShiftKeyDown()) {
            // Reactivate the block and trigger spread
            level.setBlockAndUpdate(pos, state.setValue(INACTIVE, false));
            doSpread((ServerLevel) level, pos, level.getRandom());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void onStarve(ServerLevel level, BlockPos pos, BlockState state) {
        // Wall becomes inactive after spreading
        level.setBlockAndUpdate(pos, state.setValue(INACTIVE, true));
    }
}
