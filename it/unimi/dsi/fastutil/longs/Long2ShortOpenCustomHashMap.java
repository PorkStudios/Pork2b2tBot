/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.longs.AbstractLong2ShortMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2ShortMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongHash;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
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
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongToIntFunction;

public class Long2ShortOpenCustomHashMap
extends AbstractLong2ShortMap
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient long[] key;
    protected transient short[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected LongHash.Strategy strategy;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Long2ShortMap.FastEntrySet entries;
    protected transient LongSet keys;
    protected transient ShortCollection values;

    public Long2ShortOpenCustomHashMap(int expected, float f, LongHash.Strategy strategy) {
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
        this.key = new long[this.n + 1];
        this.value = new short[this.n + 1];
    }

    public Long2ShortOpenCustomHashMap(int expected, LongHash.Strategy strategy) {
        this(expected, 0.75f, strategy);
    }

    public Long2ShortOpenCustomHashMap(LongHash.Strategy strategy) {
        this(16, 0.75f, strategy);
    }

    public Long2ShortOpenCustomHashMap(Map<? extends Long, ? extends Short> m, float f, LongHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Long2ShortOpenCustomHashMap(Map<? extends Long, ? extends Short> m, LongHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }

    public Long2ShortOpenCustomHashMap(Long2ShortMap m, float f, LongHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Long2ShortOpenCustomHashMap(Long2ShortMap m, LongHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }

    public Long2ShortOpenCustomHashMap(long[] k, short[] v, float f, LongHash.Strategy strategy) {
        this(k.length, f, strategy);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Long2ShortOpenCustomHashMap(long[] k, short[] v, LongHash.Strategy strategy) {
        this(k, v, 0.75f, strategy);
    }

    public LongHash.Strategy strategy() {
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

    private short removeEntry(int pos) {
        short oldValue = this.value[pos];
        --this.size;
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private short removeNullEntry() {
        this.containsNullKey = false;
        short oldValue = this.value[this.n];
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Short> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }

    private int find(long k) {
        if (this.strategy.equals(k, 0L)) {
            return this.containsNullKey ? this.n : - this.n + 1;
        }
        long[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        long curr = key[pos];
        if (curr == 0L) {
            return - pos + 1;
        }
        if (this.strategy.equals(k, curr)) {
            return pos;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return - pos + 1;
        } while (!this.strategy.equals(k, curr));
        return pos;
    }

    private void insert(int pos, long k, short v) {
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
    public short put(long k, short v) {
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return this.defRetValue;
        }
        short oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    private short addToValue(int pos, short incr) {
        short oldValue = this.value[pos];
        this.value[pos] = (short)(oldValue + incr);
        return oldValue;
    }

    public short addTo(long k, short incr) {
        int pos;
        if (this.strategy.equals(k, 0L)) {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        } else {
            long[] key = this.key;
            pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
            long curr = key[pos];
            if (curr != 0L) {
                if (this.strategy.equals(curr, k)) {
                    return this.addToValue(pos, incr);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
                    if (!this.strategy.equals(curr, k)) continue;
                    return this.addToValue(pos, incr);
                }
            }
        }
        this.key[pos] = k;
        this.value[pos] = (short)(this.defRetValue + incr);
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
                int slot = HashCommon.mix(this.strategy.hashCode(curr)) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            } while (true);
            key[last] = curr;
            this.value[last] = this.value[pos];
        } while (true);
    }

    @Override
    public short remove(long k) {
        if (this.strategy.equals(k, 0L)) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        long[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        long curr = key[pos];
        if (curr == 0L) {
            return this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        return this.removeEntry(pos);
    }

    @Override
    public short get(long k) {
        if (this.strategy.equals(k, 0L)) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        long[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        long curr = key[pos];
        if (curr == 0L) {
            return this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        return this.value[pos];
    }

    @Override
    public boolean containsKey(long k) {
        if (this.strategy.equals(k, 0L)) {
            return this.containsNullKey;
        }
        long[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        long curr = key[pos];
        if (curr == 0L) {
            return false;
        }
        if (this.strategy.equals(k, curr)) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return false;
        } while (!this.strategy.equals(k, curr));
        return true;
    }

    @Override
    public boolean containsValue(short v) {
        short[] value = this.value;
        long[] key = this.key;
        if (this.containsNullKey && value[this.n] == v) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (key[i] == 0L || value[i] != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public short getOrDefault(long k, short defaultValue) {
        if (this.strategy.equals(k, 0L)) {
            return this.containsNullKey ? this.value[this.n] : defaultValue;
        }
        long[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        long curr = key[pos];
        if (curr == 0L) {
            return defaultValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return defaultValue;
        } while (!this.strategy.equals(k, curr));
        return this.value[pos];
    }

    @Override
    public short putIfAbsent(long k, short v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(long k, short v) {
        if (this.strategy.equals(k, 0L)) {
            if (this.containsNullKey && v == this.value[this.n]) {
                this.removeNullEntry();
                return true;
            }
            return false;
        }
        long[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        long curr = key[pos];
        if (curr == 0L) {
            return false;
        }
        if (this.strategy.equals(k, curr) && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return false;
        } while (!this.strategy.equals(k, curr) || v != this.value[pos]);
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(long k, short oldValue, short v) {
        int pos = this.find(k);
        if (pos < 0 || oldValue != this.value[pos]) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public short replace(long k, short v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        short oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public short computeIfAbsent(long k, LongToIntFunction mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        short newValue = SafeMath.safeIntToShort(mappingFunction.applyAsInt(k));
        this.insert(- pos - 1, k, newValue);
        return newValue;
    }

    @Override
    public short computeIfAbsentNullable(long k, LongFunction<? extends Short> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        Short newValue = mappingFunction.apply(k);
        if (newValue == null) {
            return this.defRetValue;
        }
        short v = newValue;
        this.insert(- pos - 1, k, v);
        return v;
    }

    @Override
    public short computeIfPresent(long k, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Short newValue = remappingFunction.apply((Long)k, (Short)this.value[pos]);
        if (newValue == null) {
            if (this.strategy.equals(k, 0L)) {
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
    public short compute(long k, BiFunction<? super Long, ? super Short, ? extends Short> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Short newValue = remappingFunction.apply((Long)k, pos >= 0 ? Short.valueOf(this.value[pos]) : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (this.strategy.equals(k, 0L)) {
                    this.removeNullEntry();
                } else {
                    this.removeEntry(pos);
                }
            }
            return this.defRetValue;
        }
        short newVal = newValue;
        if (pos < 0) {
            this.insert(- pos - 1, k, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public short merge(long k, short v, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Short newValue = remappingFunction.apply((Short)this.value[pos], (Short)v);
        if (newValue == null) {
            if (this.strategy.equals(k, 0L)) {
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

    public Long2ShortMap.FastEntrySet long2ShortEntrySet() {
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
    public ShortCollection values() {
        if (this.values == null) {
            this.values = new AbstractShortCollection(){

                @Override
                public ShortIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Long2ShortOpenCustomHashMap.this.size;
                }

                @Override
                public boolean contains(short v) {
                    return Long2ShortOpenCustomHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Long2ShortOpenCustomHashMap.this.clear();
                }

                @Override
                public void forEach(IntConsumer consumer) {
                    if (Long2ShortOpenCustomHashMap.this.containsNullKey) {
                        consumer.accept(Long2ShortOpenCustomHashMap.this.value[Long2ShortOpenCustomHashMap.this.n]);
                    }
                    int pos = Long2ShortOpenCustomHashMap.this.n;
                    while (pos-- != 0) {
                        if (Long2ShortOpenCustomHashMap.this.key[pos] == 0L) continue;
                        consumer.accept(Long2ShortOpenCustomHashMap.this.value[pos]);
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
        short[] value = this.value;
        int mask = newN - 1;
        long[] newKey = new long[newN + 1];
        short[] newValue = new short[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (key[--i] == 0L) {
            }
            int pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask;
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

    public Long2ShortOpenCustomHashMap clone() {
        Long2ShortOpenCustomHashMap c;
        try {
            c = (Long2ShortOpenCustomHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (long[])this.key.clone();
        c.value = (short[])this.value.clone();
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
            while (this.key[i] == 0L) {
                ++i;
            }
            t = this.strategy.hashCode(this.key[i]);
            h += (t ^= this.value[i]);
            ++i;
        }
        if (this.containsNullKey) {
            h += this.value[this.n];
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        long[] key = this.key;
        short[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeLong(key[e]);
            s.writeShort(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new long[this.n + 1];
        long[] key = this.key;
        this.value = new short[this.n + 1];
        short[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            long k = s.readLong();
            short v = s.readShort();
            if (this.strategy.equals(k, 0L)) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
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
    implements ShortIterator {
        public ValueIterator() {
            super();
        }

        @Override
        public short nextShort() {
            return Long2ShortOpenCustomHashMap.this.value[this.nextEntry()];
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
            if (Long2ShortOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(Long2ShortOpenCustomHashMap.this.key[Long2ShortOpenCustomHashMap.this.n]);
            }
            int pos = Long2ShortOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                long k = Long2ShortOpenCustomHashMap.this.key[pos];
                if (k == 0L) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Long2ShortOpenCustomHashMap.this.size;
        }

        @Override
        public boolean contains(long k) {
            return Long2ShortOpenCustomHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(long k) {
            int oldSize = Long2ShortOpenCustomHashMap.this.size;
            Long2ShortOpenCustomHashMap.this.remove(k);
            return Long2ShortOpenCustomHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Long2ShortOpenCustomHashMap.this.clear();
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
            return Long2ShortOpenCustomHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Long2ShortMap.Entry>
    implements Long2ShortMap.FastEntrySet {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Long2ShortMap.Entry> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Long2ShortMap.Entry> fastIterator() {
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
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            long k = (Long)e.getKey();
            short v = (Short)e.getValue();
            if (Long2ShortOpenCustomHashMap.this.strategy.equals(k, 0L)) {
                return Long2ShortOpenCustomHashMap.this.containsNullKey && Long2ShortOpenCustomHashMap.this.value[Long2ShortOpenCustomHashMap.this.n] == v;
            }
            long[] key = Long2ShortOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Long2ShortOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ShortOpenCustomHashMap.this.mask;
            long curr = key[pos];
            if (curr == 0L) {
                return false;
            }
            if (Long2ShortOpenCustomHashMap.this.strategy.equals(k, curr)) {
                return Long2ShortOpenCustomHashMap.this.value[pos] == v;
            }
            do {
                if ((curr = key[pos = pos + 1 & Long2ShortOpenCustomHashMap.this.mask]) != 0L) continue;
                return false;
            } while (!Long2ShortOpenCustomHashMap.this.strategy.equals(k, curr));
            return Long2ShortOpenCustomHashMap.this.value[pos] == v;
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
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            long k = (Long)e.getKey();
            short v = (Short)e.getValue();
            if (Long2ShortOpenCustomHashMap.this.strategy.equals(k, 0L)) {
                if (Long2ShortOpenCustomHashMap.this.containsNullKey && Long2ShortOpenCustomHashMap.this.value[Long2ShortOpenCustomHashMap.this.n] == v) {
                    Long2ShortOpenCustomHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            long[] key = Long2ShortOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Long2ShortOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ShortOpenCustomHashMap.this.mask;
            long curr = key[pos];
            if (curr == 0L) {
                return false;
            }
            if (Long2ShortOpenCustomHashMap.this.strategy.equals(curr, k)) {
                if (Long2ShortOpenCustomHashMap.this.value[pos] == v) {
                    Long2ShortOpenCustomHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Long2ShortOpenCustomHashMap.this.mask]) != 0L) continue;
                return false;
            } while (!Long2ShortOpenCustomHashMap.this.strategy.equals(curr, k) || Long2ShortOpenCustomHashMap.this.value[pos] != v);
            Long2ShortOpenCustomHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Long2ShortOpenCustomHashMap.this.size;
        }

        @Override
        public void clear() {
            Long2ShortOpenCustomHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Long2ShortMap.Entry> consumer) {
            if (Long2ShortOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(new AbstractLong2ShortMap.BasicEntry(Long2ShortOpenCustomHashMap.this.key[Long2ShortOpenCustomHashMap.this.n], Long2ShortOpenCustomHashMap.this.value[Long2ShortOpenCustomHashMap.this.n]));
            }
            int pos = Long2ShortOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Long2ShortOpenCustomHashMap.this.key[pos] == 0L) continue;
                consumer.accept(new AbstractLong2ShortMap.BasicEntry(Long2ShortOpenCustomHashMap.this.key[pos], Long2ShortOpenCustomHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Long2ShortMap.Entry> consumer) {
            AbstractLong2ShortMap.BasicEntry entry = new AbstractLong2ShortMap.BasicEntry();
            if (Long2ShortOpenCustomHashMap.this.containsNullKey) {
                entry.key = Long2ShortOpenCustomHashMap.this.key[Long2ShortOpenCustomHashMap.this.n];
                entry.value = Long2ShortOpenCustomHashMap.this.value[Long2ShortOpenCustomHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Long2ShortOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Long2ShortOpenCustomHashMap.this.key[pos] == 0L) continue;
                entry.key = Long2ShortOpenCustomHashMap.this.key[pos];
                entry.value = Long2ShortOpenCustomHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends MapIterator
    implements ObjectIterator<Long2ShortMap.Entry> {
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
    implements ObjectIterator<Long2ShortMap.Entry> {
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
            this.pos = Long2ShortOpenCustomHashMap.this.n;
            this.last = -1;
            this.c = Long2ShortOpenCustomHashMap.this.size;
            this.mustReturnNullKey = Long2ShortOpenCustomHashMap.this.containsNullKey;
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
                this.last = Long2ShortOpenCustomHashMap.this.n;
                return this.last;
            }
            long[] key = Long2ShortOpenCustomHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                long k = this.wrapped.getLong(- this.pos - 1);
                int p = HashCommon.mix(Long2ShortOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ShortOpenCustomHashMap.this.mask;
                while (!Long2ShortOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                    p = p + 1 & Long2ShortOpenCustomHashMap.this.mask;
                }
                return p;
            } while (key[this.pos] == 0L);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            long[] key = Long2ShortOpenCustomHashMap.this.key;
            do {
                long curr;
                int last = pos;
                pos = last + 1 & Long2ShortOpenCustomHashMap.this.mask;
                do {
                    if ((curr = key[pos]) == 0L) {
                        key[last] = 0L;
                        return;
                    }
                    int slot = HashCommon.mix(Long2ShortOpenCustomHashMap.this.strategy.hashCode(curr)) & Long2ShortOpenCustomHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Long2ShortOpenCustomHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new LongArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Long2ShortOpenCustomHashMap.this.value[last] = Long2ShortOpenCustomHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Long2ShortOpenCustomHashMap.this.n) {
                Long2ShortOpenCustomHashMap.this.containsNullKey = false;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Long2ShortOpenCustomHashMap.this.remove(this.wrapped.getLong(- this.pos - 1));
                this.last = -1;
                return;
            }
            --Long2ShortOpenCustomHashMap.this.size;
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
    implements Long2ShortMap.Entry,
    Map.Entry<Long, Short> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public long getLongKey() {
            return Long2ShortOpenCustomHashMap.this.key[this.index];
        }

        @Override
        public short getShortValue() {
            return Long2ShortOpenCustomHashMap.this.value[this.index];
        }

        @Override
        public short setValue(short v) {
            short oldValue = Long2ShortOpenCustomHashMap.this.value[this.index];
            Long2ShortOpenCustomHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Long getKey() {
            return Long2ShortOpenCustomHashMap.this.key[this.index];
        }

        @Deprecated
        @Override
        public Short getValue() {
            return Long2ShortOpenCustomHashMap.this.value[this.index];
        }

        @Deprecated
        @Override
        public Short setValue(Short v) {
            return this.setValue((short)v);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Long2ShortOpenCustomHashMap.this.strategy.equals(Long2ShortOpenCustomHashMap.this.key[this.index], (Long)e.getKey()) && Long2ShortOpenCustomHashMap.this.value[this.index] == (Short)e.getValue();
        }

        @Override
        public int hashCode() {
            return Long2ShortOpenCustomHashMap.this.strategy.hashCode(Long2ShortOpenCustomHashMap.this.key[this.index]) ^ Long2ShortOpenCustomHashMap.this.value[this.index];
        }

        public String toString() {
            return "" + Long2ShortOpenCustomHashMap.this.key[this.index] + "=>" + Long2ShortOpenCustomHashMap.this.value[this.index];
        }
    }

}

