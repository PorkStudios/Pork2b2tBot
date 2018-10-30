/*
 * Decompiled with CFR 0_132.
 */
package io.netty.buffer;

public interface PoolSubpageMetric {
    public int maxNumElements();

    public int numAvailable();

    public int elementSize();

    public int pageSize();
}

