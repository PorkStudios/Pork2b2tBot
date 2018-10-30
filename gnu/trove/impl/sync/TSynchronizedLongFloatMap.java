/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.sync.TSynchronizedFloatCollection;
import gnu.trove.impl.sync.TSynchronizedLongSet;
import gnu.trove.iterator.TLongFloatIterator;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TLongFloatProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TSynchronizedLongFloatMap
implements TLongFloatMap,
Serializable {
    private static final long serialVersionUID = 1978198479659022715L;
    private final TLongFloatMap m;
    final Object mutex;
    private transient TLongSet keySet = null;
    private transient TFloatCollection values = null;

    public TSynchronizedLongFloatMap(TLongFloatMap m) {
        if (m == null) {
            throw new NullPointerException();
        }
        this.m = m;
        this.mutex = this;
    }

    public TSynchronizedLongFloatMap(TLongFloatMap m, Object mutex) {
        this.m = m;
        this.mutex = mutex;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int size() {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isEmpty() {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.isEmpty();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsKey(long key) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.containsKey(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsValue(float value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.containsValue(value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float get(long key) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.get(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float put(long key, float value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.put(key, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float remove(long key) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.remove(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putAll(Map<? extends Long, ? extends Float> map) {
        Object object = this.mutex;
        synchronized (object) {
            this.m.putAll(map);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putAll(TLongFloatMap map) {
        Object object = this.mutex;
        synchronized (object) {
            this.m.putAll(map);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        Object object = this.mutex;
        synchronized (object) {
            this.m.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TLongSet keySet() {
        Object object = this.mutex;
        synchronized (object) {
            if (this.keySet == null) {
                this.keySet = new TSynchronizedLongSet(this.m.keySet(), this.mutex);
            }
            return this.keySet;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long[] keys() {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.keys();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long[] keys(long[] array) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.keys(array);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TFloatCollection valueCollection() {
        Object object = this.mutex;
        synchronized (object) {
            if (this.values == null) {
                this.values = new TSynchronizedFloatCollection(this.m.valueCollection(), this.mutex);
            }
            return this.values;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float[] values() {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.values();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float[] values(float[] array) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.values(array);
        }
    }

    @Override
    public TLongFloatIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public long getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public float getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float putIfAbsent(long key, float value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.putIfAbsent(key, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachKey(TLongProcedure procedure) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.forEachKey(procedure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachValue(TFloatProcedure procedure) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.forEachValue(procedure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachEntry(TLongFloatProcedure procedure) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.forEachEntry(procedure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void transformValues(TFloatFunction function) {
        Object object = this.mutex;
        synchronized (object) {
            this.m.transformValues(function);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TLongFloatProcedure procedure) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.retainEntries(procedure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean increment(long key) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.increment(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean adjustValue(long key, float amount) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.adjustValue(key, amount);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float adjustOrPutValue(long key, float adjust_amount, float put_amount) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.adjustOrPutValue(key, adjust_amount, put_amount);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean equals(Object o) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.equals(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int hashCode() {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.hashCode();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.toString();
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

