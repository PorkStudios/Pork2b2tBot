/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.list;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Random;

public interface TDoubleList
extends TDoubleCollection {
    public double getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean add(double var1);

    public void add(double[] var1);

    public void add(double[] var1, int var2, int var3);

    public void insert(int var1, double var2);

    public void insert(int var1, double[] var2);

    public void insert(int var1, double[] var2, int var3, int var4);

    public double get(int var1);

    public double set(int var1, double var2);

    public void set(int var1, double[] var2);

    public void set(int var1, double[] var2, int var3, int var4);

    public double replace(int var1, double var2);

    public void clear();

    public boolean remove(double var1);

    public double removeAt(int var1);

    public void remove(int var1, int var2);

    public void transformValues(TDoubleFunction var1);

    public void reverse();

    public void reverse(int var1, int var2);

    public void shuffle(Random var1);

    public TDoubleList subList(int var1, int var2);

    public double[] toArray();

    public double[] toArray(int var1, int var2);

    public double[] toArray(double[] var1);

    public double[] toArray(double[] var1, int var2, int var3);

    public double[] toArray(double[] var1, int var2, int var3, int var4);

    public boolean forEach(TDoubleProcedure var1);

    public boolean forEachDescending(TDoubleProcedure var1);

    public void sort();

    public void sort(int var1, int var2);

    public void fill(double var1);

    public void fill(int var1, int var2, double var3);

    public int binarySearch(double var1);

    public int binarySearch(double var1, int var3, int var4);

    public int indexOf(double var1);

    public int indexOf(int var1, double var2);

    public int lastIndexOf(double var1);

    public int lastIndexOf(int var1, double var2);

    public boolean contains(double var1);

    public TDoubleList grep(TDoubleProcedure var1);

    public TDoubleList inverseGrep(TDoubleProcedure var1);

    public double max();

    public double min();

    public double sum();
}

