/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.list;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.procedure.TFloatProcedure;
import java.util.Random;

public interface TFloatList
extends TFloatCollection {
    public float getNoEntryValue();

    public int size();

    public boolean isEmpty();

    public boolean add(float var1);

    public void add(float[] var1);

    public void add(float[] var1, int var2, int var3);

    public void insert(int var1, float var2);

    public void insert(int var1, float[] var2);

    public void insert(int var1, float[] var2, int var3, int var4);

    public float get(int var1);

    public float set(int var1, float var2);

    public void set(int var1, float[] var2);

    public void set(int var1, float[] var2, int var3, int var4);

    public float replace(int var1, float var2);

    public void clear();

    public boolean remove(float var1);

    public float removeAt(int var1);

    public void remove(int var1, int var2);

    public void transformValues(TFloatFunction var1);

    public void reverse();

    public void reverse(int var1, int var2);

    public void shuffle(Random var1);

    public TFloatList subList(int var1, int var2);

    public float[] toArray();

    public float[] toArray(int var1, int var2);

    public float[] toArray(float[] var1);

    public float[] toArray(float[] var1, int var2, int var3);

    public float[] toArray(float[] var1, int var2, int var3, int var4);

    public boolean forEach(TFloatProcedure var1);

    public boolean forEachDescending(TFloatProcedure var1);

    public void sort();

    public void sort(int var1, int var2);

    public void fill(float var1);

    public void fill(int var1, int var2, float var3);

    public int binarySearch(float var1);

    public int binarySearch(float var1, int var2, int var3);

    public int indexOf(float var1);

    public int indexOf(int var1, float var2);

    public int lastIndexOf(float var1);

    public int lastIndexOf(int var1, float var2);

    public boolean contains(float var1);

    public TFloatList grep(TFloatProcedure var1);

    public TFloatList inverseGrep(TFloatProcedure var1);

    public float max();

    public float min();

    public float sum();
}

