/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.list;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.procedure.TByteProcedure;
import java.util.Random;

public interface TByteList
extends TByteCollection {
    public byte getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean add(byte var1);

    public void add(byte[] var1);

    public void add(byte[] var1, int var2, int var3);

    public void insert(int var1, byte var2);

    public void insert(int var1, byte[] var2);

    public void insert(int var1, byte[] var2, int var3, int var4);

    public byte get(int var1);

    public byte set(int var1, byte var2);

    public void set(int var1, byte[] var2);

    public void set(int var1, byte[] var2, int var3, int var4);

    public byte replace(int var1, byte var2);

    public void clear();

    public boolean remove(byte var1);

    public byte removeAt(int var1);

    public void remove(int var1, int var2);

    public void transformValues(TByteFunction var1);

    public void reverse();

    public void reverse(int var1, int var2);

    public void shuffle(Random var1);

    public TByteList subList(int var1, int var2);

    public byte[] toArray();

    public byte[] toArray(int var1, int var2);

    public byte[] toArray(byte[] var1);

    public byte[] toArray(byte[] var1, int var2, int var3);

    public byte[] toArray(byte[] var1, int var2, int var3, int var4);

    public boolean forEach(TByteProcedure var1);

    public boolean forEachDescending(TByteProcedure var1);

    public void sort();

    public void sort(int var1, int var2);

    public void fill(byte var1);

    public void fill(int var1, int var2, byte var3);

    public int binarySearch(byte var1);

    public int binarySearch(byte var1, int var2, int var3);

    public int indexOf(byte var1);

    public int indexOf(int var1, byte var2);

    public int lastIndexOf(byte var1);

    public int lastIndexOf(int var1, byte var2);

    public boolean contains(byte var1);

    public TByteList grep(TByteProcedure var1);

    public TByteList inverseGrep(TByteProcedure var1);

    public byte max();

    public byte min();

    public byte sum();
}

