package com.stevenrs11.greygoo.spread;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Utility class for finding blocks in various patterns.
 * Reproduces original SpreadHelper functionality.
 */
public class SpreadHelper {

    /**
     * Find blocks matching a predicate within a Manhattan distance radius.
     *
     * @param level The server level
     * @param center The center position
     * @param radius Maximum Manhattan distance
     * @param predicate Test for matching blocks
     * @return List of matching block positions
     */
    public static List<BlockPos> findBlocksInRadius(ServerLevel level, BlockPos center,
                                                     int radius, Predicate<BlockState> predicate) {
        List<BlockPos> found = new ArrayList<>();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    // Manhattan distance check
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) > radius) {
                        continue;
                    }

                    BlockPos pos = center.offset(dx, dy, dz);
                    if (pos.equals(center)) continue;  // Skip self

                    BlockState state = level.getBlockState(pos);
                    if (predicate.test(state)) {
                        found.add(pos);
                    }
                }
            }
        }

        return found;
    }

    /**
     * Find blocks matching a predicate only at the exact shell (max radius).
     *
     * @param level The server level
     * @param center The center position
     * @param radius Exact Manhattan distance (shell)
     * @param predicate Test for matching blocks
     * @return List of matching block positions
     */
    public static List<BlockPos> findBlocksAtShell(ServerLevel level, BlockPos center,
                                                    int radius, Predicate<BlockState> predicate) {
        List<BlockPos> found = new ArrayList<>();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    // Exact Manhattan distance check
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) != radius) {
                        continue;
                    }

                    BlockPos pos = center.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);
                    if (predicate.test(state)) {
                        found.add(pos);
                    }
                }
            }
        }

        return found;
    }

    /**
     * Find blocks in a 3x3x3 cube (26 neighbors).
     *
     * @param level The server level
     * @param center The center position
     * @param predicate Test for matching blocks
     * @return List of matching block positions
     */
    public static List<BlockPos> findBlocksInCube(ServerLevel level, BlockPos center,
                                                   Predicate<BlockState> predicate) {
        List<BlockPos> found = new ArrayList<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;

                    BlockPos pos = center.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);
                    if (predicate.test(state)) {
                        found.add(pos);
                    }
                }
            }
        }

        return found;
    }

    /**
     * Find adjacent blocks (6 direct neighbors).
     *
     * @param level The server level
     * @param center The center position
     * @param predicate Test for matching blocks
     * @return List of matching block positions
     */
    public static List<BlockPos> findAdjacentBlocks(ServerLevel level, BlockPos center,
                                                     Predicate<BlockState> predicate) {
        List<BlockPos> found = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            BlockPos pos = center.relative(dir);
            BlockState state = level.getBlockState(pos);
            if (predicate.test(state)) {
                found.add(pos);
            }
        }

        return found;
    }

    /**
     * Find the first block matching a predicate in the 6 directions.
     * Returns null if not found.
     */
    @Nullable
    public static BlockPos findFirstAdjacent(ServerLevel level, BlockPos center,
                                              Predicate<BlockState> predicate) {
        for (Direction dir : Direction.values()) {
            BlockPos pos = center.relative(dir);
            if (predicate.test(level.getBlockState(pos))) {
                return pos;
            }
        }
        return null;
    }

    /**
     * Find blocks of specific types within radius.
     */
    public static List<BlockPos> findBlocksOfType(ServerLevel level, BlockPos center,
                                                   int radius, Set<Block> targetBlocks) {
        return findBlocksInRadius(level, center, radius,
            state -> targetBlocks.contains(state.getBlock()));
    }

    /**
     * Find blocks NOT of specific types within radius.
     */
    public static List<BlockPos> findBlocksNotOfType(ServerLevel level, BlockPos center,
                                                      int radius, Set<Block> excludeBlocks) {
        return findBlocksInRadius(level, center, radius,
            state -> !excludeBlocks.contains(state.getBlock()));
    }

    /**
     * Get a random position from a list, or null if empty.
     */
    @Nullable
    public static BlockPos getRandomPosition(List<BlockPos> positions, net.minecraft.util.RandomSource random) {
        if (positions.isEmpty()) return null;
        return positions.get(random.nextInt(positions.size()));
    }

    /**
     * Shuffle a list of positions in place.
     */
    public static void shuffle(List<BlockPos> positions, net.minecraft.util.RandomSource random) {
        Collections.shuffle(positions, new java.util.Random(random.nextLong()));
    }
}
