/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil;

import it.unimi.dsi.fastutil.BidirectionalIterator;

public interface BigListIterator<K>
extends BidirectionalIterator<K> {
    public long nextIndex();

    public long previousIndex();

    default public void set(K e) {
        throw new UnsupportedOperationException();
    }

    default public void add(K e) {
        throw new UnsupportedOperationException();
    }
}

