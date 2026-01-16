package com.stevenrs11.greygoo.spread;

import com.stevenrs11.greygoo.core.GooType;
import net.minecraft.util.RandomSource;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages spread rate limiting for all goo types.
 *
 * Features:
 * - Per-goo-type limits with soft caps and throttling
 * - Global limits across all goo types
 * - Probabilistic throttling as counts approach limits
 * - Counter reset each server tick
 */
public class SpreadLimiterManager {
    private static SpreadLimiterManager INSTANCE;

    // Per-goo-type counters (reset each tick)
    private final Map<GooType, AtomicInteger> counters = new EnumMap<>(GooType.class);

    // Per-goo-type configuration
    private final Map<GooType, LimiterConfig> configs = new EnumMap<>(GooType.class);

    // Global counters and limits
    private final AtomicInteger globalCounter = new AtomicInteger(0);
    private int globalSoftCap = 300;
    private int globalHardCap = 600;

    /**
     * Configuration for a single goo type's spread limiter.
     */
    public static class LimiterConfig {
        public int maxPerTick;
        public int softCapStart;
        public float throttleScale;

        public LimiterConfig(int maxPerTick, int softCapStart, float throttleScale) {
            this.maxPerTick = maxPerTick;
            this.softCapStart = softCapStart;
            this.throttleScale = throttleScale;
        }
    }

    private SpreadLimiterManager() {
        // Initialize counters and default configs for all goo types
        for (GooType type : GooType.values()) {
            counters.put(type, new AtomicInteger(0));
            configs.put(type, new LimiterConfig(
                type.defaultMaxPerTick,
                type.defaultSoftCap,
                type.defaultThrottleScale
            ));
        }
    }

    /**
     * Get the singleton instance.
     */
    public static SpreadLimiterManager get() {
        if (INSTANCE == null) {
            INSTANCE = new SpreadLimiterManager();
        }
        return INSTANCE;
    }

    /**
     * Check if a goo of this type is allowed to spread.
     * Uses probabilistic throttling as counts approach limits.
     *
     * @param type The goo type attempting to spread
     * @param random Random source for probabilistic checks
     * @return true if spreading is allowed
     */
    public boolean canSpread(GooType type, RandomSource random) {
        // 1. Check global hard cap
        int globalCount = globalCounter.get();
        if (globalCount >= globalHardCap) {
            return false;
        }

        // 2. Global soft cap throttling
        if (globalCount >= globalSoftCap) {
            float ratio = (float)(globalCount - globalSoftCap) / (globalHardCap - globalSoftCap);
            if (random.nextFloat() >= (1.0f - ratio * 0.5f)) {
                return false;
            }
        }

        // 3. Per-type hard cap
        LimiterConfig config = configs.get(type);
        if (config == null || config.maxPerTick <= 0) {
            // No config or zero limit = never spread (inert types)
            return config != null && config.maxPerTick == 0 ? false : true;
        }

        int typeCount = counters.get(type).get();
        if (typeCount >= config.maxPerTick) {
            return false;
        }

        // 4. Per-type soft cap throttling
        if (typeCount >= config.softCapStart) {
            float ratio = (float)(typeCount - config.softCapStart) /
                         (config.maxPerTick - config.softCapStart);
            if (random.nextFloat() >= (1.0f - ratio * config.throttleScale)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Record that a spread action occurred.
     * Call after successfully spreading.
     */
    public void recordSpread(GooType type) {
        counters.get(type).incrementAndGet();
        globalCounter.incrementAndGet();
    }

    /**
     * Reset all counters. Called each server tick.
     */
    public void resetCounters() {
        for (AtomicInteger counter : counters.values()) {
            counter.set(0);
        }
        globalCounter.set(0);
    }

    // ==================== Configuration Methods ====================

    /**
     * Update configuration for a specific goo type.
     */
    public void setConfig(GooType type, int maxPerTick, int softCapStart, float throttleScale) {
        configs.put(type, new LimiterConfig(maxPerTick, softCapStart, throttleScale));
    }

    /**
     * Set global limits.
     */
    public void setGlobalLimits(int softCap, int hardCap) {
        this.globalSoftCap = softCap;
        this.globalHardCap = hardCap;
    }

    /**
     * Get current spread count for a goo type (for debugging/display).
     */
    public int getCurrentCount(GooType type) {
        return counters.get(type).get();
    }

    /**
     * Get current global spread count (for debugging/display).
     */
    public int getGlobalCount() {
        return globalCounter.get();
    }

    /**
     * Get config for a goo type.
     */
    public LimiterConfig getConfig(GooType type) {
        return configs.get(type);
    }

    /**
     * Reload all configurations from defaults.
     * Call when config files change.
     */
    public void reloadDefaults() {
        for (GooType type : GooType.values()) {
            configs.put(type, new LimiterConfig(
                type.defaultMaxPerTick,
                type.defaultSoftCap,
                type.defaultThrottleScale
            ));
        }
    }
}
