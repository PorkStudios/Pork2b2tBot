/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.IndirectPriorityQueue;
import it.unimi.dsi.fastutil.longs.LongComparator;
import java.util.Comparator;

public interface LongIndirectPriorityQueue
extends IndirectPriorityQueue<Long> {
    public LongComparator comparator();
}

