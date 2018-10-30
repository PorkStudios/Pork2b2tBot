/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.procedure.TShortProcedure;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TSynchronizedShortCollection
implements TShortCollection,
Serializable {
    private static final long serialVersionUID = 3053995032091335093L;
    final TShortCollection c;
    final Object mutex;

    public TSynchronizedShortCollection(TShortCollection c) {
        if (c == null) {
            throw new NullPointerException();
        }
        this.c = c;
        this.mutex = this;
    }

    public TSynchronizedShortCollection(TShortCollection c, Object mutex) {
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
    public boolean contains(short o) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.contains(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public short[] toArray() {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.toArray();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public short[] toArray(short[] a) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.toArray(a);
        }
    }

    @Override
    public TShortIterator iterator() {
        return this.c.iterator();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean add(short e) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.add(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean remove(short o) {
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
    public boolean containsAll(TShortCollection coll) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.containsAll(coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsAll(short[] array) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.containsAll(array);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean addAll(Collection<? extends Short> coll) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.addAll(coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean addAll(TShortCollection coll) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.addAll(coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean addAll(short[] array) {
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
    public boolean removeAll(TShortCollection coll) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.removeAll(coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean removeAll(short[] array) {
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
    public boolean retainAll(TShortCollection coll) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.retainAll(coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainAll(short[] array) {
        Object object = this.mutex;
        synchronized (object) {
            return this.c.retainAll(array);
        }
    }

    @Override
    public short getNoEntryValue() {
        return this.c.getNoEntryValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEach(TShortProcedure procedure) {
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

