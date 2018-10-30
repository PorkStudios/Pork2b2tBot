/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReference2DoubleMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
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
import java.util.function.DoubleConsumer;
import java.util.function.ToDoubleFunction;

public class Reference2DoubleOpenHashMap<K>
extends AbstractReference2DoubleMap<K>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient K[] key;
    protected transient double[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Reference2DoubleMap.FastEntrySet<K> entries;
    protected transient ReferenceSet<K> keys;
    protected transient DoubleCollection values;

    public Reference2DoubleOpenHashMap(int expected, float f) {
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
        this.value = new double[this.n + 1];
    }

    public Reference2DoubleOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Reference2DoubleOpenHashMap() {
        this(16, 0.75f);
    }

    public Reference2DoubleOpenHashMap(Map<? extends K, ? extends Double> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Reference2DoubleOpenHashMap(Map<? extends K, ? extends Double> m) {
        this(m, 0.75f);
    }

    public Reference2DoubleOpenHashMap(Reference2DoubleMap<K> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Reference2DoubleOpenHashMap(Reference2DoubleMap<K> m) {
        this(m, 0.75f);
    }

    public Reference2DoubleOpenHashMap(K[] k, double[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Reference2DoubleOpenHashMap(K[] k, double[] v) {
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
        this.key[this.n] = null;
        double oldValue = this.value[this.n];
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends Double> m) {
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

    private void insert(int pos, K k, double v) {
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
    public double put(K k, double v) {
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

    public double addTo(K k, double incr) {
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
    public double removeDouble(Object k) {
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
    public double getDouble(Object k) {
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
    public boolean containsValue(double v) {
        double[] value = this.value;
        K[] key = this.key;
        if (this.containsNullKey && Double.doubleToLongBits(value[this.n]) == Double.doubleToLongBits(v)) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (key[i] == null || Double.doubleToLongBits(value[i]) != Double.doubleToLongBits(v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public double getOrDefault(Object k, double defaultValue) {
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
    public double putIfAbsent(K k, double v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(Object k, double v) {
        if (k == null) {
            if (this.containsNullKey && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[this.n])) {
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
        if (k == curr && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[pos])) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return false;
        } while (k != curr || Double.doubleToLongBits(v) != Double.doubleToLongBits(this.value[pos]));
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(K k, double oldValue, double v) {
        int pos = this.find(k);
        if (pos < 0 || Double.doubleToLongBits(oldValue) != Double.doubleToLongBits(this.value[pos])) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public double replace(K k, double v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        double oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public double computeDoubleIfAbsent(K k, ToDoubleFunction<? super K> mappingFunction) {
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
    public double computeDoubleIfPresent(K k, BiFunction<? super K, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Double newValue = remappingFunction.apply(k, (Double)this.value[pos]);
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
    public double computeDouble(K k, BiFunction<? super K, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Double newValue = remappingFunction.apply(k, pos >= 0 ? Double.valueOf(this.value[pos]) : null);
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
        double newVal = newValue;
        if (pos < 0) {
            this.insert(- pos - 1, k, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public double mergeDouble(K k, double v, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Double newValue = remappingFunction.apply((Double)this.value[pos], (Double)v);
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

    public Reference2DoubleMap.FastEntrySet<K> reference2DoubleEntrySet() {
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
    public DoubleCollection values() {
        if (this.values == null) {
            this.values = new AbstractDoubleCollection(){

                @Override
                public DoubleIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Reference2DoubleOpenHashMap.this.size;
                }

                @Override
                public boolean contains(double v) {
                    return Reference2DoubleOpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Reference2DoubleOpenHashMap.this.clear();
                }

                @Override
                public void forEach(DoubleConsumer consumer) {
                    if (Reference2DoubleOpenHashMap.this.containsNullKey) {
                        consumer.accept(Reference2DoubleOpenHashMap.this.value[Reference2DoubleOpenHashMap.this.n]);
                    }
                    int pos = Reference2DoubleOpenHashMap.this.n;
                    while (pos-- != 0) {
                        if (Reference2DoubleOpenHashMap.this.key[pos] == null) continue;
                        consumer.accept(Reference2DoubleOpenHashMap.this.value[pos]);
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
        double[] value = this.value;
        int mask = newN - 1;
        Object[] newKey = new Object[newN + 1];
        double[] newValue = new double[newN + 1];
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

    public Reference2DoubleOpenHashMap<K> clone() {
        Reference2DoubleOpenHashMap c;
        try {
            c = (Reference2DoubleOpenHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (Object[])this.key.clone();
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
            while (this.key[i] == null) {
                ++i;
            }
            if (this != this.key[i]) {
                t = System.identityHashCode(this.key[i]);
            }
            h += (t ^= HashCommon.double2int(this.value[i]));
            ++i;
        }
        if (this.containsNullKey) {
            h += HashCommon.double2int(this.value[this.n]);
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        K[] key = this.key;
        double[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeObject(key[e]);
            s.writeDouble(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new Object[this.n + 1];
        Object[] key = this.key;
        this.value = new double[this.n + 1];
        double[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            Object k = s.readObject();
            double v = s.readDouble();
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
    extends Reference2DoubleOpenHashMap<K>
    implements DoubleIterator {
        public ValueIterator() {
            super();
        }

        @Override
        public double nextDouble() {
            return Reference2DoubleOpenHashMap.this.value[this.nextEntry()];
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
            if (Reference2DoubleOpenHashMap.this.containsNullKey) {
                consumer.accept(Reference2DoubleOpenHashMap.this.key[Reference2DoubleOpenHashMap.this.n]);
            }
            int pos = Reference2DoubleOpenHashMap.this.n;
            while (pos-- != 0) {
                Object k = Reference2DoubleOpenHashMap.this.key[pos];
                if (k == null) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Reference2DoubleOpenHashMap.this.size;
        }

        @Override
        public boolean contains(Object k) {
            return Reference2DoubleOpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(Object k) {
            int oldSize = Reference2DoubleOpenHashMap.this.size;
            Reference2DoubleOpenHashMap.this.removeDouble(k);
            return Reference2DoubleOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Reference2DoubleOpenHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends Reference2DoubleOpenHashMap<K>
    implements ObjectIterator<K> {
        public KeyIterator() {
            super();
        }

        @Override
        public K next() {
            return Reference2DoubleOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Reference2DoubleMap.Entry<K>>
    implements Reference2DoubleMap.FastEntrySet<K> {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Reference2DoubleMap.Entry<K>> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Reference2DoubleMap.Entry<K>> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            Object k = e.getKey();
            double v = (Double)e.getValue();
            if (k == null) {
                return Reference2DoubleOpenHashMap.this.containsNullKey && Double.doubleToLongBits(Reference2DoubleOpenHashMap.this.value[Reference2DoubleOpenHashMap.this.n]) == Double.doubleToLongBits(v);
            }
            K[] key = Reference2DoubleOpenHashMap.this.key;
            int pos = HashCommon.mix(System.identityHashCode(k)) & Reference2DoubleOpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (k == curr) {
                return Double.doubleToLongBits(Reference2DoubleOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v);
            }
            do {
                if ((curr = key[pos = pos + 1 & Reference2DoubleOpenHashMap.this.mask]) != null) continue;
                return false;
            } while (k != curr);
            return Double.doubleToLongBits(Reference2DoubleOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v);
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Double)) {
                return false;
            }
            Object k = e.getKey();
            double v = (Double)e.getValue();
            if (k == null) {
                if (Reference2DoubleOpenHashMap.this.containsNullKey && Double.doubleToLongBits(Reference2DoubleOpenHashMap.this.value[Reference2DoubleOpenHashMap.this.n]) == Double.doubleToLongBits(v)) {
                    Reference2DoubleOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            K[] key = Reference2DoubleOpenHashMap.this.key;
            int pos = HashCommon.mix(System.identityHashCode(k)) & Reference2DoubleOpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (curr == k) {
                if (Double.doubleToLongBits(Reference2DoubleOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v)) {
                    Reference2DoubleOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Reference2DoubleOpenHashMap.this.mask]) != null) continue;
                return false;
            } while (curr != k || Double.doubleToLongBits(Reference2DoubleOpenHashMap.this.value[pos]) != Double.doubleToLongBits(v));
            Reference2DoubleOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Reference2DoubleOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Reference2DoubleOpenHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Reference2DoubleMap.Entry<K>> consumer) {
            if (Reference2DoubleOpenHashMap.this.containsNullKey) {
                consumer.accept(new AbstractReference2DoubleMap.BasicEntry(Reference2DoubleOpenHashMap.this.key[Reference2DoubleOpenHashMap.this.n], Reference2DoubleOpenHashMap.this.value[Reference2DoubleOpenHashMap.this.n]));
            }
            int pos = Reference2DoubleOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Reference2DoubleOpenHashMap.this.key[pos] == null) continue;
                consumer.accept(new AbstractReference2DoubleMap.BasicEntry(Reference2DoubleOpenHashMap.this.key[pos], Reference2DoubleOpenHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Reference2DoubleMap.Entry<K>> consumer) {
            AbstractReference2DoubleMap.BasicEntry entry = new AbstractReference2DoubleMap.BasicEntry();
            if (Reference2DoubleOpenHashMap.this.containsNullKey) {
                entry.key = Reference2DoubleOpenHashMap.this.key[Reference2DoubleOpenHashMap.this.n];
                entry.value = Reference2DoubleOpenHashMap.this.value[Reference2DoubleOpenHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Reference2DoubleOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Reference2DoubleOpenHashMap.this.key[pos] == null) continue;
                entry.key = Reference2DoubleOpenHashMap.this.key[pos];
                entry.value = Reference2DoubleOpenHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends Reference2DoubleOpenHashMap<K>
    implements ObjectIterator<Reference2DoubleMap.Entry<K>> {
        private final Reference2DoubleOpenHashMap<K> entry;

        private FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        @Override
        public Reference2DoubleOpenHashMap<K> next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends Reference2DoubleOpenHashMap<K>
    implements ObjectIterator<Reference2DoubleMap.Entry<K>> {
        private Reference2DoubleOpenHashMap<K> entry;

        private EntryIterator() {
            super();
        }

        @Override
        public Reference2DoubleOpenHashMap<K> next() {
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
            this.pos = Reference2DoubleOpenHashMap.this.n;
            this.last = -1;
            this.c = Reference2DoubleOpenHashMap.this.size;
            this.mustReturnNullKey = Reference2DoubleOpenHashMap.this.containsNullKey;
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
                this.last = Reference2DoubleOpenHashMap.this.n;
                return this.last;
            }
            K[] key = Reference2DoubleOpenHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                K k = this.wrapped.get(- this.pos - 1);
                int p = HashCommon.mix(System.identityHashCode(k)) & Reference2DoubleOpenHashMap.this.mask;
                while (k != key[p]) {
                    p = p + 1 & Reference2DoubleOpenHashMap.this.mask;
                }
                return p;
            } while (key[this.pos] == null);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            K[] key = Reference2DoubleOpenHashMap.this.key;
            do {
                Object curr;
                int last = pos;
                pos = last + 1 & Reference2DoubleOpenHashMap.this.mask;
                do {
                    if ((curr = key[pos]) == null) {
                        key[last] = null;
                        return;
                    }
                    int slot = HashCommon.mix(System.identityHashCode(curr)) & Reference2DoubleOpenHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Reference2DoubleOpenHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new ReferenceArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Reference2DoubleOpenHashMap.this.value[last] = Reference2DoubleOpenHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Reference2DoubleOpenHashMap.this.n) {
                Reference2DoubleOpenHashMap.this.containsNullKey = false;
                Reference2DoubleOpenHashMap.this.key[Reference2DoubleOpenHashMap.this.n] = null;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Reference2DoubleOpenHashMap.this.removeDouble(this.wrapped.set(- this.pos - 1, null));
                this.last = -1;
                return;
            }
            --Reference2DoubleOpenHashMap.this.size;
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
    implements Reference2DoubleMap.Entry<K>,
    Map.Entry<K, Double> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public K getKey() {
            return Reference2DoubleOpenHashMap.this.key[this.index];
        }

        @Override
        public double getDoubleValue() {
            return Reference2DoubleOpenHashMap.this.value[this.index];
        }

        @Override
        public double setValue(double v) {
            double oldValue = Reference2DoubleOpenHashMap.this.value[this.index];
            Reference2DoubleOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Double getValue() {
            return Reference2DoubleOpenHashMap.this.value[this.index];
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
            return Reference2DoubleOpenHashMap.this.key[this.index] == e.getKey() && Double.doubleToLongBits(Reference2DoubleOpenHashMap.this.value[this.index]) == Double.doubleToLongBits((Double)e.getValue());
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(Reference2DoubleOpenHashMap.this.key[this.index]) ^ HashCommon.double2int(Reference2DoubleOpenHashMap.this.value[this.index]);
        }

        public String toString() {
            return Reference2DoubleOpenHashMap.this.key[this.index] + "=>" + Reference2DoubleOpenHashMap.this.value[this.index];
        }
    }

}

