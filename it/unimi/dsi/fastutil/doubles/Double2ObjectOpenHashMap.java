/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2ObjectMap;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;

public class Double2ObjectOpenHashMap<V>
extends AbstractDouble2ObjectMap<V>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient double[] key;
    protected transient V[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Double2ObjectMap.FastEntrySet<V> entries;
    protected transient DoubleSet keys;
    protected transient ObjectCollection<V> values;

    public Double2ObjectOpenHashMap(int expected, float f) {
        if (f <= 0.0f || f > 1.0f) {
            throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
        }
        if (expected < 0) {
            throw new IllegalArgumentException("The expected number of elements must be nonnegative");
        }
        this.f = f;
        this.minN = this.n = HashCommon.arraySize(expected, f);
        this.mask = this.n - 1;
        this.maxFill = HashCommon.maxFill(this.n, f);
        this.key = new double[this.n + 1];
        this.value = new Object[this.n + 1];
    }

    public Double2ObjectOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Double2ObjectOpenHashMap() {
        this(16, 0.75f);
    }

    public Double2ObjectOpenHashMap(Map<? extends Double, ? extends V> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Double2ObjectOpenHashMap(Map<? extends Double, ? extends V> m) {
        this(m, 0.75f);
    }

    public Double2ObjectOpenHashMap(Double2ObjectMap<V> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Double2ObjectOpenHashMap(Double2ObjectMap<V> m) {
        this(m, 0.75f);
    }

    public Double2ObjectOpenHashMap(double[] k, V[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Double2ObjectOpenHashMap(double[] k, V[] v) {
        this(k, v, 0.75f);
    }

    private int realSize() {
        return this.containsNullKey ? this.size - 1 : this.size;
    }

    private void ensureCapacity(int capacity) {
        int needed = HashCommon.arraySize(capacity, this.f);
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    private void tryCapacity(long capacity) {
        int needed = (int)Math.min(0x40000000L, Math.max(2L, HashCommon.nextPowerOfTwo((long)Math.ceil((float)capacity / this.f))));
        if (needed > this.n) {
            this.rehash(needed);
        }
    }

    private V removeEntry(int pos) {
        V oldValue = this.value[pos];
        this.value[pos] = null;
        --this.size;
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private V removeNullEntry() {
        this.containsNullKey = false;
        V oldValue = this.value[this.n];
        this.value[this.n] = null;
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends Double, ? extends V> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }

    private int find(double k) {
        if (Double.doubleToLongBits(k) == 0L) {
            return this.containsNullKey ? this.n : - this.n + 1;
        }
        double[] key = this.key;
        int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return - pos + 1;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return pos;
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return - pos + 1;
        } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr));
        return pos;
    }

    private void insert(int pos, double k, V v) {
        if (pos == this.n) {
            this.containsNullKey = true;
        }
        this.key[pos] = k;
        this.value[pos] = v;
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
    }

    @Override
    public V put(double k, V v) {
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return (V)this.defRetValue;
        }
        V oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    protected final void shiftKeys(int pos) {
        double[] key = this.key;
        do {
            double curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if (Double.doubleToLongBits(curr = key[pos]) == 0L) {
                    key[last] = 0.0;
                    this.value[last] = null;
                    return;
                }
                int slot = (int)HashCommon.mix(Double.doubleToRawLongBits(curr)) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            } while (true);
            key[last] = curr;
            this.value[last] = this.value[pos];
        } while (true);
    }

    @Override
    public V remove(double k) {
        if (Double.doubleToLongBits(k) == 0L) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return (V)this.defRetValue;
        }
        double[] key = this.key;
        int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return (V)this.defRetValue;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return this.removeEntry(pos);
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return (V)this.defRetValue;
        } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr));
        return this.removeEntry(pos);
    }

    @Override
    public V get(double k) {
        if (Double.doubleToLongBits(k) == 0L) {
            return (V)(this.containsNullKey ? this.value[this.n] : this.defRetValue);
        }
        double[] key = this.key;
        int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return (V)this.defRetValue;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return this.value[pos];
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return (V)this.defRetValue;
        } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr));
        return this.value[pos];
    }

    @Override
    public boolean containsKey(double k) {
        if (Double.doubleToLongBits(k) == 0L) {
            return this.containsNullKey;
        }
        double[] key = this.key;
        int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return false;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return true;
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return false;
        } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr));
        return true;
    }

    @Override
    public boolean containsValue(Object v) {
        V[] value = this.value;
        double[] key = this.key;
        if (this.containsNullKey && Objects.equals(value[this.n], v)) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (Double.doubleToLongBits(key[i]) == 0L || !Objects.equals(value[i], v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public V getOrDefault(double k, V defaultValue) {
        if (Double.doubleToLongBits(k) == 0L) {
            return this.containsNullKey ? this.value[this.n] : defaultValue;
        }
        double[] key = this.key;
        int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return defaultValue;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return this.value[pos];
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return defaultValue;
        } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr));
        return this.value[pos];
    }

    @Override
    public V putIfAbsent(double k, V v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return (V)this.defRetValue;
    }

    @Override
    public boolean remove(double k, Object v) {
        if (Double.doubleToLongBits(k) == 0L) {
            if (this.containsNullKey && Objects.equals(v, this.value[this.n])) {
                this.removeNullEntry();
                return true;
            }
            return false;
        }
        double[] key = this.key;
        int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return false;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr) && Objects.equals(v, this.value[pos])) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return false;
        } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr) || !Objects.equals(v, this.value[pos]));
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(double k, V oldValue, V v) {
        int pos = this.find(k);
        if (pos < 0 || !Objects.equals(oldValue, this.value[pos])) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public V replace(double k, V v) {
        int pos = this.find(k);
        if (pos < 0) {
            return (V)this.defRetValue;
        }
        V oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public V computeIfAbsent(double k, DoubleFunction<? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        V newValue = mappingFunction.apply(k);
        this.insert(- pos - 1, k, newValue);
        return newValue;
    }

    @Override
    public V computeIfPresent(double k, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return (V)this.defRetValue;
        }
        V newValue = remappingFunction.apply((Double)k, this.value[pos]);
        if (newValue == null) {
            if (Double.doubleToLongBits(k) == 0L) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return (V)this.defRetValue;
        }
        this.value[pos] = newValue;
        return this.value[pos];
    }

    @Override
    public V compute(double k, BiFunction<? super Double, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        V newValue = remappingFunction.apply((Double)k, pos >= 0 ? (Object)this.value[pos] : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (Double.doubleToLongBits(k) == 0L) {
                    this.removeNullEntry();
                } else {
                    this.removeEntry(pos);
                }
            }
            return (V)this.defRetValue;
        }
        V newVal = newValue;
        if (pos < 0) {
            this.insert(- pos - 1, k, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public V merge(double k, V v, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0 || this.value[pos] == null) {
            if (v == null) {
                return (V)this.defRetValue;
            }
            this.insert(- pos - 1, k, v);
            return v;
        }
        V newValue = remappingFunction.apply(this.value[pos], v);
        if (newValue == null) {
            if (Double.doubleToLongBits(k) == 0L) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return (V)this.defRetValue;
        }
        this.value[pos] = newValue;
        return this.value[pos];
    }

    @Override
    public void clear() {
        if (this.size == 0) {
            return;
        }
        this.size = 0;
        this.containsNullKey = false;
        Arrays.fill(this.key, 0.0);
        Arrays.fill(this.value, null);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public Double2ObjectMap.FastEntrySet<V> double2ObjectEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public DoubleSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public ObjectCollection<V> values() {
        if (this.values == null) {
            this.values = new AbstractObjectCollection<V>(){

                @Override
                public ObjectIterator<V> iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Double2ObjectOpenHashMap.this.size;
                }

                @Override
                public boolean contains(Object v) {
                    return Double2ObjectOpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Double2ObjectOpenHashMap.this.clear();
                }

                @Override
                public void forEach(Consumer<? super V> consumer) {
                    if (Double2ObjectOpenHashMap.this.containsNullKey) {
                        consumer.accept(Double2ObjectOpenHashMap.this.value[Double2ObjectOpenHashMap.this.n]);
                    }
                    int pos = Double2ObjectOpenHashMap.this.n;
                    while (pos-- != 0) {
                        if (Double.doubleToLongBits(Double2ObjectOpenHashMap.this.key[pos]) == 0L) continue;
                        consumer.accept(Double2ObjectOpenHashMap.this.value[pos]);
                    }
                }
            };
        }
        return this.values;
    }

    public boolean trim() {
        int l = HashCommon.arraySize(this.size, this.f);
        if (l >= this.n || this.size > HashCommon.maxFill(l, this.f)) {
            return true;
        }
        try {
            this.rehash(l);
        }
        catch (OutOfMemoryError cantDoIt) {
            return false;
        }
        return true;
    }

    public boolean trim(int n) {
        int l = HashCommon.nextPowerOfTwo((int)Math.ceil((float)n / this.f));
        if (l >= n || this.size > HashCommon.maxFill(l, this.f)) {
            return true;
        }
        try {
            this.rehash(l);
        }
        catch (OutOfMemoryError cantDoIt) {
            return false;
        }
        return true;
    }

    protected void rehash(int newN) {
        double[] key = this.key;
        V[] value = this.value;
        int mask = newN - 1;
        double[] newKey = new double[newN + 1];
        Object[] newValue = new Object[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (Double.doubleToLongBits(key[--i]) == 0L) {
            }
            int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(key[i])) & mask;
            if (Double.doubleToLongBits(newKey[pos]) != 0L) {
                while (Double.doubleToLongBits(newKey[pos = pos + 1 & mask]) != 0L) {
                }
            }
            newKey[pos] = key[i];
            newValue[pos] = value[i];
        }
        newValue[newN] = value[this.n];
        this.n = newN;
        this.mask = mask;
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.key = newKey;
        this.value = newValue;
    }

    public Double2ObjectOpenHashMap<V> clone() {
        Double2ObjectOpenHashMap c;
        try {
            c = (Double2ObjectOpenHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (double[])this.key.clone();
        c.value = (Object[])this.value.clone();
        return c;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        int t = 0;
        while (j-- != 0) {
            while (Double.doubleToLongBits(this.key[i]) == 0L) {
                ++i;
            }
            t = HashCommon.double2int(this.key[i]);
            if (this != this.value[i]) {
                t ^= this.value[i] == null ? 0 : this.value[i].hashCode();
            }
            h += t;
            ++i;
        }
        if (this.containsNullKey) {
            h += this.value[this.n] == null ? 0 : this.value[this.n].hashCode();
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        double[] key = this.key;
        V[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeDouble(key[e]);
            s.writeObject(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new double[this.n + 1];
        double[] key = this.key;
        this.value = new Object[this.n + 1];
        Object[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            double k = s.readDouble();
            Object v = s.readObject();
            if (Double.doubleToLongBits(k) == 0L) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
                while (Double.doubleToLongBits(key[pos]) != 0L) {
                    pos = pos + 1 & this.mask;
                }
            }
            key[pos] = k;
            value[pos] = v;
        }
    }

    private void checkTable() {
    }

    private final class ValueIterator
    extends Double2ObjectOpenHashMap<V>
    implements ObjectIterator<V> {
        public ValueIterator() {
            super();
        }

        @Override
        public V next() {
            return Double2ObjectOpenHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractDoubleSet {
        private KeySet() {
        }

        @Override
        public DoubleIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public void forEach(DoubleConsumer consumer) {
            if (Double2ObjectOpenHashMap.this.containsNullKey) {
                consumer.accept(Double2ObjectOpenHashMap.this.key[Double2ObjectOpenHashMap.this.n]);
            }
            int pos = Double2ObjectOpenHashMap.this.n;
            while (pos-- != 0) {
                double k = Double2ObjectOpenHashMap.this.key[pos];
                if (Double.doubleToLongBits(k) == 0L) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Double2ObjectOpenHashMap.this.size;
        }

        @Override
        public boolean contains(double k) {
            return Double2ObjectOpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(double k) {
            int oldSize = Double2ObjectOpenHashMap.this.size;
            Double2ObjectOpenHashMap.this.remove(k);
            return Double2ObjectOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Double2ObjectOpenHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends Double2ObjectOpenHashMap<V>
    implements DoubleIterator {
        public KeyIterator() {
            super();
        }

        @Override
        public double nextDouble() {
            return Double2ObjectOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Double2ObjectMap.Entry<V>>
    implements Double2ObjectMap.FastEntrySet<V> {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Double2ObjectMap.Entry<V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Double2ObjectMap.Entry<V>> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Double)) {
                return false;
            }
            double k = (Double)e.getKey();
            Object v = e.getValue();
            if (Double.doubleToLongBits(k) == 0L) {
                return Double2ObjectOpenHashMap.this.containsNullKey && Objects.equals(Double2ObjectOpenHashMap.this.value[Double2ObjectOpenHashMap.this.n], v);
            }
            double[] key = Double2ObjectOpenHashMap.this.key;
            int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & Double2ObjectOpenHashMap.this.mask;
            double curr = key[pos];
            if (Double.doubleToLongBits(curr) == 0L) {
                return false;
            }
            if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                return Objects.equals(Double2ObjectOpenHashMap.this.value[pos], v);
            }
            do {
                if (Double.doubleToLongBits(curr = key[pos = pos + 1 & Double2ObjectOpenHashMap.this.mask]) != 0L) continue;
                return false;
            } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr));
            return Objects.equals(Double2ObjectOpenHashMap.this.value[pos], v);
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Double)) {
                return false;
            }
            double k = (Double)e.getKey();
            Object v = e.getValue();
            if (Double.doubleToLongBits(k) == 0L) {
                if (Double2ObjectOpenHashMap.this.containsNullKey && Objects.equals(Double2ObjectOpenHashMap.this.value[Double2ObjectOpenHashMap.this.n], v)) {
                    Double2ObjectOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            double[] key = Double2ObjectOpenHashMap.this.key;
            int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & Double2ObjectOpenHashMap.this.mask;
            double curr = key[pos];
            if (Double.doubleToLongBits(curr) == 0L) {
                return false;
            }
            if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
                if (Objects.equals(Double2ObjectOpenHashMap.this.value[pos], v)) {
                    Double2ObjectOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if (Double.doubleToLongBits(curr = key[pos = pos + 1 & Double2ObjectOpenHashMap.this.mask]) != 0L) continue;
                return false;
            } while (Double.doubleToLongBits(curr) != Double.doubleToLongBits(k) || !Objects.equals(Double2ObjectOpenHashMap.this.value[pos], v));
            Double2ObjectOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Double2ObjectOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Double2ObjectOpenHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Double2ObjectMap.Entry<V>> consumer) {
            if (Double2ObjectOpenHashMap.this.containsNullKey) {
                consumer.accept(new AbstractDouble2ObjectMap.BasicEntry(Double2ObjectOpenHashMap.this.key[Double2ObjectOpenHashMap.this.n], Double2ObjectOpenHashMap.this.value[Double2ObjectOpenHashMap.this.n]));
            }
            int pos = Double2ObjectOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Double.doubleToLongBits(Double2ObjectOpenHashMap.this.key[pos]) == 0L) continue;
                consumer.accept(new AbstractDouble2ObjectMap.BasicEntry(Double2ObjectOpenHashMap.this.key[pos], Double2ObjectOpenHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Double2ObjectMap.Entry<V>> consumer) {
            AbstractDouble2ObjectMap.BasicEntry entry = new AbstractDouble2ObjectMap.BasicEntry();
            if (Double2ObjectOpenHashMap.this.containsNullKey) {
                entry.key = Double2ObjectOpenHashMap.this.key[Double2ObjectOpenHashMap.this.n];
                entry.value = Double2ObjectOpenHashMap.this.value[Double2ObjectOpenHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Double2ObjectOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Double.doubleToLongBits(Double2ObjectOpenHashMap.this.key[pos]) == 0L) continue;
                entry.key = Double2ObjectOpenHashMap.this.key[pos];
                entry.value = Double2ObjectOpenHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends Double2ObjectOpenHashMap<V>
    implements ObjectIterator<Double2ObjectMap.Entry<V>> {
        private final Double2ObjectOpenHashMap<V> entry;

        private FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        @Override
        public Double2ObjectOpenHashMap<V> next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends Double2ObjectOpenHashMap<V>
    implements ObjectIterator<Double2ObjectMap.Entry<V>> {
        private Double2ObjectOpenHashMap<V> entry;

        private EntryIterator() {
            super();
        }

        @Override
        public Double2ObjectOpenHashMap<V> next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        public void remove() {
            MapIterator.super.remove();
            this.entry.index = -1;
        }
    }

    private class MapIterator {
        int pos;
        int last;
        int c;
        boolean mustReturnNullKey;
        DoubleArrayList wrapped;

        private MapIterator() {
            this.pos = Double2ObjectOpenHashMap.this.n;
            this.last = -1;
            this.c = Double2ObjectOpenHashMap.this.size;
            this.mustReturnNullKey = Double2ObjectOpenHashMap.this.containsNullKey;
        }

        public boolean hasNext() {
            return this.c != 0;
        }

        public int nextEntry() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            --this.c;
            if (this.mustReturnNullKey) {
                this.mustReturnNullKey = false;
                this.last = Double2ObjectOpenHashMap.this.n;
                return this.last;
            }
            double[] key = Double2ObjectOpenHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                double k = this.wrapped.getDouble(- this.pos - 1);
                int p = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & Double2ObjectOpenHashMap.this.mask;
                while (Double.doubleToLongBits(k) != Double.doubleToLongBits(key[p])) {
                    p = p + 1 & Double2ObjectOpenHashMap.this.mask;
                }
                return p;
            } while (Double.doubleToLongBits(key[this.pos]) == 0L);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            double[] key = Double2ObjectOpenHashMap.this.key;
            do {
                double curr;
                int last = pos;
                pos = last + 1 & Double2ObjectOpenHashMap.this.mask;
                do {
                    if (Double.doubleToLongBits(curr = key[pos]) == 0L) {
                        key[last] = 0.0;
                        Double2ObjectOpenHashMap.this.value[last] = null;
                        return;
                    }
                    int slot = (int)HashCommon.mix(Double.doubleToRawLongBits(curr)) & Double2ObjectOpenHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Double2ObjectOpenHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new DoubleArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Double2ObjectOpenHashMap.this.value[last] = Double2ObjectOpenHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Double2ObjectOpenHashMap.this.n) {
                Double2ObjectOpenHashMap.this.containsNullKey = false;
                Double2ObjectOpenHashMap.this.value[Double2ObjectOpenHashMap.this.n] = null;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Double2ObjectOpenHashMap.this.remove(this.wrapped.getDouble(- this.pos - 1));
                this.last = -1;
                return;
            }
            --Double2ObjectOpenHashMap.this.size;
            this.last = -1;
        }

        public int skip(int n) {
            int i = n;
            while (i-- != 0 && this.hasNext()) {
                this.nextEntry();
            }
            return n - i - 1;
        }
    }

    final class MapEntry
    implements Double2ObjectMap.Entry<V>,
    Map.Entry<Double, V> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public double getDoubleKey() {
            return Double2ObjectOpenHashMap.this.key[this.index];
        }

        @Override
        public V getValue() {
            return Double2ObjectOpenHashMap.this.value[this.index];
        }

        @Override
        public V setValue(V v) {
            Object oldValue = Double2ObjectOpenHashMap.this.value[this.index];
            Double2ObjectOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Double getKey() {
            return Double2ObjectOpenHashMap.this.key[this.index];
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Double.doubleToLongBits(Double2ObjectOpenHashMap.this.key[this.index]) == Double.doubleToLongBits((Double)e.getKey()) && Objects.equals(Double2ObjectOpenHashMap.this.value[this.index], e.getValue());
        }

        @Override
        public int hashCode() {
            return HashCommon.double2int(Double2ObjectOpenHashMap.this.key[this.index]) ^ (Double2ObjectOpenHashMap.this.value[this.index] == null ? 0 : Double2ObjectOpenHashMap.this.value[this.index].hashCode());
        }

        public String toString() {
            return "" + Double2ObjectOpenHashMap.this.key[this.index] + "=>" + Double2ObjectOpenHashMap.this.value[this.index];
        }
    }

}

