/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TByteCollection;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.procedure.TByteProcedure;
import java.io.Serializable;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableByteCollection
implements TByteCollection,
Serializable {
    private static final long serialVersionUID = 1820017752578914078L;
    final TByteCollection c;

    public TUnmodifiableByteCollection(TByteCollection c) {
        if (c == null) {
            throw new NullPointerException();
        }
        this.c = c;
    }

    @Override
    public int size() {
        return this.c.size();
    }

    @Override
    public boolean isEmpty() {
        return this.c.isEmpty();
    }

    @Override
    public boolean contains(byte o) {
        return this.c.contains(o);
    }

    @Override
    public byte[] toArray() {
        return this.c.toArray();
    }

    @Override
    public byte[] toArray(byte[] a) {
        return this.c.toArray(a);
    }

    public String toString() {
        return this.c.toString();
    }

    @Override
    public byte getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TByteProcedure procedure) {
        return this.c.forEach(procedure);
    }

    @Override
    public TByteIterator iterator() {
        return new TByteIterator(){
            TByteIterator i;
            {
                this.i = TUnmodifiableByteCollection.this.c.iterator();
            }

            public boolean hasNext() {
                return this.i.hasNext();
            }

            public byte next() {
                return this.i.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean add(byte e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(byte o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> coll) {
        return this.c.containsAll(coll);
    }

    @Override
    public boolean containsAll(TByteCollection coll) {
        return this.c.containsAll(coll);
    }

    @Override
    public boolean containsAll(byte[] array) {
        return this.c.containsAll(array);
    }

    @Override
    public boolean addAll(TByteCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Byte> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(byte[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(TByteCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(byte[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(TByteCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(byte[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

}

