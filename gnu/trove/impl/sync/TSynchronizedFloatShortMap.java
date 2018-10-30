/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.sync.TSynchronizedFloatSet;
import gnu.trove.impl.sync.TSynchronizedShortCollection;
import gnu.trove.iterator.TFloatShortIterator;
import gnu.trove.map.TFloatShortMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TFloatShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TFloatSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TSynchronizedFloatShortMap
implements TFloatShortMap,
Serializable {
    private static final long serialVersionUID = 1978198479659022715L;
    private final TFloatShortMap m;
    final Object mutex;
    private transient TFloatSet keySet = null;
    private transient TShortCollection values = null;

    public TSynchronizedFloatShortMap(TFloatShortMap m) {
        if (m == null) {
            throw new NullPointerException();
        }
        this.m = m;
        this.mutex = this;
    }

    public TSynchronizedFloatShortMap(TFloatShortMap m, Object mutex) {
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
    public boolean containsKey(float key) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.containsKey(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsValue(short value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.containsValue(value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public short get(float key) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.get(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public short put(float key, short value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.put(key, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public short remove(float key) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.remove(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putAll(Map<? extends Float, ? extends Short> map) {
        Object object = this.mutex;
        synchronized (object) {
            this.m.putAll(map);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putAll(TFloatShortMap map) {
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
    public TFloatSet keySet() {
        Object object = this.mutex;
        synchronized (object) {
            if (this.keySet == null) {
                this.keySet = new TSynchronizedFloatSet(this.m.keySet(), this.mutex);
            }
            return this.keySet;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float[] keys() {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.keys();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float[] keys(float[] array) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.keys(array);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TShortCollection valueCollection() {
        Object object = this.mutex;
        synchronized (object) {
            if (this.values == null) {
                this.values = new TSynchronizedShortCollection(this.m.valueCollection(), this.mutex);
            }
            return this.values;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public short[] values() {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.values();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public short[] values(short[] array) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.values(array);
        }
    }

    @Override
    public TFloatShortIterator iterator() {
        return this.m.iterator();
    }

    @Override
    public float getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    @Override
    public short getNoEntryValue() {
        return this.m.getNoEntryValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public short putIfAbsent(float key, short value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.putIfAbsent(key, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachKey(TFloatProcedure procedure) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.forEachKey(procedure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachValue(TShortProcedure procedure) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.forEachValue(procedure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachEntry(TFloatShortProcedure procedure) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.forEachEntry(procedure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void transformValues(TShortFunction function) {
        Object object = this.mutex;
        synchronized (object) {
            this.m.transformValues(function);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TFloatShortProcedure procedure) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.retainEntries(procedure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean increment(float key) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.increment(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean adjustValue(float key, short amount) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.adjustValue(key, amount);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public short adjustOrPutValue(float key, short adjust_amount, short put_amount) {
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

