package com.stevenrs11.greygoo.blocks.destroyer;

import com.stevenrs11.greygoo.core.GooType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * TGD Inert Block - the inactive state of TGD.
 *
 * Original behavior (BlockTGDinert.java):
 * - No random ticks (inert)
 * - Original had scheduled tick for golem spawning pattern check
 * - Golem spawning disabled per architecture plan
 *
 * This block is simply the inert end-state of TGD.
 */
public class TGDInertBlock extends Block {

    public TGDInertBlock() {
        // No random ticks - this block is inert
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
    }

    /**
     * Get the goo type for this block.
     */
    public GooType getGooType() {
        return GooType.TGD_INERT;
    }
}
