/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.IndirectPriorityQueue;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import java.util.Comparator;

public interface DoubleIndirectPriorityQueue
extends IndirectPriorityQueue<Double> {
    public DoubleComparator comparator();
}

