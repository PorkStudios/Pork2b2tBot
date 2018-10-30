/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.IndirectPriorityQueue;
import it.unimi.dsi.fastutil.shorts.ShortComparator;
import java.util.Comparator;

public interface ShortIndirectPriorityQueue
extends IndirectPriorityQueue<Short> {
    public ShortComparator comparator();
}

