/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.objects.AbstractObject2ShortMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import java.util.function.ToIntFunction;

public class Object2ShortOpenCustomHashMap<K>
extends AbstractObject2ShortMap<K>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient K[] key;
    protected transient short[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected Hash.Strategy<K> strategy;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Object2ShortMap.FastEntrySet<K> entries;
    protected transient ObjectSet<K> keys;
    protected transient ShortCollection values;

    public Object2ShortOpenCustomHashMap(int expected, float f, Hash.Strategy<K> strategy) {
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
        this.key = new Object[this.n + 1];
        this.value = new short[this.n + 1];
    }

    public Object2ShortOpenCustomHashMap(int expected, Hash.Strategy<K> strategy) {
        this(expected, 0.75f, strategy);
    }

    public Object2ShortOpenCustomHashMap(Hash.Strategy<K> strategy) {
        this(16, 0.75f, strategy);
    }

    public Object2ShortOpenCustomHashMap(Map<? extends K, ? extends Short> m, float f, Hash.Strategy<K> strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Object2ShortOpenCustomHashMap(Map<? extends K, ? extends Short> m, Hash.Strategy<K> strategy) {
        this(m, 0.75f, strategy);
    }

    public Object2ShortOpenCustomHashMap(Object2ShortMap<K> m, float f, Hash.Strategy<K> strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Object2ShortOpenCustomHashMap(Object2ShortMap<K> m, Hash.Strategy<K> strategy) {
        this(m, 0.75f, strategy);
    }

    public Object2ShortOpenCustomHashMap(K[] k, short[] v, float f, Hash.Strategy<K> strategy) {
        this(k.length, f, strategy);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Object2ShortOpenCustomHashMap(K[] k, short[] v, Hash.Strategy<K> strategy) {
        this(k, v, 0.75f, strategy);
    }

    public Hash.Strategy<K> strategy() {
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
        this.key[this.n] = null;
        short oldValue = this.value[this.n];
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends Short> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }

    private int find(K k) {
        if (this.strategy.equals(k, null)) {
            return this.containsNullKey ? this.n : - this.n + 1;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return - pos + 1;
        }
        if (this.strategy.equals(k, curr)) {
            return pos;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return - pos + 1;
        } while (!this.strategy.equals(k, curr));
        return pos;
    }

    private void insert(int pos, K k, short v) {
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
    public short put(K k, short v) {
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

    public short addTo(K k, short incr) {
        int pos;
        if (this.strategy.equals(k, null)) {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        } else {
            K[] key = this.key;
            pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
            K curr = key[pos];
            if (curr != null) {
                if (this.strategy.equals(curr, k)) {
                    return this.addToValue(pos, incr);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != null) {
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
                int slot = HashCommon.mix(this.strategy.hashCode(curr)) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            } while (true);
            key[last] = curr;
            this.value[last] = this.value[pos];
        } while (true);
    }

    @Override
    public short removeShort(Object k) {
        if (this.strategy.equals(k, null)) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        return this.removeEntry(pos);
    }

    @Override
    public short getShort(Object k) {
        if (this.strategy.equals(k, null)) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return this.defRetValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return this.defRetValue;
        } while (!this.strategy.equals(k, curr));
        return this.value[pos];
    }

    @Override
    public boolean containsKey(Object k) {
        if (this.strategy.equals(k, null)) {
            return this.containsNullKey;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return false;
        }
        if (this.strategy.equals(k, curr)) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return false;
        } while (!this.strategy.equals(k, curr));
        return true;
    }

    @Override
    public boolean containsValue(short v) {
        short[] value = this.value;
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
    public short getOrDefault(Object k, short defaultValue) {
        if (this.strategy.equals(k, null)) {
            return this.containsNullKey ? this.value[this.n] : defaultValue;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return defaultValue;
        }
        if (this.strategy.equals(k, curr)) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return defaultValue;
        } while (!this.strategy.equals(k, curr));
        return this.value[pos];
    }

    @Override
    public short putIfAbsent(K k, short v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(Object k, short v) {
        if (this.strategy.equals(k, null)) {
            if (this.containsNullKey && v == this.value[this.n]) {
                this.removeNullEntry();
                return true;
            }
            return false;
        }
        K[] key = this.key;
        int pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
        K curr = key[pos];
        if (curr == null) {
            return false;
        }
        if (this.strategy.equals(k, curr) && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return false;
        } while (!this.strategy.equals(k, curr) || v != this.value[pos]);
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(K k, short oldValue, short v) {
        int pos = this.find(k);
        if (pos < 0 || oldValue != this.value[pos]) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public short replace(K k, short v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        short oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public short computeShortIfAbsent(K k, ToIntFunction<? super K> mappingFunction) {
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
    public short computeShortIfPresent(K k, BiFunction<? super K, ? super Short, ? extends Short> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Short newValue = remappingFunction.apply(k, (Short)this.value[pos]);
        if (newValue == null) {
            if (this.strategy.equals(k, null)) {
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
    public short computeShort(K k, BiFunction<? super K, ? super Short, ? extends Short> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Short newValue = remappingFunction.apply(k, pos >= 0 ? Short.valueOf(this.value[pos]) : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (this.strategy.equals(k, null)) {
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
    public short mergeShort(K k, short v, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Short newValue = remappingFunction.apply((Short)this.value[pos], (Short)v);
        if (newValue == null) {
            if (this.strategy.equals(k, null)) {
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

    public Object2ShortMap.FastEntrySet<K> object2ShortEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public ObjectSet<K> keySet() {
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
                    return Object2ShortOpenCustomHashMap.this.size;
                }

                @Override
                public boolean contains(short v) {
                    return Object2ShortOpenCustomHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Object2ShortOpenCustomHashMap.this.clear();
                }

                @Override
                public void forEach(IntConsumer consumer) {
                    if (Object2ShortOpenCustomHashMap.this.containsNullKey) {
                        consumer.accept(Object2ShortOpenCustomHashMap.this.value[Object2ShortOpenCustomHashMap.this.n]);
                    }
                    int pos = Object2ShortOpenCustomHashMap.this.n;
                    while (pos-- != 0) {
                        if (Object2ShortOpenCustomHashMap.this.key[pos] == null) continue;
                        consumer.accept(Object2ShortOpenCustomHashMap.this.value[pos]);
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
        short[] value = this.value;
        int mask = newN - 1;
        Object[] newKey = new Object[newN + 1];
        short[] newValue = new short[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (key[--i] == null) {
            }
            int pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask;
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

    public Object2ShortOpenCustomHashMap<K> clone() {
        Object2ShortOpenCustomHashMap c;
        try {
            c = (Object2ShortOpenCustomHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (Object[])this.key.clone();
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
            while (this.key[i] == null) {
                ++i;
            }
            if (this != this.key[i]) {
                t = this.strategy.hashCode(this.key[i]);
            }
            h += (t ^= this.value[i]);
            ++i;
        }
        if (this.containsNullKey) {
            h += this.value[this.n];
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        K[] key = this.key;
        short[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeObject(key[e]);
            s.writeShort(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new Object[this.n + 1];
        Object[] key = this.key;
        this.value = new short[this.n + 1];
        short[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            Object k = s.readObject();
            short v = s.readShort();
            if (this.strategy.equals(k, null)) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;
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
    extends Object2ShortOpenCustomHashMap<K>
    implements ShortIterator {
        public ValueIterator() {
            super();
        }

        @Override
        public short nextShort() {
            return Object2ShortOpenCustomHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractObjectSet<K> {
        private KeySet() {
        }

        @Override
        public ObjectIterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public void forEach(Consumer<? super K> consumer) {
            if (Object2ShortOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(Object2ShortOpenCustomHashMap.this.key[Object2ShortOpenCustomHashMap.this.n]);
            }
            int pos = Object2ShortOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                Object k = Object2ShortOpenCustomHashMap.this.key[pos];
                if (k == null) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Object2ShortOpenCustomHashMap.this.size;
        }

        @Override
        public boolean contains(Object k) {
            return Object2ShortOpenCustomHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(Object k) {
            int oldSize = Object2ShortOpenCustomHashMap.this.size;
            Object2ShortOpenCustomHashMap.this.removeShort(k);
            return Object2ShortOpenCustomHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Object2ShortOpenCustomHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends Object2ShortOpenCustomHashMap<K>
    implements ObjectIterator<K> {
        public KeyIterator() {
            super();
        }

        @Override
        public K next() {
            return Object2ShortOpenCustomHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Object2ShortMap.Entry<K>>
    implements Object2ShortMap.FastEntrySet<K> {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Object2ShortMap.Entry<K>> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Object2ShortMap.Entry<K>> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            Object k = e.getKey();
            short v = (Short)e.getValue();
            if (Object2ShortOpenCustomHashMap.this.strategy.equals(k, null)) {
                return Object2ShortOpenCustomHashMap.this.containsNullKey && Object2ShortOpenCustomHashMap.this.value[Object2ShortOpenCustomHashMap.this.n] == v;
            }
            K[] key = Object2ShortOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Object2ShortOpenCustomHashMap.this.strategy.hashCode(k)) & Object2ShortOpenCustomHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (Object2ShortOpenCustomHashMap.this.strategy.equals(k, curr)) {
                return Object2ShortOpenCustomHashMap.this.value[pos] == v;
            }
            do {
                if ((curr = key[pos = pos + 1 & Object2ShortOpenCustomHashMap.this.mask]) != null) continue;
                return false;
            } while (!Object2ShortOpenCustomHashMap.this.strategy.equals(k, curr));
            return Object2ShortOpenCustomHashMap.this.value[pos] == v;
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Short)) {
                return false;
            }
            Object k = e.getKey();
            short v = (Short)e.getValue();
            if (Object2ShortOpenCustomHashMap.this.strategy.equals(k, null)) {
                if (Object2ShortOpenCustomHashMap.this.containsNullKey && Object2ShortOpenCustomHashMap.this.value[Object2ShortOpenCustomHashMap.this.n] == v) {
                    Object2ShortOpenCustomHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            K[] key = Object2ShortOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Object2ShortOpenCustomHashMap.this.strategy.hashCode(k)) & Object2ShortOpenCustomHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (Object2ShortOpenCustomHashMap.this.strategy.equals(curr, k)) {
                if (Object2ShortOpenCustomHashMap.this.value[pos] == v) {
                    Object2ShortOpenCustomHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Object2ShortOpenCustomHashMap.this.mask]) != null) continue;
                return false;
            } while (!Object2ShortOpenCustomHashMap.this.strategy.equals(curr, k) || Object2ShortOpenCustomHashMap.this.value[pos] != v);
            Object2ShortOpenCustomHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Object2ShortOpenCustomHashMap.this.size;
        }

        @Override
        public void clear() {
            Object2ShortOpenCustomHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Object2ShortMap.Entry<K>> consumer) {
            if (Object2ShortOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(new AbstractObject2ShortMap.BasicEntry(Object2ShortOpenCustomHashMap.this.key[Object2ShortOpenCustomHashMap.this.n], Object2ShortOpenCustomHashMap.this.value[Object2ShortOpenCustomHashMap.this.n]));
            }
            int pos = Object2ShortOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Object2ShortOpenCustomHashMap.this.key[pos] == null) continue;
                consumer.accept(new AbstractObject2ShortMap.BasicEntry(Object2ShortOpenCustomHashMap.this.key[pos], Object2ShortOpenCustomHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Object2ShortMap.Entry<K>> consumer) {
            AbstractObject2ShortMap.BasicEntry entry = new AbstractObject2ShortMap.BasicEntry();
            if (Object2ShortOpenCustomHashMap.this.containsNullKey) {
                entry.key = Object2ShortOpenCustomHashMap.this.key[Object2ShortOpenCustomHashMap.this.n];
                entry.value = Object2ShortOpenCustomHashMap.this.value[Object2ShortOpenCustomHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Object2ShortOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Object2ShortOpenCustomHashMap.this.key[pos] == null) continue;
                entry.key = Object2ShortOpenCustomHashMap.this.key[pos];
                entry.value = Object2ShortOpenCustomHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends Object2ShortOpenCustomHashMap<K>
    implements ObjectIterator<Object2ShortMap.Entry<K>> {
        private final Object2ShortOpenCustomHashMap<K> entry;

        private FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        @Override
        public Object2ShortOpenCustomHashMap<K> next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends Object2ShortOpenCustomHashMap<K>
    implements ObjectIterator<Object2ShortMap.Entry<K>> {
        private Object2ShortOpenCustomHashMap<K> entry;

        private EntryIterator() {
            super();
        }

        @Override
        public Object2ShortOpenCustomHashMap<K> next() {
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
        ObjectArrayList<K> wrapped;

        private MapIterator() {
            this.pos = Object2ShortOpenCustomHashMap.this.n;
            this.last = -1;
            this.c = Object2ShortOpenCustomHashMap.this.size;
            this.mustReturnNullKey = Object2ShortOpenCustomHashMap.this.containsNullKey;
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
                this.last = Object2ShortOpenCustomHashMap.this.n;
                return this.last;
            }
            K[] key = Object2ShortOpenCustomHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                K k = this.wrapped.get(- this.pos - 1);
                int p = HashCommon.mix(Object2ShortOpenCustomHashMap.this.strategy.hashCode(k)) & Object2ShortOpenCustomHashMap.this.mask;
                while (!Object2ShortOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                    p = p + 1 & Object2ShortOpenCustomHashMap.this.mask;
                }
                return p;
            } while (key[this.pos] == null);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            K[] key = Object2ShortOpenCustomHashMap.this.key;
            do {
                Object curr;
                int last = pos;
                pos = last + 1 & Object2ShortOpenCustomHashMap.this.mask;
                do {
                    if ((curr = key[pos]) == null) {
                        key[last] = null;
                        return;
                    }
                    int slot = HashCommon.mix(Object2ShortOpenCustomHashMap.this.strategy.hashCode(curr)) & Object2ShortOpenCustomHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Object2ShortOpenCustomHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new ObjectArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Object2ShortOpenCustomHashMap.this.value[last] = Object2ShortOpenCustomHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Object2ShortOpenCustomHashMap.this.n) {
                Object2ShortOpenCustomHashMap.this.containsNullKey = false;
                Object2ShortOpenCustomHashMap.this.key[Object2ShortOpenCustomHashMap.this.n] = null;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Object2ShortOpenCustomHashMap.this.removeShort(this.wrapped.set(- this.pos - 1, null));
                this.last = -1;
                return;
            }
            --Object2ShortOpenCustomHashMap.this.size;
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
    implements Object2ShortMap.Entry<K>,
    Map.Entry<K, Short> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public K getKey() {
            return Object2ShortOpenCustomHashMap.this.key[this.index];
        }

        @Override
        public short getShortValue() {
            return Object2ShortOpenCustomHashMap.this.value[this.index];
        }

        @Override
        public short setValue(short v) {
            short oldValue = Object2ShortOpenCustomHashMap.this.value[this.index];
            Object2ShortOpenCustomHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Short getValue() {
            return Object2ShortOpenCustomHashMap.this.value[this.index];
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
            return Object2ShortOpenCustomHashMap.this.strategy.equals(Object2ShortOpenCustomHashMap.this.key[this.index], e.getKey()) && Object2ShortOpenCustomHashMap.this.value[this.index] == (Short)e.getValue();
        }

        @Override
        public int hashCode() {
            return Object2ShortOpenCustomHashMap.this.strategy.hashCode(Object2ShortOpenCustomHashMap.this.key[this.index]) ^ Object2ShortOpenCustomHashMap.this.value[this.index];
        }

        public String toString() {
            return Object2ShortOpenCustomHashMap.this.key[this.index] + "=>" + Object2ShortOpenCustomHashMap.this.value[this.index];
        }
    }

}

