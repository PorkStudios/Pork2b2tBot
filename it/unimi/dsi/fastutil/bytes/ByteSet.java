/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.util.Iterator;
import java.util.Set;

public interface ByteSet
extends ByteCollection,
Set<Byte> {
    @Override
    public ByteIterator iterator();

    public boolean remove(byte var1);

    @Deprecated
    @Override
    public boolean rem(byte var1);

    @Deprecated
    @Override
    public boolean add(Byte var1);

    @Deprecated
    @Override
    public boolean contains(Object var1);

    @Deprecated
    @Override
    public boolean remove(Object var1);
}

