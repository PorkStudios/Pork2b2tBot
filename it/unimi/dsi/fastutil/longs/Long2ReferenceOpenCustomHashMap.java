/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLong2ReferenceMap;
import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongHash;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
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
import java.util.function.LongConsumer;
import java.util.function.LongFunction;

public class Long2ReferenceOpenCustomHashMap<V>
extends AbstractLong2ReferenceMap<V>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient long[] key;
    protected transient V[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected LongHash.Strategy strategy;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Long2ReferenceMap.FastEntrySet<V> entries;
    protected transient LongSet keys;
    protected transient ReferenceCollection<V> values;

    public Long2ReferenceOpenCustomHashMap(int expected, float f, LongHash.Strategy strategy) {
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
        this.value = new Object[this.n + 1];
    }

    public Long2ReferenceOpenCustomHashMap(int expected, LongHash.Strategy strategy) {
        this(expected, 0.75f, strategy);
    }

    public Long2ReferenceOpenCustomHashMap(LongHash.Strategy strategy) {
        this(16, 0.75f, strategy);
    }

    public Long2ReferenceOpenCustomHashMap(Map<? extends Long, ? extends V> m, float f, LongHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Long2ReferenceOpenCustomHashMap(Map<? extends Long, ? extends V> m, LongHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }

    public Long2ReferenceOpenCustomHashMap(Long2ReferenceMap<V> m, float f, LongHash.Strategy strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Long2ReferenceOpenCustomHashMap(Long2ReferenceMap<V> m, LongHash.Strategy strategy) {
        this(m, 0.75f, strategy);
    }

    public Long2ReferenceOpenCustomHashMap(long[] k, V[] v, float f, LongHash.Strategy strategy) {
        this(k.length, f, strategy);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Long2ReferenceOpenCustomHashMap(long[] k, V[] v, LongHash.Strategy strategy) {
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
    public void putAll(Map<? extends Long, ? extends V> m) {
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

    private void insert(int pos, long k, V v) {
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
    public V put(long k, V v) {
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
        long[] key = this.key;
        do {
            long curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if ((curr = key[pos]) == 0L) {
                    key[last] = 0L;
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
    public V remove(long k) {
        if (this.strategy.equals(k, 0L)) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return (V)this.defRetValue;
        }
        long[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        long curr = key[pos];
        if (curr == 0L) {
            return (V)this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return (V)this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        return this.removeEntry(pos);
    }

    @Override
    public V get(long k) {
        if (this.strategy.equals(k, 0L)) {
            return (V)(this.containsNullKey ? this.value[this.n] : this.defRetValue);
        }
        long[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        long curr = key[pos];
        if (curr == 0L) {
            return (V)this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0L) continue;
            return (V)this.defRetValue;
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
    public boolean containsValue(Object v) {
        V[] value = this.value;
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
    public V getOrDefault(long k, V defaultValue) {
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
    public V putIfAbsent(long k, V v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return (V)this.defRetValue;
    }

    @Override
    public boolean remove(long k, Object v) {
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
    public boolean replace(long k, V oldValue, V v) {
        int pos = this.find(k);
        if (pos < 0 || oldValue != this.value[pos]) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public V replace(long k, V v) {
        int pos = this.find(k);
        if (pos < 0) {
            return (V)this.defRetValue;
        }
        V oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public V computeIfAbsent(long k, LongFunction<? extends V> mappingFunction) {
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
    public V computeIfPresent(long k, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return (V)this.defRetValue;
        }
        V newValue = remappingFunction.apply((Long)k, this.value[pos]);
        if (newValue == null) {
            if (this.strategy.equals(k, 0L)) {
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
    public V compute(long k, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        V newValue = remappingFunction.apply((Long)k, pos >= 0 ? (Object)this.value[pos] : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (this.strategy.equals(k, 0L)) {
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
    public V merge(long k, V v, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
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
            if (this.strategy.equals(k, 0L)) {
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
        Arrays.fill(this.key, 0L);
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

    public Long2ReferenceMap.FastEntrySet<V> long2ReferenceEntrySet() {
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
    public ReferenceCollection<V> values() {
        if (this.values == null) {
            this.values = new AbstractReferenceCollection<V>(){

                @Override
                public ObjectIterator<V> iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Long2ReferenceOpenCustomHashMap.this.size;
                }

                @Override
                public boolean contains(Object v) {
                    return Long2ReferenceOpenCustomHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Long2ReferenceOpenCustomHashMap.this.clear();
                }

                @Override
                public void forEach(Consumer<? super V> consumer) {
                    if (Long2ReferenceOpenCustomHashMap.this.containsNullKey) {
                        consumer.accept(Long2ReferenceOpenCustomHashMap.this.value[Long2ReferenceOpenCustomHashMap.this.n]);
                    }
                    int pos = Long2ReferenceOpenCustomHashMap.this.n;
                    while (pos-- != 0) {
                        if (Long2ReferenceOpenCustomHashMap.this.key[pos] == 0L) continue;
                        consumer.accept(Long2ReferenceOpenCustomHashMap.this.value[pos]);
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
        V[] value = this.value;
        int mask = newN - 1;
        long[] newKey = new long[newN + 1];
        Object[] newValue = new Object[newN + 1];
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

    public Long2ReferenceOpenCustomHashMap<V> clone() {
        Long2ReferenceOpenCustomHashMap c;
        try {
            c = (Long2ReferenceOpenCustomHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (long[])this.key.clone();
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
            while (this.key[i] == 0L) {
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
        long[] key = this.key;
        V[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeLong(key[e]);
            s.writeObject(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new long[this.n + 1];
        long[] key = this.key;
        this.value = new Object[this.n + 1];
        Object[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            long k = s.readLong();
            Object v = s.readObject();
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
    extends Long2ReferenceOpenCustomHashMap<V>
    implements ObjectIterator<V> {
        public ValueIterator() {
            super();
        }

        @Override
        public V next() {
            return Long2ReferenceOpenCustomHashMap.this.value[this.nextEntry()];
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
            if (Long2ReferenceOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(Long2ReferenceOpenCustomHashMap.this.key[Long2ReferenceOpenCustomHashMap.this.n]);
            }
            int pos = Long2ReferenceOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                long k = Long2ReferenceOpenCustomHashMap.this.key[pos];
                if (k == 0L) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Long2ReferenceOpenCustomHashMap.this.size;
        }

        @Override
        public boolean contains(long k) {
            return Long2ReferenceOpenCustomHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(long k) {
            int oldSize = Long2ReferenceOpenCustomHashMap.this.size;
            Long2ReferenceOpenCustomHashMap.this.remove(k);
            return Long2ReferenceOpenCustomHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Long2ReferenceOpenCustomHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends Long2ReferenceOpenCustomHashMap<V>
    implements LongIterator {
        public KeyIterator() {
            super();
        }

        @Override
        public long nextLong() {
            return Long2ReferenceOpenCustomHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Long2ReferenceMap.Entry<V>>
    implements Long2ReferenceMap.FastEntrySet<V> {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Long2ReferenceMap.Entry<V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Long2ReferenceMap.Entry<V>> fastIterator() {
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
            long k = (Long)e.getKey();
            Object v = e.getValue();
            if (Long2ReferenceOpenCustomHashMap.this.strategy.equals(k, 0L)) {
                return Long2ReferenceOpenCustomHashMap.this.containsNullKey && Long2ReferenceOpenCustomHashMap.this.value[Long2ReferenceOpenCustomHashMap.this.n] == v;
            }
            long[] key = Long2ReferenceOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Long2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ReferenceOpenCustomHashMap.this.mask;
            long curr = key[pos];
            if (curr == 0L) {
                return false;
            }
            if (Long2ReferenceOpenCustomHashMap.this.strategy.equals(k, curr)) {
                return Long2ReferenceOpenCustomHashMap.this.value[pos] == v;
            }
            do {
                if ((curr = key[pos = pos + 1 & Long2ReferenceOpenCustomHashMap.this.mask]) != 0L) continue;
                return false;
            } while (!Long2ReferenceOpenCustomHashMap.this.strategy.equals(k, curr));
            return Long2ReferenceOpenCustomHashMap.this.value[pos] == v;
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
            long k = (Long)e.getKey();
            Object v = e.getValue();
            if (Long2ReferenceOpenCustomHashMap.this.strategy.equals(k, 0L)) {
                if (Long2ReferenceOpenCustomHashMap.this.containsNullKey && Long2ReferenceOpenCustomHashMap.this.value[Long2ReferenceOpenCustomHashMap.this.n] == v) {
                    Long2ReferenceOpenCustomHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            long[] key = Long2ReferenceOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Long2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ReferenceOpenCustomHashMap.this.mask;
            long curr = key[pos];
            if (curr == 0L) {
                return false;
            }
            if (Long2ReferenceOpenCustomHashMap.this.strategy.equals(curr, k)) {
                if (Long2ReferenceOpenCustomHashMap.this.value[pos] == v) {
                    Long2ReferenceOpenCustomHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Long2ReferenceOpenCustomHashMap.this.mask]) != 0L) continue;
                return false;
            } while (!Long2ReferenceOpenCustomHashMap.this.strategy.equals(curr, k) || Long2ReferenceOpenCustomHashMap.this.value[pos] != v);
            Long2ReferenceOpenCustomHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Long2ReferenceOpenCustomHashMap.this.size;
        }

        @Override
        public void clear() {
            Long2ReferenceOpenCustomHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Long2ReferenceMap.Entry<V>> consumer) {
            if (Long2ReferenceOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(new AbstractLong2ReferenceMap.BasicEntry(Long2ReferenceOpenCustomHashMap.this.key[Long2ReferenceOpenCustomHashMap.this.n], Long2ReferenceOpenCustomHashMap.this.value[Long2ReferenceOpenCustomHashMap.this.n]));
            }
            int pos = Long2ReferenceOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Long2ReferenceOpenCustomHashMap.this.key[pos] == 0L) continue;
                consumer.accept(new AbstractLong2ReferenceMap.BasicEntry(Long2ReferenceOpenCustomHashMap.this.key[pos], Long2ReferenceOpenCustomHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Long2ReferenceMap.Entry<V>> consumer) {
            AbstractLong2ReferenceMap.BasicEntry entry = new AbstractLong2ReferenceMap.BasicEntry();
            if (Long2ReferenceOpenCustomHashMap.this.containsNullKey) {
                entry.key = Long2ReferenceOpenCustomHashMap.this.key[Long2ReferenceOpenCustomHashMap.this.n];
                entry.value = Long2ReferenceOpenCustomHashMap.this.value[Long2ReferenceOpenCustomHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Long2ReferenceOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Long2ReferenceOpenCustomHashMap.this.key[pos] == 0L) continue;
                entry.key = Long2ReferenceOpenCustomHashMap.this.key[pos];
                entry.value = Long2ReferenceOpenCustomHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends Long2ReferenceOpenCustomHashMap<V>
    implements ObjectIterator<Long2ReferenceMap.Entry<V>> {
        private final Long2ReferenceOpenCustomHashMap<V> entry;

        private FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        @Override
        public Long2ReferenceOpenCustomHashMap<V> next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends Long2ReferenceOpenCustomHashMap<V>
    implements ObjectIterator<Long2ReferenceMap.Entry<V>> {
        private Long2ReferenceOpenCustomHashMap<V> entry;

        private EntryIterator() {
            super();
        }

        @Override
        public Long2ReferenceOpenCustomHashMap<V> next() {
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
        LongArrayList wrapped;

        private MapIterator() {
            this.pos = Long2ReferenceOpenCustomHashMap.this.n;
            this.last = -1;
            this.c = Long2ReferenceOpenCustomHashMap.this.size;
            this.mustReturnNullKey = Long2ReferenceOpenCustomHashMap.this.containsNullKey;
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
                this.last = Long2ReferenceOpenCustomHashMap.this.n;
                return this.last;
            }
            long[] key = Long2ReferenceOpenCustomHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                long k = this.wrapped.getLong(- this.pos - 1);
                int p = HashCommon.mix(Long2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ReferenceOpenCustomHashMap.this.mask;
                while (!Long2ReferenceOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                    p = p + 1 & Long2ReferenceOpenCustomHashMap.this.mask;
                }
                return p;
            } while (key[this.pos] == 0L);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            long[] key = Long2ReferenceOpenCustomHashMap.this.key;
            do {
                long curr;
                int last = pos;
                pos = last + 1 & Long2ReferenceOpenCustomHashMap.this.mask;
                do {
                    if ((curr = key[pos]) == 0L) {
                        key[last] = 0L;
                        Long2ReferenceOpenCustomHashMap.this.value[last] = null;
                        return;
                    }
                    int slot = HashCommon.mix(Long2ReferenceOpenCustomHashMap.this.strategy.hashCode(curr)) & Long2ReferenceOpenCustomHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Long2ReferenceOpenCustomHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new LongArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Long2ReferenceOpenCustomHashMap.this.value[last] = Long2ReferenceOpenCustomHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Long2ReferenceOpenCustomHashMap.this.n) {
                Long2ReferenceOpenCustomHashMap.this.containsNullKey = false;
                Long2ReferenceOpenCustomHashMap.this.value[Long2ReferenceOpenCustomHashMap.this.n] = null;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Long2ReferenceOpenCustomHashMap.this.remove(this.wrapped.getLong(- this.pos - 1));
                this.last = -1;
                return;
            }
            --Long2ReferenceOpenCustomHashMap.this.size;
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
    implements Long2ReferenceMap.Entry<V>,
    Map.Entry<Long, V> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public long getLongKey() {
            return Long2ReferenceOpenCustomHashMap.this.key[this.index];
        }

        @Override
        public V getValue() {
            return Long2ReferenceOpenCustomHashMap.this.value[this.index];
        }

        @Override
        public V setValue(V v) {
            Object oldValue = Long2ReferenceOpenCustomHashMap.this.value[this.index];
            Long2ReferenceOpenCustomHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Long getKey() {
            return Long2ReferenceOpenCustomHashMap.this.key[this.index];
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Long2ReferenceOpenCustomHashMap.this.strategy.equals(Long2ReferenceOpenCustomHashMap.this.key[this.index], (Long)e.getKey()) && Long2ReferenceOpenCustomHashMap.this.value[this.index] == e.getValue();
        }

        @Override
        public int hashCode() {
            return Long2ReferenceOpenCustomHashMap.this.strategy.hashCode(Long2ReferenceOpenCustomHashMap.this.key[this.index]) ^ (Long2ReferenceOpenCustomHashMap.this.value[this.index] == null ? 0 : System.identityHashCode(Long2ReferenceOpenCustomHashMap.this.value[this.index]));
        }

        public String toString() {
            return "" + Long2ReferenceOpenCustomHashMap.this.key[this.index] + "=>" + Long2ReferenceOpenCustomHashMap.this.value[this.index];
        }
    }

}

