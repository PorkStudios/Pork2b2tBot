/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.AbstractShort2IntMap;
import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.Short2IntMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
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
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public class Short2IntOpenHashMap
extends AbstractShort2IntMap
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient short[] key;
    protected transient int[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Short2IntMap.FastEntrySet entries;
    protected transient ShortSet keys;
    protected transient IntCollection values;

    public Short2IntOpenHashMap(int expected, float f) {
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
        this.key = new short[this.n + 1];
        this.value = new int[this.n + 1];
    }

    public Short2IntOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Short2IntOpenHashMap() {
        this(16, 0.75f);
    }

    public Short2IntOpenHashMap(Map<? extends Short, ? extends Integer> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Short2IntOpenHashMap(Map<? extends Short, ? extends Integer> m) {
        this(m, 0.75f);
    }

    public Short2IntOpenHashMap(Short2IntMap m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Short2IntOpenHashMap(Short2IntMap m) {
        this(m, 0.75f);
    }

    public Short2IntOpenHashMap(short[] k, int[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Short2IntOpenHashMap(short[] k, int[] v) {
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

    private int removeEntry(int pos) {
        int oldValue = this.value[pos];
        --this.size;
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private int removeNullEntry() {
        this.containsNullKey = false;
        int oldValue = this.value[this.n];
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends Short, ? extends Integer> m) {
        if ((double)this.f <= 0.5) {
            this.ensureCapacity(m.size());
        } else {
            this.tryCapacity(this.size() + m.size());
        }
        super.putAll(m);
    }

    private int find(short k) {
        if (k == 0) {
            return this.containsNullKey ? this.n : - this.n + 1;
        }
        short[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        short curr = key[pos];
        if (curr == 0) {
            return - pos + 1;
        }
        if (k == curr) {
            return pos;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return - pos + 1;
        } while (k != curr);
        return pos;
    }

    private void insert(int pos, short k, int v) {
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
    public int put(short k, int v) {
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return this.defRetValue;
        }
        int oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    private int addToValue(int pos, int incr) {
        int oldValue = this.value[pos];
        this.value[pos] = oldValue + incr;
        return oldValue;
    }

    public int addTo(short k, int incr) {
        int pos;
        if (k == 0) {
            if (this.containsNullKey) {
                return this.addToValue(this.n, incr);
            }
            pos = this.n;
            this.containsNullKey = true;
        } else {
            short[] key = this.key;
            pos = HashCommon.mix(k) & this.mask;
            short curr = key[pos];
            if (curr != 0) {
                if (curr == k) {
                    return this.addToValue(pos, incr);
                }
                while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
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
        short[] key = this.key;
        do {
            short curr;
            int last = pos;
            pos = last + 1 & this.mask;
            do {
                if ((curr = key[pos]) == 0) {
                    key[last] = 0;
                    return;
                }
                int slot = HashCommon.mix(curr) & this.mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = pos + 1 & this.mask;
            } while (true);
            key[last] = curr;
            this.value[last] = this.value[pos];
        } while (true);
    }

    @Override
    public int remove(short k) {
        if (k == 0) {
            if (this.containsNullKey) {
                return this.removeNullEntry();
            }
            return this.defRetValue;
        }
        short[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        short curr = key[pos];
        if (curr == 0) {
            return this.defRetValue;
        }
        if (k == curr) {
            return this.removeEntry(pos);
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return this.defRetValue;
        } while (k != curr);
        return this.removeEntry(pos);
    }

    @Override
    public int get(short k) {
        if (k == 0) {
            return this.containsNullKey ? this.value[this.n] : this.defRetValue;
        }
        short[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        short curr = key[pos];
        if (curr == 0) {
            return this.defRetValue;
        }
        if (k == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return this.defRetValue;
        } while (k != curr);
        return this.value[pos];
    }

    @Override
    public boolean containsKey(short k) {
        if (k == 0) {
            return this.containsNullKey;
        }
        short[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        short curr = key[pos];
        if (curr == 0) {
            return false;
        }
        if (k == curr) {
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (k != curr);
        return true;
    }

    @Override
    public boolean containsValue(int v) {
        int[] value = this.value;
        short[] key = this.key;
        if (this.containsNullKey && value[this.n] == v) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (key[i] == 0 || value[i] != v) continue;
            return true;
        }
        return false;
    }

    @Override
    public int getOrDefault(short k, int defaultValue) {
        if (k == 0) {
            return this.containsNullKey ? this.value[this.n] : defaultValue;
        }
        short[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        short curr = key[pos];
        if (curr == 0) {
            return defaultValue;
        }
        if (k == curr) {
            return this.value[pos];
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return defaultValue;
        } while (k != curr);
        return this.value[pos];
    }

    @Override
    public int putIfAbsent(short k, int v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(short k, int v) {
        if (k == 0) {
            if (this.containsNullKey && v == this.value[this.n]) {
                this.removeNullEntry();
                return true;
            }
            return false;
        }
        short[] key = this.key;
        int pos = HashCommon.mix(k) & this.mask;
        short curr = key[pos];
        if (curr == 0) {
            return false;
        }
        if (k == curr && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != 0) continue;
            return false;
        } while (k != curr || v != this.value[pos]);
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(short k, int oldValue, int v) {
        int pos = this.find(k);
        if (pos < 0 || oldValue != this.value[pos]) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public int replace(short k, int v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        int oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public int computeIfAbsent(short k, IntUnaryOperator mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        int newValue = mappingFunction.applyAsInt(k);
        this.insert(- pos - 1, k, newValue);
        return newValue;
    }

    @Override
    public int computeIfAbsentNullable(short k, IntFunction<? extends Integer> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        Integer newValue = mappingFunction.apply(k);
        if (newValue == null) {
            return this.defRetValue;
        }
        int v = newValue;
        this.insert(- pos - 1, k, v);
        return v;
    }

    @Override
    public int computeIfPresent(short k, BiFunction<? super Short, ? super Integer, ? extends Integer> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Integer newValue = remappingFunction.apply((Short)k, (Integer)this.value[pos]);
        if (newValue == null) {
            if (k == 0) {
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
    public int compute(short k, BiFunction<? super Short, ? super Integer, ? extends Integer> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Integer newValue = remappingFunction.apply((Short)k, pos >= 0 ? Integer.valueOf(this.value[pos]) : null);
        if (newValue == null) {
            if (pos >= 0) {
                if (k == 0) {
                    this.removeNullEntry();
                } else {
                    this.removeEntry(pos);
                }
            }
            return this.defRetValue;
        }
        int newVal = newValue;
        if (pos < 0) {
            this.insert(- pos - 1, k, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public int merge(short k, int v, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Integer newValue = remappingFunction.apply((Integer)this.value[pos], (Integer)v);
        if (newValue == null) {
            if (k == 0) {
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
        Arrays.fill(this.key, (short)0);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public Short2IntMap.FastEntrySet short2IntEntrySet() {
        if (this.entries == null) {
            this.entries = new MapEntrySet();
        }
        return this.entries;
    }

    @Override
    public ShortSet keySet() {
        if (this.keys == null) {
            this.keys = new KeySet();
        }
        return this.keys;
    }

    @Override
    public IntCollection values() {
        if (this.values == null) {
            this.values = new AbstractIntCollection(){

                @Override
                public IntIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Short2IntOpenHashMap.this.size;
                }

                @Override
                public boolean contains(int v) {
                    return Short2IntOpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Short2IntOpenHashMap.this.clear();
                }

                @Override
                public void forEach(IntConsumer consumer) {
                    if (Short2IntOpenHashMap.this.containsNullKey) {
                        consumer.accept(Short2IntOpenHashMap.this.value[Short2IntOpenHashMap.this.n]);
                    }
                    int pos = Short2IntOpenHashMap.this.n;
                    while (pos-- != 0) {
                        if (Short2IntOpenHashMap.this.key[pos] == 0) continue;
                        consumer.accept(Short2IntOpenHashMap.this.value[pos]);
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
        short[] key = this.key;
        int[] value = this.value;
        int mask = newN - 1;
        short[] newKey = new short[newN + 1];
        int[] newValue = new int[newN + 1];
        int i = this.n;
        int j = this.realSize();
        while (j-- != 0) {
            while (key[--i] == 0) {
            }
            int pos = HashCommon.mix(key[i]) & mask;
            if (newKey[pos] != 0) {
                while (newKey[pos = pos + 1 & mask] != 0) {
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

    public Short2IntOpenHashMap clone() {
        Short2IntOpenHashMap c;
        try {
            c = (Short2IntOpenHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (short[])this.key.clone();
        c.value = (int[])this.value.clone();
        return c;
    }

    @Override
    public int hashCode() {
        int h = 0;
        int j = this.realSize();
        int i = 0;
        int t = 0;
        while (j-- != 0) {
            while (this.key[i] == 0) {
                ++i;
            }
            t = this.key[i];
            h += (t ^= this.value[i]);
            ++i;
        }
        if (this.containsNullKey) {
            h += this.value[this.n];
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        short[] key = this.key;
        int[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeShort(key[e]);
            s.writeInt(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new short[this.n + 1];
        short[] key = this.key;
        this.value = new int[this.n + 1];
        int[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            short k = s.readShort();
            int v = s.readInt();
            if (k == 0) {
                pos = this.n;
                this.containsNullKey = true;
            } else {
                pos = HashCommon.mix(k) & this.mask;
                while (key[pos] != 0) {
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
    implements IntIterator {
        public ValueIterator() {
            super();
        }

        @Override
        public int nextInt() {
            return Short2IntOpenHashMap.this.value[this.nextEntry()];
        }
    }

    private final class KeySet
    extends AbstractShortSet {
        private KeySet() {
        }

        @Override
        public ShortIterator iterator() {
            return new KeyIterator();
        }

        @Override
        public void forEach(IntConsumer consumer) {
            if (Short2IntOpenHashMap.this.containsNullKey) {
                consumer.accept(Short2IntOpenHashMap.this.key[Short2IntOpenHashMap.this.n]);
            }
            int pos = Short2IntOpenHashMap.this.n;
            while (pos-- != 0) {
                short k = Short2IntOpenHashMap.this.key[pos];
                if (k == 0) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Short2IntOpenHashMap.this.size;
        }

        @Override
        public boolean contains(short k) {
            return Short2IntOpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(short k) {
            int oldSize = Short2IntOpenHashMap.this.size;
            Short2IntOpenHashMap.this.remove(k);
            return Short2IntOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Short2IntOpenHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends MapIterator
    implements ShortIterator {
        public KeyIterator() {
            super();
        }

        @Override
        public short nextShort() {
            return Short2IntOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Short2IntMap.Entry>
    implements Short2IntMap.FastEntrySet {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Short2IntMap.Entry> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Short2IntMap.Entry> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Short)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Integer)) {
                return false;
            }
            short k = (Short)e.getKey();
            int v = (Integer)e.getValue();
            if (k == 0) {
                return Short2IntOpenHashMap.this.containsNullKey && Short2IntOpenHashMap.this.value[Short2IntOpenHashMap.this.n] == v;
            }
            short[] key = Short2IntOpenHashMap.this.key;
            int pos = HashCommon.mix(k) & Short2IntOpenHashMap.this.mask;
            short curr = key[pos];
            if (curr == 0) {
                return false;
            }
            if (k == curr) {
                return Short2IntOpenHashMap.this.value[pos] == v;
            }
            do {
                if ((curr = key[pos = pos + 1 & Short2IntOpenHashMap.this.mask]) != 0) continue;
                return false;
            } while (k != curr);
            return Short2IntOpenHashMap.this.value[pos] == v;
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getKey() == null || !(e.getKey() instanceof Short)) {
                return false;
            }
            if (e.getValue() == null || !(e.getValue() instanceof Integer)) {
                return false;
            }
            short k = (Short)e.getKey();
            int v = (Integer)e.getValue();
            if (k == 0) {
                if (Short2IntOpenHashMap.this.containsNullKey && Short2IntOpenHashMap.this.value[Short2IntOpenHashMap.this.n] == v) {
                    Short2IntOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            short[] key = Short2IntOpenHashMap.this.key;
            int pos = HashCommon.mix(k) & Short2IntOpenHashMap.this.mask;
            short curr = key[pos];
            if (curr == 0) {
                return false;
            }
            if (curr == k) {
                if (Short2IntOpenHashMap.this.value[pos] == v) {
                    Short2IntOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Short2IntOpenHashMap.this.mask]) != 0) continue;
                return false;
            } while (curr != k || Short2IntOpenHashMap.this.value[pos] != v);
            Short2IntOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Short2IntOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Short2IntOpenHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Short2IntMap.Entry> consumer) {
            if (Short2IntOpenHashMap.this.containsNullKey) {
                consumer.accept(new AbstractShort2IntMap.BasicEntry(Short2IntOpenHashMap.this.key[Short2IntOpenHashMap.this.n], Short2IntOpenHashMap.this.value[Short2IntOpenHashMap.this.n]));
            }
            int pos = Short2IntOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Short2IntOpenHashMap.this.key[pos] == 0) continue;
                consumer.accept(new AbstractShort2IntMap.BasicEntry(Short2IntOpenHashMap.this.key[pos], Short2IntOpenHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Short2IntMap.Entry> consumer) {
            AbstractShort2IntMap.BasicEntry entry = new AbstractShort2IntMap.BasicEntry();
            if (Short2IntOpenHashMap.this.containsNullKey) {
                entry.key = Short2IntOpenHashMap.this.key[Short2IntOpenHashMap.this.n];
                entry.value = Short2IntOpenHashMap.this.value[Short2IntOpenHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Short2IntOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Short2IntOpenHashMap.this.key[pos] == 0) continue;
                entry.key = Short2IntOpenHashMap.this.key[pos];
                entry.value = Short2IntOpenHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends MapIterator
    implements ObjectIterator<Short2IntMap.Entry> {
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
    implements ObjectIterator<Short2IntMap.Entry> {
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
        ShortArrayList wrapped;

        private MapIterator() {
            this.pos = Short2IntOpenHashMap.this.n;
            this.last = -1;
            this.c = Short2IntOpenHashMap.this.size;
            this.mustReturnNullKey = Short2IntOpenHashMap.this.containsNullKey;
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
                this.last = Short2IntOpenHashMap.this.n;
                return this.last;
            }
            short[] key = Short2IntOpenHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                short k = this.wrapped.getShort(- this.pos - 1);
                int p = HashCommon.mix(k) & Short2IntOpenHashMap.this.mask;
                while (k != key[p]) {
                    p = p + 1 & Short2IntOpenHashMap.this.mask;
                }
                return p;
            } while (key[this.pos] == 0);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            short[] key = Short2IntOpenHashMap.this.key;
            do {
                short curr;
                int last = pos;
                pos = last + 1 & Short2IntOpenHashMap.this.mask;
                do {
                    if ((curr = key[pos]) == 0) {
                        key[last] = 0;
                        return;
                    }
                    int slot = HashCommon.mix(curr) & Short2IntOpenHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Short2IntOpenHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new ShortArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Short2IntOpenHashMap.this.value[last] = Short2IntOpenHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Short2IntOpenHashMap.this.n) {
                Short2IntOpenHashMap.this.containsNullKey = false;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Short2IntOpenHashMap.this.remove(this.wrapped.getShort(- this.pos - 1));
                this.last = -1;
                return;
            }
            --Short2IntOpenHashMap.this.size;
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
    implements Short2IntMap.Entry,
    Map.Entry<Short, Integer> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public short getShortKey() {
            return Short2IntOpenHashMap.this.key[this.index];
        }

        @Override
        public int getIntValue() {
            return Short2IntOpenHashMap.this.value[this.index];
        }

        @Override
        public int setValue(int v) {
            int oldValue = Short2IntOpenHashMap.this.value[this.index];
            Short2IntOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Short getKey() {
            return Short2IntOpenHashMap.this.key[this.index];
        }

        @Deprecated
        @Override
        public Integer getValue() {
            return Short2IntOpenHashMap.this.value[this.index];
        }

        @Deprecated
        @Override
        public Integer setValue(Integer v) {
            return this.setValue((int)v);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Short2IntOpenHashMap.this.key[this.index] == (Short)e.getKey() && Short2IntOpenHashMap.this.value[this.index] == (Integer)e.getValue();
        }

        @Override
        public int hashCode() {
            return Short2IntOpenHashMap.this.key[this.index] ^ Short2IntOpenHashMap.this.value[this.index];
        }

        public String toString() {
            return "" + Short2IntOpenHashMap.this.key[this.index] + "=>" + Short2IntOpenHashMap.this.value[this.index];
        }
    }

}

