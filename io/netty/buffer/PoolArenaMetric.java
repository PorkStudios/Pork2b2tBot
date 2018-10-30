/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.PoolChunkListMetric;
import io.netty.buffer.PoolSubpageMetric;
import java.util.List;

public interface PoolArenaMetric {
    public int numThreadCaches();

    public int numTinySubpages();

    public int numSmallSubpages();

    public int numChunkLists();

    public List<PoolSubpageMetric> tinySubpages();

    public List<PoolSubpageMetric> smallSubpages();

    public List<PoolChunkListMetric> chunkLists();

    public long numAllocations();

    public long numTinyAllocations();

    public long numSmallAllocations();

    public long numNormalAllocations();

    public long numHugeAllocations();

    public long numDeallocations();

    public long numTinyDeallocations();

    public long numSmallDeallocations();

    public long numNormalDeallocations();

    public long numHugeDeallocations();

    public long numActiveAllocations();

    public long numActiveTinyAllocations();

    public long numActiveSmallAllocations();

    public long numActiveNormalAllocations();

    public long numActiveHugeAllocations();

    public long numActiveBytes();
}

