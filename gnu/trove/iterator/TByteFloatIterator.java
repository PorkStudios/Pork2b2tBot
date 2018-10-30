/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.iterator;

import gnu.trove.iterator.TAdvancingIterator;

public interface TByteFloatIterator
extends TAdvancingIterator {
    public byte key();

    public float value();

    public float setValue(float var1);
}

