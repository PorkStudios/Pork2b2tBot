/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TCharIntIterator
extends TAdvancingIterator {
    public char key();

    public int value();

    public int setValue(int var1);
}

