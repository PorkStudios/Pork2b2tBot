/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteIterable;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.util.Collection;
import java.util.Iterator;

public interface ByteCollection
extends Collection<Byte>,
ByteIterable {
    @Override
    public ByteIterator iterator();

    @Override
    public boolean add(byte var1);

    public boolean contains(byte var1);

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

    public byte[] toByteArray();

    @Deprecated
    public byte[] toByteArray(byte[] var1);

    public byte[] toArray(byte[] var1);

    public boolean addAll(ByteCollection var1);

    public boolean containsAll(ByteCollection var1);

    public boolean removeAll(ByteCollection var1);

    public boolean retainAll(ByteCollection var1);
}

