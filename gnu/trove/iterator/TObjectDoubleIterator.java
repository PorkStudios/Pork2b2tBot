/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TObjectDoubleIterator<K>
extends TAdvancingIterator {
    public K key();

    public double value();

    public double setValue(double var1);
}

