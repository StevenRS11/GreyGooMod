package com.stevenrs11.greygoo;

import com.stevenrs11.greygoo.blocks.color.OrangePurpleBlock;
import com.stevenrs11.greygoo.blocks.color.OrangeRedBlock;
import com.stevenrs11.greygoo.blocks.color.OrangeWhiteBlock;
import com.stevenrs11.greygoo.blocks.color.PurpleGooBlock;
import com.stevenrs11.greygoo.blocks.consumer.AirEaterBlock;
import com.stevenrs11.greygoo.blocks.consumer.GreyGooBlock;
import com.stevenrs11.greygoo.blocks.consumer.MinerGooBlock;
import com.stevenrs11.greygoo.blocks.consumer.WaterEaterBlock;
import com.stevenrs11.greygoo.blocks.defensive.CleanerBlock;
import com.stevenrs11.greygoo.blocks.defensive.WallBlock;
import com.stevenrs11.greygoo.blocks.destroyer.BlackDestroyerBlock;
import com.stevenrs11.greygoo.blocks.destroyer.Cancer2Block;
import com.stevenrs11.greygoo.blocks.destroyer.CancerBlock;
import com.stevenrs11.greygoo.blocks.destroyer.TGDBlock;
import com.stevenrs11.greygoo.blocks.destroyer.TGDInertBlock;
import com.stevenrs11.greygoo.blocks.inert.InertBlock;
import com.stevenrs11.greygoo.blocks.rapid.RapidEaterBlock;
import com.stevenrs11.greygoo.blocks.rapid.RapidMinerBlock;
import com.stevenrs11.greygoo.blocks.rapid.RapidWaterEaterBlock;
import com.stevenrs11.greygoo.blocks.special.GravityGooBlock;
import com.stevenrs11.greygoo.core.GooType;
import com.stevenrs11.greygoo.interaction.GooInteractionRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(GreyGooMod.MODID)
public class GreyGooMod {
    public static final String MODID = "greygoo";
    private static final Logger LOGGER = LoggerFactory.getLogger(GreyGooMod.class);

    private static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // ==================== Block Registrations ====================

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

    public static final RegistryObject<Block> RAPID_WATER_EATER_BLOCK = BLOCKS.register(
            "rapid_water_eater_block",
            RapidWaterEaterBlock::new);

    public static final RegistryObject<Block> GRAVITY_GOO_BLOCK = BLOCKS.register(
            "gravity_goo_block",
            GravityGooBlock::new);

    public static final RegistryObject<Block> WALL_BLOCK = BLOCKS.register(
            "wall_block",
            WallBlock::new);

    public static final RegistryObject<Block> INERT_BLOCK = BLOCKS.register(
            "inert_block",
            InertBlock::new);

    public static final RegistryObject<Block> MINER_GOO_BLOCK = BLOCKS.register(
            "miner_goo_block",
            MinerGooBlock::new);

    public static final RegistryObject<Block> RAPID_EATER_BLOCK = BLOCKS.register(
            "rapid_eater_block",
            RapidEaterBlock::new);

    public static final RegistryObject<Block> RAPID_MINER_BLOCK = BLOCKS.register(
            "rapid_miner_block",
            RapidMinerBlock::new);

    public static final RegistryObject<Block> PURPLE_GOO_BLOCK = BLOCKS.register(
            "purple_goo_block",
            PurpleGooBlock::new);

    public static final RegistryObject<Block> ORANGE_PURPLE_BLOCK = BLOCKS.register(
            "orange_purple_block",
            OrangePurpleBlock::new);

    public static final RegistryObject<Block> ORANGE_RED_BLOCK = BLOCKS.register(
            "orange_red_block",
            OrangeRedBlock::new);

    public static final RegistryObject<Block> ORANGE_WHITE_BLOCK = BLOCKS.register(
            "orange_white_block",
            OrangeWhiteBlock::new);

    // Kept as-is per user preference (not refactored)
    public static final RegistryObject<Block> REDYELLOW_BLOCK = BLOCKS.register(
            "redyellow_block",
            RedyellowBlock::new);

    // Kept as-is per user preference (complex dimension system)
    public static final RegistryObject<Block> RESTORER_BLOCK = BLOCKS.register(
            "restorer_block",
            RestorerBlock::new);

    // ==================== Destroyer Blocks ====================

    public static final RegistryObject<Block> BLACK_DESTROYER_BLOCK = BLOCKS.register(
            "black_destroyer_block",
            BlackDestroyerBlock::new);

    public static final RegistryObject<Block> CANCER_BLOCK = BLOCKS.register(
            "cancer_block",
            CancerBlock::new);

    public static final RegistryObject<Block> CANCER2_BLOCK = BLOCKS.register(
            "cancer2_block",
            Cancer2Block::new);

    public static final RegistryObject<Block> TGD_BLOCK = BLOCKS.register(
            "tgd_block",
            TGDBlock::new);

    public static final RegistryObject<Block> TGD_INERT_BLOCK = BLOCKS.register(
            "tgd_inert_block",
            TGDInertBlock::new);

    // ==================== Item Registrations ====================

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

    public static final RegistryObject<Item> RAPID_WATER_EATER_BLOCK_ITEM = ITEMS.register(
            "rapid_water_eater_block",
            () -> new BlockItem(RAPID_WATER_EATER_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> GRAVITY_GOO_BLOCK_ITEM = ITEMS.register(
            "gravity_goo_block",
            () -> new BlockItem(GRAVITY_GOO_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> WALL_BLOCK_ITEM = ITEMS.register(
            "wall_block",
            () -> new BlockItem(WALL_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> INERT_BLOCK_ITEM = ITEMS.register(
            "inert_block",
            () -> new BlockItem(INERT_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> MINER_GOO_BLOCK_ITEM = ITEMS.register(
            "miner_goo_block",
            () -> new BlockItem(MINER_GOO_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> RAPID_EATER_BLOCK_ITEM = ITEMS.register(
            "rapid_eater_block",
            () -> new BlockItem(RAPID_EATER_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> RAPID_MINER_BLOCK_ITEM = ITEMS.register(
            "rapid_miner_block",
            () -> new BlockItem(RAPID_MINER_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> PURPLE_GOO_BLOCK_ITEM = ITEMS.register(
            "purple_goo_block",
            () -> new BlockItem(PURPLE_GOO_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORANGE_PURPLE_BLOCK_ITEM = ITEMS.register(
            "orange_purple_block",
            () -> new BlockItem(ORANGE_PURPLE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORANGE_RED_BLOCK_ITEM = ITEMS.register(
            "orange_red_block",
            () -> new BlockItem(ORANGE_RED_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORANGE_WHITE_BLOCK_ITEM = ITEMS.register(
            "orange_white_block",
            () -> new BlockItem(ORANGE_WHITE_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> REDYELLOW_BLOCK_ITEM = ITEMS.register(
            "redyellow_block",
            () -> new BlockItem(REDYELLOW_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> RESTORER_BLOCK_ITEM = ITEMS.register(
            "restorer_block",
            () -> new BlockItem(RESTORER_BLOCK.get(), new Item.Properties()));

    // ==================== Destroyer Block Items ====================

    public static final RegistryObject<Item> BLACK_DESTROYER_BLOCK_ITEM = ITEMS.register(
            "black_destroyer_block",
            () -> new BlockItem(BLACK_DESTROYER_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> CANCER_BLOCK_ITEM = ITEMS.register(
            "cancer_block",
            () -> new BlockItem(CANCER_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> CANCER2_BLOCK_ITEM = ITEMS.register(
            "cancer2_block",
            () -> new BlockItem(CANCER2_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> TGD_BLOCK_ITEM = ITEMS.register(
            "tgd_block",
            () -> new BlockItem(TGD_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> TGD_INERT_BLOCK_ITEM = ITEMS.register(
            "tgd_inert_block",
            () -> new BlockItem(TGD_INERT_BLOCK.get(), new Item.Properties()));

    // ==================== Mod Constructor ====================

    public GreyGooMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        GreyGooCreativeTabs.register(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        // Register setup event
        modEventBus.addListener(this::commonSetup);
    }

    /**
     * Common setup - initialize goo type mappings and load configs.
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Initializing Grey Goo mod systems...");

            // Register block mappings for GooType
            registerGooTypeBlocks();

            // Initialize the block-to-type mapping
            GooType.initBlockMapping();

            // Load interaction registry from config
            GooInteractionRegistry.get().loadFromConfig();

            LOGGER.info("Grey Goo mod initialization complete.");
        });
    }

    /**
     * Register block suppliers for each GooType.
     */
    private void registerGooTypeBlocks() {
        GooType.GREY_GOO.registerBlock(GREY_GOO_BLOCK::get);
        GooType.CLEANER.registerBlock(CLEANER_BLOCK::get);
        GooType.AIR_EATER.registerBlock(AIR_EATER_BLOCK::get);
        GooType.WATER_EATER.registerBlock(WATER_EATER_BLOCK::get);
        GooType.RAPID_WATER_EATER.registerBlock(RAPID_WATER_EATER_BLOCK::get);
        GooType.GRAVITY_GOO.registerBlock(GRAVITY_GOO_BLOCK::get);
        GooType.WALL.registerBlock(WALL_BLOCK::get);
        GooType.RESTORER.registerBlock(RESTORER_BLOCK::get);
        GooType.REDYELLOW.registerBlock(REDYELLOW_BLOCK::get);
        GooType.INERT.registerBlock(INERT_BLOCK::get);
        GooType.MINER_GOO.registerBlock(MINER_GOO_BLOCK::get);
        GooType.RAPID_EATER.registerBlock(RAPID_EATER_BLOCK::get);
        GooType.RAPID_MINER.registerBlock(RAPID_MINER_BLOCK::get);
        GooType.PURPLE_GOO.registerBlock(PURPLE_GOO_BLOCK::get);
        GooType.ORANGE_PURPLE.registerBlock(ORANGE_PURPLE_BLOCK::get);
        GooType.ORANGE_RED.registerBlock(ORANGE_RED_BLOCK::get);
        GooType.ORANGE_WHITE.registerBlock(ORANGE_WHITE_BLOCK::get);
        // Destroyer blocks
        GooType.BLACK_DESTROYER.registerBlock(BLACK_DESTROYER_BLOCK::get);
        GooType.CANCER.registerBlock(CANCER_BLOCK::get);
        GooType.CANCER2.registerBlock(CANCER2_BLOCK::get);
        GooType.TGD.registerBlock(TGD_BLOCK::get);
        GooType.TGD_INERT.registerBlock(TGD_INERT_BLOCK::get);
    }

    /**
     * Check if a block is any type of goo.
     * Uses the GooType registry for a centralized check.
     */
    public static boolean isGoo(Block block) {
        return GooType.isGoo(block);
    }
}
