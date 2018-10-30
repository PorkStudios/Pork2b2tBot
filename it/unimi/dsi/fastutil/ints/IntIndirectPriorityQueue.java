/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.IndirectPriorityQueue;
import it.unimi.dsi.fastutil.ints.IntComparator;
import java.util.Comparator;

public interface IntIndirectPriorityQueue
extends IndirectPriorityQueue<Integer> {
    public IntComparator comparator();
}

