/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.sync.TSynchronizedIntCollection;
import gnu.trove.impl.sync.TSynchronizedRandomAccessIntList;
import gnu.trove.list.TIntList;
import gnu.trove.procedure.TIntProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedIntList
extends TSynchronizedIntCollection
implements TIntList {
    static final long serialVersionUID = -7754090372962971524L;
    final TIntList list;

    public TSynchronizedIntList(TIntList list) {
        super(list);
        this.list = list;
    }

    public TSynchronizedIntList(TIntList list, Object mutex) {
        super(list, mutex);
        this.list = list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean equals(Object o) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.equals(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int hashCode() {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.hashCode();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int get(int index) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.get(index);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int set(int index, int element) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.set(index, element);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void set(int offset, int[] values) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.set(offset, values);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void set(int offset, int[] values, int valOffset, int length) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.set(offset, values, valOffset, length);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int replace(int offset, int val) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.replace(offset, val);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void remove(int offset, int length) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.remove(offset, length);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int removeAt(int offset) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.removeAt(offset);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(int[] vals) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.add(vals);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(int[] vals, int offset, int length) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.add(vals, offset, length);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(int offset, int value) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.insert(offset, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(int offset, int[] values) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.insert(offset, values);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(int offset, int[] values, int valOffset, int len) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.insert(offset, values, valOffset, len);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int indexOf(int o) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.indexOf(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int lastIndexOf(int o) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.lastIndexOf(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TIntList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        synchronized (object) {
            return new TSynchronizedIntList(this.list.subList(fromIndex, toIndex), this.mutex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int[] toArray(int offset, int len) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.toArray(offset, len);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int[] toArray(int[] dest, int offset, int len) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.toArray(dest, offset, len);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int[] toArray(int[] dest, int source_pos, int dest_pos, int len) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.toArray(dest, source_pos, dest_pos, len);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int indexOf(int offset, int value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.indexOf(offset, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int lastIndexOf(int offset, int value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.lastIndexOf(offset, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fill(int val) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.fill(val);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fill(int fromIndex, int toIndex, int val) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.fill(fromIndex, toIndex, val);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reverse() {
        Object object = this.mutex;
        synchronized (object) {
            this.list.reverse();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reverse(int from, int to) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.reverse(from, to);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void shuffle(Random rand) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.shuffle(rand);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sort() {
        Object object = this.mutex;
        synchronized (object) {
            this.list.sort();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sort(int fromIndex, int toIndex) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.sort(fromIndex, toIndex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int binarySearch(int value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.binarySearch(value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int binarySearch(int value, int fromIndex, int toIndex) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.binarySearch(value, fromIndex, toIndex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TIntList grep(TIntProcedure condition) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.grep(condition);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TIntList inverseGrep(TIntProcedure condition) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.inverseGrep(condition);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int max() {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.max();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int min() {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.min();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int sum() {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.sum();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean forEachDescending(TIntProcedure procedure) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.forEachDescending(procedure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void transformValues(TIntFunction function) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.transformValues(function);
        }
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TSynchronizedRandomAccessIntList(this.list) : this;
    }
}

