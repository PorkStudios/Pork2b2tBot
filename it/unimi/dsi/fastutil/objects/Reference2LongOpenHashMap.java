/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReference2LongMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2LongMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
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
import java.util.function.ToLongFunction;

public class Reference2LongOpenHashMap<K>
extends AbstractReference2LongMap<K>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient K[] key;
    protected transient long[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Reference2LongMap.FastEntrySet<K> entries;
    protected transient ReferenceSet<K> keys;
    protected transient LongCollection values;

    public Reference2LongOpenHashMap(int expected, float f) {
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
        this.key = new Object[this.n + 1];
        this.value = new long[this.n + 1];
    }

    public Reference2LongOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Reference2LongOpenHashMap() {
        this(16, 0.75f);
    }

    public Reference2LongOpenHashMap(Map<? extends K, ? extends Long> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Reference2LongOpenHashMap(Map<? extends K, ? extends Long> m) {
        this(m, 0.75f);
    }

    public Reference2LongOpenHashMap(Reference2LongMap<K> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Reference2LongOpenHashMap(Reference2LongMap<K> m) {
        this(m, 0.75f);
    }

    public Reference2LongOpenHashMap(K[] k, long[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Reference2LongOpenHashMap(K[] k, long[] v) {
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
        this.key[this.n] = null;
        long oldValue = this.value[this.n];
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends Long> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }

    private int find(K k) {
        if (k == null) {
            return this.containsNullKey ? this.n : - this.n + 1;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return - pos + 1;
        }
        if (k == curr) {
            return pos;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return - pos + 1;
        } while (k != curr);
        return pos;
    }

    private void insert(int pos, K k, long v) {
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
    public long put(K k, long v) {
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

    public long addTo(K k, long incr) {
        int pos;
        if (k == null) {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        } else {
            K[] key = this.key;
            pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;
            K curr = key[pos];
            if (curr != null) {
                if (curr == k) {
                    return this.addToValue(pos, incr);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != null) {
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
        K[] key = this.key;
        do {
            K curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if ((curr = key[pos]) == null) {
                    key[last] = null;
                    return;
                }
                int slot = HashCommon.mix(System.identityHashCode(curr)) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            } while (true);
            key[last] = curr;
            this.value[last] = this.value[pos];
        } while (true);
    }

    @Override
    public long removeLong(Object k) {
        if (k == null) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return this.defRetValue;
        }
        if (k == curr) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return this.defRetValue;
        } while (k != curr);
        return this.removeEntry(pos);
    }

    @Override
    public long getLong(Object k) {
        if (k == null) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return this.defRetValue;
        }
        if (k == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return this.defRetValue;
        } while (k != curr);
        return this.value[pos];
    }

    @Override
    public boolean containsKey(Object k) {
        if (k == null) {
            return this.containsNullKey;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return false;
        }
        if (k == curr) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return false;
        } while (k != curr);
        return true;
    }

    @Override
    public boolean containsValue(long v) {
        long[] value = this.value;
        K[] key = this.key;
        if (this.containsNullKey && value[this.n] == v) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (key[i] == null || value[i] != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public long getOrDefault(Object k, long defaultValue) {
        if (k == null) {
            return this.containsNullKey ? this.value[this.n] : defaultValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return defaultValue;
        }
        if (k == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return defaultValue;
        } while (k != curr);
        return this.value[pos];
    }

    @Override
    public long putIfAbsent(K k, long v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(Object k, long v) {
        if (k == null) {
            if (this.containsNullKey && v == this.value[this.n]) {
                this.removeNullEntry();
                return true;
            }
            return false;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return false;
        }
        if (k == curr && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return false;
        } while (k != curr || v != this.value[pos]);
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(K k, long oldValue, long v) {
        int pos = this.find(k);
        if (pos < 0 || oldValue != this.value[pos]) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public long replace(K k, long v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        long oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public long computeLongIfAbsent(K k, ToLongFunction<? super K> mappingFunction) {
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
    public long computeLongIfPresent(K k, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Long newValue = remappingFunction.apply(k, (Long)this.value[pos]);
        if (newValue == null) {
            if (k == null) {
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
    public long computeLong(K k, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Long newValue = remappingFunction.apply(k, pos >= 0 ? Long.valueOf(this.value[pos]) : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (k == null) {
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
    public long mergeLong(K k, long v, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Long newValue = remappingFunction.apply((Long)this.value[pos], (Long)v);
        if (newValue == null) {
            if (k == null) {
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
        Arrays.fill(this.key, null);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public Reference2LongMap.FastEntrySet<K> reference2LongEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public ReferenceSet<K> keySet() {
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
                    return Reference2LongOpenHashMap.this.size;
                }

                @Override
                public boolean contains(long v) {
                    return Reference2LongOpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Reference2LongOpenHashMap.this.clear();
                }

                @Override
                public void forEach(LongConsumer consumer) {
                    if (Reference2LongOpenHashMap.this.containsNullKey) {
                        consumer.accept(Reference2LongOpenHashMap.this.value[Reference2LongOpenHashMap.this.n]);
                    }
                    int pos = Reference2LongOpenHashMap.this.n;
                    while (pos-- != 0) {
                        if (Reference2LongOpenHashMap.this.key[pos] == null) continue;
                        consumer.accept(Reference2LongOpenHashMap.this.value[pos]);
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
        K[] key = this.key;
        long[] value = this.value;
        int mask = newN - 1;
        Object[] newKey = new Object[newN + 1];
        long[] newValue = new long[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (key[--i] == null) {
            }
            int pos = HashCommon.mix(System.identityHashCode(key[i])) & mask;
            if (newKey[pos] != null) {
                while (newKey[pos = pos + 1 & mask] != null) {
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

    public Reference2LongOpenHashMap<K> clone() {
        Reference2LongOpenHashMap c;
        try {
            c = (Reference2LongOpenHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (Object[])this.key.clone();
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
            while (this.key[i] == null) {
                ++i;
            }
            if (this != this.key[i]) {
                t = System.identityHashCode(this.key[i]);
            }
            h += (t ^= HashCommon.long2int(this.value[i]));
            ++i;
        }
        if (this.containsNullKey) {
            h += HashCommon.long2int(this.value[this.n]);
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        K[] key = this.key;
        long[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeObject(key[e]);
            s.writeLong(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new Object[this.n + 1];
        Object[] key = this.key;
        this.value = new long[this.n + 1];
        long[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            Object k = s.readObject();
            long v = s.readLong();
            if (k == null) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;
                while (key[pos] != null) {
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
    extends Reference2LongOpenHashMap<K>
    implements LongIterator {
        public ValueIterator() {
            super();
        }

        @Override
        public long nextLong() {
            return Reference2LongOpenHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractReferenceSet<K> {
        private KeySet() {
        }

        @Override
        public ObjectIterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public void forEach(Consumer<? super K> consumer) {
            if (Reference2LongOpenHashMap.this.containsNullKey) {
                consumer.accept(Reference2LongOpenHashMap.this.key[Reference2LongOpenHashMap.this.n]);
            }
            int pos = Reference2LongOpenHashMap.this.n;
            while (pos-- != 0) {
                Object k = Reference2LongOpenHashMap.this.key[pos];
                if (k == null) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Reference2LongOpenHashMap.this.size;
        }

        @Override
        public boolean contains(Object k) {
            return Reference2LongOpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(Object k) {
            int oldSize = Reference2LongOpenHashMap.this.size;
            Reference2LongOpenHashMap.this.removeLong(k);
            return Reference2LongOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Reference2LongOpenHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends Reference2LongOpenHashMap<K>
    implements ObjectIterator<K> {
        public KeyIterator() {
            super();
        }

        @Override
        public K next() {
            return Reference2LongOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Reference2LongMap.Entry<K>>
    implements Reference2LongMap.FastEntrySet<K> {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Reference2LongMap.Entry<K>> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Reference2LongMap.Entry<K>> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Long)) {
                return false;
            }
            Object k = e.getKey();
            long v = (Long)e.getValue();
            if (k == null) {
                return Reference2LongOpenHashMap.this.containsNullKey && Reference2LongOpenHashMap.this.value[Reference2LongOpenHashMap.this.n] == v;
            }
            K[] key = Reference2LongOpenHashMap.this.key;
            int pos = HashCommon.mix(System.identityHashCode(k)) & Reference2LongOpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (k == curr) {
                return Reference2LongOpenHashMap.this.value[pos] == v;
            }
            do {
                if ((curr = key[pos = pos + 1 & Reference2LongOpenHashMap.this.mask]) != null) continue;
                return false;
            } while (k != curr);
            return Reference2LongOpenHashMap.this.value[pos] == v;
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Long)) {
                return false;
            }
            Object k = e.getKey();
            long v = (Long)e.getValue();
            if (k == null) {
                if (Reference2LongOpenHashMap.this.containsNullKey && Reference2LongOpenHashMap.this.value[Reference2LongOpenHashMap.this.n] == v) {
                    Reference2LongOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            K[] key = Reference2LongOpenHashMap.this.key;
            int pos = HashCommon.mix(System.identityHashCode(k)) & Reference2LongOpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (curr == k) {
                if (Reference2LongOpenHashMap.this.value[pos] == v) {
                    Reference2LongOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Reference2LongOpenHashMap.this.mask]) != null) continue;
                return false;
            } while (curr != k || Reference2LongOpenHashMap.this.value[pos] != v);
            Reference2LongOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Reference2LongOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Reference2LongOpenHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Reference2LongMap.Entry<K>> consumer) {
            if (Reference2LongOpenHashMap.this.containsNullKey) {
                consumer.accept(new AbstractReference2LongMap.BasicEntry(Reference2LongOpenHashMap.this.key[Reference2LongOpenHashMap.this.n], Reference2LongOpenHashMap.this.value[Reference2LongOpenHashMap.this.n]));
            }
            int pos = Reference2LongOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Reference2LongOpenHashMap.this.key[pos] == null) continue;
                consumer.accept(new AbstractReference2LongMap.BasicEntry(Reference2LongOpenHashMap.this.key[pos], Reference2LongOpenHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Reference2LongMap.Entry<K>> consumer) {
            AbstractReference2LongMap.BasicEntry entry = new AbstractReference2LongMap.BasicEntry();
            if (Reference2LongOpenHashMap.this.containsNullKey) {
                entry.key = Reference2LongOpenHashMap.this.key[Reference2LongOpenHashMap.this.n];
                entry.value = Reference2LongOpenHashMap.this.value[Reference2LongOpenHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Reference2LongOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Reference2LongOpenHashMap.this.key[pos] == null) continue;
                entry.key = Reference2LongOpenHashMap.this.key[pos];
                entry.value = Reference2LongOpenHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends Reference2LongOpenHashMap<K>
    implements ObjectIterator<Reference2LongMap.Entry<K>> {
        private final Reference2LongOpenHashMap<K> entry;

        private FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        @Override
        public Reference2LongOpenHashMap<K> next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends Reference2LongOpenHashMap<K>
    implements ObjectIterator<Reference2LongMap.Entry<K>> {
        private Reference2LongOpenHashMap<K> entry;

        private EntryIterator() {
            super();
        }

        @Override
        public Reference2LongOpenHashMap<K> next() {
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
        ReferenceArrayList<K> wrapped;

        private MapIterator() {
            this.pos = Reference2LongOpenHashMap.this.n;
            this.last = -1;
            this.c = Reference2LongOpenHashMap.this.size;
            this.mustReturnNullKey = Reference2LongOpenHashMap.this.containsNullKey;
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
                this.last = Reference2LongOpenHashMap.this.n;
                return this.last;
            }
            K[] key = Reference2LongOpenHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                K k = this.wrapped.get(- this.pos - 1);
                int p = HashCommon.mix(System.identityHashCode(k)) & Reference2LongOpenHashMap.this.mask;
                while (k != key[p]) {
                    p = p + 1 & Reference2LongOpenHashMap.this.mask;
                }
                return p;
            } while (key[this.pos] == null);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            K[] key = Reference2LongOpenHashMap.this.key;
            do {
                Object curr;
                int last = pos;
                pos = last + 1 & Reference2LongOpenHashMap.this.mask;
                do {
                    if ((curr = key[pos]) == null) {
                        key[last] = null;
                        return;
                    }
                    int slot = HashCommon.mix(System.identityHashCode(curr)) & Reference2LongOpenHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Reference2LongOpenHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new ReferenceArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Reference2LongOpenHashMap.this.value[last] = Reference2LongOpenHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Reference2LongOpenHashMap.this.n) {
                Reference2LongOpenHashMap.this.containsNullKey = false;
                Reference2LongOpenHashMap.this.key[Reference2LongOpenHashMap.this.n] = null;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Reference2LongOpenHashMap.this.removeLong(this.wrapped.set(- this.pos - 1, null));
                this.last = -1;
                return;
            }
            --Reference2LongOpenHashMap.this.size;
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
    implements Reference2LongMap.Entry<K>,
    Map.Entry<K, Long> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public K getKey() {
            return Reference2LongOpenHashMap.this.key[this.index];
        }

        @Override
        public long getLongValue() {
            return Reference2LongOpenHashMap.this.value[this.index];
        }

        @Override
        public long setValue(long v) {
            long oldValue = Reference2LongOpenHashMap.this.value[this.index];
            Reference2LongOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Long getValue() {
            return Reference2LongOpenHashMap.this.value[this.index];
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
            return Reference2LongOpenHashMap.this.key[this.index] == e.getKey() && Reference2LongOpenHashMap.this.value[this.index] == (Long)e.getValue();
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(Reference2LongOpenHashMap.this.key[this.index]) ^ HashCommon.long2int(Reference2LongOpenHashMap.this.value[this.index]);
        }

        public String toString() {
            return Reference2LongOpenHashMap.this.key[this.index] + "=>" + Reference2LongOpenHashMap.this.value[this.index];
        }
    }

}

