package com.stevenrs11.greygoo.spread;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Manages timer-based spread sessions for rapid goo blocks.
 *
 * When a player right-clicks a rapid block, a new spread session is created.
 * All blocks that spread from that activation share the same session ID.
 * Sessions expire after a configurable duration, stopping all associated spread.
 *
 * This provides robust spread limiting without relying on broken metadata checks
 * or unlimited propagation.
 */
@Mod.EventBusSubscriber(modid = "greygoo")
public class RapidSpreadManager {

    private static final RapidSpreadManager INSTANCE = new RapidSpreadManager();

    // Default spread duration in ticks (400 ticks = 20 seconds, matches original)
    private static final int DEFAULT_SPREAD_DURATION = 400;

    // Map of session ID -> start tick
    private final Map<Integer, Long> activeSessions = new HashMap<>();

    // Next session ID to assign
    private int nextSessionId = 1;

    // Current server tick (updated each tick)
    private long currentTick = 0;

    // Configurable spread durations per goo type (can be extended)
    private int rapidEaterDuration = DEFAULT_SPREAD_DURATION;
    private int rapidWaterEaterDuration = DEFAULT_SPREAD_DURATION;
    private int rapidMinerDuration = DEFAULT_SPREAD_DURATION;

    private RapidSpreadManager() {}

    public static RapidSpreadManager get() {
        return INSTANCE;
    }

    /**
     * Start a new spread session. Returns the session ID to store in block state.
     * Session IDs are limited to 1-15 to match the block state property range.
     */
    public int startSession() {
        int sessionId = nextSessionId;
        activeSessions.put(sessionId, currentTick);

        // Wrap around at 15 (max value for SESSION_ID property)
        nextSessionId++;
        if (nextSessionId > 15) {
            nextSessionId = 1;
        }

        return sessionId;
    }

    /**
     * Check if a session is still active (not expired).
     * Session ID 0 means "no session" and is always considered inactive.
     */
    public boolean isSessionActive(int sessionId, int maxDuration) {
        if (sessionId == 0) {
            return false;  // No session assigned
        }

        Long startTick = activeSessions.get(sessionId);
        if (startTick == null) {
            return false;  // Session doesn't exist or was cleaned up
        }

        return (currentTick - startTick) < maxDuration;
    }

    /**
     * Check if a session is active using the default duration.
     */
    public boolean isSessionActive(int sessionId) {
        return isSessionActive(sessionId, DEFAULT_SPREAD_DURATION);
    }

    /**
     * Get the spread duration for rapid eater blocks.
     */
    public int getRapidEaterDuration() {
        return rapidEaterDuration;
    }

    /**
     * Get the spread duration for rapid water eater blocks.
     */
    public int getRapidWaterEaterDuration() {
        return rapidWaterEaterDuration;
    }

    /**
     * Get the spread duration for rapid miner blocks.
     */
    public int getRapidMinerDuration() {
        return rapidMinerDuration;
    }

    /**
     * Called each server tick to update timing and clean up expired sessions.
     */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            INSTANCE.tick();
        }
    }

    private void tick() {
        currentTick++;

        // Clean up expired sessions periodically (every 100 ticks)
        if (currentTick % 100 == 0) {
            cleanupExpiredSessions();
        }
    }

    /**
     * Remove sessions that have been expired for a while.
     * We keep them around a bit longer than their duration to handle edge cases.
     */
    private void cleanupExpiredSessions() {
        // Remove sessions that expired more than 200 ticks ago
        long cleanupThreshold = currentTick - DEFAULT_SPREAD_DURATION - 200;

        Iterator<Map.Entry<Integer, Long>> it = activeSessions.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Long> entry = it.next();
            if (entry.getValue() < cleanupThreshold) {
                it.remove();
            }
        }
    }

    /**
     * Reset the manager (called on server stop/world unload).
     */
    public void reset() {
        activeSessions.clear();
        currentTick = 0;
        nextSessionId = 1;
    }
}
