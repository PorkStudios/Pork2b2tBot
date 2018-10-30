/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TShortByteIterator
extends TAdvancingIterator {
    public short key();

    public byte value();

    public byte setValue(byte var1);
}

