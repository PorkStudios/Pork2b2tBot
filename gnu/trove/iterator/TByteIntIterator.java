/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TByteIntIterator
extends TAdvancingIterator {
    public byte key();

    public int value();

    public int setValue(int var1);
}

