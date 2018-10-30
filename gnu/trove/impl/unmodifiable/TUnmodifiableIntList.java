/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessIntList;
import gnu.trove.list.TIntList;
import gnu.trove.procedure.TIntProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableIntList
extends TUnmodifiableIntCollection
implements TIntList {
    static final long serialVersionUID = -283967356065247728L;
    final TIntList list;

    public TUnmodifiableIntList(TIntList list) {
        super(list);
        this.list = list;
    }

    public boolean equals(Object o) {
        return o == this || this.list.equals(o);
    }

    public int hashCode() {
        return this.list.hashCode();
    }

    public int get(int index) {
        return this.list.get(index);
    }

    public int indexOf(int o) {
        return this.list.indexOf(o);
    }

    public int lastIndexOf(int o) {
        return this.list.lastIndexOf(o);
    }

    public int[] toArray(int offset, int len) {
        return this.list.toArray(offset, len);
    }

    public int[] toArray(int[] dest, int offset, int len) {
        return this.list.toArray(dest, offset, len);
    }

    public int[] toArray(int[] dest, int source_pos, int dest_pos, int len) {
        return this.list.toArray(dest, source_pos, dest_pos, len);
    }

    public boolean forEachDescending(TIntProcedure procedure) {
        return this.list.forEachDescending(procedure);
    }

    public int binarySearch(int value) {
        return this.list.binarySearch(value);
    }

    public int binarySearch(int value, int fromIndex, int toIndex) {
        return this.list.binarySearch(value, fromIndex, toIndex);
    }

    public int indexOf(int offset, int value) {
        return this.list.indexOf(offset, value);
    }

    public int lastIndexOf(int offset, int value) {
        return this.list.lastIndexOf(offset, value);
    }

    public TIntList grep(TIntProcedure condition) {
        return this.list.grep(condition);
    }

    public TIntList inverseGrep(TIntProcedure condition) {
        return this.list.inverseGrep(condition);
    }

    public int max() {
        return this.list.max();
    }

    public int min() {
        return this.list.min();
    }

    public int sum() {
        return this.list.sum();
    }

    public TIntList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableIntList(this.list.subList(fromIndex, toIndex));
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessIntList(this.list) : this;
    }

    public void add(int[] vals) {
        throw new UnsupportedOperationException();
    }

    public void add(int[] vals, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    public int removeAt(int offset) {
        throw new UnsupportedOperationException();
    }

    public void remove(int offset, int length) {
        throw new UnsupportedOperationException();
    }

    public void insert(int offset, int value) {
        throw new UnsupportedOperationException();
    }

    public void insert(int offset, int[] values) {
        throw new UnsupportedOperationException();
    }

    public void insert(int offset, int[] values, int valOffset, int len) {
        throw new UnsupportedOperationException();
    }

    public int set(int offset, int val) {
        throw new UnsupportedOperationException();
    }

    public void set(int offset, int[] values) {
        throw new UnsupportedOperationException();
    }

    public void set(int offset, int[] values, int valOffset, int length) {
        throw new UnsupportedOperationException();
    }

    public int replace(int offset, int val) {
        throw new UnsupportedOperationException();
    }

    public void transformValues(TIntFunction function) {
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

    public void fill(int val) {
        throw new UnsupportedOperationException();
    }

    public void fill(int fromIndex, int toIndex, int val) {
        throw new UnsupportedOperationException();
    }
}

