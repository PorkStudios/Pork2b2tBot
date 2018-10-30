/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.longs.AbstractLong2DoubleMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
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
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongToDoubleFunction;

public class Long2DoubleOpenHashMap
extends AbstractLong2DoubleMap
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient long[] key;
    protected transient double[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Long2DoubleMap.FastEntrySet entries;
    protected transient LongSet keys;
    protected transient DoubleCollection values;

    public Long2DoubleOpenHashMap(int expected, float f) {
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
        this.key = new long[this.n + 1];
        this.value = new double[this.n + 1];
    }

    public Long2DoubleOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Long2DoubleOpenHashMap() {
        this(16, 0.75f);
    }

    public Long2DoubleOpenHashMap(Map<? extends Long, ? extends Double> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Long2DoubleOpenHashMap(Map<? extends Long, ? extends Double> m) {
        this(m, 0.75f);
    }

    public Long2DoubleOpenHashMap(Long2DoubleMap m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Long2DoubleOpenHashMap(Long2DoubleMap m) {
        this(m, 0.75f);
    }

    public Long2DoubleOpenHashMap(long[] k, double[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Long2DoubleOpenHashMap(long[] k, double[] v) {
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
    public void putAll(Map<? extends Long, ? extends Double> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }

    private int find(long k) {
        if (k == 0L) {
            return this.containsNullKey ? this.n : - this.n + 1;
        }
        long[] key = this.key;
        int pos = (int)HashCommon.mix(k) & this.mask;
        long curr = key[pos];
        if (curr == 0L) {
            return - pos + 1;
        }
        if (k == curr) {
            return pos;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return - pos + 1;
        } while (k != curr);
        return pos;
    }

    private void insert(int pos, long k, double v) {
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
    public double put(long k, double v) {
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

    public double addTo(long k, double incr) {
        int pos;
        if (k == 0L) {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        } else {
            long[] key = this.key;
            pos = (int)HashCommon.mix(k) & this.mask;
            long curr = key[pos];
            if (curr != 0L) {
                if (curr == k) {
                    return this.addToValue(pos, incr);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
                    if (curr != k) continue;
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
        long[] key = this.key;
        do {
            long curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if ((curr = key[pos]) == 0L) {
                    key[last] = 0L;
                    return;
                }
                int slot = (int)HashCommon.mix(curr) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            } while (true);
            key[last] = curr;
            this.value[last] = this.value[pos];
        } while (true);
    }

    @Override
    public double remove(long k) {
        if (k == 0L) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        long[] key = this.key;
        int pos = (int)HashCommon.mix(k) & this.mask;
        long curr = key[pos];
        if (curr == 0L) {
            return this.defRetValue;
        }
        if (k == curr) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return this.defRetValue;
        } while (k != curr);
        return this.removeEntry(pos);
    }

    @Override
    public double get(long k) {
        if (k == 0L) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        long[] key = this.key;
        int pos = (int)HashCommon.mix(k) & this.mask;
        long curr = key[pos];
        if (curr == 0L) {
            return this.defRetValue;
        }
        if (k == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return this.defRetValue;
        } while (k != curr);
        return this.value[pos];
    }

    @Override
    public boolean containsKey(long k) {
        if (k == 0L) {
            return this.containsNullKey;
        }
        long[] key = this.key;
        int pos = (int)HashCommon.mix(k) & this.mask;
        long curr = key[pos];
        if (curr == 0L) {
            return false;
        }
        if (k == curr) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return false;
        } while (k != curr);
        return true;
    }

    @Override
    public boolean containsValue(double v) {
        double[] value = this.value;
        long[] key = this.key;
        if (this.containsNullKey && Double.doubleToLongBits(value[this.n]) == Double.doubleToLongBits(v)) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (key[i] == 0L || Double.doubleToLongBits(value[i]) != Double.doubleToLongBits(v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public double getOrDefault(long k, double defaultValue) {
        if (k == 0L) {
            return this.containsNullKey ? this.value[this.n] : defaultValue;
        }
        long[] key = this.key;
        int pos = (int)HashCommon.mix(k) & this.mask;
        long curr = key[pos];
        if (curr == 0L) {
            return defaultValue;
        }
        if (k == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return defaultValue;
        } while (k != curr);
        return this.value[pos];
    }

    @Override
    public double putIfAbsent(long k, double v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(long k, double v) {
        if (k == 0L) {
            if (this.containsNullKey && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[this.n])) {
                this.removeNullEntry();
                return true;
            }
            return false;
        }
        long[] key = this.key;
        int pos = (int)HashCommon.mix(k) & this.mask;
        long curr = key[pos];
        if (curr == 0L) {
            return false;
        }
        if (k == curr && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[pos])) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return false;
        } while (k != curr || Double.doubleToLongBits(v) != Double.doubleToLongBits(this.value[pos]));
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(long k, double oldValue, double v) {
        int pos = this.find(k);
        if (pos < 0 || Double.doubleToLongBits(oldValue) != Double.doubleToLongBits(this.value[pos])) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public double replace(long k, double v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        double oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public double computeIfAbsent(long k, LongToDoubleFunction mappingFunction) {
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
    public double computeIfAbsentNullable(long k, LongFunction<? extends Double> mappingFunction) {
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
    public double computeIfPresent(long k, BiFunction<? super Long, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Double newValue = remappingFunction.apply((Long)k, (Double)this.value[pos]);
        if (newValue == null) {
            if (k == 0L) {
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
    public double compute(long k, BiFunction<? super Long, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Double newValue = remappingFunction.apply((Long)k, pos >= 0 ? Double.valueOf(this.value[pos]) : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (k == 0L) {
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
    public double merge(long k, double v, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Double newValue = remappingFunction.apply((Double)this.value[pos], (Double)v);
        if (newValue == null) {
            if (k == 0L) {
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
        Arrays.fill(this.key, 0L);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public Long2DoubleMap.FastEntrySet long2DoubleEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public LongSet keySet() {
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
                    return Long2DoubleOpenHashMap.this.size;
                }

                @Override
                public boolean contains(double v) {
                    return Long2DoubleOpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Long2DoubleOpenHashMap.this.clear();
                }

                @Override
                public void forEach(DoubleConsumer consumer) {
                    if (Long2DoubleOpenHashMap.this.containsNullKey) {
                        consumer.accept(Long2DoubleOpenHashMap.this.value[Long2DoubleOpenHashMap.this.n]);
                    }
                    int pos = Long2DoubleOpenHashMap.this.n;
                    while (pos-- != 0) {
                        if (Long2DoubleOpenHashMap.this.key[pos] == 0L) continue;
                        consumer.accept(Long2DoubleOpenHashMap.this.value[pos]);
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
        long[] key = this.key;
        double[] value = this.value;
        int mask = newN - 1;
        long[] newKey = new long[newN + 1];
        double[] newValue = new double[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (key[--i] == 0L) {
            }
            int pos = (int)HashCommon.mix(key[i]) & mask;
            if (newKey[pos] != 0L) {
                while (newKey[pos = pos + 1 & mask] != 0L) {
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

    public Long2DoubleOpenHashMap clone() {
        Long2DoubleOpenHashMap c;
        try {
            c = (Long2DoubleOpenHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (long[])this.key.clone();
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
            while (this.key[i] == 0L) {
                ++i;
            }
            t = HashCommon.long2int(this.key[i]);
            h += (t ^= HashCommon.double2int(this.value[i]));
            ++i;
        }
        if (this.containsNullKey) {
            h += HashCommon.double2int(this.value[this.n]);
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        long[] key = this.key;
        double[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeLong(key[e]);
            s.writeDouble(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new long[this.n + 1];
        long[] key = this.key;
        this.value = new double[this.n + 1];
        double[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            long k = s.readLong();
            double v = s.readDouble();
            if (k == 0L) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = (int)HashCommon.mix(k) & this.mask;
                while (key[pos] != 0L) {
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
            return Long2DoubleOpenHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractLongSet {
        private KeySet() {
        }

        @Override
        public LongIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public void forEach(LongConsumer consumer) {
            if (Long2DoubleOpenHashMap.this.containsNullKey) {
                consumer.accept(Long2DoubleOpenHashMap.this.key[Long2DoubleOpenHashMap.this.n]);
            }
            int pos = Long2DoubleOpenHashMap.this.n;
            while (pos-- != 0) {
                long k = Long2DoubleOpenHashMap.this.key[pos];
                if (k == 0L) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Long2DoubleOpenHashMap.this.size;
        }

        @Override
        public boolean contains(long k) {
            return Long2DoubleOpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(long k) {
            int oldSize = Long2DoubleOpenHashMap.this.size;
            Long2DoubleOpenHashMap.this.remove(k);
            return Long2DoubleOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Long2DoubleOpenHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends MapIterator
    implements LongIterator {
        public KeyIterator() {
            super();
        }

        @Override
        public long nextLong() {
            return Long2DoubleOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Long2DoubleMap.Entry>
    implements Long2DoubleMap.FastEntrySet {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Long2DoubleMap.Entry> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Long2DoubleMap.Entry> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            long k = (Long)e.getKey();
            double v = (Double)e.getValue();
            if (k == 0L) {
                return Long2DoubleOpenHashMap.this.containsNullKey && Double.doubleToLongBits(Long2DoubleOpenHashMap.this.value[Long2DoubleOpenHashMap.this.n]) == Double.doubleToLongBits(v);
            }
            long[] key = Long2DoubleOpenHashMap.this.key;
            int pos = (int)HashCommon.mix(k) & Long2DoubleOpenHashMap.this.mask;
            long curr = key[pos];
            if (curr == 0L) {
                return false;
            }
            if (k == curr) {
                return Double.doubleToLongBits(Long2DoubleOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v);
            }
            do {
                if ((curr = key[pos = pos + 1 & Long2DoubleOpenHashMap.this.mask]) != 0L) continue;
                return false;
            } while (k != curr);
            return Double.doubleToLongBits(Long2DoubleOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v);
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            long k = (Long)e.getKey();
            double v = (Double)e.getValue();
            if (k == 0L) {
                if (Long2DoubleOpenHashMap.this.containsNullKey && Double.doubleToLongBits(Long2DoubleOpenHashMap.this.value[Long2DoubleOpenHashMap.this.n]) == Double.doubleToLongBits(v)) {
                    Long2DoubleOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            long[] key = Long2DoubleOpenHashMap.this.key;
            int pos = (int)HashCommon.mix(k) & Long2DoubleOpenHashMap.this.mask;
            long curr = key[pos];
            if (curr == 0L) {
                return false;
            }
            if (curr == k) {
                if (Double.doubleToLongBits(Long2DoubleOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v)) {
                    Long2DoubleOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Long2DoubleOpenHashMap.this.mask]) != 0L) continue;
                return false;
            } while (curr != k || Double.doubleToLongBits(Long2DoubleOpenHashMap.this.value[pos]) != Double.doubleToLongBits(v));
            Long2DoubleOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Long2DoubleOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Long2DoubleOpenHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Long2DoubleMap.Entry> consumer) {
            if (Long2DoubleOpenHashMap.this.containsNullKey) {
                consumer.accept(new AbstractLong2DoubleMap.BasicEntry(Long2DoubleOpenHashMap.this.key[Long2DoubleOpenHashMap.this.n], Long2DoubleOpenHashMap.this.value[Long2DoubleOpenHashMap.this.n]));
            }
            int pos = Long2DoubleOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Long2DoubleOpenHashMap.this.key[pos] == 0L) continue;
                consumer.accept(new AbstractLong2DoubleMap.BasicEntry(Long2DoubleOpenHashMap.this.key[pos], Long2DoubleOpenHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Long2DoubleMap.Entry> consumer) {
            AbstractLong2DoubleMap.BasicEntry entry = new AbstractLong2DoubleMap.BasicEntry();
            if (Long2DoubleOpenHashMap.this.containsNullKey) {
                entry.key = Long2DoubleOpenHashMap.this.key[Long2DoubleOpenHashMap.this.n];
                entry.value = Long2DoubleOpenHashMap.this.value[Long2DoubleOpenHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Long2DoubleOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Long2DoubleOpenHashMap.this.key[pos] == 0L) continue;
                entry.key = Long2DoubleOpenHashMap.this.key[pos];
                entry.value = Long2DoubleOpenHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends MapIterator
    implements ObjectIterator<Long2DoubleMap.Entry> {
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
    implements ObjectIterator<Long2DoubleMap.Entry> {
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
        LongArrayList wrapped;

        private MapIterator() {
            this.pos = Long2DoubleOpenHashMap.this.n;
            this.last = -1;
            this.c = Long2DoubleOpenHashMap.this.size;
            this.mustReturnNullKey = Long2DoubleOpenHashMap.this.containsNullKey;
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
                this.last = Long2DoubleOpenHashMap.this.n;
                return this.last;
            }
            long[] key = Long2DoubleOpenHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                long k = this.wrapped.getLong(- this.pos - 1);
                int p = (int)HashCommon.mix(k) & Long2DoubleOpenHashMap.this.mask;
                while (k != key[p]) {
                    p = p + 1 & Long2DoubleOpenHashMap.this.mask;
                }
                return p;
            } while (key[this.pos] == 0L);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            long[] key = Long2DoubleOpenHashMap.this.key;
            do {
                long curr;
                int last = pos;
                pos = last + 1 & Long2DoubleOpenHashMap.this.mask;
                do {
                    if ((curr = key[pos]) == 0L) {
                        key[last] = 0L;
                        return;
                    }
                    int slot = (int)HashCommon.mix(curr) & Long2DoubleOpenHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Long2DoubleOpenHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new LongArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Long2DoubleOpenHashMap.this.value[last] = Long2DoubleOpenHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Long2DoubleOpenHashMap.this.n) {
                Long2DoubleOpenHashMap.this.containsNullKey = false;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Long2DoubleOpenHashMap.this.remove(this.wrapped.getLong(- this.pos - 1));
                this.last = -1;
                return;
            }
            --Long2DoubleOpenHashMap.this.size;
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
    implements Long2DoubleMap.Entry,
    Map.Entry<Long, Double> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public long getLongKey() {
            return Long2DoubleOpenHashMap.this.key[this.index];
        }

        @Override
        public double getDoubleValue() {
            return Long2DoubleOpenHashMap.this.value[this.index];
        }

        @Override
        public double setValue(double v) {
            double oldValue = Long2DoubleOpenHashMap.this.value[this.index];
            Long2DoubleOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Long getKey() {
            return Long2DoubleOpenHashMap.this.key[this.index];
        }

        @Deprecated
        @Override
        public Double getValue() {
            return Long2DoubleOpenHashMap.this.value[this.index];
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
            return Long2DoubleOpenHashMap.this.key[this.index] == (Long)e.getKey() && Double.doubleToLongBits(Long2DoubleOpenHashMap.this.value[this.index]) == Double.doubleToLongBits((Double)e.getValue());
        }

        @Override
        public int hashCode() {
            return HashCommon.long2int(Long2DoubleOpenHashMap.this.key[this.index]) ^ HashCommon.double2int(Long2DoubleOpenHashMap.this.value[this.index]);
        }

        public String toString() {
            return "" + Long2DoubleOpenHashMap.this.key[this.index] + "=>" + Long2DoubleOpenHashMap.this.value[this.index];
        }
    }

}

