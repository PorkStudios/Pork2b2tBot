/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessByteList;
import gnu.trove.list.TByteList;
import gnu.trove.procedure.TByteProcedure;
import java.util.Random;
import java.util.RandomAccess;

public class TUnmodifiableByteList
extends TUnmodifiableByteCollection
implements TByteList {
    static final long serialVersionUID = -283967356065247728L;
    final TByteList list;

    public TUnmodifiableByteList(TByteList list) {
        super(list);
        this.list = list;
    }

    public boolean equals(Object o) {
        return o == this || this.list.equals(o);
    }

    public int hashCode() {
        return this.list.hashCode();
    }

    public byte get(int index) {
        return this.list.get(index);
    }

    public int indexOf(byte o) {
        return this.list.indexOf(o);
    }

    public int lastIndexOf(byte o) {
        return this.list.lastIndexOf(o);
    }

    public byte[] toArray(int offset, int len) {
        return this.list.toArray(offset, len);
    }

    public byte[] toArray(byte[] dest, int offset, int len) {
        return this.list.toArray(dest, offset, len);
    }

    public byte[] toArray(byte[] dest, int source_pos, int dest_pos, int len) {
        return this.list.toArray(dest, source_pos, dest_pos, len);
    }

    public boolean forEachDescending(TByteProcedure procedure) {
        return this.list.forEachDescending(procedure);
    }

    public int binarySearch(byte value) {
        return this.list.binarySearch(value);
    }

    public int binarySearch(byte value, int fromIndex, int toIndex) {
        return this.list.binarySearch(value, fromIndex, toIndex);
    }

    public int indexOf(int offset, byte value) {
        return this.list.indexOf(offset, value);
    }

    public int lastIndexOf(int offset, byte value) {
        return this.list.lastIndexOf(offset, value);
    }

    public TByteList grep(TByteProcedure condition) {
        return this.list.grep(condition);
    }

    public TByteList inverseGrep(TByteProcedure condition) {
        return this.list.inverseGrep(condition);
    }

    public byte max() {
        return this.list.max();
    }

    public byte min() {
        return this.list.min();
    }

    public byte sum() {
        return this.list.sum();
    }

    public TByteList subList(int fromIndex, int toIndex) {
        return new TUnmodifiableByteList(this.list.subList(fromIndex, toIndex));
    }

    private Object readResolve() {
        return this.list instanceof RandomAccess ? new TUnmodifiableRandomAccessByteList(this.list) : this;
    }

    public void add(byte[] vals) {
        throw new UnsupportedOperationException();
    }

    public void add(byte[] vals, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    public byte removeAt(int offset) {
        throw new UnsupportedOperationException();
    }

    public void remove(int offset, int length) {
        throw new UnsupportedOperationException();
    }

    public void insert(int offset, byte value) {
        throw new UnsupportedOperationException();
    }

    public void insert(int offset, byte[] values) {
        throw new UnsupportedOperationException();
    }

    public void insert(int offset, byte[] values, int valOffset, int len) {
        throw new UnsupportedOperationException();
    }

    public byte set(int offset, byte val) {
        throw new UnsupportedOperationException();
    }

    public void set(int offset, byte[] values) {
        throw new UnsupportedOperationException();
    }

    public void set(int offset, byte[] values, int valOffset, int length) {
        throw new UnsupportedOperationException();
    }

    public byte replace(int offset, byte val) {
        throw new UnsupportedOperationException();
    }

    public void transformValues(TByteFunction function) {
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

    public void fill(byte val) {
        throw new UnsupportedOperationException();
    }

    public void fill(int fromIndex, int toIndex, byte val) {
        throw new UnsupportedOperationException();
    }
}

