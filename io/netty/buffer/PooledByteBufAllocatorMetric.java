/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.ByteBufAllocatorMetric;
import io.netty.buffer.PoolArenaMetric;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.internal.StringUtil;
import java.util.List;

public final class PooledByteBufAllocatorMetric
implements ByteBufAllocatorMetric {
    private final PooledByteBufAllocator allocator;

    PooledByteBufAllocatorMetric(PooledByteBufAllocator allocator) {
        this.allocator = allocator;
    }

    public int numHeapArenas() {
        return this.allocator.numHeapArenas();
    }

    public int numDirectArenas() {
        return this.allocator.numDirectArenas();
    }

    public List<PoolArenaMetric> heapArenas() {
        return this.allocator.heapArenas();
    }

    public List<PoolArenaMetric> directArenas() {
        return this.allocator.directArenas();
    }

    public int numThreadLocalCaches() {
        return this.allocator.numThreadLocalCaches();
    }

    public int tinyCacheSize() {
        return this.allocator.tinyCacheSize();
    }

    public int smallCacheSize() {
        return this.allocator.smallCacheSize();
    }

    public int normalCacheSize() {
        return this.allocator.normalCacheSize();
    }

    public int chunkSize() {
        return this.allocator.chunkSize();
    }

    @Override
    public long usedHeapMemory() {
        return this.allocator.usedHeapMemory();
    }

    @Override
    public long usedDirectMemory() {
        return this.allocator.usedDirectMemory();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append(StringUtil.simpleClassName(this)).append("(usedHeapMemory: ").append(this.usedHeapMemory()).append("; usedDirectMemory: ").append(this.usedDirectMemory()).append("; numHeapArenas: ").append(this.numHeapArenas()).append("; numDirectArenas: ").append(this.numDirectArenas()).append("; tinyCacheSize: ").append(this.tinyCacheSize()).append("; smallCacheSize: ").append(this.smallCacheSize()).append("; normalCacheSize: ").append(this.normalCacheSize()).append("; numThreadLocalCaches: ").append(this.numThreadLocalCaches()).append("; chunkSize: ").append(this.chunkSize()).append(')');
        return sb.toString();
    }
}

