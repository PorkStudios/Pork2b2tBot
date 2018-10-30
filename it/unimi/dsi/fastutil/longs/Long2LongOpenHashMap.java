/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLong2LongMap;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
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
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongUnaryOperator;

public class Long2LongOpenHashMap
extends AbstractLong2LongMap
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient long[] key;
    protected transient long[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Long2LongMap.FastEntrySet entries;
    protected transient LongSet keys;
    protected transient LongCollection values;

    public Long2LongOpenHashMap(int expected, float f) {
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
        this.value = new long[this.n + 1];
    }

    public Long2LongOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Long2LongOpenHashMap() {
        this(16, 0.75f);
    }

    public Long2LongOpenHashMap(Map<? extends Long, ? extends Long> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Long2LongOpenHashMap(Map<? extends Long, ? extends Long> m) {
        this(m, 0.75f);
    }

    public Long2LongOpenHashMap(Long2LongMap m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Long2LongOpenHashMap(Long2LongMap m) {
        this(m, 0.75f);
    }

    public Long2LongOpenHashMap(long[] k, long[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Long2LongOpenHashMap(long[] k, long[] v) {
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
    public void putAll(Map<? extends Long, ? extends Long> m) {
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

    private void insert(int pos, long k, long v) {
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
    public long put(long k, long v) {
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

    public long addTo(long k, long incr) {
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
    public long remove(long k) {
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
    public long get(long k) {
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
    public boolean containsValue(long v) {
        long[] value = this.value;
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
    public long getOrDefault(long k, long defaultValue) {
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
    public long putIfAbsent(long k, long v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(long k, long v) {
        if (k == 0L) {
            if (this.containsNullKey && v == this.value[this.n]) {
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
        if (k == curr && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return false;
        } while (k != curr || v != this.value[pos]);
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(long k, long oldValue, long v) {
        int pos = this.find(k);
        if (pos < 0 || oldValue != this.value[pos]) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public long replace(long k, long v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        long oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public long computeIfAbsent(long k, LongUnaryOperator mappingFunction) {
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
    public long computeIfAbsentNullable(long k, LongFunction<? extends Long> mappingFunction) {
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
    public long computeIfPresent(long k, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Long newValue = remappingFunction.apply((Long)k, (Long)this.value[pos]);
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
    public long compute(long k, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Long newValue = remappingFunction.apply((Long)k, pos >= 0 ? Long.valueOf(this.value[pos]) : null);
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
        long newVal = newValue;
        if (pos < 0) {
            this.insert(- pos - 1, k, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public long merge(long k, long v, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Long newValue = remappingFunction.apply((Long)this.value[pos], (Long)v);
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

    public Long2LongMap.FastEntrySet long2LongEntrySet() {
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
    public LongCollection values() {
        if (this.values == null) {
            this.values = new AbstractLongCollection(){

                @Override
                public LongIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Long2LongOpenHashMap.this.size;
                }

                @Override
                public boolean contains(long v) {
                    return Long2LongOpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Long2LongOpenHashMap.this.clear();
                }

                @Override
                public void forEach(LongConsumer consumer) {
                    if (Long2LongOpenHashMap.this.containsNullKey) {
                        consumer.accept(Long2LongOpenHashMap.this.value[Long2LongOpenHashMap.this.n]);
                    }
                    int pos = Long2LongOpenHashMap.this.n;
                    while (pos-- != 0) {
                        if (Long2LongOpenHashMap.this.key[pos] == 0L) continue;
                        consumer.accept(Long2LongOpenHashMap.this.value[pos]);
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
        long[] value = this.value;
        int mask = newN - 1;
        long[] newKey = new long[newN + 1];
        long[] newValue = new long[newN + 1];
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

    public Long2LongOpenHashMap clone() {
        Long2LongOpenHashMap c;
        try {
            c = (Long2LongOpenHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (long[])this.key.clone();
        c.value = (long[])this.value.clone();
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
            h += (t ^= HashCommon.long2int(this.value[i]));
            ++i;
        }
        if (this.containsNullKey) {
            h += HashCommon.long2int(this.value[this.n]);
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        long[] key = this.key;
        long[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeLong(key[e]);
            s.writeLong(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new long[this.n + 1];
        long[] key = this.key;
        this.value = new long[this.n + 1];
        long[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            long k = s.readLong();
            long v = s.readLong();
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
    implements LongIterator {
        public ValueIterator() {
            super();
        }

        @Override
        public long nextLong() {
            return Long2LongOpenHashMap.this.value[this.nextEntry()];
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
            if (Long2LongOpenHashMap.this.containsNullKey) {
                consumer.accept(Long2LongOpenHashMap.this.key[Long2LongOpenHashMap.this.n]);
            }
            int pos = Long2LongOpenHashMap.this.n;
            while (pos-- != 0) {
                long k = Long2LongOpenHashMap.this.key[pos];
                if (k == 0L) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Long2LongOpenHashMap.this.size;
        }

        @Override
        public boolean contains(long k) {
            return Long2LongOpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(long k) {
            int oldSize = Long2LongOpenHashMap.this.size;
            Long2LongOpenHashMap.this.remove(k);
            return Long2LongOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Long2LongOpenHashMap.this.clear();
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
            return Long2LongOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Long2LongMap.Entry>
    implements Long2LongMap.FastEntrySet {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Long2LongMap.Entry> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Long2LongMap.Entry> fastIterator() {
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
            if (e.getValue() == null || !(e.getValue() instanceof Long)) {
                return false;
            }
            long k = (Long)e.getKey();
            long v = (Long)e.getValue();
            if (k == 0L) {
                return Long2LongOpenHashMap.this.containsNullKey && Long2LongOpenHashMap.this.value[Long2LongOpenHashMap.this.n] == v;
            }
            long[] key = Long2LongOpenHashMap.this.key;
            int pos = (int)HashCommon.mix(k) & Long2LongOpenHashMap.this.mask;
            long curr = key[pos];
            if (curr == 0L) {
                return false;
            }
            if (k == curr) {
                return Long2LongOpenHashMap.this.value[pos] == v;
            }
            do {
                if ((curr = key[pos = pos + 1 & Long2LongOpenHashMap.this.mask]) != 0L) continue;
                return false;
            } while (k != curr);
            return Long2LongOpenHashMap.this.value[pos] == v;
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
            if (e.getValue() == null || !(e.getValue() instanceof Long)) {
                return false;
            }
            long k = (Long)e.getKey();
            long v = (Long)e.getValue();
            if (k == 0L) {
                if (Long2LongOpenHashMap.this.containsNullKey && Long2LongOpenHashMap.this.value[Long2LongOpenHashMap.this.n] == v) {
                    Long2LongOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            long[] key = Long2LongOpenHashMap.this.key;
            int pos = (int)HashCommon.mix(k) & Long2LongOpenHashMap.this.mask;
            long curr = key[pos];
            if (curr == 0L) {
                return false;
            }
            if (curr == k) {
                if (Long2LongOpenHashMap.this.value[pos] == v) {
                    Long2LongOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Long2LongOpenHashMap.this.mask]) != 0L) continue;
                return false;
            } while (curr != k || Long2LongOpenHashMap.this.value[pos] != v);
            Long2LongOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Long2LongOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Long2LongOpenHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Long2LongMap.Entry> consumer) {
            if (Long2LongOpenHashMap.this.containsNullKey) {
                consumer.accept(new AbstractLong2LongMap.BasicEntry(Long2LongOpenHashMap.this.key[Long2LongOpenHashMap.this.n], Long2LongOpenHashMap.this.value[Long2LongOpenHashMap.this.n]));
            }
            int pos = Long2LongOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Long2LongOpenHashMap.this.key[pos] == 0L) continue;
                consumer.accept(new AbstractLong2LongMap.BasicEntry(Long2LongOpenHashMap.this.key[pos], Long2LongOpenHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Long2LongMap.Entry> consumer) {
            AbstractLong2LongMap.BasicEntry entry = new AbstractLong2LongMap.BasicEntry();
            if (Long2LongOpenHashMap.this.containsNullKey) {
                entry.key = Long2LongOpenHashMap.this.key[Long2LongOpenHashMap.this.n];
                entry.value = Long2LongOpenHashMap.this.value[Long2LongOpenHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Long2LongOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Long2LongOpenHashMap.this.key[pos] == 0L) continue;
                entry.key = Long2LongOpenHashMap.this.key[pos];
                entry.value = Long2LongOpenHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends MapIterator
    implements ObjectIterator<Long2LongMap.Entry> {
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
    implements ObjectIterator<Long2LongMap.Entry> {
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
            this.pos = Long2LongOpenHashMap.this.n;
            this.last = -1;
            this.c = Long2LongOpenHashMap.this.size;
            this.mustReturnNullKey = Long2LongOpenHashMap.this.containsNullKey;
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
                this.last = Long2LongOpenHashMap.this.n;
                return this.last;
            }
            long[] key = Long2LongOpenHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                long k = this.wrapped.getLong(- this.pos - 1);
                int p = (int)HashCommon.mix(k) & Long2LongOpenHashMap.this.mask;
                while (k != key[p]) {
                    p = p + 1 & Long2LongOpenHashMap.this.mask;
                }
                return p;
            } while (key[this.pos] == 0L);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            long[] key = Long2LongOpenHashMap.this.key;
            do {
                long curr;
                int last = pos;
                pos = last + 1 & Long2LongOpenHashMap.this.mask;
                do {
                    if ((curr = key[pos]) == 0L) {
                        key[last] = 0L;
                        return;
                    }
                    int slot = (int)HashCommon.mix(curr) & Long2LongOpenHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Long2LongOpenHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new LongArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Long2LongOpenHashMap.this.value[last] = Long2LongOpenHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Long2LongOpenHashMap.this.n) {
                Long2LongOpenHashMap.this.containsNullKey = false;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Long2LongOpenHashMap.this.remove(this.wrapped.getLong(- this.pos - 1));
                this.last = -1;
                return;
            }
            --Long2LongOpenHashMap.this.size;
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
    implements Long2LongMap.Entry,
    Map.Entry<Long, Long> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public long getLongKey() {
            return Long2LongOpenHashMap.this.key[this.index];
        }

        @Override
        public long getLongValue() {
            return Long2LongOpenHashMap.this.value[this.index];
        }

        @Override
        public long setValue(long v) {
            long oldValue = Long2LongOpenHashMap.this.value[this.index];
            Long2LongOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Long getKey() {
            return Long2LongOpenHashMap.this.key[this.index];
        }

        @Deprecated
        @Override
        public Long getValue() {
            return Long2LongOpenHashMap.this.value[this.index];
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
            return Long2LongOpenHashMap.this.key[this.index] == (Long)e.getKey() && Long2LongOpenHashMap.this.value[this.index] == (Long)e.getValue();
        }

        @Override
        public int hashCode() {
            return HashCommon.long2int(Long2LongOpenHashMap.this.key[this.index]) ^ HashCommon.long2int(Long2LongOpenHashMap.this.value[this.index]);
        }

        public String toString() {
            return "" + Long2LongOpenHashMap.this.key[this.index] + "=>" + Long2LongOpenHashMap.this.value[this.index];
        }
    }

}

