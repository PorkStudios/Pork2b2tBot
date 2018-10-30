/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.IndirectPriorityQueue;
import it.unimi.dsi.fastutil.floats.FloatComparator;
import java.util.Comparator;

public interface FloatIndirectPriorityQueue
extends IndirectPriorityQueue<Float> {
    public FloatComparator comparator();
}

