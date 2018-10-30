/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

import io.netty.buffer.PoolChunkMetric;

public interface PoolChunkListMetric
extends Iterable<PoolChunkMetric> {
    public int minUsage();

    public int maxUsage();
}

