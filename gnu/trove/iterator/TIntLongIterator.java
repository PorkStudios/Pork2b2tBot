/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TIntLongIterator
extends TAdvancingIterator {
    public int key();

    public long value();

    public long setValue(long var1);
}

