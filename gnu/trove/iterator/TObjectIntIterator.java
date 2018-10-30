/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TObjectIntIterator<K>
extends TAdvancingIterator {
    public K key();

    public int value();

    public int setValue(int var1);
}

