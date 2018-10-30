/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloat2LongMap;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.Float2LongMap;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatHash;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
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
import java.util.function.DoubleToLongFunction;
import java.util.function.LongConsumer;

public class Float2LongOpenCustomHashMap
extends AbstractFloat2LongMap
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient float[] key;
    protected transient long[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected FloatHash.Strategy strategy;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Float2LongMap.FastEntrySet entries;
    protected transient FloatSet keys;
    protected transient LongCollection values;

    public Float2LongOpenCustomHashMap(int expected, float f, FloatHash.Strategy strategy) {
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
        this.value = new long[this.n + 1];
    }

    public Float2LongOpenCustomHashMap(int expected, FloatHash.Strategy strategy) {
        this(expected, 0.75f, strategy);
    }

    public Float2LongOpenCustomHashMap(FloatHash.Strategy strategy) {
        this(16, 0.75f, strategy);
    }

    public Float2LongOpenCustomHashMap(Map<? extends Float, ? extends Long> m, float f, FloatHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Float2LongOpenCustomHashMap(Map<? extends Float, ? extends Long> m, FloatHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }

    public Float2LongOpenCustomHashMap(Float2LongMap m, float f, FloatHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Float2LongOpenCustomHashMap(Float2LongMap m, FloatHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }

    public Float2LongOpenCustomHashMap(float[] k, long[] v, float f, FloatHash.Strategy strategy) {
        this(k.length, f, strategy);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Float2LongOpenCustomHashMap(float[] k, long[] v, FloatHash.Strategy strategy) {
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

    private long removeEntry(int pos) {
        long oldValue = this.value[pos];
        --this.size;
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private long removeNullEntry() {
        this.containsNullKey = false;
        long oldValue = this.value[this.n];
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Long> m) {
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

    private void insert(int pos, float k, long v) {
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
    public long put(float k, long v) {
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return this.defRetValue;
        }
        long oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    private long addToValue(int pos, long incr) {
        long oldValue = this.value[pos];
        this.value[pos] = oldValue + incr;
        return oldValue;
    }

    public long addTo(float k, long incr) {
        int pos;
        if (this.strategy.equals(k, 0.0f)) {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        } else {
            float[] key = this.key;
            pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
            float curr = key[pos];
            if (Float.floatToIntBits(curr) != 0) {
                if (this.strategy.equals(curr, k)) {
                    return this.addToValue(pos, incr);
                }
                while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
                    if (!this.strategy.equals(curr, k)) continue;
                    return this.addToValue(pos, incr);
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = this.defRetValue + incr;
        if (this.size++ >= this.maxFill) {
            this.rehash(HashCommon.arraySize(this.size + 1, this.f));
        }
        return this.defRetValue;
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
    public long remove(float k) {
        if (this.strategy.equals(k, 0.0f)) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        float[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        float curr = key[pos];
        if (Float.floatToIntBits(curr) == 0) {
            return this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.removeEntry(pos);
        }
        do {
            if (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        return this.removeEntry(pos);
    }

    @Override
    public long get(float k) {
        if (this.strategy.equals(k, 0.0f)) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        float[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        float curr = key[pos];
        if (Float.floatToIntBits(curr) == 0) {
            return this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        do {
            if (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return this.defRetValue;
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
    public boolean containsValue(long v) {
        long[] value = this.value;
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
    public long getOrDefault(float k, long defaultValue) {
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
    public long putIfAbsent(float k, long v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(float k, long v) {
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
    public boolean replace(float k, long oldValue, long v) {
        int pos = this.find(k);
        if (pos < 0 || oldValue != this.value[pos]) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public long replace(float k, long v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        long oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public long computeIfAbsent(float k, DoubleToLongFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        long newValue = mappingFunction.applyAsLong(k);
        this.insert(- pos - 1, k, newValue);
        return newValue;
    }

    @Override
    public long computeIfAbsentNullable(float k, DoubleFunction<? extends Long> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        Long newValue = mappingFunction.apply(k);
        if (newValue == null) {
            return this.defRetValue;
        }
        long v = newValue;
        this.insert(- pos - 1, k, v);
        return v;
    }

    @Override
    public long computeIfPresent(float k, BiFunction<? super Float, ? super Long, ? extends Long> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Long newValue = remappingFunction.apply(Float.valueOf(k), (Long)this.value[pos]);
        if (newValue == null) {
            if (this.strategy.equals(k, 0.0f)) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return this.defRetValue;
        }
        this.value[pos] = newValue;
        return this.value[pos];
    }

    @Override
    public long compute(float k, BiFunction<? super Float, ? super Long, ? extends Long> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Long newValue = remappingFunction.apply(Float.valueOf(k), pos >= 0 ? Long.valueOf(this.value[pos]) : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (this.strategy.equals(k, 0.0f)) {
                    this.removeNullEntry();
                } else {
                    this.removeEntry(pos);
                }
            }
            return this.defRetValue;
        }
        long newVal = newValue;
        if (pos < 0) {
            this.insert(- pos - 1, k, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public long merge(float k, long v, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Long newValue = remappingFunction.apply((Long)this.value[pos], (Long)v);
        if (newValue == null) {
            if (this.strategy.equals(k, 0.0f)) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return this.defRetValue;
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
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public Float2LongMap.FastEntrySet float2LongEntrySet() {
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
    public LongCollection values() {
        if (this.values == null) {
            this.values = new AbstractLongCollection(){

                @Override
                public LongIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Float2LongOpenCustomHashMap.this.size;
                }

                @Override
                public boolean contains(long v) {
                    return Float2LongOpenCustomHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Float2LongOpenCustomHashMap.this.clear();
                }

                @Override
                public void forEach(LongConsumer consumer) {
                    if (Float2LongOpenCustomHashMap.this.containsNullKey) {
                        consumer.accept(Float2LongOpenCustomHashMap.this.value[Float2LongOpenCustomHashMap.this.n]);
                    }
                    int pos = Float2LongOpenCustomHashMap.this.n;
                    while (pos-- != 0) {
                        if (Float.floatToIntBits(Float2LongOpenCustomHashMap.this.key[pos]) == 0) continue;
                        consumer.accept(Float2LongOpenCustomHashMap.this.value[pos]);
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
        long[] value = this.value;
        int mask = newN - 1;
        float[] newKey = new float[newN + 1];
        long[] newValue = new long[newN + 1];
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

    public Float2LongOpenCustomHashMap clone() {
        Float2LongOpenCustomHashMap c;
        try {
            c = (Float2LongOpenCustomHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (float[])this.key.clone();
        c.value = (long[])this.value.clone();
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
            h += (t ^= HashCommon.long2int(this.value[i]));
            ++i;
        }
        if (this.containsNullKey) {
            h += HashCommon.long2int(this.value[this.n]);
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        float[] key = this.key;
        long[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeFloat(key[e]);
            s.writeLong(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new float[this.n + 1];
        float[] key = this.key;
        this.value = new long[this.n + 1];
        long[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            float k = s.readFloat();
            long v = s.readLong();
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
    extends MapIterator
    implements LongIterator {
        public ValueIterator() {
            super();
        }

        @Override
        public long nextLong() {
            return Float2LongOpenCustomHashMap.this.value[this.nextEntry()];
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
            if (Float2LongOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(Float2LongOpenCustomHashMap.this.key[Float2LongOpenCustomHashMap.this.n]);
            }
            int pos = Float2LongOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                float k = Float2LongOpenCustomHashMap.this.key[pos];
                if (Float.floatToIntBits(k) == 0) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Float2LongOpenCustomHashMap.this.size;
        }

        @Override
        public boolean contains(float k) {
            return Float2LongOpenCustomHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(float k) {
            int oldSize = Float2LongOpenCustomHashMap.this.size;
            Float2LongOpenCustomHashMap.this.remove(k);
            return Float2LongOpenCustomHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Float2LongOpenCustomHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends MapIterator
    implements FloatIterator {
        public KeyIterator() {
            super();
        }

        @Override
        public float nextFloat() {
            return Float2LongOpenCustomHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Float2LongMap.Entry>
    implements Float2LongMap.FastEntrySet {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Float2LongMap.Entry> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Float2LongMap.Entry> fastIterator() {
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
            if (e.getValue() == null || !(e.getValue() instanceof Long)) {
                return false;
            }
            float k = ((Float)e.getKey()).floatValue();
            long v = (Long)e.getValue();
            if (Float2LongOpenCustomHashMap.this.strategy.equals(k, 0.0f)) {
                return Float2LongOpenCustomHashMap.this.containsNullKey && Float2LongOpenCustomHashMap.this.value[Float2LongOpenCustomHashMap.this.n] == v;
            }
            float[] key = Float2LongOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Float2LongOpenCustomHashMap.this.strategy.hashCode(k)) & Float2LongOpenCustomHashMap.this.mask;
            float curr = key[pos];
            if (Float.floatToIntBits(curr) == 0) {
                return false;
            }
            if (Float2LongOpenCustomHashMap.this.strategy.equals(k, curr)) {
                return Float2LongOpenCustomHashMap.this.value[pos] == v;
            }
            do {
                if (Float.floatToIntBits(curr = key[pos = pos + 1 & Float2LongOpenCustomHashMap.this.mask]) != 0) continue;
                return false;
            } while (!Float2LongOpenCustomHashMap.this.strategy.equals(k, curr));
            return Float2LongOpenCustomHashMap.this.value[pos] == v;
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
            if (e.getValue() == null || !(e.getValue() instanceof Long)) {
                return false;
            }
            float k = ((Float)e.getKey()).floatValue();
            long v = (Long)e.getValue();
            if (Float2LongOpenCustomHashMap.this.strategy.equals(k, 0.0f)) {
                if (Float2LongOpenCustomHashMap.this.containsNullKey && Float2LongOpenCustomHashMap.this.value[Float2LongOpenCustomHashMap.this.n] == v) {
                    Float2LongOpenCustomHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            float[] key = Float2LongOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Float2LongOpenCustomHashMap.this.strategy.hashCode(k)) & Float2LongOpenCustomHashMap.this.mask;
            float curr = key[pos];
            if (Float.floatToIntBits(curr) == 0) {
                return false;
            }
            if (Float2LongOpenCustomHashMap.this.strategy.equals(curr, k)) {
                if (Float2LongOpenCustomHashMap.this.value[pos] == v) {
                    Float2LongOpenCustomHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if (Float.floatToIntBits(curr = key[pos = pos + 1 & Float2LongOpenCustomHashMap.this.mask]) != 0) continue;
                return false;
            } while (!Float2LongOpenCustomHashMap.this.strategy.equals(curr, k) || Float2LongOpenCustomHashMap.this.value[pos] != v);
            Float2LongOpenCustomHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Float2LongOpenCustomHashMap.this.size;
        }

        @Override
        public void clear() {
            Float2LongOpenCustomHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Float2LongMap.Entry> consumer) {
            if (Float2LongOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(new AbstractFloat2LongMap.BasicEntry(Float2LongOpenCustomHashMap.this.key[Float2LongOpenCustomHashMap.this.n], Float2LongOpenCustomHashMap.this.value[Float2LongOpenCustomHashMap.this.n]));
            }
            int pos = Float2LongOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Float.floatToIntBits(Float2LongOpenCustomHashMap.this.key[pos]) == 0) continue;
                consumer.accept(new AbstractFloat2LongMap.BasicEntry(Float2LongOpenCustomHashMap.this.key[pos], Float2LongOpenCustomHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Float2LongMap.Entry> consumer) {
            AbstractFloat2LongMap.BasicEntry entry = new AbstractFloat2LongMap.BasicEntry();
            if (Float2LongOpenCustomHashMap.this.containsNullKey) {
                entry.key = Float2LongOpenCustomHashMap.this.key[Float2LongOpenCustomHashMap.this.n];
                entry.value = Float2LongOpenCustomHashMap.this.value[Float2LongOpenCustomHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Float2LongOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Float.floatToIntBits(Float2LongOpenCustomHashMap.this.key[pos]) == 0) continue;
                entry.key = Float2LongOpenCustomHashMap.this.key[pos];
                entry.value = Float2LongOpenCustomHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends MapIterator
    implements ObjectIterator<Float2LongMap.Entry> {
        private final MapEntry entry;

        private FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        @Override
        public MapEntry next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends MapIterator
    implements ObjectIterator<Float2LongMap.Entry> {
        private MapEntry entry;

        private EntryIterator() {
            super();
        }

        @Override
        public MapEntry next() {
            this.entry = new MapEntry(this.nextEntry());
            return this.entry;
        }

        @Override
        public void remove() {
            super.remove();
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
            this.pos = Float2LongOpenCustomHashMap.this.n;
            this.last = -1;
            this.c = Float2LongOpenCustomHashMap.this.size;
            this.mustReturnNullKey = Float2LongOpenCustomHashMap.this.containsNullKey;
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
                this.last = Float2LongOpenCustomHashMap.this.n;
                return this.last;
            }
            float[] key = Float2LongOpenCustomHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                float k = this.wrapped.getFloat(- this.pos - 1);
                int p = HashCommon.mix(Float2LongOpenCustomHashMap.this.strategy.hashCode(k)) & Float2LongOpenCustomHashMap.this.mask;
                while (!Float2LongOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                    p = p + 1 & Float2LongOpenCustomHashMap.this.mask;
                }
                return p;
            } while (Float.floatToIntBits(key[this.pos]) == 0);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            float[] key = Float2LongOpenCustomHashMap.this.key;
            do {
                float curr;
                int last = pos;
                pos = last + 1 & Float2LongOpenCustomHashMap.this.mask;
                do {
                    if (Float.floatToIntBits(curr = key[pos]) == 0) {
                        key[last] = 0.0f;
                        return;
                    }
                    int slot = HashCommon.mix(Float2LongOpenCustomHashMap.this.strategy.hashCode(curr)) & Float2LongOpenCustomHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Float2LongOpenCustomHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new FloatArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Float2LongOpenCustomHashMap.this.value[last] = Float2LongOpenCustomHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Float2LongOpenCustomHashMap.this.n) {
                Float2LongOpenCustomHashMap.this.containsNullKey = false;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Float2LongOpenCustomHashMap.this.remove(this.wrapped.getFloat(- this.pos - 1));
                this.last = -1;
                return;
            }
            --Float2LongOpenCustomHashMap.this.size;
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
    implements Float2LongMap.Entry,
    Map.Entry<Float, Long> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public float getFloatKey() {
            return Float2LongOpenCustomHashMap.this.key[this.index];
        }

        @Override
        public long getLongValue() {
            return Float2LongOpenCustomHashMap.this.value[this.index];
        }

        @Override
        public long setValue(long v) {
            long oldValue = Float2LongOpenCustomHashMap.this.value[this.index];
            Float2LongOpenCustomHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Float getKey() {
            return Float.valueOf(Float2LongOpenCustomHashMap.this.key[this.index]);
        }

        @Deprecated
        @Override
        public Long getValue() {
            return Float2LongOpenCustomHashMap.this.value[this.index];
        }

        @Deprecated
        @Override
        public Long setValue(Long v) {
            return this.setValue((long)v);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Float2LongOpenCustomHashMap.this.strategy.equals(Float2LongOpenCustomHashMap.this.key[this.index], ((Float)e.getKey()).floatValue()) && Float2LongOpenCustomHashMap.this.value[this.index] == (Long)e.getValue();
        }

        @Override
        public int hashCode() {
            return Float2LongOpenCustomHashMap.this.strategy.hashCode(Float2LongOpenCustomHashMap.this.key[this.index]) ^ HashCommon.long2int(Float2LongOpenCustomHashMap.this.value[this.index]);
        }

        public String toString() {
            return "" + Float2LongOpenCustomHashMap.this.key[this.index] + "=>" + Float2LongOpenCustomHashMap.this.value[this.index];
        }
    }

}

