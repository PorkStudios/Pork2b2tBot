/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.procedure.TDoubleProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TSynchronizedDoubleCollection
implements TDoubleCollection,
Serializable {
    private static final long serialVersionUID = 3053995032091335093L;
    final TDoubleCollection c;
    final Object mutex;

    public TSynchronizedDoubleCollection(TDoubleCollection c) {
        if (c == null) {
            throw new NullPointerException();
        }
        this.c = c;
        this.mutex = this;
    }

    public TSynchronizedDoubleCollection(TDoubleCollection c, Object mutex) {
        this.c = c;
        this.mutex = mutex;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int size() {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isEmpty() {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.isEmpty();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean contains(double o) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.contains(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double[] toArray() {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.toArray();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double[] toArray(double[] a) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.toArray(a);
        }
    }

    @Override
    public TDoubleIterator iterator() {
        return this.c.iterator();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean add(double e) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.add(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean remove(double o) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.remove(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsAll(Collection<?> coll) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.containsAll(coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsAll(TDoubleCollection coll) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.containsAll(coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsAll(double[] array) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.containsAll(array);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean addAll(Collection<? extends Double> coll) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.addAll(coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean addAll(TDoubleCollection coll) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.addAll(coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean addAll(double[] array) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.addAll(array);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean removeAll(Collection<?> coll) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.removeAll(coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean removeAll(TDoubleCollection coll) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.removeAll(coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean removeAll(double[] array) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.removeAll(array);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainAll(Collection<?> coll) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.retainAll(coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainAll(TDoubleCollection coll) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.retainAll(coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainAll(double[] array) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.retainAll(array);
        }
    }

    @Override
    public double getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEach(TDoubleProcedure procedure) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.forEach(procedure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        Object object = this.mutex;
        synchronized (object) {
            this.c.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.toString();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        Object object = this.mutex;
        synchronized (object) {
            s.defaultWriteObject();
        }
    }
}

