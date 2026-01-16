# Restorer Block Performance Optimization Research

**Date**: 2026-01-15
**Status**: Research Phase - No Code Changes Yet
**Goal**: Identify optimization strategies for cross-dimension block queries and caching

---

## Table of Contents

1. [Current Implementation Analysis](#current-implementation-analysis)
2. [Performance Bottleneck Identification](#performance-bottleneck-identification)
3. [Minecraft Chunk Loading Mechanics](#minecraft-chunk-loading-mechanics)
4. [Optimization Strategies](#optimization-strategies)
5. [Caching System Designs](#caching-system-designs)
6. [Batch Operations](#batch-operations)
7. [Async Processing Considerations](#async-processing-considerations)
8. [Memory vs Performance Tradeoffs](#memory-vs-performance-tradeoffs)
9. [Recommended Implementation Approach](#recommended-implementation-approach)
10. [Benchmarking Plan](#benchmarking-plan)

---

## Current Implementation Analysis

### Data Flow

```
RestorerBlock.restore()
    └─> For each of 6 directions:
        └─> pristineGenerator.getPristineBlock(level, targetPos)
            └─> backupDimension.getBlockState(pos)
                └─> Calculate chunk coords from BlockPos
                └─> ServerChunkCache.getChunk(chunkX, chunkZ)
                    └─> If chunk not loaded:
                        └─> Generate/load chunk from disk
                    └─> ChunkAccess.getBlockState(x, y, z)
                        └─> Return BlockState
```

### Call Frequency

**Per Restorer Block Update**:
- STATE 0 (DEFAULT): 6 calls to `getPristineBlock()` (one per adjacent block)
- STATE 1 (READY): 1 call to `getPristineBlock()` (for self-position)
- STATE 2 (COMPLETE): 1 call to `getPristineBlock()` (for self-position)

**With Multiple Restorers**:
- 100 active restorer blocks = 600+ queries per tick cycle (assuming STATE 0)
- Each query potentially loads a chunk if not already loaded

### Current Timing

Each restorer updates with randomized delay: `random.nextInt(25) + random.nextInt(10)` (0-34 ticks)

Average: ~17 ticks between updates per block
Expected steady-state: ~6 restorers updating per tick (if 100 active)

---

## Performance Bottleneck Identification

### Primary Bottlenecks

1. **Chunk Loading in Backup Dimension**
   - **Cost**: High (first access per chunk)
   - **Frequency**: Once per chunk, then cached by MC
   - **Impact**: Severe for sparse restorer placement (different chunks)

2. **Repeated Dimension Switching**
   - **Cost**: Minimal (just accessing different ServerLevel reference)
   - **Frequency**: 6 times per restorer update
   - **Impact**: Low (ServerLevel is just an object reference)

3. **BlockState Lookups**
   - **Cost**: Very low (array access in loaded chunk)
   - **Frequency**: 6 times per restorer update
   - **Impact**: Minimal once chunk is loaded

4. **No Caching of Results**
   - **Cost**: None currently (no cache = no cache miss handling)
   - **Frequency**: N/A
   - **Impact**: High (redundant queries for same positions)

5. **Sequential Processing**
   - **Cost**: Single-threaded tick processing
   - **Frequency**: Continuous
   - **Impact**: Medium (can't parallelize even if wanted)

### Secondary Bottlenecks

6. **Block Comparison Logic**
   - **Cost**: Very low (Block reference comparison)
   - **Frequency**: 6 times per restorer update
   - **Impact**: Negligible

7. **State Updates**
   - **Cost**: Low (BlockState change + notification)
   - **Frequency**: Variable (only when spreading)
   - **Impact**: Low to medium (depending on spread rate)

### Bottleneck Ranking

**By Impact** (most severe first):
1. Chunk loading in backup dimension (HIGH)
2. No caching of pristine BlockStates (HIGH)
3. Sequential processing limitation (MEDIUM)
4. Repeated dimension access (LOW)
5. Block comparison overhead (NEGLIGIBLE)

---

## Minecraft Chunk Loading Mechanics

### ServerLevel.getBlockState() Internals

```java
// Simplified pseudo-code of what happens:
public BlockState getBlockState(BlockPos pos) {
    int chunkX = pos.getX() >> 4;  // Divide by 16
    int chunkZ = pos.getZ() >> 4;

    ChunkAccess chunk = this.getChunk(chunkX, chunkZ);  // <-- Expensive if not loaded

    int localX = pos.getX() & 15;  // Modulo 16
    int localY = pos.getY();
    int localZ = pos.getZ() & 15;

    return chunk.getBlockState(localX, localY, localZ);  // Array lookup - very fast
}
```

### Chunk Loading Cost

**If Chunk Already Loaded**: ~O(1) - map lookup + array access
**If Chunk Not Loaded**:
1. Check disk cache (~1-5ms for SSD, ~10-50ms for HDD)
2. If not on disk, generate chunk:
   - Terrain generation: ~50-200ms
   - Feature generation: ~20-100ms
   - Structure generation: ~10-500ms (if multi-chunk structure)
3. Add to loaded chunk cache

**Total**: 20ms - 500ms+ for first access

### Chunk Persistence

Once loaded, chunks remain in memory until:
- Player moves far away (unload distance exceeded)
- Server explicitly unloads (via chunk tickets)
- Dimension unloads (only happens on server restart)

**For Backup Dimension**:
- No players ever present = no forced loading
- Chunks load only when queried
- Chunks stay loaded until memory pressure or explicit unload
- No automatic unloading unless configured

### Chunk Ticket System

Minecraft uses "chunk tickets" to keep chunks loaded:
- Player tickets (11x11 chunk area around each player)
- Force-loaded chunks (via /forceload command)
- Entity tickets (for entities requiring chunk loading)
- Custom tickets (mods can add)

**Opportunity**: Could add chunk tickets to backup dimension to keep commonly-queried chunks loaded.

---

## Optimization Strategies

### Strategy 1: Chunk-Level Caching

**Concept**: Cache entire ChunkAccess references instead of individual BlockStates

**Pros**:
- Eliminates repeat chunk lookups
- Reduces backup dimension access
- Amortizes chunk loading cost across many queries

**Cons**:
- Memory usage (ChunkAccess can be 64KB+)
- Cache invalidation complexity (chunks shouldn't change, but...)
- Thread safety concerns

**Implementation Sketch**:
```java
private final Map<ChunkPos, ChunkAccess> chunkCache = new ConcurrentHashMap<>();

public BlockState getPristineBlock(ServerLevel level, BlockPos pos) {
    ChunkPos chunkPos = new ChunkPos(pos);

    ChunkAccess chunk = chunkCache.computeIfAbsent(chunkPos, cp ->
        backupDimension.getChunk(cp.x, cp.z)
    );

    return chunk.getBlockState(pos);
}
```

**Cache Size Management**:
- LRU eviction (LinkedHashMap with access order)
- Max size: 100-500 chunks (6.4MB - 32MB)
- Clear on world unload

### Strategy 2: BlockState Result Caching

**Concept**: Cache the actual pristine BlockStates for frequently-queried positions

**Pros**:
- Lowest possible lookup time
- Smallest memory footprint per entry
- Simple invalidation (never needed - pristine doesn't change)

**Cons**:
- High cache miss rate if restorers spread over large area
- Need intelligent eviction strategy
- Doesn't help with chunk loading

**Implementation Sketch**:
```java
private final LoadingCache<BlockPos, BlockState> blockCache =
    CacheBuilder.newBuilder()
        .maximumSize(10000)  // 10K blocks = ~320KB
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build(pos -> backupDimension.getBlockState(pos));

public BlockState getPristineBlock(ServerLevel level, BlockPos pos) {
    return blockCache.get(pos);
}
```

**Cache Hit Rate Estimation**:
- Restorers spread to adjacent blocks
- 6 adjacent positions per restorer
- High spatial locality = good cache hit rate
- Expected: 60-80% hit rate for dense restorer clusters

### Strategy 3: Batch Queries

**Concept**: Query multiple BlockStates in one operation

**Pros**:
- Amortizes chunk loading across multiple queries
- Can pre-fetch adjacent positions
- Reduces function call overhead

**Cons**:
- Requires API changes (breaking existing code)
- Complexity in handling partial results
- Doesn't help if positions span many chunks

**Implementation Sketch**:
```java
public Map<BlockPos, BlockState> getPristineBlocks(ServerLevel level, Set<BlockPos> positions) {
    // Group by chunk
    Map<ChunkPos, List<BlockPos>> byChunk = positions.stream()
        .collect(Collectors.groupingBy(ChunkPos::new));

    Map<BlockPos, BlockState> results = new HashMap<>();

    for (Map.Entry<ChunkPos, List<BlockPos>> entry : byChunk.entrySet()) {
        ChunkAccess chunk = backupDimension.getChunk(entry.getKey().x, entry.getKey().z);
        for (BlockPos pos : entry.getValue()) {
            results.put(pos, chunk.getBlockState(pos));
        }
    }

    return results;
}
```

**Usage in RestorerBlock**:
```java
// Collect all positions to query
Set<BlockPos> toQuery = new HashSet<>();
toQuery.add(pos);  // Self
for (Direction dir : Direction.values()) {
    toQuery.add(pos.relative(dir));  // Adjacent
}

// Single batch query
Map<BlockPos, BlockState> pristineStates = pristineGenerator.getPristineBlocks(level, toQuery);

// Use results
for (Direction dir : Direction.values()) {
    BlockPos targetPos = pos.relative(dir);
    BlockState pristineBlock = pristineStates.get(targetPos);
    // ... comparison logic ...
}
```

### Strategy 4: Predictive Chunk Loading

**Concept**: Pre-load chunks before restorers need them

**Pros**:
- Eliminates synchronous chunk loading cost
- Spreads load over multiple ticks
- Improves user experience (no stutters)

**Cons**:
- May load chunks that are never queried
- Increased memory usage
- Complex prediction logic

**Implementation Approaches**:

**A. Chunk Ticket System**:
```java
public void addChunkTicket(ChunkPos pos) {
    // Force chunk to stay loaded
    ForgeChunkManager.forceChunk(
        backupDimension,
        GreyGooMod.MODID,
        pos,
        true,  // add ticket
        true   // ticking
    );
}

// When restorer is placed, force-load surrounding chunks
for (int dx = -1; dx <= 1; dx++) {
    for (int dz = -1; dz <= 1; dz++) {
        addChunkTicket(new ChunkPos(chunkX + dx, chunkZ + dz));
    }
}
```

**B. Async Pre-loading**:
```java
public CompletableFuture<ChunkAccess> preloadChunk(ChunkPos pos) {
    return CompletableFuture.supplyAsync(() ->
        backupDimension.getChunk(pos.x, pos.z),
        Util.backgroundExecutor()  // MC's background thread pool
    );
}
```

### Strategy 5: Dimension-Local Cache

**Concept**: Store cache in backup dimension itself (dimension-specific data)

**Pros**:
- Persists across PristineChunkGenerator instances
- Automatically scoped to dimension
- Can leverage MC's own chunk caching

**Cons**:
- More complex implementation
- Harder to debug
- Couples cache to dimension lifecycle

### Strategy 6: Comparison Result Caching

**Concept**: Cache whether blocks differ, not the BlockStates themselves

**Pros**:
- Smallest possible cache entries (1 bit per position)
- Very high compression potential
- Perfect for "already restored" areas

**Cons**:
- Invalidation needed if overworld changes
- Doesn't help with initial queries
- Complexity of tracking changes

**Implementation Sketch**:
```java
// Bitmap cache: 1 bit per block = matches pristine
private final Map<ChunkPos, BitSet> restoredChunks = new HashMap<>();

public boolean blockMatchesPristine(BlockPos pos) {
    ChunkPos chunkPos = new ChunkPos(pos);
    BitSet restored = restoredChunks.get(chunkPos);

    if (restored == null) {
        // First time checking this chunk
        restored = new BitSet(16 * 256 * 16);  // Full chunk
        restoredChunks.put(chunkPos, restored);
    }

    int index = getBlockIndex(pos);
    if (restored.get(index)) {
        return true;  // Already know it matches
    }

    // Check and cache
    boolean matches = level.getBlockState(pos).is(
        backupDimension.getBlockState(pos).getBlock()
    );

    if (matches) {
        restored.set(index);
    }

    return matches;
}
```

**Memory**: ~8KB per chunk (64K blocks / 8 bits per byte)

---

## Caching System Designs

### Design 1: Two-Level Cache (Recommended)

**L1 Cache**: BlockState cache (small, fast, recent positions)
**L2 Cache**: ChunkAccess cache (larger, holds chunks)

```java
public class PristineChunkGenerator {
    // L1: Recent BlockStates (hot cache)
    private final Map<BlockPos, BlockState> blockCache = new LinkedHashMap<>(256, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<BlockPos, BlockState> eldest) {
            return size() > 256;
        }
    };

    // L2: Loaded chunks (warm cache)
    private final Map<ChunkPos, ChunkAccess> chunkCache = new LinkedHashMap<>(100, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<ChunkPos, ChunkAccess> eldest) {
            return size() > 100;
        }
    };

    public BlockState getPristineBlock(ServerLevel level, BlockPos pos) {
        // Check L1 cache
        BlockState cached = blockCache.get(pos);
        if (cached != null) {
            return cached;
        }

        // Check L2 cache
        ChunkPos chunkPos = new ChunkPos(pos);
        ChunkAccess chunk = chunkCache.get(chunkPos);
        if (chunk == null) {
            // Load from backup dimension
            chunk = backupDimension.getChunk(chunkPos.x, chunkPos.z);
            chunkCache.put(chunkPos, chunk);
        }

        // Get and cache block state
        BlockState state = chunk.getBlockState(pos);
        blockCache.put(pos, state);

        return state;
    }
}
```

**Performance Characteristics**:
- L1 hit: O(1) - instant
- L2 hit: O(1) map + array access - ~100ns
- L2 miss: chunk load - 20ms-500ms

**Memory Usage**:
- L1: 256 entries × ~40 bytes = ~10KB
- L2: 100 chunks × ~64KB = ~6.4MB
- **Total**: ~6.5MB

### Design 2: Guava LoadingCache (Simple)

**Concept**: Use Google Guava's cache library (already in Minecraft)

```java
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

public class PristineChunkGenerator {
    private final LoadingCache<BlockPos, BlockState> cache =
        CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<BlockPos, BlockState>() {
                public BlockState load(BlockPos pos) {
                    return backupDimension.getBlockState(pos);
                }
            });

    public BlockState getPristineBlock(ServerLevel level, BlockPos pos) {
        try {
            return cache.get(pos);
        } catch (ExecutionException e) {
            LOGGER.error("Failed to load pristine block", e);
            return null;
        }
    }
}
```

**Pros**:
- Minimal code
- Proven reliability
- Built-in stats (hit rate, miss rate)
- Automatic eviction

**Cons**:
- Doesn't leverage chunk-level caching
- More memory per entry (overhead from Guava)

### Design 3: Chunk-Only Cache (Minimal)

**Concept**: Cache only chunks, not individual blocks

```java
public class PristineChunkGenerator {
    private final Map<ChunkPos, ChunkAccess> cache =
        Collections.synchronizedMap(new LinkedHashMap<>(100, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<ChunkPos, ChunkAccess> eldest) {
                return size() > 100;
            }
        });

    public BlockState getPristineBlock(ServerLevel level, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);

        ChunkAccess chunk = cache.computeIfAbsent(chunkPos, cp ->
            backupDimension.getChunk(cp.x, cp.z)
        );

        return chunk.getBlockState(pos);
    }
}
```

**Pros**:
- Simplest implementation
- Good cache hit rate for clustered restorers
- Reasonable memory usage

**Cons**:
- No BlockState-level cache (higher lookup overhead)
- Thread safety needs careful consideration

---

## Batch Operations

### Batch Query API Design

```java
public interface BatchPristineQuery {
    /**
     * Query multiple blocks in a single operation.
     * Optimized for spatial locality (adjacent blocks).
     */
    Map<BlockPos, BlockState> getPristineBlocks(ServerLevel level, Collection<BlockPos> positions);

    /**
     * Query all blocks in a chunk.
     * Most efficient for dense queries.
     */
    Map<BlockPos, BlockState> getPristineChunk(ServerLevel level, ChunkPos chunkPos);

    /**
     * Query a cuboid region.
     * Optimized for large restoration areas.
     */
    Map<BlockPos, BlockState> getPristineRegion(
        ServerLevel level,
        BlockPos start,
        BlockPos end
    );
}
```

### Implementation Optimization

**Key Insight**: Group queries by chunk to minimize chunk loads

```java
public Map<BlockPos, BlockState> getPristineBlocks(ServerLevel level, Collection<BlockPos> positions) {
    // Group positions by chunk
    Map<ChunkPos, List<BlockPos>> byChunk = new HashMap<>();
    for (BlockPos pos : positions) {
        byChunk.computeIfAbsent(new ChunkPos(pos), k -> new ArrayList<>())
               .add(pos);
    }

    Map<BlockPos, BlockState> results = new HashMap<>(positions.size());

    // Load each chunk once, query all positions in it
    for (Map.Entry<ChunkPos, List<BlockPos>> entry : byChunk.entrySet()) {
        ChunkAccess chunk = getOrLoadChunk(entry.getKey());

        for (BlockPos pos : entry.getValue()) {
            results.put(pos, chunk.getBlockState(pos));
        }
    }

    return results;
}
```

**Performance Gain**:
- Without batching: 6 positions × 50ms chunk load = 300ms
- With batching: 1 chunk load × 50ms + 6 × 0.0001ms = 50.0006ms
- **Speedup**: 6x if all positions in same chunk

---

## Async Processing Considerations

### Challenge: Minecraft's Single-Threaded Tick

Minecraft's world tick is single-threaded. You **cannot** modify blocks from another thread.

**Safe for Async**:
- Reading BlockStates (mostly safe, with caveats)
- Chunk loading (MC does this async internally)
- Comparisons and calculations
- Cache updates

**Not Safe for Async**:
- `setBlockAndUpdate()` - must be on server thread
- `scheduleTick()` - must be on server thread
- Any world modification

### Async Query Pattern

```java
public CompletableFuture<Map<BlockPos, BlockState>> getPristineBlocksAsync(
    ServerLevel level,
    Collection<BlockPos> positions
) {
    return CompletableFuture.supplyAsync(() -> {
        // Safe: reading from backup dimension
        Map<BlockPos, BlockState> results = new HashMap<>();

        for (BlockPos pos : positions) {
            results.put(pos, backupDimension.getBlockState(pos));
        }

        return results;
    }, Util.backgroundExecutor());
}

// Usage in RestorerBlock:
public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (asyncQueryFuture != null && asyncQueryFuture.isDone()) {
        try {
            Map<BlockPos, BlockState> pristineStates = asyncQueryFuture.get();
            // Now on server thread, safe to modify blocks
            processResults(pristineStates);
        } catch (Exception e) {
            LOGGER.error("Async query failed", e);
        }
        asyncQueryFuture = null;
    } else if (asyncQueryFuture == null) {
        // Start new async query
        asyncQueryFuture = pristineGenerator.getPristineBlocksAsync(level, getAdjacentPositions(pos));
    }
}
```

**Complexity**: High
**Benefit**: Marginal (chunk loading is already async internally)
**Recommendation**: **Not worth it** for this use case

---

## Memory vs Performance Tradeoffs

### Cache Size Impact

| Cache Size | Memory | Hit Rate (estimated) | Queries/sec Handled |
|------------|--------|---------------------|---------------------|
| No cache | 0 | 0% | ~20 (limited by chunk loads) |
| 100 blocks | 4 KB | 20-30% | ~50 |
| 1,000 blocks | 40 KB | 50-70% | ~200 |
| 10,000 blocks | 400 KB | 80-90% | ~1000 |
| 100 chunks (64K blocks) | 6.4 MB | 90-95% | ~5000 |

**Recommendation**:
- Start with 1,000 block cache (40KB) + 50 chunk cache (3.2MB) = **3.24MB total**
- Monitor hit rates in production
- Adjust based on actual usage patterns

### Eviction Strategies

**LRU (Least Recently Used)**:
- **Pros**: Simple, good for varied access patterns
- **Cons**: Evicts blocks that might be needed soon

**LFU (Least Frequently Used)**:
- **Pros**: Keeps "hot" blocks longer
- **Cons**: Complex, requires tracking access counts

**TTL (Time To Live)**:
- **Pros**: Prevents stale data
- **Cons**: May evict still-needed data

**FIFO (First In First Out)**:
- **Pros**: Simplest possible
- **Cons**: Worst performance

**Recommendation**: **LRU** with access-order LinkedHashMap

### Memory Budget

**Conservative**: 1MB total cache
**Moderate**: 5MB total cache (recommended)
**Aggressive**: 20MB total cache

For reference:
- Minecraft server typically uses 2-8GB
- 5MB is 0.06% of 8GB
- Negligible impact

---

## Recommended Implementation Approach

### Phase 1: Chunk-Level Caching (Low Risk)

**Goal**: Eliminate repeated chunk loads

**Changes**:
```java
public class PristineChunkGenerator {
    // Simple chunk cache with LRU eviction
    private final Map<ChunkPos, ChunkAccess> chunkCache =
        Collections.synchronizedMap(new LinkedHashMap<>(50, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<ChunkPos, ChunkAccess> eldest) {
                return size() > 50;
            }
        });

    public BlockState getPristineBlock(ServerLevel level, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);

        ChunkAccess chunk = chunkCache.computeIfAbsent(chunkPos, cp -> {
            LOGGER.debug("Loading pristine chunk: {}", cp);
            return backupDimension.getChunk(cp.x, cp.z);
        });

        return chunk.getBlockState(pos);
    }

    public void clearCache() {
        chunkCache.clear();
        LOGGER.info("Cleared pristine chunk cache");
    }
}
```

**Expected Impact**:
- 5-10x speedup for adjacent block queries
- ~3MB memory usage
- Near-zero risk (simple, well-tested pattern)

### Phase 2: Batch Query API (Medium Risk)

**Goal**: Amortize chunk loads across multiple queries

**Changes**:
1. Add batch query method to PristineChunkGenerator
2. Update RestorerBlock to collect positions first, query in batch
3. Monitor performance improvement

**Expected Impact**:
- 2-3x speedup on top of Phase 1
- No additional memory
- Moderate complexity increase

### Phase 3: BlockState Caching (Low Risk)

**Goal**: Cache frequently-accessed positions

**Changes**:
```java
private final Map<BlockPos, BlockState> blockCache =
    new LinkedHashMap<>(1000, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<BlockPos, BlockState> eldest) {
            return size() > 1000;
        }
    };
```

**Expected Impact**:
- 1.5-2x speedup on top of previous phases
- +40KB memory
- Very low risk

### Phase 4: Monitoring and Tuning (No Risk)

**Goal**: Understand actual performance characteristics

**Changes**:
```java
private long cacheHits = 0;
private long cacheMisses = 0;

public void logStats() {
    long total = cacheHits + cacheMisses;
    double hitRate = total > 0 ? (cacheHits * 100.0 / total) : 0;

    LOGGER.info("Pristine cache stats: {} hits, {} misses, {:.1f}% hit rate",
        cacheHits, cacheMisses, hitRate);
}
```

**Expected Impact**:
- Actionable data for tuning
- Informs cache size decisions
- No performance cost (increment is cheap)

---

## Benchmarking Plan

### Test Scenarios

**Scenario 1: Single Restorer**
- Place 1 restorer in modified terrain
- Measure: time to detect differences, spread, restore
- Baseline for minimum overhead

**Scenario 2: Clustered Restorers (same chunk)**
- Place 10 restorers in 16x16 area
- Measure: cache hit rate, total restoration time
- Tests cache effectiveness

**Scenario 3: Sparse Restorers (different chunks)**
- Place 50 restorers across 1000x1000 area
- Measure: chunk load frequency, memory usage
- Worst-case cache performance

**Scenario 4: Dense Restoration (100x100 area)**
- Modify large area, place restorers at edges
- Measure: time to fully restore, server TPS impact
- Real-world usage pattern

### Metrics to Collect

**Performance**:
- Restoration rate (blocks/second)
- Cache hit rate (%)
- Average query latency (ms)
- Server TPS during restoration

**Resource Usage**:
- Memory usage (heap size)
- Chunk load count (backup dimension)
- Scheduled tick count

**Correctness**:
- Restoration accuracy (%)
- Missed blocks
- Over-restored blocks

### Tools

**Built-in**:
- `/debug start` and `/debug stop` for profiling
- F3 debug screen for TPS
- Spark profiler (mod) for detailed CPU analysis

**Custom**:
- Add timing logs around critical sections
- Track cache statistics
- Count chunk loads

---

## Conclusion and Next Steps

### Summary of Findings

1. **Primary Bottleneck**: Chunk loading in backup dimension (20-500ms per chunk)
2. **Best Quick Win**: Chunk-level caching (5-10x speedup for ~3MB)
3. **Best Long-term**: Two-level cache (block + chunk) with batch queries
4. **Avoid**: Async processing (complexity not worth marginal gains)

### Recommended Implementation Order

1. ✅ **Phase 1**: Chunk caching (1-2 hours, low risk)
2. ⏳ **Phase 2**: Monitoring/stats (30 min, no risk)
3. ⏳ **Phase 3**: Batch query API (2-4 hours, medium risk)
4. ⏳ **Phase 4**: BlockState cache (1 hour, low risk)
5. ⏳ **Phase 5**: Tuning based on real data

### Questions to Answer with Benchmarks

- What is actual cache hit rate in practice?
- Does two-level cache outperform single-level?
- What is optimal cache size for typical use?
- Is batch API worth the code complexity?
- How does this scale to 1000+ active restorers?

---

## References

### General Performance Resources

- [Better Chunk Loading Mod](https://www.curseforge.com/minecraft/mc-mods/better-chunk-loading-forge-fabric) - Chunk loading optimizations
- [C2ME Mod](https://modrinth.com/mod/c2mef) - Concurrent chunk management engine
- [Minecraft Optimization Guide](https://github.com/YouHaveTrouble/minecraft-optimization) - General server optimization

### Technical Documentation

- [ServerChunkCache JavaDocs](https://nekoyue.github.io/ForgeJavaDocs-NG/javadoc/1.17.1/net/minecraft/server/level/ServerChunkCache.html)
- [ChunkAccess JavaDocs](https://nekoyue.github.io/ForgeJavaDocs-NG/javadoc/1.18.2/net/minecraft/world/level/chunk/ChunkAccess.html)

### Caching Patterns

- Google Guava Cache (already in Minecraft)
- Java LinkedHashMap for LRU
- ConcurrentHashMap for thread-safe caching

---

**End of Research Document**

**Status**: Ready for implementation of Phase 1 (chunk caching)
**Next**: Implement and benchmark, then iterate based on data
