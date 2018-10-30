/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessFloatList;
import gnu.trove.list.TFloatList;
import gnu.trove.procedure.TFloatProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableFloatList
extends TUnmodifiableFloatCollection
implements TFloatList {
    static final long serialVersionUID = -283967356065247728L;
    final TFloatList list;

    public TUnmodifiableFloatList(TFloatList list) {
        super(list);
        this.list = list;
    }

    public boolean equals(Object o) {
        return o == this || this.list.equals(o);
    }

    public int hashCode() {
        return this.list.hashCode();
    }

    public float get(int index) {
        return this.list.get(index);
    }

    public int indexOf(float o) {
        return this.list.indexOf(o);
    }

    public int lastIndexOf(float o) {
        return this.list.lastIndexOf(o);
    }

    public float[] toArray(int offset, int len) {
        return this.list.toArray(offset, len);
    }

    public float[] toArray(float[] dest, int offset, int len) {
        return this.list.toArray(dest, offset, len);
    }

    public float[] toArray(float[] dest, int source_pos, int dest_pos, int len) {
        return this.list.toArray(dest, source_pos, dest_pos, len);
    }

    public boolean forEachDescending(TFloatProcedure procedure) {
        return this.list.forEachDescending(procedure);
    }

    public int binarySearch(float value) {
        return this.list.binarySearch(value);
    }

    public int binarySearch(float value, int fromIndex, int toIndex) {
        return this.list.binarySearch(value, fromIndex, toIndex);
    }

    public int indexOf(int offset, float value) {
        return this.list.indexOf(offset, value);
    }

    public int lastIndexOf(int offset, float value) {
        return this.list.lastIndexOf(offset, value);
    }

    public TFloatList grep(TFloatProcedure condition) {
        return this.list.grep(condition);
    }

    public TFloatList inverseGrep(TFloatProcedure condition) {
        return this.list.inverseGrep(condition);
    }

    public float max() {
        return this.list.max();
    }

    public float min() {
        return this.list.min();
    }

    public float sum() {
        return this.list.sum();
    }

    public TFloatList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableFloatList(this.list.subList(fromIndex, toIndex));
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessFloatList(this.list) : this;
    }

    public void add(float[] vals) {
        throw new UnsupportedOperationException();
    }

    public void add(float[] vals, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    public float removeAt(int offset) {
        throw new UnsupportedOperationException();
    }

    public void remove(int offset, int length) {
        throw new UnsupportedOperationException();
    }

    public void insert(int offset, float value) {
        throw new UnsupportedOperationException();
    }

    public void insert(int offset, float[] values) {
        throw new UnsupportedOperationException();
    }

    public void insert(int offset, float[] values, int valOffset, int len) {
        throw new UnsupportedOperationException();
    }

    public float set(int offset, float val) {
        throw new UnsupportedOperationException();
    }

    public void set(int offset, float[] values) {
        throw new UnsupportedOperationException();
    }

    public void set(int offset, float[] values, int valOffset, int length) {
        throw new UnsupportedOperationException();
    }

    public float replace(int offset, float val) {
        throw new UnsupportedOperationException();
    }

    public void transformValues(TFloatFunction function) {
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

    public void fill(float val) {
        throw new UnsupportedOperationException();
    }

    public void fill(int fromIndex, int toIndex, float val) {
        throw new UnsupportedOperationException();
    }
}

