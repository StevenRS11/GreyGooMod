package com.stevenrs11.greygoo.blocks.special;

import com.stevenrs11.greygoo.GreyGooMod;
import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.interaction.GooInteraction;
import com.stevenrs11.greygoo.interaction.GooInteractionRegistry;
import com.stevenrs11.greygoo.interaction.ProtectedBlocks;
import com.stevenrs11.greygoo.spread.SpreadLimiterManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Gravity Goo - a goo that falls like sand and consumes blocks.
 *
 * Special case: extends FallingBlock instead of AbstractGooBlock to get
 * gravity physics. Manually implements the goo systems (GooType, SpreadLimiter,
 * GooInteractionRegistry, ProtectedBlocks).
 *
 * Original behavior: consumes non-air, non-fluid, non-protected blocks.
 * Dies when no food is found.
 */
public class GravityGooBlock extends FallingBlock {

    public GravityGooBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.SAND).randomTicks());
    }

    public GooType getGooType() {
        return GooType.GRAVITY_GOO;
    }

    private void spread(ServerLevel level, BlockPos pos, RandomSource random) {
        // Check spread limiter
        if (!SpreadLimiterManager.get().canSpread(getGooType(), random)) {
            return;
        }

        boolean hasFood = false;

        for (Direction dir : Direction.values()) {
            BlockPos target = pos.relative(dir);
            BlockState targetState = level.getBlockState(target);
            Block targetBlock = targetState.getBlock();

            // Check goo interactions via registry
            GooInteraction interaction = GooInteractionRegistry.get()
                .getInteraction(getGooType(), targetBlock);

            if (interaction == GooInteraction.CONVERT_SELF) {
                // Cleaner infection
                level.setBlockAndUpdate(pos, targetBlock.defaultBlockState());
                return;
            }

            if (interaction == GooInteraction.IGNORE) {
                continue;
            }

            // Skip protected blocks
            if (ProtectedBlocks.isProtected(targetState)) {
                continue;
            }

            // Skip air and fluids (original behavior)
            if (targetState.isAir()) {
                continue;
            }
            if (targetState.getFluidState().is(FluidTags.WATER) ||
                targetState.getFluidState().is(FluidTags.LAVA)) {
                continue;
            }

            // Consume the block
            level.setBlockAndUpdate(target, defaultBlockState());
            hasFood = true;
            SpreadLimiterManager.get().recordSpread(getGooType());
        }

        if (!hasFood) {
            // Gravity goo dies when it has no food
            level.destroyBlock(pos, false);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        spread(level, pos, random);
        // Also do falling check from parent
        super.randomTick(state, level, pos, random);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                  InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            spread((ServerLevel) level, pos, level.getRandom());
        }
        return InteractionResult.SUCCESS;
    }
}
