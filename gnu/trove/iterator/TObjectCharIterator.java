/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TObjectCharIterator<K>
extends TAdvancingIterator {
    public K key();

    public char value();

    public char setValue(char var1);
}

