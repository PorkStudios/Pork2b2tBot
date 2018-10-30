/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.sync.TSynchronizedByteCollection;
import gnu.trove.impl.sync.TSynchronizedRandomAccessByteList;
import gnu.trove.list.TByteList;
import gnu.trove.procedure.TByteProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TSynchronizedByteList
extends TSynchronizedByteCollection
implements TByteList {
    static final long serialVersionUID = -7754090372962971524L;
    final TByteList list;

    public TSynchronizedByteList(TByteList list) {
        super(list);
        this.list = list;
    }

    public TSynchronizedByteList(TByteList list, Object mutex) {
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
    public byte get(int index) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.get(index);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte set(int index, byte element) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.set(index, element);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void set(int offset, byte[] values) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.set(offset, values);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void set(int offset, byte[] values, int valOffset, int length) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.set(offset, values, valOffset, length);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte replace(int offset, byte val) {
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
    public byte removeAt(int offset) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.removeAt(offset);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(byte[] vals) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.add(vals);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(byte[] vals, int offset, int length) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.add(vals, offset, length);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(int offset, byte value) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.insert(offset, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(int offset, byte[] values) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.insert(offset, values);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(int offset, byte[] values, int valOffset, int len) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.insert(offset, values, valOffset, len);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int indexOf(byte o) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.indexOf(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int lastIndexOf(byte o) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.lastIndexOf(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TByteList subList(int fromIndex, int toIndex) {
        Object object = this.mutex;
        synchronized (object) {
            return new TSynchronizedByteList(this.list.subList(fromIndex, toIndex), this.mutex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] toArray(int offset, int len) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.toArray(offset, len);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] toArray(byte[] dest, int offset, int len) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.toArray(dest, offset, len);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] toArray(byte[] dest, int source_pos, int dest_pos, int len) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.toArray(dest, source_pos, dest_pos, len);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int indexOf(int offset, byte value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.indexOf(offset, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int lastIndexOf(int offset, byte value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.lastIndexOf(offset, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fill(byte val) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.fill(val);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fill(int fromIndex, int toIndex, byte val) {
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
    public int binarySearch(byte value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.binarySearch(value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int binarySearch(byte value, int fromIndex, int toIndex) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.binarySearch(value, fromIndex, toIndex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TByteList grep(TByteProcedure condition) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.grep(condition);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TByteList inverseGrep(TByteProcedure condition) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.inverseGrep(condition);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte max() {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.max();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte min() {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.min();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte sum() {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.sum();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean forEachDescending(TByteProcedure procedure) {
        Object object = this.mutex;
        synchronized (object) {
            return this.list.forEachDescending(procedure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void transformValues(TByteFunction function) {
        Object object = this.mutex;
        synchronized (object) {
            this.list.transformValues(function);
        }
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TSynchronizedRandomAccessByteList(this.list) : this;
    }
}

