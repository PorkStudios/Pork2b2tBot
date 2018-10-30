/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface ByteList
extends List<Byte>,
Comparable<List<? extends Byte>>,
ByteCollection {
    @Override
    public ByteListIterator iterator();

    public ByteListIterator listIterator();

    public ByteListIterator listIterator(int var1);

    public ByteList subList(int var1, int var2);

    public void size(int var1);

    public void getElements(int var1, byte[] var2, int var3, int var4);

    public void removeElements(int var1, int var2);

    public void addElements(int var1, byte[] var2);

    public void addElements(int var1, byte[] var2, int var3, int var4);

    @Override
    public boolean add(byte var1);

    @Override
    public void add(int var1, byte var2);

    @Deprecated
    @Override
    public void add(int var1, Byte var2);

    public boolean addAll(int var1, ByteCollection var2);

    public boolean addAll(int var1, ByteList var2);

    public boolean addAll(ByteList var1);

    @Override
    public byte set(int var1, byte var2);

    public byte getByte(int var1);

    public int indexOf(byte var1);

    public int lastIndexOf(byte var1);

    @Deprecated
    @Override
    public Byte get(int var1);

    @Deprecated
    @Override
    public int indexOf(Object var1);

    @Deprecated
    @Override
    public int lastIndexOf(Object var1);

    @Deprecated
    @Override
    public boolean add(Byte var1);

    public byte removeByte(int var1);

    @Deprecated
    @Override
    public Byte remove(int var1);

    @Deprecated
    @Override
    public Byte set(int var1, Byte var2);
}

