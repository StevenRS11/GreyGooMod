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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * OrangeWhite Block - spreads with foundation requirement, acts as ladder.
 *
 * Original behavior (BlockOrangeWhite.java):
 * - setTickRandomly(false) - uses SCHEDULED ticks, not random ticks
 * - Right-click activates spreading via OrangeWhiteIsSpreading flag (we use sessions)
 * - Requires foundation: OrangeWhite OR Inert on opposite side of spread direction
 * - Spreads only to orthogonal neighbors (Manhattan distance < 2)
 * - Extra check: won't spread if 2 positions AHEAD already has this block
 * - Can spread into any block except inert
 * - Special collision bounds (narrower pillar shape)
 * - Non-opaque rendering
 * - Acts as a ladder (climbable)
 */
public class OrangeWhiteBlock extends Block {

    /**
     * Session ID property - same as rapid goos.
     * 0 = no session (won't spread), 1-15 = active session.
     */
    public static final IntegerProperty SESSION_ID = IntegerProperty.create("session", 0, 15);

    // Narrow pillar shape: 0.31 to 0.69 on X and Z (original: .31F, 0F, .31F, .69F, 1F, .69F)
    private static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);

    public OrangeWhiteBlock() {
        // No randomTicks - uses scheduled ticks only
        // Non-solid for ladder behavior
        super(BlockBehaviour.Properties.copy(Blocks.STONE)
                .noOcclusion()  // Non-opaque
                .noCollission());  // Allow walking through for ladder behavior
        this.registerDefaultState(this.stateDefinition.any().setValue(SESSION_ID, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SESSION_ID);
    }

    public GooType getGooType() {
        return GooType.ORANGE_WHITE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // Use same narrow shape for collision
        return SHAPE;
    }

    /**
     * Acts as a ladder - players can climb this block.
     */
    @Override
    public boolean isLadder(BlockState state, net.minecraft.world.level.LevelReader level, BlockPos pos, net.minecraft.world.entity.LivingEntity entity) {
        return true;
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

            // Schedule this block and neighbors for activation (original behavior)
            level.scheduleTick(pos, this, 3);
            level.scheduleTick(pos.east(), this, random.nextInt(5));
            level.scheduleTick(pos.west(), this, random.nextInt(5));
            level.scheduleTick(pos.north(), this, random.nextInt(5));
            level.scheduleTick(pos.south(), this, random.nextInt(5));
            level.scheduleTick(pos.below(), this, random.nextInt(5));

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    /**
     * Spread to orthogonal neighbors with foundation requirement.
     * Extra check: won't spread if 2 positions ahead already has this block.
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

                    // Check foundation requirement: need OrangeWhite or Inert on OPPOSITE side
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

                    // Extra check: won't spread if 2 positions AHEAD already has this block
                    // This prevents overly long chains
                    BlockPos ahead = pos.offset(dx * 2, dy * 2, dz * 2);
                    if (level.getBlockState(ahead).is(this)) {
                        continue;
                    }

                    // Spread to target
                    level.setBlockAndUpdate(target, defaultBlockState().setValue(SESSION_ID, sessionId));

                    // Schedule with random delay
                    level.scheduleTick(target, this, random.nextInt(6));
                }
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        // Don't auto-schedule - spreading is controlled by right-click and session system
    }
}
