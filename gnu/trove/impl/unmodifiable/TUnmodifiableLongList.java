/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessLongList;
import gnu.trove.list.TLongList;
import gnu.trove.procedure.TLongProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableLongList
extends TUnmodifiableLongCollection
implements TLongList {
    static final long serialVersionUID = -283967356065247728L;
    final TLongList list;

    public TUnmodifiableLongList(TLongList list) {
        super(list);
        this.list = list;
    }

    public boolean equals(Object o) {
        return o == this || this.list.equals(o);
    }

    public int hashCode() {
        return this.list.hashCode();
    }

    public long get(int index) {
        return this.list.get(index);
    }

    public int indexOf(long o) {
        return this.list.indexOf(o);
    }

    public int lastIndexOf(long o) {
        return this.list.lastIndexOf(o);
    }

    public long[] toArray(int offset, int len) {
        return this.list.toArray(offset, len);
    }

    public long[] toArray(long[] dest, int offset, int len) {
        return this.list.toArray(dest, offset, len);
    }

    public long[] toArray(long[] dest, int source_pos, int dest_pos, int len) {
        return this.list.toArray(dest, source_pos, dest_pos, len);
    }

    public boolean forEachDescending(TLongProcedure procedure) {
        return this.list.forEachDescending(procedure);
    }

    public int binarySearch(long value) {
        return this.list.binarySearch(value);
    }

    public int binarySearch(long value, int fromIndex, int toIndex) {
        return this.list.binarySearch(value, fromIndex, toIndex);
    }

    public int indexOf(int offset, long value) {
        return this.list.indexOf(offset, value);
    }

    public int lastIndexOf(int offset, long value) {
        return this.list.lastIndexOf(offset, value);
    }

    public TLongList grep(TLongProcedure condition) {
        return this.list.grep(condition);
    }

    public TLongList inverseGrep(TLongProcedure condition) {
        return this.list.inverseGrep(condition);
    }

    public long max() {
        return this.list.max();
    }

    public long min() {
        return this.list.min();
    }

    public long sum() {
        return this.list.sum();
    }

    public TLongList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableLongList(this.list.subList(fromIndex, toIndex));
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessLongList(this.list) : this;
    }

    public void add(long[] vals) {
        throw new UnsupportedOperationException();
    }

    public void add(long[] vals, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    public long removeAt(int offset) {
        throw new UnsupportedOperationException();
    }

    public void remove(int offset, int length) {
        throw new UnsupportedOperationException();
    }

    public void insert(int offset, long value) {
        throw new UnsupportedOperationException();
    }

    public void insert(int offset, long[] values) {
        throw new UnsupportedOperationException();
    }

    public void insert(int offset, long[] values, int valOffset, int len) {
        throw new UnsupportedOperationException();
    }

    public long set(int offset, long val) {
        throw new UnsupportedOperationException();
    }

    public void set(int offset, long[] values) {
        throw new UnsupportedOperationException();
    }

    public void set(int offset, long[] values, int valOffset, int length) {
        throw new UnsupportedOperationException();
    }

    public long replace(int offset, long val) {
        throw new UnsupportedOperationException();
    }

    public void transformValues(TLongFunction function) {
        throw new UnsupportedOperationException();
    }

    public void reverse() {
        throw new UnsupportedOperationException();
    }

    public void reverse(int from, int to) {
        throw new UnsupportedOperationException();
    }

    public void shuffle(Random rand) {
        throw new UnsupportedOperationException();
    }

    public void sort() {
        throw new UnsupportedOperationException();
    }

    public void sort(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    public void fill(long val) {
        throw new UnsupportedOperationException();
    }

    public void fill(int fromIndex, int toIndex, long val) {
        throw new UnsupportedOperationException();
    }
}

