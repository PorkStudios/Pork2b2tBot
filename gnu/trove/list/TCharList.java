/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.list;

import gnu.trove.TCharCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.procedure.TCharProcedure;
import java.util.Random;

public interface TCharList
extends TCharCollection {
    public char getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean add(char var1);

    public void add(char[] var1);

    public void add(char[] var1, int var2, int var3);

    public void insert(int var1, char var2);

    public void insert(int var1, char[] var2);

    public void insert(int var1, char[] var2, int var3, int var4);

    public char get(int var1);

    public char set(int var1, char var2);

    public void set(int var1, char[] var2);

    public void set(int var1, char[] var2, int var3, int var4);

    public char replace(int var1, char var2);

    public void clear();

    public boolean remove(char var1);

    public char removeAt(int var1);

    public void remove(int var1, int var2);

    public void transformValues(TCharFunction var1);

    public void reverse();

    public void reverse(int var1, int var2);

    public void shuffle(Random var1);

    public TCharList subList(int var1, int var2);

    public char[] toArray();

    public char[] toArray(int var1, int var2);

    public char[] toArray(char[] var1);

    public char[] toArray(char[] var1, int var2, int var3);

    public char[] toArray(char[] var1, int var2, int var3, int var4);

    public boolean forEach(TCharProcedure var1);

    public boolean forEachDescending(TCharProcedure var1);

    public void sort();

    public void sort(int var1, int var2);

    public void fill(char var1);

    public void fill(int var1, int var2, char var3);

    public int binarySearch(char var1);

    public int binarySearch(char var1, int var2, int var3);

    public int indexOf(char var1);

    public int indexOf(int var1, char var2);

    public int lastIndexOf(char var1);

    public int lastIndexOf(int var1, char var2);

    public boolean contains(char var1);

    public TCharList grep(TCharProcedure var1);

    public TCharList inverseGrep(TCharProcedure var1);

    public char max();

    public char min();

    public char sum();
}

