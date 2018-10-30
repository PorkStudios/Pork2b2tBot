/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl.sync;

import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.sync.SynchronizedCollection;
import gnu.trove.impl.sync.TSynchronizedShortSet;
import gnu.trove.iterator.TShortObjectIterator;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TShortObjectProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TSynchronizedShortObjectMap<V>
implements TShortObjectMap<V>,
Serializable {
    private static final long serialVersionUID = 1978198479659022715L;
    private final TShortObjectMap<V> m;
    final Object mutex;
    private transient TShortSet keySet = null;
    private transient Collection<V> values = null;

    public TSynchronizedShortObjectMap(TShortObjectMap<V> m) {
        if (m == null) {
            throw new NullPointerException();
        }
        this.m = m;
        this.mutex = this;
    }

    public TSynchronizedShortObjectMap(TShortObjectMap<V> m, Object mutex) {
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
    public boolean containsKey(short key) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.containsKey(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsValue(Object value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.containsValue(value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V get(short key) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.get(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V put(short key, V value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.put(key, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V remove(short key) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.remove(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putAll(Map<? extends Short, ? extends V> map) {
        Object object = this.mutex;
        synchronized (object) {
            this.m.putAll(map);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putAll(TShortObjectMap<? extends V> map) {
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
    public TShortSet keySet() {
        Object object = this.mutex;
        synchronized (object) {
            if (this.keySet == null) {
                this.keySet = new TSynchronizedShortSet(this.m.keySet(), this.mutex);
            }
            return this.keySet;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public short[] keys() {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.keys();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public short[] keys(short[] array) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.keys(array);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Collection<V> valueCollection() {
        Object object = this.mutex;
        synchronized (object) {
            if (this.values == null) {
                this.values = new SynchronizedCollection<V>(this.m.valueCollection(), this.mutex);
            }
            return this.values;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object[] values() {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.values();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V[] values(V[] array) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.values(array);
        }
    }

    @Override
    public TShortObjectIterator<V> iterator() {
        return this.m.iterator();
    }

    @Override
    public short getNoEntryKey() {
        return this.m.getNoEntryKey();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V putIfAbsent(short key, V value) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.putIfAbsent(key, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachKey(TShortProcedure procedure) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.forEachKey(procedure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachValue(TObjectProcedure<? super V> procedure) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.forEachValue(procedure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean forEachEntry(TShortObjectProcedure<? super V> procedure) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.forEachEntry(procedure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void transformValues(TObjectFunction<V, V> function) {
        Object object = this.mutex;
        synchronized (object) {
            this.m.transformValues(function);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean retainEntries(TShortObjectProcedure<? super V> procedure) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.retainEntries(procedure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean equals(Object o) {
        Object object = this.mutex;
        synchronized (object) {
            return this.m.equals(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
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

