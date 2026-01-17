package com.stevenrs11.greygoo.blocks.color;

import com.stevenrs11.greygoo.GreyGooMod;
import com.stevenrs11.greygoo.core.GooType;
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
 * OrangeRed Block - spreads with foundation requirement, no moving front.
 *
 * Original behavior (BlockOrangeRed.java):
 * - setTickRandomly(false) - uses SCHEDULED ticks, not random ticks
 * - Right-click activates spreading via OrangeRedIsSpreading flag (we use sessions)
 * - Requires foundation: OrangeRed OR Inert on opposite side of spread direction
 * - Spreads only to orthogonal neighbors (Manhattan distance < 2)
 * - Can spread into any block except inert and itself
 * - NO moving front effect (unlike OrangePurple - doesn't remove old blocks)
 * - Uses sum of 3 random values for more variation in tick delay
 */
public class OrangeRedBlock extends Block {

    /**
     * Session ID property - same as rapid goos.
     * 0 = no session (won't spread), 1-15 = active session.
     */
    public static final IntegerProperty SESSION_ID = IntegerProperty.create("session", 0, 15);

    public OrangeRedBlock() {
        // No randomTicks - uses scheduled ticks only
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(SESSION_ID, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SESSION_ID);
    }

    public GooType getGooType() {
        return GooType.ORANGE_RED;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int sessionId = state.getValue(SESSION_ID);

        // Check if session is still active (400 ticks = 20 seconds)
        if (!RapidSpreadManager.get().isSessionActive(sessionId, 400)) {
            // Session expired - just sit here, don't spread
            return;
        }

        // Spread to neighbors
        spread(level, pos, random, sessionId);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                  Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && !player.isShiftKeyDown()) {
            RandomSource random = level.getRandom();

            // Start a new spread session
            int sessionId = RapidSpreadManager.get().startSession();

            // Update this block with the session ID
            level.setBlockAndUpdate(pos, state.setValue(SESSION_ID, sessionId));

            // Trigger immediate spread (original calls grow() directly on right-click)
            spread((ServerLevel) level, pos, random, sessionId);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    /**
     * Spread to orthogonal neighbors with foundation requirement.
     * Unlike OrangePurple, does NOT create moving front - just spreads.
     */
    private void spread(ServerLevel level, BlockPos pos, RandomSource random, int sessionId) {
        // Iterate through all neighbor positions (-1 to +1 for each axis)
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                for (int dz = -1; dz < 2; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }

                    BlockPos target = pos.offset(dx, dy, dz);
                    BlockState targetState = level.getBlockState(target);

                    // Check for cleaner - convert self to cleaner
                    if (targetState.is(GreyGooMod.CLEANER_BLOCK.get())) {
                        level.setBlockAndUpdate(pos, GreyGooMod.CLEANER_BLOCK.get().defaultBlockState());
                        return;
                    }

                    // Check foundation requirement: need OrangeRed or Inert on OPPOSITE side
                    BlockPos foundation = pos.offset(-dx, -dy, -dz);
                    BlockState foundationState = level.getBlockState(foundation);

                    boolean hasFoundation = foundationState.is(this) ||
                                           foundationState.is(GreyGooMod.INERT_BLOCK.get());
                    if (!hasFoundation) {
                        continue;
                    }

                    // Manhattan distance < 2 means only orthogonal neighbors
                    int manhattan = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                    if (manhattan >= 2) {
                        continue;
                    }

                    // Target must NOT be inert block
                    if (targetState.is(GreyGooMod.INERT_BLOCK.get())) {
                        continue;
                    }

                    // Target must NOT be this block already
                    if (targetState.is(this)) {
                        continue;
                    }

                    // Spread to target
                    level.setBlockAndUpdate(target, defaultBlockState().setValue(SESSION_ID, sessionId));

                    // Schedule with randomized delay (sum of 3 random values for more variation)
                    int delay = random.nextInt(3) + random.nextInt(3) + random.nextInt(3);
                    level.scheduleTick(target, this, delay);
                }
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        // Don't auto-schedule - spreading is controlled by right-click and session system
    }
}
