package com.stevenrs11.greygoo.blocks.inert;

import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.core.InertGooBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Inert Block (Green Goo) - non-spreading goo that persists forever.
 * Grey Goo converts to this when it runs out of food.
 * Also serves as foundation for color variant activation.
 */
public class InertBlock extends InertGooBlock {

    public InertBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
    }

    @Override
    public GooType getGooType() {
        return GooType.INERT;
    }
}
