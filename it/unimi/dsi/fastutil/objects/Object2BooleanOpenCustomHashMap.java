/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2BooleanMap;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import java.util.function.Predicate;

public class Object2BooleanOpenCustomHashMap<K>
extends AbstractObject2BooleanMap<K>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient K[] key;
    protected transient boolean[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected Hash.Strategy<K> strategy;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Object2BooleanMap.FastEntrySet<K> entries;
    protected transient ObjectSet<K> keys;
    protected transient BooleanCollection values;

    public Object2BooleanOpenCustomHashMap(int expected, float f, Hash.Strategy<K> strategy) {
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
        this.value = new boolean[this.n + 1];
    }

    public Object2BooleanOpenCustomHashMap(int expected, Hash.Strategy<K> strategy) {
        this(expected, 0.75f, strategy);
    }

    public Object2BooleanOpenCustomHashMap(Hash.Strategy<K> strategy) {
        this(16, 0.75f, strategy);
    }

    public Object2BooleanOpenCustomHashMap(Map<? extends K, ? extends Boolean> m, float f, Hash.Strategy<K> strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Object2BooleanOpenCustomHashMap(Map<? extends K, ? extends Boolean> m, Hash.Strategy<K> strategy) {
        this(m, 0.75f, strategy);
    }

    public Object2BooleanOpenCustomHashMap(Object2BooleanMap<K> m, float f, Hash.Strategy<K> strategy) {
        this(m.size(), f, strategy);
        this.putAll(m);
    }

    public Object2BooleanOpenCustomHashMap(Object2BooleanMap<K> m, Hash.Strategy<K> strategy) {
        this(m, 0.75f, strategy);
    }

    public Object2BooleanOpenCustomHashMap(K[] k, boolean[] v, float f, Hash.Strategy<K> strategy) {
        this(k.length, f, strategy);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Object2BooleanOpenCustomHashMap(K[] k, boolean[] v, Hash.Strategy<K> strategy) {
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

    private boolean removeEntry(int pos) {
        boolean oldValue = this.value[pos];
        --this.size;
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private boolean removeNullEntry() {
        this.containsNullKey = false;
        this.key[this.n] = null;
        boolean oldValue = this.value[this.n];
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends Boolean> m) {
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

    private void insert(int pos, K k, boolean v) {
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
    public boolean put(K k, boolean v) {
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return this.defRetValue;
        }
        boolean oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
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
    public boolean removeBoolean(Object k) {
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
    public boolean getBoolean(Object k) {
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
    public boolean containsValue(boolean v) {
        boolean[] value = this.value;
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
    public boolean getOrDefault(Object k, boolean defaultValue) {
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
    public boolean putIfAbsent(K k, boolean v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(Object k, boolean v) {
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
    public boolean replace(K k, boolean oldValue, boolean v) {
        int pos = this.find(k);
        if (pos < 0 || oldValue != this.value[pos]) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public boolean replace(K k, boolean v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        boolean oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public boolean computeBooleanIfAbsent(K k, Predicate<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        boolean newValue = mappingFunction.test(k);
        this.insert(- pos - 1, k, newValue);
        return newValue;
    }

    @Override
    public boolean computeBooleanIfPresent(K k, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Boolean newValue = remappingFunction.apply(k, (Boolean)this.value[pos]);
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
    public boolean computeBoolean(K k, BiFunction<? super K, ? super Boolean, ? extends Boolean> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Boolean newValue = remappingFunction.apply(k, pos >= 0 ? Boolean.valueOf(this.value[pos]) : null);
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
        boolean newVal = newValue;
        if (pos < 0) {
            this.insert(- pos - 1, k, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public boolean mergeBoolean(K k, boolean v, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Boolean newValue = remappingFunction.apply((Boolean)this.value[pos], (Boolean)v);
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

    public Object2BooleanMap.FastEntrySet<K> object2BooleanEntrySet() {
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
    public BooleanCollection values() {
        if (this.values == null) {
            this.values = new AbstractBooleanCollection(){

                @Override
                public BooleanIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Object2BooleanOpenCustomHashMap.this.size;
                }

                @Override
                public boolean contains(boolean v) {
                    return Object2BooleanOpenCustomHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Object2BooleanOpenCustomHashMap.this.clear();
                }

                @Override
                public void forEach(BooleanConsumer consumer) {
                    if (Object2BooleanOpenCustomHashMap.this.containsNullKey) {
                        consumer.accept(Object2BooleanOpenCustomHashMap.this.value[Object2BooleanOpenCustomHashMap.this.n]);
                    }
                    int pos = Object2BooleanOpenCustomHashMap.this.n;
                    while (pos-- != 0) {
                        if (Object2BooleanOpenCustomHashMap.this.key[pos] == null) continue;
                        consumer.accept(Object2BooleanOpenCustomHashMap.this.value[pos]);
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
        boolean[] value = this.value;
        int mask = newN - 1;
        Object[] newKey = new Object[newN + 1];
        boolean[] newValue = new boolean[newN + 1];
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

    public Object2BooleanOpenCustomHashMap<K> clone() {
        Object2BooleanOpenCustomHashMap c;
        try {
            c = (Object2BooleanOpenCustomHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (Object[])this.key.clone();
        c.value = (boolean[])this.value.clone();
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
            h += (t ^= this.value[i] ? 1231 : 1237);
            ++i;
        }
        if (this.containsNullKey) {
            h += this.value[this.n] ? 1231 : 1237;
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        K[] key = this.key;
        boolean[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeObject(key[e]);
            s.writeBoolean(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new Object[this.n + 1];
        Object[] key = this.key;
        this.value = new boolean[this.n + 1];
        boolean[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            Object k = s.readObject();
            boolean v = s.readBoolean();
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
    extends Object2BooleanOpenCustomHashMap<K>
    implements BooleanIterator {
        public ValueIterator() {
            super();
        }

        @Override
        public boolean nextBoolean() {
            return Object2BooleanOpenCustomHashMap.this.value[this.nextEntry()];
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
            if (Object2BooleanOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(Object2BooleanOpenCustomHashMap.this.key[Object2BooleanOpenCustomHashMap.this.n]);
            }
            int pos = Object2BooleanOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                Object k = Object2BooleanOpenCustomHashMap.this.key[pos];
                if (k == null) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Object2BooleanOpenCustomHashMap.this.size;
        }

        @Override
        public boolean contains(Object k) {
            return Object2BooleanOpenCustomHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(Object k) {
            int oldSize = Object2BooleanOpenCustomHashMap.this.size;
            Object2BooleanOpenCustomHashMap.this.removeBoolean(k);
            return Object2BooleanOpenCustomHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Object2BooleanOpenCustomHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends Object2BooleanOpenCustomHashMap<K>
    implements ObjectIterator<K> {
        public KeyIterator() {
            super();
        }

        @Override
        public K next() {
            return Object2BooleanOpenCustomHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Object2BooleanMap.Entry<K>>
    implements Object2BooleanMap.FastEntrySet<K> {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Object2BooleanMap.Entry<K>> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Object2BooleanMap.Entry<K>> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            Object k = e.getKey();
            boolean v = (Boolean)e.getValue();
            if (Object2BooleanOpenCustomHashMap.this.strategy.equals(k, null)) {
                return Object2BooleanOpenCustomHashMap.this.containsNullKey && Object2BooleanOpenCustomHashMap.this.value[Object2BooleanOpenCustomHashMap.this.n] == v;
            }
            K[] key = Object2BooleanOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Object2BooleanOpenCustomHashMap.this.strategy.hashCode(k)) & Object2BooleanOpenCustomHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (Object2BooleanOpenCustomHashMap.this.strategy.equals(k, curr)) {
                return Object2BooleanOpenCustomHashMap.this.value[pos] == v;
            }
            do {
                if ((curr = key[pos = pos + 1 & Object2BooleanOpenCustomHashMap.this.mask]) != null) continue;
                return false;
            } while (!Object2BooleanOpenCustomHashMap.this.strategy.equals(k, curr));
            return Object2BooleanOpenCustomHashMap.this.value[pos] == v;
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Boolean)) {
                return false;
            }
            Object k = e.getKey();
            boolean v = (Boolean)e.getValue();
            if (Object2BooleanOpenCustomHashMap.this.strategy.equals(k, null)) {
                if (Object2BooleanOpenCustomHashMap.this.containsNullKey && Object2BooleanOpenCustomHashMap.this.value[Object2BooleanOpenCustomHashMap.this.n] == v) {
                    Object2BooleanOpenCustomHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            K[] key = Object2BooleanOpenCustomHashMap.this.key;
            int pos = HashCommon.mix(Object2BooleanOpenCustomHashMap.this.strategy.hashCode(k)) & Object2BooleanOpenCustomHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (Object2BooleanOpenCustomHashMap.this.strategy.equals(curr, k)) {
                if (Object2BooleanOpenCustomHashMap.this.value[pos] == v) {
                    Object2BooleanOpenCustomHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Object2BooleanOpenCustomHashMap.this.mask]) != null) continue;
                return false;
            } while (!Object2BooleanOpenCustomHashMap.this.strategy.equals(curr, k) || Object2BooleanOpenCustomHashMap.this.value[pos] != v);
            Object2BooleanOpenCustomHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Object2BooleanOpenCustomHashMap.this.size;
        }

        @Override
        public void clear() {
            Object2BooleanOpenCustomHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Object2BooleanMap.Entry<K>> consumer) {
            if (Object2BooleanOpenCustomHashMap.this.containsNullKey) {
                consumer.accept(new AbstractObject2BooleanMap.BasicEntry(Object2BooleanOpenCustomHashMap.this.key[Object2BooleanOpenCustomHashMap.this.n], Object2BooleanOpenCustomHashMap.this.value[Object2BooleanOpenCustomHashMap.this.n]));
            }
            int pos = Object2BooleanOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Object2BooleanOpenCustomHashMap.this.key[pos] == null) continue;
                consumer.accept(new AbstractObject2BooleanMap.BasicEntry(Object2BooleanOpenCustomHashMap.this.key[pos], Object2BooleanOpenCustomHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Object2BooleanMap.Entry<K>> consumer) {
            AbstractObject2BooleanMap.BasicEntry entry = new AbstractObject2BooleanMap.BasicEntry();
            if (Object2BooleanOpenCustomHashMap.this.containsNullKey) {
                entry.key = Object2BooleanOpenCustomHashMap.this.key[Object2BooleanOpenCustomHashMap.this.n];
                entry.value = Object2BooleanOpenCustomHashMap.this.value[Object2BooleanOpenCustomHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Object2BooleanOpenCustomHashMap.this.n;
            while (pos-- != 0) {
                if (Object2BooleanOpenCustomHashMap.this.key[pos] == null) continue;
                entry.key = Object2BooleanOpenCustomHashMap.this.key[pos];
                entry.value = Object2BooleanOpenCustomHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends Object2BooleanOpenCustomHashMap<K>
    implements ObjectIterator<Object2BooleanMap.Entry<K>> {
        private final Object2BooleanOpenCustomHashMap<K> entry;

        private FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        @Override
        public Object2BooleanOpenCustomHashMap<K> next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends Object2BooleanOpenCustomHashMap<K>
    implements ObjectIterator<Object2BooleanMap.Entry<K>> {
        private Object2BooleanOpenCustomHashMap<K> entry;

        private EntryIterator() {
            super();
        }

        @Override
        public Object2BooleanOpenCustomHashMap<K> next() {
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
            this.pos = Object2BooleanOpenCustomHashMap.this.n;
            this.last = -1;
            this.c = Object2BooleanOpenCustomHashMap.this.size;
            this.mustReturnNullKey = Object2BooleanOpenCustomHashMap.this.containsNullKey;
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
                this.last = Object2BooleanOpenCustomHashMap.this.n;
                return this.last;
            }
            K[] key = Object2BooleanOpenCustomHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                K k = this.wrapped.get(- this.pos - 1);
                int p = HashCommon.mix(Object2BooleanOpenCustomHashMap.this.strategy.hashCode(k)) & Object2BooleanOpenCustomHashMap.this.mask;
                while (!Object2BooleanOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                    p = p + 1 & Object2BooleanOpenCustomHashMap.this.mask;
                }
                return p;
            } while (key[this.pos] == null);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            K[] key = Object2BooleanOpenCustomHashMap.this.key;
            do {
                Object curr;
                int last = pos;
                pos = last + 1 & Object2BooleanOpenCustomHashMap.this.mask;
                do {
                    if ((curr = key[pos]) == null) {
                        key[last] = null;
                        return;
                    }
                    int slot = HashCommon.mix(Object2BooleanOpenCustomHashMap.this.strategy.hashCode(curr)) & Object2BooleanOpenCustomHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Object2BooleanOpenCustomHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new ObjectArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Object2BooleanOpenCustomHashMap.this.value[last] = Object2BooleanOpenCustomHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Object2BooleanOpenCustomHashMap.this.n) {
                Object2BooleanOpenCustomHashMap.this.containsNullKey = false;
                Object2BooleanOpenCustomHashMap.this.key[Object2BooleanOpenCustomHashMap.this.n] = null;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Object2BooleanOpenCustomHashMap.this.removeBoolean(this.wrapped.set(- this.pos - 1, null));
                this.last = -1;
                return;
            }
            --Object2BooleanOpenCustomHashMap.this.size;
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
    implements Object2BooleanMap.Entry<K>,
    Map.Entry<K, Boolean> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public K getKey() {
            return Object2BooleanOpenCustomHashMap.this.key[this.index];
        }

        @Override
        public boolean getBooleanValue() {
            return Object2BooleanOpenCustomHashMap.this.value[this.index];
        }

        @Override
        public boolean setValue(boolean v) {
            boolean oldValue = Object2BooleanOpenCustomHashMap.this.value[this.index];
            Object2BooleanOpenCustomHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Boolean getValue() {
            return Object2BooleanOpenCustomHashMap.this.value[this.index];
        }

        @Deprecated
        @Override
        public Boolean setValue(Boolean v) {
            return this.setValue((boolean)v);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Object2BooleanOpenCustomHashMap.this.strategy.equals(Object2BooleanOpenCustomHashMap.this.key[this.index], e.getKey()) && Object2BooleanOpenCustomHashMap.this.value[this.index] == (Boolean)e.getValue();
        }

        @Override
        public int hashCode() {
            return Object2BooleanOpenCustomHashMap.this.strategy.hashCode(Object2BooleanOpenCustomHashMap.this.key[this.index]) ^ (Object2BooleanOpenCustomHashMap.this.value[this.index] ? 1231 : 1237);
        }

        public String toString() {
            return Object2BooleanOpenCustomHashMap.this.key[this.index] + "=>" + Object2BooleanOpenCustomHashMap.this.value[this.index];
        }
    }

}

