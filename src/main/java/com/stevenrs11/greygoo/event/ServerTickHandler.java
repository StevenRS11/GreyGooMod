package com.stevenrs11.greygoo.event;

import com.stevenrs11.greygoo.GreyGooMod;
import com.stevenrs11.greygoo.spread.SpreadLimiterManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles server tick events for the Grey Goo mod.
 *
 * Primary responsibility: Reset spread limiter counters each tick.
 */
@Mod.EventBusSubscriber(modid = GreyGooMod.MODID)
public class ServerTickHandler {

    /**
     * Called at the end of each server tick.
     * Resets spread limiter counters for the next tick.
     */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        // Only process at the end of the tick
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        // Reset spread limiter counters
        SpreadLimiterManager.get().resetCounters();
    }
}
