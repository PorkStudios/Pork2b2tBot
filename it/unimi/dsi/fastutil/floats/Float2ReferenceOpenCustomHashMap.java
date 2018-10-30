/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloat2ReferenceMap;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.Float2ReferenceMap;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatHash;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
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

public class Float2ReferenceOpenCustomHashMap<V>
extends AbstractFloat2ReferenceMap<V>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient float[] key;
    protected transient V[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected FloatHash.Strategy strategy;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Float2ReferenceMap.FastEntrySet<V> entries;
    protected transient FloatSet keys;
    protected transient ReferenceCollection<V> values;

    public Float2ReferenceOpenCustomHashMap(int expected, float f, FloatHash.Strategy strategy) {
        this.strategy = strategy;
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
        this.key = new float[this.n + 1];
        this.value = new Object[this.n + 1];
    }

    public Float2ReferenceOpenCustomHashMap(int expected, FloatHash.Strategy strategy) {
        this(expected, 0.75f, strategy);
    }

    public Float2ReferenceOpenCustomHashMap(FloatHash.Strategy strategy) {
        this(16, 0.75f, strategy);
    }

    public Float2ReferenceOpenCustomHashMap(Map<? extends Float, ? extends V> m, float f, FloatHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Float2ReferenceOpenCustomHashMap(Map<? extends Float, ? extends V> m, FloatHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }

    public Float2ReferenceOpenCustomHashMap(Float2ReferenceMap<V> m, float f, FloatHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Float2ReferenceOpenCustomHashMap(Float2ReferenceMap<V> m, FloatHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }

    public Float2ReferenceOpenCustomHashMap(float[] k, V[] v, float f, FloatHash.Strategy strategy) {
        this(k.length, f, strategy);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Float2ReferenceOpenCustomHashMap(float[] k, V[] v, FloatHash.Strategy strategy) {
        this(k, v, 0.75f, strategy);
    }

    public FloatHash.Strategy strategy() {
        return this.strategy;
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
    public void putAll(Map<? extends Float, ? extends V> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }

    private int find(float k) {
        if (this.strategy.equals(k, 0.0f)) {
            return this.containsNullKey ? this.n : - this.n + 1;
        }
        float[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        float curr = key[pos];
        if (Float.floatToIntBits(curr) == 0) {
            return - pos + 1;
        }
        if (this.strategy.equals(k, curr)) {
            return pos;
        }
        do {
            if (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return - pos + 1;
        } while (!this.strategy.equals(k, curr));
        return pos;
    }

    private void insert(int pos, float k, V v) {
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
    public V put(float k, V v) {
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
        float[] key = this.key;
        do {
            float curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if (Float.floatToIntBits(curr = key[pos]) == 0) {
                    key[last] = 0.0f;
                    this.value[last] = null;
                    return;
                }
                int slot = HashCommon.mix(this.strategy.hashCode(curr)) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            } while (true);
            key[last] = curr;
            this.value[last] = this.value[pos];
        } while (true);
    }

    @Override
    public V remove(float k) {
        if (this.strategy.equals(k, 0.0f)) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return (V)this.defRetValue;
        }
        float[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        float curr = key[pos];
        if (Float.floatToIntBits(curr) == 0) {
            return (V)this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.removeEntry(pos);
        }
        do {
            if (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return (V)this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        return this.removeEntry(pos);
    }

    @Override
    public V get(float k) {
        if (this.strategy.equals(k, 0.0f)) {
            return (V)(this.containsNullKey ? this.value[this.n] : this.defRetValue);
        }
        float[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        float curr = key[pos];
        if (Float.floatToIntBits(curr) == 0) {
            return (V)this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        do {
            if (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return (V)this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        return this.value[pos];
    }

    @Override
    public boolean containsKey(float k) {
        if (this.strategy.equals(k, 0.0f)) {
            return this.containsNullKey;
        }
        float[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        float curr = key[pos];
        if (Float.floatToIntBits(curr) == 0) {
            return false;
        }
        if (this.strategy.equals(k, curr)) {
            return true;
        }
        do {
            if (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (!this.strategy.equals(k, curr));
        return true;
    }

    @Override
    public boolean containsValue(Object v) {
        V[] value = this.value;
        float[] key = this.key;
        if (this.containsNullKey && value[this.n] == v) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (Float.floatToIntBits(key[i]) == 0 || value[i] != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public V getOrDefault(float k, V defaultValue) {
        if (this.strategy.equals(k, 0.0f)) {
            return this.containsNullKey ? this.value[this.n] : defaultValue;
        }
        float[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        float curr = key[pos];
        if (Float.floatToIntBits(curr) == 0) {
            return defaultValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        do {
            if (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return defaultValue;
        } while (!this.strategy.equals(k, curr));
        return this.value[pos];
    }

    @Override
    public V putIfAbsent(float k, V v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return (V)this.defRetValue;
    }

    @Override
    public boolean remove(float k, Object v) {
        if (this.strategy.equals(k, 0.0f)) {
            if (this.containsNullKey && v == this.value[this.n]) {
                this.removeNullEntry();
                return true;
            }
            return false;
        }
        float[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        float curr = key[pos];
        if (Float.floatToIntBits(curr) == 0) {
            return false;
        }
        if (this.strategy.equals(k, curr) && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (!this.strategy.equals(k, curr) || v != this.value[pos]);
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(float k, V oldValue, V v) {
        int pos = this.find(k);
        if (pos < 0 || oldValue != this.value[pos]) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public V replace(float k, V v) {
        int pos = this.find(k);
        if (pos < 0) {
            return (V)this.defRetValue;
        }
        V oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public V computeIfAbsent(float k, DoubleFunction<? extends V> mappingFunction) {
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
    public V computeIfPresent(float k, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return (V)this.defRetValue;
        }
        V newValue = remappingFunction.apply(Float.valueOf(k), this.value[pos]);
        if (newValue == null) {
            if (this.strategy.equals(k, 0.0f)) {
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
    public V compute(float k, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        V newValue = remappingFunction.apply(Float.valueOf(k), pos >= 0 ? (Object)this.value[pos] : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (this.strategy.equals(k, 0.0f)) {
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
    public V merge(float k, V v, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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
            if (this.strategy.equals(k, 0.0f)) {
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
        Arrays.fill(this.key, 0.0f);
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

    public Float2ReferenceMap.FastEntrySet<V> float2ReferenceEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public FloatSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public ReferenceCollection<V> values() {
        if (this.values == null) {
            this.values = new AbstractReferenceCollection<V>(){

                @Override
                public ObjectIterator<V> iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Float2ReferenceOpenCustomHashMap.this.size;
                }

                @Override
                public boolean contains(Object v) {
                    return Float2ReferenceOpenCustomHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Float2ReferenceOpenCustomHashMap.this.clear();
                }

                @Override
                public void forEach(Consumer<? super V> consumer) {
                    if (Float2ReferenceOpenCustomHashMap.this.containsNullKey) {
                        consumer.accept(Float2ReferenceOpenCustomHashMap.this.value[Float2ReferenceOpenCustomHashMap.this.n]);
                    }
                    int pos = Float2ReferenceOpenCustomHashMap.this.n;
                    while (pos-- != 0) {
                        if (Float.floatToIntBits(Float2ReferenceOpenCustomHashMap.this.key[pos]) == 0) continue;
                        consumer.accept(Float2ReferenceOpenCustomHashMap.this.value[pos]);
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
        float[] key = this.key;
        V[] value = this.value;
        int mask = newN - 1;
        float[] newKey = new float[newN + 1];
        Object[] newValue = new Object[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (Float.floatToIntBits(key[--i]) == 0) {
            }
            int pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask;
            if (Float.floatToIntBits(newKey[pos]) != 0) {
                while (Float.floatToIntBits(newKey[pos = pos + 1 & mask]) != 0) {
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

    public Float2ReferenceOpenCustomHashMap<V> clone() {
        Float2ReferenceOpenCustomHashMap c;
        try {
            c = (Float2ReferenceOpenCustomHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (float[])this.key.clone();
        c.value = (Object[])this.value.clone();
        c.strategy = this.strategy;
        return c;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        int t = 0;
        while (j-- != 0) {
            while (Float.floatToIntBits(this.key[i]) == 0) {
                ++i;
            }
            t = this.strategy.hashCode(this.key[i]);
            if (this != this.value[i]) {
                t ^= this.value[i] == null ? 0 : System.identityHashCode(this.value[i]);
            }
            h += t;
            ++i;
        }
        if (this.containsNullKey) {
            h += this.value[this.n] == null ? 0 : System.identityHashCode(this.value[this.n]);
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        float[] key = this.key;
        V[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeFloat(key[e]);
            s.writeObject(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new float[this.n + 1];
        float[] key = this.key;
        this.value = new Object[this.n + 1];
        Object[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            float k = s.readFloat();
            Object v = s.readObject();
            if (this.strategy.equals(k, 0.0f)) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
                while (Float.floatToIntBits(key[pos]) != 0) {
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
    extends Float2ReferenceOpenCustomHashMap<V>
    implements ObjectIterator<V> {
        public ValueIterator() {
            super();
        }

        @Override
        public V next() {
            return Float2ReferenceOpenCustomHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractFloatSet {
        private KeySet() {
        }

        @Override
        public FloatIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public void forEach(DoubleConsumer consumer) {
            if (Float2ReferenceOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(Float2ReferenceOpenCustomHashMap.this.key[Float2ReferenceOpenCustomHashMap.this.n]);
            }
            int pos = Float2ReferenceOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                float k = Float2ReferenceOpenCustomHashMap.this.key[pos];
                if (Float.floatToIntBits(k) == 0) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Float2ReferenceOpenCustomHashMap.this.size;
        }

        @Override
        public boolean contains(float k) {
            return Float2ReferenceOpenCustomHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(float k) {
            int oldSize = Float2ReferenceOpenCustomHashMap.this.size;
            Float2ReferenceOpenCustomHashMap.this.remove(k);
            return Float2ReferenceOpenCustomHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Float2ReferenceOpenCustomHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends Float2ReferenceOpenCustomHashMap<V>
    implements FloatIterator {
        public KeyIterator() {
            super();
        }

        @Override
        public float nextFloat() {
            return Float2ReferenceOpenCustomHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Float2ReferenceMap.Entry<V>>
    implements Float2ReferenceMap.FastEntrySet<V> {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Float2ReferenceMap.Entry<V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Float2ReferenceMap.Entry<V>> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                return false;
            }
            float k = ((Float)e.getKey()).floatValue();
            Object v = e.getValue();
            if (Float2ReferenceOpenCustomHashMap.this.strategy.equals(k, 0.0f)) {
                return Float2ReferenceOpenCustomHashMap.this.containsNullKey && Float2ReferenceOpenCustomHashMap.this.value[Float2ReferenceOpenCustomHashMap.this.n] == v;
            }
            float[] key = Float2ReferenceOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Float2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Float2ReferenceOpenCustomHashMap.this.mask;
            float curr = key[pos];
            if (Float.floatToIntBits(curr) == 0) {
                return false;
            }
            if (Float2ReferenceOpenCustomHashMap.this.strategy.equals(k, curr)) {
                return Float2ReferenceOpenCustomHashMap.this.value[pos] == v;
            }
            do {
                if (Float.floatToIntBits(curr = key[pos = pos + 1 & Float2ReferenceOpenCustomHashMap.this.mask]) != 0) continue;
                return false;
            } while (!Float2ReferenceOpenCustomHashMap.this.strategy.equals(k, curr));
            return Float2ReferenceOpenCustomHashMap.this.value[pos] == v;
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                return false;
            }
            float k = ((Float)e.getKey()).floatValue();
            Object v = e.getValue();
            if (Float2ReferenceOpenCustomHashMap.this.strategy.equals(k, 0.0f)) {
                if (Float2ReferenceOpenCustomHashMap.this.containsNullKey && Float2ReferenceOpenCustomHashMap.this.value[Float2ReferenceOpenCustomHashMap.this.n] == v) {
                    Float2ReferenceOpenCustomHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            float[] key = Float2ReferenceOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Float2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Float2ReferenceOpenCustomHashMap.this.mask;
            float curr = key[pos];
            if (Float.floatToIntBits(curr) == 0) {
                return false;
            }
            if (Float2ReferenceOpenCustomHashMap.this.strategy.equals(curr, k)) {
                if (Float2ReferenceOpenCustomHashMap.this.value[pos] == v) {
                    Float2ReferenceOpenCustomHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if (Float.floatToIntBits(curr = key[pos = pos + 1 & Float2ReferenceOpenCustomHashMap.this.mask]) != 0) continue;
                return false;
            } while (!Float2ReferenceOpenCustomHashMap.this.strategy.equals(curr, k) || Float2ReferenceOpenCustomHashMap.this.value[pos] != v);
            Float2ReferenceOpenCustomHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Float2ReferenceOpenCustomHashMap.this.size;
        }

        @Override
        public void clear() {
            Float2ReferenceOpenCustomHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Float2ReferenceMap.Entry<V>> consumer) {
            if (Float2ReferenceOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(new AbstractFloat2ReferenceMap.BasicEntry(Float2ReferenceOpenCustomHashMap.this.key[Float2ReferenceOpenCustomHashMap.this.n], Float2ReferenceOpenCustomHashMap.this.value[Float2ReferenceOpenCustomHashMap.this.n]));
            }
            int pos = Float2ReferenceOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Float.floatToIntBits(Float2ReferenceOpenCustomHashMap.this.key[pos]) == 0) continue;
                consumer.accept(new AbstractFloat2ReferenceMap.BasicEntry(Float2ReferenceOpenCustomHashMap.this.key[pos], Float2ReferenceOpenCustomHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Float2ReferenceMap.Entry<V>> consumer) {
            AbstractFloat2ReferenceMap.BasicEntry entry = new AbstractFloat2ReferenceMap.BasicEntry();
            if (Float2ReferenceOpenCustomHashMap.this.containsNullKey) {
                entry.key = Float2ReferenceOpenCustomHashMap.this.key[Float2ReferenceOpenCustomHashMap.this.n];
                entry.value = Float2ReferenceOpenCustomHashMap.this.value[Float2ReferenceOpenCustomHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Float2ReferenceOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Float.floatToIntBits(Float2ReferenceOpenCustomHashMap.this.key[pos]) == 0) continue;
                entry.key = Float2ReferenceOpenCustomHashMap.this.key[pos];
                entry.value = Float2ReferenceOpenCustomHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends Float2ReferenceOpenCustomHashMap<V>
    implements ObjectIterator<Float2ReferenceMap.Entry<V>> {
        private final Float2ReferenceOpenCustomHashMap<V> entry;

        private FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        @Override
        public Float2ReferenceOpenCustomHashMap<V> next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends Float2ReferenceOpenCustomHashMap<V>
    implements ObjectIterator<Float2ReferenceMap.Entry<V>> {
        private Float2ReferenceOpenCustomHashMap<V> entry;

        private EntryIterator() {
            super();
        }

        @Override
        public Float2ReferenceOpenCustomHashMap<V> next() {
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
        FloatArrayList wrapped;

        private MapIterator() {
            this.pos = Float2ReferenceOpenCustomHashMap.this.n;
            this.last = -1;
            this.c = Float2ReferenceOpenCustomHashMap.this.size;
            this.mustReturnNullKey = Float2ReferenceOpenCustomHashMap.this.containsNullKey;
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
                this.last = Float2ReferenceOpenCustomHashMap.this.n;
                return this.last;
            }
            float[] key = Float2ReferenceOpenCustomHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                float k = this.wrapped.getFloat(- this.pos - 1);
                int p = HashCommon.mix(Float2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Float2ReferenceOpenCustomHashMap.this.mask;
                while (!Float2ReferenceOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                    p = p + 1 & Float2ReferenceOpenCustomHashMap.this.mask;
                }
                return p;
            } while (Float.floatToIntBits(key[this.pos]) == 0);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            float[] key = Float2ReferenceOpenCustomHashMap.this.key;
            do {
                float curr;
                int last = pos;
                pos = last + 1 & Float2ReferenceOpenCustomHashMap.this.mask;
                do {
                    if (Float.floatToIntBits(curr = key[pos]) == 0) {
                        key[last] = 0.0f;
                        Float2ReferenceOpenCustomHashMap.this.value[last] = null;
                        return;
                    }
                    int slot = HashCommon.mix(Float2ReferenceOpenCustomHashMap.this.strategy.hashCode(curr)) & Float2ReferenceOpenCustomHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Float2ReferenceOpenCustomHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new FloatArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Float2ReferenceOpenCustomHashMap.this.value[last] = Float2ReferenceOpenCustomHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Float2ReferenceOpenCustomHashMap.this.n) {
                Float2ReferenceOpenCustomHashMap.this.containsNullKey = false;
                Float2ReferenceOpenCustomHashMap.this.value[Float2ReferenceOpenCustomHashMap.this.n] = null;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Float2ReferenceOpenCustomHashMap.this.remove(this.wrapped.getFloat(- this.pos - 1));
                this.last = -1;
                return;
            }
            --Float2ReferenceOpenCustomHashMap.this.size;
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
    implements Float2ReferenceMap.Entry<V>,
    Map.Entry<Float, V> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public float getFloatKey() {
            return Float2ReferenceOpenCustomHashMap.this.key[this.index];
        }

        @Override
        public V getValue() {
            return Float2ReferenceOpenCustomHashMap.this.value[this.index];
        }

        @Override
        public V setValue(V v) {
            Object oldValue = Float2ReferenceOpenCustomHashMap.this.value[this.index];
            Float2ReferenceOpenCustomHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Float getKey() {
            return Float.valueOf(Float2ReferenceOpenCustomHashMap.this.key[this.index]);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Float2ReferenceOpenCustomHashMap.this.strategy.equals(Float2ReferenceOpenCustomHashMap.this.key[this.index], ((Float)e.getKey()).floatValue()) && Float2ReferenceOpenCustomHashMap.this.value[this.index] == e.getValue();
        }

        @Override
        public int hashCode() {
            return Float2ReferenceOpenCustomHashMap.this.strategy.hashCode(Float2ReferenceOpenCustomHashMap.this.key[this.index]) ^ (Float2ReferenceOpenCustomHashMap.this.value[this.index] == null ? 0 : System.identityHashCode(Float2ReferenceOpenCustomHashMap.this.value[this.index]));
        }

        public String toString() {
            return "" + Float2ReferenceOpenCustomHashMap.this.key[this.index] + "=>" + Float2ReferenceOpenCustomHashMap.this.value[this.index];
        }
    }

}

