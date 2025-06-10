package com.stevenrs11.greygoo;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.RegistryObject;

public class GreyGooCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GreyGooMod.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("greygoo",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + GreyGooMod.MODID))
                    .icon(() -> new ItemStack(GreyGooMod.GREY_GOO_BLOCK_ITEM.get()))
                    .displayItems((params, output) -> {
                        output.accept(GreyGooMod.GREY_GOO_BLOCK_ITEM.get());
                        output.accept(GreyGooMod.CLEANER_BLOCK_ITEM.get());
                        output.accept(GreyGooMod.AIR_EATER_BLOCK_ITEM.get());
                        output.accept(GreyGooMod.WATER_EATER_BLOCK_ITEM.get());
                        output.accept(GreyGooMod.GRAVITY_GOO_BLOCK_ITEM.get());
                        output.accept(GreyGooMod.REDYELLOW_BLOCK_ITEM.get());
                    })
                    .build());

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
