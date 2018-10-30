/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.PoolArena;
import io.netty.buffer.PoolChunk;
import io.netty.buffer.PooledByteBuf;
import io.netty.util.Recycler;
import io.netty.util.ThreadDeathWatcher;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

final class PoolThreadCache {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(PoolThreadCache.class);
    final PoolArena<byte[]> heapArena;
    final PoolArena<ByteBuffer> directArena;
    private final MemoryRegionCache<byte[]>[] tinySubPageHeapCaches;
    private final MemoryRegionCache<byte[]>[] smallSubPageHeapCaches;
    private final MemoryRegionCache<ByteBuffer>[] tinySubPageDirectCaches;
    private final MemoryRegionCache<ByteBuffer>[] smallSubPageDirectCaches;
    private final MemoryRegionCache<byte[]>[] normalHeapCaches;
    private final MemoryRegionCache<ByteBuffer>[] normalDirectCaches;
    private final int numShiftsNormalDirect;
    private final int numShiftsNormalHeap;
    private final int freeSweepAllocationThreshold;
    private final Thread deathWatchThread;
    private final Runnable freeTask;
    private int allocations;

    PoolThreadCache(PoolArena<byte[]> heapArena, PoolArena<ByteBuffer> directArena, int tinyCacheSize, int smallCacheSize, int normalCacheSize, int maxCachedBufferCapacity, int freeSweepAllocationThreshold, boolean useThreadDeathWatcher) {
        if (maxCachedBufferCapacity < 0) {
            throw new IllegalArgumentException("maxCachedBufferCapacity: " + maxCachedBufferCapacity + " (expected: >= 0)");
        }
        this.freeSweepAllocationThreshold = freeSweepAllocationThreshold;
        this.heapArena = heapArena;
        this.directArena = directArena;
        if (directArena != null) {
            this.tinySubPageDirectCaches = PoolThreadCache.createSubPageCaches(tinyCacheSize, 32, PoolArena.SizeClass.Tiny);
            this.smallSubPageDirectCaches = PoolThreadCache.createSubPageCaches(smallCacheSize, directArena.numSmallSubpagePools, PoolArena.SizeClass.Small);
            this.numShiftsNormalDirect = PoolThreadCache.log2(directArena.pageSize);
            this.normalDirectCaches = PoolThreadCache.createNormalCaches(normalCacheSize, maxCachedBufferCapacity, directArena);
            directArena.numThreadCaches.getAndIncrement();
        } else {
            this.tinySubPageDirectCaches = null;
            this.smallSubPageDirectCaches = null;
            this.normalDirectCaches = null;
            this.numShiftsNormalDirect = -1;
        }
        if (heapArena != null) {
            this.tinySubPageHeapCaches = PoolThreadCache.createSubPageCaches(tinyCacheSize, 32, PoolArena.SizeClass.Tiny);
            this.smallSubPageHeapCaches = PoolThreadCache.createSubPageCaches(smallCacheSize, heapArena.numSmallSubpagePools, PoolArena.SizeClass.Small);
            this.numShiftsNormalHeap = PoolThreadCache.log2(heapArena.pageSize);
            this.normalHeapCaches = PoolThreadCache.createNormalCaches(normalCacheSize, maxCachedBufferCapacity, heapArena);
            heapArena.numThreadCaches.getAndIncrement();
        } else {
            this.tinySubPageHeapCaches = null;
            this.smallSubPageHeapCaches = null;
            this.normalHeapCaches = null;
            this.numShiftsNormalHeap = -1;
        }
        if ((this.tinySubPageDirectCaches != null || this.smallSubPageDirectCaches != null || this.normalDirectCaches != null || this.tinySubPageHeapCaches != null || this.smallSubPageHeapCaches != null || this.normalHeapCaches != null) && freeSweepAllocationThreshold < 1) {
            throw new IllegalArgumentException("freeSweepAllocationThreshold: " + freeSweepAllocationThreshold + " (expected: > 0)");
        }
        if (useThreadDeathWatcher) {
            this.freeTask = new Runnable(){

                @Override
                public void run() {
                    PoolThreadCache.this.free0();
                }
            };
            this.deathWatchThread = Thread.currentThread();
            ThreadDeathWatcher.watch(this.deathWatchThread, this.freeTask);
        } else {
            this.freeTask = null;
            this.deathWatchThread = null;
        }
    }

    private static <T> MemoryRegionCache<T>[] createSubPageCaches(int cacheSize, int numCaches, PoolArena.SizeClass sizeClass) {
        if (cacheSize > 0 && numCaches > 0) {
            MemoryRegionCache[] cache = new MemoryRegionCache[numCaches];
            for (int i = 0; i < cache.length; ++i) {
                cache[i] = new SubPageMemoryRegionCache(cacheSize, sizeClass);
            }
            return cache;
        }
        return null;
    }

    private static <T> MemoryRegionCache<T>[] createNormalCaches(int cacheSize, int maxCachedBufferCapacity, PoolArena<T> area) {
        if (cacheSize > 0 && maxCachedBufferCapacity > 0) {
            int max = Math.min(area.chunkSize, maxCachedBufferCapacity);
            int arraySize = Math.max(1, PoolThreadCache.log2(max / area.pageSize) + 1);
            MemoryRegionCache[] cache = new MemoryRegionCache[arraySize];
            for (int i = 0; i < cache.length; ++i) {
                cache[i] = new NormalMemoryRegionCache(cacheSize);
            }
            return cache;
        }
        return null;
    }

    private static int log2(int val) {
        int res = 0;
        while (val > 1) {
            val >>= 1;
            ++res;
        }
        return res;
    }

    boolean allocateTiny(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int normCapacity) {
        return this.allocate(this.cacheForTiny(area, normCapacity), buf, reqCapacity);
    }

    boolean allocateSmall(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int normCapacity) {
        return this.allocate(this.cacheForSmall(area, normCapacity), buf, reqCapacity);
    }

    boolean allocateNormal(PoolArena<?> area, PooledByteBuf<?> buf, int reqCapacity, int normCapacity) {
        return this.allocate(this.cacheForNormal(area, normCapacity), buf, reqCapacity);
    }

    private boolean allocate(MemoryRegionCache<?> cache, PooledByteBuf buf, int reqCapacity) {
        if (cache == null) {
            return false;
        }
        boolean allocated = cache.allocate(buf, reqCapacity);
        if (++this.allocations >= this.freeSweepAllocationThreshold) {
            this.allocations = 0;
            this.trim();
        }
        return allocated;
    }

    boolean add(PoolArena<?> area, PoolChunk chunk, long handle, int normCapacity, PoolArena.SizeClass sizeClass) {
        MemoryRegionCache<?> cache = this.cache(area, normCapacity, sizeClass);
        if (cache == null) {
            return false;
        }
        return cache.add(chunk, handle);
    }

    private MemoryRegionCache<?> cache(PoolArena<?> area, int normCapacity, PoolArena.SizeClass sizeClass) {
        switch (sizeClass) {
            case Normal: {
                return this.cacheForNormal(area, normCapacity);
            }
            case Small: {
                return this.cacheForSmall(area, normCapacity);
            }
            case Tiny: {
                return this.cacheForTiny(area, normCapacity);
            }
        }
        throw new Error();
    }

    void free() {
        if (this.freeTask != null) {
            assert (this.deathWatchThread != null);
            ThreadDeathWatcher.unwatch(this.deathWatchThread, this.freeTask);
        }
        this.free0();
    }

    private void free0() {
        int numFreed = PoolThreadCache.free(this.tinySubPageDirectCaches) + PoolThreadCache.free(this.smallSubPageDirectCaches) + PoolThreadCache.free(this.normalDirectCaches) + PoolThreadCache.free(this.tinySubPageHeapCaches) + PoolThreadCache.free(this.smallSubPageHeapCaches) + PoolThreadCache.free(this.normalHeapCaches);
        if (numFreed > 0 && logger.isDebugEnabled()) {
            logger.debug("Freed {} thread-local buffer(s) from thread: {}", (Object)numFreed, (Object)Thread.currentThread().getName());
        }
        if (this.directArena != null) {
            this.directArena.numThreadCaches.getAndDecrement();
        }
        if (this.heapArena != null) {
            this.heapArena.numThreadCaches.getAndDecrement();
        }
    }

    private static int free(MemoryRegionCache<?>[] caches) {
        if (caches == null) {
            return 0;
        }
        int numFreed = 0;
        for (MemoryRegionCache<?> c : caches) {
            numFreed += PoolThreadCache.free(c);
        }
        return numFreed;
    }

    private static int free(MemoryRegionCache<?> cache) {
        if (cache == null) {
            return 0;
        }
        return cache.free();
    }

    void trim() {
        PoolThreadCache.trim(this.tinySubPageDirectCaches);
        PoolThreadCache.trim(this.smallSubPageDirectCaches);
        PoolThreadCache.trim(this.normalDirectCaches);
        PoolThreadCache.trim(this.tinySubPageHeapCaches);
        PoolThreadCache.trim(this.smallSubPageHeapCaches);
        PoolThreadCache.trim(this.normalHeapCaches);
    }

    private static void trim(MemoryRegionCache<?>[] caches) {
        if (caches == null) {
            return;
        }
        for (MemoryRegionCache<?> c : caches) {
            PoolThreadCache.trim(c);
        }
    }

    private static void trim(MemoryRegionCache<?> cache) {
        if (cache == null) {
            return;
        }
        cache.trim();
    }

    private MemoryRegionCache<?> cacheForTiny(PoolArena<?> area, int normCapacity) {
        int idx = PoolArena.tinyIdx(normCapacity);
        if (area.isDirect()) {
            return PoolThreadCache.cache(this.tinySubPageDirectCaches, idx);
        }
        return PoolThreadCache.cache(this.tinySubPageHeapCaches, idx);
    }

    private MemoryRegionCache<?> cacheForSmall(PoolArena<?> area, int normCapacity) {
        int idx = PoolArena.smallIdx(normCapacity);
        if (area.isDirect()) {
            return PoolThreadCache.cache(this.smallSubPageDirectCaches, idx);
        }
        return PoolThreadCache.cache(this.smallSubPageHeapCaches, idx);
    }

    private MemoryRegionCache<?> cacheForNormal(PoolArena<?> area, int normCapacity) {
        if (area.isDirect()) {
            int idx = PoolThreadCache.log2(normCapacity >> this.numShiftsNormalDirect);
            return PoolThreadCache.cache(this.normalDirectCaches, idx);
        }
        int idx = PoolThreadCache.log2(normCapacity >> this.numShiftsNormalHeap);
        return PoolThreadCache.cache(this.normalHeapCaches, idx);
    }

    private static <T> MemoryRegionCache<T> cache(MemoryRegionCache<T>[] cache, int idx) {
        if (cache == null || idx > cache.length - 1) {
            return null;
        }
        return cache[idx];
    }

    private static abstract class MemoryRegionCache<T> {
        private final int size;
        private final Queue<Entry<T>> queue;
        private final PoolArena.SizeClass sizeClass;
        private int allocations;
        private static final Recycler<Entry> RECYCLER = new Recycler<Entry>(){

            @Override
            protected Entry newObject(Recycler.Handle<Entry> handle) {
                return new Entry(handle);
            }
        };

        MemoryRegionCache(int size, PoolArena.SizeClass sizeClass) {
            this.size = MathUtil.safeFindNextPositivePowerOfTwo(size);
            this.queue = PlatformDependent.newFixedMpscQueue(this.size);
            this.sizeClass = sizeClass;
        }

        protected abstract void initBuf(PoolChunk<T> var1, long var2, PooledByteBuf<T> var4, int var5);

        public final boolean add(PoolChunk<T> chunk, long handle) {
            Entry entry = MemoryRegionCache.newEntry(chunk, handle);
            boolean queued = this.queue.offer(entry);
            if (!queued) {
                entry.recycle();
            }
            return queued;
        }

        public final boolean allocate(PooledByteBuf<T> buf, int reqCapacity) {
            Entry<T> entry = this.queue.poll();
            if (entry == null) {
                return false;
            }
            this.initBuf(entry.chunk, entry.handle, buf, reqCapacity);
            entry.recycle();
            ++this.allocations;
            return true;
        }

        public final int free() {
            return this.free(Integer.MAX_VALUE);
        }

        private int free(int max) {
            int numFreed;
            for (numFreed = 0; numFreed < max; ++numFreed) {
                Entry<T> entry = this.queue.poll();
                if (entry == null) {
                    return numFreed;
                }
                this.freeEntry(entry);
            }
            return numFreed;
        }

        public final void trim() {
            int free = this.size - this.allocations;
            this.allocations = 0;
            if (free > 0) {
                this.free(free);
            }
        }

        private void freeEntry(Entry entry) {
            PoolChunk<T> chunk = entry.chunk;
            long handle = entry.handle;
            entry.recycle();
            chunk.arena.freeChunk(chunk, handle, this.sizeClass);
        }

        private static Entry newEntry(PoolChunk<?> chunk, long handle) {
            Entry entry = RECYCLER.get();
            entry.chunk = chunk;
            entry.handle = handle;
            return entry;
        }

        static final class Entry<T> {
            final Recycler.Handle<Entry<?>> recyclerHandle;
            PoolChunk<T> chunk;
            long handle = -1L;

            Entry(Recycler.Handle<Entry<?>> recyclerHandle) {
                this.recyclerHandle = recyclerHandle;
            }

            void recycle() {
                this.chunk = null;
                this.handle = -1L;
                this.recyclerHandle.recycle(this);
            }
        }

    }

    private static final class NormalMemoryRegionCache<T>
    extends MemoryRegionCache<T> {
        NormalMemoryRegionCache(int size) {
            super(size, PoolArena.SizeClass.Normal);
        }

        @Override
        protected void initBuf(PoolChunk<T> chunk, long handle, PooledByteBuf<T> buf, int reqCapacity) {
            chunk.initBuf(buf, handle, reqCapacity);
        }
    }

    private static final class SubPageMemoryRegionCache<T>
    extends MemoryRegionCache<T> {
        SubPageMemoryRegionCache(int size, PoolArena.SizeClass sizeClass) {
            super(size, sizeClass);
        }

        @Override
        protected void initBuf(PoolChunk<T> chunk, long handle, PooledByteBuf<T> buf, int reqCapacity) {
            chunk.initBufWithSubpage(buf, handle, reqCapacity);
        }
    }

}

