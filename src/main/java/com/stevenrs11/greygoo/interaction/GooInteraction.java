package com.stevenrs11.greygoo.interaction;

/**
 * Defines how two goo types interact when they meet.
 */
public enum GooInteraction {
    /**
     * Do nothing with this neighbor - skip it during spread checks.
     */
    IGNORE,

    /**
     * This goo becomes the other goo type (e.g., cleaner infection).
     * Highest priority interaction - stops all further processing.
     */
    CONVERT_SELF,

    /**
     * Convert the neighbor to this goo type (dominance).
     * Counts as "finding food" for starvation checks.
     */
    CONVERT_OTHER,

    /**
     * Normal spreading behavior - convert non-goo blocks to this goo.
     * Default for non-goo blocks.
     */
    SPREAD_INTO,

    /**
     * Can only spread if this goo type or Inert is adjacent.
     * Used by Wall and color variants.
     */
    REQUIRE_FOUNDATION,

    /**
     * Both goos ignore each other completely.
     * Used for defensive block interactions.
     */
    MUTUAL_IGNORE,

    /**
     * This neighbor doesn't count as food.
     * If ALL neighbors return this, the goo starves.
     */
    STARVE_CHECK;

    /**
     * Parse an interaction from a string (case-insensitive).
     */
    public static GooInteraction fromString(String name) {
        if (name == null) return IGNORE;
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return IGNORE;
        }
    }
}
