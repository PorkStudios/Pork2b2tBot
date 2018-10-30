/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.list;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.procedure.TLongProcedure;
import java.util.Random;

public interface TLongList
extends TLongCollection {
    public long getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean add(long var1);

    public void add(long[] var1);

    public void add(long[] var1, int var2, int var3);

    public void insert(int var1, long var2);

    public void insert(int var1, long[] var2);

    public void insert(int var1, long[] var2, int var3, int var4);

    public long get(int var1);

    public long set(int var1, long var2);

    public void set(int var1, long[] var2);

    public void set(int var1, long[] var2, int var3, int var4);

    public long replace(int var1, long var2);

    public void clear();

    public boolean remove(long var1);

    public long removeAt(int var1);

    public void remove(int var1, int var2);

    public void transformValues(TLongFunction var1);

    public void reverse();

    public void reverse(int var1, int var2);

    public void shuffle(Random var1);

    public TLongList subList(int var1, int var2);

    public long[] toArray();

    public long[] toArray(int var1, int var2);

    public long[] toArray(long[] var1);

    public long[] toArray(long[] var1, int var2, int var3);

    public long[] toArray(long[] var1, int var2, int var3, int var4);

    public boolean forEach(TLongProcedure var1);

    public boolean forEachDescending(TLongProcedure var1);

    public void sort();

    public void sort(int var1, int var2);

    public void fill(long var1);

    public void fill(int var1, int var2, long var3);

    public int binarySearch(long var1);

    public int binarySearch(long var1, int var3, int var4);

    public int indexOf(long var1);

    public int indexOf(int var1, long var2);

    public int lastIndexOf(long var1);

    public int lastIndexOf(int var1, long var2);

    public boolean contains(long var1);

    public TLongList grep(TLongProcedure var1);

    public TLongList inverseGrep(TLongProcedure var1);

    public long max();

    public long min();

    public long sum();
}

