/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TIntDoubleIterator
extends TAdvancingIterator {
    public int key();

    public double value();

    public double setValue(double var1);
}

