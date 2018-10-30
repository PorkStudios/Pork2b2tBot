/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.list;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.procedure.TShortProcedure;
import java.util.Random;

public interface TShortList
extends TShortCollection {
    public short getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean add(short var1);

    public void add(short[] var1);

    public void add(short[] var1, int var2, int var3);

    public void insert(int var1, short var2);

    public void insert(int var1, short[] var2);

    public void insert(int var1, short[] var2, int var3, int var4);

    public short get(int var1);

    public short set(int var1, short var2);

    public void set(int var1, short[] var2);

    public void set(int var1, short[] var2, int var3, int var4);

    public short replace(int var1, short var2);

    public void clear();

    public boolean remove(short var1);

    public short removeAt(int var1);

    public void remove(int var1, int var2);

    public void transformValues(TShortFunction var1);

    public void reverse();

    public void reverse(int var1, int var2);

    public void shuffle(Random var1);

    public TShortList subList(int var1, int var2);

    public short[] toArray();

    public short[] toArray(int var1, int var2);

    public short[] toArray(short[] var1);

    public short[] toArray(short[] var1, int var2, int var3);

    public short[] toArray(short[] var1, int var2, int var3, int var4);

    public boolean forEach(TShortProcedure var1);

    public boolean forEachDescending(TShortProcedure var1);

    public void sort();

    public void sort(int var1, int var2);

    public void fill(short var1);

    public void fill(int var1, int var2, short var3);

    public int binarySearch(short var1);

    public int binarySearch(short var1, int var2, int var3);

    public int indexOf(short var1);

    public int indexOf(int var1, short var2);

    public int lastIndexOf(short var1);

    public int lastIndexOf(int var1, short var2);

    public boolean contains(short var1);

    public TShortList grep(TShortProcedure var1);

    public TShortList inverseGrep(TShortProcedure var1);

    public short max();

    public short min();

    public short sum();
}

