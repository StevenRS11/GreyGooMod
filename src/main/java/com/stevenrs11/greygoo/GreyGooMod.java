package com.stevenrs11.greygoo;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import com.stevenrs11.greygoo.GravityGooBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


// Custom creative tab registration
import com.stevenrs11.greygoo.GreyGooCreativeTabs;
import java.util.Set;
@Mod(GreyGooMod.MODID)
public class GreyGooMod {
    public static final String MODID = "greygoo";

    private static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Block> GREY_GOO_BLOCK = BLOCKS.register(
            "grey_goo_block",
            GreyGooBlock::new);

    public static final RegistryObject<Block> CLEANER_BLOCK = BLOCKS.register(
            "cleaner_block",
            CleanerBlock::new);

    public static final RegistryObject<Block> AIR_EATER_BLOCK = BLOCKS.register(
            "air_eater_block",
            AirEaterBlock::new);

    public static final RegistryObject<Block> WATER_EATER_BLOCK = BLOCKS.register(
            "water_eater_block",
            WaterEaterBlock::new);

    public static final RegistryObject<Block> GRAVITY_GOO_BLOCK = BLOCKS.register(
            "gravity_goo_block",
            GravityGooBlock::new);

    public static final RegistryObject<Item> GREY_GOO_BLOCK_ITEM = ITEMS.register(
            "grey_goo_block",
            () -> new BlockItem(GREY_GOO_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> CLEANER_BLOCK_ITEM = ITEMS.register(
            "cleaner_block",
            () -> new BlockItem(CLEANER_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> AIR_EATER_BLOCK_ITEM = ITEMS.register(
            "air_eater_block",
            () -> new BlockItem(AIR_EATER_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> WATER_EATER_BLOCK_ITEM = ITEMS.register(
            "water_eater_block",
            () -> new BlockItem(WATER_EATER_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> GRAVITY_GOO_BLOCK_ITEM = ITEMS.register(
            "gravity_goo_block",
            () -> new BlockItem(GRAVITY_GOO_BLOCK.get(), new Item.Properties()));

    public GreyGooMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        GreyGooCreativeTabs.register(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
    }

    public static boolean isGoo(Block block) {
        return block == GREY_GOO_BLOCK.get()
                || block == CLEANER_BLOCK.get()
                || block == AIR_EATER_BLOCK.get()
                || block == WATER_EATER_BLOCK.get()
                || block == GRAVITY_GOO_BLOCK.get();
    }
}
