/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TLongObjectIterator<V>
extends TAdvancingIterator {
    public long key();

    public V value();

    public V setValue(V var1);
}

