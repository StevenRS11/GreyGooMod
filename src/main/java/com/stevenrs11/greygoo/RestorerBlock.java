package com.stevenrs11.greygoo;

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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Restorer Block (also known as Rainbow Goo) restores the world to its original
 * pristine state by comparing the current blocks against the backup dimension.
 *
 * The block has 3 states matching the original implementation:
 * - STATE 0 (DEFAULT): Scanning for differences and spreading
 * - STATE 1 (READY): Found differences, ready to restore
 * - STATE 2 (COMPLETE): Restoration complete, will restore then decay
 *
 * The block emits full light (level 15) and spreads with 50% probability to
 * adjacent modified blocks. When no work remains, the block self-destructs.
 */
public class RestorerBlock extends Block {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestorerBlock.class);
    public static final IntegerProperty RESTORE_STATE = IntegerProperty.create("restore_state", 0, 2);

    // Shared pristine chunk generator for all restorer blocks
    private static final PristineChunkGenerator pristineGenerator = new PristineChunkGenerator();

    // State constants for readability
    private static final int STATE_DEFAULT = 0;   // Scanning and spreading
    private static final int STATE_READY = 1;     // Ready to restore
    private static final int STATE_COMPLETE = 2;  // Restoration complete

    public RestorerBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE)
            .randomTicks()
            .lightLevel(state -> 15));  // Full light emission
        this.registerDefaultState(
            this.stateDefinition.any().setValue(RESTORE_STATE, STATE_DEFAULT)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RESTORE_STATE);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        restore(level, pos, state, random);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Scheduled tick - same as random tick but more frequent
        restore(level, pos, state, random);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            restore((ServerLevel) level, pos, state, level.getRandom());
            // Schedule another tick with randomized delay - matches original line 202
            int delay = level.getRandom().nextInt(25) + level.getRandom().nextInt(10);
            level.scheduleTick(pos, this, delay);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide && !oldState.is(this)) {
            // Schedule first tick when block is placed with randomized delay
            int delay = level.getRandom().nextInt(25) + level.getRandom().nextInt(10);
            level.scheduleTick(pos, this, delay);
        }
    }

    /**
     * Main restoration logic - implements the 3-state cycle matching the original exactly
     */
    private void restore(ServerLevel level, BlockPos pos, BlockState currentState, RandomSource random) {
        int state = currentState.getValue(RESTORE_STATE);

        // State 1 and 2 both restore first (original lines 66-85)
        if (state == STATE_READY || state == STATE_COMPLETE) {
            BlockState pristineBlock = pristineGenerator.getPristineBlock(level, pos);
            if (pristineBlock != null) {
                // Restore the current position
                level.setBlockAndUpdate(pos, pristineBlock);
                LOGGER.debug("Restorer at {} restored itself to {}", pos, pristineBlock.getBlock());
                return;  // Block has been replaced, we're done
            }
        }

        // Now handle spreading and state transitions (STATE 0 logic)
        if (state == STATE_DEFAULT) {
            boolean flag = true;   // True if all adjacent blocks either match or spread succeeded
            boolean flag1 = true;  // True if no differences found at all

            // Check all 6 adjacent blocks (original lines 90-133)
            for (Direction dir : Direction.values()) {
                BlockPos targetPos = pos.relative(dir);
                BlockState currentBlock = level.getBlockState(targetPos);
                BlockState pristineBlock = pristineGenerator.getPristineBlock(level, targetPos);

                if (pristineBlock == null) {
                    continue;
                }

                // Check if blocks differ (compare block type only, like original)
                if (!currentBlock.is(pristineBlock.getBlock())) {
                    flag1 = false;  // Found at least one difference

                    // 50% chance to spread restorer block to this position (original line 110)
                    if (random.nextBoolean()) {
                        // Can spread into anything except bedrock/barrier (ALLOW spreading into goo!)
                        if (!currentBlock.is(Blocks.BEDROCK) && !currentBlock.is(Blocks.BARRIER)) {
                            level.setBlockAndUpdate(targetPos,
                                defaultBlockState().setValue(RESTORE_STATE, STATE_DEFAULT));

                            // Schedule the new restorer block to update soon (original line 116)
                            int delay = random.nextInt(25) + random.nextInt(10);
                            level.scheduleTick(targetPos, this, delay);

                            LOGGER.debug("Spread restorer to {}, scheduled in {} ticks", targetPos, delay);
                        }
                    } else {
                        // 50% chance we didn't spread - mark as incomplete
                        flag = false;
                    }
                }
            }

            // State transitions based on flags (original lines 141-149)
            if (flag && level.getBlockState(pos).is(this)) {
                // All adjacent blocks either match or we spread successfully
                level.setBlockAndUpdate(pos,
                    defaultBlockState().setValue(RESTORE_STATE, STATE_READY));
                LOGGER.debug("Restorer at {} transitioned to READY state", pos);
            } else if (flag1 && level.getBlockState(pos).is(this)) {
                // No differences found at all - ready to decay
                level.setBlockAndUpdate(pos,
                    defaultBlockState().setValue(RESTORE_STATE, STATE_COMPLETE));
                LOGGER.debug("Restorer at {} transitioned to COMPLETE state (no work)", pos);
            }

            // Schedule next update (original line 151)
            int delay = random.nextInt(25) + random.nextInt(10);
            level.scheduleTick(pos, this, delay);
        }
    }

    /**
     * Get the shared pristine chunk generator instance
     */
    public static PristineChunkGenerator getPristineGenerator() {
        return pristineGenerator;
    }
}
