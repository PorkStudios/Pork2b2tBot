/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.IndirectPriorityQueue;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import java.util.Comparator;

public interface ByteIndirectPriorityQueue
extends IndirectPriorityQueue<Byte> {
    public ByteComparator comparator();
}

