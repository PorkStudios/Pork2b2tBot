/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.PoolArena;
import io.netty.buffer.PoolChunk;
import io.netty.buffer.PoolChunkListMetric;
import io.netty.buffer.PoolChunkMetric;
import io.netty.buffer.PooledByteBuf;
import io.netty.util.internal.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

final class PoolChunkList<T>
implements PoolChunkListMetric {
    private static final Iterator<PoolChunkMetric> EMPTY_METRICS = Collections.emptyList().iterator();
    private final PoolArena<T> arena;
    private final PoolChunkList<T> nextList;
    private final int minUsage;
    private final int maxUsage;
    private final int maxCapacity;
    private PoolChunk<T> head;
    private PoolChunkList<T> prevList;

    PoolChunkList(PoolArena<T> arena, PoolChunkList<T> nextList, int minUsage, int maxUsage, int chunkSize) {
        assert (minUsage <= maxUsage);
        this.arena = arena;
        this.nextList = nextList;
        this.minUsage = minUsage;
        this.maxUsage = maxUsage;
        this.maxCapacity = PoolChunkList.calculateMaxCapacity(minUsage, chunkSize);
    }

    private static int calculateMaxCapacity(int minUsage, int chunkSize) {
        if ((minUsage = PoolChunkList.minUsage0(minUsage)) == 100) {
            return 0;
        }
        return (int)((long)chunkSize * (100L - (long)minUsage) / 100L);
    }

    void prevList(PoolChunkList<T> prevList) {
        assert (this.prevList == null);
        this.prevList = prevList;
    }

    boolean allocate(PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {
        long handle;
        if (this.head == null || normCapacity > this.maxCapacity) {
            return false;
        }
        PoolChunk<T> cur = this.head;
        while ((handle = cur.allocate(normCapacity)) < 0L) {
            cur = cur.next;
            if (cur != null) continue;
            return false;
        }
        cur.initBuf(buf, handle, reqCapacity);
        if (cur.usage() >= this.maxUsage) {
            this.remove(cur);
            this.nextList.add(cur);
        }
        return true;
    }

    boolean free(PoolChunk<T> chunk, long handle) {
        chunk.free(handle);
        if (chunk.usage() < this.minUsage) {
            this.remove(chunk);
            return this.move0(chunk);
        }
        return true;
    }

    private boolean move(PoolChunk<T> chunk) {
        assert (chunk.usage() < this.maxUsage);
        if (chunk.usage() < this.minUsage) {
            return this.move0(chunk);
        }
        this.add0(chunk);
        return true;
    }

    private boolean move0(PoolChunk<T> chunk) {
        if (this.prevList == null) {
            assert (chunk.usage() == 0);
            return false;
        }
        return PoolChunkList.super.move(chunk);
    }

    void add(PoolChunk<T> chunk) {
        if (chunk.usage() >= this.maxUsage) {
            this.nextList.add(chunk);
            return;
        }
        this.add0(chunk);
    }

    void add0(PoolChunk<T> chunk) {
        chunk.parent = this;
        if (this.head == null) {
            this.head = chunk;
            chunk.prev = null;
            chunk.next = null;
        } else {
            chunk.prev = null;
            chunk.next = this.head;
            this.head.prev = chunk;
            this.head = chunk;
        }
    }

    private void remove(PoolChunk<T> cur) {
        if (cur == this.head) {
            this.head = cur.next;
            if (this.head != null) {
                this.head.prev = null;
            }
        } else {
            PoolChunk next;
            cur.prev.next = next = cur.next;
            if (next != null) {
                next.prev = cur.prev;
            }
        }
    }

    @Override
    public int minUsage() {
        return PoolChunkList.minUsage0(this.minUsage);
    }

    @Override
    public int maxUsage() {
        return Math.min(this.maxUsage, 100);
    }

    private static int minUsage0(int value) {
        return Math.max(1, value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Iterator<PoolChunkMetric> iterator() {
        PoolArena<T> poolArena = this.arena;
        synchronized (poolArena) {
            if (this.head == null) {
                return EMPTY_METRICS;
            }
            ArrayList<PoolChunk<T>> metrics = new ArrayList<PoolChunk<T>>();
            PoolChunk<T> cur = this.head;
            do {
                metrics.add(cur);
            } while ((cur = cur.next) != null);
            return metrics.iterator();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        StringBuilder buf = new StringBuilder();
        PoolArena<T> poolArena = this.arena;
        synchronized (poolArena) {
            if (this.head == null) {
                return "none";
            }
            PoolChunk<T> cur = this.head;
            do {
                buf.append(cur);
                cur = cur.next;
                if (cur == null) break;
                buf.append(StringUtil.NEWLINE);
            } while (true);
        }
        return buf.toString();
    }

    void destroy(PoolArena<T> arena) {
        PoolChunk<T> chunk = this.head;
        while (chunk != null) {
            arena.destroyChunk(chunk);
            chunk = chunk.next;
        }
        this.head = null;
    }
}

