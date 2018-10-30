/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.unmodifiable;

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;
import java.io.Serializable;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TUnmodifiableIntCollection
implements TIntCollection,
Serializable {
    private static final long serialVersionUID = 1820017752578914078L;
    final TIntCollection c;

    public TUnmodifiableIntCollection(TIntCollection c) {
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
    public boolean contains(int o) {
        return this.c.contains(o);
    }

    @Override
    public int[] toArray() {
        return this.c.toArray();
    }

    @Override
    public int[] toArray(int[] a) {
        return this.c.toArray(a);
    }

    public String toString() {
        return this.c.toString();
    }

    @Override
    public int getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    @Override
    public boolean forEach(TIntProcedure procedure) {
        return this.c.forEach(procedure);
    }

    @Override
    public TIntIterator iterator() {
        return new TIntIterator(){
            TIntIterator i;
            {
                this.i = TUnmodifiableIntCollection.this.c.iterator();
            }

            public boolean hasNext() {
                return this.i.hasNext();
            }

            public int next() {
                return this.i.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean add(int e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(int o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> coll) {
        return this.c.containsAll(coll);
    }

    @Override
    public boolean containsAll(TIntCollection coll) {
        return this.c.containsAll(coll);
    }

    @Override
    public boolean containsAll(int[] array) {
        return this.c.containsAll(array);
    }

    @Override
    public boolean addAll(TIntCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Integer> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(TIntCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(int[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(TIntCollection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(int[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

}

