/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TFloatCollection;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.procedure.TFloatProcedure;
import java.io.Serializable;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableFloatCollection
implements TFloatCollection,
Serializable {
    private static final long serialVersionUID = 1820017752578914078L;
    final TFloatCollection c;

    public TUnmodifiableFloatCollection(TFloatCollection c) {
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
    public boolean contains(float o) {
        return this.c.contains(o);
    }

    @Override
    public float[] toArray() {
        return this.c.toArray();
    }

    @Override
    public float[] toArray(float[] a) {
        return this.c.toArray(a);
    }

    public String toString() {
        return this.c.toString();
    }

    @Override
    public float getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TFloatProcedure procedure) {
        return this.c.forEach(procedure);
    }

    @Override
    public TFloatIterator iterator() {
        return new TFloatIterator(){
            TFloatIterator i;
            {
                this.i = TUnmodifiableFloatCollection.this.c.iterator();
            }

            public boolean hasNext() {
                return this.i.hasNext();
            }

            public float next() {
                return this.i.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean add(float e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(float o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> coll) {
        return this.c.containsAll(coll);
    }

    @Override
    public boolean containsAll(TFloatCollection coll) {
        return this.c.containsAll(coll);
    }

    @Override
    public boolean containsAll(float[] array) {
        return this.c.containsAll(array);
    }

    @Override
    public boolean addAll(TFloatCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Float> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(float[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(TFloatCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(float[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(TFloatCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(float[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

}

