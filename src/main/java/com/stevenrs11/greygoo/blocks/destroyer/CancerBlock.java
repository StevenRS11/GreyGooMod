package com.stevenrs11.greygoo.blocks.destroyer;

import com.stevenrs11.greygoo.GreyGooMod;
import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.core.RandomTickGooBlock;
import com.stevenrs11.greygoo.interaction.ProtectedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Cancer Block - spreads orthogonally, destroys blocks, transforms to Black when starving.
 *
 * Original behavior (BlockCancer.java):
 * - Uses random ticks
 * - Spreads only to orthogonal neighbors (Manhattan distance == 1)
 * - Won't spread into: Black, Inert, Cancer2
 * - Won't destroy: Black, Cancer, Wall, Cancer2, TGD, TGDInert, Inert, Cleaner, protected
 * - Destroys (sets to air) other edible blocks
 * - Population density control: won't spread if 3+ Cancer blocks nearby
 * - Transforms to Black when no food in 7x7x7 area (with hasTicked toggle)
 * - Becomes inactive (metadata 2) when no food found this tick
 */
public class CancerBlock extends RandomTickGooBlock {

    public static final BooleanProperty INACTIVE = BooleanProperty.create("inactive");

    // Instance toggle for transformation delay (original uses hasTicked field)
    private boolean hasTicked = false;

    public CancerBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(INACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(INACTIVE);
    }

    @Override
    public GooType getGooType() {
        return GooType.CANCER;
    }

    @Override
    protected void doSpread(ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState currentState = level.getBlockState(pos);

        // Check if inactive - skip spreading (original: metadata == 2)
        if (currentState.getValue(INACTIVE)) {
            return;
        }

        // FIRST PASS: Check for food in 7x7x7 area (flag1 in original)
        // Food = non-air blocks that aren't Cancer, Cancer2, TGD, TGDInert, Black
        boolean hasFoodInArea = false;
        for (int l1 = -3; l1 < 4; l1++) {
            for (int i2 = -3; i2 < 4; i2++) {
                for (int j2 = -3; j2 < 4; j2++) {
                    BlockPos checkPos = pos.offset(l1, i2, j2);
                    BlockState state = level.getBlockState(checkPos);

                    if (!state.isAir()) {
                        Block block = state.getBlock();
                        if (block != this &&
                            block != GreyGooMod.CANCER2_BLOCK.get() &&
                            block != GreyGooMod.TGD_BLOCK.get() &&
                            block != GreyGooMod.TGD_INERT_BLOCK.get() &&
                            block != GreyGooMod.BLACK_DESTROYER_BLOCK.get()) {
                            hasFoodInArea = true;
                        }
                    }
                }
            }
        }

        boolean hasFood = false;

        // SECOND PASS: Spread and destroy (only if food exists in area)
        if (hasFoodInArea) {
            // byte0 in original - offset for density counting, starts at 0,
            // becomes -1 after first successful spread (allows one more nearby cancer)
            int spreadOffset = 0;

            // Original loop order: i1 (Y), j1 (Z), l (X)
            for (int i1 = -3; i1 < 4; i1++) {
                for (int j1 = -3; j1 < 4; j1++) {
                    for (int l = -3; l < 4; l++) {
                        if (l == 0 && i1 == 0 && j1 == 0) continue;

                        int manhattan = Math.abs(l) + Math.abs(i1) + Math.abs(j1);
                        BlockPos target = pos.offset(l, i1, j1);
                        BlockState targetState = level.getBlockState(target);

                        // SPREAD: Only to orthogonal neighbors (manhattan == 1)
                        // Original checks: != Black, != Inert, != Cancer2 (does NOT check != Cancer!)
                        if (manhattan == 1 && canSpreadInto(targetState)) {
                            // Count nearby cancer blocks at TARGET position
                            // Original: k1 starts at byte0 (0 or -1)
                            int k1 = spreadOffset;

                            // Count cancer in range around target
                            // Original quirk: k2, l2, i3 all start at -1, but k2 and i3
                            // reset to -2 after first iteration. This creates asymmetric counting.
                            int k2 = -1;
                            int l2 = -1;
                            int i3 = -1;
                            while (l2 < 3) {
                                while (i3 < 3) {
                                    while (k2 < 3) {
                                        int countManhattan = Math.abs(k2) + Math.abs(l2) + Math.abs(i3);
                                        if (countManhattan != 0 && countManhattan < 3) {
                                            BlockPos countPos = target.offset(k2, l2, i3);
                                            if (level.getBlockState(countPos).is(this)) {
                                                k1++;
                                            }
                                        }
                                        k2++;
                                    }
                                    k2 = -2;  // Reset to -2 (not -1) after first iteration
                                    i3++;
                                }
                                i3 = -2;  // Reset to -2 (not -1) after first iteration
                                l2++;
                            }

                            // Spread if density allows (< 3 nearby cancer)
                            if (k1 < 3) {
                                hasFood = true;
                                level.setBlockAndUpdate(target, defaultBlockState());
                                recordSpread();
                                spreadOffset = -1;  // Original: byte0 = -1
                            }
                        }

                        // DESTROY: Within manhattan < 5, re-fetch state after potential spread
                        if (manhattan < 5) {
                            BlockState currentTargetState = level.getBlockState(target);
                            if (canDestroy(currentTargetState)) {
                                hasFood = true;
                                level.removeBlock(target, false);
                            }
                        }
                    }
                }
            }
        }

        // Transform to Black if no food in area (with toggle delay)
        if (!hasFoodInArea) {
            hasTicked = !hasTicked;
            if (hasTicked) {
                level.setBlockAndUpdate(pos, GreyGooMod.BLACK_DESTROYER_BLOCK.get().defaultBlockState());
            }
            return;
        }

        // Become inactive if no food found this tick
        if (!hasFood) {
            onStarve(level, pos, currentState);
        }
    }

    /**
     * Check if Cancer can spread into this block.
     * Original only checks: != Black, != Inert, != Cancer2
     * NOTE: Cancer CAN spread into itself and into air!
     */
    private boolean canSpreadInto(BlockState state) {
        Block block = state.getBlock();

        // Can't spread into these specific blocks (original line 68)
        if (block == GreyGooMod.BLACK_DESTROYER_BLOCK.get()) return false;
        if (block == GreyGooMod.INERT_BLOCK.get()) return false;
        if (block == GreyGooMod.CANCER2_BLOCK.get()) return false;
        // NOTE: Original does NOT check for Cancer itself - it CAN spread into existing Cancer!

        // Can spread into air, Cancer, and everything else
        return true;
    }

    /**
     * Check if Cancer can destroy (remove) this block.
     * Original line 102 does NOT exclude air - air passes all the != checks
     * and counts as "food" for the hasFood flag (even though destroying air is a no-op).
     */
    private boolean canDestroy(BlockState state) {
        Block block = state.getBlock();

        // Can't destroy these (original line 102)
        if (block == GreyGooMod.BLACK_DESTROYER_BLOCK.get()) return false;
        if (block == this) return false;
        if (block == GreyGooMod.WALL_BLOCK.get()) return false;
        if (block == GreyGooMod.CANCER2_BLOCK.get()) return false;
        if (block == GreyGooMod.TGD_BLOCK.get()) return false;
        if (block == GreyGooMod.TGD_INERT_BLOCK.get()) return false;
        if (block == GreyGooMod.INERT_BLOCK.get()) return false;
        if (block == GreyGooMod.CLEANER_BLOCK.get()) return false;

        if (ProtectedBlocks.isProtected(state)) return false;

        // NOTE: Air is NOT excluded - it counts as food (though destroying it is a no-op)
        return true;
    }

    @Override
    protected void onStarve(ServerLevel level, BlockPos pos, BlockState state) {
        // Become inactive
        level.setBlockAndUpdate(pos, state.setValue(INACTIVE, true));
    }
}
