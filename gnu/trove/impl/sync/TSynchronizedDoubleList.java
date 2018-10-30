/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.sync.TSynchronizedDoubleCollection;
import gnu.trove.impl.sync.TSynchronizedRandomAccessDoubleList;
import gnu.trove.list.TDoubleList;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedDoubleList
extends TSynchronizedDoubleCollection
implements TDoubleList {
    static final long serialVersionUID = -7754090372962971524L;
    final TDoubleList list;

    public TSynchronizedDoubleList(TDoubleList list) {
        super(list);
        this.list = list;
    }

    public TSynchronizedDoubleList(TDoubleList list, Object mutex) {
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
    public double get(int index) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.get(index);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public double set(int index, double element) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.set(index, element);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void set(int offset, double[] values) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.set(offset, values);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void set(int offset, double[] values, int valOffset, int length) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.set(offset, values, valOffset, length);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public double replace(int offset, double val) {
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
    public double removeAt(int offset) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.removeAt(offset);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(double[] vals) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.add(vals);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(double[] vals, int offset, int length) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.add(vals, offset, length);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(int offset, double value) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.insert(offset, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(int offset, double[] values) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.insert(offset, values);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(int offset, double[] values, int valOffset, int len) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.insert(offset, values, valOffset, len);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int indexOf(double o) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.indexOf(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int lastIndexOf(double o) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.lastIndexOf(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TDoubleList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        synchronized (object) {
            return new TSynchronizedDoubleList(this.list.subList(fromIndex, toIndex), this.mutex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public double[] toArray(int offset, int len) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.toArray(offset, len);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public double[] toArray(double[] dest, int offset, int len) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.toArray(dest, offset, len);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public double[] toArray(double[] dest, int source_pos, int dest_pos, int len) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.toArray(dest, source_pos, dest_pos, len);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int indexOf(int offset, double value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.indexOf(offset, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int lastIndexOf(int offset, double value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.lastIndexOf(offset, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fill(double val) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.fill(val);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fill(int fromIndex, int toIndex, double val) {
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
    public int binarySearch(double value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.binarySearch(value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int binarySearch(double value, int fromIndex, int toIndex) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.binarySearch(value, fromIndex, toIndex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TDoubleList grep(TDoubleProcedure condition) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.grep(condition);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TDoubleList inverseGrep(TDoubleProcedure condition) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.inverseGrep(condition);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public double max() {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.max();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public double min() {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.min();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public double sum() {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.sum();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean forEachDescending(TDoubleProcedure procedure) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.forEachDescending(procedure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void transformValues(TDoubleFunction function) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.transformValues(function);
        }
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TSynchronizedRandomAccessDoubleList(this.list) : this;
    }
}

