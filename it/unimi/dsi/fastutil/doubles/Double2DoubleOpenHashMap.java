/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDouble2DoubleMap;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleSet;
import it.unimi.dsi.fastutil.doubles.Double2DoubleMap;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
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

public class Double2DoubleOpenHashMap
extends AbstractDouble2DoubleMap
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient double[] key;
    protected transient double[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Double2DoubleMap.FastEntrySet entries;
    protected transient DoubleSet keys;
    protected transient DoubleCollection values;

    public Double2DoubleOpenHashMap(int expected, float f) {
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
        this.value = new double[this.n + 1];
    }

    public Double2DoubleOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Double2DoubleOpenHashMap() {
        this(16, 0.75f);
    }

    public Double2DoubleOpenHashMap(Map<? extends Double, ? extends Double> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Double2DoubleOpenHashMap(Map<? extends Double, ? extends Double> m) {
        this(m, 0.75f);
    }

    public Double2DoubleOpenHashMap(Double2DoubleMap m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Double2DoubleOpenHashMap(Double2DoubleMap m) {
        this(m, 0.75f);
    }

    public Double2DoubleOpenHashMap(double[] k, double[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Double2DoubleOpenHashMap(double[] k, double[] v) {
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
    public void putAll(Map<? extends Double, ? extends Double> m) {
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

    private void insert(int pos, double k, double v) {
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
    public double put(double k, double v) {
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

    public double addTo(double k, double incr) {
        int pos;
        if (Double.doubleToLongBits(k) == 0L) {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        } else {
            double[] key = this.key;
            pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
            double curr = key[pos];
            if (Double.doubleToLongBits(curr) != 0L) {
                if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
                    return this.addToValue(pos, incr);
                }
                while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
                    if (Double.doubleToLongBits(curr) != Double.doubleToLongBits(k)) continue;
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
        double[] key = this.key;
        do {
            double curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if (Double.doubleToLongBits(curr = key[pos]) == 0L) {
                    key[last] = 0.0;
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
    public double remove(double k) {
        if (Double.doubleToLongBits(k) == 0L) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        double[] key = this.key;
        int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return this.defRetValue;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return this.removeEntry(pos);
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return this.defRetValue;
        } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr));
        return this.removeEntry(pos);
    }

    @Override
    public double get(double k) {
        if (Double.doubleToLongBits(k) == 0L) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        double[] key = this.key;
        int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;
        double curr = key[pos];
        if (Double.doubleToLongBits(curr) == 0L) {
            return this.defRetValue;
        }
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return this.value[pos];
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return this.defRetValue;
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
    public boolean containsValue(double v) {
        double[] value = this.value;
        double[] key = this.key;
        if (this.containsNullKey && Double.doubleToLongBits(value[this.n]) == Double.doubleToLongBits(v)) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (Double.doubleToLongBits(key[i]) == 0L || Double.doubleToLongBits(value[i]) != Double.doubleToLongBits(v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public double getOrDefault(double k, double defaultValue) {
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
    public double putIfAbsent(double k, double v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(double k, double v) {
        if (Double.doubleToLongBits(k) == 0L) {
            if (this.containsNullKey && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[this.n])) {
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
        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr) && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[pos])) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return false;
        } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr) || Double.doubleToLongBits(v) != Double.doubleToLongBits(this.value[pos]));
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(double k, double oldValue, double v) {
        int pos = this.find(k);
        if (pos < 0 || Double.doubleToLongBits(oldValue) != Double.doubleToLongBits(this.value[pos])) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public double replace(double k, double v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        double oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public double computeIfAbsent(double k, DoubleUnaryOperator mappingFunction) {
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
    public double computeIfAbsentNullable(double k, DoubleFunction<? extends Double> mappingFunction) {
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
    public double computeIfPresent(double k, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Double newValue = remappingFunction.apply((Double)k, (Double)this.value[pos]);
        if (newValue == null) {
            if (Double.doubleToLongBits(k) == 0L) {
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
    public double compute(double k, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Double newValue = remappingFunction.apply((Double)k, pos >= 0 ? Double.valueOf(this.value[pos]) : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (Double.doubleToLongBits(k) == 0L) {
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
    public double merge(double k, double v, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Double newValue = remappingFunction.apply((Double)this.value[pos], (Double)v);
        if (newValue == null) {
            if (Double.doubleToLongBits(k) == 0L) {
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
        Arrays.fill(this.key, 0.0);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public Double2DoubleMap.FastEntrySet double2DoubleEntrySet() {
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
    public DoubleCollection values() {
        if (this.values == null) {
            this.values = new AbstractDoubleCollection(){

                @Override
                public DoubleIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Double2DoubleOpenHashMap.this.size;
                }

                @Override
                public boolean contains(double v) {
                    return Double2DoubleOpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Double2DoubleOpenHashMap.this.clear();
                }

                @Override
                public void forEach(DoubleConsumer consumer) {
                    if (Double2DoubleOpenHashMap.this.containsNullKey) {
                        consumer.accept(Double2DoubleOpenHashMap.this.value[Double2DoubleOpenHashMap.this.n]);
                    }
                    int pos = Double2DoubleOpenHashMap.this.n;
                    while (pos-- != 0) {
                        if (Double.doubleToLongBits(Double2DoubleOpenHashMap.this.key[pos]) == 0L) continue;
                        consumer.accept(Double2DoubleOpenHashMap.this.value[pos]);
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
        double[] value = this.value;
        int mask = newN - 1;
        double[] newKey = new double[newN + 1];
        double[] newValue = new double[newN + 1];
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

    public Double2DoubleOpenHashMap clone() {
        Double2DoubleOpenHashMap c;
        try {
            c = (Double2DoubleOpenHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (double[])this.key.clone();
        c.value = (double[])this.value.clone();
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
            h += (t ^= HashCommon.double2int(this.value[i]));
            ++i;
        }
        if (this.containsNullKey) {
            h += HashCommon.double2int(this.value[this.n]);
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        double[] key = this.key;
        double[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeDouble(key[e]);
            s.writeDouble(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new double[this.n + 1];
        double[] key = this.key;
        this.value = new double[this.n + 1];
        double[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            double k = s.readDouble();
            double v = s.readDouble();
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
    extends MapIterator
    implements DoubleIterator {
        public ValueIterator() {
            super();
        }

        @Override
        public double nextDouble() {
            return Double2DoubleOpenHashMap.this.value[this.nextEntry()];
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
            if (Double2DoubleOpenHashMap.this.containsNullKey) {
                consumer.accept(Double2DoubleOpenHashMap.this.key[Double2DoubleOpenHashMap.this.n]);
            }
            int pos = Double2DoubleOpenHashMap.this.n;
            while (pos-- != 0) {
                double k = Double2DoubleOpenHashMap.this.key[pos];
                if (Double.doubleToLongBits(k) == 0L) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Double2DoubleOpenHashMap.this.size;
        }

        @Override
        public boolean contains(double k) {
            return Double2DoubleOpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(double k) {
            int oldSize = Double2DoubleOpenHashMap.this.size;
            Double2DoubleOpenHashMap.this.remove(k);
            return Double2DoubleOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Double2DoubleOpenHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends MapIterator
    implements DoubleIterator {
        public KeyIterator() {
            super();
        }

        @Override
        public double nextDouble() {
            return Double2DoubleOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Double2DoubleMap.Entry>
    implements Double2DoubleMap.FastEntrySet {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Double2DoubleMap.Entry> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Double2DoubleMap.Entry> fastIterator() {
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
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            double k = (Double)e.getKey();
            double v = (Double)e.getValue();
            if (Double.doubleToLongBits(k) == 0L) {
                return Double2DoubleOpenHashMap.this.containsNullKey && Double.doubleToLongBits(Double2DoubleOpenHashMap.this.value[Double2DoubleOpenHashMap.this.n]) == Double.doubleToLongBits(v);
            }
            double[] key = Double2DoubleOpenHashMap.this.key;
            int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & Double2DoubleOpenHashMap.this.mask;
            double curr = key[pos];
            if (Double.doubleToLongBits(curr) == 0L) {
                return false;
            }
            if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                return Double.doubleToLongBits(Double2DoubleOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v);
            }
            do {
                if (Double.doubleToLongBits(curr = key[pos = pos + 1 & Double2DoubleOpenHashMap.this.mask]) != 0L) continue;
                return false;
            } while (Double.doubleToLongBits(k) != Double.doubleToLongBits(curr));
            return Double.doubleToLongBits(Double2DoubleOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v);
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
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            double k = (Double)e.getKey();
            double v = (Double)e.getValue();
            if (Double.doubleToLongBits(k) == 0L) {
                if (Double2DoubleOpenHashMap.this.containsNullKey && Double.doubleToLongBits(Double2DoubleOpenHashMap.this.value[Double2DoubleOpenHashMap.this.n]) == Double.doubleToLongBits(v)) {
                    Double2DoubleOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            double[] key = Double2DoubleOpenHashMap.this.key;
            int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & Double2DoubleOpenHashMap.this.mask;
            double curr = key[pos];
            if (Double.doubleToLongBits(curr) == 0L) {
                return false;
            }
            if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
                if (Double.doubleToLongBits(Double2DoubleOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v)) {
                    Double2DoubleOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if (Double.doubleToLongBits(curr = key[pos = pos + 1 & Double2DoubleOpenHashMap.this.mask]) != 0L) continue;
                return false;
            } while (Double.doubleToLongBits(curr) != Double.doubleToLongBits(k) || Double.doubleToLongBits(Double2DoubleOpenHashMap.this.value[pos]) != Double.doubleToLongBits(v));
            Double2DoubleOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Double2DoubleOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Double2DoubleOpenHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Double2DoubleMap.Entry> consumer) {
            if (Double2DoubleOpenHashMap.this.containsNullKey) {
                consumer.accept(new AbstractDouble2DoubleMap.BasicEntry(Double2DoubleOpenHashMap.this.key[Double2DoubleOpenHashMap.this.n], Double2DoubleOpenHashMap.this.value[Double2DoubleOpenHashMap.this.n]));
            }
            int pos = Double2DoubleOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Double.doubleToLongBits(Double2DoubleOpenHashMap.this.key[pos]) == 0L) continue;
                consumer.accept(new AbstractDouble2DoubleMap.BasicEntry(Double2DoubleOpenHashMap.this.key[pos], Double2DoubleOpenHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Double2DoubleMap.Entry> consumer) {
            AbstractDouble2DoubleMap.BasicEntry entry = new AbstractDouble2DoubleMap.BasicEntry();
            if (Double2DoubleOpenHashMap.this.containsNullKey) {
                entry.key = Double2DoubleOpenHashMap.this.key[Double2DoubleOpenHashMap.this.n];
                entry.value = Double2DoubleOpenHashMap.this.value[Double2DoubleOpenHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Double2DoubleOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Double.doubleToLongBits(Double2DoubleOpenHashMap.this.key[pos]) == 0L) continue;
                entry.key = Double2DoubleOpenHashMap.this.key[pos];
                entry.value = Double2DoubleOpenHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends MapIterator
    implements ObjectIterator<Double2DoubleMap.Entry> {
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
    implements ObjectIterator<Double2DoubleMap.Entry> {
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
        DoubleArrayList wrapped;

        private MapIterator() {
            this.pos = Double2DoubleOpenHashMap.this.n;
            this.last = -1;
            this.c = Double2DoubleOpenHashMap.this.size;
            this.mustReturnNullKey = Double2DoubleOpenHashMap.this.containsNullKey;
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
                this.last = Double2DoubleOpenHashMap.this.n;
                return this.last;
            }
            double[] key = Double2DoubleOpenHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                double k = this.wrapped.getDouble(- this.pos - 1);
                int p = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & Double2DoubleOpenHashMap.this.mask;
                while (Double.doubleToLongBits(k) != Double.doubleToLongBits(key[p])) {
                    p = p + 1 & Double2DoubleOpenHashMap.this.mask;
                }
                return p;
            } while (Double.doubleToLongBits(key[this.pos]) == 0L);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            double[] key = Double2DoubleOpenHashMap.this.key;
            do {
                double curr;
                int last = pos;
                pos = last + 1 & Double2DoubleOpenHashMap.this.mask;
                do {
                    if (Double.doubleToLongBits(curr = key[pos]) == 0L) {
                        key[last] = 0.0;
                        return;
                    }
                    int slot = (int)HashCommon.mix(Double.doubleToRawLongBits(curr)) & Double2DoubleOpenHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Double2DoubleOpenHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new DoubleArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Double2DoubleOpenHashMap.this.value[last] = Double2DoubleOpenHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Double2DoubleOpenHashMap.this.n) {
                Double2DoubleOpenHashMap.this.containsNullKey = false;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Double2DoubleOpenHashMap.this.remove(this.wrapped.getDouble(- this.pos - 1));
                this.last = -1;
                return;
            }
            --Double2DoubleOpenHashMap.this.size;
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
    implements Double2DoubleMap.Entry,
    Map.Entry<Double, Double> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public double getDoubleKey() {
            return Double2DoubleOpenHashMap.this.key[this.index];
        }

        @Override
        public double getDoubleValue() {
            return Double2DoubleOpenHashMap.this.value[this.index];
        }

        @Override
        public double setValue(double v) {
            double oldValue = Double2DoubleOpenHashMap.this.value[this.index];
            Double2DoubleOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Double getKey() {
            return Double2DoubleOpenHashMap.this.key[this.index];
        }

        @Deprecated
        @Override
        public Double getValue() {
            return Double2DoubleOpenHashMap.this.value[this.index];
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
            return Double.doubleToLongBits(Double2DoubleOpenHashMap.this.key[this.index]) == Double.doubleToLongBits((Double)e.getKey()) && Double.doubleToLongBits(Double2DoubleOpenHashMap.this.value[this.index]) == Double.doubleToLongBits((Double)e.getValue());
        }

        @Override
        public int hashCode() {
            return HashCommon.double2int(Double2DoubleOpenHashMap.this.key[this.index]) ^ HashCommon.double2int(Double2DoubleOpenHashMap.this.value[this.index]);
        }

        public String toString() {
            return "" + Double2DoubleOpenHashMap.this.key[this.index] + "=>" + Double2DoubleOpenHashMap.this.value[this.index];
        }
    }

}

