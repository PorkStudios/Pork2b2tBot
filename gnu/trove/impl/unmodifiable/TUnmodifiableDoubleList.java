/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessDoubleList;
import gnu.trove.list.TDoubleList;
import gnu.trove.procedure.TDoubleProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableDoubleList
extends TUnmodifiableDoubleCollection
implements TDoubleList {
    static final long serialVersionUID = -283967356065247728L;
    final TDoubleList list;

    public TUnmodifiableDoubleList(TDoubleList list) {
        super(list);
        this.list = list;
    }

    public boolean equals(Object o) {
        return o == this || this.list.equals(o);
    }

    public int hashCode() {
        return this.list.hashCode();
    }

    public double get(int index) {
        return this.list.get(index);
    }

    public int indexOf(double o) {
        return this.list.indexOf(o);
    }

    public int lastIndexOf(double o) {
        return this.list.lastIndexOf(o);
    }

    public double[] toArray(int offset, int len) {
        return this.list.toArray(offset, len);
    }

    public double[] toArray(double[] dest, int offset, int len) {
        return this.list.toArray(dest, offset, len);
    }

    public double[] toArray(double[] dest, int source_pos, int dest_pos, int len) {
        return this.list.toArray(dest, source_pos, dest_pos, len);
    }

    public boolean forEachDescending(TDoubleProcedure procedure) {
        return this.list.forEachDescending(procedure);
    }

    public int binarySearch(double value) {
        return this.list.binarySearch(value);
    }

    public int binarySearch(double value, int fromIndex, int toIndex) {
        return this.list.binarySearch(value, fromIndex, toIndex);
    }

    public int indexOf(int offset, double value) {
        return this.list.indexOf(offset, value);
    }

    public int lastIndexOf(int offset, double value) {
        return this.list.lastIndexOf(offset, value);
    }

    public TDoubleList grep(TDoubleProcedure condition) {
        return this.list.grep(condition);
    }

    public TDoubleList inverseGrep(TDoubleProcedure condition) {
        return this.list.inverseGrep(condition);
    }

    public double max() {
        return this.list.max();
    }

    public double min() {
        return this.list.min();
    }

    public double sum() {
        return this.list.sum();
    }

    public TDoubleList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableDoubleList(this.list.subList(fromIndex, toIndex));
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessDoubleList(this.list) : this;
    }

    public void add(double[] vals) {
        throw new UnsupportedOperationException();
    }

    public void add(double[] vals, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    public double removeAt(int offset) {
        throw new UnsupportedOperationException();
    }

    public void remove(int offset, int length) {
        throw new UnsupportedOperationException();
    }

    public void insert(int offset, double value) {
        throw new UnsupportedOperationException();
    }

    public void insert(int offset, double[] values) {
        throw new UnsupportedOperationException();
    }

    public void insert(int offset, double[] values, int valOffset, int len) {
        throw new UnsupportedOperationException();
    }

    public double set(int offset, double val) {
        throw new UnsupportedOperationException();
    }

    public void set(int offset, double[] values) {
        throw new UnsupportedOperationException();
    }

    public void set(int offset, double[] values, int valOffset, int length) {
        throw new UnsupportedOperationException();
    }

    public double replace(int offset, double val) {
        throw new UnsupportedOperationException();
    }

    public void transformValues(TDoubleFunction function) {
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

    public void fill(double val) {
        throw new UnsupportedOperationException();
    }

    public void fill(int fromIndex, int toIndex, double val) {
        throw new UnsupportedOperationException();
    }
}

