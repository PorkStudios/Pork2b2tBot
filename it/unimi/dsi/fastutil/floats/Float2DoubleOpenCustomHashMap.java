/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.floats.AbstractFloat2DoubleMap;
import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.Float2DoubleMap;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatHash;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSet;
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
import java.util.function.DoubleUnaryOperator;

public class Float2DoubleOpenCustomHashMap
extends AbstractFloat2DoubleMap
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient float[] key;
    protected transient double[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected FloatHash.Strategy strategy;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Float2DoubleMap.FastEntrySet entries;
    protected transient FloatSet keys;
    protected transient DoubleCollection values;

    public Float2DoubleOpenCustomHashMap(int expected, float f, FloatHash.Strategy strategy) {
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
        this.value = new double[this.n + 1];
    }

    public Float2DoubleOpenCustomHashMap(int expected, FloatHash.Strategy strategy) {
        this(expected, 0.75f, strategy);
    }

    public Float2DoubleOpenCustomHashMap(FloatHash.Strategy strategy) {
        this(16, 0.75f, strategy);
    }

    public Float2DoubleOpenCustomHashMap(Map<? extends Float, ? extends Double> m, float f, FloatHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Float2DoubleOpenCustomHashMap(Map<? extends Float, ? extends Double> m, FloatHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }

    public Float2DoubleOpenCustomHashMap(Float2DoubleMap m, float f, FloatHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Float2DoubleOpenCustomHashMap(Float2DoubleMap m, FloatHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }

    public Float2DoubleOpenCustomHashMap(float[] k, double[] v, float f, FloatHash.Strategy strategy) {
        this(k.length, f, strategy);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Float2DoubleOpenCustomHashMap(float[] k, double[] v, FloatHash.Strategy strategy) {
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

    private double removeEntry(int pos) {
        double oldValue = this.value[pos];
        --this.size;
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private double removeNullEntry() {
        this.containsNullKey = false;
        double oldValue = this.value[this.n];
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends Float, ? extends Double> m) {
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

    private void insert(int pos, float k, double v) {
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
    public double put(float k, double v) {
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return this.defRetValue;
        }
        double oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    private double addToValue(int pos, double incr) {
        double oldValue = this.value[pos];
        this.value[pos] = oldValue + incr;
        return oldValue;
    }

    public double addTo(float k, double incr) {
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
    public double remove(float k) {
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
    public double get(float k) {
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
    public boolean containsValue(double v) {
        double[] value = this.value;
        float[] key = this.key;
        if (this.containsNullKey && Double.doubleToLongBits(value[this.n]) == Double.doubleToLongBits(v)) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (Float.floatToIntBits(key[i]) == 0 || Double.doubleToLongBits(value[i]) != Double.doubleToLongBits(v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public double getOrDefault(float k, double defaultValue) {
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
    public double putIfAbsent(float k, double v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(float k, double v) {
        if (this.strategy.equals(k, 0.0f)) {
            if (this.containsNullKey && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[this.n])) {
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
        if (this.strategy.equals(k, curr) && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[pos])) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (!this.strategy.equals(k, curr) || Double.doubleToLongBits(v) != Double.doubleToLongBits(this.value[pos]));
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(float k, double oldValue, double v) {
        int pos = this.find(k);
        if (pos < 0 || Double.doubleToLongBits(oldValue) != Double.doubleToLongBits(this.value[pos])) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public double replace(float k, double v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        double oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public double computeIfAbsent(float k, DoubleUnaryOperator mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        double newValue = mappingFunction.applyAsDouble(k);
        this.insert(- pos - 1, k, newValue);
        return newValue;
    }

    @Override
    public double computeIfAbsentNullable(float k, DoubleFunction<? extends Double> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        Double newValue = mappingFunction.apply(k);
        if (newValue == null) {
            return this.defRetValue;
        }
        double v = newValue;
        this.insert(- pos - 1, k, v);
        return v;
    }

    @Override
    public double computeIfPresent(float k, BiFunction<? super Float, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Double newValue = remappingFunction.apply(Float.valueOf(k), (Double)this.value[pos]);
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
    public double compute(float k, BiFunction<? super Float, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Double newValue = remappingFunction.apply(Float.valueOf(k), pos >= 0 ? Double.valueOf(this.value[pos]) : null);
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
        double newVal = newValue;
        if (pos < 0) {
            this.insert(- pos - 1, k, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public double merge(float k, double v, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Double newValue = remappingFunction.apply((Double)this.value[pos], (Double)v);
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

    public Float2DoubleMap.FastEntrySet float2DoubleEntrySet() {
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
    public DoubleCollection values() {
        if (this.values == null) {
            this.values = new AbstractDoubleCollection(){

                @Override
                public DoubleIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Float2DoubleOpenCustomHashMap.this.size;
                }

                @Override
                public boolean contains(double v) {
                    return Float2DoubleOpenCustomHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Float2DoubleOpenCustomHashMap.this.clear();
                }

                @Override
                public void forEach(DoubleConsumer consumer) {
                    if (Float2DoubleOpenCustomHashMap.this.containsNullKey) {
                        consumer.accept(Float2DoubleOpenCustomHashMap.this.value[Float2DoubleOpenCustomHashMap.this.n]);
                    }
                    int pos = Float2DoubleOpenCustomHashMap.this.n;
                    while (pos-- != 0) {
                        if (Float.floatToIntBits(Float2DoubleOpenCustomHashMap.this.key[pos]) == 0) continue;
                        consumer.accept(Float2DoubleOpenCustomHashMap.this.value[pos]);
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
        double[] value = this.value;
        int mask = newN - 1;
        float[] newKey = new float[newN + 1];
        double[] newValue = new double[newN + 1];
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

    public Float2DoubleOpenCustomHashMap clone() {
        Float2DoubleOpenCustomHashMap c;
        try {
            c = (Float2DoubleOpenCustomHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (float[])this.key.clone();
        c.value = (double[])this.value.clone();
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
            h += (t ^= HashCommon.double2int(this.value[i]));
            ++i;
        }
        if (this.containsNullKey) {
            h += HashCommon.double2int(this.value[this.n]);
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        float[] key = this.key;
        double[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeFloat(key[e]);
            s.writeDouble(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new float[this.n + 1];
        float[] key = this.key;
        this.value = new double[this.n + 1];
        double[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            float k = s.readFloat();
            double v = s.readDouble();
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
    implements DoubleIterator {
        public ValueIterator() {
            super();
        }

        @Override
        public double nextDouble() {
            return Float2DoubleOpenCustomHashMap.this.value[this.nextEntry()];
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
            if (Float2DoubleOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(Float2DoubleOpenCustomHashMap.this.key[Float2DoubleOpenCustomHashMap.this.n]);
            }
            int pos = Float2DoubleOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                float k = Float2DoubleOpenCustomHashMap.this.key[pos];
                if (Float.floatToIntBits(k) == 0) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Float2DoubleOpenCustomHashMap.this.size;
        }

        @Override
        public boolean contains(float k) {
            return Float2DoubleOpenCustomHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(float k) {
            int oldSize = Float2DoubleOpenCustomHashMap.this.size;
            Float2DoubleOpenCustomHashMap.this.remove(k);
            return Float2DoubleOpenCustomHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Float2DoubleOpenCustomHashMap.this.clear();
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
            return Float2DoubleOpenCustomHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Float2DoubleMap.Entry>
    implements Float2DoubleMap.FastEntrySet {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Float2DoubleMap.Entry> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Float2DoubleMap.Entry> fastIterator() {
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
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            float k = ((Float)e.getKey()).floatValue();
            double v = (Double)e.getValue();
            if (Float2DoubleOpenCustomHashMap.this.strategy.equals(k, 0.0f)) {
                return Float2DoubleOpenCustomHashMap.this.containsNullKey && Double.doubleToLongBits(Float2DoubleOpenCustomHashMap.this.value[Float2DoubleOpenCustomHashMap.this.n]) == Double.doubleToLongBits(v);
            }
            float[] key = Float2DoubleOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Float2DoubleOpenCustomHashMap.this.strategy.hashCode(k)) & Float2DoubleOpenCustomHashMap.this.mask;
            float curr = key[pos];
            if (Float.floatToIntBits(curr) == 0) {
                return false;
            }
            if (Float2DoubleOpenCustomHashMap.this.strategy.equals(k, curr)) {
                return Double.doubleToLongBits(Float2DoubleOpenCustomHashMap.this.value[pos]) == Double.doubleToLongBits(v);
            }
            do {
                if (Float.floatToIntBits(curr = key[pos = pos + 1 & Float2DoubleOpenCustomHashMap.this.mask]) != 0) continue;
                return false;
            } while (!Float2DoubleOpenCustomHashMap.this.strategy.equals(k, curr));
            return Double.doubleToLongBits(Float2DoubleOpenCustomHashMap.this.value[pos]) == Double.doubleToLongBits(v);
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
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            float k = ((Float)e.getKey()).floatValue();
            double v = (Double)e.getValue();
            if (Float2DoubleOpenCustomHashMap.this.strategy.equals(k, 0.0f)) {
                if (Float2DoubleOpenCustomHashMap.this.containsNullKey && Double.doubleToLongBits(Float2DoubleOpenCustomHashMap.this.value[Float2DoubleOpenCustomHashMap.this.n]) == Double.doubleToLongBits(v)) {
                    Float2DoubleOpenCustomHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            float[] key = Float2DoubleOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Float2DoubleOpenCustomHashMap.this.strategy.hashCode(k)) & Float2DoubleOpenCustomHashMap.this.mask;
            float curr = key[pos];
            if (Float.floatToIntBits(curr) == 0) {
                return false;
            }
            if (Float2DoubleOpenCustomHashMap.this.strategy.equals(curr, k)) {
                if (Double.doubleToLongBits(Float2DoubleOpenCustomHashMap.this.value[pos]) == Double.doubleToLongBits(v)) {
                    Float2DoubleOpenCustomHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if (Float.floatToIntBits(curr = key[pos = pos + 1 & Float2DoubleOpenCustomHashMap.this.mask]) != 0) continue;
                return false;
            } while (!Float2DoubleOpenCustomHashMap.this.strategy.equals(curr, k) || Double.doubleToLongBits(Float2DoubleOpenCustomHashMap.this.value[pos]) != Double.doubleToLongBits(v));
            Float2DoubleOpenCustomHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Float2DoubleOpenCustomHashMap.this.size;
        }

        @Override
        public void clear() {
            Float2DoubleOpenCustomHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Float2DoubleMap.Entry> consumer) {
            if (Float2DoubleOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(new AbstractFloat2DoubleMap.BasicEntry(Float2DoubleOpenCustomHashMap.this.key[Float2DoubleOpenCustomHashMap.this.n], Float2DoubleOpenCustomHashMap.this.value[Float2DoubleOpenCustomHashMap.this.n]));
            }
            int pos = Float2DoubleOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Float.floatToIntBits(Float2DoubleOpenCustomHashMap.this.key[pos]) == 0) continue;
                consumer.accept(new AbstractFloat2DoubleMap.BasicEntry(Float2DoubleOpenCustomHashMap.this.key[pos], Float2DoubleOpenCustomHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Float2DoubleMap.Entry> consumer) {
            AbstractFloat2DoubleMap.BasicEntry entry = new AbstractFloat2DoubleMap.BasicEntry();
            if (Float2DoubleOpenCustomHashMap.this.containsNullKey) {
                entry.key = Float2DoubleOpenCustomHashMap.this.key[Float2DoubleOpenCustomHashMap.this.n];
                entry.value = Float2DoubleOpenCustomHashMap.this.value[Float2DoubleOpenCustomHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Float2DoubleOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Float.floatToIntBits(Float2DoubleOpenCustomHashMap.this.key[pos]) == 0) continue;
                entry.key = Float2DoubleOpenCustomHashMap.this.key[pos];
                entry.value = Float2DoubleOpenCustomHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends MapIterator
    implements ObjectIterator<Float2DoubleMap.Entry> {
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
    implements ObjectIterator<Float2DoubleMap.Entry> {
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
            this.pos = Float2DoubleOpenCustomHashMap.this.n;
            this.last = -1;
            this.c = Float2DoubleOpenCustomHashMap.this.size;
            this.mustReturnNullKey = Float2DoubleOpenCustomHashMap.this.containsNullKey;
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
                this.last = Float2DoubleOpenCustomHashMap.this.n;
                return this.last;
            }
            float[] key = Float2DoubleOpenCustomHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                float k = this.wrapped.getFloat(- this.pos - 1);
                int p = HashCommon.mix(Float2DoubleOpenCustomHashMap.this.strategy.hashCode(k)) & Float2DoubleOpenCustomHashMap.this.mask;
                while (!Float2DoubleOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                    p = p + 1 & Float2DoubleOpenCustomHashMap.this.mask;
                }
                return p;
            } while (Float.floatToIntBits(key[this.pos]) == 0);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            float[] key = Float2DoubleOpenCustomHashMap.this.key;
            do {
                float curr;
                int last = pos;
                pos = last + 1 & Float2DoubleOpenCustomHashMap.this.mask;
                do {
                    if (Float.floatToIntBits(curr = key[pos]) == 0) {
                        key[last] = 0.0f;
                        return;
                    }
                    int slot = HashCommon.mix(Float2DoubleOpenCustomHashMap.this.strategy.hashCode(curr)) & Float2DoubleOpenCustomHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Float2DoubleOpenCustomHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new FloatArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Float2DoubleOpenCustomHashMap.this.value[last] = Float2DoubleOpenCustomHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Float2DoubleOpenCustomHashMap.this.n) {
                Float2DoubleOpenCustomHashMap.this.containsNullKey = false;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Float2DoubleOpenCustomHashMap.this.remove(this.wrapped.getFloat(- this.pos - 1));
                this.last = -1;
                return;
            }
            --Float2DoubleOpenCustomHashMap.this.size;
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
    implements Float2DoubleMap.Entry,
    Map.Entry<Float, Double> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public float getFloatKey() {
            return Float2DoubleOpenCustomHashMap.this.key[this.index];
        }

        @Override
        public double getDoubleValue() {
            return Float2DoubleOpenCustomHashMap.this.value[this.index];
        }

        @Override
        public double setValue(double v) {
            double oldValue = Float2DoubleOpenCustomHashMap.this.value[this.index];
            Float2DoubleOpenCustomHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Float getKey() {
            return Float.valueOf(Float2DoubleOpenCustomHashMap.this.key[this.index]);
        }

        @Deprecated
        @Override
        public Double getValue() {
            return Float2DoubleOpenCustomHashMap.this.value[this.index];
        }

        @Deprecated
        @Override
        public Double setValue(Double v) {
            return this.setValue((double)v);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Float2DoubleOpenCustomHashMap.this.strategy.equals(Float2DoubleOpenCustomHashMap.this.key[this.index], ((Float)e.getKey()).floatValue()) && Double.doubleToLongBits(Float2DoubleOpenCustomHashMap.this.value[this.index]) == Double.doubleToLongBits((Double)e.getValue());
        }

        @Override
        public int hashCode() {
            return Float2DoubleOpenCustomHashMap.this.strategy.hashCode(Float2DoubleOpenCustomHashMap.this.key[this.index]) ^ HashCommon.double2int(Float2DoubleOpenCustomHashMap.this.value[this.index]);
        }

        public String toString() {
            return "" + Float2DoubleOpenCustomHashMap.this.key[this.index] + "=>" + Float2DoubleOpenCustomHashMap.this.value[this.index];
        }
    }

}

