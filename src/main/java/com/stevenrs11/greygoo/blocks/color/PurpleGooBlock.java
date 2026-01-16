package com.stevenrs11.greygoo.blocks.color;

import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.core.ScheduledTickGooBlock;
import com.stevenrs11.greygoo.interaction.GooInteraction;
import com.stevenrs11.greygoo.interaction.ProtectedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
 * Purple Goo (OrangePurple) - creates a "moving front" effect.
 * Manual spreading only (no random ticks).
 * When spreading, removes old blocks behind itself creating a trail.
 * Requires right-click to activate and spread.
 */
public class PurpleGooBlock extends ScheduledTickGooBlock {

    // Use the standard activated property
    public static final BooleanProperty ACTIVATED = ScheduledTickGooBlock.ACTIVATED;

    public PurpleGooBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVATED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    @Override
    public GooType getGooType() {
        return GooType.ORANGE_PURPLE;
    }

    @Override
    protected int getMinDelay() {
        return 0;  // Minimum delay
    }

    @Override
    protected int getDelayVariance() {
        return 10;  // Original: random.nextInt(10)
    }

    @Override
    protected boolean requiresActivation() {
        return true;  // Only spreads when clicked
    }

    @Override
    protected boolean hasFoundationRequirement() {
        return true;  // Requires inert/purple foundation to spread
    }

    @Override
    protected boolean isFoundationBlock(Block block) {
        // Purple goo can spread from other purple goo or inert blocks
        if (block == this) return true;
        GooType type = GooType.fromBlock(block);
        return type == GooType.INERT;
    }

    @Override
    protected void doSpread(ServerLevel level, BlockPos pos, RandomSource random) {
        // Search in adjacent directions with Manhattan distance check
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }

                    // Manhattan distance check (original: < 2)
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) >= 2) {
                        continue;
                    }

                    BlockPos target = pos.offset(dx, dy, dz);
                    BlockState targetState = level.getBlockState(target);

                    // Skip if already purple goo
                    if (targetState.is(this)) {
                        continue;
                    }

                    // Skip protected blocks
                    if (ProtectedBlocks.isProtected(targetState)) {
                        continue;
                    }

                    // Skip air
                    if (targetState.isAir()) {
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

                    // Skip other goos (except inert)
                    GooType targetGooType = GooType.fromBlock(targetState.getBlock());
                    if (targetGooType != null && targetGooType != GooType.INERT) {
                        continue;
                    }

                    // Spread to target (activated)
                    level.setBlockAndUpdate(target, defaultBlockState().setValue(ACTIVATED, true));

                    // Remove the block behind (creates "moving front" trail effect)
                    // Original: removes at pos - direction*2, but we'll remove current pos
                    // since that creates the moving front effect
                    BlockPos behind = pos.offset(-dx * 2, -dy * 2, -dz * 2);
                    BlockState behindState = level.getBlockState(behind);
                    if (behindState.is(this)) {
                        level.removeBlock(behind, false);
                    }

                    // Schedule tick for new block
                    int delay = getMinDelay() + random.nextInt(getDelayVariance() + 1);
                    level.scheduleTick(target, this, delay);
                    recordSpread();
                }
            }
        }

        // Purple goo removes itself after spreading (part of trail effect)
        level.removeBlock(pos, false);
    }

    @Override
    protected void onStarve(ServerLevel level, BlockPos pos, BlockState state) {
        // Purple goo just disappears when it can't spread
        level.removeBlock(pos, false);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        // Override parent to NOT auto-schedule
        // Purple goo only activates via right-click or when placed already activated
        if (!level.isClientSide && state.getValue(ACTIVATED)) {
            scheduleNextTick(level, pos);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                  Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            if (!state.getValue(ACTIVATED)) {
                // Activate on click
                level.setBlockAndUpdate(pos, state.setValue(ACTIVATED, true));
                scheduleNextTick(level, pos);
            } else {
                // Manual spread trigger
                doSpread((ServerLevel) level, pos, level.getRandom());
            }
        }
        return InteractionResult.SUCCESS;
    }
}
