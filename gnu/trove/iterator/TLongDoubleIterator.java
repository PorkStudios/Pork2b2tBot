/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TLongDoubleIterator
extends TAdvancingIterator {
    public long key();

    public double value();

    public double setValue(double var1);
}

