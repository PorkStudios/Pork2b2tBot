/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReference2FloatMap;
import it.unimi.dsi.fastutil.objects.AbstractReferenceSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2FloatMap;
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

public class Reference2FloatOpenHashMap<K>
extends AbstractReference2FloatMap<K>
implements Serializable,
Cloneable,
Hash {
    private static final long serialVersionUID = 0L;
    private static final boolean ASSERTS = false;
    protected transient K[] key;
    protected transient float[] value;
    protected transient int mask;
    protected transient boolean containsNullKey;
    protected transient int n;
    protected transient int maxFill;
    protected final transient int minN;
    protected int size;
    protected final float f;
    protected transient Reference2FloatMap.FastEntrySet<K> entries;
    protected transient ReferenceSet<K> keys;
    protected transient FloatCollection values;

    public Reference2FloatOpenHashMap(int expected, float f) {
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
        this.value = new float[this.n + 1];
    }

    public Reference2FloatOpenHashMap(int expected) {
        this(expected, 0.75f);
    }

    public Reference2FloatOpenHashMap() {
        this(16, 0.75f);
    }

    public Reference2FloatOpenHashMap(Map<? extends K, ? extends Float> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Reference2FloatOpenHashMap(Map<? extends K, ? extends Float> m) {
        this(m, 0.75f);
    }

    public Reference2FloatOpenHashMap(Reference2FloatMap<K> m, float f) {
        this(m.size(), f);
        this.putAll(m);
    }

    public Reference2FloatOpenHashMap(Reference2FloatMap<K> m) {
        this(m, 0.75f);
    }

    public Reference2FloatOpenHashMap(K[] k, float[] v, float f) {
        this(k.length, f);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; ++i) {
            this.put(k[i], v[i]);
        }
    }

    public Reference2FloatOpenHashMap(K[] k, float[] v) {
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

    private float removeEntry(int pos) {
        float oldValue = this.value[pos];
        --this.size;
        this.shiftKeys(pos);
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    private float removeNullEntry() {
        this.containsNullKey = false;
        this.key[this.n] = null;
        float oldValue = this.value[this.n];
        --this.size;
        if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends Float> m) {
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

    private void insert(int pos, K k, float v) {
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
    public float put(K k, float v) {
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return this.defRetValue;
        }
        float oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    private float addToValue(int pos, float incr) {
        float oldValue = this.value[pos];
        this.value[pos] = oldValue + incr;
        return oldValue;
    }

    public float addTo(K k, float incr) {
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
    public float removeFloat(Object k) {
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
    public float getFloat(Object k) {
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
    public boolean containsValue(float v) {
        float[] value = this.value;
        K[] key = this.key;
        if (this.containsNullKey && Float.floatToIntBits(value[this.n]) == Float.floatToIntBits(v)) {
            return true;
        }
        int i = this.n;
        while (i-- != 0) {
            if (key[i] == null || Float.floatToIntBits(value[i]) != Float.floatToIntBits(v)) continue;
            return true;
        }
        return false;
    }

    @Override
    public float getOrDefault(Object k, float defaultValue) {
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
    public float putIfAbsent(K k, float v) {
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        this.insert(- pos - 1, k, v);
        return this.defRetValue;
    }

    @Override
    public boolean remove(Object k, float v) {
        if (k == null) {
            if (this.containsNullKey && Float.floatToIntBits(v) == Float.floatToIntBits(this.value[this.n])) {
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
        if (k == curr && Float.floatToIntBits(v) == Float.floatToIntBits(this.value[pos])) {
            this.removeEntry(pos);
            return true;
        }
        do {
            if ((curr = key[pos = pos + 1 & this.mask]) != null) continue;
            return false;
        } while (k != curr || Float.floatToIntBits(v) != Float.floatToIntBits(this.value[pos]));
        this.removeEntry(pos);
        return true;
    }

    @Override
    public boolean replace(K k, float oldValue, float v) {
        int pos = this.find(k);
        if (pos < 0 || Float.floatToIntBits(oldValue) != Float.floatToIntBits(this.value[pos])) {
            return false;
        }
        this.value[pos] = v;
        return true;
    }

    @Override
    public float replace(K k, float v) {
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        float oldValue = this.value[pos];
        this.value[pos] = v;
        return oldValue;
    }

    @Override
    public float computeFloatIfAbsent(K k, ToDoubleFunction<? super K> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        int pos = this.find(k);
        if (pos >= 0) {
            return this.value[pos];
        }
        float newValue = SafeMath.safeDoubleToFloat(mappingFunction.applyAsDouble(k));
        this.insert(- pos - 1, k, newValue);
        return newValue;
    }

    @Override
    public float computeFloatIfPresent(K k, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            return this.defRetValue;
        }
        Float newValue = remappingFunction.apply(k, Float.valueOf(this.value[pos]));
        if (newValue == null) {
            if (k == null) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return this.defRetValue;
        }
        this.value[pos] = newValue.floatValue();
        return this.value[pos];
    }

    @Override
    public float computeFloat(K k, BiFunction<? super K, ? super Float, ? extends Float> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        Float newValue = remappingFunction.apply(k, pos >= 0 ? Float.valueOf(this.value[pos]) : null);
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
        float newVal = newValue.floatValue();
        if (pos < 0) {
            this.insert(- pos - 1, k, newVal);
            return newVal;
        }
        this.value[pos] = newVal;
        return this.value[pos];
    }

    @Override
    public float mergeFloat(K k, float v, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        int pos = this.find(k);
        if (pos < 0) {
            this.insert(- pos - 1, k, v);
            return v;
        }
        Float newValue = remappingFunction.apply(Float.valueOf(this.value[pos]), Float.valueOf(v));
        if (newValue == null) {
            if (k == null) {
                this.removeNullEntry();
            } else {
                this.removeEntry(pos);
            }
            return this.defRetValue;
        }
        this.value[pos] = newValue.floatValue();
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

    public Reference2FloatMap.FastEntrySet<K> reference2FloatEntrySet() {
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
    public FloatCollection values() {
        if (this.values == null) {
            this.values = new AbstractFloatCollection(){

                @Override
                public FloatIterator iterator() {
                    return new ValueIterator();
                }

                @Override
                public int size() {
                    return Reference2FloatOpenHashMap.this.size;
                }

                @Override
                public boolean contains(float v) {
                    return Reference2FloatOpenHashMap.this.containsValue(v);
                }

                @Override
                public void clear() {
                    Reference2FloatOpenHashMap.this.clear();
                }

                @Override
                public void forEach(DoubleConsumer consumer) {
                    if (Reference2FloatOpenHashMap.this.containsNullKey) {
                        consumer.accept(Reference2FloatOpenHashMap.this.value[Reference2FloatOpenHashMap.this.n]);
                    }
                    int pos = Reference2FloatOpenHashMap.this.n;
                    while (pos-- != 0) {
                        if (Reference2FloatOpenHashMap.this.key[pos] == null) continue;
                        consumer.accept(Reference2FloatOpenHashMap.this.value[pos]);
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
        float[] value = this.value;
        int mask = newN - 1;
        Object[] newKey = new Object[newN + 1];
        float[] newValue = new float[newN + 1];
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

    public Reference2FloatOpenHashMap<K> clone() {
        Reference2FloatOpenHashMap c;
        try {
            c = (Reference2FloatOpenHashMap)Object.super.clone();
        }
        catch (CloneNotSupportedException cantHappen) {
            throw new InternalError();
        }
        c.keys = null;
        c.values = null;
        c.entries = null;
        c.containsNullKey = this.containsNullKey;
        c.key = (Object[])this.key.clone();
        c.value = (float[])this.value.clone();
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
            h += (t ^= HashCommon.float2int(this.value[i]));
            ++i;
        }
        if (this.containsNullKey) {
            h += HashCommon.float2int(this.value[this.n]);
        }
        return h;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        K[] key = this.key;
        float[] value = this.value;
        MapIterator i = new MapIterator();
        s.defaultWriteObject();
        int j = this.size;
        while (j-- != 0) {
            int e = i.nextEntry();
            s.writeObject(key[e]);
            s.writeFloat(value[e]);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.n = HashCommon.arraySize(this.size, this.f);
        this.maxFill = HashCommon.maxFill(this.n, this.f);
        this.mask = this.n - 1;
        this.key = new Object[this.n + 1];
        Object[] key = this.key;
        this.value = new float[this.n + 1];
        float[] value = this.value;
        int i = this.size;
        while (i-- != 0) {
            int pos;
            Object k = s.readObject();
            float v = s.readFloat();
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
    extends Reference2FloatOpenHashMap<K>
    implements FloatIterator {
        public ValueIterator() {
            super();
        }

        @Override
        public float nextFloat() {
            return Reference2FloatOpenHashMap.this.value[this.nextEntry()];
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
            if (Reference2FloatOpenHashMap.this.containsNullKey) {
                consumer.accept(Reference2FloatOpenHashMap.this.key[Reference2FloatOpenHashMap.this.n]);
            }
            int pos = Reference2FloatOpenHashMap.this.n;
            while (pos-- != 0) {
                Object k = Reference2FloatOpenHashMap.this.key[pos];
                if (k == null) continue;
                consumer.accept(k);
            }
        }

        @Override
        public int size() {
            return Reference2FloatOpenHashMap.this.size;
        }

        @Override
        public boolean contains(Object k) {
            return Reference2FloatOpenHashMap.this.containsKey(k);
        }

        @Override
        public boolean remove(Object k) {
            int oldSize = Reference2FloatOpenHashMap.this.size;
            Reference2FloatOpenHashMap.this.removeFloat(k);
            return Reference2FloatOpenHashMap.this.size != oldSize;
        }

        @Override
        public void clear() {
            Reference2FloatOpenHashMap.this.clear();
        }
    }

    private final class KeyIterator
    extends Reference2FloatOpenHashMap<K>
    implements ObjectIterator<K> {
        public KeyIterator() {
            super();
        }

        @Override
        public K next() {
            return Reference2FloatOpenHashMap.this.key[this.nextEntry()];
        }
    }

    private final class MapEntrySet
    extends AbstractObjectSet<Reference2FloatMap.Entry<K>>
    implements Reference2FloatMap.FastEntrySet<K> {
        private MapEntrySet() {
        }

        @Override
        public ObjectIterator<Reference2FloatMap.Entry<K>> iterator() {
            return new EntryIterator();
        }

        @Override
        public ObjectIterator<Reference2FloatMap.Entry<K>> fastIterator() {
            return new FastEntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            Object k = e.getKey();
            float v = ((Float)e.getValue()).floatValue();
            if (k == null) {
                return Reference2FloatOpenHashMap.this.containsNullKey && Float.floatToIntBits(Reference2FloatOpenHashMap.this.value[Reference2FloatOpenHashMap.this.n]) == Float.floatToIntBits(v);
            }
            K[] key = Reference2FloatOpenHashMap.this.key;
            int pos = HashCommon.mix(System.identityHashCode(k)) & Reference2FloatOpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (k == curr) {
                return Float.floatToIntBits(Reference2FloatOpenHashMap.this.value[pos]) == Float.floatToIntBits(v);
            }
            do {
                if ((curr = key[pos = pos + 1 & Reference2FloatOpenHashMap.this.mask]) != null) continue;
                return false;
            } while (k != curr);
            return Float.floatToIntBits(Reference2FloatOpenHashMap.this.value[pos]) == Float.floatToIntBits(v);
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            if (e.getValue() == null || !(e.getValue() instanceof Float)) {
                return false;
            }
            Object k = e.getKey();
            float v = ((Float)e.getValue()).floatValue();
            if (k == null) {
                if (Reference2FloatOpenHashMap.this.containsNullKey && Float.floatToIntBits(Reference2FloatOpenHashMap.this.value[Reference2FloatOpenHashMap.this.n]) == Float.floatToIntBits(v)) {
                    Reference2FloatOpenHashMap.this.removeNullEntry();
                    return true;
                }
                return false;
            }
            K[] key = Reference2FloatOpenHashMap.this.key;
            int pos = HashCommon.mix(System.identityHashCode(k)) & Reference2FloatOpenHashMap.this.mask;
            Object curr = key[pos];
            if (curr == null) {
                return false;
            }
            if (curr == k) {
                if (Float.floatToIntBits(Reference2FloatOpenHashMap.this.value[pos]) == Float.floatToIntBits(v)) {
                    Reference2FloatOpenHashMap.this.removeEntry(pos);
                    return true;
                }
                return false;
            }
            do {
                if ((curr = key[pos = pos + 1 & Reference2FloatOpenHashMap.this.mask]) != null) continue;
                return false;
            } while (curr != k || Float.floatToIntBits(Reference2FloatOpenHashMap.this.value[pos]) != Float.floatToIntBits(v));
            Reference2FloatOpenHashMap.this.removeEntry(pos);
            return true;
        }

        @Override
        public int size() {
            return Reference2FloatOpenHashMap.this.size;
        }

        @Override
        public void clear() {
            Reference2FloatOpenHashMap.this.clear();
        }

        @Override
        public void forEach(Consumer<? super Reference2FloatMap.Entry<K>> consumer) {
            if (Reference2FloatOpenHashMap.this.containsNullKey) {
                consumer.accept(new AbstractReference2FloatMap.BasicEntry(Reference2FloatOpenHashMap.this.key[Reference2FloatOpenHashMap.this.n], Reference2FloatOpenHashMap.this.value[Reference2FloatOpenHashMap.this.n]));
            }
            int pos = Reference2FloatOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Reference2FloatOpenHashMap.this.key[pos] == null) continue;
                consumer.accept(new AbstractReference2FloatMap.BasicEntry(Reference2FloatOpenHashMap.this.key[pos], Reference2FloatOpenHashMap.this.value[pos]));
            }
        }

        @Override
        public void fastForEach(Consumer<? super Reference2FloatMap.Entry<K>> consumer) {
            AbstractReference2FloatMap.BasicEntry entry = new AbstractReference2FloatMap.BasicEntry();
            if (Reference2FloatOpenHashMap.this.containsNullKey) {
                entry.key = Reference2FloatOpenHashMap.this.key[Reference2FloatOpenHashMap.this.n];
                entry.value = Reference2FloatOpenHashMap.this.value[Reference2FloatOpenHashMap.this.n];
                consumer.accept(entry);
            }
            int pos = Reference2FloatOpenHashMap.this.n;
            while (pos-- != 0) {
                if (Reference2FloatOpenHashMap.this.key[pos] == null) continue;
                entry.key = Reference2FloatOpenHashMap.this.key[pos];
                entry.value = Reference2FloatOpenHashMap.this.value[pos];
                consumer.accept(entry);
            }
        }
    }

    private class FastEntryIterator
    extends Reference2FloatOpenHashMap<K>
    implements ObjectIterator<Reference2FloatMap.Entry<K>> {
        private final Reference2FloatOpenHashMap<K> entry;

        private FastEntryIterator() {
            super();
            this.entry = new MapEntry();
        }

        @Override
        public Reference2FloatOpenHashMap<K> next() {
            this.entry.index = this.nextEntry();
            return this.entry;
        }
    }

    private class EntryIterator
    extends Reference2FloatOpenHashMap<K>
    implements ObjectIterator<Reference2FloatMap.Entry<K>> {
        private Reference2FloatOpenHashMap<K> entry;

        private EntryIterator() {
            super();
        }

        @Override
        public Reference2FloatOpenHashMap<K> next() {
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
            this.pos = Reference2FloatOpenHashMap.this.n;
            this.last = -1;
            this.c = Reference2FloatOpenHashMap.this.size;
            this.mustReturnNullKey = Reference2FloatOpenHashMap.this.containsNullKey;
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
                this.last = Reference2FloatOpenHashMap.this.n;
                return this.last;
            }
            K[] key = Reference2FloatOpenHashMap.this.key;
            do {
                if (--this.pos >= 0) continue;
                this.last = Integer.MIN_VALUE;
                K k = this.wrapped.get(- this.pos - 1);
                int p = HashCommon.mix(System.identityHashCode(k)) & Reference2FloatOpenHashMap.this.mask;
                while (k != key[p]) {
                    p = p + 1 & Reference2FloatOpenHashMap.this.mask;
                }
                return p;
            } while (key[this.pos] == null);
            this.last = this.pos;
            return this.last;
        }

        private void shiftKeys(int pos) {
            K[] key = Reference2FloatOpenHashMap.this.key;
            do {
                Object curr;
                int last = pos;
                pos = last + 1 & Reference2FloatOpenHashMap.this.mask;
                do {
                    if ((curr = key[pos]) == null) {
                        key[last] = null;
                        return;
                    }
                    int slot = HashCommon.mix(System.identityHashCode(curr)) & Reference2FloatOpenHashMap.this.mask;
                    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                    pos = pos + 1 & Reference2FloatOpenHashMap.this.mask;
                } while (true);
                if (pos < last) {
                    if (this.wrapped == null) {
                        this.wrapped = new ReferenceArrayList(2);
                    }
                    this.wrapped.add(key[pos]);
                }
                key[last] = curr;
                Reference2FloatOpenHashMap.this.value[last] = Reference2FloatOpenHashMap.this.value[pos];
            } while (true);
        }

        public void remove() {
            if (this.last == -1) {
                throw new IllegalStateException();
            }
            if (this.last == Reference2FloatOpenHashMap.this.n) {
                Reference2FloatOpenHashMap.this.containsNullKey = false;
                Reference2FloatOpenHashMap.this.key[Reference2FloatOpenHashMap.this.n] = null;
            } else if (this.pos >= 0) {
                this.shiftKeys(this.last);
            } else {
                Reference2FloatOpenHashMap.this.removeFloat(this.wrapped.set(- this.pos - 1, null));
                this.last = -1;
                return;
            }
            --Reference2FloatOpenHashMap.this.size;
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
    implements Reference2FloatMap.Entry<K>,
    Map.Entry<K, Float> {
        int index;

        MapEntry(int index) {
            this.index = index;
        }

        MapEntry() {
        }

        @Override
        public K getKey() {
            return Reference2FloatOpenHashMap.this.key[this.index];
        }

        @Override
        public float getFloatValue() {
            return Reference2FloatOpenHashMap.this.value[this.index];
        }

        @Override
        public float setValue(float v) {
            float oldValue = Reference2FloatOpenHashMap.this.value[this.index];
            Reference2FloatOpenHashMap.this.value[this.index] = v;
            return oldValue;
        }

        @Deprecated
        @Override
        public Float getValue() {
            return Float.valueOf(Reference2FloatOpenHashMap.this.value[this.index]);
        }

        @Deprecated
        @Override
        public Float setValue(Float v) {
            return Float.valueOf(this.setValue(v.floatValue()));
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            return Reference2FloatOpenHashMap.this.key[this.index] == e.getKey() && Float.floatToIntBits(Reference2FloatOpenHashMap.this.value[this.index]) == Float.floatToIntBits(((Float)e.getValue()).floatValue());
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(Reference2FloatOpenHashMap.this.key[this.index]) ^ HashCommon.float2int(Reference2FloatOpenHashMap.this.value[this.index]);
        }

        public String toString() {
            return Reference2FloatOpenHashMap.this.key[this.index] + "=>" + Reference2FloatOpenHashMap.this.value[this.index];
        }
    }

}

