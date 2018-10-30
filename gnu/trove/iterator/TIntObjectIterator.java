/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TIntObjectIterator<V>
extends TAdvancingIterator {
    public int key();

    public V value();

    public V setValue(V var1);
}

