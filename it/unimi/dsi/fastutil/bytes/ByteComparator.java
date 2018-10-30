/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import java.util.Comparator;

@FunctionalInterface
public interface ByteComparator
extends Comparator<Byte> {
    @Override
    public int compare(byte var1, byte var2);

    @Deprecated
    @Override
    default public int compare(Byte ok1, Byte ok2) {
        return this.compare((byte)ok1, (byte)ok2);
    }
}

