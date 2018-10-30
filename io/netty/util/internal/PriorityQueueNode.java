/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal;

import io.netty.util.internal.DefaultPriorityQueue;

public interface PriorityQueueNode {
    public static final int INDEX_NOT_IN_QUEUE = -1;

    public int priorityQueueIndex(DefaultPriorityQueue<?> var1);

    public void priorityQueueIndex(DefaultPriorityQueue<?> var1, int var2);
}

