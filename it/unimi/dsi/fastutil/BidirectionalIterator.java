/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil;

import java.util.Iterator;

public interface BidirectionalIterator<K>
extends Iterator<K> {
    public K previous();

    public boolean hasPrevious();
}

